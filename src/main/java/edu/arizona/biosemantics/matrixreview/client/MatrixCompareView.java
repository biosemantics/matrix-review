package edu.arizona.biosemantics.matrixreview.client;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widget.client.TextButton;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

/**
 * A window that holds a comparison grid and a selection grid. 
 * 
 * There are two compare modes: 
 * - BY_TAXON, where Taxon is the constant category and the selection grid shows a 
 * Character/Version graph,
 * - BY_CHARACTER where Character is the constant category and the selection grid shows a 
 * Taxon/Version graph. 
 * 
 * @author Andrew Stockton
 *
 */

public class MatrixCompareView extends Composite {
	private static enum CompareMode {BY_TAXON, BY_CHARACTER};
	
	private EventBus eventBus;
	private CompareMode compareMode;
	
	private List<SimpleMatrixVersion> oldVersions;
	private MatrixVersion currentVersion;
	
	private TaxonStore taxonStore;
	private TreeStore<CharacterTreeNode> characterStore;
	
	private SimpleContainer content;
	
	private HBoxLayoutContainer northContent;
	private Label compareModeLabel;
	private TextButton changeCompareModeButton;

	private ContentPanel westPanel;
	private ContentPanel centerContent;
	
	public MatrixCompareView(List<SimpleMatrixVersion> old, MatrixVersion current){
		this.oldVersions = old;
		this.currentVersion = new MatrixVersion(current);
		this.eventBus = new SimpleEventBus();
		
		content = GWT.create(SimpleContainer.class);
		
		compareModeLabel = new Label();
		changeCompareModeButton = new TextButton();
		northContent = new HBoxLayoutContainer();
		northContent.setPadding(new Padding(5));
		northContent.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		northContent.setPack(BoxLayoutPack.END);
		BoxLayoutData layoutData = new BoxLayoutData(new Margins(0, 5, 0, 0));
		northContent.add(compareModeLabel, layoutData);
		northContent.add(changeCompareModeButton, layoutData);
		
		centerContent = new ContentPanel();
		centerContent.setHeaderVisible(false);
		

		createTaxonStore(); //TODO: these need to get all taxons and characters used in all versions, not just the current version. 
		createCharacterStore();
		
		westPanel = new ContentPanel();
		westPanel.setHeaderVisible(false);
		
		final BorderLayoutContainer container = new BorderLayoutContainer();
		
		BorderLayoutData northData = new BorderLayoutData(40);
		
		BorderLayoutData westData = new BorderLayoutData(150);
		westData.setCollapsible(true);
		westData.setSplit(true);
		westData.setCollapseMini(true);
		
		container.setNorthWidget(northContent, northData);
		container.setWestWidget(westPanel, westData);
		container.setCenterWidget(centerContent, new MarginData());
		
		content.add(container);
		container.setBorders(true);
		
		addEventHandlers();
		updateCompareMode(CompareMode.BY_TAXON);
	}
	
	private void createTaxonStore(){
		TaxonMatrix currentMatrix = currentVersion.getTaxonMatrix();
		//Create store and populate with taxon list.
		taxonStore = new TaxonStore(); //this extends TreeStore<Taxon>. Awesome!
		for (Taxon root: currentMatrix.getRootTaxa()){
			taxonStore.add(root);
			addTaxonChildrenToStore(root);
		}
	}
	private void addTaxonChildrenToStore(Taxon root){
		for (Taxon child: root.getChildren()){
			taxonStore.add(root, child);
			addTaxonChildrenToStore(child);
		}
	}
	
	private void createCharacterStore(){ 
		TaxonMatrix currentMatrix = currentVersion.getTaxonMatrix();
		
		CharacterTreeNodeProperties properties = new CharacterTreeNodeProperties();
		
		characterStore = new TreeStore<CharacterTreeNode>(properties.key());
		
		for (Character character: currentMatrix.getCharacters()){
			CharacterTreeNode characterNode = new CharacterTreeNode(character);
			if (character.hasOrgan()){
				CharacterTreeNode organNode = new CharacterTreeNode(character.getOrgan());
				if (characterStore.findModel(organNode) == null){
					characterStore.add(organNode);
				}
				characterStore.add(organNode, characterNode); //add this character underneath the <organ name> folder.
			} else{
				characterStore.add(characterNode);
			}
		}
	}
	
	/**
	 * Clears west and center content and reloads it. 
	 * @param mode
	 */
	private void updateCompareMode(CompareMode mode){
		this.compareMode = mode;
		westPanel.clear();
		centerContent.clear();
		
		if (this.compareMode == CompareMode.BY_TAXON){
			TaxonTreeGrid taxonGrid = TaxonTreeGrid.createNew(eventBus,  taxonStore, false);
			westPanel.add(taxonGrid.asWidget());
			CompareByTaxonGrid compareByTaxonGrid = new CompareByTaxonGrid(eventBus, oldVersions, currentVersion, getPreselectedTaxon(), characterStore);
			centerContent.add(compareByTaxonGrid);
			compareModeLabel.setText("Currently comparing by taxon.");
			changeCompareModeButton.setText("View by character");
			
		} else{
			CharacterTreeGrid characterGrid = CharacterTreeGrid.createNew(eventBus, characterStore, false);
			westPanel.add(characterGrid.asWidget());
			CompareByCharacterGrid compareByCharacterGrid = new CompareByCharacterGrid(eventBus, oldVersions, currentVersion, getPreselectedCharacter(), taxonStore);
			centerContent.add(compareByCharacterGrid);
			compareModeLabel.setText("Currently comparing by character.");
			changeCompareModeButton.setText("View by taxon");
		}
		
		content.forceLayout();
	}

	private void addEventHandlers(){
		
		changeCompareModeButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				if (compareMode == CompareMode.BY_CHARACTER)
					updateCompareMode(CompareMode.BY_TAXON);
				else
					updateCompareMode(CompareMode.BY_CHARACTER);
			}
		});
	}
	
	private Taxon getPreselectedTaxon() {
		return oldVersions.get(0).getMatrix().getTaxon(0);
	}

	private CharacterTreeNode getPreselectedCharacter() {
		CharacterTreeNode selectedCharacterNode = null;
		for (CharacterTreeNode node: characterStore.getAll()){
			if (node.getData() instanceof Character){
				selectedCharacterNode = node;
				System.out.println("Selected node:" + selectedCharacterNode.getData());
				break;
			}
		}
		return selectedCharacterNode;
	}
	
	public Widget asWidget(){
		return content.asWidget();
	}

	public MatrixVersion getModifiedVersion() {
		return currentVersion; 
	}
}
