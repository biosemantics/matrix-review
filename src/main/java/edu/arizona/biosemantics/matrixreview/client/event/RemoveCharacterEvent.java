package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.Collection;
import java.util.LinkedList;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent.RemoveCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class RemoveCharacterEvent extends GwtEvent<RemoveCharacterEventHandler> {

	public interface RemoveCharacterEventHandler extends EventHandler {
		void onRemove(RemoveCharacterEvent event);
	}
	
	public static Type<RemoveCharacterEventHandler> TYPE = new Type<RemoveCharacterEventHandler>();
	private Collection<Character> characters;
	
	public RemoveCharacterEvent(Collection<Character> characters) {
		this.characters = characters;
	}
	
	public RemoveCharacterEvent(Character character) {
		this.characters = new LinkedList<Character>();
		this.characters.add(character);
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

	public Collection<Character> getCharacters() {
		return characters;
	}	
	
}