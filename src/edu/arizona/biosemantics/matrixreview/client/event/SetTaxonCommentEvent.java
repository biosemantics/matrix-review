package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent.SetTaxonCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class SetTaxonCommentEvent extends GwtEvent<SetTaxonCommentEventHandler> {

	public interface SetTaxonCommentEventHandler extends EventHandler {
		void onSet(SetTaxonCommentEvent event);
	}
	
	public static Type<SetTaxonCommentEventHandler> TYPE = new Type<SetTaxonCommentEventHandler>();
	private Taxon taxon;
	private String comment;
	
	public SetTaxonCommentEvent(Taxon taxon, String comment) {
		this.taxon = taxon;
		this.comment = comment;
	}
	
	@Override
	public GwtEvent.Type<SetTaxonCommentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetTaxonCommentEventHandler handler) {
		handler.onSet(this);
	}

	public static Type<SetTaxonCommentEventHandler> getTYPE() {
		return TYPE;
	}

	public Taxon getTaxon() {
		return taxon;
	}

	public String getComment() {
		return comment;
	}
	
}