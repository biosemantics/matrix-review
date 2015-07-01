package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;

import edu.arizona.biosemantics.matrixreview.client.common.compare.CharactersByCoverageComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.CharactersByNameComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.CharactersByOrganComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.TaxaByCharacterComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.TaxaByCoverageComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.TaxaByNameComparator;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent.SetCharacterColorEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent.SetCharacterCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent.SetTaxonColorEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeCharactersEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeMode;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansDownEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansDownEvent.MoveOrgansDownEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansUpEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansUpEvent.MoveOrgansUpEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetColorsEvent.SetColorsEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent.SetTaxonCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent.SetValueColorEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent.SetValueCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent.SortCharatersByCoverageEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent.AddCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetColorsEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent.AddTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent.LoadModelEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent.ModifyCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyOrganEvent.ModifyOrganEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent.ModifyTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersDownEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersDownEvent.MoveCharactersDownEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersUpEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersUpEvent.MoveCharactersUpEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaDownEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaDownEvent.MoveTaxaDownEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaUpEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaUpEvent.MoveTaxaUpEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent.RemoveCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent.RemoveTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent.SetControlModeEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent.SetValueEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent.SortCharatersByNameEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent.SortCharatersByOrganEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent.SortTaxaByCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent.SortTaxaByCoverageEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent.SortTaxaByNameEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;
import edu.arizona.biosemantics.common.taxonomy.Rank;

