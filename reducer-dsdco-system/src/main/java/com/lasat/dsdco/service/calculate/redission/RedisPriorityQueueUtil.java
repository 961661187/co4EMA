package com.lasat.dsdco.service.calculate.redission;

import org.redisson.Redisson;
import org.redisson.api.RPriorityQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.*;

public class RedisPriorityQueueUtil<T> {

    private final int sizeThreshold;

    private final PriorityQueue<T> memoryPriorityQueue;
    private final List<RPriorityQueue<T>> redisPriorityQueueList;
    private T bestElementInRedis;
    private final Comparator<T> comparator;
    private final int variableCount;
    private int flushIndex = 0;

    /**
     * initialize the priority queues in memory and redis
     *
     * @param variableCount  the number of the queues in redis depends ont the count of the variables
     * @param taskId         current task id is a part of the name of priority queues
     * @param comparator     comparator for priority queues
     * @param flushThreshold flush threshold is set for preventing OutOfMemoryException
     */
    public RedisPriorityQueueUtil(int variableCount, long taskId, Comparator<T> comparator, int flushThreshold, String host, String port, String password) {
        // the number of priority queues depends on the number of variables
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password).setPingTimeout(5000).setRetryAttempts(3).setRetryInterval(5000);
        RedissonClient redissonClient = Redisson.create(config);
        this.comparator = comparator;
        this.memoryPriorityQueue = new PriorityQueue<>(comparator);
        this.redisPriorityQueueList = new ArrayList<>();
        this.variableCount = variableCount * 10;
        for (int i = 0; i < this.variableCount; i++) {
            RPriorityQueue<T> rPriorityQueue = redissonClient.getPriorityQueue(getQueueName(taskId, i));
            try {
                rPriorityQueue.trySetComparator(comparator);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.redisPriorityQueueList.add(rPriorityQueue);
        }
        this.bestElementInRedis = null;
        this.sizeThreshold = flushThreshold;
    }

    /**
     * get the best element and delete it from queues
     *
     * @return the best region
     */
    public T poll() {
        checkQueues(true);
        return memoryPriorityQueue.poll();
    }

    /**
     * @param t elemetn wil be added to queues
     */
    public void offer(T t) {
        memoryPriorityQueue.offer(t);
        checkQueues(false);
    }

    /**
     * get the best element but don't delete it
     *
     * @return the best element
     */
    public T peek() {
        return memoryPriorityQueue.peek();
    }

    public void clear() {
        memoryPriorityQueue.clear();
        for (RPriorityQueue<T> ts : redisPriorityQueueList) {
            ts.clear();
        }
        redisPriorityQueueList.clear();
        bestElementInRedis = null;
    }

    private void checkQueues(boolean poll) {
        if (!poll) {
            // offer
            if (memoryPriorityQueue.size() >= sizeThreshold) {
                shuffleQueues();
            }
        } else {
            // poll
            if (bestElementInRedis == null) {
                return;
            }
            T bestElementInMemory = memoryPriorityQueue.peek();
            if (bestElementInMemory == null || comparator.compare(bestElementInMemory, bestElementInRedis) > 0) {
                shuffleQueues();
            }
        }

    }

    private void shuffleQueues() {
        // there id no element in redis
        if (bestElementInRedis == null) {
            System.out.println("Start flushing to Redis");
            List<T> temp = new ArrayList<>();
            while (temp.size() < sizeThreshold * 0.25) {
                temp.add(memoryPriorityQueue.poll());
            }
            bestElementInRedis = memoryPriorityQueue.peek();
            long startTime = System.currentTimeMillis();
            while (!memoryPriorityQueue.isEmpty()) {
                flushBestElementToRedis(startTime);
            }
            for (T t : temp) {
                memoryPriorityQueue.offer(t);
            }
            System.out.println("Flushing to Redis succeed");
            System.out.println("Don't need to flush to memory");
            return;
        }
        // load balance for priority queue
        System.out.println("Start flushing to Redis");
        long startTime = System.currentTimeMillis();
        while (!memoryPriorityQueue.isEmpty()) {
            flushBestElementToRedis(startTime);
        }
        System.out.println("Flushing to Redis succeed");

        PriorityQueue<RPriorityQueue<T>> queueSelectorByBestElement = new PriorityQueue<>((q1, q2) -> comparator.compare(q1.peek(), q2.peek()));
        for (RPriorityQueue<T> queue : redisPriorityQueueList) {
            queueSelectorByBestElement.offer(queue);
        }
        System.out.println("Start flushing to memory");
        startTime = System.currentTimeMillis();
        while (memoryPriorityQueue.size() < sizeThreshold * 0.25 && !queueSelectorByBestElement.isEmpty()) {
            RPriorityQueue<T> bestQueue = queueSelectorByBestElement.poll();
            if (bestQueue == null || bestQueue.isEmpty()) {
                continue;
            }
            T bestElement = bestQueue.poll();
            if (bestElement != null) {
                memoryPriorityQueue.offer(bestElement);
                queueSelectorByBestElement.offer(bestQueue);
            }
            if (memoryPriorityQueue.size() % 100 == 0) {
                System.out.println(": Current progress: " + 400 * memoryPriorityQueue.size() / sizeThreshold + "%, Time cost: " + (System.currentTimeMillis() - startTime) / 1000 + "s");
            }
        }
        System.out.println("Flushing to memory succeed");
        while (queueSelectorByBestElement.peek() != null) {
            T peek = queueSelectorByBestElement.poll().peek();
            if (peek != null) {
                bestElementInRedis = peek;
                break;
            }
        }
    }

    private void flushBestElementToRedis(long startTime) {
        flushIndex = flushIndex % variableCount;
        RPriorityQueue<T> queue = redisPriorityQueueList.get(flushIndex++);
        if (queue != null) {
            queue.offer(memoryPriorityQueue.poll());
            if (memoryPriorityQueue.size() % 100 == 0) {
                System.out.println(System.currentTimeMillis() + ": Current progress: " + 100 * (sizeThreshold - memoryPriorityQueue.size()) / sizeThreshold + "%, Time cost: " + (System.currentTimeMillis() - startTime) / 1000 + "s");
            }
        } else {
            System.out.println("Get null queue from redis");
        }
    }

    private String getQueueName(Long taskId, int index) {
        return taskId + "PriorityQueue" + index;
    }
}
