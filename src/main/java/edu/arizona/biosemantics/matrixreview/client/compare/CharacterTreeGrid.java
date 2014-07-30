package edu.arizona.biosemantics.matrixreview.client.compare;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
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
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

/**
 * A single-column TreeGrid that shows a list of characters, grouped by organ. 
 * 
 * @author Andrew Stockton
 */

public class CharacterTreeGrid extends MaintainListStoreTreeGrid<CharacterTreeNode>{
	
	private CharacterTreeGrid(TreeStore<CharacterTreeNode> treeStore, ColumnModel<CharacterTreeNode> model, ColumnConfig<CharacterTreeNode, String> column){
		super(treeStore, model, column);
	}
	
	private CharacterTreeGrid(TreeStore<CharacterTreeNode> treeStore, ColumnModel<CharacterTreeNode> model, ColumnConfig<CharacterTreeNode, String> column, GridAppearance app, ControllerGridAppearance treeApp){
		super(treeStore, model, column, app, treeApp);
	}
	
	public static CharacterTreeGrid createNew(final EventBus eventBus, TreeStore<CharacterTreeNode> store, boolean useHeaderStyle){
		final CharacterTreeNodeProperties characterTreeNodeProperties = new CharacterTreeNodeProperties();
		ColumnConfig<CharacterTreeNode, String> column = new ColumnConfig<CharacterTreeNode, String>(characterTreeNodeProperties.name(), 150);
		column.setHeader("Characters");
		column.setMenuDisabled(true);
		
		List<ColumnConfig<CharacterTreeNode, ?>> columns = new ArrayList<ColumnConfig<CharacterTreeNode, ?>>();
		columns.add(column);
		ColumnModel<CharacterTreeNode> columnModel = new ColumnModel<CharacterTreeNode>(columns);
		
		CharacterTreeGrid grid;
		if (useHeaderStyle){
			GridAppearance ga = GWT.<GridAppearance> create(GridAppearance.class);
			ControllerGridAppearance cga = GWT.<ControllerGridAppearance> create(ControllerGridAppearance.class);
			grid = new CharacterTreeGrid(store, columnModel, column, ga, cga);
		} else
			grid = new CharacterTreeGrid(store, columnModel, column);
		
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		grid.getSelectionModel().addSelectionHandler(new SelectionHandler<CharacterTreeNode>(){
			@Override
			public void onSelection(SelectionEvent<CharacterTreeNode> event) {
				CharacterTreeNode nodeSelected = event.getSelectedItem();
				if (nodeSelected.getData() instanceof Character){
					eventBus.fireEvent(new ChangeComparingSelectionEvent(nodeSelected));
				}
			}
		});
		
		return grid;
	}
}
