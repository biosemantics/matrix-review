package com.sencha.gxt.widget.core.client.grid.filters;

import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class CharactersGridFilters extends GridFilters<Taxon> {
	
	private int insertPositionFilters = 11;

	protected void onContextMenu(HeaderContextMenuEvent event) {
		int column = event.getColumnIndex();
		
		if (checkFilterItem == null) {
			checkFilterItem = new CheckMenuItem(DefaultMessages.getMessages()
					.gridFilters_filterText());
			checkFilterItem
					.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {

						@Override
						public void onCheckChange(
								CheckChangeEvent<CheckMenuItem> event) {
							CharactersGridFilters.this.onCheckChange(event);
						}
					});
		}

		checkFilterItem.setData("index", column);
		
		
		Filter<Taxon, ?> f = getFilter(grid.getColumnModel().getColumn(column).getValueProvider().getPath());
		
		if (f != null) {
			Menu filterMenu = f.getMenu();
			Menu menu = event.getMenu();
			checkFilterItem.setChecked(f.isActive(), true);
			checkFilterItem.setSubMenu(filterMenu);
			menu.insert(checkFilterItem, insertPositionFilters);
		}
	}
}
