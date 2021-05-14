package com.aim.project.sdsstp.hyperheuristics;


import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import com.aim.project.sdsstp.AIM_SDSSTP;
import com.aim.project.sdsstp.SolutionPrinter;
import com.aim.project.sdsstp.instance.SDSSTPLocation;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Warren G. Jackson
 * @since 26/03/2021
 * <p>
 * Creates an initial solution and finishes.
 * Can be used for testing your initialisation method.
 */
public class InitialisationTest_HH extends HyperHeuristic {

    public InitialisationTest_HH(long seed) {
        super(seed);
    }
    public int updateTime = 0;
    public int runTime = 0;
    @Override
    protected void solve(ProblemDomain problem) {
//        problem.initialiseSolution(0);
//		hasTimeExpired();

        problem.setMemorySize(3); // two parent(current) one candidate

        problem.initialiseSolution(0);
        problem.initialiseSolution(1);
        double current = problem.getFunctionValue(0);  // object function value of initial solution

        problem.setIntensityOfMutation(0.2);
        problem.setDepthOfSearch(0.2);

        int h = 1;
        boolean accept;

        int[] xos = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.CROSSOVER);
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i : xos) {
            set.add(i);
        }

        System.out.println("Iteration\tf(s)\tf(s')\tAccept");

        while (!hasTimeExpired()) {
            h = 6;
//            h = rng.nextInt(problem.getNumberOfHeuristics()); // randomly choose the next heuristics
            double candidate;   // object function value of candidate

            if (set.contains(h)) { // population-based
                problem.initialiseSolution(2); // ???????
                candidate = problem.applyHeuristic(h, 0, 2, 1);
            } else { // single-based
                candidate = problem.applyHeuristic(h, 0, 1);
//                System.out.println(candidate+" < "+current);
            }
            ++runTime;
            accept = candidate <= current;
            if (accept) {
                problem.copySolution(1, 0); // copy from candidate solution to current solution
                current = candidate;
                ++updateTime;
            }
        }
        System.out.println("Runtime = " + runTime + ", Accepted times = " + updateTime);

        int[] cities = ((AIM_SDSSTP) problem).getBestSolution().getSolutionRepresentation().getSolutionRepresentation();
        List<SDSSTPLocation> routeLocations = new ArrayList<>();

        for (int i = 0; i < ((AIM_SDSSTP) problem).getBestSolution().getNumberOfLandmarks(); i++) {
            routeLocations.add(((AIM_SDSSTP) problem).instance.getLocationForLandmark(cities[i]));
        }

        SDSSTPSolutionInterface oSolution = ((AIM_SDSSTP) problem).getBestSolution();
        SolutionPrinter.printSolution(((AIM_SDSSTP) problem).instance.getSolutionAsListOfLocations(oSolution));

    }

    @Override
    public String toString() {
        return "InitialisationTest_HH";
    }
}
