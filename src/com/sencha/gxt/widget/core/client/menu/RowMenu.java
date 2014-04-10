package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.info.Info;

import edu.arizona.biosemantics.matrixreview.client.manager.AnnotationManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ControlManager;
import edu.arizona.biosemantics.matrixreview.client.manager.DataManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ViewManager;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class RowMenu extends Menu {

	public RowMenu(final DataManager dataManager, final ViewManager viewManager, final ControlManager controlManager,
			final AnnotationManager annotationManager, final int rowIndex)	{
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
						dataManager.addTaxonAfter(rowIndex, name);
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
				dataManager.removeTaxon(rowIndex);
			}
		});
		add(item);
		
		item = new MenuItem();
		item.setText("Rename");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final Taxon taxon = dataManager.getTaxon(rowIndex);
				final PromptMessageBox nameBox = new PromptMessageBox(
						"Taxon Name", "");
				nameBox.setValue(taxon.getName());
				nameBox.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						String name = nameBox.getValue();
						dataManager.renameTaxon(rowIndex, name);
					}
				});
				nameBox.show();
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
				dataManager.moveTaxon(rowIndex, 0);
			}
		});
		moveMenu.add(item);
		
		final CheckMenuItem lockItem = new CheckMenuItem("Lock");
		lockItem.setChecked(controlManager.isLockedTaxon(rowIndex));
		lockItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean newValue = !controlManager.isLockedTaxon(rowIndex);
				lockItem.setChecked(newValue);
				controlManager.setLockedTaxon(rowIndex, newValue);
			}
		});
		add(lockItem);	
		
		//menu.add(new SeparatorMenuItem());
		add(new HeaderMenuItem("View"));

		int visibleRowCount = dataManager.getVisibleTaxaCount();
		for (int i = 0; i < visibleRowCount; i++) {
			if(i != rowIndex) {
				final int theI = i;
				item = new MenuItem(dataManager.getTaxon(i).getName());
				item.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						dataManager.moveTaxon(rowIndex, theI);
						/*Taxon taxon =  grid.getStore().remove(rowIndex);
						int finalI = theI;
						if(rowIndex < theI)
							finalI--;				
						grid.getStore().add(finalI + 1, taxon);*/
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
				box.setValue(annotationManager.getTaxonComment(rowIndex));
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						annotationManager.setTaxonComment(rowIndex, box.getValue());
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
				annotationManager.setTaxonColor(rowIndex, null);
			}
		});
		colorMenu.add(offItem);
		for(final Color color : annotationManager.getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					annotationManager.setTaxonColor(rowIndex, color);
				}
			});
			colorMenu.add(colorItem);
		}
		
		add(item);
	}
	
	
	private void restrictMenu(Menu rows) {
	//private void restrictMenu(ColumnModel<Taxon> cm, Menu columns) {
		//TODO for rows rather than columns
		/*int count = 0;
		for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
			if (hasHeaderValue(i)) {
				ColumnConfig<M, ?> cc = cm.getColumn(i);
				if (cc.isHidden() || !cc.isHideable()) {
					continue;
				}
				count++;
			}
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
				ColumnConfig<M, ?> config = cm.getColumn(i);
				if (item instanceof Component && config.isHideable()) {
					((Component) item).enable();
				}
			}
		} */
	}
}
