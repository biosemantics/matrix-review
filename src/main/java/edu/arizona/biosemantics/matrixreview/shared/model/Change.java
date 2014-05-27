package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;

public class Change implements Serializable {
	
	private Taxon taxon;
	private Character character;
	private Value oldValue;
	private Value newValue;
	
	public Change() { }
	
	public Change(Taxon taxon, Character character, Value oldValue,
			Value newValue) {
		super();
		this.taxon = taxon;
		this.character = character;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Taxon getTaxon() {
		return taxon;
	}
	public void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}
	public Character getCharacter() {
		return character;
	}
	public void setCharacter(Character character) {
		this.character = character;
	}
	public Value getOldValue() {
		return oldValue;
	}
	public void setOldValue(Value oldValue) {
		this.oldValue = oldValue;
	}
	public Value getNewValue() {
		return newValue;
	}
	public void setNewValue(Value newValue) {
		this.newValue = newValue;
	}

}
