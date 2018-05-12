package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.Collection;
import java.util.LinkedList;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent.RemoveTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class RemoveTaxaEvent extends GwtEvent<RemoveTaxonEventHandler> {

	public interface RemoveTaxonEventHandler extends EventHandler {
		void onRemove(RemoveTaxaEvent event);
	}
	
	public static Type<RemoveTaxonEventHandler> TYPE = new Type<RemoveTaxonEventHandler>();
	private Collection<Taxon> taxa;
	
	public RemoveTaxaEvent(Collection<Taxon> taxa) {
		this.taxa = taxa;
	}
	
	public RemoveTaxaEvent(Taxon taxon) {
		this.taxa = new LinkedList<Taxon>();
		this.taxa.add(taxon);
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

	public Collection<Taxon> getTaxa() {
		return taxa;
	}	
	
}