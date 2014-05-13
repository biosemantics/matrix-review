package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent.SortCharatersByNameEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class SortCharactersByNameEvent extends GwtEvent<SortCharatersByNameEventHandler> {

	public interface SortCharatersByNameEventHandler extends EventHandler {
		void onSort(SortCharactersByNameEvent event);
	}
	
	public static Type<SortCharatersByNameEventHandler> TYPE = new Type<SortCharatersByNameEventHandler>();
	
	private boolean descending;
	
	public SortCharactersByNameEvent(boolean descending) {
		this.descending = descending;
	}

	@Override
	public GwtEvent.Type<SortCharatersByNameEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortCharatersByNameEventHandler handler) {
		handler.onSort(this);
	}

	public static Type<SortCharatersByNameEventHandler> getTYPE() {
		return TYPE;
	}

	public boolean isDescending() {
		return descending;
	}
	
	
	
}