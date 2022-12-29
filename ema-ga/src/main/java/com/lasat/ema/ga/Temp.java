package com.lasat.ema.ga;

import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.model.sample.bean.SamplePoint;

import java.util.Arrays;

public class Temp {
    public static void main(String[] args) {
        /*
         * double ld = variables[0] = 0.02;
         * double rs = variables[1] = 1;
         * double flux = variables[2] = 0.2;
         * double ds = variables[3] = 0.05;
         * double alpha = variables[4] = 0.08;
         * double kp = variables[5] = 2000;
         */
        //calculate();
        //getMass(new double[]{0.0227, 1.197, 0.246, 0.0509, 0.0967, 2500});
        for (int i = 1; i <= 1329; i++) {
            System.out.print(i + ",");
        }
    }

    public static void calculate() {
        EMASystemProblem problem = new EMASystemProblem(new double[]{0.026, 1.2, 0.25, 0.65, 0.1, 2800}, new double[]{0.016, 0.8, 0.16, 0.04, 0.064, 4500});

        GaBestPointService service = new GaBestPointService();
        OptimizationResult resultInRegion = service.getResultInRegion(new double[]{0.026, 1.2, 0.25, 0.65, 0.1, 2500}, new double[]{0.016, 0.8, 0.16, 0.04, 0.08, 2000});
        System.out.println(Arrays.toString(resultInRegion.getVariables()));
        System.out.println("Original Ts: " + problem.getTs(new double[]{0.02, 1, 0.2, 0.05, 0.08, 2000}));
        System.out.println("Best Ts: " + problem.getTs(resultInRegion.getVariables()));
        System.out.println("Original Mass: " + problem.getMass(new double[]{0.02, 1, 0.2, 0.05, 0.08, 2000}));
        System.out.println("Best Mass: " + problem.getMass(resultInRegion.getVariables()));
    }

    public static void getMass(double[] variables) {
        EMASystemProblem problem = new EMASystemProblem(new double[]{0.026, 1.2, 0.25, 0.65, 0.1, 2500}, new double[]{0.016, 0.8, 0.16, 0.04, 0.064, 1500});
        System.out.println("The mass of EMA is " + problem.getMass(variables));
        SamplePoint samplePoint = problem.arr2samplePoint(variables);
        System.out.println("The mass of battery is " + problem.getMassOfBattery(samplePoint));
        System.out.println("The mass of motor is " + problem.getMassOfMotor(samplePoint));
        System.out.println("The mass of screw is " + problem.getMassOfScrew(samplePoint));
    }
}
