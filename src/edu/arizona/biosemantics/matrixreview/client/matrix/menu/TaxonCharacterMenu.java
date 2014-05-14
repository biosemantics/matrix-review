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

import edu.arizona.biosemantics.matrixreview.client.event.HideCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.HideTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModelModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.ColorSettingsDialog;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView.ModelMode;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.CharacterMenu.CharacterAddDialog;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class TaxonCharacterMenu extends Menu {

	private EventBus eventBus;
	private TaxonMatrix taxonMatrix;
	private ModelMode modelMode;
	private TreeStore<Taxon> treeStore;

	public TaxonCharacterMenu(final EventBus eventBus, TaxonMatrix taxonMatrix, ModelMode modelMode, TreeStore<Taxon> treeStore) {
		this.eventBus = eventBus;
		this.taxonMatrix = taxonMatrix;
		this.modelMode = modelMode;
		this.treeStore = treeStore;
		
		add(new HeaderMenuItem("Taxa/Characters"));
		add(createAddCharacter());
		add(createLock());
		add(new HeaderMenuItem("View"));
		add(createModelMode());
		add(createSortTaxa());
		add(createSortCharacters());
		add(createHideTaxa());
		add(createHideCharacters());
		add(new HeaderMenuItem("Annotation"));
		add(createColorSettings());
		add(new HeaderMenuItem("Analysis"));
		add(createAnalysisStart());
	}
	
	private Widget createModelMode() {
		MenuItem item = new MenuItem("Taxon Concept Presentation");
		Menu menu = new Menu();
		item.setSubMenu(menu);
		CheckMenuItem flat = new CheckMenuItem("Matrix");
		flat.setGroup("mode");
		CheckMenuItem hierarchy = new CheckMenuItem("Taxonomy");
		hierarchy.setGroup("mode");
		//CheckMenuItem custom = new CheckMenuItem("Custom");
		//custom.setGroup("mode");
		menu.add(flat);
		menu.add(hierarchy);
		//menu.add(custom);
		switch(modelMode) {
		//case CUSTOM_HIERARCHY:
		//	custom.setChecked(true);
		//	break;
		case FLAT:
			flat.setChecked(true);
			break;
		case TAXONOMIC_HIERARCHY:
			hierarchy.setChecked(true);
			break;		
		}
		flat.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				modelMode = ModelMode.FLAT;
				eventBus.fireEvent(new ModelModeEvent(ModelMode.FLAT));
			}
		});
		hierarchy.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				modelMode = ModelMode.TAXONOMIC_HIERARCHY;
				eventBus.fireEvent(new ModelModeEvent(ModelMode.TAXONOMIC_HIERARCHY));
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

	private MenuItem createAnalysisStart() {
		MenuItem item = new MenuItem("Start");
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
				ColorSettingsDialog dialog = new ColorSettingsDialog(eventBus, taxonMatrix);
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
		for (final Character character : taxonMatrix.getCharacters()) {
			final CheckMenuItem check = new CheckMenuItem();
			check.setHideOnClick(false);
			check.setText(character.toString());
			check.setChecked(!character.isHidden());
			check.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
				@Override
				public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
					eventBus.fireEvent(new HideCharacterEvent(character, !check.isChecked()));
				}
			});
			columnMenu.add(check);
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
		
		switch(modelMode) {
			case FLAT:
				for(final Taxon taxon : taxonMatrix.list()) {
					final CheckMenuItem check = new CheckMenuItem();
					check.setHideOnClick(false);
					check.setText(taxon.getFullName());
					check.setChecked(!taxon.isHidden());
					check.addCheckChangeHandler(new CheckChangeHandler<CheckMenuItem>() {
						@Override
						public void onCheckChange(CheckChangeEvent<CheckMenuItem> event) {
							eventBus.fireEvent(new HideTaxonEvent(taxon, !check.isChecked()));
						}
					});
					rowMenu.add(check);
				}
				break;
			case CUSTOM_HIERARCHY:
			case TAXONOMIC_HIERARCHY:
				for (final Taxon taxon : taxonMatrix.list()) {
					if(!taxon.hasParent()) {
						createHideTaxonMenu(taxon, rowMenu);
					}
				}
				break;
		}
		if(rowMenu.getWidgetCount() > 0)
			rows.setSubMenu(rowMenu);
		return rows;
	}
	
	private void createHideTaxonMenu(final Taxon taxon, final Menu menu) {
		final CheckMenuItem check = new CheckMenuItem();
		final Menu subMenu = new Menu();
		
		check.setHideOnClick(false);
		check.setText(taxon.getFullName());
		check.setChecked(!taxon.isHidden());
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
				eventBus.fireEvent(new HideTaxonEvent(taxon, !check.isChecked()));
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

	private MenuItem createSortCharacters() {
		MenuItem item = new MenuItem();
		item.setText("Sort Characters");
		Menu sortMenu = new Menu();
		item.setSubMenu(sortMenu);
		MenuItem coverageSortDesc = new MenuItem("Coverage Desc");
		sortMenu.add(coverageSortDesc);
		MenuItem coverageSortAsc = new MenuItem("Coverage Asc");
		sortMenu.add(coverageSortAsc);
		MenuItem nameSortDesc = new MenuItem("Character Name Desc");
		sortMenu.add(nameSortDesc);
		MenuItem nameSortAsc = new MenuItem("Character Name Asc");
		sortMenu.add(nameSortAsc);
		MenuItem organSortDesc = new MenuItem("Character Organ Desc");
		sortMenu.add(organSortDesc);
		MenuItem organSortAsc = new MenuItem("Character Organ Asc");
		sortMenu.add(organSortAsc);
		
		coverageSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByCoverageEvent(true));
			}
		});
		coverageSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByCoverageEvent(false));
			}
		});
		nameSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByNameEvent(true));
			}
		});
		nameSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByNameEvent(false));
			}
		});
		organSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByOrganEvent(true));
			}
		});
		organSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SortCharactersByOrganEvent(true));
			}
		});
		return item;
	}

	private MenuItem createSortTaxa() {
		MenuItem item = new MenuItem();
		item.setText("Sort Taxa");
		Menu sortMenu = new Menu();
		item.setSubMenu(sortMenu);
		MenuItem coverageSortDesc = new MenuItem("Coverage Desc");
		sortMenu.add(coverageSortDesc);
		MenuItem coverageSortAsc = new MenuItem("Coverage Asc");
		sortMenu.add(coverageSortAsc);
		MenuItem nameSortDesc = new MenuItem("Taxon Name Desc");
		sortMenu.add(nameSortDesc);
		MenuItem nameSortAsc = new MenuItem("Taxon Name Asc");
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
		dialog.setHeadingText("BorderLayout Dialog");
		dialog.setWidth(600);
		dialog.setHeight(275);
		dialog.setHideOnButtonClick(true);

		BorderLayoutContainer layout = new BorderLayoutContainer();
		dialog.add(layout);

		// Layout - west
		ContentPanel westPanel = new ContentPanel();
		westPanel.setHeadingText("Create Color");
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
		centerPanel.setHeadingText("Available colors");
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
		CheckMenuItem set = new CheckMenuItem("Set");
		set.setGroup("editMode");
		CheckMenuItem unset = new CheckMenuItem("Unset");
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
		if(!taxonMatrix.isLocked())
			unset.setChecked(true);
		if(taxonMatrix.isLocked())
			set.setChecked(true);
		editMenu.add(set);
		editMenu.add(unset);
		add(editMode);
		return editMode;
	}

	private MenuItem createAddCharacter() {
		MenuItem item = new MenuItem();
		item.setText("Add Character");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for (int i = 0; i < 1; i++) {
					CharacterAddDialog addDialog = new CharacterAddDialog(eventBus, taxonMatrix);
					addDialog.show();
					//eventBus.fireEvent(new AddCharacterEvent(new Character("character" + i, new Organ("organ" + i))));
				}
			}
		});
		add(item);

		item = new MenuItem();
		item.setText("Add Taxon");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for (int i = 0; i < 1; i++) {
					TaxonMenu.TaxonAddDialog addDialog = new TaxonMenu.TaxonAddDialog(eventBus, taxonMatrix, null);
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
