package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ModifyOrganEvent.ModifyOrganEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;

public class ModifyOrganEvent extends GwtEvent<ModifyOrganEventHandler> {

	public interface ModifyOrganEventHandler extends EventHandler {
		void onModify(ModifyOrganEvent event);
	}
	
	public static Type<ModifyOrganEventHandler> TYPE = new Type<ModifyOrganEventHandler>();
	private Organ oldOrgan;
	private String newName;
	
	public ModifyOrganEvent(Organ oldOrgan, String newName) {
		this.oldOrgan = oldOrgan;
		this.newName = newName;
	}
	
	@Override
	public GwtEvent.Type<ModifyOrganEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ModifyOrganEventHandler handler) {
		handler.onModify(this);
	}

	public Organ getOldOrgan() {
		return oldOrgan;
	}

	public String getNewName() {
		return newName;
	}
	

}
