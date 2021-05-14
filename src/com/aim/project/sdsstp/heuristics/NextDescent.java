package com.aim.project.sdsstp.heuristics;


import java.util.Arrays;
import java.util.Random;

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
public class NextDescent extends HeuristicOperators implements HeuristicInterface {

    private final Random random;

    public NextDescent(Random random) {
        super();
        this.random = random;
    }

    @Override
    public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

        int times = applyDOS(depthOfSearch);

        // get the starting point
        int index = random.nextInt(solution.getNumberOfLandmarks());
        int numOfLoop = solution.getNumberOfLandmarks();
        double bestValue = solution.getObjectiveFunctionValue();

        // loop while the number of accepted improvements doesnt exceeds times
        // and A complete loop of the solution has not completed yet without finding any improvement.
        while (numOfLoop != 0 && times != 0) {

//            System.out.println("index = " + index + "\nBefore: " + Arrays.toString(solution.getSolutionRepresentation().getSolutionRepresentation()) + " = " + solution.getObjectiveFunctionValue());
            performAdjacentSwap(solution, index);
            double tmpValue = solution.getObjectiveFunctionValue();
//            System.out.println("After: " + Arrays.toString(solution.getSolutionRepresentation().getSolutionRepresentation()) + " = " + solution.getObjectiveFunctionValue());

            // if improved, keep the solution, decrement times of improvement, start a new loop at this point
            if (tmpValue < bestValue) {
                bestValue = tmpValue;
                --times; // decrement times of improvement
                numOfLoop = solution.getNumberOfLandmarks(); // set index from this improvement as a new loop again.
            }
            // otherwise, swap back
            else {
                performAdjacentSwap(solution, index);
                --numOfLoop; // decrement num of moves in this loop
            }

            // increment index
            index = (index + 1) % solution.getNumberOfLandmarks();
//            System.out.println("After acceptance: " + Arrays.toString(solution.getSolutionRepresentation().getSolutionRepresentation()) + " = " + solution.getObjectiveFunctionValue() + "\n");
        }

//        if(numOfLoop == 0)
//            System.out.println("end of loop");
//        if(times == 0)
//            System.out.println("max of times of improvement");
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
