
package com.aim.project.sdsstp.hyperheuristics;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import com.aim.project.sdsstp.AIM_SDSSTP;
import com.aim.project.sdsstp.SolutionPrinter;
import com.aim.project.sdsstp.Zy21586RL;
import com.aim.project.sdsstp.instance.SDSSTPLocation;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RL_HH extends HyperHeuristic {

    public RL_HH(long seed) {
        super(seed);
    }

    public int updateTime = 0;
    public int runTime = 0;

    public void solve(ProblemDomain problem) {
        problem.setMemorySize(3); // two parent(current) one candidate
        problem.initialiseSolution(0);
        problem.initialiseSolution(1);
        double current = problem.getFunctionValue(0);  // object function value of initial solution

        problem.setIntensityOfMutation(0.2);
        problem.setDepthOfSearch(0.2);

        int h = 1;

        int[] xos = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.CROSSOVER);
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i : xos) {
            set.add(i);
        }

        Zy21586RL RL = new Zy21586RL(problem.getNumberOfHeuristics());
        System.out.println("Iteration\tf(s)\tf(s')\tAccept");
        while (!hasTimeExpired()) {

            h = RL.selectBestHeuristic(super.rng);

            double candidate;   // object function value of candidate
            if (set.contains(h)) { // population-based
                problem.initialiseSolution(2);
                candidate = problem.applyHeuristic(h, 0, 2, 1);
            } else { // single-based
                candidate = problem.applyHeuristic(h, 0, 1);
            }

            RL.updateHeuristicScore(h, current - candidate);
//            // All accept!
//            problem.copySolution(1, 0); // copy from candidate solution to current solution
//            current = candidate;

            ++runTime;
            if (candidate < current) {
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

    public String toString() {
        return "RL_HH";
    }

}
