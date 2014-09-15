package edu.arizona.biosemantics.matrixreview.shared.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Organ implements Serializable {
	
	//organ has to have id. Using name as an identifier is not sufficient. When renamed they keying info in map is lost
	private static int ID = 0;
	private int id = ID++;
	
	private String name;
	
	private Set<Character> characters = new HashSet<Character>();
	private List<Character> flatCharacters = new ArrayList<Character>();
	
	public Organ() { }
	
	public Organ(String name) {
		this.name = name;
	}
	
	public Organ(String name, List<Character> flatCharacters) {
		this.name = name;
		this.setFlatCharacters(flatCharacters);
	}
		
	public void setFlatCharacters(List<Character> flatCharacters) {
		this.flatCharacters = flatCharacters;
		this.characters.addAll(flatCharacters);
		for(int i=0; i<flatCharacters.size(); i++) {
			Character character = flatCharacters.get(i);
			character.setOrgan(this, i);
		}
	}

	public Set<Character> getCharacters() {
		return characters;
	}
	
	public boolean isContained(Character character) {
		return characters.contains(character);
	}
	
	public List<Character> getFlatCharacters() {
		return flatCharacters;
	}

	public void ensureContained(Character character, int flatIndex) {
		if(!characters.contains(character)) {
			characters.add(character);
			flatCharacters.add(flatIndex, character);
		}
	}
	
	public void remove(Character character) {
		this.characters.remove(character);
		this.flatCharacters.remove(character);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
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

	public int getId() {
		return id;
	}
		
}

