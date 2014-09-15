package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MatrixModeEvent.MatrixModeEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.MatrixMode;

public class MatrixModeEvent extends GwtEvent<MatrixModeEventHandler> {

	public interface MatrixModeEventHandler extends EventHandler {
		void onMode(MatrixModeEvent event);
	}
	
	public static Type<MatrixModeEventHandler> TYPE = new Type<MatrixModeEventHandler>();
	private MatrixMode mode;

	public MatrixModeEvent(MatrixMode mode) {
		this.mode = mode;
	}

	@Override
	public Type<MatrixModeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(MatrixModeEventHandler handler) {
		handler.onMode(this);
	}

	public MatrixMode getMode() {
		return mode;
	}
	
	
}