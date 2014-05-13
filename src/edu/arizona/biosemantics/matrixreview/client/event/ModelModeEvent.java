package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ModelModeEvent.ModelModeEventHandler;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView.ModelMode;

public class ModelModeEvent extends GwtEvent<ModelModeEventHandler> {

	public interface ModelModeEventHandler extends EventHandler {
		void onMode(ModelModeEvent event);
	}
	
	public static Type<ModelModeEventHandler> TYPE = new Type<ModelModeEventHandler>();
	private ModelMode mode;

	public ModelModeEvent(ModelMode mode) {
		this.mode = mode;
	}

	@Override
	public Type<ModelModeEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ModelModeEventHandler handler) {
		handler.onMode(this);
	}

	public ModelMode getMode() {
		return mode;
	}
	
	
}