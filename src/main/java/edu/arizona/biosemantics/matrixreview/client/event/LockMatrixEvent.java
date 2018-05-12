package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.LockMatrixEvent.LockMatrixEventHandler;

public class LockMatrixEvent extends GwtEvent<LockMatrixEventHandler> {

	public interface LockMatrixEventHandler extends EventHandler {
		void onLock(LockMatrixEvent event);
	}
	
	public static Type<LockMatrixEventHandler> TYPE = new Type<LockMatrixEventHandler>();
	private boolean lock;
	
	public LockMatrixEvent(boolean lock) {
		this.lock = lock;
	}

	@Override
	public Type<LockMatrixEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LockMatrixEventHandler handler) {
		handler.onLock(this);
	}

	public static Type<LockMatrixEventHandler> getTYPE() {
		return TYPE;
	}

	public boolean isLock() {
		return lock;
	}


	
	
}