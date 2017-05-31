package edu.arizona.biosemantics.matrixreview.shared.model.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * value for a character, used for display
 *
 */
public class Value implements Serializable, Comparable<Value> {

	private static final long serialVersionUID = 1L;

	private String value;
	
	//<value, sentence>
	private Map<String, String> valueStatement = new HashMap();

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
	
	public void addValueStatement(String value, String sentence){
		this.valueStatement.put(value, sentence);
	}
	
	public String getStatements(){
		String allStatements = "";
		for( String statement : valueStatement.values()) allStatements+= " "+statement;
		return allStatements;
	}
	
	public String getStatements(String value){
		return valueStatement.get(value);
	}
}
