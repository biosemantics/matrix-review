package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent.SetValueEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class SetValueEvent extends GwtEvent<SetValueEventHandler> implements PrintableEvent {

	public interface SetValueEventHandler extends EventHandler {
		void onSet(SetValueEvent event);
	}
	
	public static Type<SetValueEventHandler> TYPE = new Type<SetValueEventHandler>();
	private Value oldValue;
	private Value newValue;
	private Taxon taxon;
	private Character character;
	
	public SetValueEvent(Taxon taxon, Character character, Value oldValue, Value newValue) {
		this.taxon = taxon;
		this.character = character;
		this.newValue = newValue;
		this.oldValue = oldValue;
	}
	
	@Override
	public GwtEvent.Type<SetValueEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetValueEventHandler handler) {
		handler.onSet(this);
	}
	
	public Value getOldValue() {
		return oldValue;
	}

	public Value getNewValue() {
		return newValue;
	}
	
	public Taxon getTaxon() {
		return taxon;
	}

	public Character getCharacter() {
		return character;
	}

	@Override
	public String print() {
		return "set value from " + oldValue.getValue() + " to " + newValue.getValue(); 
		/*return "Set value for " + oldValue.getTaxon().getFullName() + " at character " + oldValue.getCharacter().toString() + 
				" from " + oldValue.getValue() + " to " + newValue.getValue();*/
	}

	
}