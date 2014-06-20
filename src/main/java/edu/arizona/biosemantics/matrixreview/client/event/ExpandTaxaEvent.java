package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ExpandTaxaEvent.ExpandTaxaEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class ExpandTaxaEvent extends  GwtEvent<ExpandTaxaEventHandler>  {

	public interface ExpandTaxaEventHandler extends EventHandler {
		void onExpand(ExpandTaxaEvent event);
	}
	
	public static Type<ExpandTaxaEventHandler> TYPE = new Type<ExpandTaxaEventHandler>();
	private List<Taxon> taxa = new LinkedList<Taxon>();
	
	public ExpandTaxaEvent(List<Taxon> taxa) {
		this.taxa.addAll(taxa);
	}
	
	public ExpandTaxaEvent(Taxon taxon) {
		this.taxa.add(taxon);
	}


	@Override
	public GwtEvent.Type<ExpandTaxaEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ExpandTaxaEventHandler handler) {
		handler.onExpand(this);
	}

	public List<Taxon> getTaxa() {
		return taxa;
	}


}
