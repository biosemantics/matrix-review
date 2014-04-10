package com.sencha.gxt.widget.core.client.grid.filters;

import java.util.HashSet;
import java.util.Set;

import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class HideTaxonStoreFilter implements StoreFilter<Taxon> {

	public Set<Taxon> hiddenTaxa = new HashSet<Taxon>();

	@Override
	public boolean select(Store<Taxon> store, Taxon parent, Taxon item) {
		return !hiddenTaxa.contains(item);
	}

	public void addHiddenTaxa(Taxon taxon) {
		this.hiddenTaxa.add(taxon);
	}

	public void removeHiddenTaxa(Taxon taxon) {
		this.hiddenTaxa.remove(taxon);
	}

	public boolean isHidden(Taxon taxon) {
		return this.hiddenTaxa.contains(taxon);
	}

	public Set<Taxon> getHiddenTaxa() {
		return this.hiddenTaxa;
	}

}