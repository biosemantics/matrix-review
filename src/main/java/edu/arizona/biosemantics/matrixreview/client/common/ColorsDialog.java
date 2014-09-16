package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.theme.base.client.colorpalette.ColorPaletteBaseAppearance;
import com.sencha.gxt.widget.core.client.ColorPalette;
import com.sencha.gxt.widget.core.client.ColorPaletteCell;
import com.sencha.gxt.widget.core.client.ColorPaletteCell.ColorPaletteAppearance;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.CellSelectionEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.SetValueValidator.ValidationResult;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.ColorProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class ColorsDialog extends Dialog {

	public enum ColorEntryType {
		taxonType("Taxon"), characterType("Character"), taxonCharacterValueType("Taxon-Character-Value");
		
		private String readable;

		private ColorEntryType(String readable) {
			this.readable = readable;
		}
		
		public String getReadable() {
			return readable;
		}
		
		@Override
		public String toString() {
			return getReadable();
		}
	}
	
	public class ColorEntry {

		private String source;
		private String value;
		private Color color;
		private Object object;
		private ColorEntryType type;

		public ColorEntry(Object object, String source, String value, Color color) {
			this.object = object;
			if (object instanceof Value)
				type = ColorEntryType.taxonCharacterValueType;
			if (object instanceof Character)
				type = ColorEntryType.characterType;
			if (object instanceof Taxon)
				type = ColorEntryType.taxonType;
			this.source = source;
			this.value = value;
			this.color = color;
		}		
		
		public Object getObject() {
			return object;
		}

		public String getSource() {
			return source;
		}

		public String getValue() {
			return value;
		}

		public Color getColor() {
			return color;
		}
		
		public ColorEntryType getType() {
			return type;
		}
		
		public void setValue(String value) {
			if(this.type.equals(ColorEntryType.taxonCharacterValueType))
				this.value = value;
		}

		public void setColor(Color color) {
			this.color = color;
		}
	}

	public interface ColorEntryProperties extends PropertyAccess<ColorEntry> {

		@Path("source")
		ModelKeyProvider<ColorEntry> key();

		@Path("object")
		ValueProvider<ColorEntry, Object> object();
		
		@Path("source")
		ValueProvider<ColorEntry, String> source();

		@Path("value")
		ValueProvider<ColorEntry, String> value();

		@Path("color")
		ValueProvider<ColorEntry, Color> color();
		
		@Path("type")
		ValueProvider<ColorEntry, ColorEntryType> type();

	}

	private EventBus eventBus;
	private Model model;
	private ListStore<ColorEntry> colorEntriesStore;
	private Grid<ColorEntry> grid;
	private ColorEntryProperties colorEntryProperties = GWT.create(ColorEntryProperties.class);
	private ColorProperties colorProperties = GWT.create(ColorProperties.class);
	private ColorPaletteBaseAppearance appearance = GWT.create(ColorPaletteAppearance.class);

	public ColorsDialog(final EventBus eventBus, final Model model) {
		this.eventBus = eventBus;
		this.model = model;
		

		IdentityValueProvider<ColorEntry> identity = new IdentityValueProvider<ColorEntry>();
	    final CheckBoxSelectionModel<ColorEntry> checkBoxSelectionModel = new CheckBoxSelectionModel<ColorEntry>(identity);
	    		
	    ColumnConfig<ColorEntry, ColorEntryType> typeCol = new ColumnConfig<ColorEntry, ColorEntryType>(
				colorEntryProperties.type(), 0, "Type");
		ColumnConfig<ColorEntry, String> sourceCol = new ColumnConfig<ColorEntry, String>(
				colorEntryProperties.source(), 190, "Source");
		final ColumnConfig<ColorEntry, String> valueCol = new ColumnConfig<ColorEntry, String>(
				colorEntryProperties.value(), 150, "Value(s)");
		final ColumnConfig<ColorEntry, String> colorCol = new ColumnConfig<ColorEntry, String>(new ValueProvider<ColorEntry, String>() {
			@Override
			public String getValue(ColorEntry object) {
				return object.getColor().getHex();
			}
			@Override
			public void setValue(ColorEntry object, String value) {
				//don't implement ColorPalette will call this and overwrite the Color's correct hex value
			}
			@Override
			public String getPath() {
				return "color";
			}
		}, 210, "Color");
		/*colorCol.setCell(new AbstractCell<Color>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Color value, SafeHtmlBuilder sb) {
				String colorHex = value.getHex();
				sb.append(SafeHtmlUtils.fromTrustedString("<div style='width:15px;height:15px; background-color:#" + colorHex + "'/>"));
			}
		});*/
		
		int numColors = model.getColors().size();
		appearance.setColumnCount(8);
		List<Color> colors = model.getColors();
		final Map<String, Color> hexColorsMap = new HashMap<String, Color>();
		String[] hexs = new String[colors.size()];
		String[] labels = new String[colors.size()];
		for(int i=0; i<colors.size(); i++) {
			Color color = colors.get(i);
			hexColorsMap.put(color.getHex(), color);
			hexs[i] = color.getHex();
			labels[i] = color.getUse();
		}
		//ColorPalette colorPalette = new ColorPalette();
		//colorPalette.get
		ColorPaletteCell colorPaletteCell = new ColorPaletteCell(appearance, hexs, labels) {
			@Override
			public boolean handlesSelection() {
				return true;
			}
		};
		colorPaletteCell.addSelectionHandler(new SelectionHandler<String>() {
			@Override
			public void onSelection(SelectionEvent<String> event) {
				String selectedHex = event.getSelectedItem();
				Color selectedColor = hexColorsMap.get(selectedHex);
				if(event instanceof CellSelectionEvent) {
					CellSelectionEvent cellEvent = (CellSelectionEvent)event;
					final ColorEntry colorEntry = grid.getStore().get(cellEvent.getContext().getIndex());
					colorEntry.setColor(selectedColor);
					switch(colorEntry.getType()) {
					case taxonCharacterValueType:
						Value value = (Value)colorEntry.getObject();
						eventBus.fireEvent(new SetValueColorEvent(value, selectedColor));
						//grid.getStore().getRecord(colorEntry).addChange(colorEntryProperties.color(), selectedColor);
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							@Override
							public void execute() {
								colorEntriesStore.update(colorEntry);
							}
						});
						break;
					case characterType:
						Character character = (Character)colorEntry.getObject();
						eventBus.fireEvent(new SetCharacterColorEvent(character, selectedColor));
						//grid.getStore().getRecord(colorEntry).addChange(colorEntryProperties.color(), selectedColor);
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							@Override
							public void execute() {
								colorEntriesStore.update(colorEntry);
							}
						});
						break;
					case taxonType:
						Taxon taxon = (Taxon)colorEntry.getObject();
						eventBus.fireEvent(new SetTaxonColorEvent(taxon, selectedColor));
						Scheduler.get().scheduleDeferred(new ScheduledCommand() {
							@Override
							public void execute() {
								colorEntriesStore.update(colorEntry);
							}
						});
						break;
					default:
						break;
					}
				}
			}
		});
		colorCol.setCell(colorPaletteCell);
		
		final ColumnConfig<ColorEntry, Color> colorUseCol = new ColumnConfig<ColorEntry, Color>(
				colorEntryProperties.color(), 400, "Color Use");
		colorUseCol.setCell(new AbstractCell<Color>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Color value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString(value.getUse()));
			}
		});
		List<ColumnConfig<ColorEntry, ?>> columns = new ArrayList<ColumnConfig<ColorEntry, ?>>();
		columns.add(checkBoxSelectionModel.getColumn());
		columns.add(typeCol);
		columns.add(sourceCol);
		columns.add(valueCol);
		columns.add(colorCol);
		columns.add(colorUseCol);
		ColumnModel<ColorEntry> cm = new ColumnModel<ColorEntry>(columns);
		
		colorEntriesStore = new ListStore<ColorEntry>(colorEntryProperties.key());
		colorEntriesStore.setAutoCommit(true);
		List<ColorEntry> colorEntries = createColorEntries();
		for (ColorEntry colorEntry : colorEntries)
			colorEntriesStore.add(colorEntry);
				
		final GroupingView<ColorEntry> groupingView = new GroupingView<ColorEntry>();
	    groupingView.setShowGroupedColumn(false);
	    groupingView.setForceFit(true);
	    groupingView.groupBy(typeCol);
		
		grid = new Grid<ColorEntry>(colorEntriesStore, cm);
		grid.setView(groupingView);
		grid.setContextMenu(createContextMenu());
		grid.setSelectionModel(checkBoxSelectionModel);
		grid.getView().setAutoExpandColumn(colorUseCol);
		grid.setBorders(false);
	    grid.getView().setStripeRows(true);
	    grid.getView().setColumnLines(true);
	    
	    StringFilter<ColorEntry> textFilter = new StringFilter<ColorEntry>(new ValueProvider<ColorEntry, String>() {
			@Override
			public String getValue(ColorEntry object) {
				return object.getColor().getUse();
			}
			@Override
			public void setValue(ColorEntry object, String value) { // should never update color	
			}
			@Override
			public String getPath() {
				return "color";
			}
	    }); 
	    StringFilter<ColorEntry> sourceFilter = new StringFilter<ColorEntry>(colorEntryProperties.source());
	    StringFilter<ColorEntry> valueFilter = new StringFilter<ColorEntry>(colorEntryProperties.value());
	    
	    ListStore<ColorEntryType> typeFilterStore = new ListStore<ColorEntryType>(new ModelKeyProvider<ColorEntryType>() {
			@Override
			public String getKey(ColorEntryType item) {
				return item.toString();
			}
	    });
		for(ColorEntryType type : ColorEntryType.values())
			typeFilterStore.add(type);
	    
	    ListFilter<ColorEntry, ColorEntryType> typeFilter = new ListFilter<ColorEntry, ColorEntryType>(colorEntryProperties.type(), typeFilterStore);
	    
	    /*ListStore<Color> hexFilterStore = new ListStore<Color>(colorProperties.key());
	    for(Color color : model.getColors()) {
	    	hexFilterStore.add(color);
	    }	    
	    ListFilter<ColorEntry, Color> hexFilter = new ListFilter<ColorEntry, Color>(colorEntryProperties.color(), hexFilterStore);
	    */
	    
	    /*ListStore<String> textFilterStore = new ListStore<String>(new ModelKeyProvider<String>() {
			@Override
			public String getKey(String item) {
				return item;
			}
	    });
	    for(Color color : model.getColors()) {
	    	textFilterStore.add(color.getUse());
	    }	    
	    ListFilter<ColorEntry, String> textFilter = new ListFilter<ColorEntry, String>(new ValueProvider<ColorEntry, String>() {
			@Override
			public String getValue(ColorEntry object) {
				return object.getColor().getUse();
			}
			@Override
			public void setValue(ColorEntry object, String value) {	}

			@Override
			public String getPath() {
				return "use";
			}
	    }, textFilterStore); */
	    
		GridFilters<ColorEntry> filters = new GridFilters<ColorEntry>();
		filters.initPlugin(grid);
		filters.setLocal(true);
		
		filters.addFilter(textFilter);
		filters.addFilter(sourceFilter);
		filters.addFilter(valueFilter);
		filters.addFilter(typeFilter);
		//filters.addFilter(hexFilter);
		
		GridInlineEditing<ColorEntry> editing = new GridInlineEditing<ColorEntry>(grid);
		/*editing.addEditor(textCol, new Converter<Color, String>() {
			@Override
			public Color convertFieldValue(String object) {	
				return new Color("FFFFFF", object);
			}
			@Override
			public String convertModelValue(Color object) {
				return object.getUse();
			}
		}, new TextField()); */
		editing.addEditor(valueCol, new TextField());
		final SetValueValidator setValueValidator = new SetValueValidator(model);
		editing.addCompleteEditHandler(new CompleteEditHandler<ColorEntry>() {
			@Override
			public void onCompleteEdit(CompleteEditEvent<ColorEntry> event) {	
				GridCell cell = event.getEditCell();
				ColorEntry colorEntry = grid.getStore().get(cell.getRow());
				ColumnConfig<ColorEntry, String> config = grid.getColumnModel().getColumn(cell.getCol());
				if(config.equals(valueCol)) {
					switch(colorEntry.getType()) {
						case taxonCharacterValueType:
							Value oldValue = (Value)colorEntry.getObject();
							Character character = model.getTaxonMatrix().getCharacter(oldValue);
							Taxon taxon = model.getTaxonMatrix().getTaxon(oldValue);
							String value = config.getValueProvider().getValue(colorEntry);
							
							ValidationResult validationResult = setValueValidator.validValue(value, taxon, character);
							if(validationResult.isValid()) {
								Value newValue = new Value(value);
								eventBus.fireEvent(new SetValueEvent(taxon, character, oldValue, newValue));
								//will loose coloring otherwise because in model color tied to old value ref
								eventBus.fireEvent(new SetValueColorEvent(newValue, colorEntry.getColor()));
							} else {
								AlertMessageBox alert = new AlertMessageBox("Set value failed", "Can't set value " +
										value + " for " + character.getName() + " of " +  taxon.getFullName() + ". Control mode " + 
										model.getControlMode(character).toString().toLowerCase() + " was selected for " + character.getName());
								alert.show();
							}
							break;
						default:
							break;
					}
				}
			}
		});

		setBodyBorder(false);
		setHeadingText("Colorations");
		setWidth(800);
		setHeight(600);
		setHideOnButtonClick(true);

		ContentPanel panel = new ContentPanel();
		panel.add(grid);
		this.add(panel);
	}
	
	private Menu createContextMenu() {
		Menu menu = new Menu();
		MenuItem removeItem = new MenuItem("Remove");
		menu.add(removeItem);
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for(ColorEntry colorEntry : grid.getSelectionModel().getSelectedItems()) {
					colorEntriesStore.remove(colorEntry);
					Object object = colorEntry.getObject();
					if(object instanceof Value) {
						eventBus.fireEvent(new SetValueColorEvent((Value)object, null));
					}
					if(object instanceof Character) {
						eventBus.fireEvent(new SetCharacterColorEvent((Character)object, null));
					}
					if(object instanceof Taxon) {
						eventBus.fireEvent(new SetTaxonColorEvent((Taxon)object, null));
					}
				}
			}
		});
		return menu;
	}

	private List<ColorEntry> createColorEntries() {
		List<ColorEntry> colorEntries = new LinkedList<ColorEntry>();
		
		for(Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			System.out.println("taxon " + taxon.getFullName());
		}
		
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			if (model.hasColor(taxon))
				colorEntries.add(new ColorEntry(taxon, taxon.getFullName(), "", model
						.getColor(taxon)));
		}
		for (Character character : model.getTaxonMatrix()
				.getHierarchyCharactersBFS()) {
			if (model.hasColor(character))
				colorEntries.add(new ColorEntry(character, character.toString(), "", model
						.getColor(character)));
		}
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			for (Character character : model.getTaxonMatrix()
					.getHierarchyCharactersBFS()) {
				Value value = model.getTaxonMatrix().getValue(taxon, character);
				if (model.hasColor(value))
					colorEntries.add(new ColorEntry(value, "Value of " + character.toString()
							+ " of " + taxon.getFullName(), value.getValue(),
							model.getColor(value)));
			}
		}
		return colorEntries;
	}

}
