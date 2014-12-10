package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ShowModifyEvent.ShowModifyEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class ShowModifyEvent extends GwtEvent<ShowModifyEventHandler> {

	public interface ShowModifyEventHandler extends EventHandler {
		void onShow(ShowModifyEvent event);
	}
	
	public static Type<ShowModifyEventHandler> TYPE = new Type<ShowModifyEventHandler>();
	private Model model;
	
	public ShowModifyEvent(Model model) {
		this.model = model;
	}
	
	@Override
	public GwtEvent.Type<ShowModifyEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowModifyEventHandler handler) {
		handler.onShow(this);
	}

	public Model getModel() {
		return model;
	}

}