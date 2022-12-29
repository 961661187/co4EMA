package com.lasat.dsdco.util;

import com.lasat.dsdco.bean.Point;

import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    public static Map<String, Object> convertTargetToMap(Point target) {
        Map<String, Object> result = new HashMap<>();

        Long taskId = target.getTaskId() == null ? -1 : target.getTaskId();
        String disciplinaryNam = target.getDisciplinaryName() == null ? "" : target.getDisciplinaryName();
        Integer iteratorCount = target.getIteratorCount() == null ? -1 : target.getIteratorCount();
        Double[] variables = target.getVariables() == null ? new Double[0] : target.getVariables();

        result.put("taskId", taskId);
        result.put("disciplinaryName", disciplinaryNam);
        result.put("iteratorCount", iteratorCount);
        result.put("variables", variables);

        return result;
    }
}
