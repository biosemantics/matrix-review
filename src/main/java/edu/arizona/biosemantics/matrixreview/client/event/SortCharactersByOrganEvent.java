package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent.SortCharatersByOrganEventHandler;

public class SortCharactersByOrganEvent extends GwtEvent<SortCharatersByOrganEventHandler> {

	public interface SortCharatersByOrganEventHandler extends EventHandler {
		void onSort(SortCharactersByOrganEvent event);
	}
	
	public static Type<SortCharatersByOrganEventHandler> TYPE = new Type<SortCharatersByOrganEventHandler>();
	
	private SortDir sortDir;
	
	public SortCharactersByOrganEvent(SortDir sortDir) {
		this.sortDir = sortDir;
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

	public SortDir getSortDir() {
		return sortDir;
	}	
	
	
}