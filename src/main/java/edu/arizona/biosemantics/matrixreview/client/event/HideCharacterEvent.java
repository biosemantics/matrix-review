package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

import edu.arizona.biosemantics.matrixreview.client.event.HideCharacterEvent.HideCharacterEventHandler;

public class HideCharacterEvent extends GwtEvent<HideCharacterEventHandler> {

	public interface HideCharacterEventHandler extends EventHandler {
		void onHide(HideCharacterEvent event);
	}
	
	public static Type<HideCharacterEventHandler> TYPE = new Type<HideCharacterEventHandler>();
	private boolean hide;
	private Set<Character> characters;
	
	public HideCharacterEvent(Character character, boolean hide) {
		this.characters = new HashSet<Character>();
		characters.add(character);
		this.hide = hide;
	}
	
	public HideCharacterEvent(Set<Character> characters, boolean hide) {
		this.characters = characters;
		this.hide = hide;
	}

	public HideCharacterEvent(List<Character> characters, boolean hide) {
		this.characters = new HashSet<Character>();
		this.characters.addAll(characters);
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
	public Set<Character> getCharacters() {
		return characters;
	}

	public boolean isHide() {
		return hide;
	}	
	
}