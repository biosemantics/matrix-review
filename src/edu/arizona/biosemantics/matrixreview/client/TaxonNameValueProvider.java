package edu.arizona.biosemantics.matrixreview.client;

import com.sencha.gxt.core.client.ValueProvider;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxonNameValueProvider implements ValueProvider<Taxon, Taxon> {

	@Override
	public Taxon getValue(Taxon object) {
		return object;
	}

	@Override
	public void setValue(Taxon object, Taxon value) {
		object.setName(value.getName());
	}

	@Override
	public String getPath() {
		return "/name";
	}

}
