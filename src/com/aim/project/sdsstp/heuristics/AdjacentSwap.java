package com.aim.project.sdsstp.heuristics;

import java.util.Arrays;
import java.util.Random;

import com.aim.project.sdsstp.interfaces.HeuristicInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.interfaces.SolutionRepresentationInterface;
import com.aim.project.sdsstp.solution.SDSSTPSolution;
import com.aim.project.sdsstp.solution.SolutionRepresentation;

/**
 * @author Warren G. Jackson
 * @since 26/03/2021
 * <p>
 * Methods needing to be implemented:
 * - public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation)
 * - public boolean isCrossover()
 * - public boolean usesIntensityOfMutation()
 * - public boolean usesDepthOfSearch()
 */
public class AdjacentSwap extends HeuristicOperators implements HeuristicInterface {

    private final Random random;

    public AdjacentSwap(Random random) {
        super();
        this.random = random;
    }

    @Override
    public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

        int times = applyIOM(intensityOfMutation);
        for (int i = 0; i < times; i++) {
            // get the index of location to be swapped
            int index = random.nextInt(solution.getNumberOfLandmarks());
            performAdjacentSwap(solution, index);
        }

        // test
//        System.out.println(Arrays.toString(solution.getSolutionRepresentation().getSolutionRepresentation()) + " = " + solution.getObjectiveFunctionValue());
        return solution.getObjectiveFunctionValue();
    }

    @Override
    public boolean isCrossover() {
        return false;
    }

    @Override
    public boolean usesIntensityOfMutation() {
        return true;
    }

    @Override
    public boolean usesDepthOfSearch() {
        return false;
    }

    @Override
    protected int applyIOM(double intensityOfMutation) {
        if (0.0 <= intensityOfMutation && intensityOfMutation < 0.2)
            return 1;
        if (0.2 <= intensityOfMutation && intensityOfMutation < 0.4)
            return 2;
        if (0.4 <= intensityOfMutation && intensityOfMutation < 0.6)
            return 4;
        if (0.6 <= intensityOfMutation && intensityOfMutation < 0.8)
            return 8;
        if (0.8 <= intensityOfMutation && intensityOfMutation < 1.0)
            return 16;
        if (1.0 == intensityOfMutation)
            return 32;
        return -1;
    }

}

