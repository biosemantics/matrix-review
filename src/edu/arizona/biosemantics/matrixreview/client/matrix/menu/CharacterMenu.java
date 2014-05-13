package edu.arizona.biosemantics.matrixreview.client.matrix.menu;

import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeMode;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.form.AllowFreeTextComboBoxCell;
import edu.arizona.biosemantics.matrixreview.client.matrix.form.ResetOldValueComboBoxCell;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.AllAccessListStore;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class CharacterMenu extends Menu {
	
	public static class CharacterInformationContainer extends SimpleContainer {
	
		private ComboBox<Organ> organComboBox;
		private TextField characterNameField;

		public CharacterInformationContainer(TaxonMatrix taxonMatrix, String initialName, Organ initialOrgan) {
			FieldSet fieldSet = new FieldSet();
		    fieldSet.setHeadingText("Character Information");
		    fieldSet.setCollapsible(true);
		    this.add(fieldSet, new MarginData(10));
		 
		    VerticalLayoutContainer p = new VerticalLayoutContainer();
		    fieldSet.add(p);
		    
		    OrganProperties organProperties = GWT.create(OrganProperties.class);
		    AllAccessListStore<Organ> store = new AllAccessListStore<Organ>(organProperties.key());
		    Set<Organ> organs = taxonMatrix.getOrgans();
		    store.addAll(organs);
		    organComboBox = new ComboBox<Organ>(new AllowFreeTextComboBoxCell<Organ>(store, organProperties.nameLabel()));
		    organComboBox.setAllowBlank(true);
		    organComboBox.setForceSelection(false);
		    organComboBox.setTriggerAction(TriggerAction.ALL);
		    organComboBox.setValue(initialOrgan);
		    p.add(new FieldLabel(organComboBox, "Organ"), new VerticalLayoutData(1, -1));
		 
		    characterNameField = new TextField();
		    characterNameField.setText(initialName);
		    characterNameField.setAllowBlank(false);
		    p.add(new FieldLabel(characterNameField, "Character Name"), new VerticalLayoutData(1, -1));
		}

		public ComboBox<Organ> getOrganComboBox() {
			return organComboBox;
		}

		public TextField getCharacterNameField() {
			return characterNameField;
		}
		
	}
	
	public static class CharacterAddDialog extends Dialog {
		
		public CharacterAddDialog(final EventBus eventBus, TaxonMatrix taxonMatrix) {
			this.setHeadingText("Add Character");
			CharacterInformationContainer characterInformationContainer = new CharacterInformationContainer(taxonMatrix, "", null);
		    this.add(characterInformationContainer);
		 
		    final ComboBox<Organ> organComboBox = characterInformationContainer.getOrganComboBox();
		    final TextField characterNameField = characterInformationContainer.getCharacterNameField();
		    
		    getButtonBar().clear();
		    TextButton add = new TextButton("Add");
		    add.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					Organ selected = organComboBox.getValue();
					if(organComboBox.getValue() == null)
						selected = new Organ(organComboBox.getText());
					Character newCharacter = new Character(characterNameField.getText(), selected);
					eventBus.fireEvent(new AddCharacterEvent(newCharacter));
					CharacterAddDialog.this.hide();
				}
		    });
		    TextButton cancel =  new TextButton("Cancel");
		    cancel.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					CharacterAddDialog.this.hide();
				}
		    });
		    addButton(add);
		    addButton(cancel);
		}

	}

	
	public static class CharacterModifyDialog extends Dialog {
			
		public CharacterModifyDialog(final EventBus eventBus, TaxonMatrix taxonMatrix, final Character character) {
			this.setHeadingText("Modify Character");	
			CharacterInformationContainer characterInformationContainer = new CharacterInformationContainer(taxonMatrix, character.getName(), character.getOrgan());
		    this.add(characterInformationContainer);
		 
		    final ComboBox<Organ> organComboBox = characterInformationContainer.getOrganComboBox();
		    final TextField characterNameField = characterInformationContainer.getCharacterNameField();
		    
		    getButtonBar().clear();
		    TextButton save = new TextButton("Save");
		    save.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					Organ selected = organComboBox.getValue();
					if(organComboBox.getValue() == null)
						selected = new Organ(organComboBox.getText());
					eventBus.fireEvent(new ModifyCharacterEvent(character, characterNameField.getText(), selected));
					CharacterModifyDialog.this.hide();
				}
		    });
		    TextButton cancel =  new TextButton("Cancel");
		    cancel.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					CharacterModifyDialog.this.hide();
				}
		    });
		    addButton(save);
		    addButton(cancel);
		}
	
	}
	
	private ColumnHeaderAppearance columnHeaderAppearance = GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class);
	//private CharacterColumnConfig columnConfig;
	//private int characterCount;
	private TaxonMatrix taxonMatrix;
	private Character character;
	private EventBus eventBus;
	
	public CharacterMenu(final EventBus eventBus, TaxonMatrix taxonMatrix, Character character) {
		super();
		this.eventBus = eventBus;
		this.taxonMatrix = taxonMatrix;
		this.character = character;
		//this.columnConfig = columnModel.getColumn(colIndex);
		//this.characterCount = columnModel.getColumnCount();
		//this.character = columnConfig.getCharacter();
		
		add(new HeaderMenuItem("Character"));
		add(createAddCharacter());
		add(createDeleteCharacter());
		add(createModifyCharacter());
		add(createMergeCharacters());
		add(createMoveCharacter());
		add(createLockCharacter());
		add(new HeaderMenuItem("State"));
		add(createControlMode());
		add(new HeaderMenuItem("View"));
		add(createSortAsc());
		add(createSortDesc());
		add(new HeaderMenuItem("Annotation"));
		add(createComment());
		add(createColorize());
		add(new HeaderMenuItem("Analysis"));
		add(createAnalysis());
	}
	
	@Override
	public void add(Widget child) {
		if(child != null)
			super.add(child);
	}

	private MenuItem createAnalysis() {
		MenuItem item = new MenuItem("Start");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new AnalyzeCharacterEvent(character));
			}
		});
		return item;
	}

	private MenuItem createColorize() {
		if(taxonMatrix.getColors().isEmpty())
			return null;
		
		MenuItem item = new MenuItem("Colorize");
		Menu colorMenu = new Menu();
		item.setSubMenu(colorMenu);
		MenuItem offItem = new MenuItem("None");
		offItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SetCharacterColorEvent(character, null));
			}
		});
		colorMenu.add(offItem);
		for(final Color color : character.getTaxonMatrix().getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new SetCharacterColorEvent(character, color));
				}
			});
			colorMenu.add(colorItem);
		}
		return item;
	}

	private MenuItem createComment() {
		MenuItem item = new MenuItem("Comment");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox(
						"Comment", "");
				box.setValue(character.getComment());
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						eventBus.fireEvent(new SetCharacterCommentEvent(character, box.getValue()));
						String comment = Format.ellipse(box.getValue(), 80);
						String message = Format.substitute("'{0}' saved",
								new Params(comment));
						Info.display("Comment", message);
					}
				});
				box.show();
			}
		});
		return item;
	}

	private MenuItem createSortDesc() {
		MenuItem item = new MenuItem();
		item.setText(DefaultMessages.getMessages()
				.gridView_sortDescText());
		item.setIcon(columnHeaderAppearance.sortDescendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortTaxaByCharacterEvent(character, SortDir.DESC));
			}
		});
		return item;
	}

	private MenuItem createSortAsc() {
		MenuItem item = new MenuItem();
		item.setText(DefaultMessages.getMessages()
				.gridView_sortAscText());
		item.setIcon(columnHeaderAppearance.sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortTaxaByCharacterEvent(character, SortDir.ASC));
			}
		});
		return item;
	}

	private MenuItem createControlMode() {
		final MenuItem controlItem = new MenuItem("Control input");
		final Menu controlSelectMenu = new Menu();			
		controlItem.setSubMenu(controlSelectMenu);
		final MenuItem automatic = new MenuItem("Automatic");
		//automatic.setGroup("Controlled");
		controlSelectMenu.add(automatic);
		final CheckMenuItem numerical = new CheckMenuItem("Numerical");
		numerical.setGroup("Controlled");
		controlSelectMenu.add(numerical);
		final CheckMenuItem categorical = new CheckMenuItem("Categorical");
		categorical.setGroup("Controlled");
		controlSelectMenu.add(categorical);
		final CheckMenuItem off = new CheckMenuItem("Off");
		off.setGroup("Controlled");
		controlSelectMenu.add(off);
		off.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!character.getControlMode().equals(ControlMode.OFF)) {
					eventBus.fireEvent(new SetControlModeEvent(character, ControlMode.OFF));
				}
			}
		});
		automatic.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ControlMode controlMode = character.determineControlMode();
				eventBus.fireEvent(new SetControlModeEvent(character, controlMode));
			}
		});
		numerical.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!character.getControlMode().equals(ControlMode.NUMERICAL)) {
					eventBus.fireEvent(new SetControlModeEvent(character, ControlMode.NUMERICAL));
				}
			}
		});
		categorical.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!character.getControlMode().equals(ControlMode.CATEGORICAL)) {
					eventBus.fireEvent(new SetControlModeEvent(character, ControlMode.CATEGORICAL));
				}
			}
		});
		switch(character.getControlMode()) {
		case CATEGORICAL:
			categorical.setChecked(true);
			break;
		case NUMERICAL:
			numerical.setChecked(true);
			break;
		case OFF:
			off.setChecked(true);
			break;
		}
		return controlItem;
	}

	private Widget createLockCharacter() {
		final CheckMenuItem lockItem = new CheckMenuItem("Lock");
		lockItem.setChecked(character.isLocked());
		lockItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean newValue = !character.isLocked();
				lockItem.setChecked(newValue);
				eventBus.fireEvent(new LockCharacterEvent(character, newValue));
			}
		});
		return lockItem;
	}

	private MenuItem createMoveCharacter() {
		if(taxonMatrix.getCharacterCount() <= 1)
			return null;
		
		MenuItem item = new MenuItem("Move after");
		Menu moveMenu = new Menu();
		item.setSubMenu(moveMenu);

		MenuItem subItem = new MenuItem("start");
		subItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new MoveCharacterEvent(character, null));
			}
		});
		moveMenu.add(subItem);

		for (final Character after : taxonMatrix.getCharacters()) {
			if (!after.equals(character)) {
				subItem = new MenuItem(after.toString());
				subItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new MoveCharacterEvent(character, after));
					}
				});
				moveMenu.add(subItem);
			}
		}
		return item;
	}

	private MenuItem createMergeCharacters() {
		if(taxonMatrix.getCharacterCount() <= 1)
			return null;
		
		MenuItem item = new MenuItem("Merge with");
		Menu targetMenu = new Menu();
		item.setSubMenu(targetMenu);
		for (final Character target : taxonMatrix.getCharacters()) {
			if(!target.equals(character)) {
				MenuItem targetItem = new MenuItem(target.toString());
				Menu modeMenu = new Menu();
				targetItem.setSubMenu(modeMenu);
				for(final MergeMode mergeMode : MergeMode.values()) {
					MenuItem modeItem = new MenuItem();
					switch(mergeMode) {
					case A_OVER_B:
						modeItem.setText("Priority: " + character.toString());
						break;
					case B_OVER_A:
						modeItem.setText("Priority: " + target.toString());
						break;
					case MIX:
						modeItem.setText("Mix: " + character.toString() + " ; " + target.toString());
						break;
					}
					modeMenu.add(modeItem);
					modeItem.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new MergeCharactersEvent(character, target, mergeMode));
						}
					});
				}

				targetMenu.add(targetItem);
			}
		}
		return item;
	}

	private MenuItem createDeleteCharacter() {
		MenuItem item = new MenuItem();
		item.setText("Delete");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new RemoveCharacterEvent(character));
			}
		});
		return item;
	}
	
	private MenuItem createModifyCharacter() {
		MenuItem item = new MenuItem();
		item.setText("Modify");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				CharacterModifyDialog modifyDialog = new CharacterModifyDialog(eventBus, taxonMatrix, character);
				modifyDialog.show();
			}
		});
		return item;
	}

	private MenuItem createAddCharacter() {
		MenuItem item = new MenuItem();
		item.setText("Add");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				CharacterAddDialog addDialog = new CharacterAddDialog(eventBus, taxonMatrix);
				addDialog.show();
			}
		});
		return item;
	}	
}
