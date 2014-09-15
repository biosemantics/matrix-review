package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent.SetCharacterColorEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class SetCharacterColorEvent extends GwtEvent<SetCharacterColorEventHandler> {

	public interface SetCharacterColorEventHandler extends EventHandler {
		void onSet(SetCharacterColorEvent event);
	}
	
	public static Type<SetCharacterColorEventHandler> TYPE = new Type<SetCharacterColorEventHandler>();
	private Character character;
	private Color color;
	
	public SetCharacterColorEvent(Character character, Color color) {
		this.character = character;
		this.color = color;
	}
	
	@Override
	public GwtEvent.Type<SetCharacterColorEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetCharacterColorEventHandler handler) {
		handler.onSet(this);
	}

	public static Type<SetCharacterColorEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}

	public Color getColor() {
		return color;
	}
	
}