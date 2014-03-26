package com.sencha.gxt.widget.core.client.grid;

import com.sencha.gxt.data.shared.ListStore;

import edu.arizona.biosemantics.matrixreview.client.TaxonMatrixView;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class MyGrid extends Grid<Taxon> {

	  public MyGrid(ListStore<Taxon> store, ColumnModel<Taxon> cm, TaxonMatrixView taxonMatrixView) {
	    super(store, cm, new MyGridView(taxonMatrixView));
	  }
	
	  //possibly need to override setView() also to make sure this is used with MyGridView only
}
