package com.lasat.dsdco.bean;

import java.io.Serializable;
import java.util.Arrays;

public class DsdcoRegion implements Serializable {

    private Double[] upperLim;
    private Double[] lowerLim;
    private Double minTargetFunValue;
    private Double[] bestVariables;

    public DsdcoRegion() {
    }

    public DsdcoRegion(Double[] upperLim, Double[] lowerLim) {
        this.upperLim = upperLim;
        this.lowerLim = lowerLim;
    }

    public Double[] getUpperLim() {
        return upperLim;
    }

    public void setUpperLim(Double[] upperLim) {
        this.upperLim = upperLim;
    }

    public Double[] getLowerLim() {
        return lowerLim;
    }

    public void setLowerLim(Double[] lowerLim) {
        this.lowerLim = lowerLim;
    }

    public Double getMinTargetFunValue() {
        return minTargetFunValue;
    }

    public void setMinTargetFunValue(Double minTargetFunValue) {
        this.minTargetFunValue = minTargetFunValue;
    }

    public Double[] getBestVariables() {
        return bestVariables;
    }

    public void setBestVariables(Double[] bestVariables) {
        this.bestVariables = bestVariables;
    }

    @Override
    public String toString() {
        return "DsdcoRegion{" +
                "upperLim=" + Arrays.toString(upperLim) +
                ", lowerLim=" + Arrays.toString(lowerLim) +
                ", minTargetFunValue=" + minTargetFunValue +
                ", bestVariables=" + Arrays.toString(bestVariables) +
                '}';
    }
}
