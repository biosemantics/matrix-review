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
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonPropertiesByLocation;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonTreeNodeProperties;

/**
 * A single-column TreeGrid that shows a hierarchy of Taxons.
 * 
 * @author Andrew Stockton
 */

public class TaxonTreeGrid extends MaintainListStoreTreeGrid<TaxonTreeNode>{
	
	private TaxonTreeGrid(TreeStore<TaxonTreeNode> treeStore, ColumnModel<TaxonTreeNode> model, ColumnConfig<TaxonTreeNode, String> column){
		super(treeStore, model, column);
	}
	
	private TaxonTreeGrid(TreeStore<TaxonTreeNode> treeStore, ColumnModel<TaxonTreeNode> model, ColumnConfig<TaxonTreeNode, String> column, GridAppearance app, ControllerGridAppearance treeApp){
		super(treeStore, model, column, app, treeApp);
	}
	
	public static TaxonTreeGrid createNew(final EventBus eventBus, TreeStore<TaxonTreeNode> store, boolean useHeaderStyle, MatrixVersion currentVersion){
		final TaxonTreeNodeProperties taxonProperties = new TaxonTreeNodeProperties(currentVersion);
		ColumnConfig<TaxonTreeNode, String> column = new ColumnConfig<TaxonTreeNode, String>(taxonProperties.currentTaxonName(), 200);
		column.setHeader("Taxon Concept / Character");
		column.setMenuDisabled(true);
		
		List<ColumnConfig<TaxonTreeNode, ?>> columns = new ArrayList<ColumnConfig<TaxonTreeNode, ?>>();
		columns.add(column);
		ColumnModel<TaxonTreeNode> columnModel = new ColumnModel<TaxonTreeNode>(columns);
		
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