public class ModelControler implements LoadModelEventHandler, AddTaxonEventHandler, RemoveTaxonEventHandler, 
	ModifyTaxonEventHandler, MoveTaxaUpEventHandler, MoveTaxaDownEventHandler, MoveCharactersUpEventHandler, 
	MoveCharactersDownEventHandler, MoveOrgansDownEventHandler, MoveOrgansUpEventHandler, 
	AddCharacterEventHandler, RemoveCharacterEventHandler, ModifyCharacterEventHandler, 
	ModifyOrganEventHandler, SetControlModeEventHandler, SetValueEventHandler, SetColorsEventHandler, 
	SortCharatersByNameEventHandler, SortCharatersByOrganEventHandler, SortCharatersByCoverageEventHandler, 
	SortTaxaByNameEventHandler, SortTaxaByCharacterEventHandler, SortTaxaByCoverageEventHandler, MergeCharactersEventHandler, 
	SetTaxonCommentEventHandler, SetCharacterCommentEventHandler, SetTaxonColorEventHandler, SetCharacterColorEventHandler, 
	SetValueCommentEventHandler, SetValueColorEventHandler
	{

	protected EventBus eventBus;
	protected Model model;

	public ModelControler(EventBus eventBus) {
		this.eventBus = eventBus;
		
		addEventHandlers();
	}

	protected void addEventHandlers() {
		eventBus.addHandler(LoadModelEvent.TYPE, this);
		eventBus.addHandler(AddTaxonEvent.TYPE, this);
		eventBus.addHandler(RemoveTaxaEvent.TYPE, this);
		eventBus.addHandler(ModifyTaxonEvent.TYPE, this);
		eventBus.addHandler(MoveTaxaUpEvent.TYPE, this);
		eventBus.addHandler(MoveTaxaDownEvent.TYPE, this);
		eventBus.addHandler(MoveCharactersUpEvent.TYPE, this);
		eventBus.addHandler(MoveCharactersDownEvent.TYPE, this);
		eventBus.addHandler(MoveOrgansUpEvent.TYPE, this);
		eventBus.addHandler(MoveOrgansDownEvent.TYPE, this);
		eventBus.addHandler(AddCharacterEvent.TYPE, this);
		eventBus.addHandler(RemoveCharacterEvent.TYPE, this);
		eventBus.addHandler(ModifyCharacterEvent.TYPE, this);
		eventBus.addHandler(ModifyOrganEvent.TYPE, this);
		eventBus.addHandler(SetControlModeEvent.TYPE, this);
		eventBus.addHandler(SetValueEvent.TYPE, this);
		eventBus.addHandler(SetColorsEvent.TYPE, this);
		eventBus.addHandler(SortCharactersByNameEvent.TYPE, this);
		eventBus.addHandler(SortCharactersByCoverageEvent.TYPE, this);
		eventBus.addHandler(MergeCharactersEvent.TYPE, this);
		eventBus.addHandler(SetTaxonCommentEvent.TYPE, this);
		eventBus.addHandler(SetCharacterCommentEvent.TYPE, this);
		eventBus.addHandler(SetTaxonColorEvent.TYPE, this);
		eventBus.addHandler(SetCharacterColorEvent.TYPE, this);
		eventBus.addHandler(SetValueColorEvent.TYPE, this);
		eventBus.addHandler(SetValueCommentEvent.TYPE, this);
	}

	@Override
	public void onSet(SetControlModeEvent event) {
		setControlMode(event.getCharacter(), event.getControlMode(), event.getStates());
	}

	@Override
	public void onModify(ModifyOrganEvent event) {
		modifyOrgan(event.getOldOrgan(), event.getNewName());
	}

	@Override
	public void onModify(ModifyCharacterEvent event) {
		modifyCharacter(event.getOldCharacter(), event.getOldName(), event.getNewName(), event.getOldOrgan(), event.getNewOrgan());
	}

	@Override
	public void onRemove(RemoveCharacterEvent event) {
		removeCharacters(event.getCharacters());
	}

	@Override
	public void onAdd(AddCharacterEvent event) {
		addCharacterHierarchically(event.getOrgan(), event.getCharacter(), event.getAddAfterCharacter());
	}

	@Override
	public void onMove(MoveCharactersDownEvent event) {
		moveDownCharactersHierarchically(event.getCharacters());
	}

	@Override
	public void onMove(MoveCharactersUpEvent event) {
		moveUpCharactersHierarchically(event.getCharacters());
	}
	
	@Override
	public void onMove(MoveOrgansUpEvent event) {
		moveUpOrgansHierarchically(event.getOrgans());
	}

	@Override
	public void onMove(MoveOrgansDownEvent event) {
		moveDownOrgansHierarchically(event.getOrgans());
	}

	@Override
	public void onMove(MoveTaxaDownEvent event) {
		moveDownTaxaHierarchically(event.getTaxa());
	}

	@Override
	public void onMove(MoveTaxaUpEvent event) {
		moveUpTaxaHierarchically(event.getTaxa());
	}

	@Override
	public void onModify(ModifyTaxonEvent event) {
		modifyTaxon(event.getTaxon(), event.getParent(), event.getRank(), event.getName(), event.getAuthor(), event.getYear());
	}

	@Override
	public void onRemove(RemoveTaxaEvent event) {
		removeTaxa(event.getTaxa());
	}

	@Override
	public void onAdd(AddTaxonEvent event) {
		if(event.getParent() == null) {
			addRootTaxonHierarchically(event.getTaxon());
		} else {
			addTaxonHierarchically(event.getParent(), event.getTaxon());
		}
	}

	@Override
	public void onLoad(LoadModelEvent event) {
		this.model = event.getModel();
	}
	
	public void setControlMode(Character character, ControlMode controlMode, List<String> states) {
		model.setControlMode(character, controlMode);
		model.setStates(character, states);
	}

	public void moveDownCharactersHierarchically(List<Character> characters) {
		for(Character character : characters) {
			moveDownCharacterHierarchically(character);
		}
	}

	private void moveDownCharacterHierarchically(Character character) {
		Organ organ = character.getOrgan();
		List<Character> children = organ.getFlatCharacters();		
		int index = children.indexOf(character);
		if(index < children.size() - 1)
			Collections.swap(children, index, index + 1);
	}

	public void moveUpCharactersHierarchically(List<Character> characters) {
		for(Character character : characters) {
			moveUpCharacterHierarchically(character);
		}
	}

	private void moveUpCharacterHierarchically(Character character) {
		Organ organ = character.getOrgan();
		List<Character> children = organ.getFlatCharacters();		
		int index = children.indexOf(character);
		if(index > 0)
			Collections.swap(children, index, index - 1);
	}
	
	private void moveUpOrgansHierarchically(List<Organ> organs) {
		for(Organ organ : organs) {
			moveUpOrganHierarchically(organ);
		}
	}

	private void moveUpOrganHierarchically(Organ organ) {
		List<Organ> organs = model.getTaxonMatrix().getHierarchyCharacters();
		int index = organs.indexOf(organ);
		if(index > 0)
			Collections.swap(organs, index, index - 1);
	}

	private void moveDownOrgansHierarchically(List<Organ> organs) {
		for(Organ organ : organs) {
			moveDownOrganHierarchically(organ);
		}
	}
	
	private void moveDownOrganHierarchically(Organ organ) {
		List<Organ> organs = model.getTaxonMatrix().getHierarchyCharacters();
		int index = organs.indexOf(organ);
		if(index < organs.size() - 1)
			Collections.swap(organs, index, index + 1);
	}

	public void modifyOrgan(Organ organ, String name) {
		model.getTaxonMatrix().renameOrgan(organ, name);
	}
	
	public void modifyCharacter(Character character, String oldName, String newName, Organ oldOrgan, Organ newOrgan) {
		character.setName(newName);
		if(newOrgan != null && !newOrgan.equals(oldOrgan)) {
			character.setOrgan(newOrgan, 0);
			oldOrgan.remove(character);
		}
		if(oldOrgan.getCharacters().isEmpty()) {
			model.getTaxonMatrix().getHierarchyCharacters().remove(oldOrgan);
		}
	}
	
	public void removeCharacters(Collection<Character> characters) {
		for(Character character : characters) {
			removeCharacter(character);
		}
	}
		
	public void removeCharacter(Character character) {
		model.getTaxonMatrix().getFlatCharacters().remove(character);
		model.getTaxonMatrix().getVisibleCharacters().remove(character);
		Organ organ = character.getOrgan();
		organ.remove(character);
		if(organ.getCharacters().isEmpty())
			model.getTaxonMatrix().getHierarchyCharacters().remove(organ);
	}
	
	public void addCharacterHierarchically(Organ organ, Character character, Character addAfterCharacter) {
		if(!model.getTaxonMatrix().isContained(character)) {
			model.getTaxonMatrix().getVisibleCharacters().add(character);
			if(!model.getTaxonMatrix().isContained(organ)) {
				model.getTaxonMatrix().getHierarchyCharacters().add(organ);
			}
			if(!organ.isContained(character)) {
				if(addAfterCharacter != null && organ.getFlatCharacters().indexOf(addAfterCharacter) != -1) {
					organ.getFlatCharacters().add(organ.getFlatCharacters().indexOf(addAfterCharacter) + 1, character);
				} else {
					organ.getFlatCharacters().add(0, character);
				}
				organ.getCharacters().add(character);
			}
			
			List<Character> modelFlatCharacters = model.getTaxonMatrix().getFlatCharacters();
			if(addAfterCharacter != null && modelFlatCharacters.indexOf(addAfterCharacter) != -1) {
				modelFlatCharacters.add(modelFlatCharacters.indexOf(addAfterCharacter) + 1, character);
			} else {
				modelFlatCharacters.add(0, character);
			}
		}
	}
	
	public void moveDownTaxaHierarchically(List<Taxon> taxa) {
		for(Taxon taxon : taxa) {
			moveDownTaxonHierarchically(taxon);
		}
	}

	public void moveDownTaxonHierarchically(Taxon taxon) {
		Taxon parent = taxon.getParent();
		List<Taxon> children = parent.getChildren();
		int index = children.indexOf(taxon);
		if(index < children.size() - 1)
			Collections.swap(children, index, index + 1);
	}

	public void moveUpTaxonHierarchically(Taxon taxon) {
		Taxon parent = taxon.getParent();
		List<Taxon> children = parent.getChildren();
		int index = children.indexOf(taxon);
		if(index > 0)
			Collections.swap(children, index, index - 1);
	}
	
	public void moveUpTaxaHierarchically(List<Taxon> taxa) {
		for(Taxon taxon : taxa) {
			moveUpTaxonHierarchically(taxon);
		}
	}
	
	public void modifyTaxon(Taxon taxon, Taxon parent, Rank rank, String name, String author, String year) {
		if(Rank.isValidParentChild(parent == null ? null : parent.getRank(), rank)) {
			taxon.setRank(rank);
			moveTaxonHierarchically(parent, taxon);
			taxon.setName(name);
			taxon.setAuthor(author);
			taxon.setYear(year);
		} else {
			AlertMessageBox alertMessageBox = new AlertMessageBox("Modify Taxon", "Unable to modify: Incompatible rank of taxon with parent.");
			alertMessageBox.show();
		}
	}
	
	public void moveTaxonHierarchically(Taxon parent, Taxon taxon) {
		if(parent != null && !parent.equals(taxon.getParent())) {
			if(taxon.hasParent())
				taxon.getParent().removeChild(taxon);
			if(!Rank.isValidParentChild(parent == null ? null : parent.getRank(), taxon.getRank())) {
				throw new IllegalArgumentException("Invalid levels");
			}
			parent.addChild(taxon);
		}
	}

	public void removeTaxa(final Collection<Taxon> taxa) {
		for(Taxon taxon : taxa)
			removeTaxon(taxon);
	}
	
	public void removeTaxon(Taxon taxon) {
		model.getTaxonMatrix().getVisibleTaxa().remove(taxon);
		model.getTaxonMatrix().getFlatTaxa().remove(taxon);
		if(taxon.hasParent())
			taxon.getParent().removeChild(taxon);
		else
			model.getTaxonMatrix().getHierarchyRootTaxa().remove(taxon);
	}

	public void addRootTaxonHierarchically(Taxon taxon) {
		if(!model.getTaxonMatrix().isContained(taxon)) {
			model.getTaxonMatrix().getVisibleTaxa().add(taxon);
			model.getTaxonMatrix().getHierarchyRootTaxa().add(taxon);
			model.getTaxonMatrix().getFlatTaxa().add(taxon);
		}
	}
	
	public void addTaxonHierarchically(Taxon parent, int index, Taxon taxon) {
		if(!model.getTaxonMatrix().isContained(taxon)) {
			model.getTaxonMatrix().getVisibleTaxa().add(taxon);
			model.getTaxonMatrix().getFlatTaxa().add(taxon);
			
			if (!Rank.isValidParentChild(parent == null ? null : parent.getRank(), taxon.getRank())) {
				throw new IllegalArgumentException("Invalid levels");
			}
			taxon.setParent(parent);
			if (parent != null)
				parent.addChild(index, taxon);
		}
	}

	public void addTaxaHierarchically(Taxon parent, int index, List<Taxon> taxa) {
		for (Taxon child : taxa)
			addTaxonHierarchically(parent, index++, child);
	}

	public void addTaxonHierarchically(Taxon existingParent, Taxon taxon) {
		addTaxonHierarchically(existingParent, existingParent.getChildren().size(), taxon);
	}

	@Override
	public void onSet(SetValueEvent event) {
		for(Taxon taxon : event.getTaxa()) {
			for(Character character : event.getCharacters()) {
				if(event.getOldValues().containsKey(taxon) && event.getNewValues().containsKey(taxon)) {
					if(event.getOldValues().get(taxon).containsKey(character) && event.getNewValues().get(taxon).containsKey(character)) {
						Value oldValue = event.getOldValues().get(taxon).get(character);
						Value newValue = event.getNewValues().get(taxon).get(character);
						model.getTaxonMatrix().setValue(taxon, character, newValue);
						model.setColor(newValue, model.getColor(oldValue));
						model.setComment(newValue, model.getComment(oldValue));
					}
				}
			}
		}
		
	}

	@Override
	public void onSet(SetColorsEvent event) {
		model.getColors().clear();
		model.getColors().addAll(event.getColors());
	}

	private void sort(List<Taxon> taxaList, Comparator<Taxon> comparator) {
		Collections.sort(taxaList, comparator);
		for(Taxon taxon : taxaList) {
			this.sort(taxon.getChildren(), comparator);
		}
	}	
	
	@Override
	public void onSort(final SortTaxaByCoverageEvent event) {
		TaxaByCoverageComparator taxaByCoverageComparator = new TaxaByCoverageComparator(model, event.getSortDirection());
		List<Taxon> rootTaxa = model.getTaxonMatrix().getHierarchyRootTaxa();
		sort(rootTaxa, taxaByCoverageComparator);
		sort(model.getTaxonMatrix().getFlatTaxa(), taxaByCoverageComparator);
	}

	@Override
	public void onSort(final SortTaxaByCharacterEvent event) {
		TaxaByCharacterComparator taxonByCharacterComparator = new TaxaByCharacterComparator(model, event.getCharacter(), event.getSortDirection());
		List<Taxon> rootTaxa = model.getTaxonMatrix().getHierarchyRootTaxa();
		sort(rootTaxa, taxonByCharacterComparator);
		sort(model.getTaxonMatrix().getFlatTaxa(), taxonByCharacterComparator);
	}

	@Override
	public void onSort(final SortTaxaByNameEvent event) {
		TaxaByNameComparator taxaByNameComparator = new TaxaByNameComparator(event.getSortDirection());
		List<Taxon> rootTaxa = model.getTaxonMatrix().getHierarchyRootTaxa();
		sort(rootTaxa, taxaByNameComparator);
		sort(model.getTaxonMatrix().getFlatTaxa(), taxaByNameComparator);
	}

	@Override
	public void onSort(final SortCharactersByCoverageEvent event) {
		CharactersByCoverageComparator charactersByCoverageComparator = new CharactersByCoverageComparator(model, event.getSortDir());
		List<Organ> organs = model.getTaxonMatrix().getHierarchyCharacters();
		for(Organ organ : organs)
			Collections.sort(organ.getFlatCharacters(), charactersByCoverageComparator);
		Collections.sort(model.getTaxonMatrix().getFlatCharacters(), charactersByCoverageComparator);
	}

	@Override
	public void onSort(final SortCharactersByOrganEvent event) {
		CharactersByOrganComparator charactersByOrganComparator = new CharactersByOrganComparator(model, event.getSortDir());
		//List<Organ> organs = model.getTaxonMatrix().getHierarchyCharacters();
		Collections.sort(model.getTaxonMatrix().getFlatCharacters(), charactersByOrganComparator);
	}

	@Override
	public void onSort(final SortCharactersByNameEvent event) {
		CharactersByNameComparator charactersByNameComparator = new CharactersByNameComparator(model, event.getSortDir());
		List<Organ> organs = model.getTaxonMatrix().getHierarchyCharacters();
		for(Organ organ : organs)
			Collections.sort(organ.getFlatCharacters(), charactersByNameComparator);
		Collections.sort(model.getTaxonMatrix().getFlatCharacters(), charactersByNameComparator);
	}
	
	@Override
	public void onMerge(MergeCharactersEvent event) {
		mergeCharacters(event.getCharacter(), event.getTarget(), event.getMergeMode(), event.mergeAll(), event.getToMerge());
	}

	private void mergeCharacters(Character characterA, Character characterB,
			MergeMode mergeMode, boolean mergeAll, Set<Taxon> toMerge) {
		model.setControlMode(characterA, ControlMode.OFF);
		if(mergeAll) {
			String mergedName = mergeName(characterA.getName(), characterB.getName(), mergeMode);
			Organ mergedOrgan = mergeOrgan(characterA.getOrgan(), characterB.getOrgan(), mergeMode);
			for(Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
				mergeValueColorComment(taxon, characterA, characterB, mergeMode);
			}
			this.modifyCharacter(characterA, characterA.getName(), mergedName, characterA.getOrgan(), mergedOrgan);
			this.removeCharacter(characterB);
		} else {
			for(Taxon taxon : toMerge) {
				//String mergedName = mergeName(characterA.getName(), characterB.getName(), mergeMode);
				//Organ mergedOrgan = mergeOrgan(characterA.getOrgan(), characterB.getOrgan(), mergeMode);
				mergeValueColorComment(taxon, characterA, characterB, mergeMode);
				//this.modifyCharacter(characterA, characterA.getName(), mergedName, characterA.getOrgan(), mergedOrgan);
			}
			this.removeCharacter(characterB);
		}
	}
	
	private void mergeValueColorComment(Taxon taxon, Character characterA, Character characterB, MergeMode mergeMode) {
		Value a = model.getTaxonMatrix().getValue(taxon, characterA);
		Value b = model.getTaxonMatrix().getValue(taxon, characterB);
		String mergedValue = mergeValues(a, b, mergeMode);
		Color mergedColor = mergeColors(model.getColor(a), model.getColor(b), mergeMode);
		String mergedComment = mergeComment(model.getComment(a), model.getComment(b), mergeMode);
		Value newValue = new Value(mergedValue);
		model.getTaxonMatrix().setValue(taxon, characterA, newValue);
		model.setColor(newValue, mergedColor);
		model.setComment(newValue, mergedComment);
	}

	private String mergeName(String a, String b, MergeMode mergeMode) {
		a = a.trim();
		b = b.trim();
		if (a.isEmpty())
			return b;
		if (b.isEmpty())
			return a;
		switch (mergeMode) {
		case A_OVER_B:
			return a;
		case B_OVER_A:
			return b;
		case MIX:
		default:
			return a + " ; " + b;
		}
	}

	private Organ mergeOrgan(Organ a, Organ b, MergeMode mergeMode) {
		if (a == null)
			return b;
		if (b == null)
			return a;
		switch (mergeMode) {
		case A_OVER_B:
			return a;
		case B_OVER_A:
			return b;
		case MIX:
		default:
			if(a.getName().equals(b.getName()))
				return a;
			model.getTaxonMatrix().renameOrgan(a, a + " ; " + b);
			return a;
		}
	}

	private String mergeValues(Value a, Value b, MergeMode mergeMode) {
		String aValue = a.getValue().trim();
		String bValue = b.getValue().trim();
		if (aValue.isEmpty())
			return bValue;
		if (bValue.isEmpty())
			return aValue;
		switch (mergeMode) {
		case A_OVER_B:
			return aValue;
		case B_OVER_A:
			return bValue;
		case MIX:
		default:
			return a.getValue() + " ; " + b.getValue();
		}
	}

	private Color mergeColors(Color a, Color b, MergeMode mergeMode) {
		switch (mergeMode) {
		case A_OVER_B:
			return a;
		case B_OVER_A:
			return b;
		case MIX:
		default:
			return null;
		}
	}

	private String mergeComment(String a, String b, MergeMode mergeMode) {
		a = a.trim();
		b = b.trim();
		if (a.isEmpty())
			return b;
		if (b.isEmpty())
			return a;
		switch (mergeMode) {
		case A_OVER_B:
			return a;
		case B_OVER_A:
			return b;
		case MIX:
		default:
			return a + " ; " + b;
		}
	}

	@Override
	public void onSet(SetCharacterCommentEvent event) {
		model.setComment(event.getCharacter(), event.getComment());
	}

	@Override
	public void onSet(SetTaxonCommentEvent event) {
		model.setComment(event.getTaxon(), event.getComment());
	}
	
	@Override
	public void onSet(SetCharacterColorEvent event) {
		model.setColor(event.getCharacter(), event.getColor());
	}

	@Override
	public void onSet(SetTaxonColorEvent event) {
		model.setColor(event.getTaxon(), event.getColor());
	}

	@Override
	public void onSet(SetValueColorEvent event) {
		model.setColor(event.getValue(), event.getColor());
	}

	@Override
	public void onSet(SetValueCommentEvent event) {
		model.setComment(event.getValue(), event.getComment());
	}

}
