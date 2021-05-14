package com.aim.project.sdsstp.solution;

import com.aim.project.sdsstp.interfaces.SolutionRepresentationInterface;

/**
 * 
 * @author Warren G. Jackson
 * 
 *
 */
public class SolutionRepresentation implements SolutionRepresentationInterface {

	private int[] representation;
	
	public SolutionRepresentation(int[] representation) {
		this.representation = representation;
	}
	
	@Override
	public int[] getSolutionRepresentation() {
//		return representation.clone();
		return representation;
	}

	@Override
	public void setSolutionRepresentation(int[] solution) {
		this.representation = solution;
	}

	@Override
	public int getNumberOfLandmarks() {
		return representation.length;
	}

	@Override
	public SolutionRepresentationInterface clone() { // deep clone
		return new SolutionRepresentation(representation.clone());
	}

}
