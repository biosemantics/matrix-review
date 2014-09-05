package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.HideTaxaEvent.HideCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class HideTaxaEvent extends GwtEvent<HideCharacterEventHandler> {

	public interface HideCharacterEventHandler extends EventHandler {
		void onHide(HideTaxaEvent event);
	}
	
	public static Type<HideCharacterEventHandler> TYPE = new Type<HideCharacterEventHandler>();
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
	public GwtEvent.Type<HideCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HideCharacterEventHandler handler) {
		handler.onHide(this);
	}

	public static Type<HideCharacterEventHandler> getTYPE() {
		return TYPE;
	}
	public Set<Taxon> getTaxa() {
		return taxa;
	}

	public boolean isHide() {
		return hide;
	}	
	
}