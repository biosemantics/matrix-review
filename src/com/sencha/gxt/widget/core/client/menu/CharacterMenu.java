package com.sencha.gxt.widget.core.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.MyGridView;
import com.sencha.gxt.widget.core.client.info.Info;

import edu.arizona.biosemantics.matrixreview.client.ControlMode;
import edu.arizona.biosemantics.matrixreview.client.TaxonMatrixView;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class CharacterMenu extends Menu {

	private TaxonMatrixView taxonMatrixView;
	private int colIndex;
	private MyGridView myGridView;
	private ColumnModel<Taxon> cm;

	public CharacterMenu(final TaxonMatrixView taxonMatrixView, final MyGridView myGridView, final int colIndex) {
		super();
		this.taxonMatrixView = taxonMatrixView;
		this.colIndex = colIndex;
		this.myGridView = myGridView;
		this.cm = myGridView.getColumnModel();
		
		int cols = cm.getColumnCount();
		
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
								taxonMatrixView.addCharacterAfter(colIndex, new Character(name, organ));
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
				taxonMatrixView.deleteColumn(colIndex);
			}
		});
		add(item);
		
		item = new MenuItem();
		item.setText("Rename");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final Character character = taxonMatrixView.getCharacter(colIndex);
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
								taxonMatrixView.renameCharacter(colIndex, name, organ);
							}
						});
						organBox.show();
					}
				});
				nameBox.show();
			}
		});
		add(item);
		
		item = new MenuItem("Move after");
		add(item);
		Menu moveMenu = new Menu();
		item.setSubMenu(moveMenu);

		item = new MenuItem("start");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				cm.moveColumn(colIndex, 0);
			}
		});
		moveMenu.add(item);

		// col 0 is for the expander, col 1 is taxon name: Do we want them
		// to be rearrangable too? when browsing vertically far maybe?
		for (int i = 0; i < cols; i++) {
			if (i != colIndex) {
				final int theI = i;
				item = new MenuItem(cm.getColumnHeader(i).asString());
				item.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						cm.moveColumn(colIndex, theI + 1);
					}
				});
				moveMenu.add(item);
			}
		}

		final CheckMenuItem lockItem = new CheckMenuItem("Lock");
		lockItem.setChecked(taxonMatrixView.isLockedColumn(colIndex));
		lockItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean newValue = !taxonMatrixView
						.isLockedColumn(colIndex);
				lockItem.setChecked(newValue);
				taxonMatrixView.setLockedColumn(colIndex, newValue);
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
				if(!taxonMatrixView.getControlMode(colIndex).equals(ControlMode.OFF)) {
					taxonMatrixView.setControlMode(colIndex, ControlMode.OFF);
				}
			}
		});
		automatic.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ControlMode controlMode = taxonMatrixView.determineControlMode(colIndex);
				taxonMatrixView.setControlMode(colIndex, controlMode);
			}
		});
		numerical.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!taxonMatrixView.getControlMode(colIndex).equals(ControlMode.NUMERICAL)) {
					taxonMatrixView.setControlMode(colIndex, ControlMode.NUMERICAL);
				}
			}
		});
		categorical.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				if(!taxonMatrixView.getControlMode(colIndex).equals(ControlMode.CATEGORICAL)) {
					taxonMatrixView.setControlMode(colIndex, ControlMode.CATEGORICAL);
				}
			}
		});
		add(controlItem);
		switch(taxonMatrixView.getControlMode(colIndex)) {
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
			item.setIcon(myGridView.getHeader().getAppearance().sortAscendingIcon());
			item.addSelectionHandler(new SelectionHandler<Item>() {

				@Override
				public void onSelection(SelectionEvent<Item> event) {
					myGridView.doSort(colIndex, SortDir.ASC);

				}
			});
			add(item);

			item = new MenuItem();
			item.setText(DefaultMessages.getMessages()
					.gridView_sortDescText());
			item.setIcon(myGridView.getHeader().getAppearance().sortDescendingIcon());
			item.addSelectionHandler(new SelectionHandler<Item>() {

				@Override
				public void onSelection(SelectionEvent<Item> event) {
					myGridView.doSort(colIndex, SortDir.DESC);

				}
			});
			add(item);
		}

		MenuItem columns = new MenuItem();
		columns.setText(DefaultMessages.getMessages()
				.gridView_columnsText());
		columns.setIcon(myGridView.getHeader().getAppearance().columnsIcon());
		columns.setData("gxt-columns", "true");

		final Menu columnMenu = new Menu();

		for (int i = 0; i < cols; i++) {
			ColumnConfig<Taxon, ?> config = cm.getColumn(i);
			// ignore columns with no header text
			if (!myGridView.hasHeaderValue(i)) {
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
					myGridView.restrictMenu(cm, columnMenu);

				}
			});
			columnMenu.add(check);
		}

		myGridView.restrictMenu(cm, columnMenu);
		columns.setEnabled(columnMenu.getWidgetCount() > 0);
		columns.setSubMenu(columnMenu);
		add(columns);

		add(new HeaderMenuItem("Annotation"));
		item = new MenuItem("Comment");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox(
						"Comment", "");
				box.setValue(taxonMatrixView.getColumnComment(colIndex));
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						taxonMatrixView.setColumnComment(colIndex, box.getValue());
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
				taxonMatrixView.setColumnColor(colIndex, null);
			}
		});
		colorMenu.add(offItem);
		for(final Color color : taxonMatrixView.getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					taxonMatrixView.setColumnColor(colIndex, color);
				}
			});
			colorMenu.add(colorItem);
		}
		
		add(item);
	}	
}
