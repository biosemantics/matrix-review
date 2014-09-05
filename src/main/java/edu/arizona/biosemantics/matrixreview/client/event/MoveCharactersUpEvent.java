package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersUpEvent.MoveCharactersUpEventHandler;

public class MoveCharactersUpEvent extends GwtEvent<MoveCharactersUpEventHandler> {

	public interface MoveCharactersUpEventHandler extends EventHandler {
		void onMove(MoveCharactersUpEvent event);
	}
	
	public static Type<MoveCharactersUpEventHandler> TYPE = new Type<MoveCharactersUpEventHandler>();
	
	private List<Character> characters;

	public MoveCharactersUpEvent(List<Character> characters) {
		super();
		this.characters = characters;
	}

	@Override
	protected void dispatch(MoveCharactersUpEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveCharactersUpEventHandler> getAssociatedType() {
		return TYPE;
	}


	public List<Character> getCharacters() {
		return characters;
	}

}
