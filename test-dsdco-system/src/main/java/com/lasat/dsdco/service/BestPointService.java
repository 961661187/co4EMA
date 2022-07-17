package com.lasat.dsdco.service;

import com.lasat.dsdco.bean.OptimizationResult;
import org.springframework.stereotype.Service;

@Service
public class BestPointService {
    public OptimizationResult getResultInRegion(double[] upperLim, double[] lowerLim) {
        return new OptimizationResult(-lowerLim[0], lowerLim);
    }
}
