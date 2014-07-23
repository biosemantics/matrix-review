package edu.arizona.biosemantics.matrixreview.client.matrix.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.arizona.biosemantics.matrixreview.shared.model.Change;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

/**
 * The memory-friendly version of TaxonMatrix. Contains only the information needed to display in 
 * the CompareVersionsView. 
 *
 */

public class SimpleTaxonMatrix implements Serializable {
	private static final long serialVersionUID = 7082371463207593414L;

	public class TaxaList extends ArrayList<Taxon> implements Serializable {
		private static final long serialVersionUID = 2178928332527792729L;

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
	private Map<Taxon, Map<Character, LinkedList<Change>>> taxonCharacterChanges = new HashMap<Taxon, Map<Character, LinkedList<Change>>>();
	
	public SimpleTaxonMatrix() { }
	
	public SimpleTaxonMatrix(TaxonMatrix copyFrom) {
		this.characters.addAll(copyFrom.getCharacters());
		this.rootTaxa.addAll(copyFrom.getRootTaxa());
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
		
		return lockedAllTaxa && lockedAllCharacters;
	}

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

	public Taxon getTaxon(int index) {
		return list().get(index);
	}
	
	public Taxon getTaxonById(String id){
		for (Taxon t: list()){
			if (t.getId().equals(id))
				return t;
		}
		return null;
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

	
	/*private void initTaxon(Taxon taxon) {
		taxon.init(characters);
		if(!taxonCharacterChanges.containsKey(taxon)) {
			taxonCharacterChanges.put(taxon, new HashMap<Character, LinkedList<Change>>());
			for(Character character : characters)
				taxonCharacterChanges.get(taxon).put(character, new LinkedList<Change>());
		}
	}*/

	public int getIndexOf(Character character) {
		return characters.indexOf(character);
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

	public Character getCharacterById(String id) {
		for (Character character: characters){
			if (character.getId().equals(id)){
				return character;
			}
		}
		return null;
	}
}

