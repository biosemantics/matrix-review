package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersDownEvent.MoveCharacterDownEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class MoveCharactersDownEvent extends GwtEvent<MoveCharacterDownEventHandler> {

	public interface MoveCharacterDownEventHandler extends EventHandler {
		void onMove(MoveCharactersDownEvent event);
	}
	
	public static Type<MoveCharacterDownEventHandler> TYPE = new Type<MoveCharacterDownEventHandler>();
	
	private List<Character> characters;

	public MoveCharactersDownEvent(List<Character> characters) {
		super();
		this.characters = characters;
	}

	@Override
	protected void dispatch(MoveCharacterDownEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveCharacterDownEventHandler> getAssociatedType() {
		return TYPE;
	}


	public List<Character> getCharacters() {
		return characters;
	}

}
