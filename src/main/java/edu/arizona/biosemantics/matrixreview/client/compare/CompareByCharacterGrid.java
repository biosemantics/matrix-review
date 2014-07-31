package edu.arizona.biosemantics.matrixreview.client.compare;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;

import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleTaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
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

	private TaxonStore taxonStore;
	
	public CompareByCharacterGrid(EventBus eventBus, List<SimpleMatrixVersion> oldVersions, MatrixVersion currentVersion, CharacterTreeNode selectedSubject, TaxonStore store) {
		super(eventBus, oldVersions, currentVersion, selectedSubject);
		this.taxonStore = store;
		this.init();
	}

	@Override
	protected void setHeading(CharacterTreeNode subject) {
		this.setHeadingText("Viewing Character: " + subject.getData());
	}

	@Override
	protected MaintainListStoreTreeGrid<Taxon> createControllerGrid() {
		return TaxonTreeGrid.createNew(this.eventBus, this.taxonStore, true);
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
						TaxonPropertiesByLocation props = new TaxonPropertiesByLocation();
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
}