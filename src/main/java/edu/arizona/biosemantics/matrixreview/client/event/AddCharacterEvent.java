package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent.AddCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;

public class AddCharacterEvent extends GwtEvent<AddCharacterEventHandler> implements PrintableEvent {

	public interface AddCharacterEventHandler extends EventHandler {
		void onAdd(AddCharacterEvent event);
	}
	
	public static Type<AddCharacterEventHandler> TYPE = new Type<AddCharacterEventHandler>();
	private Organ organ;
	private Character after;
	private Character character;
	

	public AddCharacterEvent(Organ organ, Character after, Character character) {
		this.organ = organ;
		this.after = after;
		this.character = character;
	}

	@Override
	public Type<AddCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AddCharacterEventHandler handler) {
		handler.onAdd(this);
	}

	public Character getAfter() {
		return after;
	}

	public Character getCharacter() {
		return character;
	}
	
	public Organ getOrgan() {
		return organ;
	}

	@Override
	public String print() {
		if(after == null)
			return "Add character " + character;
		return "Add character " + character + " after " + after;
	}
	
	
}