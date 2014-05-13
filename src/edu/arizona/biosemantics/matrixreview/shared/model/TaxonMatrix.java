package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.matrixreview.shared.model.Change;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.HasDirty;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon.Level;

/**
 * This model stores according to "hierarchical taxonomy" view. Matrix and Custom view are only temporary 
 * and their orderings etc. are only stored as long as they are loaded in the corresponding treeStore.
 * 
 * For a correctly functioning model, all changes to it should be made through the interface of this class
 * e.g. assign character values, comment on character values, adding taxa, adding characters etc.
 * However, this cannot always be done as ValueProvider e.g. only provide Character, Taxon etc.
 * Reference to TaxonMatrix inside those?
 * @author rodenhausen
 */
public class TaxonMatrix implements Serializable, HasDirty, HasLocked {

	private List<Taxon> taxa = new ArrayList<Taxon>();
	private List<Character> characters = new ArrayList<Character>();
	private List<Color> colors = new LinkedList<Color>();
	private List<Change> changes = new LinkedList<Change>();
	private Map<Taxon, Map<Character, LinkedList<Change>>> taxonCharacterChanges = new HashMap<Taxon, Map<Character, LinkedList<Change>>>();
	
	public TaxonMatrix() { }
	
	public TaxonMatrix(List<Character> characters) {
		this.characters.addAll(characters);
		for(Character character : characters)
			character.setTaxonMatrix(this);
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
	
	public Taxon addTaxon(String id, Level level, String name, String author, String year) {
		Taxon result = new Taxon(id, level, name, author, year, "", characters);
		this.addTaxon(result);
		return result;
	}
	
	public void addTaxon(Taxon taxon) {
		taxon.setTaxonMatrix(this);
		taxon.init(characters);
		addTaxon(taxa.size(), taxon);
	}
	
	public Taxon addTaxon(String id, int index, Level level, String name, String author, String year) {
		Taxon result = new Taxon(id, level, name, author, year, "", characters);
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
		character.setTaxonMatrix(this);
		this.characters.add(index, character);
		for(Taxon taxon : taxa) {
			taxon.addCharacter(character);
		}
		for(Taxon taxon : this.taxonCharacterChanges.keySet()) {
			taxonCharacterChanges.get(taxon).put(character, new LinkedList<Change>());
		}
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
			value.setCharacter(character);
			value.setTaxon(taxon);
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
	
	public void modifyTaxon(Taxon parent, int index, Taxon taxon, Level level, String name, String author, String year) {
		taxon.setLevel(level);
		taxon.setName(name);
		taxon.setAuthor(author);
		taxon.setYear(year);
		
		setChild(parent, index, taxon);
	}

	public void modifyTaxon(Taxon taxon, Taxon parent, Level level, String name, String author, String year) {
		taxon.setLevel(level);
		taxon.setName(name);
		taxon.setAuthor(author);
		taxon.setYear(year);
		
		setChild(parent, taxon);
		//only set dirty based on character value changes for the taxon. not the taxon name itself
		//taxon.setDirty();
	}
	
	public void renameCharacter(Character character, String name) {
		character.setName(name);
		//only set dirty based on character value changes for the character. not the character name itself
		//character.setDirty();
	}
	
	public void setOrgan(Character character, Organ organ) {
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
	
	public void setComment(Value value, String comment) {
		value.setComment(comment);
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
	
	@Override
	public boolean isLocked() {
		boolean lockedAllTaxa = true;
		for(Taxon taxon : taxa) {
			if(!taxon.isLocked())
				lockedAllTaxa = false;
		}
		
		boolean lockedAllCharacters = true;
		for(Character character : characters) {
			if(!character.isLocked())
				lockedAllCharacters = false;
		}
		
		return lockedAllTaxa || lockedAllCharacters;
	}
	
	public void setLocked(Taxon taxon, boolean locked) {
		taxon.setLocked(locked);
	}
	
	public void setLocked(Character character, boolean locked) {
		character.setLocked(locked);
	}

	public void clearChanges() {
		changes = new LinkedList<Change>();
	}
	
	public void setChild(Taxon parent, int index, Taxon child) {
		if(!isValidParentChildRelation(parent, child)) {
			throw new IllegalArgumentException("Invalid levels");
		}
			
		if(child.getParent() != null)
			child.getParent().removeChild(child);
		child.setParent(parent);
		if(parent != null)
			parent.addChild(index, child);
	}
	
	public boolean isValidParentChildRelation(Taxon parent, Taxon child) {
		int parentLevelId = parent == null? -1 : parent.getLevel().getId();
		int childLevelId = child == null? -1 : child.getLevel().getId();
		return parentLevelId <= childLevelId;
	}

	public void setChild(Taxon parent, Taxon child) {
		if(parent == null)
			setChild(parent, 0, child);
		else
			setChild(parent, parent.getChildren().size(), child);
	}
	
	public void addChild(Taxon parent, int index, Taxon child) {
		setChild(parent, index, child);
		addTaxon(child);
	}

	public void addChild(Taxon parent, Taxon child) {
		if(parent == null)
			addChild(parent, 0, child);
		else
			addChild(parent, parent.getChildren().size(), child);
	}

	public int getCharacterCount() {
		return characters.size();
	}

	public int getTaxaCount() {
		return taxa.size();
	}
	
	public void setHidden(Character character, boolean hidden) {
		character.setHidden(hidden);
	}
	
	public void setHidden(Taxon taxon, boolean hidden) {
		taxon.setHidden(hidden);
	}

	public int getIndexOf(Character character) {
		return characters.indexOf(character);
	}
	
	public int getIndexOf(Taxon taxon) {
		return taxa.indexOf(taxon);
	}

	public Taxon getTaxon(int index) {
		return taxa.get(index);
	}
	
	public Character getCharacter(int index) {
		return characters.get(index);
	}

	public void setLocked(boolean value) {
		for(Taxon taxon : taxa)
			this.setLocked(taxon, value);
		for(Character character : characters) 
			this.setLocked(character, value);
	}

	public void moveCharacter(Character character, Character after) {
		this.characters.remove(character);
		if(after == null)
			this.characters.add(0, character);
		else
			this.characters.add(characters.indexOf(after) + 1, character);
	}

	public void moveTaxon(Taxon taxon, Taxon after, Taxon aftersParent) {
		this.taxa.remove(taxon);
		if(aftersParent != null && taxon.hasParent())
			taxon.getParent().getChildren().remove(taxon);
		
		if(aftersParent == null) {
			if(after == null)
				this.taxa.add(0, taxon);
			else 
				this.taxa.add(taxa.indexOf(after) + 1, taxon);
		} else {
			List<Taxon> children = aftersParent.getChildren();
			if(after == null) {
				this.taxa.add(taxa.indexOf(aftersParent) + 1, taxon);
				children.add(0, taxon);
			} else {
				this.taxa.add(taxa.indexOf(after) + 1, taxon);
				children.add(children.indexOf(after), taxon);
			}
		}
	}

	public void setControlMode(Character character, ControlMode controlMode) {
		character.setControlMode(controlMode);
	}

	public void addColor(Color color) {
		colors.add(color);
	}

	public void removeColors(Set<Color> colors) {
		colors.removeAll(colors);
	}
	
	public Set<Organ> getOrgans() {
		Set<Organ> organs = new LinkedHashSet<Organ>();
		for(Character character : characters) {
			if(character.hasOrgan())
				organs.add(character.getOrgan());
		}
		return organs;
	}
	
}
