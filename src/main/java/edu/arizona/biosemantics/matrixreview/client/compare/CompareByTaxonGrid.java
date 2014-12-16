package edu.arizona.biosemantics.matrixreview.client.compare;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleTaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;

/**
 * A widget that shows a Character/Version graph, using Taxon as the selected constant. 
 * 
 * @author Andrew Stockton
 */

public class CompareByTaxonGrid extends ComparisonGrid<TaxonTreeNode, CharacterTreeNode>{

	private TreeStore<CharacterTreeNode> characterStore;
	
	public CompareByTaxonGrid(EventBus eventBus, List<SimpleMatrixVersion> oldVersions, MatrixVersion currentVersion, TaxonTreeNode selectedConstant, TreeStore<CharacterTreeNode> store) {
		super(eventBus, oldVersions, currentVersion, selectedConstant);
		this.characterStore = store;
		this.init();
	}
	
	@Override
	protected void addEventHandlers(){
		super.addEventHandlers();
		
		/**
		 * ChangeComparingSelectionEvent
		 * Fired when the selected constant has been changed and the grids should be reloaded 
		 * using the new value. 
		 */
		eventBus.addHandler(ChangeComparingSelectionEvent.TYPE, new ChangeComparingSelectionEvent.ChangeComparingSelectionEventHandler() {
			@Override
			public void onChange(ChangeComparingSelectionEvent event) {
				if (event.getSelection() instanceof TaxonTreeNode){
					updateSelectedConstant((TaxonTreeNode)event.getSelection());
				}
			}
		});
	}

	@Override
	protected void setHeading(TaxonTreeNode subject) {
		this.setHeadingText("Viewing Taxon: " + subject.getData());
	}

	@Override
	protected MaintainListStoreTreeGrid<CharacterTreeNode> createControllerGrid() {
		return CharacterTreeGrid.createNew(this.eventBus, this.characterStore, true);
	}

	@Override
	protected ValueProvider<CharacterTreeNode, String> getSimpleVersionValueProvider(SimpleMatrixVersion version) {
		SimpleMatrixVersionProperties versionProperties = new SimpleMatrixVersionProperties(version);
		return versionProperties.valueOfTaxon(selectedConstant.getData());
	}

	@Override
	protected ValueProvider<CharacterTreeNode, String> getVersionValueProvider(MatrixVersion version){
		MatrixVersionProperties versionProperties = new MatrixVersionProperties(version);
		return versionProperties.valueOfTaxon(selectedConstant.getData());
	}
	
	@Override
	protected List<CellIdentifier> getChangedCells(SimpleMatrixVersion version1, SimpleMatrixVersion version2){
		//TODO: This searches for comparable taxons/characters in the next version, by ID. This might
		//have to change to something else if we decide that taxons/characters should be matched by
		//name instead of id. 
		
		List<CellIdentifier> changedCells = new LinkedList<CellIdentifier>();
		
		SimpleTaxonMatrix matrix1 = version1.getMatrix();
		SimpleTaxonMatrix matrix2 = version2.getMatrix();
		for (Taxon taxon1: matrix1.list()){
			for (Character character1: matrix1.getCharacters()){
				Value value1 = taxon1.get(character1);
				
				//See if this taxon and character exist in the next version.
				Taxon taxon2 = matrix2.getTaxonById(taxon1.getId());
				Character character2 = matrix2.getCharacterById(character1.getId());
				if (taxon2 != null && character2 != null){
					Value value2 = taxon2.get(character2);
				
					//compare value from version1 and value from version2. If they differ, set the version 2 cell to changed.
					if (!value1.getValue().equals(value2.getValue())){
						CharacterTreeNodeProperties props = new CharacterTreeNodeProperties();
						String key = props.key().getKey(new CharacterTreeNode(character2));
						changedCells.add(new CellIdentifier(new TaxonTreeNode(taxon2), key));
						//System.out.println("Added a changed cell: taxon " + taxon2 + " with key " + key);
						//System.out.println(taxon1 + "/" + character1 + ": " + value1.getValue() + " to " + value2.getValue());
					}
				}
			}
		}
		return changedCells;
	}

