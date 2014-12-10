package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.ScrollDirection;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent.BodyScrollHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.matrixreview.client.matrix.FrozenFirstColumTaxonTreeGrid.TaxaTreeGrid;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class FrozenFirstColumnTreeGrid<M> extends HorizontalLayoutContainer {
	
	private class SynchronizeSelectionHandler<M> implements SelectionHandler<M> {
		
		private Set<SelectionHandler<M>> selectionHandlers = new HashSet<SelectionHandler<M>>();
		
		@Override
		public void onSelection(SelectionEvent<M> event) {
			if(event.getSource().equals(grid.getSelectionModel())) {
				if(!grid.getSelectionModel().getSelection().equals(treeGrid.getSelectionModel().getSelection())) {
					treeGrid.getSelectionModel().setSelection(grid.getSelectionModel().getSelection());
				}
			}
			if(event.getSource().equals(treeGrid.getSelectionModel())) {
				if(!grid.getSelectionModel().getSelection().equals(treeGrid.getSelectionModel().getSelection())) {
					grid.getSelectionModel().setSelection(treeGrid.getSelectionModel().getSelection());
				}
			}
			for(SelectionHandler<M> handler : selectionHandlers) {
				handler.onSelection(event);
			}
		}

		public void addSelectionHandler(SelectionHandler<M> handler) {
			selectionHandlers.add(handler);
		}
		public void removeSelectionHandler(SelectionHandler<M> handler) {
			selectionHandlers.remove(handler);
		}
	}
	
	protected TreeStore<M> store;
	private ColumnConfig<M, ?> treeColumn;
	protected MaintainListStoreTreeGrid<M> treeGrid;
	protected Grid<M> grid;
	private SynchronizeSelectionHandler<M> synchronizeSelectionHandler = new SynchronizeSelectionHandler<M>();
	private boolean initialized = false;
	
	public FrozenFirstColumnTreeGrid(TreeStore<M> treeStore, ColumnConfig<M, ?> treeColumn) {
		this.store = treeStore;
		this.treeColumn = treeColumn;
	}
	
	private void layoutGridsOnContainer() {
		// link scrolling
		treeGrid.addBodyScrollHandler(new BodyScrollHandler() {
			@Override
			public void onBodyScroll(BodyScrollEvent event) {
				grid.getView().getScroller().scrollTo(ScrollDirection.TOP, event.getScrollTop());
			}
		});
		grid.addBodyScrollHandler(new BodyScrollHandler() {
			@Override
			public void onBodyScroll(BodyScrollEvent event) {
				treeGrid.getView().getScroller().scrollTo(ScrollDirection.TOP, event.getScrollTop());
			}
		});		
		
		// remove possibly  undesirable scroll bars 
		//this.getElement().getStyle().setOverflow(Overflow.HIDDEN);
		//getScrollSupport().setScrollMode(ScrollMode.AUTO);

		// add locked column, only 300px wide (in this example, use layouts
		// to change how this works
		HorizontalLayoutData lockedColumnLayoutData = new HorizontalLayoutData(300, 1.0);
		
		//margin for bottom to not get off synchronization with right grid when right grid has bottom scrollbar but left grid does not
		lockedColumnLayoutData.setMargins(new Margins(0, 0, XDOM.getScrollBarWidth(), 0));		
		
		//hide scrollers: Note that child is grabbed at index 0. Even though after full blown rendering the index shown in browser may be different
		//System.out.println(treeGrid.getElement());
		treeGrid.getElement().getChild(0).<XElement> cast().getStyle().setOverflow(Overflow.HIDDEN);
		
		add(treeGrid, lockedColumnLayoutData);

		// add non-locked section, taking up all remaining width
		add(grid, new HorizontalLayoutData(1.0, 1.0));
	}

	public void init(List<ColumnConfig<M, ?>> otherColumnConfigs, GridView<M> gridView) {
		List<ColumnConfig<M, ?>> treeGridColumns = new LinkedList<ColumnConfig<M, ?>>();
		treeGridColumns.add(treeColumn);
		ColumnModel<M> treeGridColumnModel = createTreeGridColumnModel(treeGridColumns);
		this.treeGrid = createTreeGrid(store, treeGridColumnModel, treeColumn);
		this.grid = createGrid(store, treeGrid.getListStore(), createGridColumnModel(otherColumnConfigs), gridView);
		QuickTip gridGuickTip = new QuickTip(grid);
		QuickTip treeGridQuickTip = new QuickTip(treeGrid);
		
		this.treeGrid.getSelectionModel().addSelectionHandler(synchronizeSelectionHandler);
		this.grid.getSelectionModel().addSelectionHandler(synchronizeSelectionHandler);
		
		layoutGridsOnContainer();
		
		this.initialized = true;
	}
	
	protected ColumnModel<M> createTreeGridColumnModel(List<ColumnConfig<M, ?>> columns) {
		return new ColumnModel<M>(columns);
	}
	
	protected ColumnModel<M> createGridColumnModel(List<ColumnConfig<M, ?>> columns) {
		return new ColumnModel<M>(columns);
	}
	
	public void reconfigure(List<? extends ColumnConfig<M, ?>> otherColumnConfigs) {
		List<ColumnConfig<M, ?>> list = new LinkedList<ColumnConfig<M, ?>>(otherColumnConfigs);
		this.grid.reconfigure(treeGrid.getListStore(), createGridColumnModel(list));
	}

	protected MaintainListStoreTreeGrid<M> createTreeGrid(TreeStore<M> store2,
			ColumnModel<M> treeGridColumnModel, ColumnConfig<M, ?> treeColumn) {
		return new MaintainListStoreTreeGrid<M>(store, treeGridColumnModel, treeColumn);
	}

	protected Grid<M> createGrid(TreeStore<M> treeStore, ListStore<M> listStore, ColumnModel<M> columnModel, GridView<M> gridView) {
		return new Grid<M>(listStore, columnModel, gridView);
	}

	public TreeStore<M> getTreeStore() {
		return store;
	}

	public TreeGrid<M> getTreeGrid() {
		return this.treeGrid;
	}
	
	public Grid<M> getGrid() {
		return this.grid;
	}
	
	public void addSelectionHandler(SelectionHandler<M> handler) {
		synchronizeSelectionHandler.addSelectionHandler(handler);
	}

	public boolean isInitialized() {
		return initialized;
	}
}