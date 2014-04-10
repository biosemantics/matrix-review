package edu.arizona.biosemantics.matrixreview.client.manager;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.AllAccessListStore;
import com.sencha.gxt.widget.core.client.grid.CharacterColumnConfig;
import com.sencha.gxt.widget.core.client.grid.CharacterColumnConfig.CharacterValueProvider;
import com.sencha.gxt.widget.core.client.grid.CharactersColumnModel;
import com.sencha.gxt.widget.core.client.grid.CharactersGrid;
import com.sencha.gxt.widget.core.client.grid.TaxaColumnConfig;
import com.sencha.gxt.widget.core.client.grid.TaxaColumnModel;
import com.sencha.gxt.widget.core.client.grid.TaxaGrid;

import edu.arizona.biosemantics.matrixreview.client.cells.TaxonCell;
import edu.arizona.biosemantics.matrixreview.client.cells.ValueCell;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class DataManager {

	public class StringValueProvider implements ValueProvider<Taxon, String> {
		
		private CharacterColumnConfig characterColumnConfig;

		public StringValueProvider(CharacterColumnConfig characterColumnConfig) {
			this.characterColumnConfig = characterColumnConfig;
		}
		
		@Override
		public String getValue(Taxon object) {
			return characterColumnConfig.getValueProvider().getValue(object).getValue();
		}

		@Override
		public void setValue(Taxon object, String value) {
			taxonMatrix.setValue(object, characterColumnConfig.getCharacter(), new Value(value));
		}

		@Override
		public String getPath() {
			return characterColumnConfig.getValueProvider().getPath();
		}
	}
	
	public class NumericValueProvider implements ValueProvider<Taxon, Double> {
		
		private CharacterColumnConfig characterColumnConfig;

		public NumericValueProvider(CharacterColumnConfig characterColumnConfig) {
			this.characterColumnConfig = characterColumnConfig;
		}
		
		
		@Override
		public Double getValue(Taxon object) {
			try {
				return Double.valueOf(characterColumnConfig.getValueProvider().getValue(object).getValue());
			} catch (NumberFormatException e) {
				return 0.0;
			}
		}

		@Override
		public void setValue(Taxon object, Double value) {
			taxonMatrix.setValue(object, characterColumnConfig.getCharacter(), new Value(String.valueOf(value)));
		}

		@Override
		public String getPath() {
			return characterColumnConfig.getValueProvider().getPath();
		}
	}
	
	private TaxonMatrix taxonMatrix;
	private AllAccessListStore<Taxon> store;
	private TaxaGrid taxaGrid;
	private CharactersGrid charactersGrid;
	
	private ViewManager viewManager;
	
	private TaxonCell taxonCell;
	private ValueCell valueCell;

	public DataManager(TaxonMatrix taxonMatrix, AllAccessListStore<Taxon> store, TaxaGrid taxaGrid, CharactersGrid charactersGrid, 
			ViewManager viewManager, TaxonCell taxonCell, ValueCell valueCell) {
		this.taxonMatrix = taxonMatrix;
		this.store = store;
		this.taxaGrid = taxaGrid;
		this.charactersGrid = charactersGrid;
		this.viewManager = viewManager;
		this.taxonCell = taxonCell;
		this.valueCell = valueCell;
	}

	public DataManager(TaxonMatrix taxonMatrix, AllAccessListStore<Taxon> store) {
		this.taxonMatrix = taxonMatrix;
		this.store = store;
	}

	public void setTaxaGrid(TaxaGrid taxaGrid) {
		this.taxaGrid = taxaGrid;
	}

	public void setCharactersGrid(CharactersGrid charactersGrid) {
		this.charactersGrid = charactersGrid;
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
	
	public void setTaxonCell(TaxonCell taxonCell) {
		this.taxonCell = taxonCell;
	}

	public void setValueCell(ValueCell valueCell) {
		this.valueCell = valueCell;
	}
	
	public void init() {
		store.addAll(taxonMatrix.getTaxa());
		
		// init charactersGrid
		List<CharacterColumnConfig> characterColumnConfigs = new ArrayList<CharacterColumnConfig>();
		for(Character character : taxonMatrix.getCharacters())
			characterColumnConfigs.add(this.createCharacterColumnConfig(character));
		CharactersColumnModel charactersColumnModel = new CharactersColumnModel(characterColumnConfigs);
		charactersGrid.reconfigure(store, charactersColumnModel);
		
		// init taxaGrid
		// columnConfigs.add(expander);
		List<TaxaColumnConfig> taxaColumnConfigs = new ArrayList<TaxaColumnConfig>();
		taxaColumnConfigs.add(createTaxaColumnConfig());
		TaxaColumnModel taxaColumnModel = new TaxaColumnModel(taxaColumnConfigs);
		taxaGrid.reconfigure(store, taxaColumnModel);
	}
	
	private TaxaColumnConfig createTaxaColumnConfig() {
		TaxaColumnConfig taxaColumnConfig = new TaxaColumnConfig();
		taxaColumnConfig.setCell(taxonCell);
		return taxaColumnConfig;
	}

	private CharacterColumnConfig createCharacterColumnConfig(final Character character) {
		CharacterColumnConfig characterColumnConfig = new CharacterColumnConfig(200, character, viewManager);
		CharacterValueProvider characterValueProvider = (CharacterValueProvider) characterColumnConfig.getValueProvider();
		characterValueProvider.setCharacterColumnConfig(characterColumnConfig);
		characterColumnConfig.setCell(valueCell);
		return characterColumnConfig;
	}

	public void addTaxon(String name) {
		this.addTaxonAfter(store.size() - 1, name);
	}

	public void addTaxonAfter(int rowIndex, Taxon taxon) {
		taxonMatrix.addTaxon(rowIndex + 1, taxon);
		store.add(rowIndex + 1, taxon);
		//editing.addEditor(taxon);
		//this.rowConfigs.put(taxon, new RowConfig<Taxon>(taxon));
		if(viewManager != null)
			viewManager.refreshColumnHeaders();
	}

	public void addTaxonAfter(int rowIndex, String name) {
		Taxon taxon = this.taxonMatrix.addTaxon(rowIndex + 1, name);
		store.add(rowIndex + 1, taxon);
		//editing.addEditor(taxon);
		//this.rowConfigs.put(taxon, new RowConfig<Taxon>(taxon));
		if(viewManager != null)
			viewManager.refreshColumnHeaders();
	}

	public void removeTaxon(Taxon taxon) {
		taxonMatrix.removeTaxon(taxon);
		store.remove(taxon);
		//editing.removeEditor(taxon);
		//this.rowConfigs.remove(taxon);
		if(viewManager != null)
			viewManager.refreshColumnHeaders();
	}

	public void addCharacter(Character character) {
		this.addCharacterAfter(charactersGrid.getColumnModel().getColumnCount() - 1, character);
		/*
		 * taxonMatrix.addCharacter(character); List<ColumnConfig<Taxon, ?>>
		 * columns = new ArrayList<ColumnConfig<Taxon,
		 * ?>>(grid.getColumnModel().getColumns()); ColumnConfig columnConfig =
		 * createCharacterColumnConfig(character); columns.add(columnConfig);
		 * ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);
		 * this.setControlMode(columnConfig, ControlMode.OFF);
		 * this.enableEditing(columnConfig); grid.reconfigure(grid.getStore(),
		 * cm);
		 */
	}

	public void addCharacterAfter(int colIndex, Character character) {
		// taxonMatrix.addCharacter(colIndex - this.taxonNameColumn, character);
		CharactersColumnModel charactersColumnModel = charactersGrid.getColumnModel();
		taxonMatrix.addCharacter(colIndex, character);
		List<CharacterColumnConfig> columns = new ArrayList<CharacterColumnConfig>(charactersColumnModel.getCharacterColumns());
		CharacterColumnConfig columnConfig = createCharacterColumnConfig(character);
		columns.add(colIndex + 1, columnConfig);
		charactersColumnModel = new CharactersColumnModel(columns);

		//this.setControlMode(columnConfig, ControlMode.OFF);

		//this.enableEditing(columnConfig);

		//ValueFilter characterStateFilter = new ValueFilter(columnConfig.getValueProvider());
		//columnConfig.setFilter(characterStateFilter);
		//filters.addFilter(characterStateFilter);

		charactersGrid.reconfigure(store, charactersColumnModel);
	}

	public void removeCharacter(int i) {
		taxonMatrix.removeCharacter(i - 1);
		CharactersColumnModel charactersColumnModel = charactersGrid.getColumnModel();
		List<CharacterColumnConfig> columns = new ArrayList<CharacterColumnConfig>(charactersColumnModel.getCharacterColumns());
		CharacterColumnConfig columnConfig = columns.remove(i);
		// ColumnConfig columnConfig = columns.remove(i + this.taxonNameColumn);
		charactersColumnModel = new CharactersColumnModel(columns);
		//this.disableEditing(columnConfig);
		//this.columnControlMap.remove(columnConfig);
		charactersGrid.reconfigure(store, charactersColumnModel);
	}

	public void deleteColumn(int colIndex) {
		// if(colIndex > this.taxonNameColumn) {
		this.removeCharacter(colIndex);
		// this.removeCharacter(colIndex - this.taxonNameColumn);
		// }
	}

	public void deleteRow(int rowIndex) {
		Taxon taxon = store.get(rowIndex);
		this.removeTaxon(taxon);
	}

	public Character getCharacter(int column) {
		CharacterColumnConfig columnConfig = charactersGrid.getColumnModel().getColumn(column);
		return columnConfig.getCharacter();
	}

	public void renameCharacter(int colIndex, String name, String organ) {
		final Character character = getCharacter(colIndex);
		taxonMatrix.renameCharacter(character, name);
		taxonMatrix.setOrgan(character, organ);
		viewManager.refreshColumnHeader(colIndex);
	}

	public String getCoverage(Taxon taxon) {
		return taxonMatrix.getCoverage(taxon);
	}

	public String getCoverage(Character character) {
		return taxonMatrix.getCoverage(character);
	}

	public String getSummary(Taxon taxon) {
		return "Character coverage: " + getCoverage(taxon);
	}

	public String getSummary(Character character) {
		return "Taxon coverage: " + getCoverage(character);
	}

	public String getQuickTipText(CharacterColumnConfig columnConfig) {
		return this.getSummary(columnConfig.getCharacter());
	}
	
	public String getQuickTipText(TaxaColumnConfig columnConfig) {
		return "The matrix contains " + taxonMatrix.getTaxa().size() + " taxa and " + taxonMatrix.getCharacters().size() + " characters";
	}

	public Value getValue(int row, int column) {
		Taxon taxon = this.getTaxon(row);
		Character character = this.getCharacter(column);
		if (taxon != null && character != null)
			return this.getValue(taxon, character);
		return null;
	}

	public Value getValue(Taxon taxon, Character character) {
		return taxon.get(character);
	}
	
	public int getTaxaCount() {
		return this.store.sizeOfAllItems();
	}

	public int getCharacterCount() {
		return charactersGrid.getColumnModel().getColumnCount();
	}
	

	public Taxon getTaxonFromAll(int indexOfAllItems) {
		return store.getFromAllItems(indexOfAllItems);
	}
	
	public Taxon getVisibleTaxon(int visibleIndex) {
		return store.get(visibleIndex);
	}

	public int getVisibleTaxaCount() {
		return store.size();
	}

	public void moveTaxon(int source, int target) {
		Taxon taxon =  store.remove(source);
		store.add(target, taxon);
	}
	
	public Taxon getTaxon(int row) {
		return store.get(row);
	}


}
