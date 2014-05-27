package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent.SetValueCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class SetValueCommentEvent extends GwtEvent<SetValueCommentEventHandler> {

	public interface SetValueCommentEventHandler extends EventHandler {
		void onSet(SetValueCommentEvent event);
	}
	
	public static Type<SetValueCommentEventHandler> TYPE = new Type<SetValueCommentEventHandler>();
	private Value value;
	private String comment;
	
	public SetValueCommentEvent(Value value, String comment) {
		this.value = value;
		this.comment = comment;
	}
	
	@Override
	public GwtEvent.Type<SetValueCommentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetValueCommentEventHandler handler) {
		handler.onSet(this);
	}

	public static Type<SetValueCommentEventHandler> getTYPE() {
		return TYPE;
	}

	public Value getValue() {
		return value;
	}

	public String getComment() {
		return comment;
	}
	
}