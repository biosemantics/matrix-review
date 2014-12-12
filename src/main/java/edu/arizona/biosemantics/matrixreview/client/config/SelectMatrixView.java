package edu.arizona.biosemantics.matrixreview.client.config;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreAddEvent.StoreAddHandler;
import com.sencha.gxt.data.shared.event.StoreClearEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreHandlers;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent;
import com.sencha.gxt.data.shared.event.StoreRecordChangeEvent.StoreRecordChangeHandler;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent.StoreRemoveHandler;
import com.sencha.gxt.data.shared.event.StoreSortEvent;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.field.DualTreeField;
import edu.arizona.biosemantics.matrixreview.client.matrix.field.DualTreeFieldDefaultAppearance;
import edu.arizona.biosemantics.matrixreview.client.matrix.field.DualTreeField.Mode;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.*;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonProperties;

public class SelectMatrixView extends ContentPanel {

	private class UpdateSelectedHandler implements StoreAddHandler, StoreRemoveHandler {
		@Override
		public void onAdd(StoreAddEvent event) {
			updateSelectedHtml();
		}
		@Override
		public void onRemove(StoreRemoveEvent event) {
			updateSelectedHtml();
		}
	}	
	
	private OrganCharacterNodeProperties organCharacterNodeProperties = new OrganCharacterNodeProperties();
	private TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);
	private TreeStore<OrganCharacterNode> characterFromStore = new TreeStore<OrganCharacterNode>(organCharacterNodeProperties.key());
	private TreeStore<OrganCharacterNode> characterToStore = new TreeStore<OrganCharacterNode>(organCharacterNodeProperties.key());
	private TreeStore<Taxon> taxaFromStore = new TreeStore<Taxon>(taxonProperties.key());
	private TreeStore<Taxon> taxaToStore = new TreeStore<Taxon>(taxonProperties.key());

	
	private DualTreeField<OrganCharacterNode, String> organCharacterField;
	private DualTreeField<Taxon, String> taxaField;
	private HTML selectedHtml = new HTML();
	private UpdateSelectedHandler updateSelectedHandler = new UpdateSelectedHandler();
	protected Model model;

	public SelectMatrixView(Model model) {
		this.model = model;
		loadMatrix();
		organCharacterField = getDualOrganCharacterTreeField(model.getTaxonMatrix());		
		taxaField = getDualTaxaTreeField(model.getTaxonMatrix());
		
		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		
		FieldSet taxonFieldSet = new FieldSet();
		//taxonFieldSet.setCollapsible(true);
		taxonFieldSet.setHeadingText("Select Taxa to Show");
		taxonFieldSet.setWidget(taxaField);
		
		FieldSet characterFieldSet = new FieldSet();
		//characterFieldSet.setCollapsible(true);
		characterFieldSet.setHeadingText("Select Characters to Show");
		characterFieldSet.setWidget(organCharacterField);
		
		FieldSet resultFieldSet = new FieldSet();
		//resultFieldSet.setCollapsible(true);
		resultFieldSet.setHeadingText("Resulting Sub-Matrix");
		resultFieldSet.setWidget(selectedHtml);
		
		verticalLayoutContainer.add(taxonFieldSet, new VerticalLayoutData(1.0, 0.5));
		verticalLayoutContainer.add(characterFieldSet, new VerticalLayoutData(1.0, 0.5));	
		verticalLayoutContainer.add(resultFieldSet, new VerticalLayoutData(1.0, 150));
		this.setWidget(verticalLayoutContainer);
		
		TextButton nextButton = new TextButton("Next");
		nextButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				SelectMatrixView.this.hide();
			}
		});
		this.addButton(nextButton);
	}
	
	@Override
	public void hide() {
		if(!getSelectedCharacters().isEmpty() && !getSelectedRootTaxa().isEmpty())
			super.hide();
		else {
			AlertMessageBox alert = new AlertMessageBox("Nothing to Load", "You have to select at least one taxon and character");
			alert.show();
		}
	}
	
	private DualTreeField<Taxon, String> getDualTaxaTreeField(TaxonMatrix matrix) {
		taxaFromStore.setAutoCommit(true);
		taxaToStore.setAutoCommit(true);
		
		DualTreeField<Taxon, String> taxaField = new DualTreeField<Taxon, String>(taxaFromStore, taxaToStore, 
			taxonProperties.fullName(), 
			new TextCell(), 
			GWT.<DualTreeFieldDefaultAppearance> create(DualTreeFieldDefaultAppearance.class));
		
		taxaField.getToStore().addStoreDataChangeHandler(new StoreDataChangeHandler<Taxon>() {
			@Override
			public void onDataChange(StoreDataChangeEvent<Taxon> event) {
				updateSelectedHtml();
			}
		});
		taxaToStore.addStoreAddHandler(updateSelectedHandler);
		taxaToStore.addStoreRemoveHandler(updateSelectedHandler);
		taxaField.setMode(Mode.INSERT);
		taxaField.setEnableDnd(true);
		return taxaField;
	}
	
	private void updateSelectedHtml() {
		int taxa = this.getSelectedTaxa().size();
		int characters = this.getSelectedCharacters().size();
		int organs = this.getSelectedOrgans().size();
		selectedHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
				"<p><b>Selected Taxa: </b>" + taxa + "</p>" +
				"<p><b>Selected Characters: </b>" + characters + " (of " + organs + " organs)</p>" +
				"<p><b>Matrix values: </b>" + taxa * characters + "</p>"));
	}

	private void addToStoreRecursively(TreeStore<Taxon> store, Taxon taxon) {
		for(Taxon child : taxon.getChildren()) {
			store.add(taxon, child);
			this.addToStoreRecursively(store, child);
		}
	}
	
	private DualTreeField<OrganCharacterNode, String> getDualOrganCharacterTreeField(TaxonMatrix matrix) {		
		characterFromStore.setAutoCommit(true);
		characterToStore.setAutoCommit(true);
		DualTreeField<OrganCharacterNode, String> organCharacterField = new DualTreeField<OrganCharacterNode, String>(characterFromStore, characterToStore, 
			organCharacterNodeProperties.name(), 
			new TextCell(), 
			GWT.<DualTreeFieldDefaultAppearance> create(DualTreeFieldDefaultAppearance.class));
		characterToStore.addStoreAddHandler(updateSelectedHandler);
		characterToStore.addStoreRemoveHandler(updateSelectedHandler);
		organCharacterField.setMode(Mode.INSERT_MAINTAIN_ANCESTRY);
		organCharacterField.setEnableDnd(true);
		return organCharacterField;
	}
	
	protected void loadMatrix() {
		List<OrganCharacterNode> organCharacterNodes = new LinkedList<OrganCharacterNode>();
		for(Organ organ : model.getTaxonMatrix().getHierarchyCharacters()) {
			organCharacterNodes.add(new OrganNode(organ));
			for(Character character : organ.getFlatCharacters()) {
				organCharacterNodes.add(new CharacterNode(character));
			}
		}
		Map<Organ, OrganNode> organNodes = new HashMap<Organ, OrganNode>();
		
		for(OrganCharacterNode organCharacterNode : organCharacterNodes) {
			if(organCharacterNode instanceof OrganNode) {
				OrganNode organNode = (OrganNode)organCharacterNode;
				characterFromStore.add(organCharacterNode);
				organNodes.put(organNode.getOrgan(), organNode);
			}
		}

		for(OrganCharacterNode organCharacterNode : organCharacterNodes) {
			if(organCharacterNode instanceof CharacterNode) {
				CharacterNode characterNode = (CharacterNode)organCharacterNode;
				characterFromStore.add(organNodes.get(characterNode.getCharacter().getOrgan()), characterNode);
			}
		}
		
		for(Taxon rootTaxon : model.getTaxonMatrix().getHierarchyRootTaxa()) {
			taxaFromStore.add(rootTaxon);
			addToStoreRecursively(taxaFromStore, rootTaxon);
		}
	}
	
	public List<Character> getSelectedCharacters() {
		List<Character> result = new LinkedList<Character>();
		for(OrganCharacterNode node : organCharacterField.getValue()) {
			if(node instanceof CharacterNode) {
				result.add(((CharacterNode)node).getCharacter());
			}
		}
		return result;
	}
	
	public List<Taxon> getSelectedRootTaxa() {
		return taxaField.getRootValues();
	}
	
	public List<Taxon> getSelectedTaxa() {
		return taxaField.getValue();
	}
	
	private List<Organ> getSelectedOrgans() {
		List<Organ> result = new LinkedList<Organ>();
		for(OrganCharacterNode node : organCharacterField.getValue()) {
			if(node instanceof OrganNode) {
				result.add(((OrganNode)node).getOrgan());
			}
		}
		return result;
	}
}
