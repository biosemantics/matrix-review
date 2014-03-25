package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.MyListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.MyGridDragSource;
import com.sencha.gxt.dnd.core.client.MyGridDropTarget;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.UpdateEvent;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.MyGrid;
import com.sencha.gxt.widget.core.client.grid.RowExpander;
import com.sencha.gxt.widget.core.client.grid.editing.MyGridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.HideTaxonFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.google.gwt.cell.client.AbstractCell;

import com.sencha.gxt.widget.core.client.form.Field;

import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class TaxonMatrixView implements IsWidget {
	
	private TaxonMatrix taxonMatrix;
	private MyGrid<Taxon> grid;
	private MyGridInlineEditing<Taxon> editing;
	private FlowLayoutContainer container = new FlowLayoutContainer();
	private RowExpander<Taxon> expander;
	private Map<ColumnConfig, ControlMode> columnControlMap = new HashMap<ColumnConfig, ControlMode>();
	private MyListStore<Taxon> store;
	private HideTaxonFilter hideTaxonFilter = new HideTaxonFilter();
	
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
		
		List<ColumnConfig<Taxon, ?>> l = new ArrayList<ColumnConfig<Taxon, ?>>();
		l.add(expander);
		ColumnConfig nameColumnConfig = createNameColumnConfig();
		l.add(nameColumnConfig);
		for (final Character character : taxonMatrix.getCharacters()) {
			ColumnConfig characterColumnConfig = createCharacterColumnConfig(character);
			l.add(characterColumnConfig);
		}
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(l);
		
		grid.reconfigure(store, cm);
		
		//set up editing
		editing = new MyGridInlineEditing<Taxon>(grid, store);
		for (int i=1; i<l.size(); i++) {
			ColumnConfig columnConfig = l.get(i);
			this.setControlMode(columnConfig, ControlMode.OFF);
			this.enableEditing(columnConfig);
		}
		for(Taxon taxon : taxonMatrix.getTaxa())
			editing.addEditor(taxon);
		
		// set up filtering (tied to the store internally, so has to be done after grid is reconfigured with new store object)
		GridFilters<Taxon> filters = new GridFilters<Taxon>();
		filters.setLocal(true);
		StringFilter<Taxon> taxonNameFilter = new StringFilter<Taxon>(new TaxonNameValueProvider());
		filters.addFilter(taxonNameFilter);
		for (int i=1; i<l.size(); i++) {
			ColumnConfig columnConfig = l.get(i);
			StringFilter<Taxon> characterStateFilter = new StringFilter<Taxon>(columnConfig.getValueProvider());
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

	private MyGrid<Taxon> createGrid() {
		MyGrid<Taxon> grid = new MyGrid<Taxon>(new ListStore<Taxon>(new TaxonModelKeyProvider()), 
				new ColumnModel<Taxon>(new ArrayList<ColumnConfig<Taxon, ?>>()), this);
		grid.getView().setForceFit(false); // if change in column width we want the table to become wider not stay fixed at overall width
		grid.setColumnReordering(true);
		
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
		this.taxonMatrix.addTaxon(taxon);
		grid.getStore().add(taxon);
		editing.addEditor(taxon);
	}
	
	public void removeTaxon(Taxon taxon) {
		this.taxonMatrix.removeTaxon(taxon);
		grid.getStore().remove(taxon);
		editing.removeEditor(taxon);
	}
	
	public void addCharacter(Character character) {
		taxonMatrix.addCharacter(character);
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(grid.getColumnModel().getColumns());
		ColumnConfig columnConfig = createCharacterColumnConfig(character);
		columns.add(columnConfig);
		ColumnModel<Taxon> cm = new ColumnModel<Taxon>(columns);
		this.setControlMode(columnConfig, ControlMode.OFF);
		this.enableEditing(columnConfig);	
		grid.reconfigure(grid.getStore(), cm);
	}
	
	public void removeCharacter(int i) {
		taxonMatrix.removeCharacter(i);
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>(grid.getColumnModel().getColumns());
		ColumnConfig columnConfig = columns.remove(i + 1);
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
		HorizontalPanel functionsPanel = new HorizontalPanel();
		panel.add(functionsPanel);
		
		Button addTaxonButton = new Button("Add Taxon");
		addTaxonButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for(int i=0; i<10; i++)
					addTaxon(new Taxon("mu"));
				
			}
		});
		functionsPanel.add(addTaxonButton);
		
		Button addCharacterButton = new Button("Add Character");
		addCharacterButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addCharacter(new Character("ch"));
			}
		});
		functionsPanel.add(addCharacterButton);

		/*final TextButton removeButton = new TextButton("Remove Taxon");
		removeButton.setEnabled(false);
		SelectHandler removeButtonHandler = new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for (Taxon taxon : grid.getSelectionModel().getSelectedItems()) {
					grid.getStore().remove(taxon);
				}
				removeButton.setEnabled(false);
			}
		};
		removeButton.addSelectHandler(removeButtonHandler);
		functionsPanel.add(removeButton);
		
		grid.getSelectionModel().addSelectionChangedHandler(
				new SelectionChangedHandler<Taxon>() {
					@Override
					public void onSelectionChanged(SelectionChangedEvent<Taxon> event) {
						removeButton.setEnabled(!event.getSelection().isEmpty());
					}
				});
		*/
		
		/*final Button lockButton = new Button("Lock Taxon");
		lockButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				for (Taxon taxon : grid.getSelectionModel().getSelectedItems()) {
					//grid.getR
				}
			}
		});
		functionsPanel.add(lockButton);
		*/
		container.add(panel);
		return container;
		//return panel;
	}

	private ColumnConfig<Taxon, String> createNameColumnConfig() {
		ColumnConfig<Taxon, String> nameCol = new ColumnConfig<Taxon, String>(
			new TaxonNameValueProvider(), 200, "Taxon Concept");
				
		nameCol.setCell(new TaxonCell<String>(grid, this));
		return nameCol;
	}
	
	private ColumnConfig<Taxon, String> createCharacterColumnConfig(final Character character) {
		ColumnConfig<Taxon, String> characterCol = new ColumnConfig<Taxon, String>(
				new ValueProvider<Taxon, String>() {
					@Override
					public String getValue(Taxon object) {
						return object.get(character).getValue();
					}

					@Override
					public void setValue(Taxon object, String value) {
						object.put(character, new Value(value));
					}

					@Override
					public String getPath() {
						return "/" + character.getName() + "/value";
					}
				}, 200, character.getName());
		characterCol.setCell(new MenuExtendedCell<String>());
		return characterCol;
	}

	public void deleteColumn(int colIndex) {
		//name is not allowed to be deleted
		if(colIndex > 0)
			this.removeCharacter(colIndex - 1);
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
			editing.addEditor(columnConfig, new TextField());
			break;
		case CATEGORICAL:
			Set<String> values = new HashSet<String>();
			for(Taxon taxon : taxonMatrix.getTaxa()) {
				values.add((String)columnConfig.getValueProvider().getValue(taxon));
			}
			ListStore<String> comboValues = new ListStore<String>(
					new ModelKeyProvider<String>() {
						@Override
						public String getKey(String item) {
							return item;
						}
					});
			comboValues.addAll(values);
			ComboBox<String> editComboBox = new ComboBox<String>(comboValues, new LabelProvider<String>() {
				@Override
				public String getLabel(String item) {
					return item;
				}
			});
			editComboBox.setEditable(true);
			editComboBox.setTypeAhead(true);
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
	
}
