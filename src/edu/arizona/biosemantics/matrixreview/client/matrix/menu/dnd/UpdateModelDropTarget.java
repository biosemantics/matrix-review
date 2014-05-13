package edu.arizona.biosemantics.matrixreview.client.matrix.menu.dnd;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.TreeGridDropTarget;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class UpdateModelDropTarget extends TreeGridDropTarget<Taxon> {

	private TaxonMatrix taxonMatrix;
	private EventBus eventBus;

	public UpdateModelDropTarget(EventBus eventBus, TreeGrid<Taxon> tree, TaxonMatrix taxonMatrix) {
		super(tree);
		this.eventBus = eventBus;
		this.taxonMatrix = taxonMatrix;
		
		setAllowSelfAsSource(true);
		setAllowDropOnLeaf(true);
	}
	
	//TODO: Check for consistency with ranks before making any chances to model whatsoever
	protected void appendModel(Taxon p, List<?> items, int index) {
		if (items.size() == 0)
			return;
		if (items.get(0) instanceof TreeStore.TreeNode) {
			@SuppressWarnings("unchecked")
			List<TreeStore.TreeNode<Taxon>> nodes = (List<TreeStore.TreeNode<Taxon>>) items;
			
			boolean validDrop = true;
			for(int i=0; i<nodes.size(); i++)
				if(!taxonMatrix.isValidParentChildRelation(p, nodes.get(i).getData()))
					validDrop = false;
			
			if(validDrop) {
				
				eventBus.fireEvent(new ModifyTaxonEvent(p, index, nodes));
				
				/*
				if (p == null) {
					getWidget().getTreeStore().addSubTree(index, nodes);
					for(int i=0; i<nodes.size(); i++) {
						//eventBus.fireEvent(new ModifyTaxonEvent());
						taxonMatrix.addTaxon(index + i, nodes.get(i).getData());
					}
				} else {
					getWidget().getTreeStore().addSubTree(p, index, nodes);
					for(int i=0; i<nodes.size(); i++) {
						taxonMatrix.addChild(p, index + i, nodes.get(i).getData());
					}
				}*/
			}
		} else {
			@SuppressWarnings("unchecked")
			List<Taxon> models = (List<Taxon>) items;
			
			boolean validDrop = true;
			for(int i=0; i<models.size(); i++)
				if(!taxonMatrix.isValidParentChildRelation(p, models.get(i)))
					validDrop = false;
			
			if(validDrop) {
				eventBus.fireEvent(new ModifyTaxonEvent(p, index, models));
			}
			
			/*if (p == null) {
				getWidget().getTreeStore().insert(index, models);
				for(int i=0; i<models.size(); i++)
					taxonMatrix.addTaxon(index + i, models.get(i));
			} else {
				getWidget().getTreeStore().insert(p, index, models);
				for(int i=0; i<models.size(); i++)
					taxonMatrix.addChild(p, index + i, models.get(i));
			}*/
		}
	}

}
