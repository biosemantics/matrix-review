package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.ImageHelper;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStyles;
import com.sencha.gxt.widget.core.client.grid.MyGrid;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.RowMenu;
import com.sencha.gxt.widget.core.client.menu.SeparatorMenuItem;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxonCell extends MenuExtendedCell<Taxon> {

	private MyGrid grid;
	private GridStyles styles;

	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div qtitle=\"Summary\" qtip=\"{5}\">" +
				"<div class=\"{1}\" " +
				"style=\"" +
				"width: calc(100% - 9px); " +
				"height:14px;" +
				"background: no-repeat 0 0;" +
				"background-image:{7};" +
				"background-color:{6};\"" +
				">{3}<span style=\"position:absolute;right:0px;background-color:#b0e0e6;width:35px;\">{4}</span>" + 
				"<a href=\"#\" class=\"{2}\" style=\"height: 22px;\"></a>" +
				"</div>" +
				"</div>")
		SafeHtml cell(String grandParentStyleClass, String parentStyleClass,
				String aStyleClass, String value, String coverage, String quickTipText, String colorHex, String backgroundImage);
	}
	
	protected static Templates templates = GWT.create(Templates.class);
	
	public TaxonCell(MyGrid grid, TaxonMatrixView taxonMatrixView) {
		super(taxonMatrixView);
		this.grid = grid;
		this.taxonMatrixView = taxonMatrixView;
		this.styles = grid.getView().getAppearance().styles();
	}

	@Override
	public void render(Context context, Taxon value, SafeHtmlBuilder sb) {
		if (value == null)
			return;
		String grandParentStyleClass = columnHeaderStyles.header() + " " + columnHeaderStyles.head();
		String parentStyleClass = columnHeaderStyles.headInner();
		String aStyleClass = columnHeaderStyles.headButton();
		
		String quickTipText = value.getName();
		quickTipText += "<br>" + taxonMatrixView.getSummary(value);
		
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
				value.getName(), taxonMatrixView.getCoverage(value), quickTipText, colorHex, backgroundImage);
		sb.append(rendered);
	}

	private void restrictMenu(Menu columns) {
		int count = 0;
		for (int i = 0, len = taxonMatrixView.getTaxaCount(); i < len; i++) {
			if (taxonMatrixView.isHiddenTaxon(i)) {
				continue;
			}
			count++;
		}

		if (count == 1) {
			for (Widget item : columns) {
				CheckMenuItem ci = (CheckMenuItem) item;
				if (ci.isChecked()) {
					ci.disable();
				}
			}
		} else {
			for (int i = 0, len = columns.getWidgetCount(); i < len; i++) {
				Widget item = columns.getWidget(i);
				if (item instanceof Component) {
					((Component) item).enable();
				}
			}
		}
	}
	
	@Override
	protected Menu createContextMenu(final int colIndex, final int rowIndex) {
		final Menu menu = new RowMenu(taxonMatrixView, grid, rowIndex);
		return menu;
	}
}
