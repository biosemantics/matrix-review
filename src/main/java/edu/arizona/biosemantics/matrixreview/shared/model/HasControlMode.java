package edu.arizona.biosemantics.matrixreview.shared.model;

public interface HasControlMode {

	public enum ControlMode {
		CATEGORICAL, NUMERICAL, OFF
	}	
	
	public ControlMode getControlMode();
	
}
