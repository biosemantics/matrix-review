package edu.arizona.biosemantics.matrixreview.client.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent.SetValueEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class SetValueEvent extends GwtEvent<SetValueEventHandler> implements PrintableEvent {

	public interface SetValueEventHandler extends EventHandler {
		void onSet(SetValueEvent event);
	}
	
	public static Type<SetValueEventHandler> TYPE = new Type<SetValueEventHandler>();
	private Set<Taxon> taxa;
	private Set<Character> characters;
	private Map<Taxon, Map<Character, Value>> oldValues;
	private Map<Taxon, Map<Character, Value>> newValues;
	
	public SetValueEvent(Taxon taxon, Character character, Value oldValue, Value newValue) {
		this.taxa = new HashSet<Taxon>();
		this.taxa.add(taxon);
		this.characters = new HashSet<Character>();
		this.characters.add(character);
		this.oldValues = new HashMap<Taxon, Map<Character, Value>>();
		this.oldValues.put(taxon, new HashMap<Character, Value>());
		this.oldValues.get(taxon).put(character, oldValue);
		this.newValues = new HashMap<Taxon, Map<Character, Value>>();
		this.newValues.put(taxon, new HashMap<Character, Value>());
		this.newValues.get(taxon).put(character, newValue);
	}
	
	public SetValueEvent(Set<Taxon> taxa, Set<Character> characters, Map<Taxon, 
			Map<Character, Value>> oldValues, Map<Taxon, Map<Character, Value>> newValues) {
		this.taxa = taxa;
		this.characters = characters;
		this.oldValues = oldValues;
		this.newValues = newValues;
	}
	
	@Override
	public GwtEvent.Type<SetValueEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SetValueEventHandler handler) {
		handler.onSet(this);
	}	
	
	public Set<Taxon> getTaxa() {
		return taxa;
	}

	public Set<Character> getCharacters() {
		return characters;
	}

	public Map<Taxon, Map<Character, Value>> getOldValues() {
		return oldValues;
	}


	public Map<Taxon, Map<Character, Value>> getNewValues() {
		return newValues;
	}

	@Override
	public String print() {
		String result = "";
		for(Taxon taxon : taxa) {
			for(Character character : characters) {
				if(oldValues.containsKey(taxon) && newValues.containsKey(taxon)) {
					if(oldValues.get(taxon).containsKey(character) && newValues.get(taxon).containsKey(character)) {
						Value oldValue = oldValues.get(taxon).get(character);
						Value newValue = newValues.get(taxon).get(character);
						result += "set value from " + oldValue.getValue() + " to " + newValue.getValue() + "\n"; 
					}
				}
			}
		}
		return result;
		//return "set value from " + oldValue.getValue() + " to " + newValue.getValue(); 
		/*return "Set value for " + oldValue.getTaxon().getFullName() + " at character " + oldValue.getCharacter().toString() + 
				" from " + oldValue.getValue() + " to " + newValue.getValue();*/
	}

	
}