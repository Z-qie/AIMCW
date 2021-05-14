package com.aim.project.sdsstp;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Zy21586RL {

    private final int numOfHeuristics;

    // scores
    private final double[] scores;

    private final int iLowerBound = 1;


    public Zy21586RL(int numOfHeuristics) {
        this.numOfHeuristics = numOfHeuristics;
        scores = new double[numOfHeuristics];
        for (int i = 0; i < numOfHeuristics; i++) {
            scores[i] = iLowerBound;
        }
//        scores[5] = 10000;
    }

    public int selectBestHeuristic(Random random) {
        System.out.println(Arrays.toString(scores));

        // find the highest score
        double max = Arrays.stream(scores).max().orElseThrow();
        // get array of all indexes of max:
        int[] indexes = IntStream.range(0, numOfHeuristics).filter(i -> scores[i] == max).toArray();
        // choose an index at random
        int ans = indexes[random.ints(1, 0, indexes.length).sum()];
        System.out.println("heuristic" + ans);
        return ans;
    }

    public void updateHeuristicScore(int heuristic, double delta) {
//        if(delta>0)
//            ++scores[heuristic];
//
//        if(delta<0)
//            --scores[heuristic];
        scores[heuristic] += delta;
        if (scores[heuristic] < iLowerBound)
            scores[heuristic] = iLowerBound;
    }
}
