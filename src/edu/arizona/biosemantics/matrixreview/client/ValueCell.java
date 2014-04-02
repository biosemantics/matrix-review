package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.util.ImageHelper;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
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
				"\">{3}<a href=\"#\" class=\"{2}\" style=\"height: 22px;\"></a>" +
				"</div>" +
				"</div>")
		SafeHtml cell(String grandParentStyleClass, String parentStyleClass,
				String aStyleClass, String value, String quickTipText, String colorHex, String backgroundImage);
	}
	
	protected static Templates templates = GWT.create(Templates.class);
	
	public ValueCell(TaxonMatrixView taxonMatrixView) {
		super(taxonMatrixView);
	}

	@Override
	public void render(Context context,	Value value, SafeHtmlBuilder sb) {
		if (value == null)
			return;
		Taxon taxon = taxonMatrixView.getTaxon(context.getIndex());
		String quickTipText = taxon.getName();
		
		Character character = taxonMatrixView.getCharacter(context.getColumn());
		if(character != null) {
			Value characterValue = taxon.get(character);
			quickTipText = character.toString() + " of " + taxon.getName() + " is " + characterValue.getValue(); 
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
}
