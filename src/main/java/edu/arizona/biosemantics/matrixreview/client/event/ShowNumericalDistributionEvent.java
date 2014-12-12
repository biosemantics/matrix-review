package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ShowNumericalDistributionEvent.ShowNumericalDistributionEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class ShowNumericalDistributionEvent extends GwtEvent<ShowNumericalDistributionEventHandler> {

	public interface ShowNumericalDistributionEventHandler extends EventHandler {
		void onShow(ShowNumericalDistributionEvent event);
	}
	
	public static Type<ShowNumericalDistributionEventHandler> TYPE = new Type<ShowNumericalDistributionEventHandler>();
	private Character character;
	
	public ShowNumericalDistributionEvent(Character character) {
		this.character = character;
	}
	
	@Override
	public GwtEvent.Type<ShowNumericalDistributionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowNumericalDistributionEventHandler handler) {
		handler.onShow(this);
	}

	public static Type<ShowNumericalDistributionEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}
	
	
}