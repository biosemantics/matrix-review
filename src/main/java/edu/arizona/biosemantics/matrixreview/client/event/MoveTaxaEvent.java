package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaEvent.MoveTaxaEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class MoveTaxaEvent extends GwtEvent<MoveTaxaEventHandler> {

	public interface MoveTaxaEventHandler extends EventHandler {
		void onMove(MoveTaxaEvent event);
	}
	
	public static Type<MoveTaxaEventHandler> TYPE = new Type<MoveTaxaEventHandler>();
	
	private Taxon parent;
	private int index;
	private List<Taxon> taxa;

	public MoveTaxaEvent(Taxon parent, int index, List<Taxon> taxa) {
		super();
		this.parent = parent;
		this.index = index;
		this.taxa = taxa;
	}

	@Override
	protected void dispatch(MoveTaxaEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveTaxaEventHandler> getAssociatedType() {
		return TYPE;
	}

	public Taxon getParent() {
		return parent;
	}

	public int getIndex() {
		return index;
	}

	public List<Taxon> getTaxa() {
		return taxa;
	}


}
