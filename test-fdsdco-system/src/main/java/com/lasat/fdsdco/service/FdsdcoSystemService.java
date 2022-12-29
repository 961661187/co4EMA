package com.lasat.fdsdco.service;

import com.alibaba.fastjson.JSON;
import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.dsdco.bean.Point;
import com.lasat.dsdco.bean.Space;
import com.lasat.dsdco.node.SystemCalculator;
import com.lasat.dsdco.util.ConvertUtil;
import com.lasat.dsdco.util.ObjectMapperSingleton;
import com.lasat.fdsdco.config.CalculationConfig;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class FdsdcoSystemService {
    @Autowired
    private MqService4System mqService4System;
    @Autowired
    private SpaceServiceImpl spaceService;
    @Autowired
    private SystemCalculator systemCalculator;

    // the message producer of system optimization
    private final ResourceBundle mqConfig = ResourceBundle.getBundle("mqConfig4testSystem");
    // the message consumers of system optimization
    private final DefaultMQPushConsumer consumer4space = new DefaultMQPushConsumer(mqConfig.getString("consumerGroupTest4space"));
    private final DefaultMQPushConsumer consumer4point = new DefaultMQPushConsumer(mqConfig.getString("consumerGroupTest4point"));

    private final Map<String, Space> spaceMap = new HashMap<>();
    private final Map<String, Point> pointMap = new HashMap<>();

    // iterator count and current task id are deemed as idempotent token
    private Integer pointCount = -1;
    private Integer spaceCount = -1;
    private Long currentTaskId = null;
    private Space currentSpace = null;
    private Point currentPoint = null;

    // reentrant lock is used to ensure only one thread can operate the spaces
    private final ReentrantLock lock4space = new ReentrantLock();
    private final ReentrantLock lock4point = new ReentrantLock();

    public void startTask() {
        pointCount++;
        spaceCount++;

        currentSpace = new Space();
        currentSpace.setLowerLim(new Double[]{0.0, -100.0});
        currentSpace.setUpperLim(new Double[]{100.0, 100.0});
        OptimizationResult optimizedPoint = systemCalculator.getBestPoint(currentSpace);
        Point point = ConvertUtil.optimizationResult2Point(optimizedPoint, "test-system", currentTaskId, 0);
        currentSpace.setBestVariables(point.getVariables());
        currentSpace.setMinTargetFunValue(point.getScore());
        currentSpace.setTaskId(currentTaskId);
        currentSpace.setIteratorCount(pointCount);
        String spaceStr = JSON.toJSONString(currentSpace);
        mqService4System.sendMessage(spaceStr, mqConfig.getString("system2DisciplinaryTopicTest"), mqConfig.getString("spaceTagTest"));
    }

    public void closeTask() {
        pointCount = -1;
        spaceCount = -1;
        currentTaskId = null;
        currentSpace = null;
        currentPoint = null;
        pointMap.clear();
        spaceMap.clear();

        Point point = new Point();
        // -1 means current task is finished, notify the disciplinary calculator that the task is finished
        // and clear the variables
        point.setIteratorCount(-1);
        String pointStr = JSON.toJSONString(point);
        mqService4System.sendMessage(pointStr, mqConfig.getString("system2DisciplinaryTopicTest"), mqConfig.getString("pointTagTest"));
    }

    /**
     * The process for space calculation
     */
    @PostConstruct
    public void initConsumer4Space() {
        consumer4space.setNamesrvAddr(mqConfig.getString("nameServerAddress"));
        consumer4space.setInstanceName(mqConfig.getString("consumerInstanceNameTest4space"));
        try {
            consumer4space.subscribe(mqConfig.getString("disciplinary2SystemTopicTest"), mqConfig.getString("spaceTagTest"));
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        consumer4space.registerMessageListener((MessageListenerConcurrently) (messages, consumeConcurrentlyContext) -> {
            for (MessageExt message : messages) {
                // the check result of a disciplinary calculator
                String spaceJson = new String(message.getBody());
                Space space = null;
                try {
                    space = ObjectMapperSingleton.getInstance().getObjectMapper().readValue(spaceJson, Space.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    lock4space.lock();
                    if (space != null
                            && space.getTaskId().equals(currentTaskId)
                            && space.getIteratorCount().equals(spaceCount)
                            && !spaceMap.containsKey(space.getDisciplinaryName())) {
                        // only one thread can operate the region
                        spaceMap.put(space.getDisciplinaryName(), space);
                        checkDisciplinarySpaceResult();
                    } else {
                        System.out.println("Duplicate message received: " + space);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock4space.unlock();
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        try {
            consumer4space.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    private void checkDisciplinarySpaceResult() {
        if (spaceMap.size() < CalculationConfig.getDisciplinaryCount()) {
            return;
        }

        boolean isCurrentSpaceValid = true;
        for (Map.Entry<String, Space> spaceEntry : spaceMap.entrySet()) {
            Space space = spaceEntry.getValue();
            if (!space.getValid()) {
                isCurrentSpaceValid = false;
                break;
            }
        }
        // space check has been finished
        spaceCount++;
        spaceMap.clear();

        if (isCurrentSpaceValid) {
            Point point = ConvertUtil.getBestPointFromSpace(currentSpace, pointCount);
            currentPoint = point;
            System.out.println(currentPoint.getScore());
            String pointStr = JSON.toJSONString(point);
            mqService4System.sendMessage(pointStr, mqConfig.getString("system2DisciplinaryTopicTest"), mqConfig.getString("pointTagTest"));
        } else {
            if (spaceService.getSpaceCount() == 0) {
                System.out.println("[ERROR]: No suitable result!!");
                return;
            }
            currentSpace = spaceService.getBestSpace();
            currentSpace.setIteratorCount(spaceCount);
            currentSpace.setTaskId(currentTaskId);
            String spaceStr = JSON.toJSONString(currentSpace);
            mqService4System.sendMessage(spaceStr, mqConfig.getString("system2DisciplinaryTopicTest"), mqConfig.getString("spaceTagTest"));
        }

    }

    /**
     * The process for point calculation
     */
    @PostConstruct
    public void initConsumer4Point() {
        consumer4point.setNamesrvAddr(mqConfig.getString("nameServerAddress"));
        consumer4point.setInstanceName(mqConfig.getString("consumerInstanceNameTest4point"));
        try {
            consumer4point.subscribe(mqConfig.getString("disciplinary2SystemTopicTest"), mqConfig.getString("pointTagTest"));
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        consumer4point.registerMessageListener((MessageListenerConcurrently) (messages, consumeConcurrentlyContext) -> {
            for (MessageExt message : messages) {
                // the closest point of a disciplinary calculator
                String pointJson = new String(message.getBody());
                Point point = null;
                try {
                    point = ObjectMapperSingleton.getInstance().getObjectMapper().readValue(pointJson, Point.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    lock4point.lock();
                    if (point != null
                            && point.getTaskId().equals(currentTaskId)
                            && point.getIteratorCount().equals(pointCount)
                            && !pointMap.containsKey(point.getDisciplinaryName())) {
                        // only one thread can operate the region
                        pointMap.put(point.getDisciplinaryName(), point);
                        checkDisciplinaryPointResult();
                    } else {
                        System.out.println("Duplicate message received: " + point);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock4point.unlock();
                }

            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        try {
            consumer4point.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    private void checkDisciplinaryPointResult() {
        if (pointMap.size() < CalculationConfig.getDisciplinaryCount()) {
            return;
        }
        int constraintMeetCount = 0;
        List<Point> closestPointList = new ArrayList<>(CalculationConfig.getDisciplinaryCount());
        for (Map.Entry<String, Point> disPointEntry : pointMap.entrySet()) {
            Point disPoint = disPointEntry.getValue();
            if (disPoint.equals(currentPoint)) {
                constraintMeetCount++;
            }
            closestPointList.add(disPoint);
        }

        if (constraintMeetCount == CalculationConfig.getDisciplinaryCount()) {
            System.out.println("the result is " + currentPoint.toString() + ", score: " + currentPoint.getScore());
            closeTask();
        } else if (pointCount >= 1000000000) {
            System.out.println("[ERROR]: The task has been calculated too many times, but don't get a result");
        } else {
            spaceService.splitSpace(currentSpace, currentPoint, closestPointList);
            if (spaceService.getSpaceCount() == 0) {
                System.out.println("[ERROR]: No suitable result!!");
                return;
            }

            // prepare for the next calculation
            pointCount++;
            pointMap.clear();
            spaceCount++;

            currentSpace = spaceService.getBestSpace();
            currentSpace.setIteratorCount(spaceCount);
            currentSpace.setTaskId(currentTaskId);
            currentPoint = null;

            String spaceStr = JSON.toJSONString(currentSpace);
            mqService4System.sendMessage(spaceStr, mqConfig.getString("system2DisciplinaryTopicTest"), mqConfig.getString("spaceTagTest"));
        }
    }


    public void setTaskId(Long taskId) {
        this.currentTaskId = taskId;
    }
}
