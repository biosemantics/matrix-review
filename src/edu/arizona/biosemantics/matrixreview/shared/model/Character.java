package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Character implements Serializable, HasColor, HasComment, HasDirty, HasLocked, HasControlMode {

	private String name = "";
	private Organ organ;
	
	/**
	 * Matrix related
	 */
	private TaxonMatrix taxonMatrix;
	private String comment = "";
	private Color color;
	private boolean dirty = false;
	private boolean locked = false;
	private boolean hidden = false;
	private ControlMode controlMode = ControlMode.OFF;
	private List<String> states = null;

	public Character() { }
	
	public Character(String name) {
		this.name = name;
	}
	
	public Character(String name, Organ organ) {
		this.name = name;
		this.organ = organ;
	}
	
	public TaxonMatrix getTaxonMatrix() {
		return taxonMatrix;
	}

	protected void setTaxonMatrix(TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}
	
	public Organ getOrgan() {
		return organ;
	}
	
	public boolean hasOrgan() {
		return organ != null;
	}
	
	protected void setOrgan(Organ organ) {
		this.organ = organ;
	}
	
	public String toString() {
		if(organ == null)
			return name;
		return name + " of " + organ.toString();
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

	public boolean isLocked() {
		return locked;
	}

	protected void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Override
	public ControlMode getControlMode() {
		return controlMode;
	}
	
	protected void setControlMode(ControlMode controlMode) {
		this.controlMode = controlMode;
	}
	
	protected void setStates(List<String> states) {
		this.states = states;
	}
	
	public List<String> getStates() {
		return new ArrayList<String>(states);
	}
	
	public ControlMode determineControlMode() {
		Set<String> values = new HashSet<String>();
		for (Taxon taxon : taxonMatrix.list()) {
			values.add(taxon.get(this).getValue());
		}
		if (isNumeric(values))
			return ControlMode.NUMERICAL;
		if (values.isEmpty())
			return ControlMode.OFF;
		return ControlMode.CATEGORICAL;
	}
	
	/**
	 * Determine whether the values are probably more numerical. Simplistic
	 * implementation for now
	 * 
	 * @param values
	 * @return
	 */
	private boolean isNumeric(Set<String> values) {
		int numericalCount = 0;
		for (String value : values) {
			if (value.matches("[0-9]*")) {
				numericalCount++;
			}
		}
		return numericalCount > values.size() / 2;
	}

	public boolean isHidden() {
		return hidden;
	}
	
	protected void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
}
