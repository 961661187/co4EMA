package dsdco.service;

import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.dsdco.bean.Space;
import com.mathworks.toolbox.javabuilder.MWClassID;
import com.mathworks.toolbox.javabuilder.MWException;
import com.mathworks.toolbox.javabuilder.MWNumericArray;
import matlabSQP.MatlabSQP;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class MatlabService4Disciplinary1 {

    //the upper limit of each variables
    private final double[] upperLim = new double[]{100.0, 100};
    //the lower limit of each variables
    private final double[] lowerLim = new double[]{0.0, -100.0};
    //the count of variables
    private final int variablesCount = 2;
    private MatlabSQP matlabSQP;

    private final double CONSTRAINT_THRESHOLD = 0.00101;

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

    public boolean isSpaceValid(Space space) {
        Double[] lowerLim = space.getLowerLim();
        Double[] upperLim = space.getUpperLim();
        if (lowerLim[1] - 100 * Math.sin(0.1 * lowerLim[0] - 0.5) <= CONSTRAINT_THRESHOLD) {
            return true;
        }
        if (lowerLim[1] - 100 * Math.sin(0.1 * upperLim[0] - 0.5) <= CONSTRAINT_THRESHOLD) {
            return true;
        }
        if (lowerLim[0] <= 15.7 && upperLim[0] >= 15.7) {
            return lowerLim[1] <= 100;
        }
        if (lowerLim[0] <= 78.75 && upperLim[0] >= 78.75) {
            return lowerLim[1] <= 100;
        }
        return false;
    }

    private boolean checkConstraint(double[] targetVal) {
        for (int i = 0; i < variablesCount; i++) {
            if (targetVal[i] > upperLim[i] || targetVal[i] < lowerLim[i]) return false;
        }
        double constraints1 = targetVal[1] - 100 * Math.sin(0.1 * targetVal[0] - 0.5);
        //the convergence condition threshold
        return constraints1 <= CONSTRAINT_THRESHOLD;
    }

    private double[] getClosetPointBySQP(double[] targetVal) {
        MWNumericArray target = new MWNumericArray(targetVal, MWClassID.DOUBLE);
        try {
            Object[] objects = this.matlabSQP.cloestPointOfDisciplinary1(1, target);
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
