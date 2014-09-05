package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent.SetControlModeEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;

public class SetControlModeEvent extends GwtEvent<SetControlModeEventHandler> {

	public interface SetControlModeEventHandler extends EventHandler {
		void onSet(SetControlModeEvent event);
	}
	
	public static Type<SetControlModeEventHandler> TYPE = new Type<SetControlModeEventHandler>();
	private Character character;
	private ControlMode controlMode;
	private List<String> states = new LinkedList<String>();
	
	public SetControlModeEvent(Character character, ControlMode controlMode) {
		this.character = character;
		this.controlMode = controlMode;
	}
	
	public SetControlModeEvent(Character character, ControlMode controlMode, List<String> states) {
		this.character = character;
		this.controlMode = controlMode;
		this.states = states;
	}
	
	@Override
	public GwtEvent.Type<SetControlModeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetControlModeEventHandler handler) {
		handler.onSet(this);
	}

	public static Type<SetControlModeEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}

	public ControlMode getControlMode() {
		return controlMode;
	}

	public List<String> getStates() {
		return states;
	}
	
	public boolean hasStates() {
		return states != null;
	}
	
	
}