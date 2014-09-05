package edu.arizona.biosemantics.matrixreview.client.matrix.filters;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;

import edu.arizona.biosemantics.matrixreview.client.event.ModelModeEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView.ModelMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class HideTaxonStoreFilter implements StoreFilter<Taxon> {

	public Set<Taxon> hiddenTaxa = new HashSet<Taxon>();
	private EventBus eventBus;
	private ModelMode modelMode = ModelMode.TAXONOMIC_HIERARCHY; 

	public HideTaxonStoreFilter(EventBus eventBus) {
		this.eventBus = eventBus;
		addEventHandlers();
	}
	
	private void addEventHandlers() {
		eventBus.addHandler(ModelModeEvent.TYPE, new ModelModeEvent.ModelModeEventHandler() {
			@Override
			public void onMode(ModelModeEvent event) {
				modelMode = event.getMode();
			}
		});
	}

	@Override
	public boolean select(Store<Taxon> store, Taxon parent, Taxon item) {
		switch(modelMode) {
		case FLAT:
			return !hiddenTaxa.contains(item);
		case CUSTOM_HIERARCHY:
		case TAXONOMIC_HIERARCHY:
			if(hiddenTaxa.contains(item) || hidenParent(item)) {
				return false;
			}
			return true;
		}
		return true;
	}

	private boolean hidenParent(Taxon item) {
		return item.getParent() != null && (hiddenTaxa.contains(item.getParent()) || hidenParent(item.getParent()));
	}

	public void addHiddenTaxa(Set<Taxon> taxa) {
		this.hiddenTaxa.addAll(taxa);
	}

	public void removeHiddenTaxa(Set<Taxon> taxa) {
		this.hiddenTaxa.removeAll(taxa);
	}

	public boolean isHidden(Taxon taxon) {
		return this.hiddenTaxa.contains(taxon);
	}

	public Set<Taxon> getHiddenTaxa() {
		return this.hiddenTaxa;
	}

}