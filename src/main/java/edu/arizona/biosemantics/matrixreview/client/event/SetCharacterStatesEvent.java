package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent.SetCharacterStatesEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class SetCharacterStatesEvent extends GwtEvent<SetCharacterStatesEventHandler> {

	public interface SetCharacterStatesEventHandler extends EventHandler {
		void onSet(SetCharacterStatesEvent event);
	}
	
	public static Type<SetCharacterStatesEventHandler> TYPE = new Type<SetCharacterStatesEventHandler>();
	private Character character;
	private List<String> states;
	
	public SetCharacterStatesEvent(Character character, List<String> states) {
		this.character = character;
		this.states = states;
	}
	
	@Override
	public GwtEvent.Type<SetCharacterStatesEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetCharacterStatesEventHandler handler) {
		handler.onSet(this);
	}

	public Character getCharacter() {
		return character;
	}

	public List<String> getStates() {
		return states;
	}

	
}
