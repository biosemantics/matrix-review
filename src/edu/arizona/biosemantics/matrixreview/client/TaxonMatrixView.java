package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.cell.core.client.form.MyComboBoxCell;
import com.sencha.gxt.cell.core.client.form.MyValidator;
import com.sencha.gxt.cell.core.client.form.TextFieldChangeHandler;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.MyListStore;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.MyGridDragSource;
import com.sencha.gxt.dnd.core.client.MyGridDropTarget;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.Head;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.MyColumnConfig;
import com.sencha.gxt.widget.core.client.grid.MyColumnHeader.MyHead;
import com.sencha.gxt.widget.core.client.grid.MyGrid;
import com.sencha.gxt.widget.core.client.grid.RowConfig;
import com.sencha.gxt.widget.core.client.grid.RowExpander;
import com.sencha.gxt.widget.core.client.grid.editing.MyGridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.HideTaxonFilter;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.grid.filters.TaxonNameFilter;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.DoublePropertyEditor;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;


public class TaxonMatrixView implements IsWidget {
	
	private FlowLayoutContainer container = new FlowLayoutContainer();
	
	private TaxonMatrix taxonMatrix;
	private MyListStore<Taxon> store;
	private MyGrid grid;
	private Map<RowConfig, Map<ColumnConfig, String>> comments = new HashMap<RowConfig, Map<ColumnConfig, String>>();
	private Map<Taxon, RowConfig<Taxon>> rowConfigs = new HashMap<Taxon, RowConfig<Taxon>>();
	private RowConfig<String> headerRowConfig = new RowConfig<String>("header");
	
	private MyGridInlineEditing<Taxon> editing;
	private Map<ColumnConfig, ControlMode> columnControlMap = new HashMap<ColumnConfig, ControlMode>();
	private RowExpander<Taxon> expander;
	private HideTaxonFilter hideTaxonFilter = new HideTaxonFilter();
	private QuickTip quickTip;
	private GridFilters<Taxon> filters;
	
	private int taxonNameColumn = 1;
	private int firstCharacterColumn = 2;

	public TaxonMatrixView() {
		this.grid = createGrid();
	}
	
	private class TaxonModelKeyProvider implements ModelKeyProvider<Taxon> {
		@Override
		public String getKey(Taxon item) {
			return taxonMatrix.getId(item);
		}
	}
	
	public void init(final TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
		this.store = new MyListStore<Taxon>(new TaxonModelKeyProvider());
		store.addFilter(this.hideTaxonFilter);
		//yes or no? store.setAutoCommit(false);
		store.setAutoCommit(true);
		
		for (Taxon taxon : taxonMatrix.getTaxa()) {
			store.add(taxon);
		}
		
		List<ColumnConfig<Taxon, ?>> columnConfigs = new ArrayList<ColumnConfig<Taxon, ?>>();
		columnConfigs.add(expander);
		ColumnConfig nameColumnConfig = createNameColumnConfig();
		columnConfigs.add(nameColumnConfig);
		for (final Character character : taxonMatrix.getCharacters()) {
			ColumnConfig characterColumnConfig = createCharacterColumnConfig(character);
			columnConfigs.add(characterColumnConfig);
		}
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columnConfigs);
		
		rowConfigs.clear();
		for(Taxon taxon : taxonMatrix.getTaxa()) {
			rowConfigs.put(taxon, new RowConfig<Taxon>(taxon));
		}
		grid.reconfigure(store, cm);
		
		//set up editing
		editing = new MyGridInlineEditing<Taxon>(grid, store);
		ColumnConfig columnConfig = columnConfigs.get(this.taxonNameColumn);
		setControlMode(columnConfig, ControlMode.OFF);
		editing.addEditor(columnConfig, new Converter<Taxon, String>() {
			@Override
			public Taxon convertFieldValue(String object) {
				return new Taxon(object);
			}
			@Override
			public String convertModelValue(Taxon object) {
				return object.getName();
			}
		}, new TextField());
		
		for (int i = firstCharacterColumn; i<columnConfigs.size(); i++) {
			final int theI = i;
			columnConfig = columnConfigs.get(i);
			this.setControlMode(columnConfig, ControlMode.OFF);
			this.enableEditing(columnConfig);
			if(columnConfig instanceof MyColumnConfig) {
				editing.addCompleteEditHandler(new CompleteEditHandler<Taxon>() {
					@Override
					public void onCompleteEdit(CompleteEditEvent<Taxon> event) {
						int j = event.getEditCell().getCol();
						if(theI ==j) {
							refreshColumnHeader(j);
						}
					}
				});
			}
		}
		
