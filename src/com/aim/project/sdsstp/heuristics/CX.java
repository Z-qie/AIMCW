package com.aim.project.sdsstp.heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.interfaces.XOHeuristicInterface;

/**
 * @author Warren G. Jackson
 * @since 26/03/2021
 * <p>
 * Methods needing to be implemented:
 * - public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation)
 * - public double apply(SDSSTPSolutionInterface p1, SDSSTPSolutionInterface p2, SDSSTPSolutionInterface c, double depthOfSearch, double intensityOfMutation)
 * - public boolean isCrossover()
 * - public boolean usesIntensityOfMutation()
 * - public boolean usesDepthOfSearch()
 */
public class CX implements XOHeuristicInterface {

    private final Random random;

    private ObjectiveFunctionInterface f;

    public CX(Random random) {

        this.random = random;
    }

    @Override
    public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {
        return -1;
    }

    @Override
    public double apply(SDSSTPSolutionInterface p1, SDSSTPSolutionInterface p2, SDSSTPSolutionInterface c, double depthOfSearch, double intensityOfMutation) {
        int times = applyIOM(intensityOfMutation);
        int numberOfLandMarks = p1.getNumberOfLandmarks();

        int[] parent1 = p1.getSolutionRepresentation().getSolutionRepresentation().clone();
        int[] parent2 = p2.getSolutionRepresentation().getSolutionRepresentation().clone();
        int[] offspring1 = parent1.clone();
        int[] offspring2 = parent2.clone();
        for (int a = 0; a < times; a++) { // apply crossover to each successive round of offspring until the correct number of successive crossover operations have been performed.
            int index = random.nextInt(numberOfLandMarks); // randomly choose a starting point
            //test
//            System.out.println("Time = " + a);
//            System.out.println("Starting point : " + index);
//            System.out.println("Parent1      = " + Arrays.toString(parent1));
//            System.out.println("Parent2      = " + Arrays.toString(parent2));

            List<Integer> indices = new ArrayList<>(numberOfLandMarks);
            indices.add(index);

            int item = parent2[index];
            for (int i = 0; i < numberOfLandMarks; i++) {
                if (item == parent1[i]) {
                    index = i;
                    break;
                }
            }

            while (index != indices.get(0)) {
                // add that index to the cycle indices
                indices.add(index);
                // get the item in the second parent at that index
                item = parent2[index];
                // get the index of that item in the first parent
                for (int i = 0; i < numberOfLandMarks; i++) {
                    if (item == parent1[i]) {
                        index = i;
                        break;
                    }
                }
            }
            // swap
            for (int i : indices) {
                int tmp = offspring1[i];
                offspring1[i] = offspring2[i];
                offspring2[i] = tmp;
            }
            parent1 = offspring1.clone();
            parent2 = offspring2.clone();
            // test
//            System.out.println("offspring1   = " + Arrays.toString(offspring1));
//            System.out.println("offspring2   = " + Arrays.toString(offspring2));
        }

        // random choose an offspring
        if (random.nextInt(2) == 0) {
            c.getSolutionRepresentation().setSolutionRepresentation(offspring1);
        } else {
            c.getSolutionRepresentation().setSolutionRepresentation(offspring2);
        }

        // update object function value
        int iValue = f.getObjectiveFunctionValue(c.getSolutionRepresentation()); // calculate Objective Function Value
        c.setObjectiveFunctionValue(iValue);

//        System.out.println("selected    = " + Arrays.toString(c.getSolutionRepresentation().getSolutionRepresentation()) + "\nOffspring value = " + iValue + "\n");
        return iValue;
    }

    @Override
    public boolean isCrossover() {
        return true;
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
    public void setObjectiveFunction(ObjectiveFunctionInterface f) {
        this.f = f;
    }

    protected int applyIOM(double intensityOfMutation) {
        if (0.0 <= intensityOfMutation && intensityOfMutation < 0.2)
            return 1;
        if (0.2 <= intensityOfMutation && intensityOfMutation < 0.4)
            return 2;
        if (0.4 <= intensityOfMutation && intensityOfMutation < 0.6)
            return 3;
        if (0.6 <= intensityOfMutation && intensityOfMutation < 0.8)
            return 4;
        if (0.8 <= intensityOfMutation && intensityOfMutation < 1.0)
            return 5;
        if (1.0 == intensityOfMutation)
            return 6;
        return -1;
    }

}
