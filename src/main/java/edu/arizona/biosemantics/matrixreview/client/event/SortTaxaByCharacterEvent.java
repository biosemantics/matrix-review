package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent.SortTaxaByCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class SortTaxaByCharacterEvent extends GwtEvent<SortTaxaByCharacterEventHandler> {

	public interface SortTaxaByCharacterEventHandler extends EventHandler {
		void onSort(SortTaxaByCharacterEvent event);
	}
	
	public static Type<SortTaxaByCharacterEventHandler> TYPE = new Type<SortTaxaByCharacterEventHandler>();
	private Character character;
	private SortDir sortDirection;
	
	public SortTaxaByCharacterEvent(Character character, SortDir sortDirection) {
		this.character = character;
		this.sortDirection = sortDirection;
	}
	
	@Override
	public GwtEvent.Type<SortTaxaByCharacterEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SortTaxaByCharacterEventHandler handler) {
		handler.onSort(this);
	}

	public static Type<SortTaxaByCharacterEventHandler> getTYPE() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}

	public SortDir getSortDirection() {
		return sortDirection;
	}	
	
}