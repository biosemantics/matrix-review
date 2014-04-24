package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.VerticalSplitPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.VerticalSplitPanel.Resources;
import com.sencha.gxt.core.client.Style.ScrollDirection;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.AllAccessListStore;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.dnd.core.client.MyGridDragSource;
import com.sencha.gxt.dnd.core.client.MyGridDropTarget;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent.BodyScrollHandler;
import com.sencha.gxt.widget.core.client.grid.CharacterColumnConfig;
import com.sencha.gxt.widget.core.client.grid.CharactersColumnModel;
import com.sencha.gxt.widget.core.client.grid.CharactersGrid;
import com.sencha.gxt.widget.core.client.grid.CharactersGridView;
import com.sencha.gxt.widget.core.client.grid.TaxaColumnConfig;
import com.sencha.gxt.widget.core.client.grid.TaxaColumnModel;
import com.sencha.gxt.widget.core.client.grid.TaxaGrid;
import com.sencha.gxt.widget.core.client.grid.TaxaGridView;
import com.sencha.gxt.widget.core.client.tips.QuickTip;

import edu.arizona.biosemantics.matrixreview.client.cells.TaxonCell;
import edu.arizona.biosemantics.matrixreview.client.cells.ValueCell;
import edu.arizona.biosemantics.matrixreview.client.manager.AnalysisManager;
import edu.arizona.biosemantics.matrixreview.client.manager.AnnotationManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ControlManager;
import edu.arizona.biosemantics.matrixreview.client.manager.DataManager;
import edu.arizona.biosemantics.matrixreview.client.manager.ViewManager;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class TaxonMatrixView implements IsWidget {
	
	public static class TaxonModelKeyProvider implements ModelKeyProvider<Taxon> {
		private TaxonMatrix taxonMatrix;
		public TaxonModelKeyProvider(TaxonMatrix taxonMatrix) {
			this.taxonMatrix = taxonMatrix;
		}
		@Override
		public String getKey(Taxon item) {
			return taxonMatrix.getId(item);
		}
	}

	private TaxaGrid taxaGrid;
	private CharactersGrid charactersGrid;
	private DataManager dataManager;
	private ViewManager viewManager;
	private ControlManager controlManager;
	private AnnotationManager annotationManager;
	private AnalysisManager analysisManager;
	
	private FocusPanel footerPanel;
	private SplitLayoutPanel splitLayoutPanel;
	
	public TaxonMatrixView() {

	}
		
	public void init(final TaxonMatrix taxonMatrix) {
		// create store
		AllAccessListStore<Taxon> store = new AllAccessListStore<Taxon>(new TaxonModelKeyProvider(taxonMatrix));
		// yes or no? store.setAutoCommit(false); 
		// this also has influence on
		// the dirty icon that comes out of the box; true wont show?
		// also one may not directly use the model to calculate view related
		// things, such as coverage, because it is not yet represented in model
		// if autocommit is set to false
		store.setAutoCommit(true);
		
		// instantiate managers for different aspects of matrix/grid interaction
		this.dataManager = new DataManager(taxonMatrix, store);		
		this.viewManager = new ViewManager(taxonMatrix, store);
		this.controlManager = new ControlManager(taxonMatrix, store);
		this.annotationManager = new AnnotationManager(taxonMatrix);
		this.analysisManager = new AnalysisManager(this, taxonMatrix, store);
		
		// create grids
		this.charactersGrid = createCharactersGrid(store, dataManager, viewManager, controlManager, annotationManager);
		this.taxaGrid = createTaxaGrid(store, dataManager, viewManager, controlManager, annotationManager, charactersGrid.getView());
		
		// complete wiring of components
		dataManager.setCharactersGrid(charactersGrid);
		dataManager.setTaxaGrid(taxaGrid);
		dataManager.setViewManager(viewManager);
		dataManager.setControlManager(controlManager);
		dataManager.setAnnotationManager(annotationManager);
		dataManager.setTaxonCell(new TaxonCell(dataManager, viewManager, controlManager, annotationManager, analysisManager));
		dataManager.setValueCell(new ValueCell(dataManager, annotationManager));
		dataManager.init();

		viewManager.setCharactersGrid(charactersGrid);
		viewManager.setTaxaGrid(taxaGrid);
		viewManager.setAnnotationManager(annotationManager);
		viewManager.setDataManager(dataManager);
		
		controlManager.setCharactersGrid(charactersGrid);
		controlManager.setTaxaGrid(taxaGrid);
		controlManager.setDataManager(dataManager);
		controlManager.setViewManager(viewManager);
		controlManager.init();
		
		annotationManager.setCharactersGrid(charactersGrid);
		annotationManager.setDataManager(dataManager);
		annotationManager.setViewManager(viewManager);
		
		analysisManager.setDataManager(dataManager);
		analysisManager.setControlManager(controlManager);
		
		store.addStoreHandlers(dataManager);
		controlManager.addEditHandler(dataManager);
	}
	
	private CharactersGrid createCharactersGrid(ListStore<Taxon> store, DataManager dataManager, ViewManager viewManager, ControlManager controlManager,
			AnnotationManager annotationManager) {
		CharactersGridView view = new CharactersGridView(dataManager, viewManager, controlManager, annotationManager, analysisManager);
		view.setShowDirtyCells(false);
		view.setStripeRows(true);
		view.setColumnLines(true);		
		// don't want this feature, want to encourage horizontal scrollbars
		// grid.getView().setAutoExpandColumn(nameCol);
		CharactersGrid grid = new CharactersGrid(store, new CharactersColumnModel(new ArrayList<CharacterColumnConfig>()), view);
		grid.setBorders(false);
		grid.setColumnReordering(true);
		grid.setStateful(true);
		grid.setStateId("charactersGrid");
		QuickTip quickTip = new QuickTip(grid);
		return grid;
	}

	private TaxaGrid createTaxaGrid(ListStore<Taxon> store, DataManager dataManager, ViewManager viewManager, ControlManager controlManager,
			AnnotationManager annotationManager, CharactersGridView charactersGridView) {
		TaxaGridView view = new TaxaGridView(dataManager, viewManager, controlManager, annotationManager, charactersGridView, analysisManager);
		view.setShowDirtyCells(false);// require columns to always fit, preventing scrollbar
		view.setForceFit(true);
		view.setStripeRows(true);
		view.setColumnLines(true);
		TaxaGrid grid = new TaxaGrid(store, new TaxaColumnModel(new ArrayList<TaxaColumnConfig>()), view);
		grid.setBorders(false);
		grid.setColumnReordering(true);
		grid.setStateful(true);
		grid.setStateId("taxaGrid");
		QuickTip quickTip = new QuickTip(grid);
		
		MyGridDragSource<Taxon> dragSource = new MyGridDragSource<Taxon>(grid);
		MyGridDropTarget<Taxon> target = new MyGridDropTarget<Taxon>(grid);
		target.setFeedback(Feedback.INSERT);
		target.setAllowSelfAsSource(true);
		
		return grid;
	}

	@Override
	public Widget asWidget() {
		// link scrolling
		taxaGrid.addBodyScrollHandler(new BodyScrollHandler() {
			@Override
			public void onBodyScroll(BodyScrollEvent event) {
				charactersGrid.getView().getScroller().scrollTo(ScrollDirection.TOP, event.getScrollTop());
			}
		});
		charactersGrid.addBodyScrollHandler(new BodyScrollHandler() {
			@Override
			public void onBodyScroll(BodyScrollEvent event) {
				taxaGrid.getView().getScroller().scrollTo(ScrollDirection.TOP, event.getScrollTop());
			}
		});		
		
		final HorizontalLayoutContainer container = new HorizontalLayoutContainer();
		container.getScrollSupport().setScrollMode(ScrollMode.AUTO);

		// add locked column, only 300px wide (in this example, use layouts
		// to change how this works
		HorizontalLayoutData lockedColumnLayoutData = new HorizontalLayoutData(200, 1.0);
		lockedColumnLayoutData.setMargins(new Margins(0, 0, XDOM.getScrollBarWidth(), 0));

		container.add(taxaGrid, lockedColumnLayoutData);

		// add non-locked section, taking up all remaining width
		container.add(charactersGrid, new HorizontalLayoutData(1.0, 1.0));
		
		splitLayoutPanel = new SplitLayoutPanel();
		footerPanel = new FocusPanel();
		splitLayoutPanel.addSouth(footerPanel, 0);
		/*footerPanel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleFooter();
			}
		});*/
		splitLayoutPanel.add(container);
		return splitLayoutPanel;
	}
	
	public void showFooter() {
		splitLayoutPanel.forceLayout();
		if(splitLayoutPanel.getWidgetSize(footerPanel) == 0) 
			splitLayoutPanel.setWidgetSize(footerPanel, 500);
		splitLayoutPanel.animate(500);
	}
	
	public void toggleFooter() {
		splitLayoutPanel.forceLayout();
		if(splitLayoutPanel.getWidgetSize(footerPanel) == 500) 
			splitLayoutPanel.setWidgetSize(footerPanel, 0);
		else if(splitLayoutPanel.getWidgetSize(footerPanel) == 0) 
			splitLayoutPanel.setWidgetSize(footerPanel, 500);
		splitLayoutPanel.animate(500);
	}

	public void setFooterPanel(IsWidget widget) {
		footerPanel.clear();
		footerPanel.add(widget);
	}
	
}
