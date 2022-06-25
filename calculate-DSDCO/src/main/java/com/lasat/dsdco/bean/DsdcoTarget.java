package com.lasat.dsdco.bean;

import java.util.Arrays;

/**
 * the message send to each disciplinary consumer by json
 */
public class DsdcoTarget {

    private Long taskId;
    private String disciplinaryName;
    private Integer iteratorCount;
    private Double[] variables;

    public DsdcoTarget() {
    }

    public DsdcoTarget(Long taskId, String disciplinaryName, Integer iteratorCount, Double[] variables) {
        this.taskId = taskId;
        this.disciplinaryName = disciplinaryName;
        this.iteratorCount = iteratorCount;
        this.variables = variables;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getDisciplinaryName() {
        return disciplinaryName;
    }

    public void setDisciplinaryName(String disciplinaryName) {
        this.disciplinaryName = disciplinaryName;
    }

    public Integer getIteratorCount() {
        return iteratorCount;
    }

    public void setIteratorCount(Integer iteratorCount) {
        this.iteratorCount = iteratorCount;
    }

    public Double[] getVariables() {
        return variables;
    }

    public void setVariables(Double[] variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return "DsdcoTarget{" +
                "taskId=" + taskId +
                ", disciplinaryName='" + disciplinaryName + '\'' +
                ", iteratorCount=" + iteratorCount +
                ", variables=" + Arrays.toString(variables) +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DsdcoTarget) {
            DsdcoTarget other = (DsdcoTarget) obj;
            Double[] otherVar = other.getVariables();
            for (int i = 0; i < otherVar.length; i++) {
                // ensure the accuracy change during serialization will not affect the judgement
                if (Math.abs(otherVar[i] - variables[i]) > 0.001) return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
