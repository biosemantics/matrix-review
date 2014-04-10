package edu.arizona.biosemantics.matrixreview.client.manager;

import java.util.List;

import com.sencha.gxt.widget.core.client.grid.CharacterColumnConfig;
import com.sencha.gxt.widget.core.client.grid.CharactersGrid;
import com.sencha.gxt.widget.core.client.grid.TaxaColumnConfig;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class AnnotationManager {

	private DataManager dataManager;
	private TaxonMatrix taxonMatrix;
	private ViewManager viewManager;
	private CharactersGrid charactersGrid;
		
	public AnnotationManager(TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
	}
	
	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}

	public void setCharactersGrid(CharactersGrid charactersGrid) {
		this.charactersGrid = charactersGrid;
	}

	public void setComment(int row, int column, String comment) {
		Taxon taxon = dataManager.getTaxon(row);
		Character character = dataManager.getCharacter(column);
		if (taxon != null && character != null) {
			taxonMatrix.setComment(taxon, character, comment);
			viewManager.refreshCharactersGridView(false);
		}
	}
	
	public void setTaxonComment(int row, String comment) {
		Taxon taxon = dataManager.getTaxon(row);
		if (taxon != null) {
			taxonMatrix.setComment(taxon, comment);
			viewManager.refreshTaxaGridView();
		}
	}
	
	public void setCharacterComment(int column, String comment) {
		Character character = dataManager.getCharacter(column);
		if (character != null) {
			taxonMatrix.setComment(character, comment);
			viewManager.refreshCharacterHeaderHeader(column);
		}
	}

	public String getComment(int row, int column) {
		Taxon taxon = dataManager.getTaxon(row);
		Character character = dataManager.getCharacter(column);
		return this.getComment(taxon, character);
	}

	public String getComment(Taxon taxon, Character character) {
		if (taxon != null && character != null)
			return taxon.get(character).getComment();
		else
			return "";
	}
	
	public String getCharacterComment(int column) {
		Character character = dataManager.getCharacter(column);
		if (character != null)
			return character.getComment();
		return "";
	}
	
	public String getTaxonComment(int row) {
		Taxon taxon = dataManager.getTaxon(row);
		if (taxon != null)
			return taxon.getComment();
		return "";
	}

	public boolean hasComment(Taxon taxon, CharacterColumnConfig characterColumnConfig) {
		return !this.getComment(taxon, characterColumnConfig.getCharacter()).isEmpty();
	}
	
	public boolean hasComment(Taxon taxon, TaxaColumnConfig taxaColumnConfig) {
		return !taxon.getComment().isEmpty();
	}
	
	public boolean hasCharacterComment(int column) {
		return !this.getCharacterComment(column).isEmpty();
	}

	public boolean hasTaxonComment(int row) {
		return !this.getTaxonComment(row).isEmpty();
	}

	public List<Color> getColors() {
		return taxonMatrix.getColors();
	}

	public void setColors(List<Color> colors) {
		taxonMatrix.setColors(colors);
	}

	public void setColor(int row, int column, Color color) {
		Value value = dataManager.getValue(row, column);
		if (value != null) {
			taxonMatrix.setColor(value, color);
			viewManager.refreshCharactersGridView(false);
		}
	}

	public void setTaxonColor(int row, Color color) {
		Taxon taxon = dataManager.getTaxon(row);
		taxonMatrix.setColor(taxon, color);
		viewManager.refreshCharactersGridView(false);
		viewManager.refreshTaxaGridView();
	}

	public void setCharacterColor(int colIndex, Color color) {
		if(charactersGrid != null) {
			CharacterColumnConfig characterColumnConfig = charactersGrid.getColumnModel().getColumn(colIndex);
			taxonMatrix.setColor(characterColumnConfig.getCharacter(), color);
			viewManager.refreshCharactersGridView(true);
			viewManager.refreshTaxaGridView();
		}
	}

}
