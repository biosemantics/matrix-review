package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MoveCharacterEvent.MoveCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class MoveCharacterEvent extends GwtEvent<MoveCharacterEventHandler> {

	public interface MoveCharacterEventHandler extends EventHandler {
		void onMove(MoveCharacterEvent event);
	}
	
	public static Type<MoveCharacterEventHandler> TYPE = new Type<MoveCharacterEventHandler>();
	
	private Character character;
	private Character after;

	public MoveCharacterEvent(Character character, Character after) {
		this.character = character;
		this.after = after;
	}
	
	@Override
	protected void dispatch(MoveCharacterEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}

	public Character getAfter() {
		return after;
	}
	
}
