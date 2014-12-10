package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent.AddCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;

public class AddCharacterEvent extends GwtEvent<AddCharacterEventHandler> implements PrintableEvent {

	public interface AddCharacterEventHandler extends EventHandler {
		void onAdd(AddCharacterEvent event);
	}
	
	public static Type<AddCharacterEventHandler> TYPE = new Type<AddCharacterEventHandler>();
	
	private Organ organ;
	private Character character;
	private Character addAfterCharacter = null;

	public AddCharacterEvent(Organ organ, Character character) {
		this.organ = organ;
		this.character = character;
	}
	
	public AddCharacterEvent(Organ organ, Character character, Character addAfterCharacter) {
		this.organ = organ;
		this.character = character;
		this.addAfterCharacter = addAfterCharacter;
	}

	@Override
	public Type<AddCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddCharacterEventHandler handler) {
		handler.onAdd(this);
	}

	public Character getCharacter() {
		return character;
	}
	
	public Organ getOrgan() {
		return organ;
	}

	@Override
	public String print() {
		return "Add character " + character;
	}

	public Character getAddAfterCharacter() {
		return addAfterCharacter;
	}	
	
}