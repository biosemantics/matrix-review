package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent.AddTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class AddTaxonEvent extends GwtEvent<AddTaxonEventHandler> {

	public interface AddTaxonEventHandler extends EventHandler {
		void onAdd(AddTaxonEvent event);
	}
	
	public static Type<AddTaxonEventHandler> TYPE = new Type<AddTaxonEventHandler>();
	private Taxon parent;
	private Taxon taxon;
	
	public AddTaxonEvent(Taxon taxon, Taxon parent) {
		this.taxon = taxon;
		this.parent = parent;
	}
		
	@Override
	public GwtEvent.Type<AddTaxonEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddTaxonEventHandler handler) {
		handler.onAdd(this);
	}

	public static Type<AddTaxonEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getParent() {
		return parent;
	}

	public Taxon getTaxon() {
		return taxon;
	}


	
	
}