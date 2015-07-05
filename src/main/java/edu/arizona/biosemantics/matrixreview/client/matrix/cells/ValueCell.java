package edu.arizona.biosemantics.matrixreview.client.matrix.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.util.ImageHelper;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.menu.Menu;

import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharactersColumnModel;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.ValueMenu;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class ValueCell extends MenuExtendedCell<Value> {

	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div class=\"{0}\" qtip=\"{4}\">" +
				"<div class=\"{1}\" " +
				"style=\"" +
				"width: calc(100% - 9px); " +
				"height:14px; " +
				"background: no-repeat 0 0;" +
				"background-image:{6};" +
				"background-color:{5};" +
				"\">{3}<a class=\"{2}\" style=\"height: 22px;\"></a>" +
				"</div>" +
				"</div>")
		SafeHtml cell(String grandParentStyleClass, String parentStyleClass,
				String aStyleClass, String value, String quickTipText, String colorHex, String backgroundImage);
	}

	private static ValueCellImages valueCellImages = GWT.create(ValueCellImages.class);
	protected static Templates templates = GWT.create(Templates.class);
	private EventBus eventBus;
	private Model model;
	private ListStore<Taxon> listStore;
	private CharactersColumnModel columnModel;
	
	public ValueCell(EventBus eventBus, Model model) {
		this.eventBus = eventBus;
		this.model = model;
	}
	
	@Override
	public void render(Context context,	Value value, SafeHtmlBuilder sb) {
		if (value == null)
			return;
		Taxon taxon = model.getTaxonMatrix().getTaxon(value);
		String quickTipText = taxon.getName();
		
		Character character = model.getTaxonMatrix().getCharacter(value);
		
		if(character != null) {
			quickTipText = character.toString() + " of " + taxon.getFullName() + " is " + value.getValue(); 
		}
		
		String comment = model.getComment(value);
		if(!comment.isEmpty())
			quickTipText += "<br>Comment:" + comment;
		
		Color color = model.getColor(value);
		if(color == null) 
			color = model.getColor(character);
		if(color == null)
			color = model.getColor(taxon);

		String colorHex = "";
		if(color != null) {
			colorHex = "#" + color.getHex();
			quickTipText += "<br>Colorized: " + color.getUse();
		}
		if(model.isDirty(value)) {
			quickTipText += "<br>Edited";
		}
		
		String backgroundImage = "";
		if(model.isDirty(value)) {
			if(!model.isCommented(value)) {
				backgroundImage = "url(" + valueCellImages.black().getSafeUri().asString() + ")"; //ImageHelper.createModuleBasedUrl("base/images/grid/black.gif");
			} else {
				backgroundImage = "url(" + valueCellImages.blackRed().getSafeUri().asString() + ")"; //ImageHelper.createModuleBasedUrl("base/images/grid/black_red.gif");
			}
		} else if(model.isCommented(value)) {
			backgroundImage = "url(" + valueCellImages.red().getSafeUri().asString() + ")"; //ImageHelper.createModuleBasedUrl("base/images/grid/red.gif");
		}		
		
		SafeHtml rendered = templates.cell("", columnHeaderStyles.headInner(),
				columnHeaderStyles.headButton(), value.toString(), quickTipText, colorHex, backgroundImage);
		sb.append(rendered);
	}

	protected Menu createContextMenu(final int colIndex, final int rowIndex) {
		Taxon taxon = listStore.get(rowIndex);
		Character character = columnModel.getColumn(colIndex).getCharacter();
		Value value = model.getTaxonMatrix().getValue(taxon, character);
		final Menu menu = new ValueMenu(eventBus, model, value, taxon, character);
		return menu;
	}

	public void setListStore(ListStore<Taxon> listStore) {
		this.listStore = listStore;
	}

	public void setColumnModel(CharactersColumnModel columnModel) {
		this.columnModel = columnModel;
	}
}
