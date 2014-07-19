package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ToggleCompareModeEvent.ToggleCompareModeEventHandler;

public class ToggleCompareModeEvent extends GwtEvent<ToggleCompareModeEventHandler> {

	public interface ToggleCompareModeEventHandler extends EventHandler {
		void onToggleCompareView(ToggleCompareModeEvent event);
	}
	
	public static Type<ToggleCompareModeEventHandler> TYPE = new Type<ToggleCompareModeEventHandler>();
	
	public ToggleCompareModeEvent() {
	}
	
	@Override
	public GwtEvent.Type<ToggleCompareModeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ToggleCompareModeEventHandler handler) {
		handler.onToggleCompareView(this);
	}

	public static Type<ToggleCompareModeEventHandler> getTYPE() {
		return TYPE;
	}
}