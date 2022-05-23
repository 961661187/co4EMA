package com.lasat.model.proxy.service;

import com.lasat.model.proxy.bean.SamplePoint;

public interface SamplePointService {
    SamplePoint getById(Integer id);

    Boolean insert(SamplePoint samplePoint);
}
