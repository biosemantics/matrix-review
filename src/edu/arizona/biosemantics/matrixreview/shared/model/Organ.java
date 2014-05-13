package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Organ implements Serializable {

	private String name;
	private Set<Character> characters = new HashSet<Character>();

	public Organ() { }
	
	public Organ(String name) {
		this.name = name;
	}
		
	public Set<Character> getCharacters() {
		return characters;
	}

	protected void addCharacter(Character character) {
		characters.add(character);
	}
	
	protected void removeCharacter(Character character) {
		characters.remove(character);
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
}
