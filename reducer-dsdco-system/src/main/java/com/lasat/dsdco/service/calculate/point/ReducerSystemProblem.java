package com.lasat.dsdco.service.calculate.point;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;
import com.lasat.dsdco.util.PointUtil;

public class ReducerSystemProblem extends AbstractDoubleProblem {

    public ReducerSystemProblem(double[] lowerLimit, double[] upperLimit) {
        this(7, lowerLimit, upperLimit);
    }

    public ReducerSystemProblem(Integer numberOfVariables, double[] lowerLimit, double[] upperLimit) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(1);
        setName("Reducer");

        List<Double> lowerLimitList = new ArrayList<>(getNumberOfVariables()) ;
        List<Double> upperLimitLIst = new ArrayList<>(getNumberOfVariables()) ;

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimitList.add(lowerLimit[i]);
            upperLimitLIst.add(upperLimit[i]);
        }

        setLowerLimit(lowerLimitList);
        setUpperLimit(upperLimitLIst);
    }

    /** Evaluate() method */
    public void evaluate(DoubleSolution solution) {
        double[] variables = new double[getNumberOfVariables()];

        for (int i = 0; i < variables.length; i++) {
            variables[i] = solution.getVariableValue(i);
        }

        solution.setObjective(0, PointUtil.getScore(variables));
    }
}
