package com.lasat.fdsdco.service;

import com.lasat.dsdco.bean.OptimizationResult;
import com.lasat.dsdco.bean.Point;
import com.lasat.dsdco.bean.Space;
import com.lasat.dsdco.node.SpaceService;
import com.lasat.fdsdco.config.CalculationConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * implement of space service
 * @author MactavishCui
 */
@Service
public class SpaceServiceImpl implements SpaceService {

    @Autowired
    private SimpleSpaceStorageService simpleSpaceStorageService;
    @Autowired
    private SystemCalculatorService systemCalculatorService;
    private final int variableCount = CalculationConfig.getVariablesCount();

    @Override
    public void splitSpace(Space space, Point target, List<Point> closestPointList) {
        // get the maximum distance of the closest points
        double maxDistance = 0;
        for (Point point : closestPointList) {
            maxDistance = Math.max(maxDistance, getDistance(point, target));
        }
        double excludeDistance = maxDistance * Math.sqrt(variableCount) / variableCount;

        // space split
        Double[] currentVariables = target.getVariables();
        Double[] currentUpperLim = space != null ? space.getUpperLim() : new Double[0];
        Double[] currentLowerLim = space != null ? space.getLowerLim() : new Double[0];
        for (int i = 0; i < variableCount; i++) {
            // in order to prevent divergence of the spaces, set the bigger one for upper limit while the smaller one for lower limit
            double newLowerLim = Math.max(currentVariables[i] - excludeDistance, currentLowerLim[i]);
            double newUpperLim = Math.min(currentVariables[i] + excludeDistance, currentUpperLim[i]);

            // get the upper space and add it to the priority queue if it is suitable
            Double[] lowerLimOfUpperSpace = Arrays.copyOf(currentLowerLim, variableCount);
            lowerLimOfUpperSpace[i] = newUpperLim;
            Double[] upperLimOfUpperSpace = Arrays.copyOf(currentUpperLim, variableCount);
            checkAndAddSpaceByLimits(lowerLimOfUpperSpace, upperLimOfUpperSpace);

            // get the lower space and add it to the priority queue if it is suitable
            Double[] lowerLimOfLowerSpace = Arrays.copyOf(currentLowerLim, variableCount);
            Double[] upperLimOfLowerSpace = Arrays.copyOf(currentUpperLim, variableCount);
            upperLimOfLowerSpace[i] = newLowerLim;
            checkAndAddSpaceByLimits(lowerLimOfLowerSpace, upperLimOfLowerSpace);

            currentLowerLim[i] = newLowerLim;
            currentUpperLim[i] = newUpperLim;
        }
    }

    @Override
    public Space getBestSpace() {
        return simpleSpaceStorageService.getBestSpace();
    }

    @Override
    public int getSpaceCount() {
        return simpleSpaceStorageService.getSpaceCount();
    }

    /**
     * get the distance between 2 points
     *
     * @param point1 point1
     * @param point2 point2
     * @return the distance between 2 points
     */
    private double getDistance(Point point1, Point point2) {
        Double[] variables1 = point1.getVariables();
        Double[] variables2 = point2.getVariables();
        int length = variables1.length;

        double result = .0;

        for (int i = 0; i < length; i++) {
            double difference = variables1[i] - variables2[i];
            difference *= difference;
            result += difference;
        }

        return Math.sqrt(result);
    }

    private void checkAndAddSpaceByLimits(Double[] lowerLimit, Double[] upperLimit) {
        Space space = new Space();
        space.setLowerLim(lowerLimit);
        space.setUpperLim(upperLimit);
        space.setDisciplinaryName("test-system");
        if (isSpaceValid(space)) {
            OptimizationResult bestPointOfUpperSpace = systemCalculatorService.getBestPoint(space);
            space.setMinTargetFunValue(bestPointOfUpperSpace.getScore());
            Double[] bestVariables = new Double[variableCount];
            for (int i = 0; i < bestVariables.length; i++) {
                bestVariables[i] = bestPointOfUpperSpace.getVariables()[i];
            }
            space.setBestVariables(bestVariables);
            simpleSpaceStorageService.addSpace(space);
        }
    }

    private boolean isSpaceValid(Space space) {
        Double[] upperLim = space.getUpperLim();
        Double[] lowerLim = space.getLowerLim();
        for (int i = 0; i < variableCount; i++) {
            if (upperLim[i] - lowerLim[i] <= 0) return false;
        }
        return true;
    }
}
