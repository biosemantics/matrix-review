package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.LockTaxonEvent.LockTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class LockTaxonEvent extends GwtEvent<LockTaxonEventHandler> {

	public interface LockTaxonEventHandler extends EventHandler {
		void onLock(LockTaxonEvent event);
	}
	
	public static Type<LockTaxonEventHandler> TYPE = new Type<LockTaxonEventHandler>();
	private Taxon taxon;
	private boolean lock;
	
	public LockTaxonEvent(Taxon taxon, boolean lock) {
		this.taxon = taxon;
		this.lock = lock;
	}
	
	@Override
	public GwtEvent.Type<LockTaxonEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LockTaxonEventHandler handler) {
		handler.onLock(this);
	}

	public static Type<LockTaxonEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public boolean isLock() {
		return lock;
	}
}