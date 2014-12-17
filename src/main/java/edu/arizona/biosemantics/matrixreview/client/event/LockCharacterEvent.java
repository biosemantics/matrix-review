package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.LockCharacterEvent.LockCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class LockCharacterEvent extends GwtEvent<LockCharacterEventHandler> {

	public interface LockCharacterEventHandler extends EventHandler {
		void onLock(LockCharacterEvent event);
	}
	
	public static Type<LockCharacterEventHandler> TYPE = new Type<LockCharacterEventHandler>();
	private Character character;
	private boolean lock;
	
	public LockCharacterEvent(Character character, boolean lock) {
		this.character = character;
		this.lock = lock;
	}
	
	@Override
	public GwtEvent.Type<LockCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LockCharacterEventHandler handler) {
		handler.onLock(this);
	}

	public static Type<LockCharacterEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}

	public boolean isLock() {
		return lock;
	}
}