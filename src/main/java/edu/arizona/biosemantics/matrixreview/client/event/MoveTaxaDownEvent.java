package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaDownEvent.MoveTaxaDownEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class MoveTaxaDownEvent extends GwtEvent<MoveTaxaDownEventHandler> {

	public interface MoveTaxaDownEventHandler extends EventHandler {
		void onMove(MoveTaxaDownEvent event);
	}
	
	public static Type<MoveTaxaDownEventHandler> TYPE = new Type<MoveTaxaDownEventHandler>();
	
	private List<Taxon> taxa;

	public MoveTaxaDownEvent(List<Taxon> taxa) {
		super();
		this.taxa = taxa;
	}

	@Override
	protected void dispatch(MoveTaxaDownEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveTaxaDownEventHandler> getAssociatedType() {
		return TYPE;
	}


	public List<Taxon> getTaxa() {
		return taxa;
	}

}
