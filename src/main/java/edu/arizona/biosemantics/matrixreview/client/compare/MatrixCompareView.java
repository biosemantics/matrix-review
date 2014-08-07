package edu.arizona.biosemantics.matrixreview.client.compare;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleTaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonPropertiesByLocation;

/**
 * A window that holds a comparison grid and a selection grid. 
 * 
 * There are two compare modes: 
 * - BY_TAXON, where Taxon is the constant category and the selection grid shows a 
 * Character/Version graph,
 * - BY_CHARACTER, where Character is the constant category and the selection grid shows a 
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
	private CheckBox markChangedValuesCheckBox;

	private ContentPanel westPanel;
	private ContentPanel centerContent;
	
	private TaxonTreeGrid taxonGrid;
	private CompareByTaxonGrid compareByTaxonGrid;
	private CharacterTreeGrid characterGrid;
	private CompareByCharacterGrid compareByCharacterGrid;
	
	public MatrixCompareView(List<SimpleMatrixVersion> old, MatrixVersion current){
		this.oldVersions = old;
		this.currentVersion = new MatrixVersion(current);
		this.eventBus = new SimpleEventBus();
		
		content = GWT.create(SimpleContainer.class);
		
		compareModeLabel = new Label();
		changeCompareModeButton = new TextButton();
		markChangedValuesCheckBox = new CheckBox();
		markChangedValuesCheckBox.addValueChangeHandler(new ValueChangeHandler<Boolean>(){
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				//toggle 'mark changed values'.
				if (compareByTaxonGrid != null){
					boolean markCells = !compareByTaxonGrid.getMarkChangedCells();
					compareByTaxonGrid.setMarkChangedCells(markCells);
					if (compareMode == CompareMode.BY_TAXON)
						compareByTaxonGrid.refresh();
				}
				if (compareByCharacterGrid != null){
					boolean markCells = !compareByCharacterGrid.getMarkChangedCells();
					compareByCharacterGrid.setMarkChangedCells(markCells);
					if (compareMode == CompareMode.BY_CHARACTER)
						compareByCharacterGrid.refresh();
				}
			}
		});
		markChangedValuesCheckBox.setValue(true);
		northContent = new HBoxLayoutContainer();
		northContent.setPadding(new Padding(5));
		northContent.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		//northContent.setPack(BoxLayoutPack.END);
		BoxLayoutData layoutData = new BoxLayoutData(new Margins(0, 0, 0, 5));
		northContent.add(compareModeLabel, new BoxLayoutData(new Margins(0, 0, 0, 10)));
		northContent.add(changeCompareModeButton, layoutData);
		northContent.add(markChangedValuesCheckBox, new BoxLayoutData(new Margins(0, 0, 0, 70)));
		northContent.add(new Label("Mark changed cells"), new BoxLayoutData(new Margins(0, 0, 0, 0)));
		
		
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
		final TaxonPropertiesByLocation taxonProperties = new TaxonPropertiesByLocation();
		taxonStore = new TaxonStore(taxonProperties); 
		for (Taxon root: currentMatrix.getRootTaxa()){
			addTaxonAndChildrenToStore(root, null, 0);
		}
		for (SimpleMatrixVersion version: oldVersions){
			SimpleTaxonMatrix matrix = version.getMatrix();
			for (Taxon root: matrix.getRootTaxa()){
				addTaxonAndChildrenToStore(root, null, 0);
			}
		}
	}
	private void addTaxonAndChildrenToStore(Taxon taxon, Taxon parent, int depth){
		/*for (int i = 0; i < depth; i++)
			System.out.print("  ");
		System.out.print("Try to add " + taxon.getId() + " " + taxon.getFullName() + ", parent " + parent + ": ");*/
		if (taxonStore.findModel(taxon) == null){
			if (parent == null)
				taxonStore.add(taxon);
			else
				taxonStore.add(parent, taxon);
			//System.out.println("ADDED.");
		}
		else{
			//System.out.println("ALREADY EXISTS.");
		}
			
		for (Taxon child: taxon.getChildren()){
			addTaxonAndChildrenToStore(child, taxon, depth+1);
		}
	}
	
	private void createCharacterStore(){ 
		TaxonMatrix currentMatrix = currentVersion.getTaxonMatrix();
		
		CharacterTreeNodeProperties properties = new CharacterTreeNodeProperties();
		
		characterStore = new TreeStore<CharacterTreeNode>(properties.key());
		
		for (Character character: currentMatrix.getCharacters()){
			addCharacterToStore(character);
		}
		for (SimpleMatrixVersion version: oldVersions){
			for (Character character: version.getMatrix().getCharacters()){
				addCharacterToStore(character);
			}
		}
	}
	private void addCharacterToStore(Character character){
		CharacterTreeNode characterNode = new CharacterTreeNode(character);
		if (characterStore.findModel(characterNode) == null){
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
		eventBus = new SimpleEventBus();
		
		
		if (this.compareMode == CompareMode.BY_TAXON){
			final Taxon preselectedNode = getPreselectedTaxon();
			if (taxonGrid == null){
				taxonGrid = TaxonTreeGrid.createNew(eventBus,  taxonStore, false);				
				taxonGrid.addViewReadyHandler(new ViewReadyHandler(){
					@Override
					public void onViewReady(ViewReadyEvent event) {
						//Expand all parent nodes of the selected one. 
						Taxon parent = preselectedNode.getParent();
						if (parent != null){
							taxonGrid.setExpanded(parent, true);
						}
						taxonGrid.getSelectionModel().select(false, preselectedNode);
					}
				});
				taxonGrid.getSelectionModel().addSelectionHandler(new SelectionHandler<Taxon>(){
					@Override
					public void onSelection(SelectionEvent<Taxon> event) {
						Taxon taxonSelected = event.getSelectedItem();
						eventBus.fireEvent(new ChangeComparingSelectionEvent(taxonSelected));
					}
				});
			}
			if (compareByTaxonGrid == null){
				compareByTaxonGrid = new CompareByTaxonGrid(eventBus, oldVersions, currentVersion, preselectedNode, characterStore);
				compareByTaxonGrid.setMarkChangedCells(markChangedValuesCheckBox.getValue());
			}
			
			
			westPanel.add(taxonGrid.asWidget());
			centerContent.add(compareByTaxonGrid);
			compareByTaxonGrid.refresh();
			compareModeLabel.setText("Currently comparing by taxon.");
			changeCompareModeButton.setText("View by Character");
			
		} else{
			final CharacterTreeNode preselectedNode = getPreselectedCharacter();
			if (characterGrid == null){
				characterGrid = CharacterTreeGrid.createNew(eventBus, characterStore, false);
				characterGrid.addViewReadyHandler(new ViewReadyHandler(){
					@Override
					public void onViewReady(ViewReadyEvent event) {
						//Expand all parent nodes of the selected one. 
						if (preselectedNode.getData() instanceof Character){
							Character character = (Character)preselectedNode.getData();
							if (character.hasOrgan()){
								CharacterTreeNode organNode = new CharacterTreeNode(character.getOrgan());
								characterGrid.setExpanded(organNode, true);
							}
						}
						characterGrid.getSelectionModel().select(false, preselectedNode);
					}
				});
				characterGrid.getSelectionModel().addSelectionHandler(new SelectionHandler<CharacterTreeNode>(){
					@Override
					public void onSelection(SelectionEvent<CharacterTreeNode> event) {
						CharacterTreeNode nodeSelected = event.getSelectedItem();
						if (nodeSelected.getData() instanceof Character){
							eventBus.fireEvent(new ChangeComparingSelectionEvent(nodeSelected));
						}
					}
				});
			}
			if (compareByCharacterGrid == null){
				compareByCharacterGrid = new CompareByCharacterGrid(eventBus, oldVersions, currentVersion, preselectedNode, taxonStore);
				compareByCharacterGrid.setMarkChangedCells(markChangedValuesCheckBox.getValue());
			}
			
			westPanel.add(characterGrid.asWidget());
			centerContent.add(compareByCharacterGrid);
			compareByCharacterGrid.refresh();
			compareModeLabel.setText("Currently comparing by character.");
			changeCompareModeButton.setText("View by Taxon");
		}
		
		content.forceLayout();
	}

	private void addEventHandlers(){
		changeCompareModeButton.addSelectHandler(new SelectHandler(){
			@Override
			public void onSelect(SelectEvent event) {
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