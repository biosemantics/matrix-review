package edu.arizona.biosemantics.matrixreview.client.manager.event;

import com.google.gwt.event.shared.EventHandler;

public interface ValueChangedEventHandler extends EventHandler {

	void onValueChanged(ValueChangedEvent valueChangedEvent);

}
