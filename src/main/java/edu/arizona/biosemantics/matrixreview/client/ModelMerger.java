package edu.arizona.biosemantics.matrixreview.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class ModelMerger { //extends ModelControler {

	private EventBus fullModelBus;
	private EventBus subModelBus;
	private Model subModel;
	private Model fullModel;
	//private List<GwtEvent<?>> subModelEvents = new LinkedList<GwtEvent<?>>();

	public ModelMerger(EventBus fullModelBus, EventBus subModelBus) {
		this.fullModelBus = fullModelBus;
		this.subModelBus = subModelBus;
		
		this.addEventHandlers();
	}

	private void addEventHandlers() {
		fullModelBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
			@Override
			public void onLoad(LoadModelEvent event) {
				fullModel = event.getModel();
			}
		});
		subModelBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
			@Override
			public void onLoad(LoadModelEvent event) {
				//subModelEvents.clear();
				subModel = event.getModel();
			}
		});
		subModelBus.addHandler(AddTaxonEvent.TYPE, new AddTaxonEvent.AddTaxonEventHandler() {
			@Override
			public void onAdd(AddTaxonEvent event) {
				//subModelEvents.add(event);
				fullModelBus.fireEvent(event);
			}
		});
		subModelBus.addHandler(RemoveTaxaEvent.TYPE, new RemoveTaxaEvent.RemoveTaxonEventHandler() {
			@Override
			public void onRemove(RemoveTaxaEvent event) {
				//subModelEvents.add(event);
				fullModelBus.fireEvent(event);
			}
		});
		subModelBus.addHandler(AddCharacterEvent.TYPE, new AddCharacterEvent.AddCharacterEventHandler() {
			@Override
			public void onAdd(AddCharacterEvent event) {
				//subModelEvents.add(event);
				fullModelBus.fireEvent(event);
			}
		});
		subModelBus.addHandler(RemoveCharacterEvent.TYPE, new RemoveCharacterEvent.RemoveCharacterEventHandler() {
			@Override
			public void onRemove(RemoveCharacterEvent event) {
				//subModelEvents.add(event);
				fullModelBus.fireEvent(event);
			}
		});
		subModelBus.addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
			@Override
			public void onModify(ModifyTaxonEvent event) {
				//subModelEvents.add(event);
				fullModelBus.fireEvent(event);
			}
		});
		subModelBus.addHandler(ModifyCharacterEvent.TYPE, new ModifyCharacterEvent.ModifyCharacterEventHandler() {
			@Override
			public void onModify(ModifyCharacterEvent event) {
				//subModelEvents.add(event);
				fullModelBus.fireEvent(event);
			}
		});
		subModelBus.addHandler(MergeCharactersEvent.TYPE, new MergeCharactersEvent.MergeCharactersEventHandler() {
			@Override
			public void onMerge(MergeCharactersEvent event) {
				//subModelEvents.add(event);
				MergeCharactersEvent mergeEvent = (MergeCharactersEvent)event;
				List<GwtEvent<?>> adaptedMerge = adaptMerge(mergeEvent);
				for(GwtEvent<?> adapted : adaptedMerge) 
					fullModelBus.fireEvent(adapted);
			}
		});
		subModelBus.addHandler(SetValueEvent.TYPE, new SetValueEvent.SetValueEventHandler() {
			@Override
			public void onSet(SetValueEvent event) {
				//subModelEvents.add(event);
				//fire already to full model bus so diagrams based on full model are already correctly updated
				fullModelBus.fireEvent(event);
			}
		});
		
		//TODO
		//sorting or move events are ommitted for now:
		//sorting events will sort entire fullmodel, but would only want to apply sort to submodel 
		//part..
	}

	public Model getFullModel() {
		return fullModel;
	}

	public Model getSubModel(List<Character> characters, List<Taxon> taxa) {		
		List<Taxon> hierarchyTaxa = new LinkedList<Taxon>();
		Set<Taxon> visibleTaxa = new HashSet<Taxon>(taxa);
		for(Taxon taxon : taxa) {
			if(isUsedAsRootTaxon(taxon, visibleTaxa))
				hierarchyTaxa.add(taxon);
		}
		Collections.sort(hierarchyTaxa, new Comparator<Taxon>() {
			@Override
			public int compare(Taxon o1, Taxon o2) {
				List<Taxon> bfsTaxa = fullModel.getTaxonMatrix().getHierarchyTaxaBFS();
				return bfsTaxa.indexOf(o1) - bfsTaxa.indexOf(o2);
			}
		});
		
		List<Organ> hierarchyCharacters = new LinkedList<Organ>();
		Set<Character> visibleCharacters = new HashSet<Character>(characters);
		LinkedHashSet<Organ> organs = new LinkedHashSet<Organ>();
		for(Character character : characters) {
			organs.add(character.getOrgan());
		}
		hierarchyCharacters.addAll(organs);		
		TaxonMatrix subMatrix = new TaxonMatrix(hierarchyCharacters, hierarchyTaxa, visibleCharacters, visibleTaxa);
		for(Taxon taxon : subMatrix.getVisibleTaxa()) {
			for(Character character : subMatrix.getVisibleCharacters()) {
				subMatrix.setValue(taxon, character, fullModel.getTaxonMatrix().getValue(taxon, character));
			}
		}
		Model subModel = new Model(fullModel, subMatrix);
		return subModel;
	}

	private boolean isUsedAsRootTaxon(Taxon taxon, Set<Taxon> visibleTaxa) {
		Taxon parent = taxon.getParent();
		while(parent != null) {
			if(visibleTaxa.contains(parent)) 
				return false;
			parent = parent.getParent();
		}
		return true;
	}

	/*public void commitEvents() {
		for(GwtEvent<?> event : subModelEvents) {
			List<GwtEvent<?>> adaptedEvents = adaptToFull(event);
			for(GwtEvent<?> adaptedEvent : adaptedEvents) {
				this.fullModelBus.fireEvent(adaptedEvent);
			}
		}
	}*/

	private List<GwtEvent<?>> adaptToFull(GwtEvent<?> event) {
		if(event instanceof MergeCharactersEvent) {
			MergeCharactersEvent mergeEvent = (MergeCharactersEvent)event;
			return adaptMerge(mergeEvent);
		}
		
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		result.add(event);
		return result;
	}

	private List<GwtEvent<?>> adaptMerge(MergeCharactersEvent mergeEvent) {
		List<GwtEvent<?>> result = new LinkedList<GwtEvent<?>>();
		Set<Taxon> affectedTaxa = new HashSet<Taxon>(fullModel.getTaxonMatrix().getTaxa());
		affectedTaxa.removeAll(subModel.getTaxonMatrix().getTaxa());
		
		MergeCharactersEvent merge = new MergeCharactersEvent(mergeEvent.getCharacter(), mergeEvent.getTarget(), mergeEvent.getMergeMode(), affectedTaxa);
		RemoveCharacterEvent remove = new RemoveCharacterEvent(mergeEvent.getTarget());
		
		result.add(merge);
		result.add(remove);
		return result;
	}

}
