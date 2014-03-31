package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;

public class Character implements Serializable {

	private String name = "";
	private String organ= "";
	private String comment = "";

	public Character() { }
	
	public Character(String name) {
		this.name = name;
	}
	
	public Character(String name, String organ) {
		this.name = name;
		this.organ = organ;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getOrgan() {
		return organ;
	}
	
	public boolean hasOrgan() {
		return organ != null && !organ.isEmpty();
	}
	
	public void setOrgan(String organ) {
		this.organ = organ;
	}
	
	public String toString() {
		if(organ == null || organ.isEmpty())
			return name;
		return name + " of " + organ;
	}
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
