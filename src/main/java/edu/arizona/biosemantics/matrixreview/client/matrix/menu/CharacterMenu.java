package edu.arizona.biosemantics.matrixreview.client.matrix.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.CharacterAddDialog;
import edu.arizona.biosemantics.matrixreview.client.common.CharacterModifyDialog;
import edu.arizona.biosemantics.matrixreview.client.common.SelectCharacterStatesDialog;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeMode;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent.SetCharacterStatesEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharacterColumnConfig;
import edu.arizona.biosemantics.matrixreview.client.matrix.FrozenFirstColumTaxonTreeGrid.CharactersGrid;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.ControlMode;

public class CharacterMenu extends Menu {
	
	private ColumnHeaderAppearance columnHeaderAppearance = GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class);
	//private CharacterColumnConfig columnConfig;
	//private int characterCount;
	private Model model;
	private Character character;
	private EventBus eventBus;
	private CharactersGrid grid;
	
	public CharacterMenu(final EventBus eventBus, CharactersGrid grid, Model model, Character character) {
		super();
		this.eventBus = eventBus;
		this.grid = grid;
		this.model = model;
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
		add(createSubAnalysis());
		add(createFullAnalysis());
	}
	
	@Override
	public void add(Widget child) {
		if(child != null)
			super.add(child);
	}

	private MenuItem createFullAnalysis() {
		MenuItem item = new MenuItem("All Taxa/Characters");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new AnalyzeCharacterEvent(character));
			}
		});
		return item;
	}
	
	private MenuItem createSubAnalysis() {
		MenuItem item = new MenuItem("Selected Taxa/Characters");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new AnalyzeCharacterEvent(character, new ArrayList<Taxon>(model.getTaxonMatrix().getTaxa())));
			}
		});
		return item;
	}

	private MenuItem createColorize() {
		if(!model.hasColors())
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
		for(final Color color : model.getColors()) {
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
				box.getTextArea().setValue(model.getComment(character));
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
		final MenuItem controlItem = new MenuItem("Control State Values");
		final Menu controlSelectMenu = new Menu();			
		controlItem.setSubMenu(controlSelectMenu);
		final CheckMenuItem numerical = new CheckMenuItem("Numerical");
		numerical.setGroup("Controlled");
		controlSelectMenu.add(numerical);
		final CheckMenuItem categorical = new CheckMenuItem("Categorical");
		categorical.setGroup("Controlled");
		controlSelectMenu.add(categorical);
		final MenuItem automatic = new MenuItem("Automatic");
		//automatic.setGroup("Controlled");
		controlSelectMenu.add(automatic);
		final CheckMenuItem off = new CheckMenuItem("Off");
		off.setGroup("Controlled");
		controlSelectMenu.add(off);
		off.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!model.getControlMode(character).equals(ControlMode.OFF)) {
					eventBus.fireEvent(new SetControlModeEvent(character, ControlMode.OFF));
				}
			}
		});
		automatic.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final ControlMode controlMode = model.determineControlMode(character);
				
				if(controlMode.equals(ControlMode.CATEGORICAL)) {
					selectStatesAndFire();
				} else
					eventBus.fireEvent(new SetControlModeEvent(character, controlMode));
			}
		});
		numerical.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!model.getControlMode(character).equals(ControlMode.NUMERICAL)) {
					eventBus.fireEvent(new SetControlModeEvent(character, ControlMode.NUMERICAL));
				}
			}
		});
		categorical.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!model.getControlMode(character).equals(ControlMode.CATEGORICAL)) {
					selectStatesAndFire();
				}
			}
		});
		switch(model.getControlMode(character)) {
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

	protected void selectStatesAndFire() {
		final CharacterColumnConfig characterColumnConfig = grid.getCharacterColumnConfig(character);
		final Set<String> values = new HashSet<String>();
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			String value = characterColumnConfig.getValueProvider().getValue(taxon).getValue();
			if(!value.trim().isEmpty()) 
				values.add(value);
		}
		List<String> sortValues = new ArrayList<String>(values);
		Collections.sort(sortValues, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		SelectCharacterStatesDialog window = new SelectCharacterStatesDialog(character, sortValues);
		window.show();
		window.addSetCharacterStatesEventHandler(new SetCharacterStatesEventHandler() {
			@Override
			public void onSet(SetCharacterStatesEvent event) {		
				eventBus.fireEvent(new SetControlModeEvent(character, ControlMode.CATEGORICAL, event.getStates()));
			}
		});
	}

	private Widget createLockCharacter() {
		final CheckMenuItem lockItem = new CheckMenuItem("Lock");
		lockItem.setChecked(model.isLocked(character));
		lockItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean newValue = !model.isLocked(character);
				lockItem.setChecked(newValue);
				eventBus.fireEvent(new LockCharacterEvent(character, newValue));
			}
		});
		return lockItem;
	}

	private MenuItem createMoveCharacter() {
		/*if(model.getTaxonMatrix().getCharacterCount() <= 1)
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

		for (final Character after : model.getTaxonMatrix().getVisibleFlatCharacters()) {
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
		return item;*/
		return null;
	}

	private MenuItem createMergeCharacters() {
		if(model.getTaxonMatrix().getCharacterCount() <= 1)
			return null;
		
		MenuItem item = new MenuItem("Merge with");
		Menu targetMenu = new Menu();
		item.setSubMenu(targetMenu);
		for (final Character target : model.getTaxonMatrix().getVisibleFlatCharacters()) {
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
							ConfirmMessageBox box = new ConfirmMessageBox(
									"Control Mode",
									"Merging will turn off the control mode of the new column");
							box.addDialogHideHandler(new DialogHideHandler() {
								@Override
								public void onDialogHide(DialogHideEvent event) {
									if(event.getHideButton().equals(PredefinedButton.YES)) {
										eventBus.fireEvent(new MergeCharactersEvent(character, target, mergeMode));
									}
								}
								
							});
							box.show();
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
				CharacterModifyDialog modifyDialog = new CharacterModifyDialog(eventBus, model, character);
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
				CharacterAddDialog addDialog = new CharacterAddDialog(eventBus, model, null);
				addDialog.setAfter(character);
				addDialog.show();
			}
		});
		return item;
	}	
}
