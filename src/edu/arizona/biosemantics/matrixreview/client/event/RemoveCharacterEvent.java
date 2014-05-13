package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent.RemoveCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class RemoveCharacterEvent extends GwtEvent<RemoveCharacterEventHandler> {

	public interface RemoveCharacterEventHandler extends EventHandler {
		void onRemove(RemoveCharacterEvent event);
	}
	
	public static Type<RemoveCharacterEventHandler> TYPE = new Type<RemoveCharacterEventHandler>();
	private Character character;
	
	public RemoveCharacterEvent(Character character) {
		this.character = character;
	}
	
	@Override
	public GwtEvent.Type<RemoveCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RemoveCharacterEventHandler handler) {
		handler.onRemove(this);
	}

	public static Type<RemoveCharacterEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}	
	
}