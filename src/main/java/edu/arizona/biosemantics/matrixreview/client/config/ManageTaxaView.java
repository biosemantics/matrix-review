package edu.arizona.biosemantics.matrixreview.client.config;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.AdapterMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.matrixreview.client.common.InputElementVisibleTextField;
import edu.arizona.biosemantics.matrixreview.client.common.SetValueValidator;
import edu.arizona.biosemantics.matrixreview.client.common.SetValueValidator.ValidationResult;
import edu.arizona.biosemantics.matrixreview.client.common.TaxonAddDialog;
import edu.arizona.biosemantics.matrixreview.client.common.TaxonIconProvider;
import edu.arizona.biosemantics.matrixreview.client.common.TaxonModifyDialog;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaDownEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaUpEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDescriptionEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.TaxonMenu;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.MatrixEntry;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.CharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.OrganNode;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class ManageTaxaView extends ContentPanel {

	private TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);
	private HTML infoHtml = new HTML();
	private Model model;
	private EventBus eventBus;
	private Tree<Taxon, Taxon> tree;
	private TreeStore<Taxon> store = new TreeStore<Taxon>(taxonProperties.key());
	private Set<SelectionChangedHandler<Taxon>> selectionChangeHandlers = 
			new HashSet<SelectionChangedHandler<Taxon>>();
	private ManageMatrixView manageMatrixView;
	
	public ManageTaxaView(EventBus eventBus, boolean navigation, ManageMatrixView manageMatrixView) {
		this.eventBus = eventBus;
		this.manageMatrixView = manageMatrixView;
		tree = createTree();
		
		this.setTitle("Right-clicks on taxa to bring up more taxa management functions");
		this.setHeadingText("Select Taxa for the Matrix");
		
		FieldSet taxaFieldSet = new FieldSet();
		//taxonFieldSet.setCollapsible(true);
		taxaFieldSet.setHeadingText("Taxa");
		taxaFieldSet.setWidget(tree);
		
		FieldSet infoFieldSet = new FieldSet();
		//taxonFieldSet.setCollapsible(true);
		infoFieldSet.setHeadingText("Taxon Details");
		FlowLayoutContainer flowInfoHtml = new FlowLayoutContainer();
		flowInfoHtml.add(infoHtml);
		flowInfoHtml.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		infoFieldSet.setWidget(flowInfoHtml);
		
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
		eventBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
			@Override
			public void onLoad(LoadModelEvent event) {
				model = event.getModel();
				loadModel();
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
				
				if(event.getParent() == null && taxon.getParent() != null) {
					store.remove(taxon);
					store.add(taxon);
				} else if(event.getParent() != null && 
						!event.getParent().equals(store.getParent(taxon))) {
					store.remove(taxon);
					store.add(event.getParent(), taxon);
				}
				store.update(taxon);
			}
		});
		eventBus.addHandler(MoveTaxaUpEvent.TYPE, new MoveTaxaUpEvent.MoveTaxaUpEventHandler() {
			@Override
			public void onMove(MoveTaxaUpEvent event) {
				move(event.getTaxa(), true);
			}
		});
		eventBus.addHandler(MoveTaxaDownEvent.TYPE, new MoveTaxaDownEvent.MoveTaxaDownEventHandler() {
			@Override
			public void onMove(MoveTaxaDownEvent event) {
				move(event.getTaxa(), false);
			}
		});
		eventBus.addHandler(SetTaxonColorEvent.TYPE, new SetTaxonColorEvent.SetTaxonColorEventHandler() {
			@Override
			public void onSet(SetTaxonColorEvent event) {
				store.update(event.getTaxon());
			}
		});
		eventBus.addHandler(SetTaxonCommentEvent.TYPE, new SetTaxonCommentEvent.SetTaxonCommentEventHandler() {
			@Override
			public void onSet(SetTaxonCommentEvent event) {
				store.update(event.getTaxon());
			}

			private void bindEvents() {
				eventBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
					@Override
					public void onLoad(LoadModelEvent event) {
						model = event.getModel();
						loadModel();
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
						
						if(event.getParent() == null && taxon.getParent() != null) {
							store.remove(taxon);
							store.add(taxon);
						} else if(event.getParent() != null && 
								!event.getParent().equals(store.getParent(taxon))) {
							store.remove(taxon);
							store.add(event.getParent(), taxon);
						}
						store.update(taxon);
					}
				});
				eventBus.addHandler(MoveTaxaUpEvent.TYPE, new MoveTaxaUpEvent.MoveTaxaUpEventHandler() {
					@Override
					public void onMove(MoveTaxaUpEvent event) {
						move(event.getTaxa(), true);
					}
				});
				eventBus.addHandler(MoveTaxaDownEvent.TYPE, new MoveTaxaDownEvent.MoveTaxaDownEventHandler() {
					@Override
					public void onMove(MoveTaxaDownEvent event) {
						move(event.getTaxa(), false);
					}
				});
				eventBus.addHandler(SetTaxonColorEvent.TYPE, new SetTaxonColorEvent.SetTaxonColorEventHandler() {
					@Override
					public void onSet(SetTaxonColorEvent event) {
						store.update(event.getTaxon());
					}
				});
				eventBus.addHandler(SetTaxonCommentEvent.TYPE, new SetTaxonCommentEvent.SetTaxonCommentEventHandler() {
					@Override
					public void onSet(SetTaxonCommentEvent event) {
						store.update(event.getTaxon());
					}
				});
				
				
				tree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Taxon>() {
					@Override
					public void onSelectionChanged(SelectionChangedEvent<Taxon> event) {
						for(SelectionChangedHandler<Taxon> handler :  selectionChangeHandlers) {
							handler.onSelectionChanged(new SelectionChangedEvent<Taxon>(event.getSelection()));
						}
					}
				});
				
			}
		});
		
		
		tree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<Taxon>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<Taxon> event) {
				for(SelectionChangedHandler<Taxon> handler :  selectionChangeHandlers) {
					handler.onSelectionChanged(new SelectionChangedEvent<Taxon>(event.getSelection()));
				}
			}
		});
		
	}

	protected void move(List<Taxon> taxa, boolean up) {
		Map<Taxon, Set<Taxon>> parentChildrenToMove = new HashMap<Taxon, Set<Taxon>>();
		for(Taxon taxon : taxa) {
			Taxon parent = taxon.getParent();
			if(!parentChildrenToMove.containsKey(parent)) {
				parentChildrenToMove.put(parent, new HashSet<Taxon>());
			}
			parentChildrenToMove.get(parent).add(taxon);
		}
		
		for(final Taxon parent : parentChildrenToMove.keySet()) {
			Set<Taxon> toMove = parentChildrenToMove.get(parent);
			
			List<Taxon> storeChildren = null;
			if(parent == null)
				storeChildren = store.getRootItems();
			else
				storeChildren = store.getChildren(parent);
			final List<Taxon> newStoreChildren =  new LinkedList<Taxon>(storeChildren);
			
			if(storeChildren.size() > 1) {
				if(up) {
					for(int i=1; i<newStoreChildren.size(); i++) {
						Taxon previousStoreChild = newStoreChildren.get(i-1);
						Taxon storeChild = newStoreChildren.get(i);
						if(toMove.contains(storeChild) && !toMove.contains(previousStoreChild)) {
							Collections.swap(newStoreChildren, i, i-1);
						}
					}
				} else {
					for(int i=newStoreChildren.size() - 2; i>=0; i--) {
						Taxon nextStoreChild = newStoreChildren.get(i+1);
						Taxon storeChild = newStoreChildren.get(i);
						if(toMove.contains(storeChild) && !toMove.contains(nextStoreChild)) {
							Collections.swap(newStoreChildren, i, i+1);
						}
					}
				}
	
				List<TreeNode<Taxon>> subtrees = new LinkedList<TreeNode<Taxon>>();
				for(Taxon storeChild : newStoreChildren) {
					subtrees.add(store.getSubTree(storeChild));
				}
				
				//non-root
				if(parent != null) {
					store.removeChildren(parent);
					store.addSubTree(parent, 0, subtrees);
					tree.setExpanded(parent, true, true);
				} else {
					store.clear();
					store.addSubTree(0, subtrees);
					tree.expandAll();
				}
			}
		}
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
				showTaxonAdd();
			}
		});
		TextButton modifyButton = new TextButton("Modify");
		modifyButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showTaxonModify();
			}
		});
		TextButton removeButton = new TextButton("Remove");
		removeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showTaxonRemove();
			}
		});
		TextButton upButton = new TextButton("Move Up");
		upButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showTaxonUp();
			}
		});
		TextButton downButton = new TextButton("Move Down");
		downButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showTaxonDown();
			}
		});
		
		//taxaButtonBar.add(addButton);
		//taxaButtonBar.add(modifyButton);
		//taxaButtonBar.add(removeButton);
		//taxaButtonBar.add(upButton);
		//taxaButtonBar.add(downButton);
		return taxaButtonBar;
	}

	protected void showTaxonUp() {
		List<Taxon> selected = tree.getSelectionModel().getSelectedItems();
		eventBus.fireEvent(new MoveTaxaUpEvent(selected));
		tree.getSelectionModel().setSelection(selected);
	}

	protected void showTaxonDown() {
		List<Taxon> selected = tree.getSelectionModel().getSelectedItems();
		eventBus.fireEvent(new MoveTaxaDownEvent(selected));
		tree.getSelectionModel().setSelection(selected);
	}

	protected void showTaxonRemove() {
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
						"Remove the taxon will also remove all of it's descendants: "
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

	protected void showTaxonModify() {
		Taxon selected = tree.getSelectionModel().getSelectedItem();
		TaxonModifyDialog modifyDialog = new TaxonModifyDialog(eventBus, model, selected);
		modifyDialog.show();
		if(selected != null && selected.getParent() != null)
			modifyDialog.selectParent(selected.getParent());
	}

	protected void showTaxonAdd() {
		Taxon selected = tree.getSelectionModel().getSelectedItem();
		TaxonAddDialog addDialog = new TaxonAddDialog(eventBus, model, null);
		addDialog.show();
		if(selected != null)
			addDialog.selectParent(selected);
	}
	
	protected void setValue(String value) {
		final List<Taxon> taxa = manageMatrixView.getSelectedTaxa();
		final List<Character> characters = manageMatrixView.getSelectedCharacters();
	
		SetValueValidator setValueValidator = new SetValueValidator(model);
		Set<Character> charactersToSet = new HashSet<Character>();
		for(Character character : characters) {
			ValidationResult validationResult = setValueValidator.validValue(value, character);
			if(validationResult.isValid()) {
				charactersToSet.add(character);
			} else {
				AlertMessageBox alert = new AlertMessageBox("Set value failed", "Can't set value " +
						value + " for " + character.getName() + ". Control mode " + 
						model.getControlMode(character).toString().toLowerCase() + " was selected for " + character.getName());
				alert.show();
			}
		}
		Set<Taxon> taxaToSet = new HashSet<Taxon>(taxa);
		Map<Taxon, Map<Character, Value>> oldValues = new HashMap<Taxon, Map<Character, Value>>();
		Map<Taxon, Map<Character, Value>> newValues = new HashMap<Taxon, Map<Character, Value>>();
		for(Taxon taxonToSet : taxaToSet) {
			oldValues.put(taxonToSet, new HashMap<Character, Value>());
			for(Character characterToSet : charactersToSet) {
				oldValues.get(taxonToSet).put(characterToSet, model.getTaxonMatrix().getValue(taxonToSet, characterToSet));
			}
		}
		for(Taxon taxonToSet : taxaToSet) {
			newValues.put(taxonToSet, new HashMap<Character, Value>());
			for(Character characterToSet : charactersToSet) {
				newValues.get(taxonToSet).put(characterToSet, new Value(value));
			}
		}
		eventBus.fireEvent(new SetValueEvent(taxaToSet, charactersToSet, oldValues, newValues));
	}

	private Tree<Taxon, Taxon> createTree() {
		final Tree<Taxon, Taxon> tree = new Tree<Taxon, Taxon>(store, new IdentityValueProvider<Taxon>());	
		tree.setIconProvider(new TaxonIconProvider());
		tree.getSelectionModel().addSelectionHandler(new SelectionHandler<Taxon>() {
			@Override
			public void onSelection(SelectionEvent<Taxon> event) {
				Taxon selection = event.getSelectedItem();
				//tree.getSelectionModel().select(store.getChildren(selection), true);	
				updateTextArea(selection);
			}
		});
		
		tree.setContextMenu(createTreeContextMenu(tree));
		tree.setCell(new AbstractCell<Taxon>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,	Taxon taxon, SafeHtmlBuilder sb) {
					String colorHex = "";
					if(model.hasColor(taxon))
						colorHex = model.getColor(taxon).getHex();
					sb.append(SafeHtmlUtils.fromTrustedString("<div style='background-color:#" + colorHex + "'>" + 
							taxon.getFullName() + "</div>"));
			}
		});
		return tree;
	}
	
	private Menu createTreeContextMenu(final Tree<Taxon, Taxon> tree) {
		final Menu menu = new Menu();
		
		menu.add(new HeaderMenuItem("Taxon"));
		MenuItem item = new MenuItem();
		item.setText("Add");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showTaxonAdd();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Modify");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showTaxonModify();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Remove");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showTaxonRemove();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Move up");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showTaxonUp();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Move down");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showTaxonDown();
			}
		});
		menu.add(item);
		
		final HeaderMenuItem stateHeaderMenuItem = new HeaderMenuItem("State");
		menu.add(stateHeaderMenuItem);
		
		final MenuItem setStateMenuItem = new MenuItem();
		setStateMenuItem.setText("Set");
		Menu setMenu = new Menu();
		setStateMenuItem.setSubMenu(setMenu);
		
		final InputElementVisibleTextField valueField = new InputElementVisibleTextField();
		setMenu.add(new HeaderMenuItem("Value"));
		setMenu.add(valueField);
		TextButton setValueButton = new TextButton("Save");
		setMenu.add(new AdapterMenuItem(setValueButton));
		setValueButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				setValue(valueField.getText());
				menu.hide();
			}
		});
		setMenu.add(new HeaderMenuItem("Annotation"));
		menu.add(setStateMenuItem);
		item = new MenuItem();
		item.setText("Comment");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
				final List<Character> characters = manageMatrixView.getSelectedCharacters();
				final List<Taxon> taxa = manageMatrixView.getSelectedTaxa();
				if(characters.size() == 1 && taxa.size() == 1) {
					Value value = model.getTaxonMatrix().getValue(taxa.get(0), characters.get(0));
					box.getTextArea().setValue(model.hasComment(value) ? model.getComment(value) : "");
				}
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						for(Taxon taxon : taxa) {
							for(Character character : characters) {
								Value value = model.getTaxonMatrix().getValue(taxon, character);
								eventBus.fireEvent(new SetValueCommentEvent(value, box.getValue()));
								String comment = Format.ellipse(box.getValue(), 80);
								String message = Format.substitute("'{0}' saved", new Params(comment));
								Info.display("Comment", message);
							}
						}
					}
				});
				box.show();
			}
		});
		setMenu.add(item);
		final MenuItem colorizeItem = new MenuItem();
		colorizeItem.setText("Colorize");
		final Menu colorMenu = new Menu();
		colorizeItem.setSubMenu(colorMenu);
		setMenu.add(colorizeItem);
		
		menu.addBeforeShowHandler(new BeforeShowHandler() {
			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				colorizeItem.setVisible(!model.getColors().isEmpty());
				colorMenu.clear();
				
				MenuItem offItem = new MenuItem("None");
				offItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						final List<Character> characters = manageMatrixView.getSelectedCharacters();
						final List<Taxon> taxa = manageMatrixView.getSelectedTaxa();
						for(Taxon taxon : taxa) {
							for(Character character : characters) {
								Value value = model.getTaxonMatrix().getValue(taxon, character);
								eventBus.fireEvent(new SetValueColorEvent(value, null));
							}
						}
					}
				});
				colorMenu.add(offItem);
				
				for(final Color color : model.getColors()) {
					MenuItem colorItem = new MenuItem(color.getUse());
					colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
					colorItem.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							final List<Character> characters = manageMatrixView.getSelectedCharacters();
							final List<Taxon> taxa = manageMatrixView.getSelectedTaxa();
							for(Taxon taxon : taxa) {
								for(Character character : characters) {
									Value value = model.getTaxonMatrix().getValue(taxon, character);
									eventBus.fireEvent(new SetValueColorEvent(value, color));
								}
							}
						}
					});
					colorMenu.add(colorItem);
				}
				
				if(manageMatrixView.getCurrentSelectedMatrixEntry() == null) {
					setStateMenuItem.setVisible(false);
					stateHeaderMenuItem.setVisible(false);
				} else {
					List<Character> characters = manageMatrixView.getSelectedCharacters();
					List<Taxon> taxa = manageMatrixView.getSelectedTaxa();
					if(characters.size() == 1 && taxa.size() == 1) {
						Value value = model.getTaxonMatrix().getValue(taxa.get(0), characters.get(0));
						valueField.setText(model.getTaxonMatrix().getValue(taxa.get(0), characters.get(0)).getValue());
						if(model.hasColor(value)) {
							valueField.getInputEl().getStyle().setBackgroundImage("none");
							valueField.getInputEl().getStyle().setBackgroundColor("#" + model.getColor(value).getHex());
						} else {
							valueField.getInputEl().getStyle().setBackgroundImage("none");
							valueField.getInputEl().getStyle().setBackgroundColor("");
						}
					} else {
							valueField.setText("");
							valueField.getInputEl().getStyle().setBackgroundImage("none");
							valueField.getInputEl().getStyle().setBackgroundColor("");
					}
					setStateMenuItem.setVisible(true);
					stateHeaderMenuItem.setVisible(true);
				}
			}
		});
		
		menu.add(new HeaderMenuItem("View"));
		item = new MenuItem();
		item.setText("Expand All");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				tree.expandAll();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Collapse All");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				tree.collapseAll();
			}
		});
		menu.add(item);
		
		menu.add(new HeaderMenuItem("Annotation"));
		item = new MenuItem();
		item.setText("Comment");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final List<Taxon> taxa = getSelectedTaxa();
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");

				if(taxa.size() == 1)
					box.getTextArea().setValue(model.hasComment(taxa.get(0)) ? model.getComment(taxa.get(0)) : "");
				else 
					box.getTextArea().setValue("");
				
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						for(Taxon taxon : taxa) { 
							eventBus.fireEvent(new SetTaxonCommentEvent(taxon, box.getValue()));
							store.update(taxon);
						}
						String comment = Format.ellipse(box.getValue(), 80);
						String message = Format.substitute("'{0}' saved", new Params(comment));
						Info.display("Comment", message);
					}
				});
				box.show();
			}
		});
		menu.add(item);
			
		menu.addBeforeShowHandler(new BeforeShowHandler() {
			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				boolean foundColorize = false;
				for(int i=0; i< menu.getWidgetCount(); i++) {
					Widget widget = menu.getWidget(i);
					if(widget instanceof MenuItem) {
						MenuItem item = (MenuItem)widget;
						if(item.getText().equals("Colorize")) {
							if(!model.getColors().isEmpty()) {
								//refresh colors, they may have changed since last show
								item.setSubMenu(createColorizeMenu());
								foundColorize = true;
							} else {
								menu.remove(widget);
							}
						}
					}
				}
				if(!foundColorize && !model.getColors().isEmpty()) {
					MenuItem item = new MenuItem();
					item.setText("Colorize");
					item.setSubMenu(createColorizeMenu());
					menu.add(item);
				}
			}
		});
		
		menu.add(new HeaderMenuItem("Analysis"));
		item = new MenuItem("Show Description");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Taxon selected = tree.getSelectionModel().getSelectedItem();
				eventBus.fireEvent(new ShowDescriptionEvent(selected));
			}
		});
		menu.add(item); 
		item = new MenuItem("Search Images");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Taxon selected = tree.getSelectionModel().getSelectedItem();
				String fullName = selected.getFullName();
				Window.open("http://www.google.com/search?tbm=isch&q=" + fullName, "_blank", "");
			}
		});
		menu.add(item);
		
		return menu;
	}
	
	/*protected void updateValueField() {
		MatrixEntry matrixEntry = getCurrentSelectedValue();
		if(matrixEntry != null) {
			String labeText = "Value of " + matrixEntry.getCharacter() + " of " + matrixEntry.getTaxon().getFullName() + ": ";
			//setValueFieldLabel.setVisible(true);
			//setValueFieldLabel.setText(labeText);
			//setValueFieldLabel.setTitle(labeText);
			valueLabel.setText(labeText);
			valueLabel.setTitle(labeText);
			setValueField.setVisible(true);
			valueLabel.setVisible(true);
			setValueButton.setVisible(true);
			Value value = matrixEntry.getValue();
			setValueField.setText(value.getValue());
			if(model.hasColor(value)) {
				setValueField.getInputEl().getStyle().setBackgroundImage("none");
				setValueField.getInputEl().getStyle().setBackgroundColor("#" + model.getColor(value).getHex());
			} else {
				setValueField.getInputEl().getStyle().setBackgroundImage("none");
				setValueField.getInputEl().getStyle().setBackgroundColor("");
			}
		} else {
			//setValueFieldLabel.setVisible(false);
			//setValueField.setVisible(false);
			//valueLabel.setVisible(false);
			//setValueButton.setVisible(false);
			//setValueField.setText("");
			setValueField.getInputEl().getStyle().setBackgroundImage("none");
			setValueField.getInputEl().getStyle().setBackgroundColor("");
		}
	}*/
	
	protected Menu createColorizeMenu() {
		Menu colorMenu = new Menu();
		MenuItem offItem = new MenuItem("None");
		offItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final List<Taxon> taxa = getSelectedTaxa();
				for(Taxon taxon : taxa) {
					eventBus.fireEvent(new SetTaxonColorEvent(taxon, null));
					store.update(taxon);
				}
			}
		});
		colorMenu.add(offItem);
		for(final Color color : model.getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					final List<Taxon> taxa = getSelectedTaxa();
					for(Taxon taxon : taxa) {
						eventBus.fireEvent(new SetTaxonColorEvent(taxon, color));
						store.update(taxon);
					}
				}
			});
			colorMenu.add(colorItem);
		}
		return colorMenu;
	}

	protected void loadModel() {
		store.clear();
		for(Taxon rootTaxon : model.getTaxonMatrix().getHierarchyRootTaxa()) {
			store.add(rootTaxon);
			addToStoreRecursively(store, rootTaxon);
		}
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				tree.expandAll();
			}
		});
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
					anchestor.getRank().name() + " " + 
					anchestor.getName() + " " + 
					anchestor.getAuthor() + " " + 
					anchestor.getYear() + 
					"</p>";
		}
					
		String infoText = "<p><b>Rank:&nbsp;</b>" + taxon.getRank().name() + "</p>" +
				"<p><b>Name:&nbsp;</b>" + taxon.getName() + "</p>" +
				"<p><b>Author:&nbsp;</b>" + taxon.getAuthor() + "</p>" +
				"<p><b>Year:&nbsp;</b>" + taxon.getYear() + "</p>" +
				"<p><b>Taxonomy:&nbsp;</b>" + taxonomy + "</p>" +
				"<p><b>Description:&nbsp;</b>" + taxon.getDescription().replaceAll("\n", "</br>") + "</p>";
		if(model.hasComment(taxon))
			infoText +=	"<p><b>Comment:&nbsp;</b>" + model.getComment(taxon) + "</p>";
		if(model.hasColor(taxon))
			infoText += "<p><b>Color:&nbsp;</b>" + model.getColor(taxon).getUse() + "</p>";
		
		infoHtml.setHTML(SafeHtmlUtils.fromSafeConstant(infoText));
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
	
	public void addSelectionChangeHandler(SelectionChangedHandler<Taxon> handler) {
		this.selectionChangeHandlers.add(handler);
	}
	
	public void removeSelectionChangeHandler(SelectionChangedHandler<Taxon> handler) {
		this.selectionChangeHandlers.remove(handler);
	}

}
