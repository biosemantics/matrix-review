package edu.arizona.biosemantics.matrixreview.client.config;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.AddEvent;
import com.sencha.gxt.widget.core.client.event.AddEvent.AddHandler;
import com.sencha.gxt.widget.core.client.event.BeforeAddEvent;
import com.sencha.gxt.widget.core.client.event.BeforeAddEvent.BeforeAddHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.ShowContextMenuEvent.ShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.matrixreview.client.ModelMerger;
import edu.arizona.biosemantics.matrixreview.client.common.CategoricalValidator;
import edu.arizona.biosemantics.matrixreview.client.common.NumericalValidator;
import edu.arizona.biosemantics.matrixreview.client.common.Validator;
import edu.arizona.biosemantics.matrixreview.client.common.Validator.ValidationResult;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowMatrixEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.CharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.OrganNode;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class ManageMatrixView extends VerticalLayoutContainer {

	private class InputElementVisibleTextField extends TextField {
		public XElement getInputEl() {
			return super.getInputEl();
		}
	}
	
	private EventBus fullModelBus;
	private Model model;
	private ModelMerger modelMerger;
	private ManageTaxaView taxaView;
	private ManageCharactersView charactersView;
	protected List<OrganCharacterNode> characterSelection = new LinkedList<OrganCharacterNode>();
	protected List<Taxon> taxaSelection = new LinkedList<Taxon>();
	private InputElementVisibleTextField setValueField;

	public ManageMatrixView(final EventBus fullModelBus, final EventBus subModelBus) {
		this.fullModelBus = fullModelBus;
		modelMerger = new ModelMerger(fullModelBus, subModelBus);
		
		taxaView = new ManageTaxaView(fullModelBus, false);
		charactersView = new ManageCharactersView(fullModelBus, false);
		
		HorizontalLayoutContainer horizontalLayoutContainer = new HorizontalLayoutContainer();
		horizontalLayoutContainer.add(taxaView, new HorizontalLayoutData(0.5, 1.0));
		horizontalLayoutContainer.add(charactersView, new HorizontalLayoutData(0.5, 1.0));
		add(horizontalLayoutContainer, new VerticalLayoutData(1.0, 1.0));
		
		
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setMinButtonWidth(75);
		buttonBar.setPack(BoxLayoutPack.CENTER);
		//taxaButtonBar.setVisible(false);
		TextButton loadButton = new TextButton("Load");
		loadButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final List<Taxon> taxa = taxaView.getSelectedTaxa();
				final List<Character> characters = charactersView.getSelectedCharacters();
				final LinkedHashSet<Organ> organs = charactersView.getSelectedOrgans();
				if(!taxa.isEmpty() && !characters.isEmpty()) {
					 ConfirmMessageBox box = new ConfirmMessageBox("Loading Matrix", getLoadMessage(taxa, characters, organs));
					 box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							fullModelBus.fireEvent(new ShowMatrixEvent(taxa, characters));
						}
					 });
			         box.show();
				} else {
					AlertMessageBox alert = new AlertMessageBox("Load impossible", "You have to select at least one taxon and character");
					alert.show();
				}
			}
		});
		
		TextButton analyzeButton = new TextButton("Analyze");
		analyzeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				List<Taxon> taxaSelection = taxaView.getSelectedTaxa();
				List<Character> characterSelection = charactersView.getSelectedCharacters(); 
				
				Model subModel = null;
				if(!taxaSelection.isEmpty()) 
					subModel = modelMerger.getSubModel(characterSelection, taxaSelection);
				else
					subModel = modelMerger.getFullModel();
				
				for(Character character : characterSelection)
					fullModelBus.fireEvent(new AnalyzeCharacterEvent(character, subModel));
			}
		});	
		

		setValueField = new InputElementVisibleTextField();
		setValueField.setContextMenu(createSetValueFieldMenu());
		TextButton setValueButton = new TextButton("Set Value");
		setValueButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final List<Taxon> taxa = taxaView.getSelectedTaxa();
				final List<Character> characters = charactersView.getSelectedCharacters();
				
				
				String value = setValueField.getValue();
				for(Taxon taxon : taxa) {
					for(Character character : characters) {
						ValidationResult validationResult = validValue(value, taxon, character);
						if(validationResult.isValid()) {
							fullModelBus.fireEvent(new SetValueEvent(taxon, character, model.getTaxonMatrix().getValue(taxon, character), 
									new Value(value)));
						} else {
							AlertMessageBox alert = new AlertMessageBox("Set value failed", "Can't set value " +
									value + " for " + character.getName() + " of " +  taxon.getFullName() + ". Control mode " + 
									model.getControlMode(character).toString().toLowerCase() + " was selected for " + character.getName());
							alert.show();
						}
					}
				}
				
				/*final PromptMessageBox box = new PromptMessageBox("Set Value", 
						"Please enter new value:");
				if(taxa.size() == 1 && characters.size() == 1) 
					box.getTextField().setText(model.getTaxonMatrix().getValue(taxa.get(0), characters.get(0)).getValue());
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						String value = box.getValue();
						for(Taxon taxon : taxa) {
							for(Character character : characters) {
								ValidationResult validationResult = validValue(value, taxon, character);
								if(validationResult.isValid()) {
									fullModelBus.fireEvent(new SetValueEvent(taxon, character, model.getTaxonMatrix().getValue(taxon, character), 
											new Value(value)));
								} else {
									AlertMessageBox alert = new AlertMessageBox("Set value failed", "Can't set value " +
											value + " for " + character.getName() + " of " +  taxon.getFullName() + ". Control mode " + 
											model.getControlMode(character).toString().toLowerCase() + " was selected for " + character.getName());
									alert.show();
								}
							}
						}
					}
				});
				box.show();*/
			}
		});
		
		buttonBar.add(new Label("Value:"));
		buttonBar.add(setValueField);
		buttonBar.add(setValueButton);
		buttonBar.add(analyzeButton);
		buttonBar.add(loadButton);
		
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeadingText("Selection");
		contentPanel.add(buttonBar);
		add(contentPanel, new VerticalLayoutData(1.0, -1.0));
		
		bindEvents();
	}

	private Menu createSetValueFieldMenu() {
		final Menu menu = new Menu();
		menu.addBeforeShowHandler(new BeforeShowHandler() {
			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				final Value value = getCurrentSelectedValue();
				if(value == null) 
					event.setCancelled(true);
			}
		});
				
		MenuItem item = new MenuItem();
		item.setText("Comment");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
				final Value value = getCurrentSelectedValue();
				box.getTextArea().setValue(model.hasComment(value) ? model.getComment(value) : "");
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
 						fullModelBus.fireEvent(new SetValueCommentEvent(value, box.getValue()));
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

	protected Menu createColorizeMenu() {
		Menu colorMenu = new Menu();
		MenuItem offItem = new MenuItem("None");
		offItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final Value value = getCurrentSelectedValue();
				fullModelBus.fireEvent(new SetValueColorEvent(value, null));
				updateValueField();
			}
		});
		colorMenu.add(offItem);
		for(final Color color : model.getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					final Value value = getCurrentSelectedValue();
					fullModelBus.fireEvent(new SetValueColorEvent(value, color));
					updateValueField();
				}
			});
			colorMenu.add(colorItem);
		}
		return colorMenu;
	}

	protected ValidationResult validValue(String value, Taxon taxon, Character character) {
		switch(model.getControlMode(character)) {
		case CATEGORICAL:
			Set<String> states = new HashSet<String>(model.getStates(character));
			Validator validator = new CategoricalValidator(states);
			ValidationResult result = validator.validate(value);
			return result;
		case NUMERICAL:
			validator = new NumericalValidator();
			result = validator.validate(value);
			return result;
		case OFF:
			break;
		default:
			break;
		}
		return new ValidationResult(true, "");
	}

	private void bindEvents() {
		fullModelBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
			@Override
			public void onLoad(LoadModelEvent event) {
				model = event.getModel();
			}
		});
		charactersView.addSelectionChangeHandler(new SelectionChangedHandler<OrganCharacterNode>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<OrganCharacterNode> event) {
				characterSelection = event.getSelection();
				updateValueField();
			}
		});
		taxaView.addSelectionChangeHandler(new SelectionChangedHandler<Taxon>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<Taxon> event) {
				taxaSelection = event.getSelection();
				updateValueField();
			}
		});
	}

	protected void updateValueField() {
		Value value = getCurrentSelectedValue();
		if(value != null) {
			setValueField.setText(value.getValue());
			if(model.hasColor(value)) {
				setValueField.getInputEl().getStyle().setBackgroundImage("none");
				setValueField.getInputEl().getStyle().setBackgroundColor("#" + model.getColor(value).getHex());
			} else {
				setValueField.getInputEl().getStyle().setBackgroundImage("none");
				setValueField.getInputEl().getStyle().setBackgroundColor("");
			}
		} else {
			setValueField.setText("");
			setValueField.getInputEl().getStyle().setBackgroundImage("none");
			setValueField.getInputEl().getStyle().setBackgroundColor("");
		}
	}

	private Value getCurrentSelectedValue() {
		if(taxaSelection.size() >= 1 && characterSelection.size() >= 1) {
			Taxon lastSelectedTaxon = taxaSelection.get(taxaSelection.size() - 1);
			OrganCharacterNode lastSelectedOrganCharacterNode = characterSelection.get(characterSelection.size() - 1);
			if(lastSelectedOrganCharacterNode instanceof CharacterNode) {
				Character character = ((CharacterNode)lastSelectedOrganCharacterNode).getCharacter();
				Value value = model.getTaxonMatrix().getValue(lastSelectedTaxon, character);
				return value;
			}
		}
		return null;
	}

	protected String getLoadMessage(List<Taxon> taxa, List<Character> characters, LinkedHashSet<Organ> organs) {
		return "Do you want to continue loading a sub-matrix with the following dimensions?</br></br>" +
				"<table><tr><td><p><b>Selected Taxa: </b></td><td>" + taxa.size() + "</p></td></tr>" +
		"<tr><td><p><b>Selected Characters: </b></td><td>" + characters.size() + " (of " + organs.size() + " organ(s))</p></tr></td>" +
		"<tr><td><p><b>Matrix values: </b></td><td>" + taxa.size() * characters.size() + "</p></tr></td>";
	}
		
}
