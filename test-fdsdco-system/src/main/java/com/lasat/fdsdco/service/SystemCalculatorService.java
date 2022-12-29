package com.lasat.fdsdco.service;

import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.dsdco.bean.Space;
import com.lasat.dsdco.node.SystemCalculator;
import org.springframework.stereotype.Service;

@Service
public class SystemCalculatorService implements SystemCalculator {
    @Override
    public OptimizationResult getBestPoint(Space space) {
        Double[] lowerLim = space.getLowerLim();
        double[] lowerLimUnboxed = new double[lowerLim.length];
        for (int i = 0; i < lowerLim.length; i++) {
            lowerLimUnboxed[i] = lowerLim[i];
        }
        return new OptimizationResult(lowerLimUnboxed[0], lowerLimUnboxed);
    }
}
