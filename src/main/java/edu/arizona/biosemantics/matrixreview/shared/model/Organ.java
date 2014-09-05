package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Organ implements Serializable {

	private static int ID = 0;
	
	private int id = ID++;
	private String name;
	private Set<Character> characters = new HashSet<Character>();

	public Organ() { }
	
	public Organ(String name) {
		this.name = name;
	}
		
	public Set<Character> getCharacters() {
		return new HashSet<Character>(characters);
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
	
	protected void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Organ other = (Organ) obj;
		if (id != other.id)
			return false;
		return true;
	}
		
}

