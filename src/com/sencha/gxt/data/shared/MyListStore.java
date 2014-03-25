package com.sencha.gxt.data.shared;

public class MyListStore<M> extends ListStore<M> {

	public MyListStore(ModelKeyProvider<? super M> keyProvider) {
		super(keyProvider);
	}
	
	public void enableAndRefreshFilters() {
	    this.filtersEnabled = true;
	    applyFilters();
	}
	
	public int sizeOfAllItems() {
		return this.allItems.size();
	}

	public M getFromAllItems(int indexOfAllItems) {
		return this.allItems.get(indexOfAllItems);
	}
}
