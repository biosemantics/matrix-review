package com.sencha.gxt.widget.core.client.treegrid;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;

/**
 * Can return the ListStore it uses internally for it's grid portion.
 * Does not recreate a new ListStore upon reconfigure but reuses the old one so any users
 * of the ListStore hold the correct reference. Requirement is, though, that the same 
 * instance of treeStore is passed in reconfigure.
 * 
 * @author rodenhausen
 */
public class MaintainListStoreTreeGrid<M> extends TreeGrid<M> {

	public MaintainListStoreTreeGrid(TreeStore<M> store, ColumnModel<M> cm,
			ColumnConfig<M, ?> treeColumn) {
		super(store, cm, treeColumn);
	}

	public MaintainListStoreTreeGrid(TreeStore<M> store, ColumnModel<M> cm,
			ColumnConfig<M, ?> treeColumn, GridAppearance appearance) {
		super(store, cm, treeColumn, appearance);
	}

	public MaintainListStoreTreeGrid(TreeStore<M> store, ColumnModel<M> cm,
			ColumnConfig<M, ?> treeColumn, GridAppearance appearance,
			TreeAppearance treeAppearance) {
		super(store, cm, treeColumn, appearance, treeAppearance);
	}

	@Override
	public void reconfigure(TreeStore<M> store, ColumnModel<M> cm,
			ColumnConfig<M, ?> treeColumn) {
		if (isLoadMask()) {
			mask(DefaultMessages.getMessages().loadMask_msg());
		}
		this.store.clear();

		nodes.clear();
		nodesByDomId.clear();

		if(!store.equals(treeStore))
			this.store = createListStore();

		if (storeHandlerRegistration != null) {
			storeHandlerRegistration.removeHandler();
		}

		if(!store.equals(treeStore)) {
			treeStore = store;
			if (treeStore != null) {
				storeHandlerRegistration = treeStore.addStoreHandlers(storeHandler);
			}
		}

		treeGridView.initData(this.store, cm);

		this.cm = cm;
		setTreeColumn(treeColumn);
		// rebind the sm
		setSelectionModel(sm);
		if (isViewReady()) {
			view.refresh(true);
			doInitialLoad();
		}

		if (isLoadMask()) {
			unmask();
		}
	}
	
	public ListStore<M> getListStore() {
		return this.store;
	}

}
