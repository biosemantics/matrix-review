package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent.SortCharatersByCoverageEventHandler;

public class SortCharactersByCoverageEvent extends GwtEvent<SortCharatersByCoverageEventHandler> {

	public interface SortCharatersByCoverageEventHandler extends EventHandler {
		void onSort(SortCharactersByCoverageEvent event);
	}
	
	public static Type<SortCharatersByCoverageEventHandler> TYPE = new Type<SortCharatersByCoverageEventHandler>();
	
	private SortDir sortDir;
	
	public SortCharactersByCoverageEvent(SortDir sortDir) {
		this.sortDir = sortDir;
	}

	@Override
	public GwtEvent.Type<SortCharatersByCoverageEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortCharatersByCoverageEventHandler handler) {
		handler.onSort(this);
	}

	public SortDir getSortDir() {
		return sortDir;
	}	
	
	
	
}