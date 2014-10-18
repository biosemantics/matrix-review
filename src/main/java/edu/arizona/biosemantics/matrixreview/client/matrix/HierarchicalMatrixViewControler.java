package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;

import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CollapseTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CollapseTaxaEvent.CollapseTaxaEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.ExpandTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ExpandTaxaEvent.ExpandTaxaEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.common.taxonomy.Rank;

public class HierarchicalMatrixViewControler extends MatrixViewControler implements CollapseTaxaEventHandler, ExpandTaxaEventHandler {

	public HierarchicalMatrixViewControler(EventBus eventBus, FrozenFirstColumTaxonTreeGrid taxonTreeGrid) {
		super(eventBus, taxonTreeGrid);
	}
	
	public HierarchicalMatrixViewControler(EventBus eventBus, FrozenFirstColumTaxonTreeGrid taxonTreeGrid, Model model) {
		super(eventBus, taxonTreeGrid, model);
	}

	@Override
	protected void addEventHandlers() {
		super.addEventHandlers();
		
		handlerRegistrations.add(eventBus.addHandler(CollapseTaxaEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(ExpandTaxaEvent.TYPE, this));
	}
	
	@Override
	public void onAdd(AddTaxonEvent event) {
		if(event.getParent() == null) {
			addRoot(event.getTaxon());
		} else {
			addRecursively(event.getParent(), event.getTaxon());
		}
	}

	@Override
	public void onRemove(RemoveTaxaEvent event) {
		removeTaxon(event.getTaxa());
	}

	@Override
	public void onModify(ModifyTaxonEvent event) {
		modifyTaxon(event.getTaxon(), event.getParent(), event.getRank(), event.getName(), event.getAuthor(), event.getYear());
	}
	
	protected void modifyTaxon(Taxon taxon, Taxon parent, Rank rank, String name, String author, String year) {
		boolean updateStore = false;
		updateStore = (taxon.getParent() == null && parent != null)
				|| (taxon.getParent() == null || !taxon.getParent().equals(
						parent));
		if (updateStore) {
			List<Taxon> taxa = new LinkedList<Taxon>();
			taxa.add(taxon);
			this.moveTaxaHierarchically(parent, 0, taxa);
		}
	}

	protected void moveTaxaHierarchically(Taxon parent, int index, List<Taxon> taxa) {
		for (Taxon taxon : taxa)
			removeRecursively(taxon);
		if (parent == null) {
			index = getMaximalValidRootIndex(index);
			addRecursively(index, taxa);
		} else {
			index = getMaximalValidIndex(parent, index);
			addRecursively(parent, index, taxa);
		}
	}

	private int addRecursively(int index, List<Taxon> taxa) {
		taxonStore.insert(index, taxa);
		for (Taxon taxon : taxa)
			addRecursively(taxon, 0, taxon.getChildren());
		return index;
	}

	private int addRecursively(Taxon parent, int index, List<Taxon> taxa) {
		taxonStore.insert(parent, index, taxa);
		for (Taxon taxon : taxa)
			addRecursively(taxon, 0, taxon.getChildren());
		return index;
	}
	
	private int getMaximalValidRootIndex(int index) {
		// drag and drop can sometime provides index that is out of bounds of
		// stores underlying list
		while (taxonStore.getRootCount() < index)
			index--;
		return index;
	}
	
	private int getMaximalValidIndex(Taxon parent, int index) {
		// drag and drop can sometime provides index that is out of bounds of
		// stores underlying list
		while (taxonStore.getChildCount(parent) < index)
			index--;
		return index;
	}
	
	protected void removeTaxon(Collection<Taxon> taxa) {
		for(final Taxon taxon : taxa) {
			if (!taxon.getChildren().isEmpty()) {
				String childrenString = "";
				for (Taxon child : taxon.getChildren()) {
					childrenString += child.getFullName() + ", ";
				}
				childrenString = childrenString.substring(0, childrenString.length() - 2);
				ConfirmMessageBox box = new ConfirmMessageBox(
						"Remove Taxon",
						"Removing the taxon will also remove all of it's descendants: "
								+ childrenString);
				box.addDialogHideHandler(new DialogHideHandler() {
					@Override
					public void onDialogHide(DialogHideEvent event) {
						if(event.getHideButton().equals(PredefinedButton.YES)) {
							removeRecursively(taxon);
						}
					}
					
				});
				box.show();
			} else {
				removeRecursively(taxon);
			}
		}
	}

	protected void removeRecursively(Taxon taxon) {
		taxonStore.remove(taxon);
		for (Taxon child : taxon.getChildren()) {
			taxonStore.remove(child);
		}
	}
	
	@Override
	protected void fillStore(Model model) {
		for(Taxon rootTaxon : model.getTaxonMatrix().getHierarchyRootTaxa()) {
			if(model.getTaxonMatrix().isVisiblyContained(rootTaxon)) {
				this.addRoot(rootTaxon);
			}
		}
	}
	
	private void addRoot(Taxon taxon) {
		taxonStore.add(taxon);
		for(Taxon child : taxon.getChildren()) 
			addRecursively(taxon, child);
	}
	
	private void addRecursively(Taxon parentToUse, Taxon taxon) {
		if(parentToUse == null) {
			if(model.getTaxonMatrix().isVisiblyContained(taxon))
				taxonStore.add(taxon);
		} else { 
			if(model.getTaxonMatrix().isVisiblyContained(taxon))
				taxonStore.add(parentToUse, taxon);
		}
		
		if(model.getTaxonMatrix().isVisiblyContained(taxon))
			parentToUse = taxon;
		for(Taxon child : taxon.getChildren())
			addRecursively(parentToUse, child);
	}
	
	@Override
	public void onExpand(ExpandTaxaEvent event) {
		taxonTreeGrid.setExpanded(event.getTaxa());
	}

	@Override
	public void onCollapse(CollapseTaxaEvent event) {
		taxonTreeGrid.setCollapsed(event.getTaxa());
	}

}
