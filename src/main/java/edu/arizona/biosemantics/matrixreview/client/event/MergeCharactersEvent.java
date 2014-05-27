package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeCharactersEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class MergeCharactersEvent extends GwtEvent<MergeCharactersEventHandler> {

	public interface MergeCharactersEventHandler extends EventHandler {
		void onMerge(MergeCharactersEvent event);
	}
	
	public enum MergeMode {
		A_OVER_B, B_OVER_A, MIX
	}
	
	public static Type<MergeCharactersEventHandler> TYPE = new Type<MergeCharactersEventHandler>();
	
	private Character character;
	private Character target;
	private MergeMode mergeMode;

	public MergeCharactersEvent(Character character, Character target, MergeMode mergeMode) {
		this.character = character;
		this.target = target;
		this.mergeMode = mergeMode;
	}
	
	@Override
	protected void dispatch(MergeCharactersEventHandler handler) {
		handler.onMerge(this);
	}

	@Override
	public Type<MergeCharactersEventHandler> getAssociatedType() {
		return TYPE;
	}

	public Character getCharacter() {
		return character;
	}

	public Character getTarget() {
		return target;
	}

	public MergeMode getMergeMode() {
		return mergeMode;
	}
	
}
