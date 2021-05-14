package com.aim.project.sdsstp.hyperheuristics;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.aim.project.sdsstp.AIM_SDSSTP;
import com.aim.project.sdsstp.SolutionPrinter;
import com.aim.project.sdsstp.instance.SDSSTPLocation;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import AbstractClasses.ProblemDomain.HeuristicType;

/**
 * @author Warren G. Jackson
 * @since 26/03/2021
 */
public class SR_IE_HH extends HyperHeuristic {

    public SR_IE_HH(long seed) {
        super(seed);
    }

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

        int[] xos = problem.getHeuristicsOfType(HeuristicType.CROSSOVER);
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i : xos) {
            set.add(i);
        }

        System.out.println("Iteration\tf(s)\tf(s')\tAccept");

        while (!hasTimeExpired()) {
            h = rng.nextInt(problem.getNumberOfHeuristics()); // randomly choose the next heuristics
//            h = 1;
            double candidate;   // object function value of candidate

            if (set.contains(h)) { // population-based
                problem.initialiseSolution(2);
                candidate = problem.applyHeuristic(h, 0, 2, 1);
            } else { // single-based
                candidate = problem.applyHeuristic(h, 0, 1);
            }

            accept = candidate <= current;
            if (accept) {
                problem.copySolution(1, 0); // copy from candidate solution to current solution
                current = candidate;
            }
        }

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
        return "SR_IE_HH";
    }
}
