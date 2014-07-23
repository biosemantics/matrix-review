package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class CharacterTreeGrid extends MaintainListStoreTreeGrid<CharacterTreeNode>{
	
	private CharacterTreeGrid(TreeStore<CharacterTreeNode> treeStore, ColumnModel<CharacterTreeNode> model, ColumnConfig<CharacterTreeNode, String> column){
		super(treeStore, model, column);
	}
	
	public static CharacterTreeGrid createNew(final EventBus eventBus, TreeStore<CharacterTreeNode> store){
		final CharacterTreeNodeProperties characterTreeNodeProperties = new CharacterTreeNodeProperties();
		ColumnConfig<CharacterTreeNode, String> column = new ColumnConfig<CharacterTreeNode, String>(characterTreeNodeProperties.name(), 150);
		column.setHeader("Characters");
		
		List<ColumnConfig<CharacterTreeNode, ?>> columns = new ArrayList<ColumnConfig<CharacterTreeNode, ?>>();
		columns.add(column);
		ColumnModel<CharacterTreeNode> columnModel = new ColumnModel<CharacterTreeNode>(columns);
		
		CharacterTreeGrid grid = new CharacterTreeGrid(store, columnModel, column);
		//TODO: check for tree store with 0 elements.
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		grid.getSelectionModel().addSelectionHandler(new SelectionHandler<CharacterTreeNode>(){
			@Override
			public void onSelection(SelectionEvent<CharacterTreeNode> event) {
				CharacterTreeNode nodeSelected = event.getSelectedItem();
				if (nodeSelected.getData() instanceof Character){
					Character selectedCharacter = (Character)nodeSelected.getData();
					eventBus.fireEvent(new ChangeComparingSelectionEvent(selectedCharacter));
				}
			}
		});
		
		return grid;
	}
}
