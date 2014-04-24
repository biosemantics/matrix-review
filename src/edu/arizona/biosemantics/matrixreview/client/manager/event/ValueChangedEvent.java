package edu.arizona.biosemantics.matrixreview.client.manager.event;

import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class ValueChangedEvent extends GwtEvent<ValueChangedEventHandler> {

	public static Type<ValueChangedEventHandler> TYPE = new Type<ValueChangedEventHandler>();
	private Value oldValue;
	private Value newValue;
	private int row;
	private int column;
	private Taxon taxon;
	private Character character;
	
	public ValueChangedEvent(Taxon taxon, Character character, int row, int column, Value oldValue, Value newValue) {
		this.taxon = taxon;
		this.character = character;
		this.row = row;
		this.column = column;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	@Override
	public GwtEvent.Type<ValueChangedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ValueChangedEventHandler handler) {
		handler.onValueChanged(this);
	}

	public static Type<ValueChangedEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public Character getCharacter() {
		return character;
	}

	public Value getOldValue() {
		return oldValue;
	}

	public Value getNewValue() {
		return newValue;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}
	
	

}
