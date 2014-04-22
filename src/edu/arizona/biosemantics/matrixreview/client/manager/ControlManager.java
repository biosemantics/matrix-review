package edu.arizona.biosemantics.matrixreview.client.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.MyValidator;
import com.google.gwt.event.logical.shared.TextFieldChangeHandler;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.cell.core.client.form.MyComboBoxCell;
import com.sencha.gxt.data.shared.AllAccessListStore;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.DoublePropertyEditor;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.grid.CharacterColumnConfig;
import com.sencha.gxt.widget.core.client.grid.CharactersGrid;
import com.sencha.gxt.widget.core.client.grid.TaxaColumnConfig.TaxonNameValueProvider;
import com.sencha.gxt.widget.core.client.grid.TaxaGrid;
import com.sencha.gxt.widget.core.client.grid.editing.MyGridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.editing.ValueConverter;
import com.sencha.gxt.widget.core.client.grid.filters.CharactersGridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.grid.filters.TaxaGridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.TaxonNameFilter;
import com.sencha.gxt.widget.core.client.grid.filters.ValueFilter;
import edu.arizona.biosemantics.matrixreview.client.manager.DataManager.StringValueProvider;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class ControlManager {
	
	public enum ControlMode {
		CATEGORICAL, NUMERICAL, OFF
	}	

	private AllAccessListStore<Taxon> store;
	private TaxonMatrix taxonMatrix;
	private CharactersGrid charactersGrid;
	private TaxaGrid taxaGrid;
	
	private DataManager dataManager;
	private ViewManager viewManager;
	
	private MyGridInlineEditing<Taxon> characterEditing;
	private Map<CharacterColumnConfig, ControlMode> characterColumnControlMap = new HashMap<CharacterColumnConfig, ControlMode>();
	private CharactersGridFilters charactersFilters;
	private TaxaGridFilters taxaFilters;
	
	public ControlManager(TaxonMatrix taxonMatrix, AllAccessListStore<Taxon> store) {
		this.store = store;
		this.taxonMatrix = taxonMatrix;
	}
	
	public void setCharactersGrid(CharactersGrid charactersGrid) {
		this.charactersGrid = charactersGrid;
	}
	
	public void setTaxaGrid(TaxaGrid taxaGrid) {
		this.taxaGrid = taxaGrid;
	}

	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
	}

	public void setViewManager(ViewManager viewManager) {
		this.viewManager = viewManager;
	}
	
	/**
	 * Filtering is tied to store internally, so has to be done
	 * after grid is reconfigured with a new store object
	 */
	public void init() {
		// init editing for taxaGrid
		// none right now: Edit/rename is possible via menu, similar to character rename
		
		// init control (none right now for taxaGrid) and filtering for taxaGrid
		taxaFilters = new TaxaGridFilters();
		// StringFilter<Taxon> taxonNameFilter = new StringFilter<Taxon>(new TaxonNameValueProvider());
		TaxonNameFilter taxonNameFilter = new TaxonNameFilter(new TaxonNameValueProvider());
		taxaFilters.setLocal(true);
		taxaFilters.addFilter(taxonNameFilter);	
		taxaFilters.initPlugin(taxaGrid);		
		
		// init editing for charactersGrid
		this.characterEditing = new MyGridInlineEditing<Taxon>(charactersGrid, store);
		this.characterEditing.addCompleteEditHandler(new CompleteEditHandler<Taxon>() {
			@Override
			public void onCompleteEdit(CompleteEditEvent<Taxon> event) {
				int j = event.getEditCell().getCol();
				ControlManager.this.viewManager.refreshCharacterHeaderHeader(j);
			}
		});
		
		// init control and filtering for charactersGrid
		charactersFilters = new CharactersGridFilters();
		charactersFilters.setLocal(true);
		for(CharacterColumnConfig characterColumnConfig : charactersGrid.getColumnModel().getCharacterColumns()) 
			init(characterColumnConfig);
		for(Taxon taxon : taxonMatrix.getTaxa())
			init(taxon);
		charactersFilters.initPlugin(charactersGrid);
	}
	
	public void init(Taxon taxon) {
		this.enableEditing(taxon);
	}

	public void remove(Taxon taxon) {
		characterEditing.removeEditor(taxon);
	}

	public void init(CharacterColumnConfig columnConfig) {
		ValueFilter characterStateFilter = new ValueFilter(columnConfig.getValueProvider());
		columnConfig.setFilter(characterStateFilter);
		charactersFilters.addFilter(characterStateFilter);
		this.setControlMode(columnConfig, ControlMode.OFF);
	}
	
	public void remove(CharacterColumnConfig columnConfig) {
		this.disableEditing(columnConfig);
		this.characterColumnControlMap.remove(columnConfig);
		charactersFilters.removeFilter(columnConfig.getFilter());
	}
	
	public void remove(int col) {
		this.remove(charactersGrid.getColumnModel().getColumn(col));
	}

	public void enableEditing(CharacterColumnConfig characterColumnConfig) {
		switch (this.getControlMode(characterColumnConfig)) {
		case NUMERICAL:
			// TODO add validation to only allow numerical values from there on
			final TextField textField = new TextField();
			textField.setAllowBlank(false);
			textField.addValidator(new Validator<String>() {
				@Override
				public List<EditorError> validate(Editor<String> editor, String value) {
					List<EditorError> result = new LinkedList<EditorError>();
					if (value == null || !value.matches("[0-9]*")) {
						result.add(new DefaultEditorError(editor, "Value not numeric", value));
					}
					return result;
				}
			});
			MyValidator numericalsValidator = new MyValidator() {
				@Override
				public boolean isValid(String value) {
					return value != null && value.matches("[0-9]*");
				}
			};
			TextFieldChangeHandler changeHandler = new TextFieldChangeHandler(numericalsValidator);
			textField.addValueChangeHandler(changeHandler);
			textField.addBeforeShowHandler(changeHandler);
			characterEditing.addEditor(characterColumnConfig, new ValueConverter(), textField);
			break;
		case CATEGORICAL:
			ValueConverter converter = new ValueConverter();
			final Set<String> values = new HashSet<String>();
			for (Taxon taxon : taxonMatrix.getTaxa()) {
				values.add(converter.convertModelValue(characterColumnConfig.getValueProvider().getValue(taxon)));
			}
			final AllAccessListStore<String> comboValues = new AllAccessListStore<String>(new ModelKeyProvider<String>() {
				@Override
				public String getKey(String item) {
					return item;
				}
			});
			List<String> sortValues = new ArrayList<String>(values);
			Collections.sort(sortValues);
			comboValues.addAll(sortValues);

			// http://www.sencha.com/forum/showthread.php?196281-GXT-3-rc2-ComboBox-setForceSelection(false)-does-not-work/page2
			// for ComboBox vs StringComboBox: ComboBox does not allow
			// "new values" -> StringComboBox is made for this use case
			// MyStringComboBox editComboBox = new
			// MyStringComboBox(sortValues);
			ComboBox<String> editComboBox = new ComboBox<String>(new MyComboBoxCell<String>(comboValues, new LabelProvider<String>() {
				@Override
				public String getLabel(String item) {
					return item;
				}
			}));
			editComboBox.addValidator(new Validator<String>() {
				@Override
				public List<EditorError> validate(Editor<String> editor, String value) {
					List<EditorError> result = new LinkedList<EditorError>();
					if (!values.contains(value)) {
						result.add(new DefaultEditorError(editor, "Value entered not part of the character's vocabulary", value));
					}
					return result;
				}
			});
			// editComboBox.setAddUserValues(true);
			// editComboBox.setFinishEditOnEnter(true);
			//editComboBox.setForceSelection(false);
			// editComboBox.setAutoValidate(true);
			// editComboBox.setEditable(true);
			//editComboBox.setTypeAhead(true);
			// editComboBox.setAllowBlank(false);
			// editComboBox.setClearValueOnParseError(false);
			// editComboBox.setForceSelection(true);
			editComboBox.setTriggerAction(TriggerAction.ALL); 
			//upon trigger ("open combo"), a query is constructred that retrievaes all values of the underlying list. 
			// otherwise upon trigger only the selected value will be queried and displayed in the open box
			editComboBox.setForceSelection(false);
			characterEditing.addEditor(characterColumnConfig, new ValueConverter(), editComboBox);
			break;
		default: // same as OFF
			characterEditing.addEditor(characterColumnConfig, new ValueConverter(), new TextField());
			/*} else {
				editing.addEditor(columnConfig, new TaxonConverter(taxonMatrix), new TextField());
			}*/
			break;
		}
	}

	public void disableEditing(CharacterColumnConfig characterColumnConfig) {
		characterEditing.removeEditor(characterColumnConfig);
	}
	
	public void enableEditing(Taxon taxon) {
		characterEditing.addEditor(taxon);
	}

	public void disableEditing(Taxon taxon) {
		characterEditing.removeEditor(taxon);
	}
	
	public void enableEditingAll(boolean activate) {
		List<CharacterColumnConfig> columns = this.charactersGrid.getColumnModel().getCharacterColumns();
		if (activate) {
			for (int i = 0; i < columns.size(); i++) {
				this.enableEditing(columns.get(i));
			}
			for (Taxon taxon : taxonMatrix.getTaxa()) {
				this.enableEditing(taxon);
			}
		} else {
			for (int i = 0; i < columns.size(); i++) {
				this.disableEditing(columns.get(i));
			}
			for (Taxon taxon : taxonMatrix.getTaxa()) {
				this.disableEditing(taxon);
			}
		}
	}

	public boolean isEditableAll() {
		List<CharacterColumnConfig> columns = this.charactersGrid.getColumnModel().getCharacterColumns();
		for (int i = 0; i < columns.size(); i++) {
			if (!this.isEditable(columns.get(i)))
				return false;
		}
		for (Taxon taxon : taxonMatrix.getTaxa()) {
			if (!this.isEditable(taxon))
				return false;
		}
		return true;
	}
	
	public boolean isNotEditableAll() {
		List<CharacterColumnConfig> columns = this.charactersGrid.getColumnModel().getCharacterColumns();
		for (int i = 0; i < columns.size(); i++) {
			if (this.isEditable(columns.get(i)))
				return false;
		}
		for (Taxon taxon : taxonMatrix.getTaxa()) {
			if (this.isEditable(taxon))
				return false;
		}
		return true;
	}

	private boolean isEditable(CharacterColumnConfig characterColumnConfig) {
		return characterEditing.getEditor(characterColumnConfig) != null;
	}

	private boolean isEditable(Taxon taxon) {
		return characterEditing.hasEditor(taxon);
	}


	
	public void setControlMode(int colIndex, ControlMode controlMode) {
		final CharacterColumnConfig characterColumnConfig = charactersGrid.getColumnModel().getCharacterColumns().get(colIndex);
		this.setControlMode(characterColumnConfig, controlMode);
	}
	
	public void setControlMode(CharacterColumnConfig characterColumnConfig, ControlMode controlMode) {
		this.characterColumnControlMap.put(characterColumnConfig, controlMode);
		
		// setup controlled filtering
		switch (controlMode) {
		case CATEGORICAL:
			ValueConverter valueConverter = new ValueConverter();
			//final Set<String> values = new HashSet<String>();
			final Set<String> values = new HashSet<String>();
			for (Taxon taxon : taxonMatrix.getTaxa()) {
				values.add(characterColumnConfig.getValueProvider().getValue(taxon).getValue());
				//values.add(valueConverter.convertModelValue(characterColumnConfig.getValueProvider().getValue(taxon)));
			}
			final AllAccessListStore<String> valueStore = new AllAccessListStore<String>(new ModelKeyProvider<String>() {
				@Override
				public String getKey(String item) {
					return item;
				}
			});
			List<String> sortValues = new ArrayList<String>(values);
			Collections.sort(sortValues, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});
			valueStore.addAll(sortValues);
			charactersFilters.removeFilter(characterColumnConfig.getFilter());
			
			ListFilter<Taxon, String> listFilter = new ListFilter<Taxon, String>(dataManager.new StringValueProvider(characterColumnConfig), valueStore);
			charactersFilters.addFilter(listFilter);
			break;
		case NUMERICAL:
			charactersFilters.removeFilter(characterColumnConfig.getFilter());
			NumericFilter<Taxon, Double> lastFilter = new NumericFilter<Taxon, Double>(dataManager.new NumericValueProvider(characterColumnConfig), new DoublePropertyEditor());
			charactersFilters.addFilter(lastFilter);
			break;
		case OFF:
			charactersFilters.removeFilter(characterColumnConfig.getFilter());
			StringFilter<Taxon> stringFilter = new StringFilter<Taxon>(dataManager.new StringValueProvider(characterColumnConfig));
			charactersFilters.addFilter(stringFilter);
			break;
		}
		
		// refresh editing
		this.enableEditing(characterColumnConfig);
	}
	
	public ControlMode getControlMode(int colIndex) {
		CharacterColumnConfig characterColumnConfig = charactersGrid.getColumnModel().getCharacterColumns().get(colIndex);
		return this.characterColumnControlMap.get(characterColumnConfig);
	}

	private ControlMode getControlMode(CharacterColumnConfig characterColumnConfig) {
		return this.characterColumnControlMap.get(characterColumnConfig);
	}

	public ControlMode determineControlMode(int colIndex) {
		CharacterColumnConfig characterColumnConfig = charactersGrid.getColumnModel().getCharacterColumns().get(colIndex);
		Set<String> values = new HashSet<String>();
		ValueConverter valueConverter = new ValueConverter();
		for (Taxon taxon : taxonMatrix.getTaxa()) {
			values.add(valueConverter.convertModelValue(characterColumnConfig.getValueProvider().getValue(taxon)));
		}
		if (isNumeric(values))
			return ControlMode.NUMERICAL;
		if (values.isEmpty())
			return ControlMode.OFF;
		return ControlMode.CATEGORICAL;
	}
	
	/**
	 * Determine whether the values are probably more numerical. Simplistic
	 * implementation for now
	 * 
	 * @param values
	 * @return
	 */
	private boolean isNumeric(Set<String> values) {
		int numericalCount = 0;
		for (String value : values) {
			if (value.matches("[0-9]*")) {
				numericalCount++;
			}
		}
		return numericalCount > values.size() / 2;
	}
	
	private boolean isControlled(CharacterColumnConfig characterColumnConfig) {
		if (characterColumnControlMap.containsKey(characterColumnConfig))
			return !characterColumnControlMap.get(characterColumnConfig).equals(ControlMode.OFF);
		else
			return false;
	}

	public void setLockedTaxon(int rowIndex, boolean newValue) {
		Taxon taxon = store.get(rowIndex);
		if (newValue) {
			this.disableEditing(taxon);
		} else {
			this.enableEditing(taxon);
		}
	}
	
	public boolean isLockedCharacter(int colIndex) {
		// return false;
		CharacterColumnConfig characterColumnConfig = charactersGrid.getColumnModel().getCharacterColumns().get(colIndex);
		return characterEditing.getEditor(characterColumnConfig) == null;
	}

	public boolean isLockedTaxon(int rowIndex) {
		Taxon taxon = store.get(rowIndex);
		return !characterEditing.hasEditor(taxon);
	}

	public void setLockedCharacter(int colIndex, boolean newValue) {
		CharacterColumnConfig characterColumnConfig = charactersGrid.getColumnModel().getCharacterColumns().get(colIndex);
		if (newValue) {
			this.disableEditing(characterColumnConfig);
		} else {
			this.enableEditing(characterColumnConfig);
		}
	}

}
