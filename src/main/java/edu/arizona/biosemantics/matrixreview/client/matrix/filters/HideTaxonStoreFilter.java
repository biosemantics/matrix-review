package edu.arizona.biosemantics.matrixreview.client.matrix.filters;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;

import edu.arizona.biosemantics.matrixreview.client.event.MatrixModeEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.MatrixMode;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class HideTaxonStoreFilter implements StoreFilter<Taxon> {

	public Set<Taxon> hiddenTaxa = new HashSet<Taxon>();
	private EventBus eventBus;
	private MatrixMode modelMode = MatrixMode.HIERARCHY; 

	public HideTaxonStoreFilter(EventBus eventBus) {
		this.eventBus = eventBus;
		addEventHandlers();
	}
	
	private void addEventHandlers() {
		eventBus.addHandler(MatrixModeEvent.TYPE, new MatrixModeEvent.MatrixModeEventHandler() {
			@Override
			public void onMode(MatrixModeEvent event) {
				modelMode = event.getMode();
			}
		});
	}

	@Override
	public boolean select(Store<Taxon> store, Taxon parent, Taxon item) {
		switch(modelMode) {
		case FLAT:
			return !hiddenTaxa.contains(item);
		case HIERARCHY:
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