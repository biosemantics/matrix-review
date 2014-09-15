package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.CommentsDialog.Comment;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.ColorProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class ColorsDialog extends Dialog {

	private final static String taxonCharacterValueType = "Taxon-Character-Value";
	private final static String characterType = "Character";
	private final static String taxonType = "Taxon";
	
	public class ColorEntry {

		private String source;
		private String value;
		private Color color;
		private Object object;
		private String type;

		public ColorEntry(Object object, String source, String value, Color color) {
			this.object = object;
			if(object instanceof Value)
				type = taxonCharacterValueType;
			if(object instanceof Character)
				type = characterType;
			if(object instanceof Taxon)
				type = taxonType;
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
		
		public String getType() {
			return type;
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
		ValueProvider<ColorEntry, String> type();

	}

	private EventBus eventBus;
	private Model model;
	private ListStore<ColorEntry> colorEntriesStore;
	private Grid<ColorEntry> grid;
	private ColorEntryProperties colorEntryProperties = GWT.create(ColorEntryProperties.class);
	private ColorProperties colorProperties = GWT.create(ColorProperties.class);

	public ColorsDialog(EventBus eventBus, Model model) {
		this.eventBus = eventBus;
		this.model = model;
		

		IdentityValueProvider<ColorEntry> identity = new IdentityValueProvider<ColorEntry>();
	    final CheckBoxSelectionModel<ColorEntry> checkBoxSelectionModel = new CheckBoxSelectionModel<ColorEntry>(identity);
	    		
	    ColumnConfig<ColorEntry, String> typeCol = new ColumnConfig<ColorEntry, String>(
				colorEntryProperties.type(), 0, "Type");
		ColumnConfig<ColorEntry, String> sourceCol = new ColumnConfig<ColorEntry, String>(
				colorEntryProperties.source(), 190, "Source");
		ColumnConfig<ColorEntry, String> valueCol = new ColumnConfig<ColorEntry, String>(
				colorEntryProperties.value(), 150, "Value(s)");
		ColumnConfig<ColorEntry, Color> hexCol = new ColumnConfig<ColorEntry, Color>(
				colorEntryProperties.color(), 40, "Color");
		hexCol.setCell(new AbstractCell<Color>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					Color value, SafeHtmlBuilder sb) {
				String colorHex = value.getHex();
				sb.append(SafeHtmlUtils.fromTrustedString("<div style='width:15px;height:15px; background-color:#" + colorHex + "'/>"));
			}
		});
		ColumnConfig<ColorEntry, Color> textCol = new ColumnConfig<ColorEntry, Color>(
				colorEntryProperties.color(), 400, "Color Use");
		textCol.setCell(new AbstractCell<Color>() {
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
		columns.add(hexCol);
		columns.add(textCol);
		ColumnModel<ColorEntry> cm = new ColumnModel<ColorEntry>(columns);
		
		colorEntriesStore = new ListStore<ColorEntry>(colorEntryProperties.key());
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
		grid.getView().setAutoExpandColumn(textCol);
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
	    
	    ListStore<String> typeFilterStore = new ListStore<String>(new ModelKeyProvider<String>() {
			@Override
			public String getKey(String item) {
				return item;
			}
	    });
	    typeFilterStore.add(taxonType);
	    typeFilterStore.add(characterType);
	    typeFilterStore.add(taxonCharacterValueType);
	    
	    ListFilter<ColorEntry, String> typeFilter = new ListFilter<ColorEntry, String>(colorEntryProperties.type(), typeFilterStore);
	    
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
