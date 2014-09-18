package edu.arizona.biosemantics.matrixreview.client.matrix.filters;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.messages.client.DefaultMessages;
//import com.sencha.gxt.widget.core.client.event.BeforeFilterEvent;
//import com.sencha.gxt.widget.core.client.event.BeforeFilterEvent.BeforeFilterHandler;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.DoublePropertyEditor;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharacterColumnConfig;
import edu.arizona.biosemantics.matrixreview.client.matrix.FrozenFirstColumTaxonTreeGrid.CharactersGrid;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.CharacterMenu;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.AllAccessListStore;
import edu.arizona.biosemantics.matrixreview.shared.model.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class CharactersGridFilters extends GridFilters<Taxon> {
	
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
			model.getTaxonMatrix().setValue(object, characterColumnConfig.getCharacter(), new Value(value));
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
				return null;
			}
		}

		@Override
		public void setValue(Taxon object, Double value) {
			model.getTaxonMatrix().setValue(object, characterColumnConfig.getCharacter(), new Value(String.valueOf(value)));
		}

		@Override
		public String getPath() {
			return characterColumnConfig.getValueProvider().getPath();
		}
	}
	
	public class RemoveEmptyNumericFilter<M, V extends Number> extends NumericFilter<M, V> {

		public RemoveEmptyNumericFilter(
				ValueProvider<? super M, V> valueProvider,
				NumberPropertyEditor<V> propertyEditor) {
			super(valueProvider, propertyEditor);
		}

		@Override
		public boolean validateModel(M model) {
			V modelValue = getValueProvider().getValue(model);
			if(modelValue == null)
				return false;
			return super.validateModel(model);
		}
		
	}

	private EventBus eventBus;
	private Model model;
	private CharactersGrid charactersGrid;
	private TaxonStore taxonStore;

	public CharactersGridFilters(EventBus eventBus, Model model, TaxonStore taxonStore, CharactersGrid charactersGrid) {
		this.eventBus = eventBus; 
		this. model =  model;
		this.taxonStore = taxonStore;
		this.charactersGrid = charactersGrid;
		
		setLocal(true);
		for(CharacterColumnConfig characterColumnConfig : charactersGrid.getColumnModel().getCharacterColumns()) {
			Character character = characterColumnConfig.getCharacter();
			setControlMode(characterColumnConfig, model.getControlMode(character), model.getStates(character));
		}
		
		addEventHandlers();
	}
	
	
	private void addEventHandlers() {
		eventBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
			@Override
			public void onLoad(LoadModelEvent event) {
				model = event.getModel();
			}
		});
	}

	protected void loadModel() {
		for(CharacterColumnConfig characterColumnConfig : charactersGrid.getColumnModel().getCharacterColumns()) {
			Character character = characterColumnConfig.getCharacter();
			setControlMode(character, model.getControlMode(character), model.getStates(character));
		}
	}


	protected void onContextMenu(HeaderContextMenuEvent event) {
		int column = event.getColumnIndex();
		
		Filter<Taxon, ?> f = getFilter(grid.getColumnModel().getColumn(column).getValueProvider().getPath());
		
		if (f != null) {
			final Menu filterMenu = f.getMenu();
			final CharacterMenu menu = (CharacterMenu)event.getMenu();
			
			if (checkFilterItem == null) {
				checkFilterItem = new CheckMenuItem(DefaultMessages.getMessages()
						.gridFilters_filterText());
				checkFilterItem
						.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
							@Override
							public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
								CharactersGridFilters.this.onCheckChange(event);
							}
						});
			}

			checkFilterItem.setData("index", column);
			checkFilterItem.setChecked(f.isActive(), true);
			checkFilterItem.setSubMenu(filterMenu);
			for(int i=0; i<menu.getWidgetCount(); i++) {
				Widget widget = menu.getWidget(i);
				if(widget instanceof HeaderMenuItem) {
					HeaderMenuItem headerMenuItem = (HeaderMenuItem)widget;
					if(headerMenuItem.getText().equals("View")) {
						menu.insert(checkFilterItem, i + 3);
						break;	
					}
				}
			}
		}
	}
	
	public void setControlMode(Character character, ControlMode controlMode, List<String> states) {
		final CharacterColumnConfig characterColumnConfig = charactersGrid.getCharacterColumnConfig(character);
		this.setControlMode(characterColumnConfig, controlMode, states);
	}
	
	public void setControlMode(CharacterColumnConfig config, ControlMode controlMode, List<String> states) {
		if(config.hasFilter())
			removeFilter(config.getFilter());
		switch (controlMode) {
		case CATEGORICAL:
			AllAccessListStore<String> valueStore = new AllAccessListStore<String>(new ModelKeyProvider<String>() {
				@Override
				public String getKey(String item) {
					return item;
				}
			});
			valueStore.addAll(states);
			setFilter(config, new ListFilter<Taxon, String>(new StringValueProvider(config), valueStore));
			break;
		case NUMERICAL:
			setFilter(config, new RemoveEmptyNumericFilter<Taxon, Double>(new NumericValueProvider(config), new DoublePropertyEditor()));
			break;
		case OFF:
			setFilter(config, new StringFilter<Taxon>(new StringValueProvider(config)));
			break;	
		default:
			setFilter(config, new StringFilter<Taxon>(new StringValueProvider(config)));
		}
	}
	
	private void setFilter(CharacterColumnConfig characterColumnConfig, Filter<Taxon, ?> filter) {
		filter.addBeforeFilterHandler(charactersGrid.getView().getScrollStateMaintainer());
		characterColumnConfig.setFilter(filter);
		addFilter(filter);
	}
	
	public void addCharacter(Character character) {
		CharacterColumnConfig characterColumnConfig = charactersGrid.getCharacterColumnConfig(character);
		StringFilter<Taxon> stringFilter = new StringFilter<Taxon>(new StringValueProvider(characterColumnConfig));
		setFilter(characterColumnConfig, stringFilter);
	}

	@Override
	protected Store<Taxon> getStore() {
		return taxonStore;
	}
}
