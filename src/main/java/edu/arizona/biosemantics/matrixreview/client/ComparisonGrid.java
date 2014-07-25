package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource.Import;
import com.sencha.gxt.core.client.Style.ScrollDirection;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance.GridResources;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance.GridStyle;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent.BodyScrollHandler;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStateStyles;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CompareViewValueChangedEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;

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
	
	private MaintainListStoreTreeGrid<C> controllerGrid; //contains the store of elements.
	private Grid<C> oldVersionsGrid; //grid displaying 1 column per version. 
	private Grid<C> currentVersionGrid; //editable 1-column grid showing the values of the current version.
	
	private List<SimpleMatrixVersion> oldVersions;
	protected MatrixVersion currentVersion;
	
	private HBoxLayoutContainer content;
	private SimpleContainer controllerGridContent;
	private SimpleContainer oldVersionsContent;
	private SimpleContainer currentVersionContent;
	
	protected T selectedConstant; 
	
	public ComparisonGrid(EventBus eventBus, List<SimpleMatrixVersion> oldVersions, MatrixVersion currentVersion, T selectedConstant){
		this.eventBus = eventBus;
		this.oldVersions = oldVersions;
		this.currentVersion = currentVersion;
		this.selectedConstant = selectedConstant;
	}
	
	/**
	 * Sets up the controller grid and the layout. 
	 */
	public void init(){
		controllerGrid = createControllerGrid();
		controllerGrid.getElement().getChild(0).<XElement> cast().getStyle().setOverflowY(Overflow.HIDDEN); //remove vertical scrollbar.
		controllerGrid.getSelectionModel().setLocked(true); //don't allow selections on the controller.
		controllerGrid.getView().setSortingEnabled(false);
		controllerGrid.getView().setColumnLines(true);
		controllerGrid.getView().setStripeRows(true);
		
		
		controllerGridContent = new SimpleContainer();
		controllerGridContent.setWidth(150);
		controllerGridContent.add(controllerGrid);
		
		oldVersionsContent = new SimpleContainer();
		
		currentVersionContent = new SimpleContainer();
		currentVersionContent.setWidth(100);
		
		content = new HBoxLayoutContainer();
		content.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		Margins margins = new Margins(0, 0, XDOM.getScrollBarWidth()-1, 0);
		BoxLayoutData flex = new BoxLayoutData(new Margins(0, 1, 0, 0));
		flex.setFlex(1);
		content.add(controllerGridContent, new BoxLayoutData(margins));
		content.add(oldVersionsContent, flex);
		content.add(currentVersionContent, new BoxLayoutData(margins));
		
		this.add(content);
		
		updateSelectedConstant(selectedConstant);
		reloadGrids();
		
		addEventHandlers();
	}

	private void addEventHandlers(){
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
	
	private Grid<C> createOldVersionsGrid(MaintainListStoreTreeGrid<C> controlColumn, List<SimpleMatrixVersion> oldVersions){
		List<ColumnConfig<C, String>> columnConfigs = new ArrayList<ColumnConfig<C, String>>();
		
		// create a column for each old matrix version.
		for (SimpleMatrixVersion version: oldVersions){
			ColumnConfig<C, String> column = new ColumnConfig<C, String>(getSimpleVersionValueProvider(version));
			column.setHeader(version.getVersionInfo().getCreated().toString());
			column.setMenuDisabled(true);
			
			columnConfigs.add(column);
		}
		
		List<ColumnConfig<C, ?>> columns = new ArrayList<ColumnConfig<C, ?>>();
		columns.addAll(columnConfigs);
		
		ColumnModel<C> columnModel = new ColumnModel<C>(columns);
		
		Grid<C> grid = new Grid<C>(controlColumn.getListStore(), columnModel, new GridView<C>(new VersionsGridAppearance()){
			@Override
		    protected int getScrollAdjust() { //this removes the extra space on the right meant to hold the scrollbar.
		        return 0;
		    }
		});
		
		CellSelectionModel<C> selectionModel = new CellSelectionModel<C>();
		selectionModel.setSelectionMode(SelectionMode.MULTI);
		grid.setSelectionModel(selectionModel);
		grid.getElement().getChild(0).<XElement> cast().getStyle().setOverflowY(Overflow.HIDDEN); //hide vertical scroll bar. 

		grid.getView().setAutoExpandColumn(columns.get(columns.size()-1));
		grid.getView().setSortingEnabled(false);
		grid.getView().setColumnLines(true);
		grid.getView().setStripeRows(true);
		
		return grid;
	}
	
	private Grid<C> createCurrentVersionGrid(MaintainListStoreTreeGrid<C> controlColumn, MatrixVersion currentVersion){
		ColumnConfig<C, String> column = new ColumnConfig<C, String>(getVersionValueProvider(currentVersion));
		column.setHeader("Current");
		column.setMenuDisabled(true);
		
		List<ColumnConfig<C, ?>> columns = new ArrayList<ColumnConfig<C, ?>>();
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
		
		grid.getView().setSortingEnabled(false);
		grid.getView().setColumnLines(true);
		grid.getView().setStripeRows(true);
		
		return grid;
	}
	
	protected abstract ValueProvider<C, String> getSimpleVersionValueProvider(SimpleMatrixVersion version);
	protected abstract ValueProvider<C, String> getVersionValueProvider(MatrixVersion version);
	protected abstract void changeMatrixValue(C node, String value);
	
	/**
	 * Uses custom css so that the height of the grid rows matches the height of the controller. 
	 */
	public class VersionsGridAppearance extends GridBaseAppearance{
		public VersionsGridAppearance() {
			super(GWT.<CustomGridResources>create(CustomGridResources.class));
		}
	}
	
	public interface CustomGridResources extends GridResources{
		@Import(GridStateStyles.class)
		@Source("CustomGrid.css")
	    GridStyle css();
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
				
				changeMatrixValue(node, value);
				eventBus.fireEvent(new CompareViewValueChangedEvent());
			}
		}
	}
	
}