package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent.SortCharatersByNameEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class SortCharactersByNameEvent extends GwtEvent<SortCharatersByNameEventHandler> {

	public interface SortCharatersByNameEventHandler extends EventHandler {
		void onSort(SortCharactersByNameEvent event);
	}
	
	public static Type<SortCharatersByNameEventHandler> TYPE = new Type<SortCharatersByNameEventHandler>();
	
	private SortDir sortDir;
	
	public SortCharactersByNameEvent(SortDir sortDir) {
		this.sortDir = sortDir;
	}

	@Override
	public GwtEvent.Type<SortCharatersByNameEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortCharatersByNameEventHandler handler) {
		handler.onSort(this);
	}

	public SortDir getSortDir() {
		return sortDir;
	}	
	
}