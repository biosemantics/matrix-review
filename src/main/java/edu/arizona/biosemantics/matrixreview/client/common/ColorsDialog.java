package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class ColorsDialog extends Dialog {

	public class ColorEntry {

		private String source;
		private String value;
		private Color color;

		public ColorEntry(String source, String value, Color color) {
			this.source = source;
			this.value = value;
			this.color = color;
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
	}

	public interface ColorEntryProperties extends PropertyAccess<ColorEntry> {

		@Path("source")
		ModelKeyProvider<ColorEntry> key();

		@Path("source")
		ValueProvider<ColorEntry, String> source();

		@Path("value")
		ValueProvider<ColorEntry, String> value();

		@Path("color")
		ValueProvider<ColorEntry, Color> color();

	}

	private EventBus eventBus;
	private Model model;

	public ColorsDialog(EventBus eventBus, Model model) {
		this.eventBus = eventBus;
		this.model = model;
		ColorEntryProperties colorEntryProperties = GWT
				.create(ColorEntryProperties.class);
		ListStore<ColorEntry> colorEntriesStore = new ListStore<ColorEntry>(
				colorEntryProperties.key());

		List<ColorEntry> colorEntries = createColorEntries();
		for (ColorEntry colorEntry : colorEntries)
			colorEntriesStore.add(colorEntry);

		List<ColumnConfig<ColorEntry, ?>> columns = new ArrayList<ColumnConfig<ColorEntry, ?>>();
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
		columns.add(sourceCol);
		columns.add(valueCol);
		columns.add(hexCol);
		columns.add(textCol);
		ColumnModel<ColorEntry> cm = new ColumnModel<ColorEntry>(columns);
		final Grid<ColorEntry> grid = new Grid<ColorEntry>(colorEntriesStore, cm);

		setBodyBorder(false);
		setHeadingText("Colorations");
		setWidth(800);
		setHeight(600);
		setHideOnButtonClick(true);

		ContentPanel panel = new ContentPanel();
		panel.add(grid);
		this.add(panel);
	}

	private List<ColorEntry> createColorEntries() {
		List<ColorEntry> colorEntries = new LinkedList<ColorEntry>();
		
		for(Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			System.out.println("taxon " + taxon.getFullName());
		}
		
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			if (model.hasColor(taxon))
				colorEntries.add(new ColorEntry(taxon.getFullName(), "", model
						.getColor(taxon)));
		}
		for (Character character : model.getTaxonMatrix()
				.getHierarchyCharactersBFS()) {
			if (model.hasColor(character))
				colorEntries.add(new ColorEntry(character.toString(), "", model
						.getColor(character)));
		}
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			for (Character character : model.getTaxonMatrix()
					.getHierarchyCharactersBFS()) {
				Value value = model.getTaxonMatrix().getValue(taxon, character);
				if (model.hasColor(value))
					colorEntries.add(new ColorEntry("Value of " + character.toString()
							+ " of " + taxon.getFullName(), value.getValue(),
							model.getColor(value)));
			}
		}
		return colorEntries;
	}

}
