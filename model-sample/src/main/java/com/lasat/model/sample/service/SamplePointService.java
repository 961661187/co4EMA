package com.lasat.model.sample.service;

import com.lasat.model.sample.bean.SamplePoint;

import java.util.List;

public interface SamplePointService {
    void insert(SamplePoint samplePoint);

    List<SamplePoint> selectPartitions(Integer start, Integer count);

    void clear();

    List<SamplePoint> getAll();
}
