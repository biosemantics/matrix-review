package com.sencha.gxt.widget.core.client.grid;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
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
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent.CheckChangeHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.CharacterMenu;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.menu.TaxonCharacterMenu;

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
			final Menu menu = new TaxonCharacterMenu(taxonMatrixView);
			return menu;
		} else {
			final Menu menu = new CharacterMenu(taxonMatrixView, this, colIndex);
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
