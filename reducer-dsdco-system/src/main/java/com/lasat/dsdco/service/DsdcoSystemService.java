package com.lasat.dsdco.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lasat.dsdco.bean.DsdcoRegion;
import com.lasat.dsdco.bean.DsdcoTarget;
import com.lasat.dsdco.bean.OptimizationResult;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class DsdcoSystemService {
    // the message producer of system optimization
    private final ResourceBundle mqConfig = ResourceBundle.getBundle("mqConfig");
    private final DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(mqConfig.getString("consumerGroup"));
    // the closest point to the targetPoint of each disciplinary
    private final ConcurrentHashMap<String, DsdcoTarget> closestPointMap = new ConcurrentHashMap<>(2);
    // reentrant lock is used to ensure only one thread can operate the regions
    private final ReentrantLock lock = new ReentrantLock();
    // iterator count and current task id are deemed as idempotent token
    private Integer iteratorCount = 0;
    private Long currentTaskId = null;
    private Double currentScore = .0;
    // priority queue is aimed at select the best region quickly
    private final PriorityQueue<DsdcoRegion> priorityQueue = new PriorityQueue<>((region1, region2) -> {
        if (region1.getMinTargetFunValue() > region2.getMinTargetFunValue()) return 1;
        else if (region1.getMinTargetFunValue().equals(region2.getMinTargetFunValue())) return 0;
        else return -1;
    });
    // current system target point
    private DsdcoTarget currentTarget;
    // current region
    private DsdcoRegion currentRegion;
    // SystemName
    private final String SYSTEM_NAME = "reducer-system";
    // the number of variables
    private final int VARIABLES_COUNT = 7;
    private final int DISCIPLINARY_COUNT = 2;

    //origin upper and lower limit of the region
    private final Double[] originLowerLim = new Double[]{2.6, 0.7, 17.0, 7.3, 7.3, 2.9, 5.0};
    private final Double[] originUpperLim = new Double[]{3.6, 0.8, 28.0, 8.3, 8.3, 3.9, 5.5};

    @Autowired
    private RegionCalculateService regionCalculateService;

    /**
     * initialize the consumer
     */
    @PostConstruct
    public void initializeConsumer() {
        consumer.setNamesrvAddr(mqConfig.getString("nameServerAddress"));
        consumer.setInstanceName(mqConfig.getString("consumerInstanceName"));
        try {
            consumer.subscribe(mqConfig.getString("disciplinary2SystemTopic"), mqConfig.getString("tag"));
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        consumer.registerMessageListener((MessageListenerConcurrently) (messages, consumeConcurrentlyContext) -> {
            for (MessageExt message : messages) {
                // the closest point of a disciplinary calculator
                String pointJson = new String(message.getBody());
                ObjectMapper jsonMapper = new ObjectMapper();
                DsdcoTarget dsdcoTarget = null;
                try {
                    dsdcoTarget = jsonMapper.readValue(pointJson, DsdcoTarget.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (dsdcoTarget != null
                        && dsdcoTarget.getTaskId().equals(currentTaskId)
                        && dsdcoTarget.getIteratorCount().equals(iteratorCount)
                        && !closestPointMap.containsKey(dsdcoTarget.getDisciplinaryName())) {
                    closestPointMap.put(dsdcoTarget.getDisciplinaryName(), dsdcoTarget);
                    System.out.println("Point map has been updated: " + closestPointMap);
                    checkDisciplinaryResult();
                } else {
                    System.out.println("Duplicate message received: " + dsdcoTarget);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }


    /**
     * start the task
     */
    @Async
    public void startTask() {
        // initialize the region
        DsdcoRegion originRegion = new DsdcoRegion();
        originRegion.setLowerLim(Arrays.copyOf(originLowerLim, originLowerLim.length));
        originRegion.setUpperLim(Arrays.copyOf(originUpperLim, originUpperLim.length));

        // get the regions best point
        OptimizationResult originResult = regionCalculateService.getResultInRegion(originRegion);
        originRegion.setMinTargetFunValue(originResult.getScore());
        Double[] variablesOfRegion = new Double[VARIABLES_COUNT];
        for (int i = 0; i < VARIABLES_COUNT; i++) {
            variablesOfRegion[i] = originResult.getVariables()[i];
        }
        originRegion.setBestVariables(variablesOfRegion);

        // add the origin region to priority queue
        priorityQueue.add(originRegion);
        currentRegion = originRegion;

        // send the region to message queue
        iteratorCount++;
        DsdcoTarget dsdcoTarget = new DsdcoTarget(currentTaskId, SYSTEM_NAME, iteratorCount, originRegion.getBestVariables());
        currentTarget = dsdcoTarget;
        currentScore = originResult.getScore();
        regionCalculateService.sendTarget2Disciplinary(dsdcoTarget);
        printMessage();
    }

    /**
     * set taskId
     *
     * @param taskId of current task
     */
    public void setTaskId(Long taskId) {
        this.currentTaskId = taskId;
    }

    /**
     * check the disciplinary result, if the target of system meet all constraints, update the calculate result to database
     * else, split and select best region to calculate
     */
    private void checkDisciplinaryResult() {
        try {
            // only one thread can operate the region
            lock.lock();
            if (closestPointMap.size() < DISCIPLINARY_COUNT) return;
            int constraintMeetCount = 0;
            double maxDistance = 0;
            for (Map.Entry<String, DsdcoTarget> disPointEntry : closestPointMap.entrySet()) {
                DsdcoTarget disPoint = disPointEntry.getValue();
                if (!disPoint.equals(currentTarget)) {
                    maxDistance = Math.max(maxDistance, getDistance(disPoint, currentTarget));
                } else {
                    constraintMeetCount++;
                }
            }

            if (constraintMeetCount == DISCIPLINARY_COUNT) {
                System.out.println("the result is " + currentTarget.toString() + ", score: " + currentScore);
                closeTask();
            } else if (currentTarget.getIteratorCount() >= 500) {
                System.out.println("[ERROR]: The task has been calculated 500 times, but don't get a result");
            } else {
                currentRegion = splitAndSelectRegion(maxDistance);
                if (currentRegion == null) {
                    System.out.println("[ERROR]: No suitable result!!");
                    return;
                }
                // prepare for the next calculation
                iteratorCount++;
                currentTarget = new DsdcoTarget(currentTaskId, SYSTEM_NAME, iteratorCount, currentRegion.getBestVariables());
                currentScore = - currentRegion.getMinTargetFunValue();
                closestPointMap.clear();
                regionCalculateService.sendTarget2Disciplinary(currentTarget);
                printMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // ensure the lock will be free even exceptions occurred
            lock.unlock();
        }
    }

    /**
     * split and select the best region
     *
     * @return the best region
     */
    private DsdcoRegion splitAndSelectRegion(double maxDistance) {
        double excludeDistance = maxDistance * Math.sqrt(VARIABLES_COUNT) / VARIABLES_COUNT;
        Double[] currentVariables = currentTarget.getVariables();
        currentRegion = priorityQueue.poll();
        Double[] currentUpperLim = currentRegion != null ? currentRegion.getUpperLim() : new Double[0];
        Double[] currentLowerLim = currentRegion != null ? currentRegion.getLowerLim() : new Double[0];
        for (int i = 0; i < VARIABLES_COUNT; i++) {
            // in order to prevent divergence of the regions, set the bigger one for upper limit while the smaller one for lower limit
            double newLowerLim = Math.max(currentVariables[i] - excludeDistance, currentLowerLim[i]);

            double newUpperLim = Math.min(currentVariables[i] + excludeDistance, currentUpperLim[i]);

            // get the upper region and add it to the priority queue if it is suitable
            DsdcoRegion upperRegion = new DsdcoRegion();
            Double[] lowerLimOfUpperRegion = Arrays.copyOf(currentLowerLim, VARIABLES_COUNT);
            lowerLimOfUpperRegion[i] = newUpperLim;
            Double[] upperLimOfUpperRegion = Arrays.copyOf(currentUpperLim, VARIABLES_COUNT);
            addRegionToQueue(upperRegion, upperLimOfUpperRegion, lowerLimOfUpperRegion);

            // get the lower region and add it to the priority queue if it is suitable
            DsdcoRegion lowerRegion = new DsdcoRegion();
            Double[] lowerLimOfLowerRegion = Arrays.copyOf(currentLowerLim, VARIABLES_COUNT);
            Double[] upperLimOfLowerRegion = Arrays.copyOf(currentUpperLim, VARIABLES_COUNT);
            upperLimOfLowerRegion[i] = newLowerLim;
            addRegionToQueue(lowerRegion, upperLimOfLowerRegion, lowerLimOfLowerRegion);

            currentLowerLim[i] = newLowerLim;
            currentUpperLim[i] = newUpperLim;
        }

        return priorityQueue.peek();
    }

    /**
     * add the region to priority queue
     *
     * @param region   the region whose upper and lower limit array are not updated
     * @param upperLim the upper limit array of the region
     * @param lowerLim the lower limit array pf the region
     */
    private void addRegionToQueue(DsdcoRegion region, Double[] upperLim, Double[] lowerLim) {
        region.setUpperLim(upperLim);
        region.setLowerLim(lowerLim);
        if (checkRegionLogical(region)) {
            OptimizationResult resultInUpperRegion = regionCalculateService.getResultInRegion(region);
            Double[] bestVariables = new Double[VARIABLES_COUNT];
            for (int i = 0; i < bestVariables.length; i++) {
                bestVariables[i] = resultInUpperRegion.getVariables()[i];
            }
            region.setBestVariables(bestVariables);
            region.setMinTargetFunValue(resultInUpperRegion.getScore());
            System.out.println("New Region add to the priority queue: ");
            System.out.println(Arrays.toString(region.getUpperLim()));
            System.out.println(Arrays.toString(region.getLowerLim()));
            priorityQueue.add(region);
            System.out.println("Region priority queue size: " + priorityQueue.size());
        }
    }

    /**
     * discard the region that is too small or illogical
     *
     * @param region the region to be checked
     * @return whether the region is suitable for next round of calculation
     */
    private boolean checkRegionLogical(DsdcoRegion region) {
        Double[] upperLim = region.getUpperLim();
        Double[] lowerLim = region.getLowerLim();

        for (int i = 0; i < VARIABLES_COUNT; i++) {
            if (upperLim[i] - lowerLim[i] <= 0) return false;
        }

        return true;
    }

    /**
     * get the distance between 2 points
     *
     * @param point1 point1
     * @param point2 point2
     * @return the distance between 2 points
     */
    private double getDistance(DsdcoTarget point1, DsdcoTarget point2) {
        Double[] variables1 = point1.getVariables();
        Double[] variables2 = point2.getVariables();
        int length = variables1.length;

        double result = .0;

        for (int i = 0; i < length; i++) {
            double difference = variables1[i] - variables2[i];
            difference *= difference;
            result += difference;
        }

        return Math.sqrt(result);
    }

    /**
     * close the task and clear all variables
     */
    public void closeTask() {
        closestPointMap.clear();
        iteratorCount = 0;
        currentTaskId = null;
        priorityQueue.clear();
        currentTarget = null;
        currentScore = .0;
        currentRegion = null;
        DsdcoTarget dsdcoTarget = new DsdcoTarget();
        // -1 means current task is finished, notify the disciplinary calculator that the task is finished
        // and clear the variables
        dsdcoTarget.setIteratorCount(-1);
        dsdcoTarget.setDisciplinaryName(SYSTEM_NAME);
        regionCalculateService.sendTarget2Disciplinary(dsdcoTarget);
    }

    @PreDestroy
    public void closeConsumer() {
        consumer.shutdown();
    }

    private void printMessage() {
        System.out.println("The target of this iterator is: \n" + currentTarget);
        System.out.println("Current region: ");
        System.out.println(Arrays.toString(currentRegion.getUpperLim()));
        System.out.println(Arrays.toString(currentRegion.getLowerLim()));
        System.out.println("The size of region queue is: " + priorityQueue.size());
    }
}
