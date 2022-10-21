package com.lasat.ema.ga;

import com.lasat.model.sample.bean.SamplePoint;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EMASystemProblem extends AbstractDoubleProblem {
    private final double voltage = 330;
    private final int pn = 4;
    private final int actTimes = 2000;
    private final int powerDensity = 120;
    private final double materialDensity = 7850;

    public EMASystemProblem(double[] lowerLimit, double[] upperLimit) {
        this(6, lowerLimit, upperLimit);
    }

    public EMASystemProblem(Integer numberOfVariables, double[] lowerLimit, double[] upperLimit) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(1);
        setName("EMA");

        List<Double> lowerLimitList = new ArrayList<>(getNumberOfVariables()) ;
        List<Double> upperLimitLIst = new ArrayList<>(getNumberOfVariables()) ;

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimitList.add(lowerLimit[i]);
            upperLimitLIst.add(upperLimit[i]);
        }

        setLowerLimit(lowerLimitList);
        setUpperLimit(upperLimitLIst);
    }

    /** Evaluate() method */
    public void evaluate(DoubleSolution solution) {
        double[] variables = new double[getNumberOfVariables()];

        for (int i = 0; i < variables.length; i++) {
            variables[i] = solution.getVariableValue(i);
        }
        /**
         * The object function can be changed here
         */
        //solution.setObjective(0, getMass(variables) + 1000 * Math.max( getTs(variables) - 0.8410799268363229, 0));
        solution.setObjective(0, Math.max(getMass(variables) - 43, 0) + getTs(variables));
    }

    public double getMass(double[] variables) {
        SamplePoint samplePoint = arr2samplePoint(variables);
        double massOfMotor = getMassOfMotor(samplePoint);
        double massOfBattery = getMassOfBattery(samplePoint);
        double massOfScrew = getMassOfScrew(samplePoint);

        return massOfBattery + massOfMotor + massOfScrew;
    }

    public double getTs(double[] variables) {
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("network_proxy"));
        double[] data = getData(arr2samplePoint(variables));
        MLData input = new BasicMLData(data);
        return inverseNormalize(network.compute(input).getData(0));
    }

    private double getMassOfMotor(SamplePoint samplePoint) {
        return 0.05 * (1.3552 + 0.0213 * voltage / samplePoint.getR()+ 0.8262 * pn * samplePoint.getFlux() * voltage / samplePoint.getR());
    }

    public double getMassOfBattery(SamplePoint samplePoint) {
        BasicNetwork network4Cost = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File("network_proxy_cost"));
        double[] data = getData(samplePoint);
        MLData input = new BasicMLData(data);
        double cost = inverseNormalizeCost(network4Cost.compute(input).getData(0));
        return cost * actTimes * powerDensity / 3600000;
    }

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

    private double getMassOfScrew(SamplePoint samplePoint) {
        return 0.25 * Math.PI * materialDensity * (samplePoint.getDs() * samplePoint.getDs() * 0.5);
    }

    private double normalize(double data, double min, double max) {
        double mid = min + (max - min) / 2;
        double half = max - mid;
        data -= mid;
        return data / half;
    }

    private double inverseNormalize(double data) {
        return data * 2 + 0.2;
    }

    private double inverseNormalizeCost(double data) {
        return data * 800 + 200;
    }

    private SamplePoint arr2samplePoint(double[] variables) {
        double ld = variables[0];
        double rs = variables[1];
        double flux = variables[2];
        double ds = variables[3];
        double alpha = variables[4];
        double kp = variables[5];
        return new SamplePoint(0, ds, alpha, flux, ld, rs, (int)kp, 0, 0);
    }
}
