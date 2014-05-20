package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.RemoveColorsEvent.RemoveColorsEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;

public class RemoveColorsEvent extends GwtEvent<RemoveColorsEventHandler> {

	public interface RemoveColorsEventHandler extends EventHandler {
		void onRemove(RemoveColorsEvent event);
	}
	
	public static Type<RemoveColorsEventHandler> TYPE = new Type<RemoveColorsEventHandler>();
	private Set<Color> colors;
	
	public RemoveColorsEvent(Set<Color> colors) {
		this.colors = colors;
	}

	@Override
	public GwtEvent.Type<RemoveColorsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RemoveColorsEventHandler handler) {
		handler.onRemove(this);
	}

	public Set<Color> getColors() {
		return new HashSet<Color>(colors);
	}

	
}