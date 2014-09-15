package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ShowMatrixEvent.ShowMatrixEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class ShowMatrixEvent extends GwtEvent<ShowMatrixEventHandler> {

	public interface ShowMatrixEventHandler extends EventHandler {
		void onShow(ShowMatrixEvent event);
	}
	
	public static Type<ShowMatrixEventHandler> TYPE = new Type<ShowMatrixEventHandler>();
	private List<Taxon> taxa;
	private List<Character> characters;
	
	public ShowMatrixEvent(List<Taxon> taxa, List<Character> characters) {
		this.taxa = taxa;
		this.characters = characters;
	}
	
	@Override
	public GwtEvent.Type<ShowMatrixEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowMatrixEventHandler handler) {
		handler.onShow(this);
	}

	public List<Taxon> getTaxa() {
		return taxa;
	}
	
	public List<Character> getCharacters() {
		return characters;
	}

}