package com.lasat.dsdco.bean;

import java.util.Arrays;

/**
 * the message send to each disciplinary consumer by json
 */
public class Point {

    private Long taskId;
    private String disciplinaryName;
    private Integer iteratorCount;
    private Double[] variables;
    private Double score;

    public Point() {
    }

    public Point(Long taskId, String disciplinaryName, Integer iteratorCount, Double[] variables) {
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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
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
        if (obj instanceof Point) {
            Point other = (Point) obj;
            Double[] otherVar = other.getVariables();
            for (int i = 0; i < otherVar.length; i++) {
                if (!otherVar[i].equals(variables[i])) return false;
            }
            return true;
        } else {
            return false;
        }
    }
}
