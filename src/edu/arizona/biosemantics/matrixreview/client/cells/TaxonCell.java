package edu.arizona.biosemantics.matrixreview.client.cells;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.ImageHelper;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.RowMenu;

import edu.arizona.biosemantics.matrixreview.client.manager.AnnotationManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ControlManager;
import edu.arizona.biosemantics.matrixreview.client.manager.DataManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ViewManager;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxonCell extends MenuExtendedCell<Taxon> {

	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div class=\"{0}\" qtitle=\"Summary\" qtip=\"{5}\">" +
				"<div class=\"{1}\" " +
				"style=\"" +
				"width: calc(100% - 9px); " +
				"height:14px;" +
				"background: no-repeat 0 0;" +
				"background-image:{7};" +
				"background-color:{6};\"" +
				">{3}<span style=\"position:absolute;right:{8}px;background-color:#b0e0e6;width:35px;\">{4}</span>" + 
				"<a href=\"#\" class=\"{2}\" style=\"height: 22px; right:{8}px;\"></a>" +
				"</div>" +
				"</div>")
		SafeHtml cell(String grandParentStyleClass, String parentStyleClass,
				String aStyleClass, String value, String coverage, String quickTipText, String colorHex, String backgroundImage, int distanceRight);
	}
	
	protected static Templates templates = GWT.create(Templates.class);
	private DataManager dataManager;
	private ViewManager viewManager;
	private ControlManager controlManager;
	private AnnotationManager annotationManager;
	
	public TaxonCell(DataManager dataManager, ViewManager viewManager, ControlManager controlManager, AnnotationManager annotationManager) {
		super();
		this.dataManager = dataManager;
		this.viewManager = viewManager;
		this.controlManager = controlManager;
		this.annotationManager = annotationManager;
	}

	@Override
	public void render(Context context, Taxon value, SafeHtmlBuilder sb) {
		if (value == null)
			return;
		String grandParentStyleClass = columnHeaderStyles.header() + " " + columnHeaderStyles.head();
		String parentStyleClass = columnHeaderStyles.headInner();
		String aStyleClass = columnHeaderStyles.headButton();
		
		String quickTipText = value.getName();
		quickTipText += "<br>" + dataManager.getSummary(value);
		
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
		
		SafeHtml rendered = templates.cell(grandParentStyleClass, parentStyleClass, aStyleClass, 
				value.getName(), dataManager.getCoverage(value), quickTipText, colorHex, backgroundImage, XDOM.getScrollBarWidth() - 5);
		sb.append(rendered);
	}
		
	@Override
	protected Menu createContextMenu(final int colIndex, final int rowIndex) {
		final Menu menu = new RowMenu(dataManager, viewManager, controlManager, annotationManager, rowIndex);
		return menu;
	}
}
