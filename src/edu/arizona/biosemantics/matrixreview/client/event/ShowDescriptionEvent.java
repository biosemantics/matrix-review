package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ShowDescriptionEvent.ShowDescriptionEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class ShowDescriptionEvent extends GwtEvent<ShowDescriptionEventHandler> {

	public interface ShowDescriptionEventHandler extends EventHandler {
		void onShow(ShowDescriptionEvent event);
	}
	
	public static Type<ShowDescriptionEventHandler> TYPE = new Type<ShowDescriptionEventHandler>();
	private Taxon taxon;
	
	public ShowDescriptionEvent(Taxon taxon) {
		this.taxon = taxon;
	}
	
	@Override
	public GwtEvent.Type<ShowDescriptionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowDescriptionEventHandler handler) {
		handler.onShow(this);
	}

	public static Type<ShowDescriptionEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}
	
	
}