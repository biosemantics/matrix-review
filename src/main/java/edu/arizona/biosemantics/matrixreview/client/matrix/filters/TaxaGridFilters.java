package edu.arizona.biosemantics.matrixreview.client.matrix.filters;

import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HeaderContextMenuEvent;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Menu;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

/**
 * GridFilters only filters based on currently visible items. 
 * I.e. if a leaf is in a collapsed parent it is not considered to be left for the final result
 * This class is created to consider these as well.
 * @author rodenhausen
 */
public class TaxaGridFilters extends GridFilters<Taxon> {
	
	private int insertPositionFilters = 1;

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
							TaxaGridFilters.this.onCheckChange(event);
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
	
	//TODO Treestore applyFilters() has to be modified to get the desired filtering behavior. Here is not the correct location
	@Override
	protected StoreFilter<Taxon> getModelFilter() {
		StoreFilter<Taxon> storeFilter = new StoreFilter<Taxon>() {
			@Override
			public boolean select(Store<Taxon> store, Taxon parent, Taxon item) {
				for (Filter<Taxon, ?> f : filters.values()) {
					if (f.isActivatable() && f.isActive() && !f.validateModel(item)) {
						return false;
					}
				}
				return true;
			}
		};
		return storeFilter;
	}
	


}
