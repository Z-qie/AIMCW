package com.aim.project.sdsstp;


import com.aim.project.sdsstp.interfaces.HeuristicInterface;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Zy21586ChoiceFunction {

    private final int numOfHeuristics;

    private double phi;
    int lastHeuristic = 0;

    // for f1
    private final double[] f1s;
    // for f2
    private final double[][] valueImprovedForf2;
    private final long[][] timesMatrixForf2;
    // for f3
    private final long[] lastSelectedTimes;


    public Zy21586ChoiceFunction(int numOfHeuristics) {

        this.numOfHeuristics = numOfHeuristics;
        this.phi = 0.99;

        f1s = new double[numOfHeuristics];
        lastSelectedTimes = new long[numOfHeuristics];
        valueImprovedForf2 = new double[numOfHeuristics][numOfHeuristics];
        timesMatrixForf2 = new long[numOfHeuristics][numOfHeuristics];

        for (int i = 0; i < numOfHeuristics; i++) {
            for (int j = 0; j < numOfHeuristics; j++) {
                timesMatrixForf2[i][j] = -Long.MAX_VALUE;
            }
        }

        long startTime = System.nanoTime();
        for (int i = 0; i < numOfHeuristics; i++)
            lastSelectedTimes[i] = startTime;

    }

    /**
     * @param heuristic
     * @param timeApplied Current time in nanoseconds.
     * @param timeTaken   Time taken to apply <code>heuristic</code> in nanoseconds.
     * @param current     Objective value of the current solution, f(s_i).
     * @param candidate   Objective value of the candidate solution f(s'_i).
     */
    public void updateHeuristicData(int heuristic, long timeApplied, long timeTaken, double current, double candidate) {
        double Ih = current - candidate;

        // if improved
        if (Ih > 0) {
            phi = 0.99;
        }

        // if deteriorated
        else if (Ih < 0) {
            phi = Math.max(0.01, phi - 0.01);
        }
        // if no change, stay the same

        // update f1s
        f1s[heuristic] = phi * (Ih / (timeTaken / 1e9));
        // update for f3
        // The f3 is the time elapsed since the heuristic was last selected by the Choice Function
        lastSelectedTimes[heuristic] = timeApplied;
        // update for f2
        timesMatrixForf2[lastHeuristic][heuristic] = timeTaken;
        valueImprovedForf2[lastHeuristic][heuristic] = Ih;
        // update last heuristic
        lastHeuristic = heuristic;

        System.out.println("current = " + current + ", candidate = " + candidate);
        System.out.println("f1s = " + f1s[heuristic] + ", phi = " + phi);
        System.out.println();

    }

    public int selectHeuristicToApply() {
        double[] fs = new double[numOfHeuristics];

        long currentTime = System.nanoTime();
        for (int i = 0; i < numOfHeuristics; i++) {
            fs[i] = calculateScore(i, currentTime);
        }
        //test
        System.out.println("Select: fs = " + Arrays.toString(fs));
        // find the smallest f
        double max = Arrays.stream(fs).max().orElseThrow();

        // get array of all indexes of max:
        int[] indexes = IntStream.range(0, numOfHeuristics).filter(i -> fs[i] == max).toArray();
        int index = new Random().ints(1, 0, indexes.length).sum();
        //test
        System.out.println("max f value = " + max + ", Heuristic = " + indexes[index]);
        // choose an index at random
        return indexes[index];
    }

    public double calculateScore(int h, long currentTime) {
        double f1 = f1s[h];
        double f2 = phi * (valueImprovedForf2[lastHeuristic][h] / (timesMatrixForf2[lastHeuristic][h] / 1e9));
        double f3 = (1 - phi) * (currentTime - lastSelectedTimes[h]) / 1e9;
//        System.out.println(f3);
//        System.out.println(f1 + " " + f2 + " " + f3);
        return f1 + f2 + f3;
    }
}
