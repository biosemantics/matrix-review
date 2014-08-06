package edu.arizona.biosemantics.matrixreview.client.compare;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.Style.ScrollDirection;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent.BodyScrollHandler;
import com.sencha.gxt.widget.core.client.event.CellClickEvent;
import com.sencha.gxt.widget.core.client.event.CellClickEvent.CellClickHandler;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.ViewReadyEvent.ViewReadyHandler;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;

import edu.arizona.biosemantics.matrixreview.client.compare.ComparisonGridCell.CustomGridResources;
import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CompareViewValueChangedEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleTaxonMatrix;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.VersionInfo;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

/**
 * The ComparisonGrid class is a widget used for comparing data values across multiple versions. 
 * 
 * It contains 3 separate grids. On the left is the 'controller' grid that contains the store of
 * elements. In the middle is a grid containing one column per version being compared. On the right
 * is an editable 1-column grid showing the values of the current version. 
 * 
 * 
 * There are really 3 data categories we want to display (e.g. Character/Taxon/Version). 
 * But since we cannot illustrate that very nicely, instead we choose a single value from one 
 * category and display a graph of the other two. 
 * 
 * For example, we choose a single Character and display a Taxon/Version
 * graph showing the value for that Character for each Taxon across all versions. Conversely, 
 * we could use a Taxon as the 'constant' and display a Character/Version graph. 
 * 
 * 
 * When a different 'constant' value is changed, the graph is reloaded using the new value as
 * the selected constant. 
 * (To notify this class that the constant has changed, fire a ChangeComparingSelectionEvent on 
 * the eventBus passed to the constructor.) 
 * 
 * 
 * @author Andrew Stockton
 *
 * @param <T> The type of the 'constant' category. 
 * @param <C> The type of the other category. (Will be the y-axis in the graph.) 
 */

public abstract class ComparisonGrid<T, C> extends ContentPanel{
	protected EventBus eventBus;
	
	protected MaintainListStoreTreeGrid<C> controllerGrid; //contains the store of elements.
	protected Grid<C> oldVersionsGrid; //grid displaying 1 column per version. 
	protected Grid<C> currentVersionGrid; //editable 1-column grid showing the values of the current version.
	
	private List<SimpleMatrixVersion> oldVersions;
	protected MatrixVersion currentVersion;
	private List<List<CellIdentifier>> changedCells;
	private List<CellIdentifier> currentVersionChangedCells;
	
	private HBoxLayoutContainer content;
	private SimpleContainer controllerGridContent;
	private SimpleContainer oldVersionsContent;
	private SimpleContainer currentVersionContent;
	
	private boolean markChangedCells;
	
	protected T selectedConstant;
	protected C selectedRow;
	
	private Margins controllerMargins;
	private Margins oldVersionsMargins;
	private Margins currentVersionMargins;
	
	private static int COLUMN_WIDTH = 100;

	
	public ComparisonGrid(EventBus eventBus, List<SimpleMatrixVersion> oldVersions, MatrixVersion currentVersion, T selectedConstant){
		this.eventBus = eventBus;
		this.oldVersions = oldVersions;
		this.currentVersion = currentVersion;
		this.selectedConstant = selectedConstant;
		
		this.changedCells = new ArrayList<List<CellIdentifier>>(oldVersions.size());
		changedCells.add(new LinkedList<CellIdentifier>()); //add a dummy list for the first column - it will never have any changes. 
		for (int i = 0; i < oldVersions.size()-1; i++){
			SimpleMatrixVersion version1 = oldVersions.get(i);
			SimpleMatrixVersion version2 = oldVersions.get(i+1);
			changedCells.add(getChangedCells(version1, version2));
		}
		
		this.currentVersionChangedCells = new ArrayList<CellIdentifier>();
		updateCurrentVersionChangedCells();
	}

