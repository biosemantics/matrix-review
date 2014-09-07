package edu.arizona.biosemantics.matrixreview.client.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
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
import com.sencha.gxt.widget.core.client.event.FocusEvent;
import com.sencha.gxt.widget.core.client.event.FocusEvent.FocusHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.matrixreview.client.common.CharacterAddDialog;
import edu.arizona.biosemantics.matrixreview.client.common.CharacterModifyDialog;
import edu.arizona.biosemantics.matrixreview.client.common.SelectCharacterStatesWindow;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersDownEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharactersUpEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansDownEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveOrgansUpEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaDownEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaUpEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent.SetCharacterStatesEventHandler;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharacterColumnConfig;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlModeProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.ValueProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.CharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.OrganNode;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.*;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class ManageCharactersView extends ContentPanel {

	private ValueProperties valueProperties = GWT.create(ValueProperties.class);
	private OrganCharacterNodeProperties organCharacterNodeProperties = new OrganCharacterNodeProperties();
	private ControlModeProperties controlModeProperties = new ControlModeProperties();
	private EventBus eventBus;
	private HTML infoHtml = new HTML();
	private SimpleContainer valuesView = new SimpleContainer();
	private SimpleContainer categoricalValuesView = new SimpleContainer();
	private Tree<OrganCharacterNode, String> tree;
	private TaxonMatrix matrix;
	private TreeStore<OrganCharacterNode> store = new TreeStore<OrganCharacterNode>(
			organCharacterNodeProperties.key());
	private ComboBox<ControlMode> controlCombo;
	private HashMap<Organ, OrganNode> organNodes;
	private HashMap<Character, CharacterNode> characterNodes;

	public ManageCharactersView(EventBus eventBus, boolean navigation) {
		this.eventBus = eventBus;
		this.tree = createTree(matrix);
		
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
		eventBus.addHandler(LoadTaxonMatrixEvent.TYPE,
				new LoadTaxonMatrixEvent.LoadTaxonMatrixEventHandler() {
					@Override
					public void onLoad(LoadTaxonMatrixEvent event) {
						matrix = event.getTaxonMatrix();
						loadMatrix();
					}
				});
		eventBus.addHandler(AddCharacterEvent.TYPE,
				new AddCharacterEvent.AddCharacterEventHandler() {
					@Override
					public void onAdd(AddCharacterEvent event) {
						addCharacter(event.getOrgan(), event.getAfter(),
								event.getCharacter());
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
						modifyCharacter(event.getOldCharacter(),
								event.getNewName(), event.getNewOrgan());
					}
				});
		eventBus.addHandler(ModifyOrganEvent.TYPE,
				new ModifyOrganEvent.ModifyOrganEventHandler() {
					
					@Override
					public void onModify(ModifyOrganEvent event) {
						modifyOrgan(event.getOldOrgan(), event.getNewName());
					}
				});
		eventBus.addHandler(MoveCharactersDownEvent.TYPE, new MoveCharactersDownEvent.MoveCharacterDownEventHandler() {
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

	protected void modifyCharacter(Character oldCharacter, String newName,
			Organ newOrgan) {
		matrix.modifyCharacter(oldCharacter, newName, newOrgan);
		CharacterNode toUpdate = characterNodes.get(oldCharacter);
		OrganNode newOrganNode = null;
		if (!organNodes.containsKey(newOrgan)) {
			newOrganNode = createOrganNode(newOrgan);
			store.add(newOrganNode);
		} else {
			newOrganNode = organNodes.get(newOrgan);
		}
		store.remove(toUpdate);
		store.add(newOrganNode, toUpdate);
		store.update(toUpdate);

		OrganCharacterNode parent = store.getParent(toUpdate);
		if (store.getChildCount(parent) == 0)
			store.remove(parent);
	}
	
	protected void modifyOrgan(Organ oldOrgan, String newName) {
		store.update(organNodes.get(oldOrgan));
	}

	protected void addCharacter(Organ organ, Character after,
			Character character) {
		if (organ != null) {
			CharacterNode characterNode = createCharacterNode(character);
			OrganNode parent = organNodes.get(organ);
			if (parent == null) {
				parent = createOrganNode(organ);
				store.add(parent);
			}
			store.add(parent, characterNode);
		}
	}

	private void removeCharacterNode(Character character) {
		CharacterNode node = characterNodes.remove(character);
		store.remove(node);
	}

	private void removeOrganNode(Organ organ) {
		OrganNode node = organNodes.remove(organ);
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
				OrganCharacterNode selected = tree.getSelectionModel()
						.getSelectedItem();
				Character after = null;
				if (selected instanceof CharacterNode)
					after = ((CharacterNode) selected).getCharacter();
				Organ organ = null;
				if (selected instanceof OrganNode)
					organ = ((OrganNode) selected).getOrgan();
				CharacterAddDialog addDialog = new CharacterAddDialog(eventBus,
						matrix, organ);
				addDialog.setAfter(after);
				addDialog.show();
			}
		});

		TextButton modifyButton = new TextButton("Modify");
		modifyButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				OrganCharacterNode selected = tree.getSelectionModel()
						.getSelectedItem();
				if (selected instanceof CharacterNode) {
					Character character = ((CharacterNode) selected)
							.getCharacter();
					CharacterModifyDialog modifyDialog = new CharacterModifyDialog(
							eventBus, matrix, character);
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
							eventBus.fireEvent(new ModifyOrganEvent(organ, box.getValue()));
						}
					});
					box.show();
				}
			}
		});

		TextButton removeButton = new TextButton("Remove");
		removeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				List<OrganCharacterNode> selected = tree.getSelectionModel()
						.getSelectedItems();
				List<Character> toRemove = new LinkedList<Character>();
				for (OrganCharacterNode node : selected) {
					if (node instanceof CharacterNode) {
						toRemove.add(((CharacterNode) node).getCharacter());
					}
					if (node instanceof OrganNode) {
						toRemove.addAll(((OrganNode) node).getOrgan()
								.getCharacters());
					}
				}
				eventBus.fireEvent(new RemoveCharacterEvent(toRemove));
			}
		});

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
				final OrganCharacterNode selected = tree.getSelectionModel()
						.getSelectedItem();
				if (selected instanceof CharacterNode) {
					final Character character = ((CharacterNode) selected).getCharacter();
					
					if(event.getSelectedItem().equals(ControlMode.CATEGORICAL)) {
						List<String> sortValues = getCharacterValues(character);
						SelectCharacterStatesWindow window = new SelectCharacterStatesWindow(character, sortValues);
						window.show();
						window.addSetCharacterStatesEventHandler(new SetCharacterStatesEventHandler() {
							@Override
							public void onSet(SetCharacterStatesEvent event) {		
								eventBus.fireEvent(new SetControlModeEvent(character, ControlMode.CATEGORICAL, event.getStates()));
								updateControlMode(selected);
								//updateCategoricalValuesList(character);
							}
						});
					} else { 
						eventBus.fireEvent(new SetControlModeEvent(character, event.getSelectedItem()));
						updateControlMode(selected);
						//updateCategoricalValuesList(character);
					}
				}
			}
		});
		
		TextButton upButton = new TextButton("Move Up");
		upButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
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
		});
		TextButton downButton = new TextButton("Move Down");
		downButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
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
		});

	
		TextButton analyzeButton = new TextButton("Analyze");
		analyzeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				List<OrganCharacterNode> selection = tree.getSelectionModel().getSelectedItems();
				for(OrganCharacterNode node : selection) {
					if(node instanceof CharacterNode)
						eventBus.fireEvent(new AnalyzeCharacterEvent(((CharacterNode)node).getCharacter()));
					if(node instanceof OrganNode)
						for(OrganCharacterNode characterNode : store.getChildren(node))
							eventBus.fireEvent(new AnalyzeCharacterEvent(((CharacterNode)characterNode).getCharacter()));
				}
			}
		});		 

		charactersButtonBar.add(addButton);
		charactersButtonBar.add(modifyButton);
		charactersButtonBar.add(removeButton);
		charactersButtonBar.add(upButton);
		charactersButtonBar.add(downButton);
		charactersButtonBar.add(new Label("Control Mode"));
		charactersButtonBar.add(controlCombo);
		charactersButtonBar.add(analyzeButton);	
		return charactersButtonBar;
	}

	protected List<String> getCharacterValues(Character character) {
		final Set<String> values = new HashSet<String>();
		for (Taxon taxon : matrix.list()) {
			String value = taxon.get(character).getValue();
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

	private Tree<OrganCharacterNode, String> createTree(TaxonMatrix matrix) {
		Tree<OrganCharacterNode, String> tree = new Tree<OrganCharacterNode, String>(
				store, organCharacterNodeProperties.name());
		tree.getSelectionModel().addSelectionHandler(
				new SelectionHandler<OrganCharacterNode>() {
					@Override
					public void onSelection(
							SelectionEvent<OrganCharacterNode> event) {
						OrganCharacterNode selection = event.getSelectedItem();
						updateInfo(selection);
						updateControlMode(selection);
					}
				});
		return tree;
	}

	protected void loadMatrix() {
		organNodes = new HashMap<Organ, OrganNode>();
		characterNodes = new HashMap<Character, CharacterNode>();

		List<OrganCharacterNode> organCharacterNodes = new LinkedList<OrganCharacterNode>();
		for (Organ organ : matrix.getOrgans()) {
			organCharacterNodes.add(createOrganNode(organ));
			for (Character character : organ.getCharacters()) {
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

				ListView<String, String> statesList = new ListView<String, String>(
						valuesStore, new IdentityValueProvider<String>());
				valuesView.setWidget(statesList);
			} else
				valuesView.clear();
		}
	}
	
	private void updateCategoricalValuesList(Character character) {
		List<String> sortValues = character.getStates();
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
					+ character.getControlMode().name() + "</p>";

			if (character.getControlMode().equals(ControlMode.CATEGORICAL)) {
				String states = "";
				for (String state : character.getStates()) {
					states += state + ", ";
				}
				states = states.substring(0, states.length() - 2);
				info += "<p><b>States:&nbsp;</b>" + states + "</p>";
			}

			infoHtml.setHTML(SafeHtmlUtils.fromSafeConstant(info));
		}
		if (organCharacterNode instanceof OrganNode) {
			Organ organ = ((OrganNode) organCharacterNode).getOrgan();

			String characters = "";
			for (Character character : organ.getCharacters())
				characters += character.getName() + ", ";
			characters = characters.substring(0, characters.length() - 2);

			String info = "<p><b>Organ Name:&nbsp;</b>" + organ.getName()
					+ "</p>" + "<p><b>Characters:&nbsp;</b>" + characters
					+ "</p>";

			infoHtml.setHTML(SafeHtmlUtils.fromSafeConstant(info));

			valuesView.clear();
		}
	}

	protected void updateControlMode(OrganCharacterNode organCharacterNode) {
		if (organCharacterNode instanceof CharacterNode) {
			Character character = ((CharacterNode) organCharacterNode)
					.getCharacter();
			controlCombo.setValue(character.getControlMode());
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
				result.addAll(((OrganNode) node).getOrgan().getCharacters());
		}
		return result;
	}

	public List<Organ> getSelectedOrgans() {
		List<Organ> result = new LinkedList<Organ>();
		for (OrganCharacterNode node : tree.getSelectionModel().getSelectedItems()) {
			if(node instanceof OrganNode) {
				result.add(((OrganNode)node).getOrgan());
			}
		}
		return result;
	}

}
