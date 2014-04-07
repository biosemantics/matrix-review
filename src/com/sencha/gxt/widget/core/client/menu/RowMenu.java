package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.MyGrid;
import com.sencha.gxt.widget.core.client.info.Info;

import edu.arizona.biosemantics.matrixreview.client.TaxonMatrixView;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class RowMenu extends Menu {

	private TaxonMatrixView taxonMatrixView;
	private int rowIndex;
	private GridView<Taxon> myGridView;
	private ColumnModel<Taxon> cm;
	private MyGrid grid;

	public RowMenu(final TaxonMatrixView taxonMatrixView, final MyGrid grid, final int rowIndex) {
		this.taxonMatrixView = taxonMatrixView;
		this.rowIndex = rowIndex;
		this.grid = grid;
		this.myGridView = grid.getView();
		this.cm = myGridView.getColumnModel();
		
		add(new HeaderMenuItem("Taxon"));
			
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
		add(item);
		
		item = new MenuItem();
		item.setText("Delete");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.deleteRow(rowIndex);
			}
		});
		add(item);
		
		item = new MenuItem("Move after");
		add(item);
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
		add(lockItem);	
		
		//menu.add(new SeparatorMenuItem());
		add(new HeaderMenuItem("View"));
		
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
					myGridView.restrictMenu(cm, rowMenu);
				}
			});
			rowMenu.add(check);
		}

		myGridView.restrictMenu(cm, rowMenu);
		rows.setEnabled(rowMenu.getWidgetCount() > 0);
		rows.setSubMenu(rowMenu);
		add(rows);

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
		
		//menu.add(new SeparatorMenuItem());
		add(new HeaderMenuItem("Annotation"));
		
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
		add(item);
		
		item = new MenuItem("Colorize");
		Menu colorMenu = new Menu();
		item.setSubMenu(colorMenu);
		MenuItem offItem = new MenuItem("None");
		offItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.setRowColor(rowIndex, null);
			}
		});
		colorMenu.add(offItem);
		for(final Color color : taxonMatrixView.getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					taxonMatrixView.setRowColor(rowIndex, color);
				}
			});
			colorMenu.add(colorItem);
		}
		
		add(item);
	}
	
}
