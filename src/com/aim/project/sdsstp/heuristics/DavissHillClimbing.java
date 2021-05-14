package com.aim.project.sdsstp.heuristics;

import java.util.*;

import com.aim.project.sdsstp.interfaces.HeuristicInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;


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
public class DavissHillClimbing extends HeuristicOperators implements HeuristicInterface {

    private final Random random;

    public DavissHillClimbing(Random random) {
        super();
        this.random = random;
    }

    @Override
    public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

        int times = applyDOS(depthOfSearch);

        // get random permutation
        List<Integer> RP = new ArrayList<Integer>();
        for (int i = 0; i < solution.getNumberOfLandmarks(); i++)
//            RP.add(solution.getSolutionRepresentation().getSolutionRepresentation()[i]);
                RP.add(i);
        Collections.shuffle(RP, random);
//        Collections.shuffle(RP, new Random(System.currentTimeMillis()));
//        System.out.println("Permutation: " + RP + "\n");

        double bestValue = solution.getObjectiveFunctionValue();

        for (int i = 0; i < times; i++) { // Davis’s Hill Climbing is performed by times according DOS
            for (int index = 0; index < solution.getNumberOfLandmarks(); index++) {
//                System.out.println("index = " + index + ": " + RP.get(index) + "\nBefore swap: " + Arrays.toString(solution.getSolutionRepresentation().getSolutionRepresentation()) + " = " + solution.getObjectiveFunctionValue());
                performAdjacentSwap(solution, RP.get(index));
                double tmpValue = solution.getObjectiveFunctionValue();
//                System.out.println("After swap: " + Arrays.toString(solution.getSolutionRepresentation().getSolutionRepresentation()) + " = " + solution.getObjectiveFunctionValue());

                // if improved, keep the solution
                if (tmpValue <= bestValue) //persist any swap which results in an improving or equal quality solution.
                    bestValue = tmpValue;
                else // otherwise, swap back
                    performAdjacentSwap(solution, RP.get(index));
//                System.out.println("After acceptance method: " + Arrays.toString(solution.getSolutionRepresentation().getSolutionRepresentation()) + " = " + solution.getObjectiveFunctionValue() + "\n");
            }
        }
        return solution.getObjectiveFunctionValue();
    }

    @Override
    public boolean isCrossover() {
        return false;
    }

    @Override
    public boolean usesIntensityOfMutation() {
        return false;
    }

    @Override
    public boolean usesDepthOfSearch() {
        return true;
    }


    @Override
    protected int applyDOS(double depthOfSearch) {
        if (0.0 <= depthOfSearch && depthOfSearch < 0.2)
            return 1;
        if (0.2 <= depthOfSearch && depthOfSearch < 0.4)
            return 2;
        if (0.4 <= depthOfSearch && depthOfSearch < 0.6)
            return 3;
        if (0.6 <= depthOfSearch && depthOfSearch < 0.8)
            return 4;
        if (0.8 <= depthOfSearch && depthOfSearch < 1.0)
            return 5;
        if (1.0 == depthOfSearch)
            return 6;
        return -1;
    }
}
