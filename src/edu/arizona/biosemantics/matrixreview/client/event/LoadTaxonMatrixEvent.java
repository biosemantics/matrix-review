package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent.LoadTaxonMatrixEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class LoadTaxonMatrixEvent extends GwtEvent<LoadTaxonMatrixEventHandler> implements PrintableEvent {

	public interface LoadTaxonMatrixEventHandler extends EventHandler {
		void onLoad(LoadTaxonMatrixEvent event);
	}
	
	public static Type<LoadTaxonMatrixEventHandler> TYPE = new Type<LoadTaxonMatrixEventHandler>();
	private TaxonMatrix taxonMatrix;
	
	public LoadTaxonMatrixEvent(TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
	}
	
	@Override
	public GwtEvent.Type<LoadTaxonMatrixEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadTaxonMatrixEventHandler handler) {
		handler.onLoad(this);
	}

	public static Type<LoadTaxonMatrixEventHandler> getTYPE() {
		return TYPE;
	}

	public TaxonMatrix getTaxonMatrix() {
		return taxonMatrix;
	}

	@Override
	public String print() {
		return "Loading matrix: \n" + taxonMatrix.toString();
	}
}