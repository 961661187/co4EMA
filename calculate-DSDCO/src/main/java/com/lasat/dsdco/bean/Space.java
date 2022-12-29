package com.lasat.dsdco.bean;

import java.io.Serializable;
import java.util.Arrays;

public class Space implements Serializable {

    private Double[] upperLim;
    private Double[] lowerLim;
    private Double minTargetFunValue;
    private Double[] bestVariables;
    private Boolean spaceValid;
    private String disciplinaryName;
    private Long taskId;
    private Integer iteratorCount;

    public Space() {
    }

    public Space(Double[] upperLim, Double[] lowerLim) {
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

    public Boolean getValid() {
        return spaceValid;
    }

    public void setValid(Boolean valid) {
        spaceValid = valid;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getIteratorCount() {
        return iteratorCount;
    }

    public void setIteratorCount(Integer iteratorCount) {
        this.iteratorCount = iteratorCount;
    }

    public String getDisciplinaryName() {
        return disciplinaryName;
    }

    public void setDisciplinaryName(String disciplinaryName) {
        this.disciplinaryName = disciplinaryName;
    }

    @Override
    public String toString() {
        return "Space{" +
                "upperLim=" + Arrays.toString(upperLim) +
                ", lowerLim=" + Arrays.toString(lowerLim) +
                ", minTargetFunValue=" + minTargetFunValue +
                ", bestVariables=" + Arrays.toString(bestVariables) +
                ", isValid=" + spaceValid +
                ", disciplinaryName='" + disciplinaryName + '\'' +
                ", taskId=" + taskId +
                ", iteratorCount=" + iteratorCount +
                '}';
    }
}
