package com.lasat.dsdco.service;

import com.lasat.dsdco.bean.OptimizationResult;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import matlabReducer.SQP4Reducer;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MatlabService4Disciplinary1 {

    //the upper limit of each variables
    private final double[] upperLim = new double[]{3.6, 0.8, 28.0, 8.3, 8.3, 3.9, 5.5};
    //the lower limit of each variables
    private final double[] lowerLim = new double[]{2.6, 0.7, 17.0, 7.3, 7.3, 2.9, 5.0};
    //the count of variables
    private final int variablesCount = 7;
    private SQP4Reducer sqp4Reducer;

    @PostConstruct
    public void initMatlab() {
        try {
            this.sqp4Reducer = new SQP4Reducer();
        } catch (MWException e) {
            e.printStackTrace();
        }
    }

    public OptimizationResult getClosetPoint(double[] targetVal) {
        if (checkConstraint(targetVal)) {
            return new OptimizationResult(0.0, targetVal);
        } else {
            return new OptimizationResult(0.0, getClosetPointBySQP(targetVal));
        }
    }

    private boolean checkConstraint(double[] x) {
        for (int i = 0; i < variablesCount; i++) {
            if (x[i] > upperLim[i] || x[i] < lowerLim[i]) return false;
        }
       double c1 = 27 / (x[0] * x[1] * x[1] * x[2]) - 1;
       double c2 = 397.5 / (x[0] * x[1] * x[1] * x[2] * x[2]) - 1;
       double c3 = 1.93 * Math.pow(x[3], 3) / (x[1] * x[2] * Math.pow(x[5], 4)) - 1;
       double c4 = 1.93 * Math.pow(x[4], 3) / (x[1] * x[2] * Math.pow(x[6], 4)) - 1;
       double c5 = (Math.sqrt(Math.pow(745 * x[3] / (x[1] * x[2]), 2) + 16.9e6) / (0.1 * Math.pow(x[5], 3))) - 1100;
       double c6 = (Math.sqrt(Math.pow(745 * x[4] / (x[1] * x[2]), 2) + 157.5e6) / (0.1 * Math.pow(x[6], 3))) - 850;
        //the convergence condition threshold
        double convergenceConditionThreshold = 0.00101;
        return c1 <= convergenceConditionThreshold
                && c2 <= convergenceConditionThreshold
                && c3 <= convergenceConditionThreshold
                && c4 <= convergenceConditionThreshold
                && c5 <= convergenceConditionThreshold
                && c6 <= convergenceConditionThreshold;
    }

    private double[] getClosetPointBySQP(double[] targetVal) {
        MWNumericArray target = new MWNumericArray(targetVal, MWClassID.DOUBLE);
        try {
            Object[] objects = this.sqp4Reducer.cloestPointOfDisciplinary1(1, target);
            String closetPointStr = objects[0].toString();
            String[] closetPointValStr = closetPointStr.split(" ");
            double[] result = new double[variablesCount];
            int count = 0;
            for (String val : closetPointValStr) {
                if (!StringUtils.isEmpty(val)) {
                    result[count] = Double.parseDouble(val);
                    count++;
                }
            }
            // Free memory
            target.dispose();
            return result;
        } catch (MWException e) {
            e.printStackTrace();
        }
        return null;
    }
}
