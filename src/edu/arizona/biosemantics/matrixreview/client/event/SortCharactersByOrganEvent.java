package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent.SortCharatersByOrganEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class SortCharactersByOrganEvent extends GwtEvent<SortCharatersByOrganEventHandler> {

	public interface SortCharatersByOrganEventHandler extends EventHandler {
		void onSort(SortCharactersByOrganEvent event);
	}
	
	public static Type<SortCharatersByOrganEventHandler> TYPE = new Type<SortCharatersByOrganEventHandler>();
	
	private boolean descending;
	
	public SortCharactersByOrganEvent(boolean descending) {
		this.descending = descending;
	}

	@Override
	public GwtEvent.Type<SortCharatersByOrganEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortCharatersByOrganEventHandler handler) {
		handler.onSort(this);
	}

	public static Type<SortCharatersByOrganEventHandler> getTYPE() {
		return TYPE;
	}

	public boolean isDescending() {
		return descending;
	}
	
	
	
}