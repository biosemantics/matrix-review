package edu.arizona.biosemantics.matrixreview.client;

import com.sencha.gxt.core.client.ValueProvider;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxonNameValueProvider implements ValueProvider<Taxon, String> {

	@Override
	public String getValue(Taxon object) {
		return object.getName();
	}

	@Override
	public void setValue(Taxon object, String value) {
		object.setName(value);
	}

	@Override
	public String getPath() {
		return "/name";
	}

}
