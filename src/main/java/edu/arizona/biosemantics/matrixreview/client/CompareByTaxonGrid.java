package edu.arizona.biosemantics.matrixreview.client;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;

import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

/**
 * A widget that shows a Character/Version graph, using Taxon as the selected constant. 
 * 
 * @author Andrew Stockton
 */

public class CompareByTaxonGrid extends ComparisonGrid<Taxon, CharacterTreeNode>{

	private TreeStore<CharacterTreeNode> characterStore;
	
	public CompareByTaxonGrid(EventBus eventBus, List<SimpleMatrixVersion> oldVersions, MatrixVersion currentVersion, Taxon selectedSubject, TreeStore<CharacterTreeNode> store) {
		super(eventBus, oldVersions, currentVersion, selectedSubject);
		this.characterStore = store;
		this.init();
	}

	@Override
	protected void setHeading(Taxon subject) {
		this.setHeadingText("Viewing Taxon: " + subject);
	}

	@Override
	protected MaintainListStoreTreeGrid<CharacterTreeNode> createControllerGrid() {
		return CharacterTreeGrid.createNew(this.eventBus, this.characterStore, true);
	}

	@Override
	protected ValueProvider<CharacterTreeNode, String> getSimpleVersionValueProvider(SimpleMatrixVersion version) {
		SimpleMatrixVersionProperties versionProperties = new SimpleMatrixVersionProperties(version);
		return versionProperties.valueOfTaxon(selectedConstant);
	}

	@Override
	protected ValueProvider<CharacterTreeNode, String> getVersionValueProvider(MatrixVersion version){
		MatrixVersionProperties versionProperties = new MatrixVersionProperties(version);
		return versionProperties.valueOfTaxon(selectedConstant);
	}

	@Override
	protected void changeMatrixValue(CharacterTreeNode node, String value) {
		if (node.getData() instanceof Character) {
			Character selectedCharacter = (Character)node.getData();
			TaxonMatrix matrix = currentVersion.getTaxonMatrix();
			
			//make sure that this taxon and character exist in the current version. 
			Taxon t = matrix.getTaxonById(selectedConstant.getId()); 
			if (t == null)
				return;
			Character c = matrix.getCharacterById(selectedCharacter.getId());
			if (c == null)
				return;
			
			matrix.setValue(t, c, new Value(value));
		}
	}
	
}
	