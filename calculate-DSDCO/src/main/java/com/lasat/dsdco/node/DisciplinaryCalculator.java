package com.lasat.dsdco.node;

import com.lasat.dsdco.bean.Space;
import com.lasat.dsdco.bean.Point;

/**
 * Disciplinary calculator
 * @author MactavishCui
 */
public interface DisciplinaryCalculator {
    /**
     * if there is at least one valid point in given space
     * @param space space to be checked
     * @return true if there is at least one valid point
     */
    boolean isSpaceValid(Space space);

    /**
     * get the closest point to a target point
     * @param target the target point
     * @return the closest point
     */
    Point getClosestPoint(Point target);
}
