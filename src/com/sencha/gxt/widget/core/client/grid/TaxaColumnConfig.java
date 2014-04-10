package com.sencha.gxt.widget.core.client.grid;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxaColumnConfig extends ColumnConfig<Taxon, Taxon> {

	private Filter<Taxon, ?> filter;

	public static class TaxonNameValueProvider implements ValueProvider<Taxon, Taxon> {

		@Override
		public Taxon getValue(Taxon object) {
			return object;
		}

		@Override
		public void setValue(Taxon object, Taxon value) {
			object.getTaxonMatrix().renameTaxon(object, value.getName());
		}

		@Override
		public String getPath() {
			return "/name";
		}

	}

	
	public TaxaColumnConfig() {
		super(new TaxonNameValueProvider(), 200, "Taxon Concept / Character");
	}

	public Filter<Taxon, ?> getFilter() {
		return filter;
	}

	public void setFilter(Filter<Taxon, ?> filter) {
		this.filter = filter;
	}
	
}
