package com.lasat.fdsdco.service;

import com.lasat.dsdco.bean.Space;
import com.lasat.dsdco.node.SpaceStorageService;
import org.springframework.stereotype.Service;

import java.util.PriorityQueue;

@Service
public class SimpleSpaceStorageService implements SpaceStorageService {
    private final PriorityQueue<Space> priorityQueue = new PriorityQueue<>((space1, space2) -> {
        if (space1.getMinTargetFunValue() > space2.getMinTargetFunValue()) return 1;
        else if (space1.getMinTargetFunValue().equals(space2.getMinTargetFunValue())) return 0;
        else return -1;
    });
    
    @Override
    public void addSpace(Space space) {
        priorityQueue.add(space);
    }

    @Override
    public Space getBestSpace() {
        return priorityQueue.poll();
    }

    @Override
    public int getSpaceCount() {
        return priorityQueue.size();
    }

    public void printPriorityQueue() {
        System.out.println(priorityQueue);
    }
}
