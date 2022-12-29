package com.lasat.dsdco.util;

import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.dsdco.bean.Point;
import com.lasat.dsdco.bean.Space;

public class ConvertUtil {
    public static Point optimizationResult2Point(OptimizationResult optimizationResult, String disciplinaryName, Long taskId, Integer iterationCount) {
        Point point = new Point();
        point.setDisciplinaryName(disciplinaryName);
        point.setIteratorCount(iterationCount);
        point.setTaskId(taskId);
        double[] variables = optimizationResult.getVariables();
        Double[] pointVariables = new Double[variables.length];
        for (int i = 0; i < variables.length; i++) {
            pointVariables[i] = variables[i];
        }
        point.setVariables(pointVariables);
        point.setScore(optimizationResult.getScore());
        return point;
    }

    public static Point getBestPointFromSpace(Space space, Integer pointCount) {
        Point point = new Point();
        point.setVariables(space.getBestVariables());
        point.setTaskId(space.getTaskId());
        point.setIteratorCount(pointCount);
        point.setDisciplinaryName(space.getDisciplinaryName());
        point.setScore(space.getMinTargetFunValue());
        return point;
    }
}
