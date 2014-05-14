package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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

	public class TaxaList extends ArrayList<Taxon> {
		
		public TaxaList() {
			for(Taxon taxon : rootTaxa) {
				insertDepthFirst(taxon);
			}
		}
		
		private void insertDepthFirst(Taxon taxon) {
			add(taxon);
			for(Taxon child : taxon.getChildren()) 
				insertDepthFirst(child);
		}
	}
	
	private List<Taxon> rootTaxa = new ArrayList<Taxon>();
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
	
	public List<Taxon> getRootTaxa() {
		return new LinkedList<Taxon>(rootTaxa);
	}
	
	public TaxaList list() {
		return new TaxaList();
	}

	public List<Character> getCharacters() {
		return new LinkedList<Character>(characters);
	}
	
	public List<Color> getColors() {
		return new LinkedList<Color>(colors);
	}

	@Override
	public boolean isLocked() {
		boolean lockedAllTaxa = true;
		for(Taxon taxon : list()) {
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
	
	private boolean noChanges(Taxon taxon, Character character) {
		return taxonCharacterChanges.get(taxon).get(character).isEmpty();
	}
	
	public boolean contains(Taxon taxon) {
		for(Taxon t : list()) {
			if(t.equals(taxon))
				return true;
		}
		return false;
	}
	
	public boolean contains(Character character) {
		return this.characters.contains(character);
	}

	public void setValue(Taxon taxon, Character character, Value value) {
		if(contains(taxon) && contains(character)) {
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

	public Taxon getTaxon(int index) {
		return list().get(index);
	}
	
	public Character getCharacter(int index) {
		return characters.get(index);
	}
	
	public Set<Organ> getOrgans() {
		Set<Organ> organs = new LinkedHashSet<Organ>();
		for(Character character : characters) {
			if(character.hasOrgan())
				organs.add(character.getOrgan());
		}
		return organs;
	}

	public int getCharacterCount() {
		return characters.size();
	}

	public int getTaxaCount() {
		return list().size();
	}

	public void addRootTaxon(int index, Taxon taxon) {
		initTaxon(taxon);
		this.rootTaxa.add(index, taxon);
	}
	
	public void addRootTaxon(int index, List<Taxon> taxa) {
		for(Taxon taxon : taxa) 
			this.addRootTaxon(index++, taxon);
	}
	
	public void moveToRootTaxon(int index, List<Taxon> taxa) {
		for(Taxon taxon : taxa) {
			if(taxon.getParent() != null)
				taxon.getParent().removeChild(taxon);
			taxon.setParent(null);
		}
		this.addRootTaxon(index, taxa);
	}
	
	private void initTaxon(Taxon taxon) {
		taxon.setTaxonMatrix(this);
		taxon.init(characters);
		if(!taxonCharacterChanges.containsKey(taxon)) {
			taxonCharacterChanges.put(taxon, new HashMap<Character, LinkedList<Change>>());
			for(Character character : characters)
				taxonCharacterChanges.get(taxon).put(character, new LinkedList<Change>());
		}
	}
	
	private void cleanupTaxon(Taxon taxon) {
		taxonCharacterChanges.remove(taxon);
	}
	
	public void addRootTaxon(Taxon taxon) {
		this.addRootTaxon(rootTaxa.size(), taxon);
	}
	
	public void addTaxon(Taxon parent, int index, Taxon child) {
		if(!Level.isValidParentChild(parent.getLevel(), child.getLevel())) {
			throw new IllegalArgumentException("Invalid levels");
		}
		
		initTaxon(child);
			
		if(child.getParent() != null)
			child.getParent().removeChild(child);
		rootTaxa.remove(child);
		child.setParent(parent);
		if(parent != null)
			parent.addChild(index, child);
	}
	
	public void addTaxon(Taxon parent, int index, List<Taxon> children) {
		for(Taxon child : children)
			if(!Level.isValidParentChild(parent.getLevel(), child.getLevel())) {
				throw new IllegalArgumentException("Invalid levels");
			}
		
		for(Taxon child : children)
			addTaxon(parent, index++, child);
	}
	
	public void addTaxon(Taxon parent, Taxon child) {
		addTaxon(parent, parent.getChildren().size(), child);
	}

	public void removeTaxon(Taxon taxon) {
		//either should remove if it was contained
		this.rootTaxa.remove(taxon);
		taxon.getParent().removeChild(taxon);
		
		cleanupTaxon(taxon);
	}

	public void modifyTaxon(Taxon taxon, Level level, String name, String author, String year) {
		Taxon parent = taxon.getParent();
		if(!Level.isValidParentChild(parent == null? null : parent.getLevel(), level)) {
			throw new IllegalArgumentException("Invalid levels");
		}
		taxon.setLevel(level);
		taxon.setName(name);
		taxon.setAuthor(author);
		taxon.setYear(year);
	}

	public int getIndexOf(Character character) {
		return characters.indexOf(character);
	}
	
	public void addCharacter(Character character) {
		this.addCharacter(characters.size(), character);
	}
	
	public void addCharacter(int index, Character character) {
		character.setTaxonMatrix(this);
		this.characters.add(index, character);
		for(Taxon taxon : list()) {
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
		for(Taxon taxon : list()) 
			taxon.remove(character);
		for(Taxon taxon : this.taxonCharacterChanges.keySet()) {
			taxonCharacterChanges.get(taxon).remove(character);
		}
	}
	

	public void modifyCharacter(Character character, String name, Organ organ) {
		character.setOrgan(organ);
		character.setName(name);
		//only set dirty based on character value changes for the character. not the character organ itself
		//character.setDirty();
	}
	
	public void moveCharacter(Character character, Character after) {
		this.characters.remove(character);
		if(after == null)
			this.characters.add(0, character);
		else
			this.characters.add(characters.indexOf(after) + 1, character);
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
		for(Taxon taxon : list())
			taxon.get(character).setColor(color);
	}
	
	public void setColor(Value value, Color color) {
		value.setColor(color);
	}
	
	public void setControlMode(Character character, ControlMode controlMode) {
		character.setControlMode(controlMode);
	}
	
	public String getCoverage(Taxon taxon) {
		return this.getTaxonValueCount(taxon) + "/" + characters.size();
	}
	
	public int getTaxonValueCount(Taxon taxon) {
		int result = 0;
		if(list().contains(taxon)) {
			for(Character character : this.characters) {
				if(hasValue(taxon, character)) {
					result++;
				}
			}
		}
		return result;
	}
	
	public String getCoverage(Character character) {
		return this.getCharacterValueCount(character) + "/" + list().size();
	}
	
	public int getCharacterValueCount(Character character) {
		int result = 0;
		if(characters.contains(character)) {
			for(Taxon taxon : list()) {
				if(hasValue(taxon, character)) {
					result++;
				}
			}
		}
		return result;
	}
	
	private boolean hasValue(Taxon taxon, Character character) {
		Value value = taxon.get(character);
		return value != null && value.getValue() != null && !value.getValue().trim().isEmpty();
	}
	
	public void setHidden(Character character, boolean hidden) {
		character.setHidden(hidden);
	}
	
	public void setHidden(Taxon taxon, boolean hidden) {
		taxon.setHidden(hidden);
	}
	
	public void addColor(Color color) {
		colors.add(color);
	}

	public void removeColors(Set<Color> colors) {
		colors.removeAll(colors);
	}
	
	public void setLocked(Taxon taxon, boolean locked) {
		taxon.setLocked(locked);
	}
	
	public void setLocked(Character character, boolean locked) {
		character.setLocked(locked);
	}
	
	public void setLocked(boolean value) {
		for(Taxon taxon : list())
			this.setLocked(taxon, value);
		for(Character character : characters) 
			this.setLocked(character, value);
	}
}
