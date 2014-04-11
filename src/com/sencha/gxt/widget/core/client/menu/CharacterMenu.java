package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.CharactersGridView;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.info.Info;

import edu.arizona.biosemantics.matrixreview.client.manager.AnnotationManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ControlManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ControlManager.ControlMode;
import edu.arizona.biosemantics.matrixreview.client.manager.DataManager.MergeMode;
import edu.arizona.biosemantics.matrixreview.client.manager.DataManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ViewManager;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class CharacterMenu extends Menu {

	public CharacterMenu(final DataManager dataManager, final ViewManager viewManager, final ControlManager controlManager,
			final AnnotationManager annotationManager, final CharactersGridView charactersGridView, final int colIndex) {
		super();
		int characters = dataManager.getCharacterCount();
		final ColumnModel<Taxon> cm = charactersGridView.getColumnModel();
		
		add(new HeaderMenuItem("Character"));
		MenuItem item = new MenuItem();
		item.setText("Add");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final PromptMessageBox nameBox = new PromptMessageBox(
						"Character Name", "");
				nameBox.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						final PromptMessageBox organBox = new PromptMessageBox(
								"Character Organ", "");
						organBox.addHideHandler(new HideHandler() {
							@Override
							public void onHide(HideEvent event) {
								String name = nameBox.getValue();
								String organ = organBox.getValue();
								dataManager.addCharacterAfter(colIndex, new Character(name, organ));
							}
						});
						organBox.show();
					}
				});
				nameBox.show();
			}
		});
		add(item);
		
		item = new MenuItem();
		item.setText("Delete");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				dataManager.removeCharacter(colIndex);
			}
		});
		add(item);
		
		item = new MenuItem();
		item.setText("Rename");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final Character character = dataManager.getCharacter(colIndex);
				final PromptMessageBox nameBox = new PromptMessageBox(
						"Character Name", "");
				nameBox.setValue(character.getName());
				nameBox.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						final PromptMessageBox organBox = new PromptMessageBox(
								"Character Organ", "");
						organBox.setValue(character.getOrgan());
						organBox.addHideHandler(new HideHandler() {
							@Override
							public void onHide(HideEvent event) {
								String name = nameBox.getValue();
								String organ = organBox.getValue();
								dataManager.renameCharacter(colIndex, name, organ);
							}
						});
						organBox.show();
					}
				});
				nameBox.show();
			}
		});
		add(item);
		
		item = new MenuItem("Merge with");
		add(item);
		Menu targetMenu = new Menu();
		item.setSubMenu(targetMenu);
		for (int i = 0; i < characters; i++) {
			if(i != colIndex) {
				final int theI = i;
				MenuItem targetItem = new MenuItem(cm.getColumnHeader(i).asString());
				
				Menu modeMenu = new Menu();
				targetItem.setSubMenu(modeMenu);
				for(final MergeMode mergeMode : MergeMode.values()) {
					MenuItem modeItem = new MenuItem();
					switch(mergeMode) {
					case A_OVER_B:
						modeItem.setText("Priority: " + cm.getColumnHeader(i).asString());
						break;
					case B_OVER_A:
						modeItem.setText("Priority: " + cm.getColumnHeader(colIndex).asString());
						break;
					case MIX:
						modeItem.setText("Mix: " + cm.getColumnHeader(colIndex).asString() + " ; " + cm.getColumnHeader(i).asString());
						break;
					}
					modeMenu.add(modeItem);
					modeItem.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							dataManager.mergeCharacters(colIndex, theI, mergeMode);
						}
					});
				}

				targetMenu.add(targetItem);
			}
		}
		
		item = new MenuItem("Move after");
		add(item);
		Menu moveMenu = new Menu();
		item.setSubMenu(moveMenu);

		item = new MenuItem("start");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				dataManager.moveCharacter(colIndex, 0);
			}
		});
		moveMenu.add(item);

		for (int i = 0; i < characters; i++) {
			if (i != colIndex) {
				final int theI = i;
				item = new MenuItem(cm.getColumnHeader(i).asString());
				item.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						dataManager.moveCharacter(colIndex, theI + 1);
					}
				});
				moveMenu.add(item);
			}
		}

		final CheckMenuItem lockItem = new CheckMenuItem("Lock");
		lockItem.setChecked(controlManager.isLockedCharacter(colIndex));
		lockItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean newValue = !controlManager.isLockedCharacter(colIndex);
				lockItem.setChecked(newValue);
				controlManager.setLockedCharacter(colIndex, newValue);
			}
		});
		add(lockItem);

		add(new HeaderMenuItem("State"));
		
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
				if(!controlManager.getControlMode(colIndex).equals(ControlMode.OFF)) {
					controlManager.setControlMode(colIndex, ControlMode.OFF);
				}
			}
		});
		automatic.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ControlMode controlMode = controlManager.determineControlMode(colIndex);
				controlManager.setControlMode(colIndex, controlMode);
			}
		});
		numerical.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!controlManager.getControlMode(colIndex).equals(ControlMode.NUMERICAL)) {
					controlManager.setControlMode(colIndex, ControlMode.NUMERICAL);
				}
			}
		});
		categorical.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!controlManager.getControlMode(colIndex).equals(ControlMode.CATEGORICAL)) {
					controlManager.setControlMode(colIndex, ControlMode.CATEGORICAL);
				}
			}
		});
		add(controlItem);
		switch(controlManager.getControlMode(colIndex)) {
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
		
		add(new HeaderMenuItem("View"));
		if (cm.isSortable(colIndex)) {
			item = new MenuItem();
			item.setText(DefaultMessages.getMessages()
					.gridView_sortAscText());
			item.setIcon(charactersGridView.getHeader().getAppearance().sortAscendingIcon());
			item.addSelectionHandler(new SelectionHandler<Item>() {

				@Override
				public void onSelection(SelectionEvent<Item> event) {
					charactersGridView.doSort(colIndex, SortDir.ASC);

				}
			});
			add(item);

			item = new MenuItem();
			item.setText(DefaultMessages.getMessages()
					.gridView_sortDescText());
			item.setIcon(charactersGridView.getHeader().getAppearance().sortDescendingIcon());
			item.addSelectionHandler(new SelectionHandler<Item>() {

				@Override
				public void onSelection(SelectionEvent<Item> event) {
					charactersGridView.doSort(colIndex, SortDir.DESC);

				}
			});
			add(item);
		}

		/*MenuItem columns = new MenuItem();
		columns.setText(DefaultMessages.getMessages()
				.gridView_columnsText());
		columns.setIcon(charactersGridView.getHeader().getAppearance().columnsIcon());
		columns.setData("gxt-columns", "true");

		final Menu columnMenu = new Menu();

		for (int i = 0; i < cols; i++) {
			ColumnConfig<Taxon, ?> config = cm.getColumn(i);
			// ignore columns with no header text
			if (!charactersGridView.hasHeaderValue(i)) {
				continue;
			}
			final int fcol = i;
			final CheckMenuItem check = new CheckMenuItem();
			check.setHideOnClick(false);
			check.setHTML(cm.getColumnHeader(i));
			check.setChecked(!cm.isHidden(i));

			if (!config.isHideable()) {
				check.disable();
			}

			check.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {

				@Override
				public void onCheckChange(
						CheckChangeEvent<CheckMenuItem> event) {
					cm.setHidden(fcol, !cm.isHidden(fcol));
					charactersGridView.restrictMenu(cm, columnMenu);

				}
			});
			columnMenu.add(check);
		}

		charactersGridView.restrictMenu(cm, columnMenu);
		columns.setEnabled(columnMenu.getWidgetCount() > 0);
		columns.setSubMenu(columnMenu);
		add(columns);*/

		add(new HeaderMenuItem("Annotation"));
		item = new MenuItem("Comment");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox(
						"Comment", "");
				box.setValue(annotationManager.getCharacterComment(colIndex));
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						annotationManager.setCharacterComment(colIndex, box.getValue());
						String comment = Format.ellipse(box.getValue(), 80);
						String message = Format.substitute("'{0}' saved",
								new Params(comment));
						Info.display("Comment", message);
					}
				});
				box.show();
			}
		});
		add(item);
		
		item = new MenuItem("Colorize");
		Menu colorMenu = new Menu();
		item.setSubMenu(colorMenu);
		MenuItem offItem = new MenuItem("None");
		offItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				annotationManager.setCharacterColor(colIndex, null);
			}
		});
		colorMenu.add(offItem);
		for(final Color color : annotationManager.getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					annotationManager.setCharacterColor(colIndex, color);
				}
			});
			colorMenu.add(colorItem);
		}
		
		add(item);
	}	
}
