package com.aim.project.sdsstp;

import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SolutionRepresentationInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SDSSTPObjectiveFunction implements ObjectiveFunctionInterface {

    private final int[][] aiTimeDistanceMatrix;

    private final int[] aiTimeDistancesFromTourOffice;

    private final int[] aiTimeDistancesToTourOffice;

    private final int[] aiVisitingDurations;

    public SDSSTPObjectiveFunction(int[][] aiTimeDistanceMatrix, int[] aiTimeDistancesFromTourOffice,
                                   int[] aiTimeDistancesToTourOffice, int[] aiVisitingDurations) {

        this.aiTimeDistanceMatrix = aiTimeDistanceMatrix;
        this.aiTimeDistancesFromTourOffice = aiTimeDistancesFromTourOffice;
        this.aiTimeDistancesToTourOffice = aiTimeDistancesToTourOffice;
        this.aiVisitingDurations = aiVisitingDurations;
    }

    @Override
    public int getObjectiveFunctionValue(SolutionRepresentationInterface solution) {
        // standard evaluation
        int[] aiRep = solution.getSolutionRepresentation();
        int iLength = solution.getNumberOfLandmarks();
        // from and to office
        int iValue = aiTimeDistancesFromTourOffice[aiRep[0]] + aiTimeDistancesToTourOffice[aiRep[iLength - 1]];
        // btw landmarks
        for (int i = 0; i <= iLength - 2; i++)
            iValue += aiTimeDistanceMatrix[aiRep[i]][aiRep[i + 1]];
        // visit durations
        for (int i = 0; i <= iLength - 1; i++)
            iValue += aiVisitingDurations[i];

        return iValue;
    }

    // delta evaluation for AdjacentSwap
    public double deltaAdjacentSwap(int[] aiRep, int index1, int index2) {
        double prePartValue;
        double newPartValue;
        int location1 = aiRep[index1];
        int location2 = aiRep[index2];
        int length = aiRep.length;
        // when index == 0
        if (index1 == 0) {
            prePartValue = aiTimeDistancesFromTourOffice[location1] + aiTimeDistanceMatrix[location1][location2] + aiTimeDistanceMatrix[location2][aiRep[2]];
            newPartValue = aiTimeDistancesFromTourOffice[location2] + aiTimeDistanceMatrix[location2][location1] + aiTimeDistanceMatrix[location1][aiRep[2]];
        }
        // when index = n - 2
        else if (index1 == length - 2) {
            prePartValue = aiTimeDistanceMatrix[aiRep[index1 - 1]][location1] + aiTimeDistanceMatrix[location1][location2] + aiTimeDistancesToTourOffice[location2];
            newPartValue = aiTimeDistanceMatrix[aiRep[index1 - 1]][location2] + aiTimeDistanceMatrix[location2][location1] + aiTimeDistancesToTourOffice[location1];
        }
        // when index = n -1
        else if (index1 == length - 1) {
            prePartValue = aiTimeDistancesFromTourOffice[location2] + aiTimeDistanceMatrix[location2][aiRep[1]] + aiTimeDistanceMatrix[aiRep[index1 - 1]][location1] + aiTimeDistancesToTourOffice[location1];
            newPartValue = aiTimeDistancesFromTourOffice[location1] + aiTimeDistanceMatrix[location1][aiRep[1]] + aiTimeDistanceMatrix[aiRep[index1 - 1]][location2] + aiTimeDistancesToTourOffice[location2];
        }
        // when 0 < index < n - 2
        else {
            prePartValue = aiTimeDistanceMatrix[aiRep[index1 - 1]][location1] + aiTimeDistanceMatrix[location1][location2] + aiTimeDistanceMatrix[location2][aiRep[index2 + 1]];
            newPartValue = aiTimeDistanceMatrix[aiRep[index1 - 1]][location2] + aiTimeDistanceMatrix[location2][location1] + aiTimeDistanceMatrix[location1][aiRep[index2 + 1]];
        }

        return newPartValue - prePartValue;
    }

    // delta evaluation for Inversion
    public double deltaInversion(int[] aiRep, int indexFrom, int indexTo) {
        double prePartValue = 0.0;
        double newPartValue = 0.0;
        int length = aiRep.length;

        for (int i = indexFrom; i < indexTo; i++) {
            prePartValue += aiTimeDistanceMatrix[aiRep[i]][aiRep[i + 1]];
            newPartValue += aiTimeDistanceMatrix[aiRep[i + 1]][aiRep[i]];
        }

        if (indexFrom == 0) {
            prePartValue += aiTimeDistancesFromTourOffice[aiRep[indexFrom]];
            newPartValue += aiTimeDistancesFromTourOffice[aiRep[indexTo]];
        } else {
            prePartValue += aiTimeDistanceMatrix[aiRep[indexFrom - 1]][aiRep[indexFrom]];
            newPartValue += aiTimeDistanceMatrix[aiRep[indexFrom - 1]][aiRep[indexTo]];
        }

        if (indexTo == length - 1) {
            prePartValue += aiTimeDistancesToTourOffice[aiRep[indexTo]];
            newPartValue += aiTimeDistancesToTourOffice[aiRep[indexFrom]];
        } else {
            prePartValue += aiTimeDistanceMatrix[aiRep[indexTo]][aiRep[indexTo + 1]];
            newPartValue += aiTimeDistanceMatrix[aiRep[indexFrom]][aiRep[indexTo + 1]];
        }
        return newPartValue - prePartValue;
    }

    // delta evaluation for Reinsertion
    public double deltaReinsertion(int[] preRep, int indexFrom, int indexTo) {
        double prePartValue = 0.0;
        double newPartValue = 0.0;
        int length = preRep.length;

        if (indexFrom == 0) {
            if (indexTo == length - 1) {
                prePartValue += aiTimeDistancesFromTourOffice[preRep[0]] + aiTimeDistanceMatrix[preRep[0]][preRep[1]] + aiTimeDistancesToTourOffice[preRep[indexTo]];
                newPartValue += aiTimeDistancesFromTourOffice[preRep[1]] + aiTimeDistanceMatrix[preRep[indexTo]][preRep[0]] + aiTimeDistancesToTourOffice[preRep[0]];
            } else {
                prePartValue += aiTimeDistancesFromTourOffice[preRep[0]] + aiTimeDistanceMatrix[preRep[0]][preRep[1]] + aiTimeDistanceMatrix[preRep[indexTo]][preRep[indexTo + 1]];
                newPartValue += aiTimeDistancesFromTourOffice[preRep[1]] + aiTimeDistanceMatrix[preRep[indexTo]][preRep[0]] + aiTimeDistanceMatrix[preRep[0]][preRep[indexTo + 1]];
            }
        } else if (indexFrom == length - 1) {
            if (indexTo == 0) {
                prePartValue += aiTimeDistancesFromTourOffice[preRep[0]] + aiTimeDistanceMatrix[preRep[indexFrom - 1]][preRep[indexFrom]] + aiTimeDistancesToTourOffice[preRep[indexFrom]];
                newPartValue += aiTimeDistancesFromTourOffice[preRep[indexFrom]] + aiTimeDistanceMatrix[preRep[indexFrom]][preRep[0]] + aiTimeDistancesToTourOffice[preRep[indexFrom - 1]];
            } else {
                prePartValue += aiTimeDistanceMatrix[preRep[indexTo - 1]][preRep[indexTo]] + aiTimeDistanceMatrix[preRep[indexFrom - 1]][preRep[indexFrom]] + aiTimeDistancesToTourOffice[preRep[indexFrom]];
                newPartValue += aiTimeDistanceMatrix[preRep[indexTo - 1]][preRep[indexFrom]] + aiTimeDistanceMatrix[preRep[indexFrom]][preRep[indexTo]] + aiTimeDistancesToTourOffice[preRep[indexFrom - 1]];
            }
        } else {
            if (indexTo == 0) {
                prePartValue += aiTimeDistancesFromTourOffice[preRep[0]] + aiTimeDistanceMatrix[preRep[indexFrom - 1]][preRep[indexFrom]] + aiTimeDistanceMatrix[preRep[indexFrom]][preRep[indexFrom + 1]];
                newPartValue += aiTimeDistancesFromTourOffice[preRep[indexFrom]] + aiTimeDistanceMatrix[preRep[indexFrom]][preRep[0]] + aiTimeDistanceMatrix[preRep[indexFrom - 1]][preRep[indexFrom + 1]];
            } else if (indexTo == length - 1) {
                prePartValue += aiTimeDistanceMatrix[preRep[indexFrom - 1]][preRep[indexFrom]] + aiTimeDistanceMatrix[preRep[indexFrom]][preRep[indexFrom + 1]] + aiTimeDistancesToTourOffice[preRep[indexTo]];
                newPartValue += aiTimeDistanceMatrix[preRep[indexFrom - 1]][preRep[indexFrom + 1]] + aiTimeDistanceMatrix[preRep[indexTo]][preRep[indexFrom]] + aiTimeDistancesToTourOffice[preRep[indexFrom]];
            } else {
                if (indexFrom < indexTo) ++indexTo;
                prePartValue += aiTimeDistanceMatrix[preRep[indexFrom - 1]][preRep[indexFrom]] + aiTimeDistanceMatrix[preRep[indexFrom]][preRep[indexFrom + 1]] + aiTimeDistanceMatrix[preRep[indexTo - 1]][preRep[indexTo]];
                newPartValue += aiTimeDistanceMatrix[preRep[indexFrom - 1]][preRep[indexFrom + 1]] + aiTimeDistanceMatrix[preRep[indexTo - 1]][preRep[indexFrom]] + aiTimeDistanceMatrix[preRep[indexFrom]][preRep[indexTo]];
            }
        }
        return newPartValue - prePartValue;

    }

    @Override
    public int getTravelTime(int location_a, int location_b) {
        return aiTimeDistanceMatrix[location_a][location_b];
    }

    @Override
    public int getVisitingTimeAt(int landmarkId) {
        return aiVisitingDurations[landmarkId];
    }

    @Override
    public int getTravelTimeFromTourOfficeToLandmark(int toLandmarkId) {
        return aiTimeDistancesFromTourOffice[toLandmarkId];
    }

    @Override
    public int getTravelTimeFromLandmarkToTourOffice(int fromLandmarkId) {
        return aiTimeDistancesToTourOffice[fromLandmarkId];
    }


    // find and return the index of landmark with smallest Time distance from tour office
    public int getNearestLandmarkIdFromTourOffice(Random oRandom) {
        // find the smallest Time distance
        int min = Arrays.stream(aiTimeDistancesFromTourOffice).min().orElseThrow();
        // get array of all indexes of min:
        int[] indexes = IntStream.range(0, aiTimeDistancesFromTourOffice.length).filter(i -> aiTimeDistancesFromTourOffice[i] == min).toArray();
        // choose an index at random
        return indexes[oRandom.ints(1, 0, indexes.length).sum()];
    }

    // find and return the index of landmark with smallest Time distance from given landmark
    public int getNearestLandmarkIdFromLandmarkId(ArrayList<Integer> arrayList, Random oRandom) {
        int from = arrayList.get(arrayList.size() - 1);
        int min = Integer.MAX_VALUE;

        // find the smallest Time distance
        for (int i = 0; i < aiTimeDistanceMatrix[from].length; i++)
            if (!arrayList.contains(i)) {
                if (aiTimeDistanceMatrix[from][i] < min)
                    min = aiTimeDistanceMatrix[from][i];
            }

        //To get array of all indexes of min:
        int finalMin = min;
        int[] indexes = IntStream.range(0, aiTimeDistanceMatrix[from].length).filter(i -> aiTimeDistanceMatrix[from][i] == finalMin && !arrayList.contains(i)).toArray();
        // choose an index at random
        return indexes[oRandom.ints(1, 0, indexes.length).sum()];
    }
}
