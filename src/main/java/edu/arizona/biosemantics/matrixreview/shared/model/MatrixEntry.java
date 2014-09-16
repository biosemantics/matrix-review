package edu.arizona.biosemantics.matrixreview.shared.model;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class MatrixEntry {

	private Taxon taxon;
	private Character character;
	private Value value;
		
	public MatrixEntry(Taxon taxon, Character character, Value value) {
		super();
		this.taxon = taxon;
		this.character = character;
		this.value = value;
	}
	public Taxon getTaxon() {
		return taxon;
	}
	public Character getCharacter() {
		return character;
	}
	public Value getValue() {
		return value;
	}
	

}
