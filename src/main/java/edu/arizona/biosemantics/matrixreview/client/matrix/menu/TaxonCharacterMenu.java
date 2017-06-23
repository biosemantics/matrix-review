package edu.arizona.biosemantics.matrixreview.client.matrix.menu;


import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.CharacterAddDialog;
import edu.arizona.biosemantics.matrixreview.client.common.ColorSettingsDialog;
import edu.arizona.biosemantics.matrixreview.client.common.TaxonAddDialog;
import edu.arizona.biosemantics.matrixreview.client.event.CollapseTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ExpandTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.HideCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.HideTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MatrixModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.MatrixMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class TaxonCharacterMenu extends Menu {

	private EventBus eventBus;
	private MatrixMode matrixMode;
	private TreeStore<Taxon> treeStore;
	private Model model;

	public TaxonCharacterMenu(final EventBus eventBus, Model model, MatrixMode matrixMode, TreeStore<Taxon> treeStore) {
		this.model = model;
		this.eventBus = eventBus;
		this.matrixMode = matrixMode;
		this.treeStore = treeStore;
		
		add(new HeaderMenuItem("Taxa/Characters"));
		add(createAddCharacter());
		add(createAddTaxon());
		add(createLock());
		add(new HeaderMenuItem("View"));
		add(createModelMode());
		add(createSortTaxa());
		add(createSortCharacters());
		add(createHideTaxa());
		add(createHideCharacters());
		if(matrixMode.equals(MatrixMode.HIERARCHY)) {
			add(createCollapseAll());
			add(createExpandAll());
		}
		//add(new HeaderMenuItem("Annotation"));
		//add(createColorSettings());
		add(new HeaderMenuItem("Analysis"));
		add(createAnalysisStart());
	}

	private Widget createModelMode() {
		MenuItem item = new MenuItem("Matrix Views");
		Menu menu = new Menu();
		item.setSubMenu(menu);
		CheckMenuItem flat = new CheckMenuItem("Matrix");
		flat.setGroup("mode");
		CheckMenuItem hierarchy = new CheckMenuItem("Taxonomy Hierarchy");
		hierarchy.setGroup("mode");
		//CheckMenuItem custom = new CheckMenuItem("Custom");
		//custom.setGroup("mode");
		menu.add(flat);
		menu.add(hierarchy);
		//menu.add(custom);
		switch(matrixMode) {
		//case CUSTOM_HIERARCHY:
		//	custom.setChecked(true);
		//	break;
		case FLAT:
			flat.setChecked(true);
			break;
		case HIERARCHY:
			hierarchy.setChecked(true);
			break;		
		}
		flat.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				matrixMode = MatrixMode.FLAT;
				eventBus.fireEvent(new MatrixModeEvent(MatrixMode.FLAT));
			}
		});
		hierarchy.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				matrixMode = MatrixMode.HIERARCHY;
				eventBus.fireEvent(new MatrixModeEvent(MatrixMode.HIERARCHY));
			}
		});
		/*custom.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				modelMode = ModelMode.CUSTOM_HIERARCHY;
				eventBus.fireEvent(new ModelModeEvent(ModelMode.CUSTOM_HIERARCHY));
			}
		});*/
		return item;
	}
	
	private Widget createExpandAll() {
		MenuItem item = new MenuItem("Expand All");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new ExpandTaxaEvent(treeStore.getAll()));
			}
		});
		return item;
	}

	private Widget createCollapseAll() {
		MenuItem item = new MenuItem("Collapse All");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new CollapseTaxaEvent(treeStore.getAll()));
			}
		});
		return item;
	}

	private MenuItem createAnalysisStart() {
		MenuItem item = new MenuItem("Show Analysis Desktop");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new ShowDesktopEvent());
			}
		});
		return item;
	}

	private MenuItem createColorSettings() {
		MenuItem item = new MenuItem("Color Setting");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ColorSettingsDialog dialog = new ColorSettingsDialog(eventBus, model);
				dialog.show();
			}
		});
		return item;
	}

	private MenuItem createHideCharacters() {
		MenuItem columns = new MenuItem();
		columns.setText("Characters");
		//columns.setIcon(charactersGridView.getHeader().getAppearance().columnsIcon());
		//columns.setData("gxt-columns", "true");
		
		final Menu columnMenu = new Menu();
		for(final Organ organ : model.getTaxonMatrix().getHierarchyCharacters()) {
			final CheckMenuItem organItem = new CheckMenuItem(organ.getName());
			
			final Menu organMenu = new Menu();
			boolean allCharactersHidden = true;
			for(final Character character : organ.getFlatCharacters()) {
				final CheckMenuItem check = new CheckMenuItem();
				check.setHideOnClick(false);
				check.setText(character.toString());
				check.setChecked(!model.isHidden(character));
				allCharactersHidden &= model.isHidden(character);
				check.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
					@Override
					public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
						eventBus.fireEvent(new HideCharacterEvent(character, !check.isChecked()));
					}
				});
				organMenu.add(check);
			}
			
			if(!allCharactersHidden && organMenu.getWidgetCount() > 0) {
				organItem.setSubMenu(organMenu);
				columnMenu.add(organItem);
			} 
			organItem.setChecked(!allCharactersHidden);
			organItem.setHideOnClick(false);
			
			organItem.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
				@Override
				public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
					/*if(organItem.isChecked() && organMenu.getWidgetCount() == 0) {
						for(Taxon child : taxon.getChildren()) {
							createHideTaxonMenu(child, subMenu);
						}
					} else {
						columnMenu.setActiveItem(null, true);
						organItem.setSubMenu(null);
					}*/
					if(organItem.isChecked() && organMenu.getWidgetCount() > 0) {
						organItem.setSubMenu(organMenu);
						columnMenu.setActiveItem(organItem, true);
					} 
					if(!organItem.isChecked()) {
						organMenu.hide();
						organItem.setSubMenu(null);
					}
					eventBus.fireEvent(new HideCharacterEvent(organ.getFlatCharacters(), !organItem.isChecked()));
				}
			});
		}
		
		columns.setSubMenu(columnMenu);
		return columns;
	}

	private MenuItem createHideTaxa() {
		MenuItem rows = new MenuItem();
		rows.setText("Taxa");
		// rows.setIcon(header.getAppearance().columnsIcon());
		// rows.setData("gxt-columns", "true");

		Menu rowMenu = new Menu();
		
		/*switch(matrixMode) {
			case FLAT:
				for(final Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
					final CheckMenuItem check = new CheckMenuItem();
					check.setHideOnClick(false);
					check.setText(taxon.getFullName());
					check.setChecked(!model.isHidden(taxon));
					check.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
						@Override
						public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
							eventBus.fireEvent(new HideTaxaEvent(taxon, !check.isChecked()));
						}
					});
					rowMenu.add(check);
				}
				break;
			case HIERARCHY:
				for (final Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
					if(!taxon.hasParent()) {
						createHideTaxonMenu(taxon, rowMenu);
					}
				}
				break;
		} */
		for (final Taxon taxon : model.getTaxonMatrix().getHierarchyRootTaxa()) {
			createHideTaxonMenu(taxon, rowMenu);
		}
		
		if(rowMenu.getWidgetCount() > 0)
			rows.setSubMenu(rowMenu);
		return rows;
	}
	
	private void createHideTaxonMenu(final Taxon taxon, final Menu menu) {
		if(model.getTaxonMatrix().isVisiblyContained(taxon)) {
			final CheckMenuItem check = new CheckMenuItem();
			final Menu subMenu = new Menu();
			
			check.setHideOnClick(false);
			check.setText(taxon.getBiologicalName());
			check.setChecked(!model.isHidden(taxon));
			check.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
				@Override
				public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
					if(check.isChecked() && subMenu.getWidgetCount() == 0) {
						for(Taxon child : taxon.getChildren()) {
							createHideTaxonMenu(child, subMenu);
						}
					} else {
						menu.setActiveItem(null, true);
						check.setSubMenu(null);
					}
					if(check.isChecked() && subMenu.getWidgetCount() > 0) {
						check.setSubMenu(subMenu);
						menu.setActiveItem(check, true);
					}
					eventBus.fireEvent(new HideTaxaEvent(taxon, !check.isChecked()));
				}
			});
			
			if(check.isChecked()) {
				for(Taxon child : taxon.getChildren()) {
					createHideTaxonMenu(child, subMenu);
				}
				if(subMenu.getWidgetCount() > 0)
					check.setSubMenu(subMenu);
			}
	
			menu.add(check);
		}
	}

	private MenuItem createSortCharacters() {
		MenuItem item = new MenuItem();
		item.setText("Sort Characters");
		Menu sortMenu = new Menu();
		item.setSubMenu(sortMenu);
		MenuItem coverageSortDesc = new MenuItem("Coverage (Descending)");
		sortMenu.add(coverageSortDesc);
		MenuItem coverageSortAsc = new MenuItem("Coverage (Ascending)");
		sortMenu.add(coverageSortAsc);
		MenuItem nameSortDesc = new MenuItem("Character Name (Descending)");
		sortMenu.add(nameSortDesc);
		MenuItem nameSortAsc = new MenuItem("Character Name (Ascending)");
		sortMenu.add(nameSortAsc);
		MenuItem organSortDesc = new MenuItem("Character Organ (Descending)");
		sortMenu.add(organSortDesc);
		MenuItem organSortAsc = new MenuItem("Character Organ (Ascending)");
		sortMenu.add(organSortAsc);
		
		coverageSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByCoverageEvent(SortDir.ASC));
			}
		});
		coverageSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByCoverageEvent(SortDir.DESC));
			}
		});
		nameSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByNameEvent(SortDir.ASC));
			}
		});
		nameSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByNameEvent(SortDir.DESC));
			}
		});
		organSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByOrganEvent(SortDir.ASC));
			}
		});
		organSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByOrganEvent(SortDir.DESC));
			}
		});
		return item;
	}

	private MenuItem createSortTaxa() {
		MenuItem item = new MenuItem();
		item.setText("Sort Taxa");
		Menu sortMenu = new Menu();
		item.setSubMenu(sortMenu);
		MenuItem coverageSortDesc = new MenuItem("Coverage (Descending)");
		sortMenu.add(coverageSortDesc);
		MenuItem coverageSortAsc = new MenuItem("Coverage (Ascending)");
		sortMenu.add(coverageSortAsc);
		MenuItem nameSortDesc = new MenuItem("Taxon Name (Descending)");
		sortMenu.add(nameSortDesc);
		MenuItem nameSortAsc = new MenuItem("Taxon Name (Ascending)");
		sortMenu.add(nameSortAsc);
		coverageSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortTaxaByCoverageEvent(SortDir.ASC));
			}
		});
		coverageSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortTaxaByCoverageEvent(SortDir.DESC));
			}
		});
		nameSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortTaxaByNameEvent(SortDir.ASC));
			}
		});
		nameSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortTaxaByNameEvent(SortDir.DESC));
			}
		});
		return item;
	}

	/*
	private Dialog createColorManagementDialog() {
		final CellTable<Color> colorsTable = new CellTable<Color>();
		
		final Dialog dialog = new Dialog();
		dialog.setBodyBorder(false);
		dialog.setHeading("BorderLayout Dialog");
		dialog.setWidth(600);
		dialog.setHeight(275);
		dialog.setHideOnButtonClick(true);

		BorderLayoutContainer layout = new BorderLayoutContainer();
		dialog.add(layout);

		// Layout - west
		ContentPanel westPanel = new ContentPanel();
		westPanel.setHeading("Create Color");
		BorderLayoutData data = new BorderLayoutData(150);
		data.setMargins(new Margins(0, 5, 0, 0));
		westPanel.setLayoutData(data);
		VerticalPanel colorChoosePanel = new VerticalPanel();
		ColorPalette colorPalette = new ColorPalette();
		colorChoosePanel.add(colorPalette);
		final TextField textField = new TextField();
		final Label colorLabel = new Label();
		colorLabel.setWidth("30px");
		colorLabel.setHeight("30px");
		colorChoosePanel.add(textField);
		colorChoosePanel.add(colorLabel);
		colorPalette.addSelectionHandler(new SelectionHandler<String>() {
			@Override
			public void onSelection(SelectionEvent<String> event) {
				textField.setValue(event.getSelectedItem());
				textField.validate();
			}
		});
		textField.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				textField.validate();
			}
		});
		textField.addValidator(new Validator<String>() {
			@Override
			public List<EditorError> validate(Editor<String> editor, String value) {
				List<EditorError> error = new LinkedList<EditorError>();
				RegExp hexPattern = RegExp.compile("^([A-Fa-f0-9]{6})$");
				if(!hexPattern.test(textField.getValue())) {
					error.add(new DefaultEditorError(editor, "Not a valid color code", value));
				} else {
					String text = textField.getText();
					if(text.length() == 6) {
						colorLabel.getElement().getStyle().setBackgroundColor("#" + text);
					}
				}
				return error;
			}
		});
		westPanel.add(colorChoosePanel);
		Button addButton = new Button("add");
		westPanel.addButton(addButton);
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				List<Color> colors = annotationManager.getColors();
				colors.add(new Color(textField.getText(), ""));
				colorsTable.setRowData(colors);
				annotationManager.setColors(colors);
			}
		});
		
		BorderLayoutData westLayoutData = new BorderLayoutData(200);
		westLayoutData.setCollapsible(true);
		westLayoutData.setSplit(true);
		westLayoutData.setCollapseMini(true);
		westLayoutData.setMargins(new Margins(0, 8, 0, 5));
		layout.setWestWidget(westPanel, westLayoutData);

		// Layout - center
		ContentPanel centerPanel = new ContentPanel();
		centerPanel.setHeading("Available colors");
		layout.setCenterWidget(centerPanel);
		
		final CheckboxCell checkboxCell = new CheckboxCell();
		Column<Color, Boolean> checkColumn = new Column<Color, Boolean>(checkboxCell) {
			@Override
			public Boolean getValue(Color object) {
				return false;
			}
		};		
		final ColorCell customCell = new ColorCell();
	    Column<Color, String> colorColumn = new Column<Color, String>(customCell) {
	      @Override
	      public String getValue(Color object) {
	        return object.getHex();
	      }
	    };
	    final TextInputCell useCell = new TextInputCell();
	    Column<Color, String> useColumn = new Column<Color, String>(useCell) {
	      @Override
	      public String getValue(Color object) {
	        return object.getUse();
	      }
	    };
		useColumn.setFieldUpdater(new FieldUpdater<Color, String>() {
		    @Override
		    public void update(int index, Color object, String value) {
		        object.setUse(value);
		    }
		});
	    
	    colorsTable.addColumn(checkColumn, "Select");
	    colorsTable.addColumn(colorColumn, "Color");
	    colorsTable.addColumn(useColumn, "Usage");
	    colorsTable.setRowData(annotationManager.getColors());
		
	    ScrollPanel scrollPanel = new ScrollPanel();
	    Button removeButton = new Button("remove");
	    
	    removeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Set<Color> toRemove = new HashSet<Color>();
				List<Color> colors = annotationManager.getColors();
				for(Color color : colors) {
					Boolean checked = checkboxCell.getViewData(color);
					if(checked == null)
						continue;
					if(checked); {
						toRemove.add(color);
					}
				}
				for(Color color : toRemove) {
					colors.remove(color);
				}	
				colorsTable.setRowData(colors);
				annotationManager.setColors(colors);
			}
	    });
	    scrollPanel.setWidget(colorsTable);
		centerPanel.setWidget(scrollPanel);
		centerPanel.addButton(removeButton);
		
		return dialog;
	}*/
	
	private MenuItem createLock() {
		MenuItem editMode = new MenuItem("Lock all");
		Menu editMenu = new Menu();
		editMode.setSubMenu(editMenu);
		CheckMenuItem set = new CheckMenuItem("Set lock");
		set.setGroup("editMode");
		CheckMenuItem unset = new CheckMenuItem("Unset lock");
		unset.setGroup("editMode");
		set.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new LockMatrixEvent(true));
			}
		});
		unset.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new LockMatrixEvent(false));
			}
		});
		if(!isLockedEntiredMatrix())
			unset.setChecked(true);
		else
			set.setChecked(true);
		editMenu.add(set);
		editMenu.add(unset);
		add(editMode);
		return editMode;
	}

	private boolean isLockedEntiredMatrix() {
		for(Character character : model.getTaxonMatrix().getVisibleFlatCharacters()) {
			if(!model.isLocked(character)) {
				return false;
			} 
		}
		for(Taxon taxon : model.getTaxonMatrix().getVisibleFlatTaxa()) {
			if(!model.isLocked(taxon)) {
				return false;
			} 
		}
		return true;
	}

	private MenuItem createAddCharacter() {
		MenuItem item = new MenuItem();
		item.setText("Add Character");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for (int i = 0; i < 1; i++) {
					CharacterAddDialog addDialog = new CharacterAddDialog(eventBus, model, null);
					addDialog.show();
					//eventBus.fireEvent(new AddCharacterEvent(new Character("character" + i, new Organ("organ" + i))));
				}
			}
		});
		return item;
	}
		
	private MenuItem createAddTaxon() {
		MenuItem item = new MenuItem();
		item.setText("Add Taxon");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for (int i = 0; i < 1; i++) {
					TaxonAddDialog addDialog = new TaxonAddDialog(eventBus, model, null);
					addDialog.show();
					//eventBus.fireEvent(new AddTaxonEvent(new Taxon(Level.SPECIES, "taxon" + Random.nextInt(), "author", "year")));
				}
			}
		});
		return item;
	}

	/*private void restrictMenu(Menu rows) {
	//private void restrictMenu(ColumnModel<Taxon> cm, Menu columns) {
		//TODO for rows rather than columns
		/*int count = 0;
		for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
			if (hasHeaderValue(i)) {
				ColumnConfig<M, ?> cc = cm.getColumn(i);
				if (cc.isHidden() || !cc.isHideable()) {
					continue;
				}
				count++;
			}
		}

		if (count == 1) {
			for (Widget item : columns) {
				CheckMenuItem ci = (CheckMenuItem) item;
				if (ci.isChecked()) {
					ci.disable();
				}
			}
		} else {
			for (int i = 0, len = columns.getWidgetCount(); i < len; i++) {
				Widget item = columns.getWidget(i);
				ColumnConfig<M, ?> config = cm.getColumn(i);
				if (item instanceof Component && config.isHideable()) {
					((Component) item).enable();
				}
			}
		} */
	//}
}
