package com.sencha.gxt.widget.core.client.grid.editing;

import com.sencha.gxt.data.shared.Converter;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class TaxonConverter implements Converter<Taxon, String> {
	
	private TaxonMatrix taxonMatrix;
	public TaxonConverter(TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
	}
	
	@Override
	public Taxon convertFieldValue(String object) {
		return new Taxon(object, taxonMatrix);
	}
	@Override
	public String convertModelValue(Taxon object) {
		return object.getName();
	}
}