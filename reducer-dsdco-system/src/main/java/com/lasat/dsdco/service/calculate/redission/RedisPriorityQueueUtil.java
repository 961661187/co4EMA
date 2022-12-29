package com.lasat.dsdco.service.calculate.redission;

public interface RedisPriorityQueueUtil<T> {
    void clear();
    T peek();
    T poll();
    void offer(T t);
}
