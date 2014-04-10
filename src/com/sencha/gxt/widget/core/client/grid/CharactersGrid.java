package com.sencha.gxt.widget.core.client.grid;

import com.sencha.gxt.data.shared.ListStore;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class CharactersGrid extends Grid<Taxon> {

	public CharactersGrid(ListStore<Taxon> store, CharactersColumnModel columnModel, CharactersGridView view) {
		super(store, columnModel, view);
	}

	@Override
	public CharactersColumnModel getColumnModel() {
		return (CharactersColumnModel)cm;
	}
	
	@Override 
	public CharactersGridView getView() {
		return (CharactersGridView)view;
	}
	
}
