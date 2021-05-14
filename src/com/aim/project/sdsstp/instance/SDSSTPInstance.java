package com.aim.project.sdsstp.instance;

import java.util.*;

import com.aim.project.sdsstp.SDSSTPObjectiveFunction;
import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPInstanceInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;
import com.aim.project.sdsstp.solution.SDSSTPSolution;
import com.aim.project.sdsstp.solution.SolutionRepresentation;

/**
 * @author Warren G. Jackson
 * @since 26/03/2021
 * <p>
 * Methods needing to be implemented:
 * - public SDSSTPSolution createSolution(InitialisationMode mode)
 */
public class SDSSTPInstance implements SDSSTPInstanceInterface {

    private final String strInstanceName;

    private final int iNumberOfLandmarks;

    private final SDSSTPLocation oTourOffice;

    private final SDSSTPLocation[] aoLandmarks;

    private final Random oRandom;

    private final ObjectiveFunctionInterface oObjectiveFunction;

    public SDSSTPInstance(String strInstanceName, int iNumberOfLandmarks,
                          com.aim.project.sdsstp.instance.SDSSTPLocation oTourOffice,
                          com.aim.project.sdsstp.instance.SDSSTPLocation[] aoLandmarks, Random oRandom,
                          ObjectiveFunctionInterface f) {

        this.strInstanceName = strInstanceName;
        this.iNumberOfLandmarks = iNumberOfLandmarks;
        this.oTourOffice = oTourOffice;
        this.aoLandmarks = aoLandmarks;
        this.oRandom = oRandom;
        this.oObjectiveFunction = f;
    }

    @Override
    public SDSSTPSolution createSolution(InitialisationMode mode) {
        int[] arr;
        ArrayList<Integer> oRep = new ArrayList<Integer>();

        // the order of the landmarks being visited should be chosen at random
        // (dependent on a seeded random number generator).
        if (mode == InitialisationMode.RANDOM) {
            // create a shuffled arraylist based on random seed.
            for (int i = 0; i < iNumberOfLandmarks; i++)
                oRep.add(i);
            Collections.shuffle(oRep, oRandom);
        }
        // start from tour office location, each successive landmark location in the tour is chosen
        // as the one that has the smallest travelling time from the current location that is not already part of the solution
        else if (mode == InitialisationMode.CONSTRUCTIVE) {
            // step 1: choose the first landmark nearest to office
            oRep.add(((SDSSTPObjectiveFunction) oObjectiveFunction).getNearestLandmarkIdFromTourOffice(oRandom));
            // step 2:
            for (int i = 1; i < iNumberOfLandmarks; i++)
                oRep.add(((SDSSTPObjectiveFunction) oObjectiveFunction).getNearestLandmarkIdFromLandmarkId(oRep, oRandom));
        }

        //change arraylist to int array
        arr = oRep.stream().mapToInt(Integer::intValue).toArray();
//        System.out.println(Arrays.toString(arr));

        // create representation and return solution
        SolutionRepresentation rep = new SolutionRepresentation(arr); // create representation
        int oValue = oObjectiveFunction.getObjectiveFunctionValue(rep); // calculate Objective Function Value
        return new SDSSTPSolution(rep, oValue, iNumberOfLandmarks); // return solution
    }

    @Override
    public ObjectiveFunctionInterface getSDSSTPObjectiveFunction() {

        return oObjectiveFunction;
    }

    @Override
    public int getNumberOfLandmarks() {

        return iNumberOfLandmarks;
    }

    @Override
    public SDSSTPLocation getLocationForLandmark(int deliveryId) {

        return aoLandmarks[deliveryId];
    }

    @Override
    public SDSSTPLocation getTourOffice() {

        return this.oTourOffice;
    }

    @Override
    public ArrayList<SDSSTPLocation> getSolutionAsListOfLocations(SDSSTPSolutionInterface oSolution) {

        ArrayList<SDSSTPLocation> locs = new ArrayList<>();
        locs.add(oTourOffice);
        int[] aiDeliveries = oSolution.getSolutionRepresentation().getSolutionRepresentation();
        for (int i = 0; i < aiDeliveries.length; i++) {
            locs.add(getLocationForLandmark(aiDeliveries[i]));
        }
        locs.add(oTourOffice);
        return locs;
    }

    @Override
    public String getInstanceName() {

        return strInstanceName;
    }

}
