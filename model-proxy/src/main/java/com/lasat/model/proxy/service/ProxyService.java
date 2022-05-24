package com.lasat.model.proxy.service;

import com.lasat.model.proxy.bean.Proxy;
import com.lasat.model.proxy.bean.SamplePoint;

import java.util.List;

public interface ProxyService {
    /**
     * get the step response time based on EMA parameters
     * @param samplePoint sample point data
     * @return the step response time
     */
    Double getTr(SamplePoint samplePoint);

    /**
     * retrain the neural network
     */
    void train();

    List<Proxy> getAll();

    List<Proxy> findByModelId(Integer modelId);

    List<Double> getTrList(List<SamplePoint> samplePointList);
}
