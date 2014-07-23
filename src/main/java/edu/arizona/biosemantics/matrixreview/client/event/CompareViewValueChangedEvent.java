package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.CompareViewValueChangedEvent.CompareViewValueChangedEventHandler;

public class CompareViewValueChangedEvent extends GwtEvent<CompareViewValueChangedEventHandler> {

	public interface CompareViewValueChangedEventHandler extends EventHandler {
		void onCompareViewValueChanged(CompareViewValueChangedEvent event);
	}
	
	public static Type<CompareViewValueChangedEventHandler> TYPE = new Type<CompareViewValueChangedEventHandler>();
	
	public CompareViewValueChangedEvent() {
	}
	
	@Override
	public GwtEvent.Type<CompareViewValueChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CompareViewValueChangedEventHandler handler) {
		handler.onCompareViewValueChanged(this);
	}

	public static Type<CompareViewValueChangedEventHandler> getTYPE() {
		return TYPE;
	}
}