package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.Collection;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.common.taxonomy.Rank;

public class FlatMatrixViewControler extends MatrixViewControler {

	public FlatMatrixViewControler(EventBus eventBus, FrozenFirstColumTaxonTreeGrid taxonTreeGrid) {
		super(eventBus, taxonTreeGrid);
	}
	
	public FlatMatrixViewControler(EventBus eventBus, FrozenFirstColumTaxonTreeGrid taxonTreeGrid, Model model) {
		super(eventBus, taxonTreeGrid, model);
	}

	@Override
	public void onAdd(AddTaxonEvent event) {
		addTaxon(event.getTaxon());
	}

	private void addTaxon(Taxon taxon) {
		taxonStore.add(taxon);
	}

	@Override
	public void onRemove(RemoveTaxaEvent event) {
		removeTaxa(event.getTaxa());
	}

	private void removeTaxa(Collection<Taxon> taxa) {
		for(Taxon taxon : taxa)
			removeTaxon(taxon);
	}

	private void removeTaxon(Taxon taxon) {
		taxonStore.remove(taxon);
	}

	@Override
	public void onModify(ModifyTaxonEvent event) {
		modifyTaxon(event.getTaxon(), event.getParent(), event.getRank(), event.getName(), event.getAuthor(), event.getYear());
	}

	private void modifyTaxon(Taxon taxon, Taxon parent, Rank rank,
			String name, String author, String year) {
		if(Rank.isValidParentChild(parent == null ? null : parent.getRank(), rank)) {
			taxonStore.update(taxon);
		} else {
			AlertMessageBox alertMessageBox = new AlertMessageBox("Modify Taxon", "Unable to modify: Incompatible rank of taxon with parent.");
			alertMessageBox.show();
		}
	}

	@Override
	protected void fillStore(Model model) {
		for(Taxon taxon : model.getTaxonMatrix().getVisibleFlatTaxa()) {
			taxonStore.add(taxon);
		}
	}

}
