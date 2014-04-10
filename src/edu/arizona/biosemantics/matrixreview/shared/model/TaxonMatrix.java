package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * For a correctly functioning model, all changes to it should be made through the interface of this class
 * e.g. assign character values, comment on character values, adding taxa, adding characters etc.
 * However, this cannot always be done as ValueProvider e.g. only provide Character, Taxon etc.
 * Reference to TaxonMatrix inside those?
 * @author rodenhausen
 */
public class TaxonMatrix implements Serializable, HasDirty {

	private List<Taxon> taxa = new ArrayList<Taxon>();
	private List<Character> characters = new ArrayList<Character>();
	private List<Color> colors = new LinkedList<Color>();
	private List<Change> changes = new LinkedList<Change>();
	private Map<Taxon, Map<Character, LinkedList<Change>>> taxonCharacterChanges = new HashMap<Taxon, Map<Character, LinkedList<Change>>>();
	
	public TaxonMatrix() { }
	
	public TaxonMatrix(List<Character> characters) {
		for(Character character : characters) {
			this.characters.add(character);
		}
	}

	public List<Taxon> getTaxa() {
		return taxa;
	}

	public List<Character> getCharacters() {
		return characters;
	}
	
	public List<Color> getColors() {
		return new LinkedList<Color>(colors);
	}
	
	public void setColors(List<Color> colors) {
		this.colors = colors;
	}
	
	public Taxon addTaxon(String name) {
		Taxon result = new Taxon(name, "", characters, this);
		this.addTaxon(result);
		return result;
	}
	
	public void addTaxon(Taxon taxon) {
		taxon.setTaxonMatrix(this);
		taxon.init(characters);
		addTaxon(taxa.size(), taxon);
	}
	
	public Taxon addTaxon(int index, String name) {
		Taxon result = new Taxon(name, "", characters, this);
		this.addTaxon(index, result);
		return result;
	}
	
	public void addTaxon(int index, Taxon taxon) {
		taxon.setTaxonMatrix(this);
		taxon.init(characters);
		taxonCharacterChanges.put(taxon, new HashMap<Character, LinkedList<Change>>());
		for(Character character : characters)
			taxonCharacterChanges.get(taxon).put(character, new LinkedList<Change>());
		taxa.add(index, taxon);
	}
	
	public void addCharacter(Character character) {
		this.addCharacter(characters.size(), character);
	}
	
	public void addCharacter(int index, Character character) {
		this.characters.add(index, character);
		for(Taxon taxon : taxa) {
			taxon.addCharacter(character);
		}
		for(Taxon taxon : this.taxonCharacterChanges.keySet()) {
			taxonCharacterChanges.get(taxon).put(character, new LinkedList<Change>());
		}
	}

	//TODO, key really ID in order?
	public String getId(Taxon item) {
		return String.valueOf(item.hashCode());
	}

	public void removeCharacter(int i) {
		Character character = this.characters.get(i);
		this.removeCharacter(character);
	}

	public void removeCharacter(Character character) {
		this.characters.remove(character);
		for(Taxon taxon : taxa) 
			taxon.remove(character);
		for(Taxon taxon : this.taxonCharacterChanges.keySet()) {
			taxonCharacterChanges.get(taxon).remove(character);
		}
	}
	
	public void removeTaxon(Taxon taxon) {
		this.taxa.remove(taxon);
		this.taxonCharacterChanges.remove(taxon);
	}
	
	public void removeTaxon(int i) {
		Taxon taxon = this.taxa.remove(i);
		this.taxonCharacterChanges.remove(taxon);
	}
	
	private boolean hasValue(Taxon taxon, Character character) {
		Value value = taxon.get(character);
		return value != null && value.getValue() != null && !value.getValue().trim().isEmpty();
	}
		
	public void setValue(Taxon taxon, Character character, Value value) {
		if(this.taxa.contains(taxon) && this.characters.contains(character)) {
			Value oldValue = taxon.get(character);
			if(oldValue != null) {
				String comment = oldValue.getComment();
				Color color = oldValue.getColor();
				value.setComment(comment);
				value.setColor(color);
			}
			taxon.put(character, value);
			
			Change change = new Change(taxon, character, oldValue, value);
			changes.add(change);
			if(!taxonCharacterChanges.containsKey(taxon)) 
				taxonCharacterChanges.put(taxon, new HashMap<Character, LinkedList<Change>>());
			if(!taxonCharacterChanges.get(taxon).containsKey(character)) 
				taxonCharacterChanges.get(taxon).put(character, new LinkedList<Change>());
			taxonCharacterChanges.get(taxon).get(character).add(change);
			
			if(isDirtyValue(taxon, character, value)) {
				value.setDirty();
				character.setDirty();
				taxon.setDirty();
			} else {
				value.clearDirty();
				if(isDirtyCharacter(character)) {
					character.setDirty();
				} else {
					character.clearDirty();
				}
				if(isDirtyTaxon(taxon)) {
					taxon.setDirty();
				} else {
					taxon.clearDirty();
				}
			}
		}
	}

