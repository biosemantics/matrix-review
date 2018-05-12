package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource.Import;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStateStyles;

public class TaxonGridAppearance extends GridBaseAppearance {
	
	public interface TaxonGridStyle extends GridStyle {
		
	}

	public interface TaxonGridResources extends GridResources {

		@Import(GridStateStyles.class)
		@Source({ "com/sencha/gxt/theme/base/client/grid/Grid.css",
				"TaxonGrid.css" })
		@Override
		TaxonGridStyle css();
	}

	public TaxonGridAppearance() {
		this(GWT.<TaxonGridResources> create(TaxonGridResources.class));
	}

	public TaxonGridAppearance(TaxonGridResources resources) {
		super(resources);
	}
	
	
}
