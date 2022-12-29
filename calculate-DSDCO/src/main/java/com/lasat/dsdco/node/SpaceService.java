package com.lasat.dsdco.node;

import com.lasat.dsdco.bean.Point;
import com.lasat.dsdco.bean.Space;

import java.util.List;

/**
 * The interface for space service
 */
public interface SpaceService {

    /**
     * Split the given space by the closest points of disciplinary
     * @param space space to be split
     * @param target the target point
     * @param closestPointList the closest points
     */
    void splitSpace(Space space, Point target, List<Point> closestPointList);

    /**
     * get the best space
     * @return the best space
     */
    Space getBestSpace();

    /**
     * get the space count
     * @return count of the space
     */
    int getSpaceCount();
}