	public void renameTaxon(Taxon taxon, String name) {
		taxon.setName(name);
		//only set dirty based on character value changes for the taxon. not the taxon name itself
		//taxon.setDirty();
	}
	
	public void renameCharacter(Character character, String name) {
		character.setName(name);
		//only set dirty based on character value changes for the character. not the character name itself
		//character.setDirty();
	}
	
	public void setOrgan(Character character, String organ) {
		character.setOrgan(organ);
		//only set dirty based on character value changes for the character. not the character organ itself
		//character.setDirty();
	}
	
	public void setComment(Taxon taxon, String comment) {
		taxon.setComment(comment);
	}
	
	public void setComment(Character character, String comment) {
		character.setComment(comment);
	}
	
	public void setComment(Taxon taxon, Character character, String comment) {
		taxon.get(character).setComment(comment);
	}
	
	public void setColor(Taxon taxon, Color color) {
		taxon.setColor(color);
	}
	
	public void setColor(Character character, Color color) {
		character.setColor(color);
		for(Taxon taxon : taxa)
			taxon.get(character).setColor(color);
	}
	
	public void setColor(Value value, Color color) {
		value.setColor(color);
	}
		
	public String getCoverage(Taxon taxon) {
		return this.getTaxonValueCount(taxon) + "/" + characters.size();
	}
	
	public int getTaxonValueCount(Taxon taxon) {
		int result = 0;
		if(taxa.contains(taxon)) {
			for(Character character : this.characters) {
				if(hasValue(taxon, character)) {
					result++;
				}
			}
		}
		return result;
	}
	
	public String getCoverage(Character character) {
		return this.getCharacterValueCount(character) + "/" + taxa.size();
	}
	
	public int getCharacterValueCount(Character character) {
		int result = 0;
		if(characters.contains(character)) {
			for(Taxon taxon : this.taxa) {
				if(hasValue(taxon, character)) {
					result++;
				}
			}
		}
		return result;
	}
	
	public String toString() {
		return characters.toString() + " \n " + taxa.toString() + "\n";
	}

	private boolean isDirtyCharacter(Character character) {
		for(Taxon taxon : taxonCharacterChanges.keySet()) {
			if(noChanges(taxon, character))
				continue;
			String initialValue = taxonCharacterChanges.get(taxon).get(character).getFirst().getOldValue().getValue();
			String newValue = taxonCharacterChanges.get(taxon).get(character).getLast().getNewValue().getValue();
			if(!initialValue.equals(newValue)) {
				return true;
			}
		}
		return false;
	}

	private boolean isDirtyTaxon(Taxon taxon) {
		for(Character character : taxonCharacterChanges.get(taxon).keySet()) {
			if(noChanges(taxon, character))
				continue;
			String initialValue = taxonCharacterChanges.get(taxon).get(character).getFirst().getOldValue().getValue();
			String newValue = taxonCharacterChanges.get(taxon).get(character).getLast().getNewValue().getValue();
			if(!initialValue.equals(newValue)) {
				return true;
			}
		}
		return false;
	}

	private boolean isDirtyValue(Taxon taxon, Character character, Value value) {
		if(noChanges(taxon, character))
			return false;
		String initialValue = taxonCharacterChanges.get(taxon).get(character).getFirst().getOldValue().getValue();
		String newValue = taxonCharacterChanges.get(taxon).get(character).getLast().getNewValue().getValue();
		if(!initialValue.equals(newValue)) {
			return true;
		}
		return false;
	}
	
	private boolean noChanges(Taxon taxon, Character character) {
		return taxonCharacterChanges.get(taxon).get(character).isEmpty();
	}
	
	@Override
	public boolean isDirty() {
		for(Taxon taxon : taxonCharacterChanges.keySet()) {
			for(Character character : taxonCharacterChanges.get(taxon).keySet()) {
				if(noChanges(taxon, character))
					continue;
				String initialValue = taxonCharacterChanges.get(taxon).get(character).getFirst().getOldValue().getValue();
				String newValue = taxonCharacterChanges.get(taxon).get(character).getLast().getNewValue().getValue();
				if(!initialValue.equals(newValue)) {
					return true;
				}
			}
		}
		return false;
	}

	public void clearChanges() {
		changes = new LinkedList<Change>();
	}






	
}
