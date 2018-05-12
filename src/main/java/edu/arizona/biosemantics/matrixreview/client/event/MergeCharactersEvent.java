package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeCharactersEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

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
	private Set<Taxon> toMerge = null;

	public MergeCharactersEvent(Character character, Character target, MergeMode mergeMode) {
		this.character = character;
		this.target = target;
		this.mergeMode = mergeMode;
	}
	
	public MergeCharactersEvent(Character character, Character target, MergeMode mergeMode, Collection<Taxon> toMerge) {
		this(character, target, mergeMode);
		this.toMerge = new HashSet<Taxon>(toMerge);
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

	public Set<Taxon> getToMerge() {
		return toMerge;
	}
	
	public boolean mergeAll() {
		return toMerge == null;
	}
	
}
