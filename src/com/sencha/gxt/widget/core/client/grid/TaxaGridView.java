package com.sencha.gxt.widget.core.client.grid;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.TaxonCharacterMenu;

import edu.arizona.biosemantics.matrixreview.client.manager.AnnotationManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ControlManager;
import edu.arizona.biosemantics.matrixreview.client.manager.DataManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ViewManager;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxaGridView extends GridView<Taxon> {

	private ColumnHeaderStyles columnHeaderStyles;
	private DataManager dataManager;
	private ViewManager viewManager;
	private ControlManager controlManager;
	private AnnotationManager annotationManager;

	public TaxaGridView(DataManager dataManager, ViewManager viewManager, ControlManager controlManager, AnnotationManager annotationManager) {
		this(dataManager, viewManager, controlManager, annotationManager, GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class));
	}

	public TaxaGridView(DataManager dataManager, ViewManager viewManager, ControlManager controlManager, AnnotationManager annotationManager, ColumnHeaderAppearance apperance) {
		this.dataManager = dataManager;
		this.viewManager = viewManager;
		this.controlManager = controlManager;
		this.annotationManager = annotationManager;
		this.columnHeaderStyles = apperance.styles();
		this.scrollOffset = 0;
	}

	protected Menu createContextMenu(final int colIndex) {
		final Menu menu = new TaxonCharacterMenu(dataManager, viewManager, controlManager, annotationManager);
		return menu;
	}

	@Override
	protected SafeHtml doRender(List<ColumnData> cs, List<Taxon> rows, int startRow) {
		final int colCount = cm.getColumnCount();
		final int last = colCount - 1;

		int[] columnWidths = getColumnWidths();

		// root builder
		SafeHtmlBuilder buf = new SafeHtmlBuilder();

		final SafeStyles rowStyles = SafeStylesUtils.fromTrustedString("width: " + getTotalWidth() + "px;");

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
		final String cellInnerClass = styles.cellInner() + " " + states.cellInner();
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

			ListStore<Taxon>.Record r = ds.hasRecord(model) ? ds.getRecord(model) : null;

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
					cellInnerClasses += " " + columnConfig.getColumnTextClassName();
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

				/*
				 * if(i == taxonMatrixView.getTaxonNameColumn()) { String
				 * grandParentStyleClass = columnHeaderStyles.header() + " " +
				 * columnHeaderStyles.head(); cellClasses += " " +
				 * grandParentStyleClass; }
				 */
				/*
				 * if (this.isShowDirtyCells() && r != null &&
				 * r.getChange(columnConfig.getValueProvider()) != null &&
				 * !taxonMatrixView.hasComment(model, columnConfig)) {
				 * cellClasses += " " + cellDirty; } else
				 * if(this.isShowDirtyCells() && r != null &&
				 * r.getChange(columnConfig.getValueProvider()) != null &&
				 * taxonMatrixView.hasComment(model, columnConfig)) {
				 * cellClasses += " " + cellDirtyCommented; } else
				 * if(taxonMatrixView.hasComment(model, columnConfig)) {
				 * cellClasses += " " + cellCommented; }
				 */

				if (viewConfig != null) {
					cellClasses += " " + viewConfig.getColStyle(model, cm.getValueProvider(i), rowIndex, i);
				}

				final SafeStyles cellStyles = columnData.getStyles();

				final SafeHtml tdContent;
				if (enableRowBody && i == 0) {
					tdContent = tpls.tdRowSpan(i, cellClasses, cellStyles, this.getRowBodyRowSpan(), cellInnerClasses, rv);
				} else {
					if (!selectable && GXT.isIE()) {
						tdContent = tpls.tdUnselectable(i, cellClasses, cellStyles, cellInnerClasses, columnConfig.getColumnTextStyle(), rv);
					} else {
						tdContent = tpls.td(i, cellClasses, cellStyles, cellInnerClasses, columnConfig.getColumnTextStyle(), rv);
					}

				}
				trBuilder.append(tdContent);
			}

			if (enableRowBody) {
				String cls = styles.dataTable() + " x-grid-resizer";

				SafeHtmlBuilder sb = new SafeHtmlBuilder();
				sb.append(tpls.tr("", trBuilder.toSafeHtml()));
				sb.appendHtmlConstant("<tr class='" + rowBodyRow + "'><td colspan=" + rowBodyColSpanCount + "><div class='" + rowBody + "'></div></td></tr>");

				SafeHtml tdWrap = null;
				if (!selectable && GXT.isIE()) {
					tdWrap = tpls.tdWrapUnselectable(colCount, "", rowWrap, tpls.table(cls, rowStyles, sb.toSafeHtml(), renderHiddenHeaders(columnWidths)));
				} else {
					tdWrap = tpls.tdWrap(colCount, "", rowWrap, tpls.table(cls, rowStyles, sb.toSafeHtml(), renderHiddenHeaders(columnWidths)));
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
		if (header == null) {
			header = new TaxaColumnHeader(grid, cm, dataManager) {

				/*
				 * @Override protected Menu getContextMenu(int column) { return
				 * createContextMenu(column); }
				 */

				/*
				 * @Override protected void onColumnSplitterMoved(int colIndex,
				 * int width) { super.onColumnSplitterMoved(colIndex, width);
				 * MyGridView.this.onColumnSplitterMoved(colIndex, width); }
				 */

				/*
				 * @Override protected void onHeaderClick(Event ce, int column)
				 * { super.onHeaderClick(ce, column);
				 * MyGridView.this.onHeaderClick(column); }
				 */

				/*
				 * @Override protected void onKeyDown(Event ce, int index) {
				 * ce.stopPropagation(); // auto select on key down if
				 * (grid.getSelectionModel() instanceof CellSelectionModel<?>) {
				 * CellSelectionModel<?> csm = (CellSelectionModel<?>) grid
				 * .getSelectionModel(); csm.selectCell(0, index); } else {
				 * grid.getSelectionModel().select(0, false); } }
				 */

			};
		}
		super.initHeader();
		/*
		 * header.setMenuFactory(new HeaderContextMenuFactory() {
		 * 
		 * @Override public Menu getMenuForColumn(int columnIndex) { return
		 * createContextMenu(columnIndex); } });
		 * header.setSplitterWidth(splitterWidth);
		 */
	}

	@Override
	public TaxaColumnHeader getHeader() {
		return (TaxaColumnHeader) header;
	}
}
