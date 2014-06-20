package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent.StoreFilterHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnData;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.grid.RowExpander;

import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.FrozenFirstColumTaxonTreeGrid.CharactersGrid;
import edu.arizona.biosemantics.matrixreview.client.matrix.filters.ScrollStateMaintainer;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.CharacterMenu;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class CharactersGridView extends GridView<Taxon> {

	private ColumnHeaderStyles columnHeaderStyles;
	private EventBus eventBus;
	private TaxonMatrix taxonMatrix;
	
	private ScrollStateMaintainer<Taxon> scrollStateMaintainer = new ScrollStateMaintainer<Taxon>(this);

	public CharactersGridView(EventBus eventBus, TaxonMatrix taxonMatrix) {
		this(eventBus, taxonMatrix, GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class));
	}

	public CharactersGridView(EventBus eventBus, TaxonMatrix taxonMatrix, ColumnHeaderAppearance apperance) {
		this.eventBus = eventBus;
		this.taxonMatrix = taxonMatrix;
		this.columnHeaderStyles = apperance.styles();
		
		this.setColumnLines(true);
		this.setShowDirtyCells(false);
		this.setStripeRows(true);
		
		addEventHandlers();
	}

	@Override
	protected Menu createContextMenu(final int colIndex) {
		CharacterMenu menu = new CharacterMenu(eventBus, (CharactersGrid)grid, taxonMatrix, getColumnModel().getColumn(colIndex).getCharacter());
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
			header = new CharacterColumnHeader(eventBus, taxonMatrix, (CharactersGrid)grid, getColumnModel());
		}
		super.initHeader();
	}
	
	public class SortInfo {
		
		private Character character;
		private SortDir sortDir;

		public SortInfo(Character character, SortDir sortDir) {
			this.character = character;
			this.sortDir = sortDir;
		}
		
		public Character getCharacter() {
			return character;
		}
		
		public SortDir getSortDir() {
			return sortDir;
		}
		
	}
	
	private SortInfo sortInfo;
	
	@Override
	protected void onHeaderClick(int column) {
		this.headerColumnIndex = column;
		if (!headerDisabled && cm.isSortable(column)) {
			Character character = getColumnModel().getCharacterColumns().get(column).getCharacter();
			SortDir sortDir = SortDir.ASC;
			if(sortInfo != null && sortInfo.getCharacter().equals(character)) {
				sortDir = sortInfo.getSortDir().equals(SortDir.ASC) ? SortDir.DESC : SortDir.ASC;
			}
			sortInfo = new SortInfo(character, sortDir);
			eventBus.fireEvent(new SortTaxaByCharacterEvent(character, sortDir));
			updateSortIcon(column, sortDir);
		}
	}
	
	private void addEventHandlers() {
		eventBus.addHandler(SortTaxaByNameEvent.TYPE, new SortTaxaByNameEvent.SortTaxaByNameEventHandler() {
			@Override
			public void onSort(SortTaxaByNameEvent event) {
				getHeader().removeSortIcon();
				sortInfo = null;
			}
		});
		eventBus.addHandler(SortTaxaByCoverageEvent.TYPE, new SortTaxaByCoverageEvent.SortTaxaByCoverageEventHandler() {
			@Override
			public void onSort(SortTaxaByCoverageEvent event) {
				getHeader().removeSortIcon();
				sortInfo = null;
			}
		});
	}

	@Override
	public CharacterColumnHeader getHeader() {
		return (CharacterColumnHeader) header;
	}
	
	@Override
	public CharactersColumnModel getColumnModel() {
		return (CharactersColumnModel)cm;
	}

	public ScrollStateMaintainer getScrollStateMaintainer() {
		return scrollStateMaintainer;
	}

}
