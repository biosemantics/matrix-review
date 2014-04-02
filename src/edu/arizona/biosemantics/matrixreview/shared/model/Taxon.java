package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class Taxon implements Serializable, Comparable<Taxon>, HasColor, HasComment, HasDirty {

	public String name;
	private String description = "";
	private Map<Character, Value> values = new HashMap<Character, Value>();
	private String comment = "";
	private Color color;
	private TaxonMatrix taxonMatrix;
	private boolean dirty = false;

	public Taxon() { }
	
	public Taxon(String name, TaxonMatrix taxonMatrix) {
		this.name = name;
		this.taxonMatrix = taxonMatrix;
	}
	
	public Taxon(String name, String description, TaxonMatrix taxonMatrix) {
		this.name = name;
		this.description = description;
		this.taxonMatrix = taxonMatrix;
	}
	
	public Taxon(String name, String description, Collection<Character> characters, TaxonMatrix taxonMatrix) {
		this.name = name;
		this.description = description;
		this.init(characters);
		this.taxonMatrix = taxonMatrix;
	}
	
	public Taxon(String name, Map<Character, Value> values, TaxonMatrix taxonMatrix) {
		super();
		this.name = name;
		this.values = values;
		this.taxonMatrix = taxonMatrix;
	}
	
	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	protected Value put(Character key, Value value) {
		return values.put(key, value);
	}

	protected Value remove(Character key) {
		return values.remove(key);
	}

	public Value get(Character key) {
		return values.get(key);
	}

	protected void init(Collection<Character> characters) {
		for(Character character : characters) {
			this.addCharacter(character);
		}
	}

	protected void addCharacter(Character character) {
		if(!values.containsKey(character))
			values.put(character, new Value(""));
	}

	public String getDescription() {
		return description;
	}
	
	protected void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return name + ": " + values.toString();
	}

	@Override
	public int compareTo(Taxon o) {
		return this.getName().compareTo(o.getName());
	}

	public String getComment() {
		return comment;
	}

	protected void setComment(String comment) {
		this.comment = comment;
	}

	public Color getColor() {
		return color;
	}

	protected void setColor(Color color) {
		this.color = color;
	}

	public TaxonMatrix getTaxonMatrix() {
		return taxonMatrix;
	}

	protected void setTaxonMatrix(TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
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

	public void setValue(Character character, Value value) {
		taxonMatrix.setValue(this, character, value);
	}
}
