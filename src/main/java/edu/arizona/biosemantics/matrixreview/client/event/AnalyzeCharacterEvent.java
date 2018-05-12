package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeCharacterEvent.AnalyzeCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class AnalyzeCharacterEvent extends GwtEvent<AnalyzeCharacterEventHandler> {

	public interface AnalyzeCharacterEventHandler extends EventHandler {
		void onAnalyze(AnalyzeCharacterEvent event);
	}
	
	public static Type<AnalyzeCharacterEventHandler> TYPE = new Type<AnalyzeCharacterEventHandler>();
	private Character character;
	private List<Taxon> taxaToConsider; //null or empty will use all
	
	public AnalyzeCharacterEvent(Character character) {
		this.character = character;
	}
	
	public AnalyzeCharacterEvent(Character character, List<Taxon> taxaToConsider) {
		this.character = character;
		this.taxaToConsider = taxaToConsider;
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

	public List<Taxon> getTaxaToConsider() {
		return taxaToConsider;
	}
	
}