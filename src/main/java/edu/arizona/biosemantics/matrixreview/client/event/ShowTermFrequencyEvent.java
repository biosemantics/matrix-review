package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ShowTermFrequencyEvent.ShowTermFrequencyEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class ShowTermFrequencyEvent extends GwtEvent<ShowTermFrequencyEventHandler> {

	public interface ShowTermFrequencyEventHandler extends EventHandler {
		void onShow(ShowTermFrequencyEvent event);
	}
	
	public static Type<ShowTermFrequencyEventHandler> TYPE = new Type<ShowTermFrequencyEventHandler>();
	private Character character;
	
	public ShowTermFrequencyEvent(Character character) {
		this.character = character;
	}
	
	@Override
	public GwtEvent.Type<ShowTermFrequencyEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowTermFrequencyEventHandler handler) {
		handler.onShow(this);
	}

	public static Type<ShowTermFrequencyEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}
	
	
}