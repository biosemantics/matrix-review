package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansDownEvent.MoveOrgansDownEventHandler;

public class MoveOrgansDownEvent extends GwtEvent<MoveOrgansDownEventHandler> {

	public interface MoveOrgansDownEventHandler extends EventHandler {
		void onMove(MoveOrgansDownEvent event);
	}
	
	public static Type<MoveOrgansDownEventHandler> TYPE = new Type<MoveOrgansDownEventHandler>();
	
	private List<Organ> organs;

	public MoveOrgansDownEvent(List<Organ> organs) {
		super();
		this.organs = organs;
	}

	@Override
	protected void dispatch(MoveOrgansDownEventHandler handler) {
		handler.onMove(this);
	}

	@Override
	public Type<MoveOrgansDownEventHandler> getAssociatedType() {
		return TYPE;
	}


	public List<Organ> getOrgans() {
		return organs;
	}

}