	/**
	 * Sets up the layout. 
	 */
	public void init(){
		controllerGrid = createControllerGrid();
		controllerGrid.getElement().getChild(0).<XElement> cast().getStyle().setOverflowY(Overflow.HIDDEN); //remove vertical scrollbar.
		controllerGrid.getSelectionModel().setSelectionMode(SelectionMode.SIMPLE);
	//	controllerGrid.getSelectionModel().setLocked(true); //don't allow selections on the controller.
		controllerGrid.getView().setSortingEnabled(false);
		controllerGrid.getView().setColumnLines(true);
		controllerGrid.getView().setStripeRows(true);
		controllerGrid.addCellClickHandler(new CellClickHandler(){
			@Override
			public void onCellClick(CellClickEvent event) {
				ListStore<C> store = controllerGrid.getStore();
				C item = store.get(event.getRowIndex());
				selectedRow = item;
				eventBus.fireEvent(event);
			}	
		});
		controllerGrid.addViewReadyHandler(new ViewReadyHandler(){
			@Override
			public void onViewReady(ViewReadyEvent event) {
				controllerGrid.expandAll();
			}
		});
		
		
		controllerGridContent = new SimpleContainer();
		controllerGridContent.setWidth(150);
		controllerGridContent.add(controllerGrid);
		
		oldVersionsContent = new SimpleContainer();
		
		currentVersionContent = new SimpleContainer();
		currentVersionContent.setWidth(100);
		
		content = new HBoxLayoutContainer();
		content.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		controllerMargins = new Margins(0, 0, 0, 0);
		oldVersionsMargins = new Margins(0, 0, 0, 0);
		currentVersionMargins = new Margins(0, 0, XDOM.getScrollBarWidth()-1, 0);
		BoxLayoutData flex = new BoxLayoutData(oldVersionsMargins);
		flex.setFlex(1);
		content.add(controllerGridContent, new BoxLayoutData(controllerMargins));
		content.add(oldVersionsContent, flex);
		content.add(currentVersionContent, new BoxLayoutData(currentVersionMargins));
		
		this.add(content);
		
		updateSelectedConstant(selectedConstant);
		reloadGrids();
		
		addEventHandlers();
	}

	protected void addEventHandlers(){
		/**
		 * ChangeComparingSelectionEvent
		 * Fired when the selected constant has been changed and the grids should be reloaded 
		 * using the new value. 
		 */
		eventBus.addHandler(ChangeComparingSelectionEvent.TYPE, new ChangeComparingSelectionEvent.ChangeComparingSelectionEventHandler() {
			@Override
			public void onChange(ChangeComparingSelectionEvent event) {
				try{
					@SuppressWarnings("unchecked")
					T selection = (T)event.getSelection(); //assume that the new value is of type T. If not, it is ignored. 
					updateSelectedConstant(selection);
					reloadGrids();
				}catch(Exception e){}
			}
		});
		
		/**
		 * CompareViewValueChangedEvent
		 * A value in the current version matrix has changed and the view should be refreshed. 
		 */
		eventBus.addHandler(CompareViewValueChangedEvent.TYPE, new CompareViewValueChangedEvent.CompareViewValueChangedEventHandler() {
			@Override
			public void onCompareViewValueChanged(CompareViewValueChangedEvent event) {
				updateCurrentVersionChangedCells();
				currentVersionGrid.getView().refresh(false);
			}
		});
		
		/**
		 * A scroll listener - when the 'current version' grid scrolls, scroll the other two grids
		 * to the same spot. 
		 */
		eventBus.addHandler(BodyScrollEvent.getType(), new BodyScrollHandler(){
			@Override
			public void onBodyScroll(BodyScrollEvent event) {
				final int yPosition = event.getScrollTop();
				controllerGrid.getView().getScroller().scrollTo(ScrollDirection.TOP, yPosition);
				oldVersionsGrid.getView().getScroller().scrollTo(ScrollDirection.TOP, yPosition);
			}
		});
	}

	/**
	 * Simply refreshes the label, maybe something like "Currently viewing character: ______."
	 * 
	 * @param constant The selected constant. 
	 */
	protected abstract void setHeading(T constant);
	
	/**
	 * Sets the value of 'selectedConstant' to a new constant value and then updates the heading.
	 * Can be overridden in child classes to ensure that only certain values are selected. (For 
	 * instance, that T is a leaf node and not a folder node.)
	 * 
	 * @param constant The selected constant.
	 */
	protected void updateSelectedConstant(T constant){
		selectedConstant = constant;
		setHeading(constant);
	}
	
	/**
	 * Reloads the old version grids and current version grids. 
	 */
	private void reloadGrids(){
		//TODO: save scroller x and y and set them again after adding the rows. 
		oldVersionsContent.clear();
		
		oldVersionsGrid = createOldVersionsGrid(controllerGrid, oldVersions);
		oldVersionsContent.add(oldVersionsGrid);
		
		currentVersionContent.clear();
		currentVersionGrid = createCurrentVersionGrid(controllerGrid, currentVersion);
		currentVersionContent.add(currentVersionGrid);
		
		this.forceLayout();
	}

