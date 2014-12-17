package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent.SortTaxaByCoverageEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class SortTaxaByCoverageEvent extends GwtEvent<SortTaxaByCoverageEventHandler> {

	public interface SortTaxaByCoverageEventHandler extends EventHandler {
		void onSort(SortTaxaByCoverageEvent event);
	}
	
	public static Type<SortTaxaByCoverageEventHandler> TYPE = new Type<SortTaxaByCoverageEventHandler>();
	private SortDir sortDirection;
		
	public SortTaxaByCoverageEvent(SortDir sortDirection) {
		this.sortDirection = sortDirection;
	}

	@Override
	public GwtEvent.Type<SortTaxaByCoverageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortTaxaByCoverageEventHandler handler) {
		handler.onSort(this);
	}

	public SortDir getSortDirection() {
		return sortDirection;
	}
	
	
	
}