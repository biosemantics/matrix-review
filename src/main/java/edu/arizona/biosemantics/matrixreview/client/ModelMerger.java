package edu.arizona.biosemantics.matrixreview.client;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.common.ModelControler;
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

//TODO: Instead of merging model copies, store all events that modified the submodel
//and replay them on the full model
public class ModelMerger { //extends ModelControler {

	private EventBus fullModelBus;
	private EventBus subModelBus;
	private Model subModel;
	private Model fullModel;
	private List<GwtEvent<?>> subModelEvents = new LinkedList<GwtEvent<?>>();

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
				subModelEvents.clear();
				subModel = event.getModel();
			}
		});
		subModelBus.addHandler(AddTaxonEvent.TYPE, new AddTaxonEvent.AddTaxonEventHandler() {
			@Override
			public void onAdd(AddTaxonEvent event) {
				subModelEvents.add(event);
			}
		});
		subModelBus.addHandler(RemoveTaxaEvent.TYPE, new RemoveTaxaEvent.RemoveTaxonEventHandler() {
			@Override
			public void onRemove(RemoveTaxaEvent event) {
				subModelEvents.add(event);
			}
		});
		subModelBus.addHandler(AddCharacterEvent.TYPE, new AddCharacterEvent.AddCharacterEventHandler() {
			@Override
			public void onAdd(AddCharacterEvent event) {
				subModelEvents.add(event);
			}
		});
		subModelBus.addHandler(RemoveCharacterEvent.TYPE, new RemoveCharacterEvent.RemoveCharacterEventHandler() {
			@Override
			public void onRemove(RemoveCharacterEvent event) {
				subModelEvents.add(event);
			}
		});
		subModelBus.addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
			@Override
			public void onModify(ModifyTaxonEvent event) {
				subModelEvents.add(event);
			}
		});
		subModelBus.addHandler(ModifyCharacterEvent.TYPE, new ModifyCharacterEvent.ModifyCharacterEventHandler() {
			@Override
			public void onModify(ModifyCharacterEvent event) {
				subModelEvents.add(event);
			}
		});
		subModelBus.addHandler(MergeCharactersEvent.TYPE, new MergeCharactersEvent.MergeCharactersEventHandler() {
			@Override
			public void onMerge(MergeCharactersEvent event) {
				subModelEvents.add(event);
			}
		});
		subModelBus.addHandler(SetValueEvent.TYPE, new SetValueEvent.SetValueEventHandler() {
			@Override
			public void onSet(SetValueEvent event) {
				subModelEvents.add(event);
			}
		});
		
		//TODO
		//sorting or move events are ommitted for now:
		//sorting events will sort entire fullmodel, but would only want to apply sort to submodel 
		//part..
	}

	/*public Model createSubModel(List<Character> characters, List<Taxon> selectedTaxa) {
		List<Organ> hierarchyCharacters = new LinkedList<Organ>();
		List<Taxon> hierarchyTaxa = new LinkedList<Taxon>(selectedTaxa);
		Collections.sort(hierarchyTaxa, new Comparator<Taxon>() {
			@Override
			public int compare(Taxon o1, Taxon o2) {
				List<Taxon> bfsTaxa = model.getTaxonMatrix().getHierarchyTaxaBFS();
				return bfsTaxa.indexOf(o1) - bfsTaxa.indexOf(o2);
			}
		});
		
		Map<Organ, List<Character>> organFlatCharacters = new HashMap<Organ, List<Character>>();
		for(Character character : characters) {
			//have to create new organ objects here, otherwise will get full set of their characters
			Organ organ = new Organ(character.getOrgan().getName());
			if(!organFlatCharacters.containsKey(organ))
				organFlatCharacters.put(organ, new LinkedList<Character>());
			organFlatCharacters.get(organ).add(character);
		}
		for(Organ organ : organFlatCharacters.keySet()) {
			List<Character> flatCharacters = organFlatCharacters.get(organ);
			Collections.sort(flatCharacters, new Comparator<Character>() {
				@Override
				public int compare(Character o1, Character o2) {
					return model.getTaxonMatrix().getHierarchyCharactersBFS().indexOf(o1) - model.getTaxonMatrix().getHierarchyCharactersBFS().indexOf(o2);
				}
			});
			organ.setFlatCharacters(flatCharacters);
			hierarchyCharacters.add(organ);
		}
		Collections.sort(hierarchyCharacters, new Comparator<Organ>() {
			@Override
			public int compare(Organ o1, Organ o2) {
				return model.getTaxonMatrix().getHierarchyCharacters().indexOf(o1) - model.getTaxonMatrix().getHierarchyCharacters().indexOf(o2);
			}
		});
		
		TaxonMatrix subMatrix = new TaxonMatrix(hierarchyCharacters, hierarchyTaxa);
		for(Taxon taxon : subMatrix.getHierarchyTaxaDFS()) {
			for(Character character : subMatrix.getHierarchyCharactersBFS()) {
				subMatrix.setValue(taxon, character, model.getTaxonMatrix().getValue(taxon, character));
			}
		}
		Model subModel = new Model(model, subMatrix);
		return subModel;
	}
	
	public void mergeToFullModel(Model fullModel, Model subModel, Model subModelOriginal) {		
		mergeTaxa(fullModel, subModel, subModelOriginal);
		mergeCharacters(fullModel, subModel, subModelOriginal);
		mergeValues(fullModel, subModel, subModelOriginal);
		//reset of model fields were same references in submodel
	}

	private void mergeValues(Model fullModel, Model subModel, Model subModelOriginal) {
		for(Taxon taxon : subModel.getTaxonMatrix().getHierarchyTaxaDFS()) {
			for(Character character : subModel.getTaxonMatrix().getVisibleCharacters()) {
				fullModel.getTaxonMatrix().setValue(taxon, character, 
						subModel.getTaxonMatrix().getValue(taxon, character));
			}
		}
	}

	private void mergeCharacters(Model fullModel, Model subModel, Model subModelOriginal) {
		final TaxonMatrix fullMatrix = fullModel.getTaxonMatrix();
		final TaxonMatrix subMatrix = subModel.getTaxonMatrix();
		final TaxonMatrix subMatrixOriginal = subModelOriginal.getTaxonMatrix();
		
		// Take care of submodel character deletions
		for(final Organ organ : subMatrixOriginal.getHierarchyCharacters()) {
			if(!subMatrix.isContained(organ)) {
				fullMatrix.removeOrgan(organ);
				fullMatrix.getHierarchyCharacters().remove(organ);
			} else {
				Organ fullOrgan = fullMatrix.getOrgan(organ.getName());
				Organ subOrgan = subMatrix.getOrgan(organ.getName());
				for(Character character : organ.getCharacters()) {
					if(!subOrgan.isContained(character)) {
						fullOrgan.remove(character);
					}
				}

				//enforce submodel ordering
				Collections.sort(fullOrgan.getFlatCharacters(), new Comparator<Character>() {
					@Override
					public int compare(Character o1, Character o2) {
						return organ.getFlatCharacters().indexOf(o1) - organ.getFlatCharacters().indexOf(o2);
					}
				});
			}
		}
		//there can still be taxa in hierarchy removed not recorded in fullmatrix's flat/taxa
		for(Character character : subMatrixOriginal.getFlatCharacters()) {
			if(!subMatrix.isContained(character)) {
				fullMatrix.getVisibleCharacters().remove(character);
				fullMatrix.getFlatCharacters().remove(character);
			}
		}
		
		// Take care of submodel character additions
		//submatrix contains organ copies and doesn't share references so it could show only subsets of the organs' characters
		//these need to be merged together with fullModels organs
		for(final Organ organ : subMatrix.getHierarchyCharacters()) {
			if(!fullMatrix.isContained(organ)) {
				fullMatrix.addOrgan(organ);
				fullMatrix.getHierarchyCharacters().add(organ);
			} else {
				Organ fullOrgan = fullMatrix.getOrgan(organ.getName());
				for(Character character : organ.getCharacters()) {
					fullOrgan.ensureContained(character, 0);
				}

				//enforce submodel ordering
				Collections.sort(fullOrgan.getFlatCharacters(), new Comparator<Character>() {
					@Override
					public int compare(Character o1, Character o2) {
						return organ.getFlatCharacters().indexOf(o1) - organ.getFlatCharacters().indexOf(o2);
					}
				});
			}
		}
		//there can still be taxa in hierarchy added not recorded in fullmatrix's flat/taxa
		for(Character character : subMatrix.getFlatCharacters()) {
			if(!fullMatrix.isContained(character)) {
				fullMatrix.getVisibleCharacters().add(character);
				fullMatrix.getFlatCharacters().add(character);
			}
		}
		
		// Enforce submodel ordering
		Collections.sort(fullMatrix.getFlatCharacters(), new Comparator<Character>() {
			@Override
			public int compare(Character o1, Character o2) {
				return subMatrix.getFlatCharacters().indexOf(o1) - subMatrix.getFlatCharacters().indexOf(o2);
			}
		});
		Collections.sort(fullMatrix.getHierarchyCharacters(), new Comparator<Organ>() {
			@Override
			public int compare(Organ o1, Organ o2) {
				return subMatrix.getHierarchyCharacters().indexOf(o1) - subMatrix.getHierarchyCharacters().indexOf(o2);
			}
		});
	}

	private void mergeTaxa(Model fullModel, Model subModel, Model subModelOriginal) {
		final TaxonMatrix fullMatrix = fullModel.getTaxonMatrix();
		final TaxonMatrix subMatrix = subModel.getTaxonMatrix();
		final TaxonMatrix subMatrixOriginal = subModelOriginal.getTaxonMatrix();
		
		// Take care of submodel taxon deletions
		for(Taxon rootTaxon : subMatrixOriginal.getHierarchyRootTaxa()) {
			if(!subMatrixOriginal.isContained(rootTaxon)) {
				fullMatrix.getHierarchyRootTaxa().remove(rootTaxon);
			}
		}
		//there can still be taxa in hierarchy added not recorded in fullmatrix's flat/taxa
		for(Taxon taxon : subMatrixOriginal.getFlatTaxa()) {
			if(!subMatrix.isContained(taxon)) {
				fullMatrix.getVisibleTaxa().remove(taxon);
				fullMatrix.getFlatTaxa().remove(taxon);
			}
		}
		
		// Take care of submodel taxon additions
		for(Taxon rootTaxon : subMatrix.getHierarchyRootTaxa()) {
			if(!fullMatrix.isContained(rootTaxon)) {
				fullMatrix.getHierarchyRootTaxa().add(rootTaxon);
			} else {
				//kids are automatically all in fullModel because shared references to Taxon objects
				/*for(Taxon taxon : rootTaxon.getChildren()) {
					ensureContained(rootTaxon, taxon);
				}*/
		/*	}
		}
		//there can still be taxa in hierarchy added not recorded in fullmatrix's flat/taxa
		for(Taxon taxon : subMatrix.getFlatTaxa()) {
			if(!fullMatrix.isContained(taxon)) {
				fullMatrix.getVisibleTaxa().add(taxon);
				fullMatrix.getFlatTaxa().add(taxon);
			}
		}
		
		// Enforce submodel ordering
		Collections.sort(fullMatrix.getFlatTaxa(), new Comparator<Taxon>() {
			@Override
			public int compare(Taxon o1, Taxon o2) {
				return subMatrix.getFlatTaxa().indexOf(o1) - subMatrix.getFlatTaxa().indexOf(o2);
			}
		});
		Collections.sort(fullMatrix.getHierarchyRootTaxa(), new Comparator<Taxon>() {
			@Override
			public int compare(Taxon o1, Taxon o2) {
				//kids are automatically all in same order in fullModel because shared references to Taxon objects
				return subMatrix.getHierarchyRootTaxa().indexOf(o1) - subMatrix.getHierarchyRootTaxa().indexOf(o2);
			}
		});
	}*/

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
		
		/*
		Map<Organ, List<Character>> organFlatCharacters = new HashMap<Organ, List<Character>>();
		for(Character character : characters) {
			//NOT ANYMORE -> visible characters in model; have to create new organ objects here, otherwise will get full set of their characters
			Organ organ = character.getOrgan(); //new Organ(character.getOrgan().getName());
			if(!organFlatCharacters.containsKey(organ))
				organFlatCharacters.put(organ, new LinkedList<Character>());
			organFlatCharacters.get(organ).add(character);
		}
		for(Organ organ : organFlatCharacters.keySet()) {
			List<Character> flatCharacters = organFlatCharacters.get(organ);
			Collections.sort(flatCharacters, new Comparator<Character>() {
				@Override
				public int compare(Character o1, Character o2) {
					return fullModel.getTaxonMatrix().getHierarchyCharactersBFS().indexOf(o1) - fullModel.getTaxonMatrix().getHierarchyCharactersBFS().indexOf(o2);
				}
			});
			organ.setFlatCharacters(flatCharacters);
			hierarchyCharacters.add(organ);
		}
		Collections.sort(hierarchyCharacters, new Comparator<Organ>() {
			@Override
			public int compare(Organ o1, Organ o2) {
				return fullModel.getTaxonMatrix().getHierarchyCharacters().indexOf(o1) - model.getTaxonMatrix().getHierarchyCharacters().indexOf(o2);
			}
		});*/
		
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

	public void commitEvents() {
		for(GwtEvent<?> event : subModelEvents) {
			List<GwtEvent<?>> adaptedEvents = adaptToFull(event);
			for(GwtEvent<?> adaptedEvent : adaptedEvents) {
				this.fullModelBus.fireEvent(adaptedEvent);
			}
		}
	}

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
