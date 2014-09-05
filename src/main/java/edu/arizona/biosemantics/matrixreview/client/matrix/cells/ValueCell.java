package edu.arizona.biosemantics.matrixreview.client.matrix.cells;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.util.ImageHelper;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.menu.Menu;

import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.ValueMenu;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

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
	
	protected static Templates templates = GWT.create(Templates.class);
	private EventBus eventBus;
	private TaxonMatrix taxonMatrix;
	private ListStore<Taxon> listStore;
	
	public ValueCell(EventBus eventBus) {
		this.eventBus = eventBus;
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadTaxonMatrixEvent.TYPE, new LoadTaxonMatrixEvent.LoadTaxonMatrixEventHandler() {
			@Override
			public void onLoad(LoadTaxonMatrixEvent event) {
				taxonMatrix = event.getTaxonMatrix();
			}
		});
	}

	@Override
	public void render(Context context,	Value value, SafeHtmlBuilder sb) {
		if (value == null)
			return;
		Taxon taxon = value.getTaxon();
		String quickTipText = taxon.getName();
		
		Character character = value.getCharacter();
		if(character != null) {
			quickTipText = character.toString() + " of " + taxon.getFullName() + " is " + value.getValue(); 
		}
		
		String comment = value.getComment();
		if(!comment.isEmpty())
			quickTipText += "<br>Comment:" + comment;
			
		Color color = value.getColor();
		String colorHex = "";
		if(color != null) {
			colorHex = "#" + color.getHex();
			quickTipText += "<br>Colorized: " + color.getUse();
		}
		if(value.isDirty()) {
			quickTipText += "<br>Dirty";
		}
		
		String backgroundImage = "";
		if(value.isDirty()) {
			if(!value.isCommented()) {
				backgroundImage = ImageHelper.createModuleBasedUrl("base/images/grid/black.gif");
			} else {
				backgroundImage = ImageHelper.createModuleBasedUrl("base/images/grid/black_red.gif");
			}
		} else if(value.isCommented()) {
			backgroundImage = ImageHelper.createModuleBasedUrl("base/images/grid/red.gif");
		}		
		
		SafeHtml rendered = templates.cell("", columnHeaderStyles.headInner(),
				columnHeaderStyles.headButton(), value.toString(), quickTipText, colorHex, backgroundImage);
		sb.append(rendered);
	}

	protected Menu createContextMenu(final int colIndex, final int rowIndex) {
		Taxon taxon = listStore.get(rowIndex);
		Character character = taxonMatrix.getCharacter(colIndex);
		Value value = taxon.get(character);
		final Menu menu = new ValueMenu(eventBus, taxonMatrix, value, taxon, character);
		return menu;
	}

	public void setListStore(ListStore<Taxon> listStore) {
		this.listStore = listStore;
	}
}
