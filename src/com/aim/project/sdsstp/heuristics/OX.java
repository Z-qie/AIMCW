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
public class OX implements XOHeuristicInterface {

    private final Random random;

    private ObjectiveFunctionInterface f;

    public OX(Random random) {
        super();
        this.random = random;
    }

    @Override
    public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {
        return -1;
    }

    @Override
    public double apply(SDSSTPSolutionInterface p1, SDSSTPSolutionInterface p2, SDSSTPSolutionInterface c, double depthOfSearch, double intensityOfMutation) {
        int times = applyIOM(intensityOfMutation);

        int[] parent1 = p1.getSolutionRepresentation().getSolutionRepresentation().clone();
        int[] parent2 = p2.getSolutionRepresentation().getSolutionRepresentation().clone();
        int[] offspring1 = parent1.clone();
        int[] offspring2 = parent2.clone();

        for (int a = 0; a < times; a++) { // apply crossover to each successive round of offspring until the correct number of successive crossover operations have been performed.
//            System.out.println("Time = " + a);
            // randomly select two cut point:
            // Restriction:
            // 1. two cut points cannot at two bounds at the same time
            // 2. the case that one point at one bound and the other at the second last bound
            // above means in all: the size of elements in swapping section should greater than 1!
            int numberOfLandMarks = p1.getNumberOfLandmarks();
            int cutpointLeft = random.nextInt(numberOfLandMarks + 1); // [0, n]  (n+1) points
            int cutpointRight = random.nextInt(numberOfLandMarks + 1);
            // make sure two points are not equal and the size of persisted section is smaller than n-1
            while (cutpointLeft == cutpointRight || Math.abs(cutpointRight - cutpointLeft) >= numberOfLandMarks - 1) {
                cutpointRight = random.nextInt(numberOfLandMarks);
            }

            // swap if right < left
            if (cutpointRight < cutpointLeft) {
                int tmp = cutpointRight;
                cutpointRight = cutpointLeft;
                cutpointLeft = tmp;
            }
            // test section p1
//            System.out.print("cutpoint     = [");
//            for (int i = 0; i < numberOfLandMarks; i++) {
//                if (cutpointLeft == i || cutpointRight == i)
//                    System.out.print("| " + i);
//                else
//                    System.out.print(", " + i);
//            }
//            if (cutpointRight == numberOfLandMarks)
//                System.out.print("|");
//            System.out.print("] ");

            // get the swap section indexes
            List<Integer> swapSection = new ArrayList<Integer>();
            for (int i = cutpointRight; i < numberOfLandMarks; i++) {
                swapSection.add(i);
            }
            for (int i = 0; i < cutpointLeft; i++) {
                swapSection.add(i);
            }
            // test
//            System.out.print("Swap section of p1: ");
//            for (Integer i : swapSection)
//                System.out.print(i + " ");
//            System.out.println("cutpointLeft : " + cutpointLeft + " cutpointRight : " + cutpointRight);
//            System.out.println("Parent1      = " + Arrays.toString(parent1));
//            System.out.println("Parent2      = " + Arrays.toString(parent2));

            int offset1 = 0;
            int offset2 = 0;
            for (Integer index : swapSection) {
                for (int j = cutpointLeft; j < cutpointRight; j++) { // if the element exists in offspring's intact segment, skip to next.
                    if (parent2[(index + offset1) % numberOfLandMarks] == offspring1[j]) {
                        ++offset1;
                        j = cutpointLeft - 1;
                    }
                }
                for (int j = cutpointLeft; j < cutpointRight; j++) { // if the element exists in offspring's intact segment, skip to next.
                    if (parent1[(index + offset2) % numberOfLandMarks] == offspring2[j]) {
                        ++offset2;
                        j = cutpointLeft - 1;
                    }
                }
                offspring1[index] = parent2[(index + offset1) % numberOfLandMarks];
                offspring2[index] = parent1[(index + offset2) % numberOfLandMarks];
            }
            // test
//            System.out.println("Offspring1    = " + Arrays.toString(offspring1));
//            System.out.println("Offspring2    = " + Arrays.toString(offspring2));
            parent1 = offspring1.clone();
            parent2 = offspring2.clone();
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
