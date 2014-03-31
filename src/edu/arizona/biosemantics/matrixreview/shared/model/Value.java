package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;

public class Value implements Serializable {

	private String value;
	private String comment = "";

	public Value() { }
	
	public Value(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return value;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
