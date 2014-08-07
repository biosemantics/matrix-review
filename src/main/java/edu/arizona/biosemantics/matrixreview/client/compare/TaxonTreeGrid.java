package edu.arizona.biosemantics.matrixreview.client.compare;

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
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.ControllerGridAppearance;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonPropertiesByLocation;

/**
 * A single-column TreeGrid that shows a hierarchy of Taxons.
 * 
 * @author Andrew Stockton
 */

public class TaxonTreeGrid extends MaintainListStoreTreeGrid<Taxon>{
	
	private TaxonTreeGrid(TreeStore<Taxon> treeStore, ColumnModel<Taxon> model, ColumnConfig<Taxon, String> column){
		super(treeStore, model, column);
	}
	
	private TaxonTreeGrid(TreeStore<Taxon> treeStore, ColumnModel<Taxon> model, ColumnConfig<Taxon, String> column, GridAppearance app, ControllerGridAppearance treeApp){
		super(treeStore, model, column, app, treeApp);
	}
	
	public static TaxonTreeGrid createNew(final EventBus eventBus, TreeStore<Taxon> store, boolean useHeaderStyle){
		final TaxonPropertiesByLocation taxonProperties = new TaxonPropertiesByLocation();
		ColumnConfig<Taxon, String> column = new ColumnConfig<Taxon, String>(taxonProperties.fullName(), 200);
		column.setHeader("Taxon Concept / Character");
		column.setMenuDisabled(true);
		
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>();
		columns.add(column);
		ColumnModel<Taxon> columnModel = new ColumnModel<Taxon>(columns);
		
		TaxonTreeGrid grid;
		if (useHeaderStyle){
			GridAppearance ga = GWT.<GridAppearance> create(GridAppearance.class);
			ControllerGridAppearance cga = GWT.<ControllerGridAppearance> create(ControllerGridAppearance.class);
			grid = new TaxonTreeGrid(store, columnModel, column, ga, cga);
		} else
			grid = new TaxonTreeGrid(store, columnModel, column);
		
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		return grid;
	}
}
