package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.matrixreview.client.event.*;
import edu.arizona.biosemantics.matrixreview.client.event.HideCharacterEvent.HideCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.HideTaxaEvent.HideTaxaEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.LockCharacterEvent.LockCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.LockMatrixEvent.LockMatrixEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.LockTaxonEvent.LockTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.MatrixModeEvent.MatrixModeEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent.SetCharacterColorEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent.SetCharacterCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent.SetControlModeEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent.SetTaxonColorEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent.SetTaxonCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent.SetValueColorEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent.SetValueCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

/**
 * TODO: maintain a map character -> charactercolumnconfig? to avoid iterating over configs to get the one corresponding to a given character
 * this is done a couple of times in this class and could potentially be avoided
 * @author rodenhausen
 */
public class ModelControler extends edu.arizona.biosemantics.matrixreview.client.common.ModelControler implements MatrixModeEventHandler, 
	HideTaxaEventHandler, LockTaxonEventHandler, LockCharacterEventHandler, LockMatrixEventHandler, 
	SetControlModeEventHandler, HideCharacterEventHandler {
	
	public ModelControler(EventBus eventBus) {
		super(eventBus);
	}

	@Override
	protected void addEventHandlers() {
		super.addEventHandlers();
		
		eventBus.addHandler(MatrixModeEvent.TYPE, this);
		eventBus.addHandler(HideTaxaEvent.TYPE, this);
		eventBus.addHandler(LockTaxonEvent.TYPE, this);
		eventBus.addHandler(LockCharacterEvent.TYPE, this);
		eventBus.addHandler(LockMatrixEvent.TYPE, this);
		eventBus.addHandler(HideCharacterEvent.TYPE, this);
		
	}
	

	
	/*
	protected void moveTaxaHierarchically(boolean storeOnly, Taxon parent, int index, List<Taxon> taxa) {
		for(Taxon taxon : taxa)
			removeFromStoreRecursively(taxon);
		if (parent == null) {
			index = getMaximalValidRootIndex(index);
			if(!storeOnly)
				taxonMatrix.moveToRootTaxon(index, taxa);
			insertToStoreRecursively(index, taxa);
		} else {
			index = getMaximalValidIndex(parent, index);
			if(!storeOnly)
				taxonMatrix.addTaxonHierarchically(parent, index, taxa);
			insertToStoreRecursively(parent, index, taxa);
		}
		//System.out.println(taxonMatrix.list().toString());
	}

	private int getMaximalValidRootIndex(int index) {
		// drag and drop can sometime provides index that is out of bounds of stores underlying list
		while(taxonStore.getRootCount() < index) 
			index--;
		return index;
	}
	
	private int getMaximalValidIndex(Taxon parent, int index) {
		// drag and drop can sometime provides index that is out of bounds of stores underlying list
		while(taxonStore.getChildCount(parent) < index) 
			index--;
		return index;
	}

	protected void moveTaxonFlat(List<Taxon> taxa, Taxon after) {
		switch(modelMode) {
		case FLAT:
			for(Taxon taxon : taxa)
				taxonStore.remove(taxon);
			if(after == null || taxonStore.getRootItems().indexOf(after) == -1) {
				taxonStore.insert(0, taxa);
			} else {
				taxonStore.insert(taxonStore.getRootItems().indexOf(after) + 1, taxa);
			}
			break;
		case CUSTOM_HIERARCHY:
		case TAXONOMIC_HIERARCHY:
			for(Taxon taxon : taxa)
				taxonStore.remove(taxon);
			if(!taxa.isEmpty() && taxa.get(0).hasParent()) {
				Taxon parent = taxa.get(0).getParent();
				if(after == null) {
					this.insertToStoreRecursively(parent, 0, taxa);
				} else {
					this.insertToStoreRecursively(parent, parent.getChildren().indexOf(after), taxa);
				}
			} else {
				if(after == null) 
					this.insertToStoreRecursively(0, taxa);
				else
					this.insertToStoreRecursively(taxonStore.getRootItems().indexOf(after) + 1, taxa);
			}
			break;
		}
	}
	
	

	protected void moveCharacter(Character character, Character after) {
		taxonMatrix.moveCharacter(character, after);
		CharacterColumnConfig charactersConfig = null;
		CharacterColumnConfig afterConfig = null;
		List<CharacterColumnConfig> columns = new LinkedList<CharacterColumnConfig>(taxonTreeGrid.getColumnModel().getCharacterColumns());
		Iterator<CharacterColumnConfig> iterator = columns.iterator();
		while(iterator.hasNext()) {
			CharacterColumnConfig config = iterator.next();
			if(config.getCharacter().equals(character)) {
				charactersConfig = config;
				iterator.remove();
			}
			if(config.getCharacter().equals(after))
				afterConfig = config;
		}
		if(charactersConfig != null)
			if(afterConfig == null)
				columns.add(0, charactersConfig);
			else
				columns.add(columns.indexOf(afterConfig) + 1, charactersConfig);
		taxonTreeGrid.reconfigure(columns);
	}
	*/

	@Override
	public void onMode(MatrixModeEvent event) {
		model.setMatrixMode(event.getMode());
	}

	@Override
	public void onHide(HideTaxaEvent event) {
		for(Taxon taxon : event.getTaxa()) {
			hide(taxon, event.isHide());
		}
	}

	private void hide(Taxon taxon, boolean hide) {
		model.setHidden(taxon, hide);
	}

	@Override
	public void onLock(LockTaxonEvent event) {
		model.setLocked(event.getTaxon(), event.isLock());
	}

	@Override
	public void onLock(LockCharacterEvent event) {
		model.setLocked(event.getCharacter(), event.isLock());
	}

	@Override
	public void onLock(LockMatrixEvent event) {
		for(Character character : model.getTaxonMatrix().getVisibleFlatCharacters())
			model.setLocked(character, event.isLock());
		for(Taxon taxon : model.getTaxonMatrix().getVisibleFlatTaxa())
			model.setLocked(taxon, event.isLock());
	}

	@Override
	public void onHide(HideCharacterEvent event) {
		for(Character character : event.getCharacters())
			hide(character, event.isHide());
	}
	
	private void hide(Character character, boolean hide) {
		model.setHidden(character, hide);
	}

}