package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;

public class TaxonTreeGrid extends MaintainListStoreTreeGrid<Taxon>{
	
	private EventBus eventBus;
	
	private TaxonTreeGrid(EventBus eventBus, TreeStore<Taxon> treeStore, ColumnModel<Taxon> model, ColumnConfig<Taxon, String> column){
		super(treeStore, model, column);
		this.eventBus = eventBus;
	}
	
	public static TaxonTreeGrid createNew(EventBus eventBus, TreeStore<Taxon> store){
		final TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);
		ColumnConfig<Taxon, String> column = new ColumnConfig<Taxon, String>(taxonProperties.fullName(), 200);
		column.setHeader("Taxon Concept / Character");
		
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>();
		columns.add(column);
		ColumnModel<Taxon> columnModel = new ColumnModel<Taxon>(columns);
		
		return new TaxonTreeGrid(eventBus, store, columnModel, column);
	}
}
