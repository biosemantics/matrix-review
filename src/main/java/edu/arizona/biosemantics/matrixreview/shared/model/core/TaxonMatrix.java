package edu.arizona.biosemantics.matrixreview.shared.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.matrixreview.shared.model.MatrixMode;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

/**
 * Only ModelControlers should make changes to any class part of the model
 * 
 * @author rodenhausen
 */
public class TaxonMatrix implements Serializable {

	private static final long serialVersionUID = 1L;

	public class DFSTaxaList extends ArrayList<Taxon> implements Serializable {

		public DFSTaxaList() {
			for (Taxon taxon : hierarchyTaxa) {
				insertDepthFirst(taxon);
			}
		}

		private void insertDepthFirst(Taxon taxon) {
			add(taxon);
			for (Taxon child : taxon.getChildren())
				insertDepthFirst(child);
		}
	}
	
	public class BFSTaxaList extends ArrayList<Taxon> implements Serializable {

		public BFSTaxaList() {
			LinkedList<Taxon> toInserts = new LinkedList<Taxon>();
			for (Taxon taxon : hierarchyTaxa) {
				toInserts.add(taxon);
			}
			insert(toInserts);
		}

		private void insert(LinkedList<Taxon> toInserts) {
			Taxon toInsert = toInserts.poll();
			while(toInsert != null) {
				for(Taxon taxon : toInsert.getChildren())
					toInserts.add(taxon);
				add(toInsert);
				toInsert = toInserts.poll();
			}
		}

		private void insertDepthFirst(Taxon taxon) {
			add(taxon);
			for (Taxon child : taxon.getChildren())
				insertDepthFirst(child);
		}
	}
	
	public class BFSCharactersList extends ArrayList<Character> implements Serializable {

		public BFSCharactersList() {
			for(Organ organ : hierarchyCharacters) {
				for(Character character : organ.getFlatCharacters()) {
					if(visibleCharacters.contains(character))
						add(character);
				}
			}
		}
	}

	private Set<Taxon> taxa = new HashSet<Taxon>();
	private Set<Taxon> visibleTaxa = new HashSet<Taxon>();
	private List<Taxon> flatTaxa = new LinkedList<Taxon>();
	private List<Taxon> hierarchyTaxa = new ArrayList<Taxon>();

	private Set<Character> characters = new HashSet<Character>();
	private Set<Character> visibleCharacters = new HashSet<Character>();
	private Set<Organ> organs = new HashSet<Organ>();
	private Map<String, Organ> nameOrgansMap = new HashMap<String, Organ>();
	private List<Character> flatCharacters = new ArrayList<Character>();
	private List<Organ> hierarchyCharacters = new ArrayList<Organ>();

	private Map<Taxon, Map<Character, Value>> values = new HashMap<Taxon, Map<Character, Value>>();

	//value sentences
	private Map<Taxon, Map<Character, Map<Value, String>>> sentences = 
			new HashMap<Taxon, Map<Character, Map<Value, String>>>();
	
	public TaxonMatrix() {
		
	}

	public TaxonMatrix(List<Organ> hierarchyCharacters, List<Taxon> hierarchyTaxa) {
		this.hierarchyCharacters = hierarchyCharacters;
		for(Organ organ : hierarchyCharacters) {
			this.nameOrgansMap.put(organ.getName(), organ);
			this.organs.add(organ);
		}
		for(Organ organ : hierarchyCharacters) {
			this.visibleCharacters.addAll(organ.getFlatCharacters());
			this.characters.addAll(organ.getFlatCharacters());
			this.flatCharacters.addAll(organ.getFlatCharacters());
		}
		
		this.hierarchyTaxa = hierarchyTaxa;
		this.flatTaxa.addAll(getHierarchyTaxaDFS());
		this.visibleTaxa.addAll(getHierarchyTaxaDFS());
		this.taxa.addAll(getHierarchyTaxaDFS());
	}
	
