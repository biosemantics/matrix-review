package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.FrozenFirstColumnTreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ToggleCompareModeEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharactersGridView;
import edu.arizona.biosemantics.matrixreview.client.matrix.FrozenFirstColumTaxonTreeGrid;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxaColumnConfig;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleTaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class MatrixCompareView extends Composite {
	private static enum CompareMode {BY_TAXON, BY_CHARACTER};
	
	//Model
	private List<SimpleMatrixVersion> oldVersions;
	private MatrixVersion currentVersionSave; //this is only changed on the user clicking 'ok'.
	private MatrixVersion currentVersion;
	private CompareMode compareMode;
	private EventBus eventBus;
	
	private TaxonStore taxonStore;
	private TreeStore<CharacterTreeNode> characterStore;
	private Character selectedCharacter;
	private Taxon selectedTaxon;
	
	//UI 
	private SimpleContainer content;
	
	private HorizontalLayoutContainer northContent;
	private Label compareModeLabel;
	private Button changeCompareModeButton;
	
	private ContentPanel southPanel;
	private ContentPanel westPanel;
	private ContentPanel centerContent;
	
	public MatrixCompareView(List<SimpleMatrixVersion> old, MatrixVersion current){
		this.oldVersions = old;
		this.currentVersionSave = current;
		this.currentVersion = new MatrixVersion(current);
		this.eventBus = new SimpleEventBus();
		
		content = GWT.create(SimpleContainer.class);
		
		northContent = new HorizontalLayoutContainer();
		compareModeLabel = new Label();
		changeCompareModeButton = new Button();
		northContent.add(compareModeLabel);
		northContent.add(changeCompareModeButton);
		
		centerContent = new ContentPanel();
		centerContent.setHeaderVisible(false);
		
		
		selectedCharacter = oldVersions.get(0).getMatrix().getCharacter(0);
		selectedTaxon = oldVersions.get(0).getMatrix().getTaxon(0);
		
		
		
		createTaxonStore();
		createCharacterStore();
		
		
		westPanel = new ContentPanel();
		westPanel.setHeaderVisible(false);
		
		final BorderLayoutContainer container = new BorderLayoutContainer();
		
		BorderLayoutData westData = new BorderLayoutData(150);
		westData.setCollapsible(true);
		westData.setSplit(true);
		westData.setCollapseMini(true);
		
		container.setNorthWidget(northContent);
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
	
	interface CharacterProperties extends PropertyAccess<Character>{
		  ModelKeyProvider<Character> key();
		  LabelProvider<Character> nameLabel();
		  ValueProvider<Character, String> name();
	}
	
	private void updateCompareMode(CompareMode mode){
		this.compareMode = mode;
		westPanel.clear();
		centerContent.clear();
		if (this.compareMode == CompareMode.BY_TAXON){
			TaxonTreeGrid taxonGrid = TaxonTreeGrid.createNew(eventBus,  taxonStore);
			westPanel.add(taxonGrid.asWidget());
			CompareByTaxonGrid compareByTaxonGrid = new CompareByTaxonGrid(eventBus, characterStore, oldVersions, currentVersion, selectedTaxon);
			centerContent.add(compareByTaxonGrid.asWidget());
			compareModeLabel.setText("Currently comparing by taxon.");
			changeCompareModeButton.setText("View by character");
		} else{
			CharacterTreeGrid characterGrid = CharacterTreeGrid.createNew(eventBus, characterStore);
			westPanel.add(characterGrid.asWidget());
			CompareByCharacterGrid compareByCharacterGrid = new CompareByCharacterGrid(eventBus, taxonStore, oldVersions, currentVersion, selectedCharacter);
			centerContent.add(compareByCharacterGrid.asWidget());
			compareModeLabel.setText("Currently comparing by character.");
			changeCompareModeButton.setText("View by taxon");
		}
		content.forceLayout();
	}
	
	private List<Taxon> getAllUsedTaxons(){
		TaxonMatrix currentMatrix = currentVersion.getTaxonMatrix();
		//Make a list of all taxons that exist within these versions.
		List<Taxon> allTaxons = new ArrayList<Taxon>();
		for (SimpleMatrixVersion version: oldVersions){
			SimpleTaxonMatrix matrix = version.getMatrix();
			for (int i = 0; i < matrix.getTaxaCount(); i++){
				Taxon t = matrix.getTaxon(i);
				if (!allTaxons.contains(t)){
					allTaxons.add(t);
				}
			}
		}
		for (int i = 0; i < currentMatrix.getTaxaCount(); i++){
			Taxon t = currentMatrix.getTaxon(i);
			if (!allTaxons.contains(t)){
				allTaxons.add(t);
			}
		}
		return allTaxons;
	}
	
	
	private void addEventHandlers(){
		eventBus.addHandler(ChangeComparingSelectionEvent.TYPE, new ChangeComparingSelectionEvent.ChangeComparingSelectionEventHandler(){
			@Override
			public void onChange(ChangeComparingSelectionEvent event) {
				if (event.getSelection() instanceof Character){
					Character newCharacter = (Character)event.getSelection();
					selectedCharacter = newCharacter;
				} else if (event.getSelection() instanceof Taxon){
					Taxon newTaxon = (Taxon)event.getSelection();
					selectedTaxon = newTaxon;
				}
			}
		});
		
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
	
	public Widget asWidget(){
		return content.asWidget();
	}

	public MatrixVersion getModifiedVersion() {
		return currentVersion; 
	}
}
