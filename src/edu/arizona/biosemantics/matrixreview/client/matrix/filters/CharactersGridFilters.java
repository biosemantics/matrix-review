package edu.arizona.biosemantics.matrixreview.client.matrix.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.DualListField.Mode;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.DoublePropertyEditor;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.NumericFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent.SetCharacterStatesEventHandler;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharacterColumnConfig;
import edu.arizona.biosemantics.matrixreview.client.matrix.FrozenFirstColumTaxonTreeGrid.CharactersGrid;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.editing.ValueConverter;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.AllAccessListStore;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

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
		
	private int insertPositionFilters = 12;
	private EventBus eventBus;
	private TaxonMatrix taxonMatrix;
	private CharactersGrid charactersGrid;
	private TaxonStore taxonStore;

	public CharactersGridFilters(EventBus eventBus, TaxonMatrix taxonMatrix, TaxonStore taxonStore, CharactersGrid charactersGrid) {
		this.eventBus = eventBus; 
		this.taxonMatrix = taxonMatrix;
		this.taxonStore = taxonStore;
		this.charactersGrid = charactersGrid;
		
		setLocal(true);
		for(CharacterColumnConfig characterColumnConfig : charactersGrid.getColumnModel().getCharacterColumns()) {
			StringFilter<Taxon> stringFilter = new StringFilter<Taxon>(new StringValueProvider(characterColumnConfig));
			setFilter(characterColumnConfig, stringFilter);
		}
		
		addEventHandlers();
	}
	
	
	private void addEventHandlers() {
		eventBus.addHandler(AddCharacterEvent.TYPE, new AddCharacterEvent.AddCharacterEventHandler() {
			@Override
			public void onAdd(AddCharacterEvent event) {
				CharacterColumnConfig characterColumnConfig = charactersGrid.getCharacterColumnConfig(event.getCharacter());
				StringFilter<Taxon> stringFilter = new StringFilter<Taxon>(new StringValueProvider(characterColumnConfig));
				setFilter(characterColumnConfig, stringFilter);
			}
		});
	}

	protected void onContextMenu(HeaderContextMenuEvent event) {
		int column = event.getColumnIndex();
		
		if (checkFilterItem == null) {
			checkFilterItem = new CheckMenuItem(DefaultMessages.getMessages()
					.gridFilters_filterText());
			checkFilterItem
					.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {

						@Override
						public void onCheckChange(
								CheckChangeEvent<CheckMenuItem> event) {
							CharactersGridFilters.this.onCheckChange(event);
						}
					});
		}

		checkFilterItem.setData("index", column);
		
		
		Filter<Taxon, ?> f = getFilter(grid.getColumnModel().getColumn(column).getValueProvider().getPath());
		
		if (f != null) {
			Menu filterMenu = f.getMenu();
			Menu menu = event.getMenu();
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
		removeFilter(characterColumnConfig.getFilter());
		switch (controlMode) {
		case CATEGORICAL:
			AllAccessListStore<String> valueStore = new AllAccessListStore<String>(new ModelKeyProvider<String>() {
				@Override
				public String getKey(String item) {
					return item;
				}
			});
			valueStore.addAll(states);
			setFilter(characterColumnConfig, new ListFilter<Taxon, String>(new StringValueProvider(characterColumnConfig), valueStore));
			break;
		case NUMERICAL:
			setFilter(characterColumnConfig, new NumericFilter<Taxon, Double>(new NumericValueProvider(characterColumnConfig), new DoublePropertyEditor()));
			break;
		case OFF:
			setFilter(characterColumnConfig, new StringFilter<Taxon>(new StringValueProvider(characterColumnConfig)));
			break;					
		}
	}
	
	private void setFilter(CharacterColumnConfig characterColumnConfig, Filter<Taxon, ?> filter) {
		characterColumnConfig.setFilter(filter);
		addFilter(filter);
	}
	

	@Override
	protected Store<Taxon> getStore() {
		return taxonStore;
	}
}
