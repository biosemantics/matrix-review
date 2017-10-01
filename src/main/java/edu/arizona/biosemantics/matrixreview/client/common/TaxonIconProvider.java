package edu.arizona.biosemantics.matrixreview.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.data.shared.IconProvider;

import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonomyImages;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class TaxonIconProvider implements IconProvider<Taxon> {

	private static TaxonomyImages taxonomyImages = GWT.create(TaxonomyImages.class);
	
	@Override
	public ImageResource getIcon(Taxon model) {
		switch(model.getRank()) {
		case FAMILY:
			return taxonomyImages.f();
		case GENUS:
			return taxonomyImages.g();
		case SPECIES:
			return taxonomyImages.s();
		default:
			return taxonomyImages.yellow();
		}
	}

}
