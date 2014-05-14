package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxonFlatEvent.MoveTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.old.DataManager.MergeMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class MoveTaxonFlatEvent extends GwtEvent<MoveTaxonEventHandler> {

	public interface MoveTaxonEventHandler extends EventHandler {
		void onMove(MoveTaxonFlatEvent event);
	}
	
	public static Type<MoveTaxonEventHandler> TYPE = new Type<MoveTaxonEventHandler>();
	
	private List<Taxon> taxa = new LinkedList<Taxon>();
	private Taxon after;

	public MoveTaxonFlatEvent(Taxon taxon, Taxon after) {
		this.taxa.add(taxon);
		this.after = after;
	}
	
	public MoveTaxonFlatEvent(List<Taxon> taxa, Taxon after) {
		this.taxa = taxa;
		this.after = after;
	}
	
	@Override
	protected void dispatch(MoveTaxonEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveTaxonEventHandler> getAssociatedType() {
		return TYPE;
	}

	public List<Taxon> getTaxa() {
		return taxa;
	}

	public Taxon getAfter() {
		return after;
	}

}
