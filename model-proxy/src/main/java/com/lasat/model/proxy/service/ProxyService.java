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
    double getTr(SamplePoint samplePoint);

    List<Double> getTrList(List<SamplePoint> samplePointList);

    /**
     * get the power cost based on EMA parameters
     * @param samplePoint sample point data
     * @return the step response time
     */
    double getCost(SamplePoint samplePoint);

    List<Double> getCostList(List<SamplePoint> samplePointList);

    /**
     * retrain the neural network
     */
    void train();

    List<Proxy> getAll();

    List<Proxy> findByModelId(Integer modelId);

}
