package com.aim.project.sdsstp.instance.reader;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import com.aim.project.sdsstp.SDSSTPObjectiveFunction;
import com.aim.project.sdsstp.instance.SDSSTPInstance;
import com.aim.project.sdsstp.instance.SDSSTPLocation;
import com.aim.project.sdsstp.interfaces.ObjectiveFunctionInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPInstanceInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPInstanceReaderInterface;

/**
 * @author Warren G. Jackson
 * @since 26/03/2021
 * <p>
 * Methods needing to be implemented:
 * - public SDSSTPInstanceInterface readSDSSTPInstance(Path path, Random random)
 */
public class SDSSTPInstanceReader implements SDSSTPInstanceReaderInterface {

    private static SDSSTPInstanceReader oInstance;

    private SDSSTPInstanceReader() {
    }

    public static SDSSTPInstanceReader getInstance() {
        if (oInstance == null) {
            oInstance = new SDSSTPInstanceReader();
        }
        return oInstance;
    }


    @Override
    public SDSSTPInstanceInterface readSDSSTPInstance(Path path, Random random) {
        List<String> oContents = null;
        String strInstanceName = "";
        int iNumberOfLandmarks = 0;
        int[][] aiTimeMatrix = new int[0][];
        int[] aiTimeFromOffice = new int[0];
        int[] aiTimeToOffice = new int[0];
        int[] aiVisitDuration = new int[0];
        SDSSTPLocation oTourOffice = null;
        SDSSTPLocation[] aoLandmarks = null;

        // read file
        try {
            oContents = Files.readAllLines(path);
        } catch (IOException ex) {
            ex.printStackTrace();//handle exception here
        }

        //Read from the stream
        if (oContents != null) {
            //for each line of content in contents
            for (int i = 0; i < oContents.size(); i++) {
//                System.out.println(contents.get(i));// print the line
                // get instance name
                if (oContents.get(i).contains("NAME:")) {
                    strInstanceName = oContents.get(++i);
                }
                // get number of landmarks
                if (oContents.get(i).contains("LANDMARKS:")) {
                    iNumberOfLandmarks = Integer.parseInt(oContents.get(++i));
                }
                // get time matrix
                if (oContents.get(i).contains("TIME_MATRIX:")) {
                    // initialize the 2d int array by iNumberOfLandmarks
                    aiTimeMatrix = new int[iNumberOfLandmarks][iNumberOfLandmarks];

                    // read all data from TIME_MATRIX
                    for (int j = 0; j < iNumberOfLandmarks; j++) {
                        String[] rawTimeValues = oContents.get(++i).split(" ");
                        for (int k = 0; k < iNumberOfLandmarks; k++)
                            aiTimeMatrix[j][k] = Integer.parseInt(rawTimeValues[k]);
                    }
                }
                // get time consumed from office to each location
                if (oContents.get(i).contains("TIME_FROM_OFFICE:")) {
                    aiTimeFromOffice = new int[iNumberOfLandmarks];
                    String[] rawTimeValues = oContents.get(++i).split(" ");
                    for (int j = 0; j < iNumberOfLandmarks; j++)
                        aiTimeFromOffice[j] = Integer.parseInt(rawTimeValues[j]);
                }
                // get time consumed from each location to the office
                if (oContents.get(i).contains("TIME_TO_OFFICE:")) {
                    aiTimeToOffice = new int[iNumberOfLandmarks];
                    String[] rawTimeValues = oContents.get(++i).split(" ");
                    for (int j = 0; j < iNumberOfLandmarks; j++)
                        aiTimeToOffice[j] = Integer.parseInt(rawTimeValues[j]);
                }
                // get visit duration at each location
                if (oContents.get(i).contains("VISIT_DURATION:")) {
                    aiVisitDuration = new int[iNumberOfLandmarks];
                    String[] rawTimeValues = oContents.get(++i).split(" ");
                    for (int j = 0; j < iNumberOfLandmarks; j++) {
                        aiVisitDuration[j] = Integer.parseInt(rawTimeValues[j]);
                    }
                }
                // get office location
                if (oContents.get(i).contains("OFFICE_LOCATION:")) {
                    String[] rawTimeValues = oContents.get(++i).split(" ");
                    oTourOffice = new SDSSTPLocation(Integer.parseInt(rawTimeValues[0]), Integer.parseInt(rawTimeValues[1]));
                }
                // get each location's coordinate
                if (oContents.get(i).contains("LANDMARK_LOCATIONS:")) {
                    aoLandmarks = new SDSSTPLocation[iNumberOfLandmarks];
                    for (int j = 0; j < iNumberOfLandmarks; j++) {
                        String[] rawTimeValues = oContents.get(++i).split(" ");
                        aoLandmarks[j] = new SDSSTPLocation(Integer.parseInt(rawTimeValues[0]), Integer.parseInt(rawTimeValues[1]));
//                        System.out.println(aoLandmarks[j].getX() + " " + aoLandmarks[j].getY());
                    }
                }
            }
        }
        SDSSTPObjectiveFunction f = new SDSSTPObjectiveFunction(aiTimeMatrix, aiTimeFromOffice, aiTimeToOffice, aiVisitDuration);
        return new SDSSTPInstance(strInstanceName, iNumberOfLandmarks, oTourOffice, aoLandmarks, random, f);
    }
}
