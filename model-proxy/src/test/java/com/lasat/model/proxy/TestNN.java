package com.lasat.model.proxy;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.arrayutil.NormalizeArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestNN {

    @Test
    public void testXOR() {
        // create and initialize a network
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 2));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 3));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 1));
        network.getStructure().finalizeStructure();
        network.reset();

        // train based on train set
        double[][] XOR_INPUT = {
                {0.0, 0.0}, {1.0, 0.0}, {0.0, 1.0}
        };
        double[][] XOR_OUTPUT = {
                {0.0}, {1.0}, {1.0}
        };
        MLDataSet trainingSet = new BasicMLDataSet(XOR_INPUT, XOR_OUTPUT);

        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);
        do {
            train.iteration();
        } while (train.getError() > 0.005);

        // get prediction result based on the trained network
        MLData input = new BasicMLData(new double[]{1, 1});
        System.out.println("Neural Network Results: ");
        MLData output = network.compute(input);
        System.out.println(input.getData(0) +
                "," + input.getData(1) +
                ", actual = " + output.getData(0) + ",ideal = 1");

        // close encog
        Encog.getInstance().shutdown();
    }
}
