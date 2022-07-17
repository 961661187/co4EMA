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
public class BestPointService {
    //the upper limit of each variables
    private final double[] originUpperLim = new double[]{3.6, 0.8, 28.0, 8.3, 8.3, 3.9, 5.5};
    //the lower limit of each variables
    private final double[] originLowerLim = new double[]{2.6, 0.7, 17.0, 7.3, 7.3, 2.9, 5.0};
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


    public OptimizationResult getResultInRegion(double[] upperLim, double[] lowerLim) {
        MWNumericArray lower = new MWNumericArray(lowerLim, MWClassID.DOUBLE);
        MWNumericArray upper = new MWNumericArray(upperLim, MWClassID.DOUBLE);

        try {
            Object[] objects = sqp4Reducer.bestPointInRegion(1, lower, upper);
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
            lower.dispose();
            upper.dispose();
            return new OptimizationResult(getScore(result), result);
        } catch (MWException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double getScore(double[] variables) {
        // TODO
        throw new RuntimeException("Not implemented method");
    }
}
