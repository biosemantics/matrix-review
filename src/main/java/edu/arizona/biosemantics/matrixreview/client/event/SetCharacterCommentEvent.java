package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent.SetCharacterCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class SetCharacterCommentEvent extends GwtEvent<SetCharacterCommentEventHandler> {

	public interface SetCharacterCommentEventHandler extends EventHandler {
		void onSet(SetCharacterCommentEvent event);
	}
	
	public static Type<SetCharacterCommentEventHandler> TYPE = new Type<SetCharacterCommentEventHandler>();
	private Character character;
	private String comment;
	
	public SetCharacterCommentEvent(Character character, String comment) {
		this.character = character;
		this.comment = comment;
	}
	
	@Override
	public GwtEvent.Type<SetCharacterCommentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetCharacterCommentEventHandler handler) {
		handler.onSet(this);
	}

	public static Type<SetCharacterCommentEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}

	public String getComment() {
		return comment;
	}
	
}