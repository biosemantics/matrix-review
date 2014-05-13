package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxonEvent.MoveTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.old.DataManager.MergeMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class MoveTaxonEvent extends GwtEvent<MoveTaxonEventHandler> {

	public interface MoveTaxonEventHandler extends EventHandler {
		void onMove(MoveTaxonEvent event);
	}
	
	public static Type<MoveTaxonEventHandler> TYPE = new Type<MoveTaxonEventHandler>();
	
	private Taxon taxon;
	private Taxon after;
	private Taxon aftersParent;

	public MoveTaxonEvent(Taxon taxon, Taxon after, Taxon aftersParent) {
		this.taxon = taxon;
		this.after = after;
		this.aftersParent = aftersParent;
	}
	
	@Override
	protected void dispatch(MoveTaxonEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveTaxonEventHandler> getAssociatedType() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public Taxon getAfter() {
		return after;
	}

	public Taxon getAftersParent() {
		return aftersParent;
	}


	
}
