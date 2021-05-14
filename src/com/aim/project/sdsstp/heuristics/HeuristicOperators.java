package com.aim.project.sdsstp.heuristics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.aim.project.sdsstp.SDSSTPObjectiveFunction;
import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.interfaces.SolutionRepresentationInterface;
import com.aim.project.sdsstp.solution.SDSSTPSolution;
import com.aim.project.sdsstp.solution.SolutionRepresentation;

/**
 * @author Warren G. Jackson
 * @since 26/03/2021
 */
public class HeuristicOperators {

    /*
     *  PLEASE NOTE THAT USE OF THIS CLASS IS OPTIONAL BUT WE
     *  STRONGLY RECOMMEND THAT YOU IMPLEMENT ANY COMMON FUNCTIONALITY
     *  IN HERE TO HELP YOU WITH IMPLEMENTING THE HEURISTICS.
     *
     *  (HINT: It might be worthwhile to have a method that performs adjacent swaps in here :))
     */

    private static final boolean ENABLE_CHECKING = false;

    private ObjectiveFunctionInterface obj;

    public HeuristicOperators() {

    }

    public void setObjectiveFunction(ObjectiveFunctionInterface f) {

        this.obj = f;
    }


    protected int applyIOM(double intensityOfMutation) {
        return -1;
    }

    protected int applyDOS(double depthOfSearch) {
        return -1;
    }


    protected void performAdjacentSwap(SDSSTPSolutionInterface solution, int index) {
        int[] aiRep = solution.getSolutionRepresentation().getSolutionRepresentation();
        int length = solution.getNumberOfLandmarks();

        // update Objective Function Value by delta evaluation
        double preValue = solution.getObjectiveFunctionValue(); // get Objective Function previous Value
        double delta = ((SDSSTPObjectiveFunction) obj).deltaAdjacentSwap(aiRep, index, (index + 1) % length);
        double newValue = preValue + delta;

        // swap
//        System.out.println("Before: " + index + " - " + (index + 1) + ": " + Arrays.toString(aiRep));
        int tmp = aiRep[index];
        aiRep[index] = aiRep[(index + 1) % length];
        aiRep[(index + 1) % length] = tmp;
//        System.out.println("After: " + index + " - " + (index + 1) + ": " + Arrays.toString(aiRep));
        solution.setObjectiveFunctionValue(newValue);

        //test
//        int iValue = obj.getObjectiveFunctionValue(solution.getSolutionRepresentation()); // calculate Objective Function Value
//        if (newValue != iValue)
//            System.exit(-1);
    }

    protected void performInversion(SDSSTPSolutionInterface solution, int indexFrom, int indexTo) {
        // sort two indexes
        if (indexFrom > indexTo) {
            int tmp = indexTo;
            indexTo = indexFrom;
            indexFrom = tmp;
        }

        int[] aiRep = solution.getSolutionRepresentation().getSolutionRepresentation();

        // update Objective Function Value by delta evaluation
        double preValue = solution.getObjectiveFunctionValue(); // get Objective Function Value
        double delta = ((SDSSTPObjectiveFunction) obj).deltaInversion(aiRep, indexFrom, indexTo);
        double newValue = preValue + delta;
        solution.setObjectiveFunctionValue(newValue);

//        System.out.println("Before: " + indexFrom + " - " + indexTo + ": " + Arrays.toString(aiRep) + " = " + solution.getObjectiveFunctionValue());
        // inverse
//        for (int i = indexFrom; i <= indexTo; i++)
//            newRep[i] = aiRep[indexTo + indexFrom - i];
        while (indexFrom < indexTo) {
            int tmp = aiRep[indexFrom];
            aiRep[indexFrom] = aiRep[indexTo];
            aiRep[indexTo] = tmp;
            ++indexFrom;
            --indexTo;
        }
//        System.out.println("After:             : " + Arrays.toString(aiRep) + " = " + solution.getObjectiveFunctionValue() + "\n");

        //test
//        int iValue = obj.getObjectiveFunctionValue(solution.getSolutionRepresentation()); // calculate Objective Function Value
//        if (newValue != iValue)
//            System.exit(-1);
    }

    protected void performInsertion(SDSSTPSolutionInterface solution, int indexFrom, int indexTo) {
        int[] preRep = solution.getSolutionRepresentation().getSolutionRepresentation();
        List<Integer> list = Arrays.stream(preRep).boxed().collect(Collectors.toList());

//        System.out.println("Before: " + indexFrom + " - " + indexTo + ": " + Arrays.toString(list.stream().mapToInt(i -> i).toArray()));
        // insertion
        list.add(indexTo, list.remove(indexFrom));
//        System.out.println("After: " + indexFrom + " - " + indexTo + ": " + list + " = " + solution.getObjectiveFunctionValue() + "\n");

        // update Objective Function Value by delta evaluation
        double preValue = solution.getObjectiveFunctionValue(); // get Objective Function Value
        double delta = ((SDSSTPObjectiveFunction) obj).deltaReinsertion(preRep, indexFrom, indexTo);
        double newValue = preValue + delta;

        solution.getSolutionRepresentation().setSolutionRepresentation(list.stream().mapToInt(i -> i).toArray());
        solution.setObjectiveFunctionValue(newValue);
//        System.out.println("After: " + indexFrom + " - " + indexTo + ": " + Arrays.toString(newRep) + " = " + solution.getObjectiveFunctionValue() );

        //test
//        int iValue = obj.getObjectiveFunctionValue(solution.getSolutionRepresentation()); // calculate Objective Function Value
//        if (newValue != iValue)
//            System.exit(-1);
    }

}
