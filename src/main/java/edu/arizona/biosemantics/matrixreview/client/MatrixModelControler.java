package edu.arizona.biosemantics.matrixreview.client;

import java.util.Collection;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon.Level;

public class MatrixModelControler {

	private EventBus eventBus;
	private TaxonMatrix taxonMatrix;

	public MatrixModelControler(EventBus eventBus) {
		this.eventBus = eventBus;
		
		addEventHandlers();
	}

	private void addEventHandlers() {
		eventBus.addHandler(LoadTaxonMatrixEvent.TYPE, new LoadTaxonMatrixEvent.LoadTaxonMatrixEventHandler() {
			@Override
			public void onLoad(LoadTaxonMatrixEvent loadTaxonMatrixEvent) {
				MatrixModelControler.this.taxonMatrix = loadTaxonMatrixEvent.getTaxonMatrix();
			}
		});
		eventBus.addHandler(AddTaxonEvent.TYPE, new AddTaxonEvent.AddTaxonEventHandler() {
			@Override
			public void onAdd(AddTaxonEvent event) {
				if(event.getParent() == null) {
					addRootTaxon(event.getTaxon());
				} else {
					addTaxon(event.getParent(), event.getTaxon());
				}
			}
		});
		eventBus.addHandler(RemoveTaxaEvent.TYPE, new RemoveTaxaEvent.RemoveTaxonEventHandler() {
			@Override
			public void onRemove(final RemoveTaxaEvent event) {
				removeTaxon(event.getTaxa());
			}
		});
		eventBus.addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
			@Override
			public void onModify(ModifyTaxonEvent event) {
				if(Level.isValidParentChild(event.getParent() == null ? null : event.getParent().getLevel(), event.getLevel()))
					modifyTaxon(event.getTaxon(), event.getParent(), event.getLevel(), event.getName(), event.getAuthor(), event.getYear());
				else {
					AlertMessageBox alertMessageBox = new AlertMessageBox("Modify Taxon", "Unable to modify: Incompatible rank of taxon with parent.");
					alertMessageBox.show();
				}
			}
		});
		
		eventBus.addHandler(AddCharacterEvent.TYPE, new AddCharacterEvent.AddCharacterEventHandler() {
			@Override
			public void onAdd(AddCharacterEvent event) {
				if(event.getAfter() != null) {
					addCharacterAfter(taxonMatrix.getIndexOf(event.getAfter()), event.getCharacter());
				} else {
					addCharacter(event.getCharacter());
				}
			}
		});
		eventBus.addHandler(RemoveCharacterEvent.TYPE, new RemoveCharacterEvent.RemoveCharacterEventHandler() {
			@Override
			public void onRemove(RemoveCharacterEvent event) {
				removeCharacter(event.getCharacters());
			}
		});
		eventBus.addHandler(ModifyCharacterEvent.TYPE, new ModifyCharacterEvent.ModifyCharacterEventHandler() {
			@Override
			public void onModify(ModifyCharacterEvent event) {
				modifyCharacter(event.getOldCharacter(), event.getNewName(), event.getNewOrgan());
			}
		});
		eventBus.addHandler(ModifyOrganEvent.TYPE, new ModifyOrganEvent.ModifyOrganEventHandler() {
			@Override
			public void onModify(ModifyOrganEvent event) {
				modifyOrgan(event.getOldOrgan(), event.getNewName());
			}
		});
		eventBus.addHandler(SetControlModeEvent.TYPE, new SetControlModeEvent.SetControlModeEventHandler() {
			@Override
			public void onSet(SetControlModeEvent event) {
				setControlMode(event.getCharacter(), event.getControlMode(), event.getStates());
			}
		});
	}

	protected void modifyOrgan(Organ organ, String newName) {
		taxonMatrix.modifyOrgan(organ, newName);
	}

	protected void modifyCharacter(Character oldCharacter, String newName, Organ newOrgan) {
		taxonMatrix.modifyCharacter(oldCharacter, newName, newOrgan);
	}

	protected void setControlMode(Character character, ControlMode controlMode,	List<String> states) {
		taxonMatrix.setControlMode(character, controlMode);
		taxonMatrix.setCharacterStates(character, states);
	}
	
	protected void removeCharacter(Collection<Character> characters) {
		for(Character character : characters) {
			removeCharacter(character);
		}
	}
		
	protected void removeCharacter(Character character) {
		taxonMatrix.removeCharacter(character);
	}
		
	protected void addCharacter(Character character) {
		this.addCharacterAfter(taxonMatrix.getCharacterCount() - 1, character);
	}
	
	protected void addCharacterAfter(int colIndex, Character character) {
		taxonMatrix.addCharacter(colIndex + 1, character);
	}
	
	protected void modifyTaxon(Taxon taxon, Taxon parent, Level level, String name, String author, String year) {
		taxonMatrix.modifyTaxon(taxon, level, name, author, year);
	}
	
	protected void removeTaxon(final Collection<Taxon> taxa) {
		for(Taxon taxon : taxa)
			taxonMatrix.removeTaxon(taxon);
	}
	
	protected void addRootTaxon(Taxon taxon) {
		taxonMatrix.addRootTaxon(taxon);
	}
	
	protected void addTaxon(Taxon parent, int index, Taxon taxon) {
		taxonMatrix.addTaxon(parent, index, taxon);
	}
	
	protected void addTaxon(Taxon parent, Taxon taxon) {
		taxonMatrix.addTaxon(parent, taxon);
	}
}
