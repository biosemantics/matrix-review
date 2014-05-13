package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent.SetValueEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class SetValueEvent extends GwtEvent<SetValueEventHandler> implements PrintableEvent {

	public interface SetValueEventHandler extends EventHandler {
		void onSet(SetValueEvent event);
	}
	
	public static Type<SetValueEventHandler> TYPE = new Type<SetValueEventHandler>();
	private Value oldValue;
	private Value newValue;
	private boolean changeRecordedInModel;
	
	public SetValueEvent(Value oldValue, Value newValue, boolean changeRecordedInModel) {
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.changeRecordedInModel = changeRecordedInModel;
	}
	
	@Override
	public GwtEvent.Type<SetValueEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetValueEventHandler handler) {
		handler.onSet(this);
	}

	public Value getValue() {
		return newValue;
	}

	public Value getOldValue() {
		return oldValue;
	}

	public Value getNewValue() {
		return newValue;
	}
	
	public boolean isChangeRecordedInModel() {
		return changeRecordedInModel;
	}

	@Override
	public String print() {
		return "Set value for " + oldValue.getTaxon().getFullName() + " at character " + oldValue.getCharacter().toString() + 
				" from " + oldValue.getValue() + " to " + newValue.getValue();
	}

	
}