package edu.arizona.biosemantics.matrixreview.client;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;

import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
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
	protected void changeMatrixValue(Taxon taxon, String value) {
		Character selectedCharacter = (Character)selectedConstant.getData(); //selectedSubject is guaranteed to have a Character and not an Organ. See overridden updateSelectedSubject method.
		TaxonMatrix matrix = currentVersion.getTaxonMatrix();
		
		//make sure that this taxon and character exist in the current version. 
		Taxon t = matrix.getTaxonById(taxon.getId()); 
		if (t == null)
			return;
		Character c = matrix.getCharacterById(selectedCharacter.getId());
		if (c == null)
			return;
		matrix.setValue(t, c, new Value(value));
	}
	
}