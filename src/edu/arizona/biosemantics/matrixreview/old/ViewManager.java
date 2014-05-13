package edu.arizona.biosemantics.matrixreview.old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.CharacterColumnConfig;
import com.sencha.gxt.widget.core.client.grid.CharacterColumnHeader;
import com.sencha.gxt.widget.core.client.grid.CharacterColumnHeader.CharacterHead;
import com.sencha.gxt.widget.core.client.grid.CharactersColumnModel;
import com.sencha.gxt.widget.core.client.grid.CharactersGrid;
import com.sencha.gxt.widget.core.client.grid.TaxaGrid;

import edu.arizona.biosemantics.matrixreview.client.matrix.filters.HideTaxonStoreFilter;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.AllAccessListStore;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class ViewManager {
	
	private TaxaGrid taxaGrid;
	private CharactersGrid charactersGrid;

	private TaxonMatrix taxonMatrix;
	private AllAccessListStore<Taxon> store;
	private DataManager dataManager;
	private AnnotationManager annotationManager;
	
	private HideTaxonStoreFilter hideTaxonFilter = new HideTaxonStoreFilter();

	public ViewManager(TaxonMatrix taxonMatrix,  AllAccessListStore<Taxon> store) {
		this.store = store;
		this.store.addFilter(this.hideTaxonFilter);
		this.taxonMatrix = taxonMatrix;
	}

	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}
	
	public void setAnnotationManager(AnnotationManager annotationManager) {
		this.annotationManager = annotationManager;
	}

	public void setTaxaGrid(TaxaGrid taxaGrid) {
		this.taxaGrid = taxaGrid;
	}

	public void setCharactersGrid(CharactersGrid charactersGrid) {
		this.charactersGrid = charactersGrid;
	}

	public void refreshCharacterHeaderHeader(int column) {
		CharacterColumnConfig characterColumnConfig = charactersGrid.getColumnModel().getColumn(column);
		Character character = characterColumnConfig.getCharacter();
		if (!characterColumnConfig.isHidden()) {
			CharacterColumnHeader characterColumnHeader = charactersGrid.getView().getHeader();
			if (characterColumnHeader != null) {
				CharacterHead h = characterColumnHeader.getHead(column);
				if (h != null && h.isRendered() && h instanceof CharacterHead) {
					CharacterHead myHead = (CharacterHead) h;
					myHead.setText(character.toString());
					myHead.setCoverage(dataManager.getCoverage(character));
					myHead.setQuickTipText(dataManager.getSummary(character));
					myHead.setCommented(annotationManager.hasCharacterComment(column));
					myHead.setBackgroundColor(character.getColor());
					myHead.setDirty(character.isDirty());
				}
			}
		}
	}

	public void refreshCharacterHeader(CharacterColumnConfig toRefresh) {
		CharactersColumnModel charactersColumnModel = charactersGrid.getColumnModel();
		for (int j = 0; j < charactersColumnModel.getColumnCount(); j++) {
			CharacterColumnConfig characterColumnConfig = charactersColumnModel.getColumn(j);
			if (characterColumnConfig.equals(toRefresh)) {
				refreshCharacterHeaderHeader(j);
			}
		}
	}

	public void refreshCharacterHeaders() {
		for (int j = 0; j < charactersGrid.getColumnModel().getColumnCount(); j++) {
			refreshCharacterHeaderHeader(j);
		}
	}
	
	public void refreshCharactersGridView(boolean headersToo) {
		charactersGrid.getView().refresh(headersToo);
	}

	public void refreshTaxaGridView() {
		taxaGrid.getView().refresh(false);
	}

	public void sortTaxa(Comparator<Taxon> comparator) {
		store.clearSortInfo();
		store.addSortInfo(new StoreSortInfo<Taxon>(comparator, SortDir.ASC));
	}

	public void sortTaxaByCoverage(final boolean ascending) {
		Comparator<Taxon> comparator = new Comparator<Taxon>() {
			@Override
			public int compare(Taxon o1, Taxon o2) {
				if (ascending)
					return taxonMatrix.getTaxonValueCount(o1) - taxonMatrix.getTaxonValueCount(o2);
				else
					return taxonMatrix.getTaxonValueCount(o2) - taxonMatrix.getTaxonValueCount(o1);
			}
		};
		this.sortTaxa(comparator);
	}

	public void sortTaxaByName(final boolean ascending) {
		Comparator<Taxon> comparator = new Comparator<Taxon>() {
			@Override
			public int compare(Taxon o1, Taxon o2) {
				if (ascending)
					return o1.getName().compareTo(o2.getName());
				else
					return o2.getName().compareTo(o1.getName());
			}
		};
		this.sortTaxa(comparator);
	}

	public void sortCharacters(Comparator<CharacterColumnConfig> comparator) {
		List<CharacterColumnConfig> characterColumnConfigs = new ArrayList<CharacterColumnConfig>(charactersGrid.getColumnModel().getCharacterColumns());
		Collections.sort(characterColumnConfigs, comparator);
		CharactersColumnModel columnModel = new CharactersColumnModel(characterColumnConfigs);
		charactersGrid.reconfigure(store, columnModel);
	}

	public void sortCharactersByCoverage(final boolean ascending) {
		Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
			@Override
			public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
				if (ascending)
					return taxonMatrix.getCharacterValueCount(o1.getCharacter()) - taxonMatrix.getCharacterValueCount(o2.getCharacter());
				else
					return taxonMatrix.getCharacterValueCount(o2.getCharacter()) - taxonMatrix.getCharacterValueCount(o1.getCharacter());
			}
		};
		this.sortCharacters(comparator);
	}

	public void sortCharactersByName(final boolean ascending) {
		Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
			@Override
			public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
				if (ascending)
					// first by name then by organ
					return (o1.getCharacter().getName() + o1.getCharacter().getOrgan()).compareTo(o2.getCharacter().getName() + o2.getCharacter().getOrgan());
				else
					return (o2.getCharacter().getName() + o2.getCharacter().getOrgan()).compareTo(o1.getCharacter().getName() + o1.getCharacter().getOrgan());
			}
		};
		this.sortCharacters(comparator);
	}

	public void sortCharactersByOrgan(final boolean ascending) {
		Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
			@Override
			public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
				if (ascending)
					// first by organ then by character name
					return (o1.getCharacter().getOrgan() + o1.getCharacter().getName()).compareTo(o2.getCharacter().getOrgan() + o2.getCharacter().getName());
				else
					return (o2.getCharacter().getOrgan() + o2.getCharacter().getName()).compareTo(o1.getCharacter().getOrgan() + o1.getCharacter().getName());
			}
		};
		this.sortCharacters(comparator);
	}

	
	public boolean isHiddenTaxon(int indexOfAllItems) {
		Taxon taxon = store.getFromAllItems(indexOfAllItems);
		return this.hideTaxonFilter.isHidden(taxon);
	}

	public void setHiddenTaxon(int indexOfAllItems, boolean value) {
		Taxon taxon = store.getFromAllItems(indexOfAllItems);
		if (value) {
			this.hideTaxonFilter.addHiddenTaxa(taxon);
		} else {
			this.hideTaxonFilter.removeHiddenTaxa(taxon);
		}
		store.enableAndRefreshFilters();
	}

}
