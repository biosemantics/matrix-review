package com.sencha.gxt.widget.core.client.grid;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.gwt.cell.client.ButtonCellBase.DefaultAppearance.Resources;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.ColorPalette;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.ControlMode;
import edu.arizona.biosemantics.matrixreview.client.ColorCell;
import edu.arizona.biosemantics.matrixreview.client.TaxonMatrixView;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class MyGridView extends GridView<Taxon> {

	private TaxonMatrixView taxonMatrixView;
	private ColumnHeaderStyles columnHeaderStyles;
	
	public MyGridView(TaxonMatrixView taxonMatrixView) {
		this(taxonMatrixView, GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class));
	}
	
	public MyGridView(TaxonMatrixView taxonMatrixView, ColumnHeaderAppearance columnHeaderAppearance) {
		this.taxonMatrixView = taxonMatrixView;
		this.columnHeaderStyles = columnHeaderAppearance.styles();
	}

	protected Menu createContextMenu(final int colIndex) {
		if(colIndex == taxonMatrixView.getTaxonNameColumn()) {
			final Menu menu = new Menu();
			
			MenuItem editMode = new MenuItem("Edit");
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
			menu.add(editMode);
			
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
			menu.add(item);

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
			menu.add(item);
			
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
			menu.add(item);
			
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
			menu.add(item);
			
			item = new MenuItem("Colors");
			item.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					Dialog dialog = createColorManagementDialog();
					dialog.show();
				}
			});
			menu.add(item);
			return menu;
		} else {
			Menu menu = super.createContextMenu(colIndex);

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
			menu.add(item);
			
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
			menu.add(item);
			
			item = new MenuItem();
			item.setText("Delete");
			// item.setIcon(header.getAppearance().sortAscendingIcon());
			item.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					taxonMatrixView.deleteColumn(colIndex);
				}
			});
			menu.add(item);

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
			menu.add(item);
			
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
			
			menu.add(item);

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
			menu.add(lockItem);

			final MenuItem controlItem = new MenuItem("Controlled");
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
			menu.add(controlItem);
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

			item = new MenuItem("Move after");
			menu.add(item);
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

			int cols = cm.getColumnCount();

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

			return menu;
		}
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

	@Override
	protected SafeHtml doRender(List<ColumnData> cs, List<Taxon> rows, int startRow) {
		final int colCount = cm.getColumnCount();
		final int last = colCount - 1;

		int[] columnWidths = getColumnWidths();

		// root builder
		SafeHtmlBuilder buf = new SafeHtmlBuilder();

		final SafeStyles rowStyles = SafeStylesUtils
				.fromTrustedString("width: " + getTotalWidth() + "px;");

		final String unselectableClass = unselectable;
		final String rowAltClass = styles.rowAlt();
		final String rowDirtyClass = styles.rowDirty();

		final String cellClass = styles.cell() + " " + states.cell();
		/*
		 * System.out.println(styles.cellInner());
		 * System.out.println(styles.cell());
		 * System.out.println(styles.rowWrap());
		 * System.out.println(styles.rowBody());
		 * System.out.println(styles.rowBodyRow());
		 * System.out.println(styles.cellDirty());
		 * System.out.println(styles.rowDirty());
		 */
		final String cellInnerClass = styles.cellInner() + " "
				+ states.cellInner();
		final String cellFirstClass = "x-grid-cell-first";
		final String cellLastClass = "x-grid-cell-last";
		final String cellDirty = styles.cellDirty();
		final String cellCommented = styles.cellCommented();
		final String cellDirtyCommented = styles.cellDirtyCommented();
		System.out.println(cellCommented);
		System.out.println(cellDirtyCommented);
		System.out.println(cellDirty);
		

		final String rowWrap = styles.rowWrap() + " " + states.rowWrap();
		final String rowBody = styles.rowBody() + " " + states.rowBody();
		final String rowBodyRow = states.rowBodyRow();

		// loop over all rows
		for (int j = 0; j < rows.size(); j++) {
			Taxon model = rows.get(j);

			ListStore<Taxon>.Record r = ds.hasRecord(model) ? ds.getRecord(model)
					: null;

			int rowBodyColSpanCount = colCount;
			if (enableRowBody) {
				for (ColumnConfig<Taxon, ?> c : cm.getColumns()) {
					if (c instanceof RowExpander) {
						rowBodyColSpanCount--;
					}
				}
			}

			int rowIndex = (j + startRow);

			String rowClasses = styles.row() + " " + states.row();

			if (!selectable) {
				rowClasses += " " + unselectableClass;
			}
			if (isStripeRows() && ((rowIndex + 1) % 2 == 0)) {
				rowClasses += " " + rowAltClass;
			}

			if (this.isShowDirtyCells() && r != null && r.isDirty()) {
				rowClasses += " " + rowDirtyClass;
			}

			if (viewConfig != null) {
				rowClasses += " " + viewConfig.getRowStyle(model, rowIndex);
			}

			SafeHtmlBuilder trBuilder = new SafeHtmlBuilder();

			// loop each cell per row
			for (int i = 0; i < colCount; i++) {
				SafeHtml rv = getRenderedValue(rowIndex, i, model, r);
				ColumnConfig<Taxon, ?> columnConfig = cm.getColumn(i);
				ColumnData columnData = cs.get(i);

				String cellClasses = "";
				// if( i != 0)
				cellClasses = cellClass;
				if (i == 0) {
					cellClasses += " " + cellFirstClass;
				} else if (i == last) {
					cellClasses += " " + cellLastClass;
				}

				String cellInnerClasses = "";
				// if (i == 0)
				// cellInnerClasses = columnHeaderStyles.headInner();
				// if (i != 0)
				// cellInnerClasses = cellInnerClass;
				if (columnConfig.getColumnTextClassName() != null) {
					cellInnerClasses += " "
							+ columnConfig.getColumnTextClassName();
				}
				if (!columnConfig.isCellPadding()) {
					cellInnerClasses += " " + styles.noPadding();
				}

				if (columnData.getClassNames() != null) {
					cellClasses += " " + columnData.getClassNames();
				}

				if (columnConfig.getCellClassName() != null) {
					cellClasses += " " + columnConfig.getCellClassName();
				}
				
				if(i == taxonMatrixView.getTaxonNameColumn()) {
					String grandParentStyleClass = columnHeaderStyles.header() + " " + columnHeaderStyles.head();
					cellClasses += " " + grandParentStyleClass;		
				}
				/*if (this.isShowDirtyCells() && r != null
						&& r.getChange(columnConfig.getValueProvider()) != null && !taxonMatrixView.hasComment(model, columnConfig)) {
					cellClasses += " " + cellDirty;
				} else if(this.isShowDirtyCells() && r != null && r.getChange(columnConfig.getValueProvider()) != null && 
						taxonMatrixView.hasComment(model, columnConfig)) {
					cellClasses += " " + cellDirtyCommented;
				} else if(taxonMatrixView.hasComment(model, columnConfig)) {
					cellClasses += " " + cellCommented;
				}*/
				

				if (viewConfig != null) {
					cellClasses += " "
							+ viewConfig.getColStyle(model,
									cm.getValueProvider(i), rowIndex, i);
				}

				final SafeStyles cellStyles = columnData.getStyles();

				final SafeHtml tdContent;
				if (enableRowBody && i == 0) {
					tdContent = tpls.tdRowSpan(i, cellClasses, cellStyles,
							this.getRowBodyRowSpan(), cellInnerClasses, rv);
				} else {
					if (!selectable && GXT.isIE()) {
						tdContent = tpls.tdUnselectable(i, cellClasses,
								cellStyles, cellInnerClasses,
								columnConfig.getColumnTextStyle(), rv);
					} else {
						tdContent = tpls.td(i, cellClasses, cellStyles,
								cellInnerClasses,
								columnConfig.getColumnTextStyle(), rv);
					}

				}
				trBuilder.append(tdContent);
			}

			if (enableRowBody) {
				String cls = styles.dataTable() + " x-grid-resizer";

				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				sb.append(tpls.tr("", trBuilder.toSafeHtml()));
				sb.appendHtmlConstant("<tr class='" + rowBodyRow
						+ "'><td colspan=" + rowBodyColSpanCount
						+ "><div class='" + rowBody + "'></div></td></tr>");

				SafeHtml tdWrap = null;
				if (!selectable && GXT.isIE()) {
					tdWrap = tpls.tdWrapUnselectable(colCount, "", rowWrap,
							tpls.table(cls, rowStyles, sb.toSafeHtml(),
									renderHiddenHeaders(columnWidths)));
				} else {
					tdWrap = tpls.tdWrap(colCount, "", rowWrap, tpls.table(cls,
							rowStyles, sb.toSafeHtml(),
							renderHiddenHeaders(columnWidths)));
				}
				buf.append(tpls.tr(rowClasses, tdWrap));

			} else {
				buf.append(tpls.tr(rowClasses, trBuilder.toSafeHtml()));
			}

		}
		// end row loop
		return buf.toSafeHtml();

	}
	
	/**
	 * Creates and initializes the column header and saves reference for future
	 * use.
	 */
	@Override
	protected void initHeader() {
		if(header == null) {
			header = new MyColumnHeader(grid, cm, taxonMatrixView.getContainer(), taxonMatrixView) {
				
				/*
				@Override
				protected Menu getContextMenu(int column) {
					return createContextMenu(column);
				}*/
	
				/*@Override
				protected void onColumnSplitterMoved(int colIndex, int width) {
					super.onColumnSplitterMoved(colIndex, width);
					MyGridView.this.onColumnSplitterMoved(colIndex, width);
				}*/
	
				/*@Override
				protected void onHeaderClick(Event ce, int column) {
					super.onHeaderClick(ce, column);
					MyGridView.this.onHeaderClick(column);
				}*/
	
				/*@Override
				protected void onKeyDown(Event ce, int index) {
					ce.stopPropagation();
					// auto select on key down
					if (grid.getSelectionModel() instanceof CellSelectionModel<?>) {
						CellSelectionModel<?> csm = (CellSelectionModel<?>) grid
								.getSelectionModel();
						csm.selectCell(0, index);
					} else {
						grid.getSelectionModel().select(0, false);
					}
				}*/
	
			};
		}
		super.initHeader();
		/*header.setMenuFactory(new HeaderContextMenuFactory() {
		      @Override
		      public Menu getMenuForColumn(int columnIndex) {
		        return createContextMenu(columnIndex);
		      }
		    });
		header.setSplitterWidth(splitterWidth);*/
	}
}
