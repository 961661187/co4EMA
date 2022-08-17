package com.lasat.dsdco.service.calculate.point;

import com.lasat.dsdco.bean.OptimizationResult;
import org.springframework.stereotype.Service;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.SimpleRandomMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;

import java.util.List;

@Service
public class GaBestPointService {

    public OptimizationResult getResultInRegion(double[] upperLim, double[] lowerLim) {
        Problem<DoubleSolution> problem;
        Algorithm<DoubleSolution> algorithm;
        CrossoverOperator<DoubleSolution> crossover;
        MutationOperator<DoubleSolution> mutation;
        SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

        // define the optimization problem
        problem = new ReducerSystemProblem(lowerLim, upperLim);

        // set the crossover operator
        double crossoverProbability = 0.9;
        double crossoverDistributionIndex = 20.0;
        crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

        // set the mutation operator
        double mutationProbability = 1.0 / problem.getNumberOfVariables();
        mutation = new SimpleRandomMutation(mutationProbability);

        // set the selector operator
        selection = new BinaryTournamentSelection<DoubleSolution>(
                new RankingAndCrowdingDistanceComparator<DoubleSolution>());

        // register the operators to algorithm
        algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
                .setSelectionOperator(selection)
                .setMaxEvaluations(10000)
                .setPopulationSize(1000)
                .build();

        // algorithm execute
        new AlgorithmRunner.Executor(algorithm).execute();

        // get the result
        DoubleSolution result = algorithm.getResult();
        double[] resultVariables = new double[result.getNumberOfVariables()];
        for (int i = 0; i < result.getNumberOfVariables(); i++) {
            resultVariables[i] = result.getVariableValue(i);
        }
        return new OptimizationResult(-result.getObjective(0), resultVariables);
    }
}
