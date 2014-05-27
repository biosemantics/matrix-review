package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxonEvent.RemoveTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class RemoveTaxonEvent extends GwtEvent<RemoveTaxonEventHandler> {

	public interface RemoveTaxonEventHandler extends EventHandler {
		void onRemove(RemoveTaxonEvent event);
	}
	
	public static Type<RemoveTaxonEventHandler> TYPE = new Type<RemoveTaxonEventHandler>();
	private Taxon taxon;
	
	public RemoveTaxonEvent(Taxon taxon) {
		this.taxon = taxon;
	}
	
	@Override
	public GwtEvent.Type<RemoveTaxonEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RemoveTaxonEventHandler handler) {
		handler.onRemove(this);
	}

	public static Type<RemoveTaxonEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}	
	
}