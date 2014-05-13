package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ToggleDesktopEvent.ToggleDesktopEventHandler;

public class ToggleDesktopEvent extends GwtEvent<ToggleDesktopEventHandler> {

	public interface ToggleDesktopEventHandler extends EventHandler {
		void onToggle(ToggleDesktopEvent event);
	}
	
	public static Type<ToggleDesktopEventHandler> TYPE = new Type<ToggleDesktopEventHandler>();
	
	public ToggleDesktopEvent() {
	}
	
	@Override
	public GwtEvent.Type<ToggleDesktopEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ToggleDesktopEventHandler handler) {
		handler.onToggle(this);
	}

	public static Type<ToggleDesktopEventHandler> getTYPE() {
		return TYPE;
	}
}