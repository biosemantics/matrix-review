package edu.arizona.biosemantics.matrixreview.client.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.AdapterMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.matrixreview.client.common.CharacterAddDialog;
import edu.arizona.biosemantics.matrixreview.client.common.CharacterModifyDialog;
import edu.arizona.biosemantics.matrixreview.client.common.InputElementVisibleTextField;
import edu.arizona.biosemantics.matrixreview.client.common.MergeDialog;
import edu.arizona.biosemantics.matrixreview.client.common.SelectCharacterStatesDialog;
import edu.arizona.biosemantics.matrixreview.client.common.SetValueValidator;
import edu.arizona.biosemantics.matrixreview.client.common.SetValueValidator.ValidationResult;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersDownEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersUpEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansDownEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansUpEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent.SetCharacterStatesEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.ControlModeProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.MatrixEntry;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.*;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class ManageCharactersView extends ContentPanel {

	private OrganCharacterNodeProperties organCharacterNodeProperties = new OrganCharacterNodeProperties();
	private ControlModeProperties controlModeProperties = new ControlModeProperties();
	private EventBus eventBus;
	private HTML infoHtml = new HTML();
	private SimpleContainer valuesView = new SimpleContainer();
	private SimpleContainer categoricalValuesView = new SimpleContainer();
	private Tree<OrganCharacterNode, OrganCharacterNode> tree;
	private Model model;
	private TreeStore<OrganCharacterNode> store = new TreeStore<OrganCharacterNode>(
			organCharacterNodeProperties.key());
	private ComboBox<ControlMode> controlCombo;
	private HashMap<Organ, OrganNode> organNodes;
	private HashMap<Character, CharacterNode> characterNodes;
	private Set<SelectionChangedHandler<OrganCharacterNode>> selectionChangeHandlers = 
			new HashSet<SelectionChangedHandler<OrganCharacterNode>>();
	private ComboBox<ControlMode> controlComboBar;
	private ManageMatrixView manageMatrixView;
	private ListView<String, String> statesList;


	public ManageCharactersView(EventBus eventBus, boolean navigation, ManageMatrixView manageMatrixView) {
		this.eventBus = eventBus;
		this.manageMatrixView = manageMatrixView;
		this.tree = createTree();
		
		this.setTitle("Character Management");
		this.setHeadingText("Character Management");

		FieldSet charactersFieldSet = new FieldSet();
		// taxonFieldSet.setCollapsible(true);
		charactersFieldSet.setHeadingText("Characters");
		charactersFieldSet.setWidget(tree);

		FieldSet infoFieldSet = new FieldSet();
		// taxonFieldSet.setCollapsible(true);
		infoFieldSet.setHeadingText("Character Details");
		FlowLayoutContainer flowInfoHtml = new FlowLayoutContainer();
		flowInfoHtml.add(infoHtml);
		flowInfoHtml.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		infoFieldSet.setWidget(flowInfoHtml);

		FieldSet valuesFieldSet = new FieldSet();
		// taxonFieldSet.setCollapsible(true);
		valuesFieldSet.setHeadingText("Character Values");
		valuesFieldSet.setWidget(valuesView);
		
		HorizontalLayoutContainer horizontalLayoutContainer = new HorizontalLayoutContainer();
		horizontalLayoutContainer.add(charactersFieldSet,
				new HorizontalLayoutData(0.5, 1.0));
		VerticalLayoutContainer infoContainer = new VerticalLayoutContainer();
		infoContainer.add(infoFieldSet, new VerticalLayoutData(1.0, 0.5));
		HorizontalLayoutContainer valuesContainer = new HorizontalLayoutContainer();
		valuesContainer.add(valuesFieldSet, new HorizontalLayoutData(0.5, 1.0));
		valuesContainer.add(categoricalValuesView, new HorizontalLayoutData(0.5, 1.0));
		infoContainer.add(valuesContainer, new VerticalLayoutData(1.0, 0.5));
		horizontalLayoutContainer.add(infoContainer, new HorizontalLayoutData(
				0.5, 1.0));

		VerticalLayoutContainer vertical = new VerticalLayoutContainer();
		vertical.add(horizontalLayoutContainer,
				new VerticalLayoutData(1.0, 1.0));
		vertical.add(createCharactersButtonBar(), new VerticalLayoutData());
		this.setWidget(vertical);

		if (navigation) {
			TextButton nextButton = new TextButton("Next");
			nextButton.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					ManageCharactersView.this.hide();
				}
			});
			this.addButton(nextButton);
		}

		bindEvents();
	}

	private void bindEvents() {
		eventBus.addHandler(LoadModelEvent.TYPE,
				new LoadModelEvent.LoadModelEventHandler() {
					@Override
					public void onLoad(LoadModelEvent event) {
						model = event.getModel();
						loadModel();
					}
				});
		eventBus.addHandler(AddCharacterEvent.TYPE,
				new AddCharacterEvent.AddCharacterEventHandler() {
					@Override
					public void onAdd(AddCharacterEvent event) {
						addCharacter(event.getOrgan(), event.getCharacter(), event.getAddAfterCharacter());
					}
				});
		eventBus.addHandler(RemoveCharacterEvent.TYPE,
				new RemoveCharacterEvent.RemoveCharacterEventHandler() {
					@Override
					public void onRemove(RemoveCharacterEvent event) {
						removeCharacter(event.getCharacters());
					}
				});
		eventBus.addHandler(ModifyCharacterEvent.TYPE,
				new ModifyCharacterEvent.ModifyCharacterEventHandler() {
					@Override
					public void onModify(ModifyCharacterEvent event) {
						modifyCharacter(event.getOldCharacter(), event.getOldName(),
								event.getNewName(), event.getOldOrgan(), event.getNewOrgan());
					}
				});
		eventBus.addHandler(ModifyOrganEvent.TYPE, new ModifyOrganEvent.ModifyOrganEventHandler() {
					@Override
					public void onModify(ModifyOrganEvent event) {
						modifyOrgan(event.getOldOrgan(), event.getOldName(), event.getNewName());
					}
				});
		eventBus.addHandler(MoveCharactersDownEvent.TYPE, new MoveCharactersDownEvent.MoveCharactersDownEventHandler() {
			@Override
			public void onMove(MoveCharactersDownEvent event) {
				move(getCharacterNodes(event.getCharacters()), false);
			}
		});
		eventBus.addHandler(MoveCharactersUpEvent.TYPE, new MoveCharactersUpEvent.MoveCharactersUpEventHandler() {
			@Override
			public void onMove(MoveCharactersUpEvent event) {
				move(getCharacterNodes(event.getCharacters()), true);
			}
		});
		eventBus.addHandler(MoveOrgansDownEvent.TYPE, new MoveOrgansDownEvent.MoveOrgansDownEventHandler() {
			@Override
			public void onMove(MoveOrgansDownEvent event) {
				move(getOrganNodes(event.getOrgans()), false);
			}
		});
		eventBus.addHandler(MoveOrgansUpEvent.TYPE, new MoveOrgansUpEvent.MoveOrgansUpEventHandler() {
			@Override
			public void onMove(MoveOrgansUpEvent event) {
				move(getOrganNodes(event.getOrgans()), true);
			}
		});
		eventBus.addHandler(MergeCharactersEvent.TYPE, new MergeCharactersEvent.MergeCharactersEventHandler() {
			@Override
			public void onMerge(MergeCharactersEvent event) {
				removeCharacterNode(event.getTarget());
				
				Organ organ = event.getCharacter().getOrgan();
				if(!organNodes.containsKey(organ)) {
					OrganNode node = createOrganNode(organ);
					store.add(node);
					store.remove(characterNodes.get(event.getCharacter()));
					store.add(node, characterNodes.get(event.getCharacter()));				
				} else {
					store.update(characterNodes.get(event.getCharacter()));
				}	
			}
		});
		eventBus.addHandler(SetCharacterColorEvent.TYPE, new SetCharacterColorEvent.SetCharacterColorEventHandler() {
			@Override
			public void onSet(SetCharacterColorEvent event) {
				store.update(characterNodes.get(event.getCharacter()));
			}
		});
		eventBus.addHandler(SetCharacterCommentEvent.TYPE, new SetCharacterCommentEvent.SetCharacterCommentEventHandler() {
			@Override
			public void onSet(SetCharacterCommentEvent event) {
				store.update(characterNodes.get(event.getCharacter()));
			}
		});
		
		tree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<OrganCharacterNode>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<OrganCharacterNode> event) {
				for(SelectionChangedHandler<OrganCharacterNode> handler :  selectionChangeHandlers) {
					handler.onSelectionChanged(new SelectionChangedEvent<OrganCharacterNode>(event.getSelection()));
					//handler.onSelectionChanged(new SelectionChangedEvent<Character>(getSelectedCharacters()));
				}
			}
		});
	}
	
	protected List<OrganCharacterNode> getOrganNodes(List<Organ> organs) {
		List<OrganCharacterNode> result = new LinkedList<OrganCharacterNode>();
		for(Organ organ : organs) {
			result.add(organNodes.get(organ));
		}
		return result;
	}

	protected List<OrganCharacterNode> getCharacterNodes(List<Character> characters) {
		List<OrganCharacterNode> result = new LinkedList<OrganCharacterNode>();
		for(Character character : characters) {
			result.add(characterNodes.get(character));
		}
		return result;
	}

	protected void move(List<OrganCharacterNode> organCharacterNodes, boolean up) {
		Map<OrganCharacterNode, Set<OrganCharacterNode>> parentChildrenToMove = new HashMap<OrganCharacterNode, Set<OrganCharacterNode>>();
		for(OrganCharacterNode node : organCharacterNodes) {
			OrganCharacterNode parent = store.getParent(node);
			if(!parentChildrenToMove.containsKey(parent)) {
				parentChildrenToMove.put(parent, new HashSet<OrganCharacterNode>());
			}
			parentChildrenToMove.get(parent).add(node);
		}
		
		for(final OrganCharacterNode parent : parentChildrenToMove.keySet()) {
			Set<OrganCharacterNode> toMove = parentChildrenToMove.get(parent);
			
			List<OrganCharacterNode> storeChildren = null;
			if(parent == null)
				storeChildren = store.getRootItems();
			else
				storeChildren = store.getChildren(parent);
			final List<OrganCharacterNode> newStoreChildren = new LinkedList<OrganCharacterNode>(storeChildren);
			
			if(storeChildren.size() > 1) {
				if(up) {
					for(int i=1; i<newStoreChildren.size(); i++) {
						OrganCharacterNode previousStoreChild = newStoreChildren.get(i-1);
						OrganCharacterNode storeChild = newStoreChildren.get(i);
						if(toMove.contains(storeChild) && !toMove.contains(previousStoreChild)) {
							Collections.swap(newStoreChildren, i, i-1);
						}
					}
				} else {
					for(int i=newStoreChildren.size() - 2; i>=0; i--) {
						OrganCharacterNode nextStoreChild = newStoreChildren.get(i+1);
						OrganCharacterNode storeChild = newStoreChildren.get(i);
						if(toMove.contains(storeChild) && !toMove.contains(nextStoreChild)) {
							Collections.swap(newStoreChildren, i, i+1);
						}
					}
				}
	
				List<TreeNode<OrganCharacterNode>> subtrees = new LinkedList<TreeNode<OrganCharacterNode>>();
				for(OrganCharacterNode storeChild : newStoreChildren) {
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

	protected void removeCharacter(Collection<Character> characters) {
		for (Character character : characters) {
			removeCharacter(character);
		}
	}

	protected void removeCharacter(Character character) {
		OrganNode organNode = organNodes.get(character.getOrgan());
		removeCharacterNode(character);
		if (store.getChildCount(organNode) == 0)
			removeOrganNode(character.getOrgan());
	}

	protected void modifyCharacter(Character character, String oldName, String newName, Organ oldOrgan, Organ newOrgan) {
		character.setName(newName);
		CharacterNode toUpdate = characterNodes.get(character);
		OrganCharacterNode oldParent = store.getParent(toUpdate);
		
		if (!organNodes.containsKey(newOrgan)) {
			OrganNode newOrganNode = createOrganNode(newOrgan);
			store.add(newOrganNode);
			store.remove(toUpdate);
			store.add(newOrganNode, toUpdate);
		} else {
			OrganNode newOrganNode = organNodes.get(newOrgan);
			int oldIndex = store.indexOf(toUpdate);
			store.remove(toUpdate);
			store.insert(newOrganNode, oldIndex, toUpdate);
			store.update(toUpdate);
		}

		OrganCharacterNode parent = store.getParent(toUpdate);
		if (store.getChildCount(parent) == 0)
			removeOrganNode(((OrganNode)parent).getOrgan());
		if(store.getChildCount(oldParent) == 0) {
			removeOrganNode(((OrganNode)oldParent).getOrgan());
		}
	}
	
	protected void modifyOrgan(Organ oldOrgan, String oldName, String newName) {
		store.update(organNodes.get(oldOrgan));
	}

	protected void addCharacter(Organ organ, Character character, Character addAfterCharacter) {
		if (organ != null) {
			CharacterNode characterNode = createCharacterNode(character);
			OrganNode parent = organNodes.get(organ);
			if (parent == null) {
				parent = createOrganNode(organ);
				store.add(parent);
			}
			
			if(characterNodes.containsKey(addAfterCharacter)) {
				CharacterNode addAfterNode = characterNodes.get(addAfterCharacter);
				if(store.getChildren(parent).contains(addAfterNode)) {
					store.insert(parent, store.getChildren(parent).indexOf(addAfterNode) + 1, characterNode);
					return;
				}
			} 
			
			store.add(parent, characterNode);
		}
	}

	private void removeCharacterNode(Character character) {
		CharacterNode node = characterNodes.remove(character);
		if(node != null)
			store.remove(node);
	}

	private void removeOrganNode(Organ organ) {
		OrganNode node = organNodes.remove(organ);
		if(node != null)
			store.remove(node);
	}

	private CharacterNode createCharacterNode(Character character) {
		CharacterNode result = new CharacterNode(character);
		characterNodes.put(character, result);
		return result;
	}

	private OrganNode createOrganNode(Organ organ) {
		OrganNode result = new OrganNode(organ);
		organNodes.put(organ, result);
		return result;
	}

	private IsWidget createCharactersButtonBar() {
		ButtonBar charactersButtonBar = new ButtonBar();
		charactersButtonBar.setMinButtonWidth(75);
		charactersButtonBar.setPack(BoxLayoutPack.END);
		// taxaButtonBar.setVisible(false);
		TextButton addButton = new TextButton("Add");
		addButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showCharacterAdd();
			}
		});

		TextButton modifyButton = new TextButton("Modify");
		modifyButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showCharacterModify();
			}
		});

		TextButton removeButton = new TextButton("Remove");
		removeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showCharacterRemove();
			}
		});

		ListStore<ControlMode> controlModeStore = new ListStore<ControlMode>(
				controlModeProperties.key());
		for (ControlMode mode : ControlMode.values())
			controlModeStore.add(mode);
		controlComboBar = new ComboBox<ControlMode>(controlModeStore,
				controlModeProperties.name());
		controlComboBar.setForceSelection(true);
		controlComboBar.setTriggerAction(TriggerAction.ALL);
		controlComboBar.addSelectionHandler(new SelectionHandler<ControlMode>() {
			@Override
			public void onSelection(SelectionEvent<ControlMode> event) {
				showControlState(event.getSelectedItem());
			}
		});
		
		TextButton upButton = new TextButton("Move Up");
		upButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showCharacterUp();
			}
		});
		TextButton downButton = new TextButton("Move Down");
		downButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showCharacterDown();
			}
		});	 
		
		TextButton mergeButton = new TextButton("Merge");
		mergeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				showCharacterMerge();
			}
		});	 

		//charactersButtonBar.add(addButton);
		//charactersButtonBar.add(modifyButton);
		//charactersButtonBar.add(removeButton);
		charactersButtonBar.add(upButton);
		charactersButtonBar.add(downButton);
		//charactersButtonBar.add(new Label("Control Mode"));
		//charactersButtonBar.add(controlCombo);
		//charactersButtonBar.add(mergeButton);
		return charactersButtonBar;
	}

	protected void showCharacterMerge() {
		List<OrganCharacterNode> selected = tree.getSelectionModel().getSelectedItems();
		List<Character> characters = new LinkedList<Character>();
		for(OrganCharacterNode node : selected) {
			if(node instanceof CharacterNode) {
				characters.add(((CharacterNode)node).getCharacter());
			}
		}
		List<Organ> organs = new LinkedList<Organ>();
		for(OrganCharacterNode node : selected) {
			if(node instanceof OrganNode) {
				characters.addAll(((OrganNode)node).getOrgan().getCharacters());
			}
		}
		if(characters.size() >= 2) {
			MergeDialog mergeDialog = new MergeDialog(eventBus, model, characters.get(0), characters.subList(1, characters.size()));
			mergeDialog.show();
		} else {
			AlertMessageBox box = new AlertMessageBox("Character selection", "You have to select at least two charaters to merge");
			box.show();
		}
	}

	protected void showCharacterDown() {
		List<OrganCharacterNode> selected = tree.getSelectionModel().getSelectedItems();
		List<Character> characters = new LinkedList<Character>();
		for(OrganCharacterNode node : selected) {
			if(node instanceof CharacterNode) {
				characters.add(((CharacterNode)node).getCharacter());
			}
		}
		List<Organ> organs = new LinkedList<Organ>();
		for(OrganCharacterNode node : selected) {
			if(node instanceof OrganNode) {
				organs.add(((OrganNode)node).getOrgan());
			}
		}
		eventBus.fireEvent(new MoveCharactersDownEvent(characters));
		eventBus.fireEvent(new MoveOrgansDownEvent(organs));
		tree.getSelectionModel().setSelection(selected);
	}

	protected void showCharacterUp() {
		List<OrganCharacterNode> selected = tree.getSelectionModel().getSelectedItems();
		List<Character> characters = new LinkedList<Character>();
		for(OrganCharacterNode node : selected) {
			if(node instanceof CharacterNode) {
				characters.add(((CharacterNode)node).getCharacter());
			}
		}
		List<Organ> organs = new LinkedList<Organ>();
		for(OrganCharacterNode node : selected) {
			if(node instanceof OrganNode) {
				organs.add(((OrganNode)node).getOrgan());
			}
		}
		eventBus.fireEvent(new MoveCharactersUpEvent(characters));
		eventBus.fireEvent(new MoveOrgansUpEvent(organs));
		tree.getSelectionModel().setSelection(selected);
	}

	protected void showControlState(ControlMode controlMode) {
		final OrganCharacterNode selected = tree.getSelectionModel()
				.getSelectedItem();
		if (selected instanceof CharacterNode) {
			final Character character = ((CharacterNode) selected).getCharacter();
			
			if(controlMode.equals(ControlMode.CATEGORICAL)) {
				List<String> sortValues = getCharacterValues(character);
				SelectCharacterStatesDialog window = new SelectCharacterStatesDialog(character, sortValues);
				window.show();
				window.addSetCharacterStatesEventHandler(new SetCharacterStatesEventHandler() {
					@Override
					public void onSet(SetCharacterStatesEvent event) {		
						eventBus.fireEvent(new SetControlModeEvent(character, ControlMode.CATEGORICAL, event.getStates()));
						updateControlMode(controlCombo, selected);
						updateControlMode(controlComboBar, selected);
						//updateCategoricalValuesList(character);
					}
				});
			} else { 
				eventBus.fireEvent(new SetControlModeEvent(character, controlMode));
				updateControlMode(controlCombo, selected);
				updateControlMode(controlComboBar, selected);
				//updateCategoricalValuesList(character);
			}
		}
	}

	protected void showCharacterRemove() {
		List<OrganCharacterNode> selected = tree.getSelectionModel()
				.getSelectedItems();
		List<Character> toRemove = new LinkedList<Character>();
		for (OrganCharacterNode node : selected) {
			if (node instanceof CharacterNode) {
				toRemove.add(((CharacterNode) node).getCharacter());
			}
			if (node instanceof OrganNode) {
				toRemove.addAll(((OrganNode) node).getOrgan()
						.getFlatCharacters());
			}
		}
		eventBus.fireEvent(new RemoveCharacterEvent(toRemove));
	}

	protected void showCharacterModify() {
		OrganCharacterNode selected = tree.getSelectionModel()
				.getSelectedItem();
		if (selected instanceof CharacterNode) {
			Character character = ((CharacterNode) selected)
					.getCharacter();
			CharacterModifyDialog modifyDialog = new CharacterModifyDialog(
					eventBus, model, character);
			modifyDialog.show();
		}
		if (selected instanceof OrganNode) {
			final PromptMessageBox box = new PromptMessageBox("Rename Organ", 
					"Please enter new organ name:");
			final Organ organ = ((OrganNode)selected).getOrgan();
			box.getTextField().setText(organ.getName());
			box.addHideHandler(new HideHandler() {
				@Override
				public void onHide(HideEvent event) {
					eventBus.fireEvent(new ModifyOrganEvent(organ, organ.getName(), box.getValue()));
				}
			});
			box.show();
		}
	}

	protected void showCharacterAdd() {
		OrganCharacterNode selected = tree.getSelectionModel()
				.getSelectedItem();
		Character after = null;
		Organ organ = null;
		if (selected instanceof CharacterNode) {
			after = ((CharacterNode) selected).getCharacter();
			organ = ((CharacterNode) selected).getCharacter().getOrgan();
		}
		if (selected instanceof OrganNode)
			organ = ((OrganNode) selected).getOrgan();
		CharacterAddDialog addDialog = new CharacterAddDialog(eventBus,	model, organ);
		addDialog.setAfter(after);
		addDialog.show();
	}

	protected List<String> getCharacterValues(Character character) {
		final Set<String> values = new HashSet<String>();
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			String value = model.getTaxonMatrix().getValue(taxon, character).getValue();
			if (!value.trim().isEmpty())
				values.add(value);
		}
		List<String> sortValues = new ArrayList<String>(values);
		Collections.sort(sortValues, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		return sortValues;
	}

	private Tree<OrganCharacterNode, OrganCharacterNode> createTree() {
		final Tree<OrganCharacterNode, OrganCharacterNode> tree = new Tree<OrganCharacterNode, OrganCharacterNode>(
				store, new IdentityValueProvider<OrganCharacterNode>());
		tree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<OrganCharacterNode>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<OrganCharacterNode> event) {
				//event.get
			}
		});
		tree.getSelectionModel().addSelectionHandler(
				new SelectionHandler<OrganCharacterNode>() {
					@Override
					public void onSelection(SelectionEvent<OrganCharacterNode> event) {
						OrganCharacterNode selection = event.getSelectedItem();
						//if(selection instanceof OrganNode)
						//	tree.getSelectionModel().select(store.getChildren(selection), true);		
						updateInfo(selection);
						updateControlMode(controlCombo, selection);
						updateControlMode(controlComboBar, selection);
					}
				});
		tree.setContextMenu(createTreeContextMenu(tree));
		tree.setCell(new AbstractCell<OrganCharacterNode>() {
			@Override
			public void render(com.google.gwt.cell.client.Cell.Context context,
					OrganCharacterNode value, SafeHtmlBuilder sb) {
				if(value instanceof OrganNode) {
					Organ organ = ((OrganNode)value).getOrgan();
					/*
					boolean allCharactersColoredSame = true;
					if(organ.getCharacters().isEmpty()) 
						allCharactersColoredSame = false;
					else {
						Color color = model.getColor(organ.getFlatCharacters().get(0));
						for(Character character : organ.getCharacters()) {
							if(!model.hasColor(character)) {
								allCharactersColoredSame = false;
								break;
							} else {
								if(!model.getColor(character).equals(color)) {
									allCharactersColoredSame = false;
									break;
								}
							}
						}
						if(allCharactersColoredSame)
						sb.append(SafeHtmlUtils.fromTrustedString("<div style='background-color:#" + color.getHex() + "'>" + organ.getName() + "</div>"));
					}
					if(!allCharactersColoredSame)
						sb.append(SafeHtmlUtils.fromTrustedString("<div style='background-color:#'>" + organ.getName() + "</div>"));*/
					sb.append(SafeHtmlUtils.fromTrustedString("<div style='background-color:#'>" + organ.getName() + "</div>"));
				}
				if(value instanceof CharacterNode) {
					Character character = ((CharacterNode)value).getCharacter();
					String colorHex = "";
					if(model.hasColor(character))
						colorHex = model.getColor(character).getHex();
					sb.append(SafeHtmlUtils.fromTrustedString("<div style='background-color:#" + colorHex + "'>" + 
						character.getName() + "</div>"));
				}
			}
		});
		return tree;
	}

	private Menu createTreeContextMenu(final Tree<OrganCharacterNode, OrganCharacterNode> tree) {
		final Menu menu = new Menu();
		
		menu.add(new HeaderMenuItem("Character"));
		MenuItem item = new MenuItem();
		item.setText("Add");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showCharacterAdd();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Modify");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showCharacterModify();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Remove");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showCharacterRemove();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Move Up");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showCharacterUp();
			}
		});
		menu.add(item);
		
		item = new MenuItem();
		item.setText("Move Up");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				showCharacterDown();
			}
		});
		menu.add(item);
		
		menu.add(new HeaderMenuItem("State"));
		item = new MenuItem();
		item.setText("Control Mode");
		Menu subMenu = new Menu();
		item.setSubMenu(subMenu);
		ListStore<ControlMode> controlModeStore = new ListStore<ControlMode>(
				controlModeProperties.key());
		for (ControlMode mode : ControlMode.values())
			controlModeStore.add(mode);
		controlCombo = new ComboBox<ControlMode>(controlModeStore,
				controlModeProperties.name());
		controlCombo.setForceSelection(true);
		controlCombo.setTriggerAction(TriggerAction.ALL);
		controlCombo.addSelectionHandler(new SelectionHandler<ControlMode>() {
			@Override
			public void onSelection(SelectionEvent<ControlMode> event) {
				showControlState(event.getSelectedItem());
			}
		});
		subMenu.add(controlCombo);
		menu.add(item);
		
				
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
				}
			}
		});
		
		menu.add(new HeaderMenuItem("View"));
		item = new MenuItem();
		item.setText("Expand All");
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
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final List<OrganCharacterNode> selected = tree.getSelectionModel().getSelectedItems();
				final List<Character> characters = getSelectedCharacters();
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
				
				if(characters.size() == 1)
					box.getTextArea().setValue(model.hasComment(characters.get(0)) ? model.getComment(characters.get(0)) : "");
				else 
					box.getTextArea().setValue("");
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						for(Character character : characters) { 
							eventBus.fireEvent(new SetCharacterCommentEvent(character, box.getValue()));
						}
						for(OrganCharacterNode node : selected) {
							store.update(node);
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
		
		return menu;
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
	
	protected Menu createColorizeMenu() {
		Menu colorMenu = new Menu();
		MenuItem offItem = new MenuItem("None");
		offItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final Set<OrganCharacterNode> selected = getSelectedOrganCharacterNodesIncludingOrgan();
				final List<Character> characters = getSelectedCharacters();
				for(Character character : characters) {
					eventBus.fireEvent(new SetCharacterColorEvent(character, null));
				}
				for(OrganCharacterNode node : selected) {
					store.update(node);
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
					final Set<OrganCharacterNode> selected = getSelectedOrganCharacterNodesIncludingOrgan();
					final List<Character> characters = getSelectedCharacters();
					for(Character character : characters)
						eventBus.fireEvent(new SetCharacterColorEvent(character, color));
					for(OrganCharacterNode node : selected) {
						store.update(node);
					}
				}
			});
			colorMenu.add(colorItem);
		}
		return colorMenu;
	}

	protected void loadModel() {
		store.clear();
		organNodes = new HashMap<Organ, OrganNode>();
		characterNodes = new HashMap<Character, CharacterNode>();

		List<OrganCharacterNode> organCharacterNodes = new LinkedList<OrganCharacterNode>();
		for (Organ organ : model.getTaxonMatrix().getHierarchyCharacters()) {
			organCharacterNodes.add(createOrganNode(organ));
			for (Character character : organ.getFlatCharacters()) {
				organCharacterNodes.add(createCharacterNode(character));
			}
		}

		for (OrganCharacterNode organCharacterNode : organCharacterNodes) {
			if (organCharacterNode instanceof OrganNode) {
				OrganNode organNode = (OrganNode) organCharacterNode;
				store.add(organCharacterNode);
				organNodes.put(organNode.getOrgan(), organNode);
			}
		}

		for (OrganCharacterNode organCharacterNode : organCharacterNodes) {
			if (organCharacterNode instanceof CharacterNode) {
				CharacterNode characterNode = (CharacterNode) organCharacterNode;
				characterNodes.put(characterNode.getCharacter(), characterNode);
				store.add(
						organNodes.get(characterNode.getCharacter().getOrgan()),
						characterNode);
			}
		}
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				tree.expandAll();
			}
		});
	}

	protected void updateInfo(OrganCharacterNode organCharacterNode) {
		updateHtml(organCharacterNode);
		updateValuesList(organCharacterNode);
	}

	private void updateValuesList(OrganCharacterNode organCharacterNode) {
		if (organCharacterNode instanceof CharacterNode) {
			Character character = ((CharacterNode) organCharacterNode)
					.getCharacter();

			List<String> sortValues = this.getCharacterValues(character);
			if(!sortValues.isEmpty()) {
				ListStore<String> valuesStore = new ListStore<String>(
						new ModelKeyProvider<String>() {
							@Override
							public String getKey(String item) {
								return item;
							}
						});
				valuesStore.addAll(sortValues);

				statesList = new ListView<String, String>(
						valuesStore, new IdentityValueProvider<String>());
				statesList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
				valuesView.setWidget(statesList);
			} else
				valuesView.clear();
		}
	}
	
	private void updateCategoricalValuesList(Character character) {
		List<String> sortValues = model.getStates(character);
		Collections.sort(sortValues);
		if(!sortValues.isEmpty()) {
			ListStore<String> valuesStore = new ListStore<String>(
					new ModelKeyProvider<String>() {
						@Override
						public String getKey(String item) {
							return item;
						}
					});
			valuesStore.addAll(sortValues);

			ListView<String, String> statesList = new ListView<String, String>(
					valuesStore, new IdentityValueProvider<String>());
			
			FieldSet categoricalValuesFieldSet = new FieldSet();
			// taxonFieldSet.setCollapsible(true);
			categoricalValuesFieldSet.setHeadingText("Categorical Character Values");
			categoricalValuesFieldSet.setWidget(statesList);
			categoricalValuesView.setWidget(categoricalValuesFieldSet);
		}
	}

	private void updateHtml(OrganCharacterNode organCharacterNode) {
		if (organCharacterNode instanceof CharacterNode) {
			Character character = ((CharacterNode) organCharacterNode)
					.getCharacter();

			String info = "<p><b>Name:&nbsp;</b>" + character.getName()
					+ "</p>" + "<p><b>Organ:&nbsp;</b>"
					+ character.getOrgan().getName() + "</p>"
					+ "<p><b>Control Mode:&nbsp;</b>"
					+ model.getControlMode(character).name() + "</p>";

			if (model.getControlMode(character).equals(ControlMode.CATEGORICAL)) {
				String states = "";
				for (String state : model.getStates(character)) {
					states += state + ", ";
				}
				states = states.substring(0, states.length() - 2);
				info += "<p><b>States:&nbsp;</b>" + states + "</p>";
			}
			
			if(model.hasComment(character))
				info +=	"<p><b>Comment:&nbsp;</b>" + model.getComment(character) + "</p>";
			if(model.hasColor(character))
				info += "<p><b>Color:&nbsp;</b>" + model.getColor(character).getUse() + "</p>";

			infoHtml.setHTML(SafeHtmlUtils.fromSafeConstant(info));
		}
		if (organCharacterNode instanceof OrganNode) {
			Organ organ = ((OrganNode) organCharacterNode).getOrgan();

			String characters = "";
			for (Character character : organ.getFlatCharacters())
				characters += character.getName() + ", ";
			if(characters.length() >=2)
				characters = characters.substring(0, characters.length() - 2);

			String info = "<p><b>Organ Name:&nbsp;</b>" + organ.getName()
					+ "</p>" + "<p><b>Characters:&nbsp;</b>" + characters
					+ "</p>";

			infoHtml.setHTML(SafeHtmlUtils.fromSafeConstant(info));

			valuesView.clear();
		}
	}

	protected void updateControlMode(ComboBox<ControlMode> controlCombo, OrganCharacterNode organCharacterNode) {
		if (organCharacterNode instanceof CharacterNode) {
			Character character = ((CharacterNode) organCharacterNode)
					.getCharacter();
			controlCombo.setValue(model.getControlMode(character));
			controlCombo.setEnabled(true);
			if(controlCombo.getValue().equals(ControlMode.CATEGORICAL))
				updateCategoricalValuesList(character);
			else
				categoricalValuesView.clear();
		}
		if (organCharacterNode instanceof OrganNode) {
			controlCombo.setEnabled(false);
			controlCombo.clear();
			categoricalValuesView.clear();
		}
	}

	public List<Character> getSelectedCharacters() {
		List<Character> result = new LinkedList<Character>();
		for (OrganCharacterNode node : tree.getSelectionModel()
				.getSelectedItems()) {
			if (node instanceof CharacterNode)
				result.add(((CharacterNode) node).getCharacter());
			if (node instanceof OrganNode)
				result.addAll(((OrganNode) node).getOrgan().getFlatCharacters());
		}
		return result;
	}

	public LinkedHashSet<Organ> getSelectedOrgans() {
		LinkedHashSet<Organ> result = new LinkedHashSet<Organ>();
		for (OrganCharacterNode node : tree.getSelectionModel().getSelectedItems()) {
			if(node instanceof OrganNode) {
				result.add(((OrganNode)node).getOrgan());
			}
			if(node instanceof CharacterNode) {
				result.add(((OrganNode)store.getParent(node)).getOrgan());
			}
		}
		return result;
	}
	
	private Set<OrganCharacterNode> getSelectedOrganCharacterNodesIncludingOrgan() {
		Set<OrganCharacterNode> result = new HashSet<OrganCharacterNode>();
		for (OrganCharacterNode node : tree.getSelectionModel().getSelectedItems()) {
			if(node instanceof OrganNode) {
				result.add(node);
				result.addAll(store.getChildren(node));
			}
			if (node instanceof CharacterNode) {
				result.add(node);
				result.add(store.getParent(node));
			}
		}
		return result;
	}
	
	public void addSelectionChangeHandler(SelectionChangedHandler<OrganCharacterNode> handler) {
		this.selectionChangeHandlers.add(handler);
	}
	
	public void removeSelectionChangeHandler(SelectionChangedHandler<OrganCharacterNode> handler) {
		this.selectionChangeHandlers.remove(handler);
	}

	public void setMatrixEntry(MatrixEntry entry) {
		List<String> selection = new LinkedList<String>();
		if(entry != null && entry.getValue() != null) 
			selection.add(entry.getValue().getValue());
		statesList.getSelectionModel().setSelection(selection);
	}

}
