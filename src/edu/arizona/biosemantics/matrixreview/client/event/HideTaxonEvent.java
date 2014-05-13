package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.HideTaxonEvent.HideCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class HideTaxonEvent extends GwtEvent<HideCharacterEventHandler> {

	public interface HideCharacterEventHandler extends EventHandler {
		void onHide(HideTaxonEvent event);
	}
	
	public static Type<HideCharacterEventHandler> TYPE = new Type<HideCharacterEventHandler>();
	private boolean hide;
	private Taxon taxon;
	
	public HideTaxonEvent(Taxon taxon, boolean hide) {
		this.taxon = taxon;
		this.hide = hide;
	}

	@Override
	public GwtEvent.Type<HideCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HideCharacterEventHandler handler) {
		handler.onHide(this);
	}

	public static Type<HideCharacterEventHandler> getTYPE() {
		return TYPE;
	}
	public Taxon getTaxon() {
		return taxon;
	}

	public boolean isHide() {
		return hide;
	}	
	
}