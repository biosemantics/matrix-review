package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Format;
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
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxonCell extends MenuExtendedCell<Taxon> {

	private MyGrid grid;
	private GridStyles styles;

	interface Templates extends SafeHtmlTemplates {
		@SafeHtmlTemplates.Template("<div qtitle=\"Summary\" qtip=\"{5}\">" +
				"<div class=\"{1}\" style=\"width: calc(100% - 9px); height:14px\">{3}" +
				"<span style=\"position:absolute;right:0px;background-color:#b0e0e6;width:35px;\">{4}</span>" + 
				"<a href=\"#\" class=\"{2}\" style=\"height: 22px;\"></a>" +
				"</div>" +
				"</div>")
		SafeHtml cell(String grandParentStyleClass, String parentStyleClass,
				String aStyleClass, String value, String coverage, String quickTipText);
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
		
		/*String cellClasses = "";
		if (grid.getView().isShowDirtyCells() && r != null
				&& r.getChange(columnConfig.getValueProvider()) != null && value.getComment().isEmpty()) {
			cellClasses += " " + styles.cellDirty();
		} else if(grid.getView().isShowDirtyCells() && r != null && r.getChange(columnConfig.getValueProvider()) != null && 
				!value.getComment().isEmpty()) {
			cellClasses += " " + styles.cellDirtyCommented();
		} else if(!value.getComment().isEmpty()) {
			cellClasses += " " + styles.cellCommented();
		}
		if(!value.getComment().isEmpty()) {
			grandParentStyleClass += " " + cellClasses;
		}*/
		SafeHtml rendered = templates.cell(grandParentStyleClass, parentStyleClass, aStyleClass, 
				value.getName(), taxonMatrixView.getCoverage(value), taxonMatrixView.getSummary(value));
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
		final Menu menu = new Menu();
		
		MenuItem item = new MenuItem();
		item.setText("Add");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final PromptMessageBox nameBox = new PromptMessageBox(
						"Taxon Name", "");
				nameBox.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						String name = nameBox.getValue();
						taxonMatrixView.addTaxonAfter(rowIndex, name);
					}
				});
				nameBox.show();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Delete");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.deleteRow(rowIndex);
			}
		});
		menu.add(item);
		
		final CheckMenuItem lockItem = new CheckMenuItem("Lock");
		lockItem.setChecked(taxonMatrixView.isLockedRow(rowIndex));
		lockItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean newValue = !taxonMatrixView.isLockedRow(rowIndex);
				lockItem.setChecked(newValue);
				taxonMatrixView.setLockedRow(rowIndex, newValue);
			}
		});
		menu.add(lockItem);	
		
		
		MenuItem rows = new MenuItem();
		rows.setText("Taxa");
		// rows.setIcon(header.getAppearance().columnsIcon());
		// rows.setData("gxt-columns", "true");

		final Menu rowMenu = new Menu();

		int rowCount = taxonMatrixView.getTaxaCount();
		for (int i = 0; i < rowCount; i++) {
			Taxon taxon = taxonMatrixView.getTaxonFromAll(i);
			final int finalRow = i;
			final CheckMenuItem check = new CheckMenuItem();
			check.setHideOnClick(false);
			check.setHTML(taxon.getName());
			check.setChecked(!taxonMatrixView.isHiddenTaxon(i));
			check.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
				@Override
				public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
					taxonMatrixView.setHiddenTaxon(finalRow,
							!taxonMatrixView.isHiddenTaxon(finalRow));
					restrictMenu(rowMenu);
				}
			});
			rowMenu.add(check);
		}

		restrictMenu(rowMenu);
		rows.setEnabled(rowMenu.getWidgetCount() > 0);
		rows.setSubMenu(rowMenu);
		menu.add(rows);
		
		item = new MenuItem("Move after");
		menu.add(item);
		Menu moveMenu = new Menu();
		item.setSubMenu(moveMenu);

		item = new MenuItem("start");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Taxon taxon =  grid.getStore().remove(rowIndex);
				grid.getStore().add(0, taxon);
			}
		});
		moveMenu.add(item);
		
		int visibleRowCount = taxonMatrixView.getVisibleTaxaCount();
		for (int i = 0; i < visibleRowCount; i++) {
			if(i != rowIndex) {
				final int theI = i;
				item = new MenuItem(taxonMatrixView.getVisibleTaxon(i).getName());
				item.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						Taxon taxon =  grid.getStore().remove(rowIndex);
						int finalI = theI;
						if(rowIndex < theI)
							finalI--;				
						grid.getStore().add(finalI + 1, taxon);
					}
				});
				moveMenu.add(item);
			}
		}
		
		item = new MenuItem("Comment");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
				box.setValue(taxonMatrixView.getRowComment(rowIndex));
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						taxonMatrixView.setRowComment(rowIndex, box.getValue());
						String comment = Format.ellipse(box.getValue(), 80);
						String message = Format.substitute("'{0}' saved", new Params(comment));
						Info.display("Comment", message);
					}
				});
				box.show();
			}
		});
		menu.add(item);
		
		
		return menu;
	}
}
