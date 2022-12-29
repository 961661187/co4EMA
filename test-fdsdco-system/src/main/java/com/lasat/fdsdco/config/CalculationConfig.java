package com.lasat.fdsdco.config;

public class CalculationConfig {
    private static final int VARIABLES_COUNT = 2;
    private static final int DISCIPLINARY_COUNT = 2;
    private static final int maxIteratorCount = 10000;

    public static int getVariablesCount() {
        return VARIABLES_COUNT;
    }

    public static int getDisciplinaryCount() {
        return DISCIPLINARY_COUNT;
    }

    public static int getMaxIteratorCount() {
        return maxIteratorCount;
    }
}
