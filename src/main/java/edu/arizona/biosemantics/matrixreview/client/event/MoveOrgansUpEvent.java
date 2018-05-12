package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansUpEvent.MoveOrgansUpEventHandler;

public class MoveOrgansUpEvent extends GwtEvent<MoveOrgansUpEventHandler> {

	public interface MoveOrgansUpEventHandler extends EventHandler {
		void onMove(MoveOrgansUpEvent event);
	}
	
	public static Type<MoveOrgansUpEventHandler> TYPE = new Type<MoveOrgansUpEventHandler>();
	
	private List<Organ> organs;

	public MoveOrgansUpEvent(List<Organ> organs) {
		super();
		this.organs = organs;
	}

	@Override
	protected void dispatch(MoveOrgansUpEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveOrgansUpEventHandler> getAssociatedType() {
		return TYPE;
	}


	public List<Organ> getOrgans() {
		return organs;
	}

}
