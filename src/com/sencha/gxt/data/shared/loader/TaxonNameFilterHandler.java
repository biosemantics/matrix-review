package com.sencha.gxt.data.shared.loader;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxonNameFilterHandler extends FilterHandler<Taxon> {

	@Override
	public Taxon convertToObject(String value) {
		//How to do this? Cell rendering gets passed in from valueprovider. If not Taxon instance passed in, coverage can not be display in same cell
		//because that information would not be easily accessible. Therefore ValueProvider has to provide taxon, which in turn requires to create own
		//filter that works StringFilter-like on taxon name cells. The filter then requires this filterHandler.
		return null;
	}

	@Override
	public String convertToString(Taxon object) {
		return object.getName();
	}

}
