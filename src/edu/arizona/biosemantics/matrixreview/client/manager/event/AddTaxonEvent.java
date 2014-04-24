package edu.arizona.biosemantics.matrixreview.client.manager.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class AddTaxonEvent extends GwtEvent<AddTaxonEventHandler> {

	public static Type<AddTaxonEventHandler> TYPE = new Type<AddTaxonEventHandler>();
	private int index;
	private Taxon taxon;
	
	public AddTaxonEvent(int index, Taxon taxon) {
		this.index = index;
		this.taxon = taxon;
	}
	
	@Override
	public GwtEvent.Type<AddTaxonEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddTaxonEventHandler handler) {
		handler.onAddTaxon(this);
	}

	public static Type<AddTaxonEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public int getIndex() {
		return index;
	}

}