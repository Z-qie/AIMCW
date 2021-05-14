package com.aim.project.sdsstp.runners;


import AbstractClasses.HyperHeuristic;

import com.aim.project.sdsstp.hyperheuristics.InitialisationTest_HH;
import com.aim.project.sdsstp.hyperheuristics.RL_HH;
import com.aim.project.sdsstp.hyperheuristics.SCF_AM_HH;
import com.aim.project.sdsstp.hyperheuristics.SR_IE_HH;

/**
 * @author Warren G. Jackson
 * @since 26/03/2021
 *
 * Runs a hyper-heuristic which just initialises a solution and dispalys that initial solution.
 */
public class InitialisationTest_VisualRunner extends HH_Runner_Visual {

	@Override
	protected HyperHeuristic getHyperHeuristic(long seed) {

//		return new InitialisationTest_HH(seed); // will test this
//		return new SCF_AM_HH(seed);
		return new RL_HH(seed);
//		return new SR_IE_HH(seed);x

	}
	
	public static void main(String [] args) {

		HH_Runner_Visual runner = new InitialisationTest_VisualRunner();
		runner.run();
	}

}