	public TaxonMatrix(List<Organ> hierarchyCharacters, List<Taxon> hierarchyTaxa, 
			Set<Character> visibleCharacters, Set<Taxon> visibleTaxa) {
		this.hierarchyCharacters = hierarchyCharacters;
		for(Organ organ : hierarchyCharacters) {
			this.nameOrgansMap.put(organ.getName(), organ);
			this.organs.add(organ);
		}
		for(Organ organ : hierarchyCharacters) {
			this.characters.addAll(organ.getFlatCharacters());
			this.flatCharacters.addAll(organ.getFlatCharacters());
		}
		
		this.hierarchyTaxa = hierarchyTaxa;
		this.flatTaxa.addAll(getHierarchyTaxaDFS());
		this.taxa.addAll(getHierarchyTaxaDFS());
		this.visibleTaxa = visibleTaxa;
		this.visibleCharacters = visibleCharacters;
	}

	public DFSTaxaList getHierarchyTaxaDFS() {
		return new DFSTaxaList();
	}
	
	public BFSTaxaList getHierarchyTaxaBFS() {
		return new BFSTaxaList();
	}
	
	public BFSCharactersList getHierarchyCharactersBFS() {
		return new BFSCharactersList();
	}

	public List<Taxon> getFlatTaxa() {
		return flatTaxa;
	}
	
	public List<Taxon> getVisibleFlatTaxa() {
		List<Taxon> result = new LinkedList<Taxon>();
		for(Taxon taxon : flatTaxa) 
			if(isVisiblyContained(taxon))
				result.add(taxon);
		return result;
	}

	public List<Taxon> getHierarchyRootTaxa() {
		return hierarchyTaxa;
	}
	
	public Set<Taxon> getVisibleTaxa() {
		return visibleTaxa;
	}
	
	public Set<Taxon> getTaxa() {
		return taxa;
	}

	public List<Character> getFlatCharacters() {
		return flatCharacters;
	}
	
	public List<Character> getVisibleFlatCharacters() {
		List<Character> result = new LinkedList<Character>();
		for(Character character : flatCharacters) {
			if(isVisiblyContained(character))
				result.add(character);
		}
		return result;
	}

	public List<Organ> getHierarchyCharacters() {
		return hierarchyCharacters;
	}

	public Set<Character> getVisibleCharacters() {
		return visibleCharacters;
	}
	
	public Set<Character> getCharacters() {
		return characters;
	}

	public List<Value> getValues(Character character) {
		List<Value> result = new ArrayList<Value>();
		for (Taxon taxon : this.getHierarchyTaxaDFS())
			if(visibleTaxa.contains(taxon))
				result.add(this.getValue(taxon, character));
		return result;
	}

	public Value getValue(Taxon taxon, Character character) {
		Value defaultValue = new Value("");
		if (isVisiblyContained(taxon) && isVisiblyContained(character)) {
			if(!values.containsKey(taxon)) {
				values.put(taxon, new HashMap<Character, Value>());
				if(!values.get(taxon).containsKey(character)) {
					values.get(taxon).put(character, defaultValue);
				}
			}
			if(!values.get(taxon).containsKey(character)) {
				values.get(taxon).put(character, defaultValue);
			}
			return this.values.get(taxon).get(character);
		}
		return null;
	}

	public int getCharacterCount() {
		return visibleCharacters.size();
	}

	// TODO: needs n*m, may want to improve at some point
	public Taxon getTaxon(Value value) {
		for (Taxon taxon : values.keySet()) {
			Map<Character, Value> characterValues = values.get(taxon);
			for (Character character : characterValues.keySet()) {
				if (characterValues.get(character).equals(value))
					return taxon;
			}
		}
		return null;
	}

	// TODO: needs n*m, may want to improve at some point
	public Character getCharacter(Value value) {
		for (Taxon taxon : values.keySet()) {
			Map<Character, Value> characterValues = values.get(taxon);
			for (Character character : characterValues.keySet()) {
				if (characterValues.get(character).equals(value))
					return character;
			}
		}
		return null;
	}

