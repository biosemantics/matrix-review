package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent.SetValueColorEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class SetValueColorEvent extends GwtEvent<SetValueColorEventHandler> {

	public interface SetValueColorEventHandler extends EventHandler {
		void onSet(SetValueColorEvent event);
	}
	
	public static Type<SetValueColorEventHandler> TYPE = new Type<SetValueColorEventHandler>();
	private Value value;
	private Color color;
	
	public SetValueColorEvent(Value value, Color color) {
		this.value = value;
		this.color = color;
	}
	
	@Override
	public GwtEvent.Type<SetValueColorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetValueColorEventHandler handler) {
		handler.onSet(this);
	}

	public static Type<SetValueColorEventHandler> getTYPE() {
		return TYPE;
	}

	public Value getValue() {
		return value;
	}

	public Color getColor() {
		return color;
	}
	
}