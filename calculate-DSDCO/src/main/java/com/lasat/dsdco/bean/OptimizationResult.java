package com.lasat.dsdco.bean;

public class OptimizationResult {
    private double score;
    private double[] variables;

    public OptimizationResult(double score, double[] variables) {
        this.score = score;
        this.variables = variables;
    }

    public OptimizationResult() {
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double[] getVariables() {
        return variables;
    }

    public void setVariables(double[] variables) {
        this.variables = variables;
    }
}
