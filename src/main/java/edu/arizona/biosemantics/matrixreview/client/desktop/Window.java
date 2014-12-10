package edu.arizona.biosemantics.matrixreview.client.desktop;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.desktop.widget.AbstractWindowManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.WindowManager;

public class Window extends FramedPanel {

	private int framedPanelWidth = 300;
	private int framedPanelHeight = 300;
	private WindowManager manager;

	public Window(boolean refreshable) {
		createToolButtons(refreshable);
		getElement().getStyle().setMargin(10, Unit.PX);
		setPixelSize(framedPanelWidth, framedPanelHeight);
		setBodyBorder(true);
	}
	
	public void setWindowManager(WindowManager manager) {
		this.manager = manager;
	}

	private void createToolButtons(boolean refreshable) {
		if(refreshable) { 
			ToolButton refreshButton = new ToolButton(ToolButton.REFRESH);
			refreshButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					manager.refreshContent();
				}
			});
			addTool(refreshButton);
		}

		//ToolButton gearButton = new ToolButton(ToolButton.GEAR);
		//panel.addTool(gearButton);
		ToolButton closeButton = new ToolButton(ToolButton.CLOSE);
		addTool(closeButton);
		closeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				hide();
			}
		});
		setCollapsible(false);
	}

	public int getFramedPanelHeight() {
		return this.framedPanelHeight;
	}
	
	public int getFramedPanelWidth() {
		return this.framedPanelWidth;
	}

	public WindowManager getManager() {
		return manager;
	}

}
