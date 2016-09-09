package edu.arizona.biosemantics.matrixreview.client.common;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class Normalization {
	public Character character;
	public String unit;
	
    
	public Normalization(Character c, String unit){
		this.character = c;
		this.unit = unit;
	}
	
	public String getUnitSets() {
		return this.unit;
	}
	
	public void setUnitSets(String unit){
		this.unit=unit;
	}
	
	public Character getCharacter(){
		return this.character;
		
	}
}
