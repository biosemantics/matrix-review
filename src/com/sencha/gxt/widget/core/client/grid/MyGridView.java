package com.sencha.gxt.widget.core.client.grid;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.HeaderContextMenuFactory;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.ControlMode;
import edu.arizona.biosemantics.matrixreview.client.TaxonMatrixView;
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
						taxonMatrixView.addTaxon(new Taxon("taxon" + i));
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
				if (this.isShowDirtyCells() && r != null
						&& r.getChange(columnConfig.getValueProvider()) != null && !taxonMatrixView.hasComment(model, columnConfig)) {
					cellClasses += " " + cellDirty;
				} else if(this.isShowDirtyCells() && r != null && r.getChange(columnConfig.getValueProvider()) != null && 
						taxonMatrixView.hasComment(model, columnConfig)) {
					cellClasses += " " + cellDirtyCommented;
				} else if(taxonMatrixView.hasComment(model, columnConfig)) {
					cellClasses += " " + cellCommented;
				}
				

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
