package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaUpEvent.MoveTaxaUpEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class MoveTaxaUpEvent extends GwtEvent<MoveTaxaUpEventHandler> {

	public interface MoveTaxaUpEventHandler extends EventHandler {
		void onMove(MoveTaxaUpEvent event);
	}
	
	public static Type<MoveTaxaUpEventHandler> TYPE = new Type<MoveTaxaUpEventHandler>();
	
	private List<Taxon> taxa;

	public MoveTaxaUpEvent(List<Taxon> taxa) {
		super();
		this.taxa = taxa;
	}

	@Override
	protected void dispatch(MoveTaxaUpEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveTaxaUpEventHandler> getAssociatedType() {
		return TYPE;
	}

	public List<Taxon> getTaxa() {
		return taxa;
	}

}
