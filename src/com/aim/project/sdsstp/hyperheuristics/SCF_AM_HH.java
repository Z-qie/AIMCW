package com.aim.project.sdsstp.hyperheuristics;

import AbstractClasses.HyperHeuristic;
import AbstractClasses.ProblemDomain;
import com.aim.project.sdsstp.AIM_SDSSTP;
import com.aim.project.sdsstp.SolutionPrinter;
import com.aim.project.sdsstp.Zy21586ChoiceFunction;
import com.aim.project.sdsstp.instance.SDSSTPLocation;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SCF_AM_HH extends HyperHeuristic {

    public SCF_AM_HH(long seed) {

        super(seed);
    }

    /**
     * This is a selection hyper-heuristic using the simple modified choice function (MCF)
     * with f=  f1(h_j) + f2(h_k, h_j) + f3(h_j)
     * heuristic selection method accepting all moves (AM).
     * <p>
     * PSEUDOCODE:
     * <p>
     * INPUT: problem_instance
     * hs <- { AdjacentSwap, Inversion, Reinsertion, OX, CX, NDCH, Davis's HC}
     * s <- initialiseSolution();
     * mcf <- initialiseNewChoiceFunctionMethod();
     * <p>
     * WHILE termination criterion is not met DO
     * <p>
     * h <- mcf.selectHeuristicToApply();
     * setHeuristicParameterSettings(iom, dos);
     * s' <- h(s);
     * <p>
     * scf.updateHeuristicData(h_i, t, t_delta, f(s), f(s'));
     * <p>
     * accept(); // AM
     * <p>
     * END_WHILE
     * <p>
     * return s_{best};
     */
    public void solve(ProblemDomain problem) {

        problem.setMemorySize(3); // two parent(current) one candidate

        problem.initialiseSolution(0);
        problem.initialiseSolution(1);
        double current = problem.getFunctionValue(0);  // object function value of initial solution

        Zy21586ChoiceFunction SMCF = new Zy21586ChoiceFunction(problem.getNumberOfHeuristics());

        problem.setIntensityOfMutation(0.2);
        problem.setDepthOfSearch(0.2);

        int h = 1;

        int[] xos = problem.getHeuristicsOfType(ProblemDomain.HeuristicType.CROSSOVER);
        HashSet<Integer> set = new HashSet<Integer>();
        for (int i : xos) {
            set.add(i);
        }

        System.out.println("Iteration\tf(s)\tf(s')\tAccept");
        while (!hasTimeExpired()) {

            // select the next heuristic by SMCF
            h = SMCF.selectHeuristicToApply();

            // application starts
            long timeApplied = System.nanoTime();
            double candidate;   // object function value of candidate
            if (set.contains(h)) { // population-based
                problem.initialiseSolution(2);
                candidate = problem.applyHeuristic(h, 0, 2, 1);
            } else { // single-based
                candidate = problem.applyHeuristic(h, 0, 1);
            }
            long timeTaken = System.nanoTime() - timeApplied; // get the time taken this heuristic is applied
            timeApplied = System.nanoTime(); // get the last time this heuristic is chosen
            // application ends

            SMCF.updateHeuristicData(h, timeApplied, timeTaken, current, candidate);
            // test

            // All accept!
            problem.copySolution(1, 0); // copy from candidate solution to current solution
            current = candidate;
        }

        int[] cities = ((AIM_SDSSTP) problem).getBestSolution().getSolutionRepresentation().getSolutionRepresentation();
        List<SDSSTPLocation> routeLocations = new ArrayList<>();

        for (int i = 0; i < ((AIM_SDSSTP) problem).getBestSolution().getNumberOfLandmarks(); i++) {
            routeLocations.add(((AIM_SDSSTP) problem).instance.getLocationForLandmark(cities[i]));
        }

        SDSSTPSolutionInterface oSolution = ((AIM_SDSSTP) problem).getBestSolution();
        SolutionPrinter.printSolution(((AIM_SDSSTP) problem).instance.getSolutionAsListOfLocations(oSolution));
    }

    public String toString() {

        return "SCF_AM_HH";
    }

}
