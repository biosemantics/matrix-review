package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SaveEvent.SaveHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class SaveEvent extends GwtEvent<SaveHandler> {

	public interface SaveHandler extends EventHandler {
		void onSave(SaveEvent event);
	}
	
    public static Type<SaveHandler> TYPE = new Type<SaveHandler>();
	private Model model;

    public SaveEvent(Model model) {
    	this.model = model;
    }
    
	@Override
	public Type<SaveHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveHandler handler) {
		handler.onSave(this);
	}

	public Model getModel() {
		return model;
	}
	
}