		for(Taxon taxon : taxonMatrix.getTaxa())
			editing.addEditor(taxon);
				
		// set up filtering (tied to the store internally, so has to be done after grid is reconfigured with new store object)
		filters = new GridFilters<Taxon>();
		filters.setLocal(true);
		//StringFilter<Taxon> taxonNameFilter = new StringFilter<Taxon>(new TaxonNameValueProvider());
		TaxonNameFilter taxonNameFilter = new TaxonNameFilter(new TaxonNameValueProvider());
		filters.addFilter(taxonNameFilter);
		for (int i = this.firstCharacterColumn; i<columnConfigs.size(); i++) {
			MyColumnConfig config = (MyColumnConfig)columnConfigs.get(i);
			StringFilter<Taxon> characterStateFilter = new StringFilter<Taxon>(config.getValueProvider());
			config.setFilter(characterStateFilter);
			filters.addFilter(characterStateFilter);
		}
		filters.initPlugin(grid);
		
		/*Filter<Taxon, String> taxonConceptFilter = new Filter<Taxon, String>(
				nameValueProvider) {
			protected TextField field;
		
			@Override
			public List<FilterConfig> getFilterConfig() {
				FilterConfig cfg = createNewFilterConfig();
				cfg.setType("string");
				cfg.setComparison("contains");
				String valueToSend = field.getCurrentValue();
				cfg.setValue(getHandler().convertToString(valueToSend));
				return Arrays.asList(cfg);
			}
		
			@Override
			public Object getValue() {
				return field.getCurrentValue();
			}
		
			@Override
			protected Class<String> getType() {
				return String.class;
			}
		};
		filters.addFilter();*/
	}

	private MyGrid createGrid() {
		MyGrid grid = new MyGrid(new ListStore<Taxon>(new TaxonModelKeyProvider()), 
				new ColumnModel<Taxon>(new ArrayList<ColumnConfig<Taxon, ?>>()), this);
		grid.getView().setForceFit(false); // if change in column width we want the table to become wider not stay fixed at overall width
		grid.setColumnReordering(true);
		
		// setup up quicktips
		quickTip = new QuickTip(grid);
		
		//set up row drag and drop for taxon move
		MyGridDragSource<Taxon> dragSource = new MyGridDragSource<Taxon>(grid);
		MyGridDropTarget<Taxon> target = new MyGridDropTarget<Taxon>(grid, container);
		target.setFeedback(Feedback.INSERT);
		target.setAllowSelfAsSource(true);

		// set up row expander
		expander = new RowExpander<Taxon>(new IdentityValueProvider<Taxon>() {
			  @Override
			  public void setValue(Taxon object, Taxon value) {
			  }
			  @Override
			  public Taxon getValue(Taxon object) {
			    return object;
			  }
			  @Override
			  public String getPath() {
			    return "";
			  }
		}, new AbstractCell<Taxon>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Taxon value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant(value.getDescription());
			}
		});
		expander.initPlugin(grid);
		
		//possibly bring up edit menu for row?
		/*grid.addRowClickHandler(new RowClickHandler() {
			@Override
			public void onRowClick(RowClickEvent event) {
				System.out.println("click");
				event.getEvent().preventDefault();
			}
		}); */
		
		return grid;
	}
	
	public void addTaxon(Taxon taxon) {
		this.addTaxonAfter(grid.getStore().size() - 1, taxon);
	}

	public void addTaxonAfter(int rowIndex, Taxon taxon) {
		this.taxonMatrix.addTaxon(rowIndex + 1, taxon);
		grid.getStore().add(rowIndex + 1, taxon);
		editing.addEditor(taxon);
		this.rowConfigs.put(taxon, new RowConfig<Taxon>(taxon));
		refreshColumnHeaders();
		System.out.println(taxonMatrix);
	}

	public void removeTaxon(Taxon taxon) {
		this.taxonMatrix.removeTaxon(taxon);
		grid.getStore().remove(taxon);
		editing.removeEditor(taxon);
		this.rowConfigs.remove(taxon);
		refreshColumnHeaders();
	}
	
	public void addCharacter(Character character) {
		this.addCharacterAfter(grid.getColumnModel().getColumnCount() - 1, character);
		/*
		taxonMatrix.addCharacter(character);
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(grid.getColumnModel().getColumns());
		ColumnConfig columnConfig = createCharacterColumnConfig(character);
		columns.add(columnConfig);
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);
		this.setControlMode(columnConfig, ControlMode.OFF);
		this.enableEditing(columnConfig);	
		grid.reconfigure(grid.getStore(), cm);*/
	}
	
	public void addCharacterAfter(int colIndex, Character character) {
		taxonMatrix.addCharacter(colIndex - this.taxonNameColumn, character);
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(grid.getColumnModel().getColumns());
		ColumnConfig columnConfig = createCharacterColumnConfig(character);
		columns.add(colIndex + 1, columnConfig);
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);
		this.setControlMode(columnConfig, ControlMode.OFF);
		this.enableEditing(columnConfig);	
		grid.reconfigure(grid.getStore(), cm);
		System.out.println(taxonMatrix);
	}	
	
	public void removeCharacter(int i) {
		taxonMatrix.removeCharacter(i - 1);
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(grid.getColumnModel().getColumns());
		ColumnConfig columnConfig = columns.remove(i + this.taxonNameColumn);
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);
		this.disableEditing(columnConfig);
		this.columnControlMap.remove(columnConfig);
		grid.reconfigure(grid.getStore(), cm);
	}

	
	@Override
	public Widget asWidget() {
		container.setScrollMode(ScrollMode.AUTO);
		VerticalPanel panel = new VerticalPanel();
		panel.add(grid);
		container.add(panel);
		return container;
	}

	private ColumnConfig<Taxon, Taxon> createNameColumnConfig() {
		ColumnConfig<Taxon, Taxon> nameCol = new ColumnConfig<Taxon, Taxon>(
			new TaxonNameValueProvider(), 200, "Taxon Concept / Character");
		nameCol.setCell(new TaxonCell(grid, this));
		return nameCol;
	}
	
	private ColumnConfig<Taxon, String> createCharacterColumnConfig(final Character character) {
		ColumnConfig<Taxon, String> characterCol = new MyColumnConfig(200, character);
		characterCol.setCell(new MenuExtendedCell<String>(this));
		return characterCol;
	}

	public void deleteColumn(int colIndex) {
		if(colIndex > this.taxonNameColumn) {
			this.removeCharacter(colIndex - this.taxonNameColumn);
		}
	}
	
	public void deleteRow(int rowIndex) {
		Taxon taxon = store.get(rowIndex);
		this.removeTaxon(taxon);
	}

	public void toggleEditing(int colIndex) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumns().get(colIndex);
		Field<?> field = editing.getEditor(columnConfig);
		if(editing.getEditor(columnConfig) != null) {
			this.disableEditing(columnConfig);
		} else {
			this.enableEditing(columnConfig);
		}
	}
	
	public void enableEditing(ColumnConfig columnConfig) {
		switch(this.getControlMode(columnConfig)) {
		case OFF:
			editing.addEditor(columnConfig, new TextField());
			break;
		case NUMERICAL:
			//TODO add validation to only allow numerical values from there on
			final TextField textField = new TextField();
			textField.setAllowBlank(false);
			textField.addValidator(new Validator<String>() {
				@Override
				public List<EditorError> validate(Editor<String> editor, String value) {
					List<EditorError> result = new LinkedList<EditorError>();
					if(value == null || !value.matches("[0-9]*")) {
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
			editing.addEditor(columnConfig, textField);
			break;
		case CATEGORICAL:
			final Set<String> values = new HashSet<String>();
			for(Taxon taxon : taxonMatrix.getTaxa()) {
				values.add((String)columnConfig.getValueProvider().getValue(taxon));
			}
			final MyListStore<String> comboValues = new MyListStore<String>(
					new ModelKeyProvider<String>() {
						@Override
						public String getKey(String item) {
							return item;
						}
					});
			List<String> sortValues = new ArrayList<String>(values);
			Collections.sort(sortValues);
			comboValues.addAll(sortValues);
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
					if(!values.contains(value)) {
						result.add(new DefaultEditorError(editor, "Value entered not part of the character's vocabulary", value));
					}
					return result;
				}
			});
			//editComboBox.setAutoValidate(true);
			editComboBox.setEditable(true);
			editComboBox.setTypeAhead(true);
			//editComboBox.setAllowBlank(false);
			//editComboBox.setClearValueOnParseError(false);
			editComboBox.setForceSelection(true);
			editComboBox.setTriggerAction(TriggerAction.ALL);
			editing.addEditor(columnConfig, editComboBox);
			break;
		default:
			editing.addEditor(columnConfig, new TextField());
			break;
		}	
	}


	/**
	 * Determine whether the values are probably more numerical. Simplistic implementation for now
	 * @param values
	 * @return
	 */
	private boolean isNumeric(Set<String> values) {
		int numericalCount = 0;
		for(String value : values) {
			if(value.matches("[0-9]*")) {
				numericalCount++;
			}
		}
		return numericalCount > values.size() / 2;
	}

	private boolean isControlled(ColumnConfig columnConfig) {
		if(columnControlMap.containsKey(columnConfig))
			return !columnControlMap.get(columnConfig).equals(ControlMode.OFF);
		else
			return false;
	}

	public void disableEditing(ColumnConfig columnConfig) {
		editing.removeEditor(columnConfig);
	}

	public Container getContainer() {
		return container;
	}
	/*
	 * // final ListLoader<ListLoadConfig, ListLoadResult<Taxon>> loader = new
		// ListLoader<ListLoadConfig, ListLoadResult<Taxon>>(
		// proxy, reader);
		// loader.useLoadConfig(XmlAutoBeanFactory.instance.create(ListLoadConfig.class).as());
		// /loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig,
		// Taxon, ListLoadResult<Taxon>>(store));
	 */

	public void setControlMode(int colIndex, ControlMode controlMode) {
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(grid.getColumnModel().getColumns());
		ColumnConfig columnConfig = columns.remove(colIndex);
		final MyColumnConfig myColumnConfig = (MyColumnConfig)columnConfig;
		
		// setup controlled filtering
		switch(controlMode) {
		case CATEGORICAL:
			final Set<String> values = new HashSet<String>();
			for(Taxon taxon : taxonMatrix.getTaxa()) {
				values.add((String)columnConfig.getValueProvider().getValue(taxon));
			}
			final MyListStore<String> valueStore = new MyListStore<String>(
					new ModelKeyProvider<String>() {
						@Override
						public String getKey(String item) {
							return item;
						}
					});
			List<String> sortValues = new ArrayList<String>(values);
			Collections.sort(sortValues);
			valueStore.addAll(sortValues);
			filters.removeFilter(myColumnConfig.getFilter());
			ListFilter<Taxon, String> listFilter = new ListFilter<Taxon, String>(columnConfig.getValueProvider(), valueStore);
			filters.addFilter(listFilter);
			break;
		case NUMERICAL:
			filters.removeFilter(myColumnConfig.getFilter());
			NumericFilter<Taxon, Double> lastFilter = new NumericFilter<Taxon, Double>(new ValueProvider<Taxon, Double>() {
				@Override
				public Double getValue(Taxon object) {
					return Double.valueOf(object.get(myColumnConfig.getCharacter()).getValue());
				}
				@Override
				public void setValue(Taxon object, Double value) {
					object.get(myColumnConfig.getCharacter()).setValue(String.valueOf(value));
				}
				@Override
				public String getPath() {
					return "/" + myColumnConfig.getCharacter().toString() + "/value";
				}
				}, new DoublePropertyEditor());
			filters.addFilter(lastFilter);
			break;
		case OFF:
			filters.removeFilter(myColumnConfig.getFilter());
			StringFilter<Taxon> stringFilter = new StringFilter<Taxon>(columnConfig.getValueProvider());
			filters.addFilter(stringFilter);
			break;
		}
		
		this.setControlMode(columnConfig, controlMode);
	}
	
	public void setControlMode(ColumnConfig columnConfig, ControlMode controlMode) {
		this.columnControlMap.put(columnConfig, controlMode);
		//refresh editing
		this.enableEditing(columnConfig);
	}

	public boolean isControlled(int colIndex) {
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(grid.getColumnModel().getColumns());
		ColumnConfig columnConfig = columns.remove(colIndex);
		return this.isControlled(columnConfig);
	}

	public boolean isLockedColumn(int colIndex) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumns().get(colIndex);
		return editing.getEditor(columnConfig) == null;
	}
	
	public boolean isLockedRow(int rowIndex) {
		Taxon taxon = store.get(rowIndex);
		return !editing.hasEditor(taxon);
	}

	public void setLockedRow(int rowIndex, boolean newValue) {
		Taxon taxon = store.get(rowIndex);
		if(newValue) {
			this.disableEditing(taxon);
		} else {
			this.enableEditing(taxon);
		}
	}
	
	private void enableEditing(Taxon taxon) {
		editing.addEditor(taxon);
	}

	private void disableEditing(Taxon taxon) {
		editing.removeEditor(taxon);
	}

	public void setLockedColumn(int colIndex, boolean newValue) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumns().get(colIndex);
		if(newValue) {
			this.disableEditing(columnConfig);
		} else {
			this.enableEditing(columnConfig);
		}
	}

	public ControlMode getControlMode(int colIndex) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumns().get(colIndex);
		return this.columnControlMap.get(columnConfig);
	}
	
	
	private ControlMode getControlMode(ColumnConfig columnConfig) {
		return this.columnControlMap.get(columnConfig);
	}
	
	public ControlMode determineControlMode(int colIndex) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumns().get(colIndex);
		Set<String> values = new HashSet<String>();
		for(Taxon taxon : taxonMatrix.getTaxa()) {
			values.add((String)columnConfig.getValueProvider().getValue(taxon));
		}
		if(isNumeric(values))
			return ControlMode.NUMERICAL;
		if(values.isEmpty())
			return ControlMode.OFF;
		return ControlMode.CATEGORICAL;
	}

	public int getTaxaCount() {
		return this.store.sizeOfAllItems();
	}
	
	public int getCharacterCount() {
		return this.grid.getColumnModel().getColumnCount();
	}

	public boolean isHiddenTaxon(int indexOfAllItems) {
		Taxon taxon = store.getFromAllItems(indexOfAllItems);
		return this.hideTaxonFilter.isHidden(taxon);
	}

	public void setHiddenTaxon(int indexOfAllItems, boolean value) {
		Taxon taxon = store.getFromAllItems(indexOfAllItems);
		if(value) {
			this.hideTaxonFilter.addHiddenTaxa(taxon);
		} else {
			this.hideTaxonFilter.removeHiddenTaxa(taxon);
		}
		store.enableAndRefreshFilters();
	}

	public Taxon getVisibleTaxon(int visibleIndex) {
		return store.get(visibleIndex);
	}

	public int getVisibleTaxaCount() {
		return store.size();
	}

	public Taxon getTaxonFromAll(int indexOfAllItems) {
		return store.getFromAllItems(indexOfAllItems);
	}
	
	public Taxon getTaxon(int row) {
		return store.get(row);
	}
	
	public Character getCharacter(int column) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumn(column);
		if(columnConfig instanceof MyColumnConfig) 
			return ((MyColumnConfig)columnConfig).getCharacter();
		return null;
	}
	
	public void setComment(RowConfig rowConfig, ColumnConfig columnConfig, String comment) {
		if(!comments.containsKey(rowConfig))
			comments.put(rowConfig, new HashMap<ColumnConfig, String>());
		comments.get(rowConfig).put(columnConfig, comment);
	}
	
	public String getComment(RowConfig rowConfig, ColumnConfig columnConfig) {
		if(comments.containsKey(rowConfig) && comments.get(rowConfig).containsKey(columnConfig))
			return comments.get(rowConfig).get(columnConfig);
		return "";
	}
	
	public void setComment(int row, int column, String comment) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumn(column);
		RowConfig rowConfig = this.rowConfigs.get(store.get(row));
		this.setComment(rowConfig, columnConfig, comment);
	}
	
	public String getComment(int row, int column) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumn(column);
		RowConfig rowConfig = this.rowConfigs.get(store.get(row));
		return this.getComment(rowConfig, columnConfig);
	}
	
	public String getComment(int column) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumn(column);
		return this.getComment(headerRowConfig, columnConfig);
	}

	public void setComment(int column, String comment) {
		ColumnConfig columnConfig = grid.getColumnModel().getColumn(column);
		this.setComment(headerRowConfig, columnConfig, comment);
	}

	public String getCoverage(Taxon taxon) {
		return taxonMatrix.getCoverage(taxon);
	}
	
	public String getCoverage(Character character) {
		return taxonMatrix.getCoverage(character);
	}
	
	public void refreshColumnHeader(int column) {
		ColumnConfig<Taxon, ?> config = grid.getColumnModel().getColumn(column);
		if(config instanceof MyColumnConfig) {
			MyColumnConfig myColumnConfig = (MyColumnConfig)config;
			if (!myColumnConfig.isHidden()) {
				ColumnHeader<Taxon> header = grid.getView().getHeader();
				if (header != null) {
					Head h = header.getHead(column);
					if (h != null && h.isRendered() && h instanceof MyHead) {
						MyHead myHead = (MyHead)h;
						myHead.setText(myColumnConfig.getCharacter().toString());
						myHead.setCoverage(TaxonMatrixView.this.getCoverage(myColumnConfig.getCharacter()));
						myHead.setQuickTipText(TaxonMatrixView.this.getSummary(myColumnConfig.getCharacter()));
					}
				}
			}
		}
	}
	
	private void refreshColumnHeaders() {
		for(int j = this.firstCharacterColumn; j<grid.getColumnModel().getColumnCount(); j++) {
			ColumnConfig columnConfig = grid.getColumnModel().getColumn(j);
			if(columnConfig instanceof MyColumnConfig) {
				refreshColumnHeader(j);
			}
		}
	}

	public String getSummary(Taxon taxon) {
		return "Character coverage: " + getCoverage(taxon);
	}
	
	public String getSummary(Character character) {
		return "Taxon coverage: " + getCoverage(character);
	}

	public String getQuickTipText(ColumnConfig columnConfig) {
		if(columnConfig.equals(grid.getColumnModel().getColumn(1))) {
			return "The matrix contains " + taxonMatrix.getTaxa().size() + " taxa and " + taxonMatrix.getCharacters().size() + " characters";
		}
		if(columnConfig instanceof MyColumnConfig) {
			return this.getSummary(((MyColumnConfig)columnConfig).getCharacter());
		}
		return "";
	}

	public int getTaxonNameColumn() {
		return taxonNameColumn;
	}

	public int getFirstCharacterColumn() {
		return firstCharacterColumn;
	}

	public void sortColumns(Comparator<MyColumnConfig> comparator) {
		int columnCount = grid.getColumnModel().getColumnCount();
		List<MyColumnConfig> characterColumns = new ArrayList<MyColumnConfig>(columnCount);
		for(int i=this.firstCharacterColumn; i<columnCount; i++) {
			ColumnConfig config = grid.getColumnModel().getColumn(i);
			if(config instanceof MyColumnConfig) 
				characterColumns.add((MyColumnConfig)config);
		}
		Collections.sort(characterColumns, comparator);
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(characterColumns);
		for(int i=this.taxonNameColumn; i>=0; i--) {
			columns.add(0, grid.getColumnModel().getColumn(i));
		}
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);	
		grid.reconfigure(grid.getStore(), cm);
	}
	
	public void sortColumnsByCoverage(final boolean ascending) {
		Comparator<MyColumnConfig> comparator = new Comparator<MyColumnConfig>() {
			@Override
			public int compare(MyColumnConfig o1, MyColumnConfig o2) {
				if(ascending)
					return taxonMatrix.getCharacterValueCount(o1.getCharacter()) - taxonMatrix.getCharacterValueCount(o2.getCharacter());
				else
					return taxonMatrix.getCharacterValueCount(o2.getCharacter()) - taxonMatrix.getCharacterValueCount(o1.getCharacter());
			}
		};
		this.sortColumns(comparator);
	}
	
	public void sortColumnsByName(final boolean ascending) {
		Comparator<MyColumnConfig> comparator = new Comparator<MyColumnConfig>() {
			@Override
			public int compare(MyColumnConfig o1, MyColumnConfig o2) {
				if(ascending)
					//first by name then by organ
					return (o1.getCharacter().getName() + o1.getCharacter().getOrgan()).compareTo(
							o2.getCharacter().getName() + o2.getCharacter().getOrgan());
				else
					return (o2.getCharacter().getName() + o2.getCharacter().getOrgan()).compareTo(
							o1.getCharacter().getName() + o1.getCharacter().getOrgan());
			}
		};
		this.sortColumns(comparator);
	}
	
	public void sortColumnsByOrgan(final boolean ascending) {
		Comparator<MyColumnConfig> comparator = new Comparator<MyColumnConfig>() {
			@Override
			public int compare(MyColumnConfig o1, MyColumnConfig o2) {
				if(ascending)
					//first by organ then by character name
					return (o1.getCharacter().getOrgan() + o1.getCharacter().getName()).compareTo(
							o2.getCharacter().getOrgan() + o2.getCharacter().getName());
				else
					return (o2.getCharacter().getOrgan() + o2.getCharacter().getName()).compareTo(
							o1.getCharacter().getOrgan() + o1.getCharacter().getName());
			}
		};
		this.sortColumns(comparator);
	}

	public void renameCharacter(int colIndex, String name, String organ) {
		final Character character = getCharacter(colIndex);
		character.setName(name);
		character.setOrgan(organ);
		refreshColumnHeader(colIndex);
	}
	
}
