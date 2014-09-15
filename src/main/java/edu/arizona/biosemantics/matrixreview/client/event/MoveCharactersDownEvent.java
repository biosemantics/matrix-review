package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersDownEvent.MoveCharactersDownEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class MoveCharactersDownEvent extends GwtEvent<MoveCharactersDownEventHandler> {

	public interface MoveCharactersDownEventHandler extends EventHandler {
		void onMove(MoveCharactersDownEvent event);
	}
	
	public static Type<MoveCharactersDownEventHandler> TYPE = new Type<MoveCharactersDownEventHandler>();
	
	private List<Character> characters;

	public MoveCharactersDownEvent(List<Character> characters) {
		super();
		this.characters = characters;
	}

	@Override
	protected void dispatch(MoveCharactersDownEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveCharactersDownEventHandler> getAssociatedType() {
		return TYPE;
	}


	public List<Character> getCharacters() {
		return characters;
	}

}
