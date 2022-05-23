package com.lasat.model.sample.service;

import com.lasat.model.sample.bean.SamplePoint;

import java.util.List;

public interface SamplePointService {
    SamplePoint getById(Integer id);

    void insert(SamplePoint samplePoint);

    List<SamplePoint> selectPartitions(Integer start);
}
