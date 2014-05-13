package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.menu.Menu;

public abstract class WidgetCreator {
	
	protected Menu contextMenu = null;
	
	public abstract Widget create();
	
	public boolean hasContextMenu() {
		return contextMenu != null;
	}
	
	public Menu getContextMenu() {
		return contextMenu;
	}
	
}
