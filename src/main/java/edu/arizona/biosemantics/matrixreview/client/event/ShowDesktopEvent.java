package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent.ShowDesktopEventHandler;

public class ShowDesktopEvent extends GwtEvent<ShowDesktopEventHandler> {

	public interface ShowDesktopEventHandler extends EventHandler {
		void onShow(ShowDesktopEvent event);
	}
	
	public static Type<ShowDesktopEventHandler> TYPE = new Type<ShowDesktopEventHandler>();
	
	public ShowDesktopEvent() {
	}
	
	@Override
	public GwtEvent.Type<ShowDesktopEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowDesktopEventHandler handler) {
		handler.onShow(this);
	}

	public static Type<ShowDesktopEventHandler> getTYPE() {
		return TYPE;
	}
}