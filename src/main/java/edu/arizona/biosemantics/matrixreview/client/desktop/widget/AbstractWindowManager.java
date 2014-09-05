package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.desktop.Window;

public abstract class AbstractWindowManager implements WindowManager {
	
	protected Menu contextMenu = null;
	protected EventBus subMatrixEventBus;
	protected Window window;
	
	public AbstractWindowManager(EventBus subMatrixEventBus, Window window) {
		this.subMatrixEventBus = subMatrixEventBus;
		this.window = window;		
		addEventHandlers();
	}
	
	public void init() {
		refreshContent();
		refreshTitle();
		refreshContextMenu();
	}
	
	@Override
	public void refreshContextMenu() {
		Menu menu = new Menu();
		MenuItem refreshItem = new MenuItem("Refresh");
		refreshItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				refreshContent();
			}
		});
		menu.add(refreshItem);
		//menu.add(new MenuItem("Axis layout??? Sorting?"));
		//menu.add(new MenuItem("Delte"));
		MenuItem closeItem = new MenuItem("Close");
		menu.add(closeItem);
		closeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				window.hide();
			}
		});
		window.setContextMenu(menu);
	}

	protected abstract void addEventHandlers();	
}
