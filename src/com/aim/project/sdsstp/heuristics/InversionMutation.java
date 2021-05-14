package com.aim.project.sdsstp.heuristics;

import java.util.Random;

import com.aim.project.sdsstp.interfaces.HeuristicInterface;
import com.aim.project.sdsstp.interfaces.SDSSTPSolutionInterface;

/**
 * 
 * @author Warren G. Jackson
 * @since 26/03/2021
 * 
 * Methods needing to be implemented:
 * - public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation)
 * - public boolean isCrossover()
 * - public boolean usesIntensityOfMutation()
 * - public boolean usesDepthOfSearch()
 */
public class InversionMutation extends HeuristicOperators implements HeuristicInterface {
	
	private final Random random;
	
	public InversionMutation(Random random) {
		super();
		this.random = random;
	}

	@Override
	public double apply(SDSSTPSolutionInterface solution, double depthOfSearch, double intensityOfMutation) {

		int times = applyIOM(intensityOfMutation);

		for (int i = 0; i < times; i++) {
			// get the index of location to be swapped
			int indexFrom = random.nextInt(solution.getNumberOfLandmarks());
			int indexTo = random.nextInt(solution.getNumberOfLandmarks());
			// make sure indexFrom != indexTo
			while(indexTo == indexFrom)
				indexTo = random.nextInt(solution.getNumberOfLandmarks());
			performInversion(solution, indexFrom, indexTo);
		}

		// test
//        System.out.println(Arrays.toString(solution.getSolutionRepresentation().getSolutionRepresentation()) + " = " + solution.getObjectiveFunctionValue());
		return solution.getObjectiveFunctionValue();
	}

	@Override
	public boolean isCrossover() {
		return false;
	}

	@Override
	public boolean usesIntensityOfMutation() {
		return true;
	}

	@Override
	public boolean usesDepthOfSearch() {
		return false;
	}


	@Override
	protected int applyIOM(double intensityOfMutation) {
		if (0.0 <= intensityOfMutation && intensityOfMutation < 0.2)
			return 1;
		if (0.2 <= intensityOfMutation && intensityOfMutation < 0.4)
			return 2;
		if (0.4 <= intensityOfMutation && intensityOfMutation < 0.6)
			return 3;
		if (0.6 <= intensityOfMutation && intensityOfMutation < 0.8)
			return 4;
		if (0.8 <= intensityOfMutation && intensityOfMutation < 1.0)
			return 5;
		if (1.0 == intensityOfMutation)
			return 6;
		return -1;
	}


}
