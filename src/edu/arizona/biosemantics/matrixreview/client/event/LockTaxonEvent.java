package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.LockTaxonEvent.LockCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class LockTaxonEvent extends GwtEvent<LockCharacterEventHandler> {

	public interface LockCharacterEventHandler extends EventHandler {
		void onLock(LockTaxonEvent event);
	}
	
	public static Type<LockCharacterEventHandler> TYPE = new Type<LockCharacterEventHandler>();
	private Taxon taxon;
	private boolean lock;
	
	public LockTaxonEvent(Taxon taxon, boolean lock) {
		this.taxon = taxon;
		this.lock = lock;
	}
	
	@Override
	public GwtEvent.Type<LockCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LockCharacterEventHandler handler) {
		handler.onLock(this);
	}

	public static Type<LockCharacterEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public boolean isLock() {
		return lock;
	}
}