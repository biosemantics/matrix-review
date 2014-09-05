package edu.arizona.biosemantics.matrixreview.client;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.TaxonMenu;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.TaxonMenu.TaxonModifyDialog;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.CharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.OrganNode;

public class ManageTaxaView extends ContentPanel {

	private TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);
	private HTML infoHtml = new HTML();
	private TaxonMatrix matrix;
	private EventBus eventBus;
	private Tree<Taxon, String> tree;
	private TreeStore<Taxon> store = new TreeStore<Taxon>(taxonProperties.key());
	
	public ManageTaxaView(EventBus eventBus, boolean navigation) {
		this.eventBus = eventBus;
		tree = createTree(matrix);
		
		FieldSet taxaFieldSet = new FieldSet();
		//taxonFieldSet.setCollapsible(true);
		taxaFieldSet.setHeadingText("Taxa");
		taxaFieldSet.setWidget(tree);
		
		FieldSet infoFieldSet = new FieldSet();
		//taxonFieldSet.setCollapsible(true);
		infoFieldSet.setHeadingText("Taxon Details");
		infoFieldSet.setWidget(infoHtml);
		
		HorizontalLayoutContainer horizontalLayoutContainer = new HorizontalLayoutContainer();
		horizontalLayoutContainer.add(taxaFieldSet, new HorizontalLayoutData(0.5, 1.0));
		horizontalLayoutContainer.add(infoFieldSet, new HorizontalLayoutData(0.5, 1.0));

		VerticalLayoutContainer vertical = new VerticalLayoutContainer();
		vertical.add(horizontalLayoutContainer, new VerticalLayoutData(1.0, 1.0));
		vertical.add(createTaxaButtonBar(), new VerticalLayoutData());
				
		this.setWidget(vertical);
		
		if(navigation) {
			TextButton nextButton = new TextButton("Next");
			nextButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					ManageTaxaView.this.hide();
				}
			});
			this.addButton(nextButton);
		}
		
		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LoadTaxonMatrixEvent.TYPE, new LoadTaxonMatrixEvent.LoadTaxonMatrixEventHandler() {
			@Override
			public void onLoad(LoadTaxonMatrixEvent event) {
				matrix = event.getTaxonMatrix();
				loadMatrix();
			}
		});
		eventBus.addHandler(RemoveTaxaEvent.TYPE, new RemoveTaxaEvent.RemoveTaxonEventHandler() {
			@Override
			public void onRemove(RemoveTaxaEvent event) {
				for(Taxon taxon : event.getTaxa()) {
					store.remove(taxon);
				}
			}
		});
		eventBus.addHandler(AddTaxonEvent.TYPE, new AddTaxonEvent.AddTaxonEventHandler() {
			@Override
			public void onAdd(AddTaxonEvent event) {
				Taxon taxon = event.getTaxon();
				if(taxon.hasParent())
					store.add(taxon.getParent(), taxon);
				else
					store.add(taxon);
			}
		});
		eventBus.addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
			@Override
			public void onModify(ModifyTaxonEvent event) {
				Taxon taxon = event.getTaxon();
				if(!event.getParent().equals(store.getParent(taxon))) {
					store.remove(taxon);
					store.add(event.getParent(), taxon);
				}
				store.update(taxon);
			}
		});
	}

	private IsWidget createTaxaButtonBar() {
		ButtonBar taxaButtonBar = new ButtonBar();
		taxaButtonBar.setMinButtonWidth(75);
		taxaButtonBar.setPack(BoxLayoutPack.END);
		//taxaButtonBar.setVisible(false);
		TextButton addButton = new TextButton("Add");
		addButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				Taxon selected = tree.getSelectionModel().getSelectedItem();
				TaxonMenu.TaxonAddDialog addDialog = new TaxonMenu.TaxonAddDialog(eventBus, matrix, null);
				addDialog.show();
				if(selected != null)
					addDialog.selectParent(selected);
			}
		});
		TextButton modifyButton = new TextButton("Modify");
		modifyButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				Taxon selected = tree.getSelectionModel().getSelectedItem();
				TaxonModifyDialog modifyDialog = new TaxonModifyDialog(eventBus, matrix, selected);
				modifyDialog.show();
				if(selected != null && selected.getParent() != null)
					modifyDialog.selectParent(selected.getParent());
			}
		});
		taxaButtonBar.add(addButton);
		TextButton removeButton = new TextButton("Remove");
		removeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				List<Taxon> selected = tree.getSelectionModel().getSelectedItems();				
				for(final Taxon taxon : selected) {
					if (!taxon.getChildren().isEmpty()) {
						String childrenString = "";
						for (Taxon child : taxon.getChildren()) {
							childrenString += child.getFullName() + ", ";
						}
						childrenString = childrenString.substring(0, childrenString.length() - 2);
						ConfirmMessageBox box = new ConfirmMessageBox(
								"Remove Taxon",
								"Removing the taxon will also remove all of it's descendants: "
										+ childrenString);
						box.addDialogHideHandler(new DialogHideHandler() {
							@Override
							public void onDialogHide(DialogHideEvent event) {
								if(event.getHideButton().equals(PredefinedButton.YES)) {
									eventBus.fireEvent(new RemoveTaxaEvent(taxon));
								}
							}
							
						});
						box.show();
					} else {
						eventBus.fireEvent(new RemoveTaxaEvent(taxon));
					}
				}
			}
		});
		taxaButtonBar.add(addButton);
		taxaButtonBar.add(modifyButton);
		taxaButtonBar.add(removeButton);
		return taxaButtonBar;
	}

	private Tree<Taxon, String> createTree(TaxonMatrix matrix) {
		Tree<Taxon, String> tree = new Tree<Taxon, String>(store, taxonProperties.fullName());	
		tree.getSelectionModel().addSelectionHandler(new SelectionHandler<Taxon>() {
			@Override
			public void onSelection(SelectionEvent<Taxon> event) {
				Taxon selection = event.getSelectedItem();
				updateTextArea(selection);
			}
		});
		return tree;
	}
	
	protected void loadMatrix() {
		for(Taxon rootTaxon : matrix.getRootTaxa()) {
			store.add(rootTaxon);
			addToStoreRecursively(store, rootTaxon);
		}
	}

	protected void updateTextArea(Taxon taxon) {
		List<Taxon> ancestors = new LinkedList<Taxon>();
		Taxon parent = taxon.getParent();
		while(parent != null) {
			ancestors.add(parent);
			parent = parent.getParent();
		}

		String taxonomy = "";
		String prefix = "";
		for(int i=ancestors.size() - 1; i >= 0; i--) {
			Taxon anchestor = ancestors.get(i);
			prefix += "-";
			taxonomy += "<p>" + prefix + " " + 
					anchestor.getLevel().name() + " " + 
					anchestor.getName() + " " + 
					anchestor.getAuthor() + " " + 
					anchestor.getYear() + 
					"</p>";
		}
					
		infoHtml.setHTML(SafeHtmlUtils.fromSafeConstant(
			"<p><b>Rank:&nbsp;</b>" + taxon.getLevel().name() + "</p>" +
			"<p><b>Name:&nbsp;</b>" + taxon.getName() + "</p>" +
			"<p><b>Author:&nbsp;</b>" + taxon.getAuthor() + "</p>" +
			"<p><b>Year:&nbsp;</b>" + taxon.getYear() + "</p>" +
			"<p><b>Taxonomy:&nbsp;</b>" + taxonomy + "</p>" +
			"<p><b>Description:&nbsp;</b>" + taxon.getDescription() + "</p>"));
	}

	private void addToStoreRecursively(TreeStore<Taxon> store, Taxon taxon) {
		for(Taxon child : taxon.getChildren()) {
			store.add(taxon, child);
			this.addToStoreRecursively(store, child);
		}
	}

	public List<Taxon> getSelectedTaxa() {
		return tree.getSelectionModel().getSelectedItems();
	}
}
