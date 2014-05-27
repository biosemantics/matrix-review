package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeCharacterEvent.AnalyzeCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class AnalyzeCharacterEvent extends GwtEvent<AnalyzeCharacterEventHandler> {

	public interface AnalyzeCharacterEventHandler extends EventHandler {
		void onAnalyze(AnalyzeCharacterEvent event);
	}
	
	public static Type<AnalyzeCharacterEventHandler> TYPE = new Type<AnalyzeCharacterEventHandler>();
	private Character character;
	
	public AnalyzeCharacterEvent(Character character) {
		this.character = character;
	}
	
	@Override
	public GwtEvent.Type<AnalyzeCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AnalyzeCharacterEventHandler handler) {
		handler.onAnalyze(this);
	}

	public static Type<AnalyzeCharacterEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}
	
	
}