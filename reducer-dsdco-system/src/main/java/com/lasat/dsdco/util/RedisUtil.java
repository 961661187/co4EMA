package com.lasat.dsdco.util;

import com.lasat.dsdco.bean.DsdcoRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RedisUtil {
    @Autowired
    private RedisTemplate<String, DsdcoRegion> redisTemplate;

    private final String LIST_KEY = "QueueStorage";

    // TODO bug exits
    /**
     * flush the priority queue to redis when the queue is too big
     * @param priorityQueue priority queue contains data
     */
    public DsdcoRegion flush(PriorityQueue<DsdcoRegion> priorityQueue, int flushCount) {
        int keepSize = Math.max(priorityQueue.size() - flushCount, 0);
        List<DsdcoRegion> temp = new ArrayList<>();
        while (keepSize != 0) {
            temp.add(priorityQueue.poll());
            keepSize--;
        }

        BoundListOperations<String, DsdcoRegion> queueStorage = redisTemplate.boundListOps(LIST_KEY);
        DsdcoRegion bestRegionInRedis = priorityQueue.poll();
        while (!priorityQueue.isEmpty() && flushCount != 0) {
            DsdcoRegion dsdcoRegion = priorityQueue.poll();
            if (dsdcoRegion != null)
                queueStorage.rightPush(dsdcoRegion);
            flushCount--;
        }

        for (DsdcoRegion dsdcoRegion : temp) {
            priorityQueue.offer(dsdcoRegion);
        }

        return bestRegionInRedis;
    }

    // TODO test for best point in redis, pre test failed
    public DsdcoRegion pullFromRedis(PriorityQueue<DsdcoRegion> priorityQueue, int flushCount) {
        BoundListOperations<String, DsdcoRegion> queueStorage = redisTemplate.boundListOps(LIST_KEY);
        while (flushCount != 0) {
            DsdcoRegion dsdcoRegion = queueStorage.leftPop();
            if (dsdcoRegion != null)
                priorityQueue.offer(dsdcoRegion);
            flushCount--;
        }
        DsdcoRegion bestRegionInRedis = queueStorage.leftPop();
        if (bestRegionInRedis != null)
            queueStorage.leftPush(bestRegionInRedis);
        return bestRegionInRedis;
    }
}
