package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent.ModifyCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;

public class ModifyCharacterEvent extends GwtEvent<ModifyCharacterEventHandler> {

	public interface ModifyCharacterEventHandler extends EventHandler {
		void onModify(ModifyCharacterEvent event);
	}
	
	public static Type<ModifyCharacterEventHandler> TYPE = new Type<ModifyCharacterEventHandler>();
	private Character oldCharacter;
	private String oldName;
	private String newName;
	private Organ oldOrgan;
	private Organ newOrgan;
	
	
	public ModifyCharacterEvent(Character oldCharacter, String oldName, String newName, Organ oldOrgan, Organ newOrgan) {
		this.oldCharacter = oldCharacter;
		this.oldName = oldName;
		this.newName = newName;
		this.oldOrgan = oldOrgan;
		this.newOrgan = newOrgan;
	}
	
	@Override
	public GwtEvent.Type<ModifyCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ModifyCharacterEventHandler handler) {
		handler.onModify(this);
	}

	public static Type<ModifyCharacterEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getOldCharacter() {
		return oldCharacter;
	}

	public String getNewName() {
		return newName;
	}
	
	public Organ getNewOrgan() {
		return newOrgan;
	}

	public String getOldName() {
		return oldName;
	}

	public Organ getOldOrgan() {
		return oldOrgan;
	}
		
}