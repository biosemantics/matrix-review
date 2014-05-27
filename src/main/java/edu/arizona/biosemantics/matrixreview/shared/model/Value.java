package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;

public class Value implements Serializable, HasColor, HasComment, HasDirty, Comparable<Value> {

	private String value;
	private String comment = "";
	private Color color;
	private boolean dirty = false;
	
	//maintained so it is e.g. possible to traverse from a Value object to all related parts of the model i.e. taxon, taxonmatrix, character.
	//this is e.g. useful in ValueCell where we want to construct a quicktip that contains information outside of Value.
	//Context variable could there be used to get col/row id and from those retrieve corresponding taxon/character. However, this design decision has been made
	//for convenience over memory efficiency. If this should ever become a problem, use Context and other means where necessary.
	private Taxon taxon;
	private Character character;

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

	public String getComment() {
		return comment;
	}

	protected void setComment(String comment) {
		this.comment = comment;
	}
	
	public boolean isCommented() {
		return !comment.trim().isEmpty();
	}

	public Color getColor() {
		return color;
	}

	protected void setColor(Color color) {
		this.color = color;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	protected void clearDirty() {
		dirty = false;
	}

	protected void setDirty() {
		dirty = true;
	}

	protected void setCharacter(Character character) {
		this.character = character;
	}
	
	protected void setTaxon(Taxon taxon) {
		this.taxon = taxon;
	}
	
	public Taxon getTaxon() {
		return taxon;
	}

	public Character getCharacter() {
		return character;
	}

	@Override
	public int compareTo(Value o) {
		return this.getValue().compareTo(o.getValue());
	}
	
}
