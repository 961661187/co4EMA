package com.lasat.model.proxy.service.impl;

import com.lasat.model.proxy.bean.Proxy;
import com.lasat.model.proxy.bean.SamplePoint;
import com.lasat.model.proxy.feign.SamplePointFeign;
import com.lasat.model.proxy.mapper.ProxyMapper;
import com.lasat.model.proxy.service.ProxyService;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ProxyServiceImpl implements ProxyService {

    @Autowired
    private SamplePointFeign samplePointFeign;

    @Autowired
    private ProxyMapper proxyMapper;

    private BasicNetwork network = null;
    private final String NETWORK_NAME = "network_proxy";

    @Override
    public Double getTr(SamplePoint samplePoint) {
        if (network == null) {
            network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(NETWORK_NAME));
        }
        if (network == null) {
            return null;
        } else {
            double[] data = getData(samplePoint);
            MLData input = new BasicMLData(data);
            return inverseNormalize(network.compute(input).getData(0));
        }
    }

    @Override
    public List<Double> getTrList(List<SamplePoint> samplePointList) {
        if (network == null) {
            network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(NETWORK_NAME));
        }
        if (network == null) {
            return null;
        } else {
            List<Double> result = new ArrayList<>();
            for (SamplePoint samplePoint : samplePointList) {
                result.add(getTr(samplePoint));
            }
            return result;
        }
    }

    @Async
    @Override
    public void train() {
        //create a network
        network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 6));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 100));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 1));
        network.getStructure().finalizeStructure();
        network.reset();

        //get the normalized training data
        List<SamplePoint> points = samplePointFeign.getAll().getData();
        int length = points.size();

        double[][] input = new double[length][6];
        double[][] output = new double[length][1];

        for (int i = 0; i < points.size(); i++) {
            SamplePoint samplePoint = points.get(i);
            double[] parameters = getData(samplePoint);
            input[i] = parameters;
            output[i] = new double[]{normalize(samplePoint.getTr())};
        }

        //train the neural network
        MLDataSet trainingSet = new BasicMLDataSet(input, output);

        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
        int epoch = 1;
        do {
            train.iteration();
            epoch++;
        } while (train.getError() > 0.0001 && epoch <= 10000);

        //save proxy information to database
        Proxy proxy = new Proxy();
        proxy.setAccuracy(1 - train.getError());
        proxy.setSampleCount(length);
        proxy.setCreateTime(new Date());
        proxyMapper.insert(proxy);

        //neural network persistence
        EncogDirectoryPersistence.saveObject(new File(NETWORK_NAME), network);
    }

    @Override
    public List<Proxy> getAll() {
        return proxyMapper.selectList(null);
    }

    @Override
    public List<Proxy> findByModelId(Integer modelId) {
        return proxyMapper.findByModelId(modelId);
    }

    /**
     * transform the sample point to array
     *
     * @param samplePoint sample point
     * @return array
     */
    private double[] getData(SamplePoint samplePoint) {
        double[] result = new double[6];
        result[0] = normalize(samplePoint.getDs(), 0.025, 0.1);
        result[1] = normalize(samplePoint.getAlpha(), 0.04, 0.16);
        result[2] = normalize(samplePoint.getFlux(), 0.1, 0.4);
        result[3] = normalize(samplePoint.getL(), 0.01, 0.04);
        result[4] = normalize(samplePoint.getR(), 0.5, 2);
        result[5] = normalize(samplePoint.getKp(), 1000, 4000);
        return result;
    }

    private double normalize(double data, double min, double max) {
        double mid = min + (max - min) / 2;
        double half = max - mid;
        data -= mid;
        return data / half;
    }

    private double normalize(double data) {
        return (data - 0.4) / 1.3;
    }

    private double inverseNormalize(double data) {
        return data * 1.3 + 0.4;
    }
}
