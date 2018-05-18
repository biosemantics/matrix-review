package edu.arizona.biosemantics.matrixreview.shared.model.core;

import java.io.Serializable;
import java.util.Map;

public class CompleteValue implements Serializable{
	
	private static final long serialVersionUID = 1L;
 // can't use boolean due to gwt limitation
	private String value;
	private String modifier; //Hong added 3/20/2018 so character similarity can make use of these info
	private String constraint;
	private String negation;	
	private String type;
	private String charType;
	private String constraintId;
	private String from;
	private String fromInclusive;
	private String fromUnit;
	private String fromModifier;
	private String geographicalConstraint;
	private String inBrackets;
	private String organConstraint;
	private String otherConstraint;
	private String parallelismConstraint;
	private String taxonConstraint;
	private String to;
	private String toInclusive;
	private String toUnit;
	private String toModifier;
	private String upperRestricted;
	private String unit;
	private String ontologyId;
	private String provenance;
	private String notes;
	private String valueOriginal;
	private String establishmentMeans;
	private String src;
	private String isModifier;
	private Map<String, String> valueStatement;


	public CompleteValue(){

	}

	public String getValue() {
 		if(value == null)
 			return "";
 		return value;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	public String getNegation() {
		return negation;
	}

	public void setNegation(String negation) {
		this.negation = negation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCharType() {
		return charType;
	}

	public void setCharType(String charType) {
		this.charType = charType;
	}

	public String getConstraintId() {
		return constraintId;
	}

	public void setConstraintId(String constraintId) {
		this.constraintId = constraintId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getFromInclusive() {
		return fromInclusive;
	}

	public void setFromInclusive(String fromInclusive) {
		this.fromInclusive = fromInclusive;
	}

	public String getFromUnit() {
		return fromUnit;
	}

	public void setFromUnit(String fromUnit) {
		this.fromUnit = fromUnit;
	}

	public String getFromModifier() {
		return fromModifier;
	}

	public void setFromModifier(String fromModifier) {
		this.fromModifier = fromModifier;
	}

	public String getGeographicalConstraint() {
		return geographicalConstraint;
	}

	public void setGeographicalConstraint(String geographicalConstraint) {
		this.geographicalConstraint = geographicalConstraint;
	}

	public String getInBrackets() {
		return inBrackets;
	}

	public void setInBrackets(String inBrackets) {
		this.inBrackets = inBrackets;
	}

	public String getOrganConstraint() {
		return organConstraint;
	}

	public void setOrganConstraint(String organConstraint) {
		this.organConstraint = organConstraint;
	}

	public String getOtherConstraint() {
		return otherConstraint;
	}

	public void setOtherConstraint(String otherConstraint) {
		this.otherConstraint = otherConstraint;
	}

	public String getParallelismConstraint() {
		return parallelismConstraint;
	}

	public void setParallelismConstraint(String parallelismConstraint) {
		this.parallelismConstraint = parallelismConstraint;
	}

	public String getTaxonConstraint() {
		return taxonConstraint;
	}

	public void setTaxonConstraint(String taxonConstraint) {
		this.taxonConstraint = taxonConstraint;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getToInclusive() {
		return toInclusive;
	}

	public void setToInclusive(String toInclusive) {
		this.toInclusive = toInclusive;
	}

	public String getToUnit() {
		return toUnit;
	}

	public void setToUnit(String toUnit) {
		this.toUnit = toUnit;
	}

	public String getToModifier() {
		return toModifier;
	}

	public void setToModifier(String toModifier) {
		this.toModifier = toModifier;
	}

	public String getUpperRestricted() {
		return upperRestricted;
	}

	public void setUpperRestricted(String upperRestricted) {
		this.upperRestricted = upperRestricted;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getOntologyId() {
		return ontologyId;
	}

	public void setOntologyId(String ontologyId) {
		this.ontologyId = ontologyId;
	}

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getValueOriginal() {
		return valueOriginal;
	}

	public void setValueOriginal(String value_original) {
		this.valueOriginal = value_original;
	}

	public String getEstablishmentMeans() {
		return establishmentMeans;
	}

	public void setEstablishmentMeans(String establishment_means) {
		this.establishmentMeans = establishment_means;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String isModifier() {
		return isModifier;
	}
	
	public void setIsModifier(String isModifier) {
		this.isModifier= isModifier;
	}
	

	public Map<String, String> getValueStatement() {
		return valueStatement;
	}

	public void setValueStatement(Map<String, String> valueStatement) {
		this.valueStatement = valueStatement;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getConstraint() {
		return constraint;
	}

	public void setConstraint(String constraint) {
		this.constraint = constraint;
	}


	public String toString() {
 		return getValue();
 	}
	
	

}
