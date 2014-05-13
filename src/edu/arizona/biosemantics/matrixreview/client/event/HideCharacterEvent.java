package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.HideCharacterEvent.HideCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class HideCharacterEvent extends GwtEvent<HideCharacterEventHandler> {

	public interface HideCharacterEventHandler extends EventHandler {
		void onHide(HideCharacterEvent event);
	}
	
	public static Type<HideCharacterEventHandler> TYPE = new Type<HideCharacterEventHandler>();
	private boolean hide;
	private Character character;
	
	public HideCharacterEvent(Character character, boolean hide) {
		this.character = character;
		this.hide = hide;
	}

	@Override
	public GwtEvent.Type<HideCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HideCharacterEventHandler handler) {
		handler.onHide(this);
	}

	public static Type<HideCharacterEventHandler> getTYPE() {
		return TYPE;
	}
	public Character getCharacter() {
		return character;
	}

	public boolean isHide() {
		return hide;
	}	
	
}