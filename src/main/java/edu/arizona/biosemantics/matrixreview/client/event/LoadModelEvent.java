package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent.LoadModelEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class LoadModelEvent extends GwtEvent<LoadModelEventHandler> implements PrintableEvent {

	public interface LoadModelEventHandler extends EventHandler {
		void onLoad(LoadModelEvent event);
	}
	
	public static Type<LoadModelEventHandler> TYPE = new Type<LoadModelEventHandler>();
	private Model model;
	
	public LoadModelEvent(Model model) {
		this.model = model;
	}
	
	@Override
	public GwtEvent.Type<LoadModelEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(LoadModelEventHandler handler) {
		handler.onLoad(this);
	}

	public static Type<LoadModelEventHandler> getTYPE() {
		return TYPE;
	}

	public Model getModel() {
		return model;
	}

	@Override
	public String print() {
		return "Loading model: \n" + model.toString();
	}
}