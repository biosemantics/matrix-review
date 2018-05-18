package edu.arizona.biosemantics.matrixreview.shared.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




/**
 * value for a character, used for display
 *
 */
public class Value implements Serializable, Comparable<Value> {

	private static final long serialVersionUID = 1L;

	private String value; //value string shown in matrix cell, like "distinct|united"
	
	private ArrayList<CompleteValue> values; //underlying values (with all value-related attributes of a character element
	
	
	
	//<value, sentence>
	private Map<String, String> valueStatement = new HashMap<String, String>();

	public Value() { }
	
	public Value(String value) {
		super();
		this.value = value;
	}
	
	public Value(String value, ArrayList<CompleteValue> values) {
		super();
		this.value = value;
		this.values = values;
	}
	
	public String getValue() {
		if(value == null)
			return "";
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public ArrayList<CompleteValue> getValues() {
		return values;
	}

	public void setValues(ArrayList<CompleteValue> values) {
		this.values = values;
	}
	
	public String toString() {
		return getValue();
	}
	
	@Override
	public int compareTo(Value o) {
		return this.getValue().compareTo(o.getValue());
	}
	
	public void addValueStatement(String value, String sentence){
		this.valueStatement.put(value, sentence);
	}
	
	public String getStatements(){
		String allStatements = "";
		for(String statement : valueStatement.values()) allStatements+= " "+statement;
		return allStatements;
	}
	
	public String getStatements(String value){
		return valueStatement.get(value);
	}
}
