package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Change so that all changes are to be done through matrix interface.
 * Otherwise problemantic, if Taxon is changed outside and not consistent with characters in matrix
 * @author rodenhausen
 *
 */
public class TaxonMatrix implements Serializable {

	private List<Taxon> taxa = new ArrayList<Taxon>();
	private List<Character> characters = new ArrayList<Character>();

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
	
	public Taxon addTaxon(String name) {
		Taxon result = new Taxon(name, "", characters);
		taxa.add(result);
		return result;
	}
	
	public void addTaxon(Taxon taxon) {
		taxon.init(characters);
		taxa.add(taxon);
	}
	
	public void addTaxon(int index, Taxon taxon) {
		taxon.init(characters);
		taxa.add(index, taxon);
	}
	
	public void addCharacter(Character character) {
		this.characters.add(character);
		for(Taxon taxon : taxa) {
			taxon.addCharacter(character);
		}
	}
	
	public void addCharacter(int index, Character character) {
		this.characters.add(index, character);
		for(Taxon taxon : taxa) {
			taxon.addCharacter(character);
		}
	}

	//TODO, key really ID in order?
	public String getId(Taxon item) {
		return String.valueOf(item.hashCode());
	}

	public void removeCharacter(int i) {
		this.characters.remove(i);
	}

	public void removeCharacter(Character character) {
		this.characters.remove(character);
	}
	
	public void removeTaxon(Taxon taxon) {
		this.taxa.remove(taxon);
	}
	
	public void removeTaxon(int i) {
		this.taxa.remove(i);
	}
		
	public String getCoverage(Taxon taxon) {
		int result = 0;
		if(taxa.contains(taxon)) {
			for(Character character : this.characters) {
				if(hasValue(taxon, character)) {
					result++;
				}
			}
		}
		return result + "/" + characters.size();
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
	
	private boolean hasValue(Taxon taxon, Character character) {
		Value value = taxon.get(character);
		return value != null && value.getValue() != null && !value.getValue().trim().isEmpty();
	}
	
	public String toString() {
		return characters.toString() + " \n " + taxa.toString() + "\n";
	}
	
}
