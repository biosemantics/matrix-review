package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class Model implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private TaxonMatrix taxonMatrix;
	private MatrixMode matrixMode = MatrixMode.HIERARCHY;
	
	private List<Color> colors = new ArrayList<Color>();
	private Map<Object, Color> coloreds = new HashMap<Object, Color>();
	
	private Map<Object, String> comments = new HashMap<Object, String>();
	private Map<Object, Boolean> locks = new HashMap<Object, Boolean>();
	private Map<Object, Boolean> hiddens = new HashMap<Object, Boolean>();
	private Map<Object, Boolean> dirties = new HashMap<Object, Boolean>();
	
	private Map<Character, ControlMode> controlModes = new HashMap<Character, ControlMode>();
	private Map<Character, List<String>> categoricalStates = new HashMap<Character, List<String>>();

	public Model() { }
	
	public Model(TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
	}
	
	public Model(Model copy, TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
		this.matrixMode = copy.matrixMode;
		this.colors = copy.colors;
		this.coloreds = copy.coloreds;
		this.comments = copy.comments;
		this.locks = copy.locks;
		this.hiddens = copy.hiddens;
		this.dirties = copy.dirties;
		this.controlModes = copy.controlModes;
		this.categoricalStates = copy.categoricalStates;
	}
	
	public List<Color> getColors() {
		return colors;
	}

	public boolean hasColors() {
		return !colors.isEmpty();
	}
	
	public boolean hasComment(Object object) {
		return comments.containsKey(object) && !comments.get(object).trim().isEmpty();
	}

	public String getComment(Object object) {
		if (comments.containsKey(object))
			return comments.get(object);
		return "";
	}
	
	public Color getColor(Object object) {
		if (coloreds.containsKey(object))
			return coloreds.get(object);
		return null;
	}

	public ControlMode getControlMode(Character character) {
		if (controlModes.containsKey(character))
			return controlModes.get(character);
		return ControlMode.OFF;
	}

	public ControlMode determineControlMode(Character character) {
		List<Value> values = taxonMatrix.getValues(character);
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
	private boolean isNumeric(List<Value> values) {
		int numericalCount = 0;
		for (Value value : values) {
			if (value.getValue().matches("[0-9]*")) {
				numericalCount++;
			}
		}
		return numericalCount > values.size() / 2;
	}

	public TaxonMatrix getTaxonMatrix() {
		return taxonMatrix;
	}

	public boolean isLocked(Object object) {
		if (locks.containsKey(object))
			return locks.get(object);
		return false;
	}

	public boolean isHidden(Object object) {
		if (hiddens.containsKey(object))
			return hiddens.get(object);
		return false;
	}

	public List<String> getStates(Character character) {
		if(taxonMatrix.isContained(character))
			if(getControlMode(character).equals(ControlMode.CATEGORICAL))
				if(categoricalStates.containsKey(character))
					return categoricalStates.get(character);
		return null;
	}

	public boolean isDirty(Object object) {
		if (dirties.containsKey(object))
			return dirties.get(object);
		return false;
		
	}

	public boolean isCommented(Object object) {
		return comments.containsKey(object);
	}

	public boolean hasColor(Object object) {
		return coloreds.containsKey(object) && coloreds.get(object) != null;
	}

	public MatrixMode getMatrixMode() {
		return matrixMode;
	}

	public void setMatrixMode(MatrixMode matrixMode) {
		this.matrixMode = matrixMode;
	}

	public void setControlMode(Character character, ControlMode controlMode) {
		controlModes.put(character, controlMode);
	}

	public void setStates(Character character, List<String> states) {
		categoricalStates.put(character, states);
	}

	public void setHidden(Object object, boolean value) {
		hiddens.put(object, value);
	}

	public void setLocked(Object object, boolean value) {
		locks.put(object, value);
	}

	public void setComment(Object object, String comment) {
		comments.put(object, comment);
	}

	public void setColor(Object object, Color color) {
		coloreds.put(object, color);
	}	

	@Override
	public String toString() {
		String result = taxonMatrix.toString() + "\n\n";
		result += "MatrixMode: " + matrixMode + "\n";
		result += "Colors: " + colors.toString()  + "\n";
		result += "Coloreds: " +  coloreds.toString()  + "\n";
		result += "Comments: " + comments.toString()  + "\n";
		result += "Locks: " + locks.toString()  + "\n";
		result += "Hiddens: " + hiddens.toString()  + "\n";
		result += "Dirties: " + dirties.toString()  + "\n";
		result += "ControlModes: " + controlModes.toString()  + "\n";
		result += "States: " + categoricalStates.toString()  + "\n";
		return result;
	}


	
}
