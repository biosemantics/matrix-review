package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.info.Info;

import edu.arizona.biosemantics.matrixreview.client.manager.AnnotationManager;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;

public class ValueMenu extends Menu {

	public ValueMenu(final AnnotationManager annotationManager, final int rowIndex, final int colIndex) {		
		add(new HeaderMenuItem("Annotation"));
		
		MenuItem item = new MenuItem("Comment");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
				box.setValue(annotationManager.getComment(rowIndex, colIndex));
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						annotationManager.setComment(rowIndex, colIndex, box.getValue());
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
				annotationManager.setColor(rowIndex, colIndex, null);
			}
		});
		colorMenu.add(offItem);
		for(final Color color : annotationManager.getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					annotationManager.setColor(rowIndex, colIndex, color);
				}
			});
			colorMenu.add(colorItem);
		}
		
		add(item);
	}
	
}
