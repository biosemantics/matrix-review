package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.AutosaveEvent.AutosaveEventHandler;

public class AutosaveEvent extends GwtEvent<AutosaveEventHandler> {

	public interface AutosaveEventHandler extends EventHandler {
		void onAutosave(AutosaveEvent event);
	}
	
	public static Type<AutosaveEventHandler> TYPE = new Type<AutosaveEventHandler>();
	
	public AutosaveEvent() {
	}
	
	@Override
	public GwtEvent.Type<AutosaveEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AutosaveEventHandler handler) {
		handler.onAutosave(this);
	}

	public static Type<AutosaveEventHandler> getTYPE() {
		return TYPE;
	}
}