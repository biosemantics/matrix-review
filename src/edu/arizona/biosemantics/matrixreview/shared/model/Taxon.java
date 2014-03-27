package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Taxon implements Serializable {

	public String name;
	private String description = "";
	private Map<Character, Value> values = new HashMap<Character, Value>();

	public Taxon() { }
	
	public Taxon(String name) {
		this.name = name;
	}
	
	public Taxon(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	public Taxon(String name, String description, Collection<Character> characters) {
		this.name = name;
		this.description = description;
		this.init(characters);
	}
	
	public Taxon(String name, Map<Character, Value> values) {
		super();
		this.name = name;
		this.values = values;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Value put(Character key, Value value) {
		return values.put(key, value);
	}

	public Value remove(Character key) {
		return values.remove(key);
	}

	public Value get(Character key) {
		return values.get(key);
	}

	public void init(Collection<Character> characters) {
		for(Character character : characters) {
			this.addCharacter(character);
		}
	}

	public void addCharacter(Character character) {
		if(!values.containsKey(character))
			values.put(character, new Value(""));
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return name + ": " + values.toString();
	}
	
}