	protected abstract MaintainListStoreTreeGrid<C> createControllerGrid();
	
	protected Grid<C> createOldVersionsGrid(MaintainListStoreTreeGrid<C> controlColumn, final List<SimpleMatrixVersion> oldVersions){
		List<ColumnConfig<C, String>> columnConfigs = new ArrayList<ColumnConfig<C, String>>();
		
		DateTimeFormat headingDateFormat = DateTimeFormat.getFormat("d MMM hh:mm aaa");
		DateTimeFormat tooltipDateFormat = DateTimeFormat.getFormat("EEE, MMM d, yyyy  hh:mm aaa");
		
		// create a column for each old matrix version.
		for (int i = 0; i < oldVersions.size(); i++){
			SimpleMatrixVersion version = oldVersions.get(i);
			ColumnConfig<C, String> column = new ColumnConfig<C, String>(getSimpleVersionValueProvider(version), COLUMN_WIDTH);
			VersionInfo info = version.getVersionInfo();
			column.setHeader(headingDateFormat.format(info.getCreated()));
			String tooltip = "<b>" + tooltipDateFormat.format(info.getCreated()) + "</b><br><br>"
					+ "<b>author: </b>" + info.getAuthor() + "<br>"
					+ "<b>comment: </b>" + info.getComment();
			column.setToolTip(SafeHtmlUtils.fromTrustedString(tooltip));
			column.setMenuDisabled(true);
			column.setCell(new ComparisonGridCell(this, changedCells.get(i)));
			columnConfigs.add(column);
		}
		
		List<ColumnConfig<C, ?>> columns = new ArrayList<ColumnConfig<C, ?>>();
		columns.addAll(columnConfigs);
		
		ColumnModel<C> columnModel = new ColumnModel<C>(columns);
		
		final Grid<C> grid = new Grid<C>(controlColumn.getListStore(), columnModel, new GridView<C>(new VersionsGridAppearance()){
			@Override
		    protected int getScrollAdjust() { //this removes the extra space on the right meant to hold the scrollbar.
		        return 0;
		    }
			/*
			@Override
			protected void calculateVBar(boolean force){
				this.vbar = true;
			}*/
		});
		grid.addCellClickHandler(new CellClickHandler(){
			@Override
			public void onCellClick(CellClickEvent event) {
				ListStore<C> store = grid.getStore();
				C item = store.get(event.getRowIndex());
				selectedRow = item;
				eventBus.fireEvent(event);
			}	
		});
		grid.addCellDoubleClickHandler(new CellDoubleClickHandler(){
			@Override
			public void onCellClick(CellDoubleClickEvent event) {
				int versionIndex = event.getCellIndex();
				
				ListStore<C> store = grid.getStore();
				C item = store.get(event.getRowIndex());
				
				Value value = getValue(oldVersions.get(versionIndex), selectedConstant, item);
				if (value != null){
					changeMatrixValue(item, value.getValue(), true);
					eventBus.fireEvent(new CompareViewValueChangedEvent());
				} 
			}
		});
		new QuickTip(grid); //register a quick tip manager with this grid.
		
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getElement().getChild(0).<XElement> cast().getStyle().setOverflowY(Overflow.HIDDEN); //hide vertical scroll bar.
		
		
		grid.getView().setAutoExpandColumn(columns.get(columns.size()-1));
		grid.getView().setAutoExpandMin(COLUMN_WIDTH);
		grid.getView().setSortingEnabled(false);
		grid.getView().setColumnLines(true);
		grid.getView().setStripeRows(true);
		
		return grid;
	}
	
	protected abstract Value getValue(SimpleMatrixVersion simpleMatrixVersion, T selectedConstant2, C item);

