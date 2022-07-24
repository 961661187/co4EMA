package com.lasat.dsdco.util;

public class PointUtil {
    public static double getScore(double[] x) {
        double result = .0;
        result += 0.7854 * x[0] * x[1] * x[1] * (3.3333 * x[2] * x[2] + 14.9334 * x[2] - 43.0934);
        result -= 1.508 * x[0] * (x[5] * x[5] + x[6] * x[6]);
        result += 7.477 * (Math.pow(x[5], 3) + Math.pow(x[6], 3));
        result += 0.7854 * (x[3] * x[5] * x[5] + x[4] * x[6] * x[6]);
        return result;
    }
}
