package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.CollapseTaxaEvent.CollapseTaxaEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class CollapseTaxaEvent extends  GwtEvent<CollapseTaxaEventHandler>  {

	public interface CollapseTaxaEventHandler extends EventHandler {
		void onCollapse(CollapseTaxaEvent event);
	}
	
	public static Type<CollapseTaxaEventHandler> TYPE = new Type<CollapseTaxaEventHandler>();
	private List<Taxon> taxa = new LinkedList<Taxon>();
	
	public CollapseTaxaEvent(List<Taxon> taxa) {
		this.taxa.addAll(taxa);
	}
	
	public CollapseTaxaEvent(Taxon taxon) {
		this.taxa.add(taxon);
	}

	@Override
	public GwtEvent.Type<CollapseTaxaEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CollapseTaxaEventHandler handler) {
		handler.onCollapse(this);
	}

	public List<Taxon> getTaxa() {
		return taxa;
	}


}