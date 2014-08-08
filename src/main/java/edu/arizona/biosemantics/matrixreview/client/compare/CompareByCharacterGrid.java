package edu.arizona.biosemantics.matrixreview.client.compare;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleTaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonPropertiesByLocation;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

/**
 * A widget that shows a Taxon/Version graph, using CharacterTreeNode as the selected constant. 
 * 
 * @author Andrew Stockton
 */

public class CompareByCharacterGrid extends ComparisonGrid<CharacterTreeNode, Taxon>{

	private TreeStore<Taxon> taxonStore;
	
	public CompareByCharacterGrid(EventBus eventBus, List<SimpleMatrixVersion> oldVersions, MatrixVersion currentVersion, CharacterTreeNode selectedConstant, TreeStore<Taxon> taxonStore2) {
		super(eventBus, oldVersions, currentVersion, selectedConstant);
		this.taxonStore = taxonStore2;
		this.init();
	}
	
	@Override
	protected void addEventHandlers(){
		super.addEventHandlers();
		
		/*eventBus.addHandler(CellClickEvent.getType(), new CellClickHandler(){
			@Override
			public void onCellClick(CellClickEvent event) {
				List<Taxon> instancesOfSameTaxon = new ArrayList<Taxon>();
				for (Taxon taxon: taxonStore.getAll()){
					if (taxon.getId().equals(selectedRow.getId())){
						instancesOfSameTaxon.add(taxon);
					}
				}
				controllerGrid.getSelectionModel().select(false, selectedRow);
				oldVersionsGrid.getSelectionModel().select(false, selectedRow);
				for (Taxon t: instancesOfSameTaxon){
					controllerGrid.getSelectionModel().select(true, t);
					oldVersionsGrid.getSelectionModel().select(true, t);
				}
			}
		});*/
		
		/**
		 * ChangeComparingSelectionEvent
		 * Fired when the selected constant has been changed and the grids should be reloaded 
		 * using the new value. 
		 */
		eventBus.addHandler(ChangeComparingSelectionEvent.TYPE, new ChangeComparingSelectionEvent.ChangeComparingSelectionEventHandler() {
			@Override
			public void onChange(ChangeComparingSelectionEvent event) {
				if (event.getSelection() instanceof CharacterTreeNode){
					updateSelectedConstant((CharacterTreeNode)event.getSelection());
				}
			}
		});
	}

	@Override
	protected Grid<Taxon> createOldVersionsGrid(MaintainListStoreTreeGrid<Taxon> controlColumn, List<SimpleMatrixVersion> oldVersions){
		Grid<Taxon> grid = super.createOldVersionsGrid(controlColumn, oldVersions);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		return grid;
	}
	
	@Override
	protected void setHeading(CharacterTreeNode subject) {
		this.setHeadingText("Viewing Character: " + subject.getData());
	}

	@Override
	protected MaintainListStoreTreeGrid<Taxon> createControllerGrid() {
		return TaxonTreeGrid.createNew(this.eventBus, this.taxonStore, true, currentVersion);
	}
	
	/**
	 * Overrides the super method: Only allow 'Character' nodes to be the selectedSubject, not 'Organ' 
	 * nodes. This ensures that the value providers can always be passed Characters, and
	 * not Organs. 
	 */
	@Override
	protected void updateSelectedConstant(CharacterTreeNode node){
		if (node.getData() instanceof Character)
			super.updateSelectedConstant(node);
	}

	@Override
	protected ValueProvider<Taxon, String> getSimpleVersionValueProvider(SimpleMatrixVersion version) {
		SimpleMatrixVersionProperties versionProperties = new SimpleMatrixVersionProperties(version);
		try{
			Character character = (Character)selectedConstant.getData(); //selectedSubject is guaranteed to have a Character and not an Organ. See overridden updateSelectedSubject method.
			return versionProperties.valueOfCharacter(character);
			
		} catch(Exception e){
			e.printStackTrace(); //surround in a try-catch just in case selectedSubject, for some reason, is not a 'Character' node.
			return new ValueProvider<Taxon, String>(){ //if something went wrong, show an "error" label.
				public String getValue(Taxon object) {
					return "ERROR";
				}
				public void setValue(Taxon object, String value) {}
				public String getPath() { return null; }
			};
		}
	}

