package edu.arizona.biosemantics.matrixreview.shared.model.core;

import java.io.Serializable;

public class Value implements Serializable, Comparable<Value> {

	private String value;

	public Value() { }
	
	public Value(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		if(value == null)
			return "";
		return value;
	}

	protected void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return getValue();
	}

	@Override
	public int compareTo(Value o) {
		return this.getValue().compareTo(o.getValue());
	}
	
}