	protected Grid<C> createCurrentVersionGrid(MaintainListStoreTreeGrid<C> controlColumn, MatrixVersion currentVersion){
		ColumnConfig<C, String> column = new ColumnConfig<C, String>(getVersionValueProvider(currentVersion));
		column.setHeader("Current");
		column.setMenuDisabled(true);
		
		List<ColumnConfig<C, ?>> columns = new ArrayList<ColumnConfig<C, ?>>();
		column.setCell(new ComparisonGridCell(this, currentVersionChangedCells));
		columns.add(column);
		
		ColumnModel<C> columnModel = new ColumnModel<C>(columns);
		
		Grid<C> grid = new Grid<C>(controlColumn.getListStore(), columnModel, new GridView<C>(new VersionsGridAppearance()));
		
		final CurrentVersionEditing editing = new CurrentVersionEditing(eventBus, grid, currentVersion);
		editing.addEditor(column, new TextField());
		
		grid.addBodyScrollHandler(new BodyScrollHandler(){
			@Override
			public void onBodyScroll(BodyScrollEvent event) {
				eventBus.fireEvent(event);
			}
		});
		
		//hide scrollers: Note that child is grabbed at index 0. Even though after full blown rendering the index shown in browser may be different
		grid.getElement().getChild(0).<XElement> cast().getStyle().setOverflowX(Overflow.HIDDEN);
		grid.getSelectionModel().setLocked(true);
		grid.getView().setSortingEnabled(false);
		grid.getView().setColumnLines(true);
		grid.getView().setStripeRows(true);
		
		return grid;
	}
	
	protected abstract ValueProvider<C, String> getSimpleVersionValueProvider(SimpleMatrixVersion version);
	protected abstract ValueProvider<C, String> getVersionValueProvider(MatrixVersion version);
	protected abstract void changeMatrixValue(C node, String value, boolean allowEditMovedTaxon);
	
	public boolean getMarkChangedCells(){
		return markChangedCells;
	}
	
	public void setMarkChangedCells(boolean markChangedCells){
		this.markChangedCells = markChangedCells;
	}
	
	protected abstract List<CellIdentifier> getChangedCells(SimpleMatrixVersion version1, SimpleMatrixVersion version2);
	
	private void updateCurrentVersionChangedCells() {
		currentVersionChangedCells.clear();
		
		SimpleMatrixVersion current = new SimpleMatrixVersion(new SimpleTaxonMatrix(currentVersion.getTaxonMatrix()), currentVersion.getVersionInfo());
		
		currentVersionChangedCells.addAll(getChangedCells(oldVersions.get(oldVersions.size()-1), current));
	}
	
	public T getSelectedConstant(){
		return selectedConstant;
	}
	
	public void refresh(){
		oldVersionsGrid.getView().refresh(true);
		currentVersionGrid.getView().refresh(true);
	}
	
	/**
	 * Uses custom css so that the height of the grid rows matches the height of the controller. 
	 */
	public class VersionsGridAppearance extends GridBaseAppearance{
		//private ColumnHeaderStyles columnHeaderStyles;
		
		public VersionsGridAppearance() {
			super(GWT.<CustomGridResources>create(CustomGridResources.class));
			//this.columnHeaderStyles = GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class).styles();
		}
		//TODO: make this render a cell as 'greyed out' (header style?) if there is no data for that cell. 
	}
	
	/**
	 * Allows editing to be done on the current version grid. 
	 * However, rather than actually completing the editing on the grid, requests that the child
	 * change the matrix based on the T and C data, then fires an event to refresh the current
	 * version grid view. 
	 */
	private class CurrentVersionEditing extends GridInlineEditing<C>{
		
		public CurrentVersionEditing(EventBus eventBus, Grid<C> editableGrid, MatrixVersion version) {
			super(editableGrid);
		}

		protected <N, O> void doCompleteEditing() {
			if (activeCell != null) {

				ListStore<C> store = getEditableGrid().getStore();
				ListStore<C>.Record r = store.getRecord(store.get(activeCell.getRow()));
				C node = r.getModel();
				
				Field<O> field = getEditor(columnModel.getColumn(activeCell.getCol()));
				String value = (String)((ValueBaseField<O>) field).getCurrentValue();
				
				changeMatrixValue(node, value, false);
				eventBus.fireEvent(new CompareViewValueChangedEvent());
			}
		}
		
	}
}

class CellIdentifier{
	private Object selectedConstant;
	private Object key;
	
	public CellIdentifier(Object selectedConstant, Object key) {
		super();
		this.selectedConstant = selectedConstant;
		this.key = key;
	}
	
	public Object getSelectedConstant(){
		return selectedConstant;
	}
	
	public void setSelectedConsant(Object selectedConstant){
		this.selectedConstant = selectedConstant;
	}

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}
	
	@Override
	public boolean equals(Object o){
		try{
			CellIdentifier other = (CellIdentifier)o;
			if (other.getSelectedConstant().equals(this.selectedConstant) && other.getKey().equals(this.key))
					return true;
		}catch(Exception e){ 
			return false;
		}
		return false;
	}
}
