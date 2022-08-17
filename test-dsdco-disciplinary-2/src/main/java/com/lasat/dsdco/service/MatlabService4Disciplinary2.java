package com.lasat.dsdco.service;

import com.lasat.dsdco.bean.OptimizationResult;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import matlabSQP.MatlabSQP;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MatlabService4Disciplinary2 {

    //the upper limit of each variables
    private final double[] upperLim = new double[]{100.0, 100};
    //the lower limit of each variables
    private final double[] lowerLim = new double[]{0.0, -100.0};
    //the count of variables
    private final int variablesCount = 2;
    private MatlabSQP matlabSQP;

    @PostConstruct
    public void initMatlab() {
        try {
            this.matlabSQP = new MatlabSQP();
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

    private boolean checkConstraint(double[] targetVal) {
        for (int i = 0; i < variablesCount; i++) {
            if (targetVal[i] > upperLim[i] || targetVal[i] < lowerLim[i]) return false;
        }
        double constraints1 = 0.1 * Math.pow(1.5 * targetVal[0] - 20, 2) - 70 - targetVal[1];
        double constraints2 = 50 / (targetVal[0] + 0.1) - 40 - targetVal[1];
        //the convergence condition threshold
        double convergenceConditionThreshold = 0.00101;
        return constraints1 <= convergenceConditionThreshold && constraints2 <= convergenceConditionThreshold;
    }

    private double[] getClosetPointBySQP(double[] targetVal) {
        MWNumericArray target = new MWNumericArray(targetVal, MWClassID.DOUBLE);
        try {
            Object[] objects = this.matlabSQP.cloestPointOfDisciplinary2(1, target);
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