	public boolean isContained(Taxon taxon) {
		return taxa.contains(taxon);
	}
	
	public boolean isContained(Character character) {
		return characters.contains(character);
	}
	
	public boolean isContained(Organ organ) {
		return organs.contains(organ);
	}

	public boolean isVisiblyContained(Taxon taxon) {
		return visibleTaxa.contains(taxon);
	}

	public boolean isVisiblyContained(Character character) {
		return visibleCharacters.contains(character);
	}
	
	public boolean isVisiblyContained(Organ organ) {
		if(organs.contains(organ)) {
			if(organ.getCharacters().isEmpty())
				return false;
			for(Character character : organ.getCharacters()) {
				if(!isVisiblyContained(character))
					return false;
			}
			return true;
		}
		return false;
	}

	public void setValue(Taxon taxon, Character character, Value value) {
		if (isVisiblyContained(taxon) && isVisiblyContained(character)) {
			if (!values.containsKey(taxon))
				values.put(taxon, new HashMap<Character, Value>());
			Map<Character, Value> taxonValues = values.get(taxon);
			taxonValues.put(character, value);
		}
	}

	public String getCoverage(Taxon taxon) {
		return this.getTaxonValueCount(taxon) + "/" + visibleCharacters.size();
	}

	public int getTaxonValueCount(Taxon taxon) {
		int result = 0;
		if (visibleTaxa.contains(taxon)) {
			for (Character character : this.visibleCharacters) {
				if (hasValue(taxon, character)) {
					result++;
				}
			}
		}
		return result;
	}

	public String getCoverage(Character character) {
		return this.getCharacterValueCount(character) + "/" + this.visibleTaxa.size();
	}

	public int getCharacterValueCount(Character character) {
		int result = 0;
		if (visibleCharacters.contains(character)) {
			for (Taxon taxon : visibleTaxa) {
				if (hasValue(taxon, character)) {
					result++;
				}
			}
		}
		return result;
	}
	
	private boolean hasValue(Taxon taxon, Character character) {
		Value value = this.getValue(taxon, character);
		return value != null && value.getValue() != null
				&& !value.getValue().trim().isEmpty();
	}

	@Override
	public String toString() {
		String result = "Matrix:\n";
		String hierarchicalTaxa = "";
		for(Taxon taxon : hierarchyTaxa) {
			hierarchicalTaxa += taxon.printHierarchy() + "\n";
		}
		result += "Hierarchical Taxa: \n" + hierarchicalTaxa  + "\n";
		result += "FlatTaxa: " + flatTaxa.toString()  + "\n";
		result += "Visible Taxa: " + visibleTaxa.toString() + "\n";
		result += "Hierarchical Characters: " + hierarchyCharacters.toString()  + "\n";
		result += "FlatCharacters: " + flatCharacters.toString()  + "\n";
		result += "Visible Characters: " + visibleCharacters.toString()  + "\n";
		result += "Organs: " + organs.toString()  + "\n";
		result += "Values: " + values.toString()  + "\n";
		return result;
	}
	
	public void renameOrgan(Organ organ, String name) {
		if(isContained(organ)) {
			nameOrgansMap.remove(organ.getName());
			organ.setName(name);
			nameOrgansMap.put(organ.getName(), organ);
		}
	}

	public Organ getOrCreateOrgan(String name) {
		if(hasOrgan(name))
			return getOrgan(name);
		return createOrgan(name);
	}
	
	private Organ getOrgan(String name) {
		return nameOrgansMap.get(name);
	}
	
	private Organ createOrgan(String name) {
		Organ organ = new Organ(name);
		nameOrgansMap.put(organ.getName(), organ);
		organs.add(organ);
		return organ;
	}

	private boolean hasOrgan(String name) {
		return this.getOrgan(name) != null;
	}

	public List<Value> getValues(Taxon taxon) {
		List<Value> result = new ArrayList<Value>();
		for (Character character : this.getCharacters())
			result.add(this.getValue(taxon, character));
		return result;
	}
}
