package com.lasat.dsdco.service.calculate.redission;

import org.redisson.Redisson;
import org.redisson.api.RPriorityQueue;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

public class RedisPriorityQueueUtil<T> {

    private final int sizeThreshold;

    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.password}")
    private String password;

    private final PriorityQueue<T> memoryPriorityQueue;
    private final List<RPriorityQueue<T>> redisPriorityQueueList;
    private T bestElementInRedis;
    private final Comparator<T> comparator;

    /**
     * initialize the priority queues in memory and redis
     * @param variableCount the number of the queues in redis depends ont the count of the variables
     * @param taskId current task id is a part of the name of priority queues
     * @param comparator comparator for priority queues
     * @param flushThreshold flush threshold is set for preventing OutOfMemoryException
     */
    public RedisPriorityQueueUtil(int variableCount, long taskId, Comparator<T> comparator, int flushThreshold) {
        // the number of priority queues depends on the number of variables
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setPassword(password);
        RedissonClient redissonClient = Redisson.create(config);
        this.comparator = comparator;
        this.memoryPriorityQueue = new PriorityQueue<>(comparator);
        this.redisPriorityQueueList = new ArrayList<>();
        for (int i = 0; i < variableCount; i++) {
            RPriorityQueue<T> rPriorityQueue = redissonClient.getPriorityQueue(getQueueName(taskId, i));
            try {
                rPriorityQueue.trySetComparator(comparator);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.redisPriorityQueueList.add(rPriorityQueue);
        }
        this.bestElementInRedis = null;
        this.sizeThreshold = Math.min(flushThreshold, variableCount * 100);
    }

    /**
     * get the best element and delete it from queues
     * @return the best region
     */
    public T poll() {
        checkQueues();
        return memoryPriorityQueue.poll();
    }

    /**
     *
     * @param t elemetn wil be added to queues
     */
    public void offer(T t) {
        memoryPriorityQueue.offer(t);
        checkQueues();
    }

    /**
     * get the best element but don't delete it
     * @return the best element
     */
    public T peek() {
        checkQueues();
        return memoryPriorityQueue.peek();
    }

    public void clear() {
        memoryPriorityQueue.clear();
        for (RPriorityQueue<T> ts : redisPriorityQueueList) {
            ts.clear();
        }
    }

    private void checkQueues() {
        if (bestElementInRedis == null || memoryPriorityQueue.isEmpty()) {
            return;
        }
        T bestElementInMemory = memoryPriorityQueue.peek();
        if (comparator.compare(bestElementInMemory, bestElementInRedis) > 0 || memoryPriorityQueue.size() >= sizeThreshold) {
            shuffleQueues();
        }
    }

    private void shuffleQueues() {
        // load balance for priority queue
        PriorityQueue<RPriorityQueue<T>> queueSelectorByCount = new PriorityQueue<>(Comparator.comparingInt(Collection::size));
        for (RPriorityQueue<T> queue : redisPriorityQueueList) {
            queueSelectorByCount.offer(queue);
        }
        while (!memoryPriorityQueue.isEmpty()) {
            RPriorityQueue<T> minCountQueue = queueSelectorByCount.poll();
            if (minCountQueue != null) {
                minCountQueue.offer(memoryPriorityQueue.poll());
                queueSelectorByCount.offer(minCountQueue);
            } else {
                System.out.println("Get null queue from redis");
            }
        }

        PriorityQueue<RPriorityQueue<T>> queueSelectorByBestElement = new PriorityQueue<>((q1, q2) -> comparator.compare(q1.peek(), q2.peek()));
        for (RPriorityQueue<T> queue : redisPriorityQueueList) {
            queueSelectorByBestElement.offer(queue);
        }
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
        }
        if (queueSelectorByBestElement.peek() != null) {
            bestElementInRedis = queueSelectorByBestElement.peek().peek();
        }

        System.out.println("queue in memory updated:\n" + memoryPriorityQueue);
    }

    private String getQueueName(Long taskId, int index) {
        return taskId + "PriorityQueue" + index;
    }
}
