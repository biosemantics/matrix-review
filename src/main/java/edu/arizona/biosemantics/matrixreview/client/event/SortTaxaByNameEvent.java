package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent.SortTaxaByNameEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class SortTaxaByNameEvent extends GwtEvent<SortTaxaByNameEventHandler> {

	public interface SortTaxaByNameEventHandler extends EventHandler {
		void onSort(SortTaxaByNameEvent event);
	}
	
	public static Type<SortTaxaByNameEventHandler> TYPE = new Type<SortTaxaByNameEventHandler>();
	
	private SortDir sortDirection;
	
	public SortTaxaByNameEvent(SortDir sortDirection) {
		this.sortDirection = sortDirection;
	}

	@Override
	public GwtEvent.Type<SortTaxaByNameEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortTaxaByNameEventHandler handler) {
		handler.onSort(this);
	}

	public SortDir getSortDirection() {
		return sortDirection;
	}
	
}