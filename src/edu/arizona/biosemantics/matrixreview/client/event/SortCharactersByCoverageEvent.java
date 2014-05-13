package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent.SortCharatersByCoverageEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class SortCharactersByCoverageEvent extends GwtEvent<SortCharatersByCoverageEventHandler> {

	public interface SortCharatersByCoverageEventHandler extends EventHandler {
		void onSort(SortCharactersByCoverageEvent event);
	}
	
	public static Type<SortCharatersByCoverageEventHandler> TYPE = new Type<SortCharatersByCoverageEventHandler>();
	
	private boolean descending;
	
	public SortCharactersByCoverageEvent(boolean descending) {
		this.descending = descending;
	}

	@Override
	public GwtEvent.Type<SortCharatersByCoverageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortCharatersByCoverageEventHandler handler) {
		handler.onSort(this);
	}

	public static Type<SortCharatersByCoverageEventHandler> getTYPE() {
		return TYPE;
	}

	public boolean isDescending() {
		return descending;
	}
	
	
	
}