	@Override
	protected ValueProvider<Taxon, String> getVersionValueProvider(
			MatrixVersion version) {
		MatrixVersionProperties versionProperties = new MatrixVersionProperties(version);
		try{
			Character character = (Character)selectedConstant.getData(); //selectedSubject is guaranteed to have a Character and not an Organ. See overridden updateSelectedSubject method.
			return versionProperties.valueOfCharacter(character);
			
		} catch(Exception e){
			e.printStackTrace(); //surround in a try-catch just in case selectedSubject, for some reason, is not a 'Character' node.
			return new ValueProvider<Taxon, String>(){ //if something went wrong, show an "error" label.
				public String getValue(Taxon object) {
					return "ERROR";
				}
				public void setValue(Taxon object, String value) {}
				public String getPath() { return null; }
			};
		}
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
						TaxonPropertiesByLocation props = new TaxonPropertiesByLocation(currentVersion);
						String key = props.key().getKey(taxon2);
						changedCells.add(new CellIdentifier(new CharacterTreeNode(character2), key));
						//System.out.println("Added a changed cell: character " + selectedConstant.getData() + " at column " + (i+1) + " with key " + key);
						//System.out.println(taxon1 + "/" + character1 + ": " + value1.getValue() + " to " + value2.getValue());
					}
				}
			}
		}
		return changedCells;
	}

	@Override
	protected void changeMatrixValue(Taxon taxon, String value, boolean allowEditMovedTaxon) {
		Character selectedCharacter = (Character)selectedConstant.getData(); //selectedSubject is guaranteed to have a Character and not an Organ. See overridden updateSelectedSubject method.
		TaxonMatrix matrix = currentVersion.getTaxonMatrix();
		
		//make sure that this taxon and character exist in the current version. 
		Taxon t = matrix.getTaxonById(taxon.getId()); 
		if (t == null)
			return;
		if (!allowEditMovedTaxon && t.getParent() != null && taxon.getParent() != null && !t.getParent().getId().equals(taxon.getParent().getId()))
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
	
	@Override
	protected Value getValue(SimpleMatrixVersion version, CharacterTreeNode selectedNode, Taxon taxon) {
		//make sure that this taxon and character exist in the current version.
		Taxon t = version.getMatrix().getTaxonById(taxon.getId()); 
		if (t == null)
			return null;
		if (t.getParent() != null && taxon.getParent() != null && !t.getParent().getId().equals(taxon.getParent().getId()))
			return null; //this taxon exists but has been moved. 
		if (selectedNode.getData() instanceof Character){
			Character c = version.getMatrix().getCharacterById(((Character)selectedNode.getData()).getId());
			return t.get(c);
		}
		return null;
	}

	@Override
	public String getQuickTip(CellIdentifier cell, int versionIndex) {
		try{
			SimpleMatrixVersion version = oldVersions.get(versionIndex);
			Taxon taxon = taxonStore.findModelWithKey((String)cell.getKey());
			Character selectedCharacter = (Character)((CharacterTreeNode)cell.getSelectedConstant()).getData();
			
			Taxon versionTaxon = version.getMatrix().getTaxonById(taxon.getId());
			Character versionCharacter = version.getMatrix().getCharacterById(selectedCharacter.getId());
			
			Taxon currentVersionTaxon = currentVersion.getTaxonMatrix().getTaxonById(taxon.getId());
			Character currentVersionCharacter = currentVersion.getTaxonMatrix().getCharacterById(selectedCharacter.getId());
			
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
			return "Error getting quick tip.";
		}
	}
}