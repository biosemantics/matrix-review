package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.HideTaxaEvent.HideTaxaEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class HideTaxaEvent extends GwtEvent<HideTaxaEventHandler> {

	public interface HideTaxaEventHandler extends EventHandler {
		void onHide(HideTaxaEvent event);
	}
	
	public static Type<HideTaxaEventHandler> TYPE = new Type<HideTaxaEventHandler>();
	private boolean hide;
	private Set<Taxon> taxa;
	
	public HideTaxaEvent(Taxon taxon, boolean hide) {
		this.taxa = new HashSet<Taxon>();
		taxa.add(taxon);
		this.hide = hide;
	}
	
	public HideTaxaEvent(Set<Taxon> taxa, boolean hide) {
		this.taxa = taxa;
		this.hide = hide;
	}
	
	public HideTaxaEvent(List<Taxon> taxa, boolean hide) {
		this.hide = hide;
		this.taxa = new HashSet<Taxon>();
		this.taxa.addAll(taxa);
	}

	@Override
	public GwtEvent.Type<HideTaxaEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HideTaxaEventHandler handler) {
		handler.onHide(this);
	}

	public static Type<HideTaxaEventHandler> getTYPE() {
		return TYPE;
	}
	public Set<Taxon> getTaxa() {
		return taxa;
	}

	public boolean isHide() {
		return hide;
	}	
	
}