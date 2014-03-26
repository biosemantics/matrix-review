package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.grid.MyGrid;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxonCell extends MenuExtendedCell<Taxon> {

	private MyGrid<Taxon> grid;

	public TaxonCell(MyGrid<Taxon> grid, TaxonMatrixView taxonMatrixView) {
		super(taxonMatrixView);
		this.grid = grid;
		this.taxonMatrixView = taxonMatrixView;
	}

	@Override
	public void render(Context context, Taxon value, SafeHtmlBuilder sb) {
		if (value == null)
			return;
		SafeHtml rendered = templates.cell(columnHeaderStyles.header() + " "
				+ columnHeaderStyles.head(), columnHeaderStyles.headInner(),
				columnHeaderStyles.headButton(), value.getName() + " Coverage: " + taxonMatrixView.getCoverage(value));
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
		
		
		return menu;
	}
}
