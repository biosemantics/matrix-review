package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.AddColorEvent.AddColorEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;

public class AddColorEvent extends GwtEvent<AddColorEventHandler> {

	public interface AddColorEventHandler extends EventHandler {
		void onAdd(AddColorEvent event);
	}
	
	public static Type<AddColorEventHandler> TYPE = new Type<AddColorEventHandler>();
	private Color color;
	
	public AddColorEvent(Color color) {
		this.color = color;
	}

	@Override
	public GwtEvent.Type<AddColorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddColorEventHandler handler) {
		handler.onAdd(this);
	}

	public static Type<AddColorEventHandler> getTYPE() {
		return TYPE;
	}

	public Color getColor() {
		return color;
	}

	
}