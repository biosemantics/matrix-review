package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeTaxonEvent.AnalyzeTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class AnalyzeTaxonEvent extends GwtEvent<AnalyzeTaxonEventHandler> {

	public interface AnalyzeTaxonEventHandler extends EventHandler {
		void onAnalyze(AnalyzeTaxonEvent event);
	}
	
	public static Type<AnalyzeTaxonEventHandler> TYPE = new Type<AnalyzeTaxonEventHandler>();
	private Taxon taxon;
	
	public AnalyzeTaxonEvent(Taxon taxon) {
		this.taxon = taxon;
	}
	
	@Override
	public GwtEvent.Type<AnalyzeTaxonEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AnalyzeTaxonEventHandler handler) {
		handler.onAnalyze(this);
	}

	public static Type<AnalyzeTaxonEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}
	
	
}