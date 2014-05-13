package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent.ModifyCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;

public class ModifyCharacterEvent extends GwtEvent<ModifyCharacterEventHandler> {

	public interface ModifyCharacterEventHandler extends EventHandler {
		void onRename(ModifyCharacterEvent event);
	}
	
	public static Type<ModifyCharacterEventHandler> TYPE = new Type<ModifyCharacterEventHandler>();
	private Character character;
	private String name;
	private Organ organ;
	
	public ModifyCharacterEvent(Character character, String name, Organ organ) {
		this.character = character;
		this.name = name;
		this.organ = organ;
	}
	
	@Override
	public GwtEvent.Type<ModifyCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ModifyCharacterEventHandler handler) {
		handler.onRename(this);
	}

	public static Type<ModifyCharacterEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}

	public String getName() {
		return name;
	}

	public Organ getOrgan() {
		return organ;
	}		
	
}