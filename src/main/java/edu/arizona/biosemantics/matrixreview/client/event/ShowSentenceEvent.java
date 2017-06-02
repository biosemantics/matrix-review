package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ShowSentenceEvent.ShowSentenceEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class ShowSentenceEvent extends GwtEvent<ShowSentenceEventHandler> {

	public interface ShowSentenceEventHandler extends EventHandler {
		void onShow(ShowSentenceEvent event);
	}
	
	
	public static Type<ShowSentenceEventHandler> TYPE = new Type<ShowSentenceEventHandler>();
	private Taxon taxon;
	private Value value;
	
	public ShowSentenceEvent(Taxon taxon, Value value) {
		this.taxon = taxon;
		this.value = value;
	}
	
	public GwtEvent.Type<ShowSentenceEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowSentenceEventHandler handler) {
		handler.onShow(this);
	}

	public static Type<ShowSentenceEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}
	
	public Value getValue(){
		return value;
	}
	
}