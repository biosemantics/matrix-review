package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;

public class TaxonTreeGrid extends MaintainListStoreTreeGrid<Taxon>{
	
	private TaxonTreeGrid(TreeStore<Taxon> treeStore, ColumnModel<Taxon> model, ColumnConfig<Taxon, String> column){
		super(treeStore, model, column);
	}
	
	public static TaxonTreeGrid createNew(final EventBus eventBus, TreeStore<Taxon> store){
		final TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);
		ColumnConfig<Taxon, String> column = new ColumnConfig<Taxon, String>(taxonProperties.fullName(), 200);
		column.setHeader("Taxon Concept / Character");
		
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>();
		columns.add(column);
		ColumnModel<Taxon> columnModel = new ColumnModel<Taxon>(columns);
		
		TaxonTreeGrid grid = new TaxonTreeGrid(store, columnModel, column);
		
		//TODO: check for tree store with 0 elements.
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		grid.getSelectionModel().addSelectionHandler(new SelectionHandler<Taxon>(){
			@Override
			public void onSelection(SelectionEvent<Taxon> event) {
				Taxon taxonSelected = event.getSelectedItem();
				eventBus.fireEvent(new ChangeComparingSelectionEvent(taxonSelected));
			}
		});
		
		return grid;
	}
}
