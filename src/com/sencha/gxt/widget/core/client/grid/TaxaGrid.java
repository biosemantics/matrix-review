package com.sencha.gxt.widget.core.client.grid;

import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Size;
import com.sencha.gxt.data.shared.ListStore;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxaGrid extends Grid<Taxon> {

	public TaxaGrid(ListStore<Taxon> store, TaxaColumnModel columnModel, TaxaGridView view) {
		super(store, columnModel, view);
	}

	@Override
	public TaxaColumnModel getColumnModel() {
		return (TaxaColumnModel)cm;
	}
	
	@Override 
	public TaxaGridView getView() {
		return (TaxaGridView)view;
	}
	
	@Override
	protected Size adjustSize(Size size) {
		// this is a tricky part - convince the grid to draw just slightly too wide
		// and so push the scrollbar out of sight
		return new Size(size.getWidth() + XDOM.getScrollBarWidth()- 1, size.getHeight());
	}
	
}