package edu.arizona.biosemantics.matrixreview.client;

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
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
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

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent.SetCharacterStatesEventHandler;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharacterColumnConfig;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.CharacterMenu.CharacterAddDialog;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.CharacterMenu.CharacterModifyDialog;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.CharacterMenu.SelectCharacterStatesWindow;
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

		FieldSet charactersFieldSet = new FieldSet();
		// taxonFieldSet.setCollapsible(true);
		charactersFieldSet.setHeadingText("Characters");
		charactersFieldSet.setWidget(tree);

		FieldSet infoFieldSet = new FieldSet();
		// taxonFieldSet.setCollapsible(true);
		infoFieldSet.setHeadingText("Character Details");
		infoFieldSet.setWidget(infoHtml);

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

		ListStore<ControlMode> store = new ListStore<ControlMode>(
				controlModeProperties.key());
		for (ControlMode mode : ControlMode.values())
			store.add(mode);
		controlCombo = new ComboBox<ControlMode>(store,
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

		/*
		 * TextButton controlButton = new TextButton("Control Mode");
		 * controlButton.addSelectHandler(new SelectHandler() {
		 * 
		 * @Override public void onSelect(SelectEvent event) {
		 * OrganCharacterNode selected =
		 * tree.getSelectionModel().getSelectedItem(); if(selected instanceof
		 * CharacterNode) {
		 * 
		 * } } }); charactersButtonBar.add(addButton);
		 */

		charactersButtonBar.add(addButton);
		charactersButtonBar.add(modifyButton);
		charactersButtonBar.add(removeButton);
		charactersButtonBar.add(controlCombo);
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

}
