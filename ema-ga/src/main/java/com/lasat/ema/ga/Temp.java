package com.lasat.ema.ga;

import com.lasat.dsdco.bean.OptimizationResult;

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
        EMASystemProblem problem = new EMASystemProblem(new double[]{0.026, 1.2, 0.25, 0.65, 0.1, 2500}, new double[]{0.016, 0.8, 0.16, 0.04, 0.064, 1500});
        System.out.println("Best Mass: " + problem.getMass(new double[]{0.024581000018259577, 1.0524005990527985, 0.24505720436551826, 0.04095680097197669, 0.09987823607090965, 2386.0990323444717}));

        GaBestPointService service = new GaBestPointService();
        OptimizationResult resultInRegion = service.getResultInRegion(new double[]{0.026, 1.2, 0.25, 0.65, 0.1, 2500}, new double[]{0.016, 0.8, 0.16, 0.04, 0.08, 2000});
        System.out.println(Arrays.toString(resultInRegion.getVariables()));
        System.out.println("Original Ts: " + problem.getTs(new double[]{0.02, 1, 0.2, 0.05, 0.08, 2000}));
        System.out.println("Best Ts: " + problem.getTs(resultInRegion.getVariables()));
        System.out.println("Original Mass: " + problem.getMass(new double[]{0.02, 1, 0.2, 0.05, 0.08, 2000}));
        System.out.println("Best Mass: " + problem.getMass(resultInRegion.getVariables()));
    }
}
