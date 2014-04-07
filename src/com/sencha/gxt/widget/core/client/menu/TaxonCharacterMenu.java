package com.sencha.gxt.widget.core.client.menu;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ColorPalette;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;

import edu.arizona.biosemantics.matrixreview.client.ColorCell;
import edu.arizona.biosemantics.matrixreview.client.TaxonMatrixView;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;

public class TaxonCharacterMenu extends Menu {

	private TaxonMatrixView taxonMatrixView;

	public TaxonCharacterMenu(final TaxonMatrixView taxonMatrixView) {
		super();
		this.taxonMatrixView = taxonMatrixView;
		
		add(new HeaderMenuItem("Taxa/Characters"));
		
		MenuItem item = new MenuItem();
		item.setText("Add Character");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for (int i = 0; i < 10; i++)
					taxonMatrixView.addCharacter(new Character("character" + i, "organ" + i));
			}
		});
		add(item);

		item = new MenuItem();
		item.setText("Add Taxon");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for (int i = 0; i < 10; i++)
					taxonMatrixView.addTaxon("taxon" + i);
			}
		});
		add(item);
		
		MenuItem editMode = new MenuItem("Lock all");
		Menu editMenu = new Menu();
		editMode.setSubMenu(editMenu);
		CheckMenuItem enable = new CheckMenuItem("Enable");
		enable.setGroup("editMode");
		CheckMenuItem disable = new CheckMenuItem("Disable");
		disable.setGroup("editMode");
		enable.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.enableEditing(true);
			}
		});
		disable.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.enableEditing(false);
			}
		});
		if(taxonMatrixView.isEditableAll())
			enable.setChecked(true);
		if(taxonMatrixView.isNotEditableAll())
			disable.setChecked(true);
		editMenu.add(enable);
		editMenu.add(disable);
		add(editMode);
		
		add(new HeaderMenuItem("View"));
		
		item = new MenuItem();
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
				taxonMatrixView.sortRowsByCoverage(true);
			}
		});
		coverageSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.sortRowsByCoverage(false);
			}
		});
		nameSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.sortRowsByName(true);
			}
		});
		nameSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.sortRowsByName(false);
			}
		});
		add(item);
		
		item = new MenuItem();
		item.setText("Sort Characters");
		sortMenu = new Menu();
		item.setSubMenu(sortMenu);
		coverageSortDesc = new MenuItem("Coverage Desc");
		sortMenu.add(coverageSortDesc);
		coverageSortAsc = new MenuItem("Coverage Asc");
		sortMenu.add(coverageSortAsc);
		nameSortDesc = new MenuItem("Character Name Desc");
		sortMenu.add(nameSortDesc);
		nameSortAsc = new MenuItem("Character Name Asc");
		sortMenu.add(nameSortAsc);
		MenuItem organSortDesc = new MenuItem("Character Organ Desc");
		sortMenu.add(organSortDesc);
		MenuItem organSortAsc = new MenuItem("Character Organ Asc");
		sortMenu.add(organSortAsc);
		
		coverageSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.sortColumnsByCoverage(true);
			}
		});
		coverageSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.sortColumnsByCoverage(false);
			}
		});
		nameSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.sortColumnsByName(true);
			}
		});
		nameSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.sortColumnsByName(false);
			}
		});
		organSortAsc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.sortColumnsByOrgan(true);
			}
		});
		organSortDesc.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				taxonMatrixView.sortColumnsByOrgan(false);
			}
		});
		add(item);
		
		add(new HeaderMenuItem("Annotation"));
		
		item = new MenuItem("Color Setting");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Dialog dialog = createColorManagementDialog();
				dialog.show();
			}
		});
		add(item);
	}
	
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
				List<Color> colors = taxonMatrixView.getColors();
				colors.add(new Color(textField.getText(), ""));
				colorsTable.setRowData(colors);
				taxonMatrixView.setColors(colors);
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
	    colorsTable.setRowData(taxonMatrixView.getColors());
		
	    ScrollPanel scrollPanel = new ScrollPanel();
	    Button removeButton = new Button("remove");
	    
	    removeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Set<Color> toRemove = new HashSet<Color>();
				List<Color> colors = taxonMatrixView.getColors();
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
			}
	    });
	    scrollPanel.setWidget(colorsTable);
		centerPanel.setWidget(scrollPanel);
		centerPanel.addButton(removeButton);
		
		return dialog;
	}
}
