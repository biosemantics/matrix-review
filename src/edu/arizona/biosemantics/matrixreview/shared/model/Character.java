package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;

public class Character implements Serializable {

	private String name;

	public Character() { }
	
	public Character(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
