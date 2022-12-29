package com.lasat.dsdco.node;

import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.dsdco.bean.Point;
import com.lasat.dsdco.bean.Space;

/**
 * The system calculator
 * @author MactavishCui
 */
public interface SystemCalculator {
    /**
     * Get the closest point in given region
     * @param space space
     * @return the best point in given region
     */
    OptimizationResult getBestPoint(Space space);
}