	@Override
	protected void changeMatrixValue(CharacterTreeNode node, String value, boolean allowEditMovedTaxon) {
		if (node.getData() instanceof Character) {
			Character selectedCharacter = (Character)node.getData();
			TaxonMatrix matrix = currentVersion.getTaxonMatrix();
			
			//make sure that this taxon and character exist in the current version. 
			Taxon t = matrix.getTaxonById(selectedConstant.getData().getId()); 
			if (t == null)
				return;
			if (!allowEditMovedTaxon && t.getParent() != null && selectedConstant.getData().getParent() != null && !t.getParent().getId().equals(selectedConstant.getData().getParent().getId()))
				return; //this taxon exists but has been moved - do not allow edit from this 'old' location. 
			Character c = matrix.getCharacterById(selectedCharacter.getId());
			if (c == null)
				return;
			
			ControlMode controlMode = c.getControlMode();
			if (controlMode == ControlMode.NUMERICAL && (value == null || !value.matches("[0-9]*")))
				return;
			if (controlMode == ControlMode.CATEGORICAL && !c.getStates().contains(value))
				return;
			
			matrix.setValue(t, c, new Value(value));
		}
	}

	@Override
	protected Value getValue(SimpleMatrixVersion version, TaxonTreeNode selectedTaxon, CharacterTreeNode node) {
		//make sure that this taxon and character exist in the current version.
		Taxon t = version.getMatrix().getTaxonById(selectedTaxon.getData().getId()); 
		if (t == null)
			return null;
		if (t.getParent() != null && selectedTaxon.getData().getParent() != null && !t.getParent().getId().equals(selectedTaxon.getData().getParent().getId()))
			return null; //this taxon exists but has been moved. 
		if (node.getData() instanceof Character){
			Character c = version.getMatrix().getCharacterById(((Character)node.getData()).getId());
			return t.get(c);
		}
		return null;
	}

	@Override
	public String getQuickTip(CellIdentifier cell, int versionIndex) {
		try{
			SimpleMatrixVersion version = oldVersions.get(versionIndex);
			Taxon selectedTaxon = ((TaxonTreeNode)cell.getSelectedConstant()).getData();
			CharacterTreeNode node = characterStore.findModelWithKey((String)cell.getKey());
			if (!(node.getData() instanceof Character)){
				return "";
			}
			Character character = (Character)node.getData();
			
			Taxon versionTaxon = version.getMatrix().getTaxonById(selectedTaxon.getId());
			Character versionCharacter = version.getMatrix().getCharacterById(character.getId());
			
			if (versionTaxon == null || versionCharacter == null) //either this taxon or this character did not exist in this version.
				return "";
			
			Taxon currentVersionTaxon = currentVersion.getTaxonMatrix().getTaxonById(selectedTaxon.getId());
			Character currentVersionCharacter = currentVersion.getTaxonMatrix().getCharacterById(character.getId());
			
			String tip = "";
			tip += "<b><i>Taxon:</i> " + (currentVersionTaxon == null ? versionTaxon.getName() : currentVersionTaxon.getName()) + "</b>";
			if (currentVersionTaxon != null && !versionTaxon.getName().equals(currentVersionTaxon.getName())){ //name was changed since this version.
				tip += "&nbsp;(formerly <i>" + versionTaxon.getName() + "</i>)";
			}
			tip += "<br>";
			tip += "<i>Author: </i>" + versionTaxon.getAuthor() + "<br>";
			tip += "<i>Year: </i>" + versionTaxon.getYear() + "<br>";
			tip += "<i>Description: </i>" + versionTaxon.getDescription() + "<br>";
			tip += "<i>Comment: </i>" + versionTaxon.getComment() + "<br>";
			tip += "<br>";
			
			tip += "<b><i>Character:</i> " + (currentVersionCharacter == null ? versionCharacter.toString() : currentVersionCharacter.toString()) + "</b>";
			if (currentVersionCharacter != null && !versionCharacter.toString().equals(currentVersionCharacter.toString())){ //name was changed since this version.
				tip += " (formerly <i>" + versionCharacter.toString() + "</i>)";
			}
			tip += "<br>";
			tip += "<i>Comment: </i>" + versionCharacter.getComment();
			tip += "<br>&nbsp;";
			
			return tip;
			
		} catch(Exception e){
			e.printStackTrace();
			return "Error getting quick tip.";
		}
	}
}
	