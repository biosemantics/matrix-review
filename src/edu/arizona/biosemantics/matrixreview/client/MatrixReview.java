package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

import edu.arizona.biosemantics.matrixreview.shared.IMatrixService;
import edu.arizona.biosemantics.matrixreview.shared.IMatrixServiceAsync;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.state.client.CookieProvider;
import com.sencha.gxt.state.client.GridStateHandler;
import com.sencha.gxt.state.client.StateManager;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.CellSelectionChangedEvent.CellSelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class MatrixReview implements EntryPoint, IsWidget {

	private static final StockProperties props = GWT
			.create(StockProperties.class);

	@Override
	public Widget asWidget() {
		final NumberFormat number = NumberFormat.getFormat("0.00");

		ColumnConfig<Stock, String> nameCol = new ColumnConfig<Stock, String>(
				props.name(), 50,
				SafeHtmlUtils.fromTrustedString("<b>Company</b>"));

		ColumnConfig<Stock, String> symbolCol = new ColumnConfig<Stock, String>(
				props.symbol(), 100, "Symbol");
		ColumnConfig<Stock, Double> lastCol = new ColumnConfig<Stock, Double>(
				props.last(), 75, "Last");

		ColumnConfig<Stock, Double> changeCol = new ColumnConfig<Stock, Double>(
				props.change(), 100, "Change");
		changeCol.setCell(new AbstractCell<Double>() {

			@Override
			public void render(Context context, Double value, SafeHtmlBuilder sb) {
				String style = "style='color: " + (value < 0 ? "red" : "green")
						+ "'";
				String v = number.format(value);
				sb.appendHtmlConstant("<span " + style
						+ " qtitle='Change' qtip='" + v + "'>" + v + "</span>");
			}
		});

		ColumnConfig<Stock, Date> lastTransCol = new ColumnConfig<Stock, Date>(
				props.lastTrans(), 100, "Last Updated");
		lastTransCol.setCell(new DateCell(DateTimeFormat
				.getFormat("MM/dd/yyyy")));

		List<ColumnConfig<Stock, ?>> l = new ArrayList<ColumnConfig<Stock, ?>>();
		l.add(nameCol);
		l.add(symbolCol);
		l.add(lastCol);
		l.add(changeCol);
		l.add(lastTransCol);
		ColumnModel<Stock> cm = new ColumnModel<Stock>(l);

		ListStore<Stock> store = new ListStore<Stock>(props.key());
		store.addAll(this.getStocks());

		ToolButton info = new ToolButton(ToolButton.QUESTION);
		ToolTipConfig config = new ToolTipConfig("Example Info",
				"This examples includes resizable panel, reorderable columns and grid state.");
		config.setMaxWidth(225);
		info.setToolTipConfig(config);

		/*
		 * final Resizable resizable = new Resizable(root, Dir.E, Dir.SE,
		 * Dir.S); root.addExpandHandler(new ExpandHandler() {
		 * 
		 * @Override public void onExpand(ExpandEvent event) {
		 * resizable.setEnabled(true); } }); root.addCollapseHandler(new
		 * CollapseHandler() {
		 * 
		 * @Override public void onCollapse(CollapseEvent event) {
		 * resizable.setEnabled(false); } });
		 */

		final Grid<Stock> grid = new Grid<Stock>(store, cm);
		grid.getView().setAutoExpandColumn(nameCol);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.setBorders(false);

		grid.setColumnReordering(true);
		grid.setStateful(true);
		grid.setStateId("gridExample");

		GridStateHandler<Stock> state = new GridStateHandler<Stock>(grid);
		state.loadState();

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.add(grid, new VerticalLayoutData(1, 1));

		// needed to enable quicktips (qtitle for the heading and qtip for the
		// content) that are setup in the change GridCellRenderer
		new QuickTip(grid);

		return con;
	}

	private static Date randomDate() {
		DateWrapper w = new DateWrapper();
		int r = (int) (Math.random() * 10) * 10;
		w = w.addDays(-r);
		return w.asDate();
	}

	public static List<Stock> getStocks() {
		List<Stock> stocks = new ArrayList<Stock>();

		stocks.add(new Stock("Apple Inc.", "AAPL", 125.64, 123.43, randomDate()));
		stocks.add(new Stock("Cisco Systems, Inc.", "CSCO", 25.84, 26.3,
				randomDate()));
		stocks.add(new Stock("Google Inc.", "GOOG", 516.2, 512.6, randomDate()));
		stocks.add(new Stock("Intel Corporation", "INTC", 21.36, 21.53,
				randomDate()));
		stocks.add(new Stock("Level 3 Communications, Inc.", "LVLT", 5.55,
				5.54, randomDate()));
		stocks.add(new Stock("Microsoft Corporation", "MSFT", 29.56, 29.72,
				randomDate()));
		stocks.add(new Stock("Nokia Corporation (ADR)", "NOK", 27.83, 27.93,
				randomDate()));
		stocks.add(new Stock("Oracle Corporation", "ORCL", 18.73, 18.98,
				randomDate()));
		stocks.add(new Stock("Starbucks Corporation", "SBUX", 27.33, 27.36,
				randomDate()));
		stocks.add(new Stock("Yahoo! Inc.", "YHOO", 26.97, 27.29, randomDate()));
		stocks.add(new Stock("Applied Materials, Inc.", "AMAT", 18.4, 18.66,
				randomDate()));
		stocks.add(new Stock("Comcast Corporation", "CMCSA", 25.9, 26.4,
				randomDate()));
		stocks.add(new Stock("Sirius Satellite", "SIRI", 2.77, 2.74,
				randomDate()));
		stocks.add(new Stock("Tellabs, Inc.", "TLAB", 10.64, 10.75,
				randomDate()));
		stocks.add(new Stock("eBay Inc.", "EBAY", 30.43, 31.21, randomDate()));
		stocks.add(new Stock("Broadcom Corporation", "BRCM", 30.88, 30.48,
				randomDate()));
		stocks.add(new Stock("CMGI Inc.", "CMGI", 2.14, 2.13, randomDate()));
		stocks.add(new Stock("Amgen, Inc.", "AMGN", 56.22, 57.02, randomDate()));
		stocks.add(new Stock("Limelight Networks", "LLNW", 23, 22.11,
				randomDate()));
		stocks.add(new Stock("Amazon.com, Inc.", "AMZN", 72.47, 72.23,
				randomDate()));
		stocks.add(new Stock("E TRADE Financial Corporation", "ETFC", 24.32,
				24.58, randomDate()));
		stocks.add(new Stock("AVANIR Pharmaceuticals", "AVNR", 3.7, 3.52,
				randomDate()));
		stocks.add(new Stock("Gemstar-TV Guide, Inc.", "GMST", 4.41, 4.55,
				randomDate()));
		stocks.add(new Stock("Akamai Technologies, Inc.", "AKAM", 43.08, 45.32,
				randomDate()));
		stocks.add(new Stock("Motorola, Inc.", "MOT", 17.74, 17.69,
				randomDate()));
		stocks.add(new Stock("Advanced Micro Devices, Inc.", "AMD", 13.77,
				13.98, randomDate()));
		stocks.add(new Stock("General Electric Company", "GE", 36.8, 36.91,
				randomDate()));
		stocks.add(new Stock("Texas Instruments Incorporated", "TXN", 35.02,
				35.7, randomDate()));
		stocks.add(new Stock("Qwest Communications", "Q", 9.9, 10.03,
				randomDate()));
		stocks.add(new Stock("Tyco International Ltd.", "TYC", 33.48, 33.26,
				randomDate()));
		stocks.add(new Stock("Pfizer Inc.", "PFE", 26.21, 26.19, randomDate()));
		stocks.add(new Stock("Time Warner Inc.", "TWX", 20.3, 20.45,
				randomDate()));
		stocks.add(new Stock("Sprint Nextel Corporation", "S", 21.85, 21.76,
				randomDate()));
		stocks.add(new Stock("Bank of America Corporation", "BAC", 49.92,
				49.73, randomDate()));
		stocks.add(new Stock("Taiwan Semiconductor", "TSM", 10.4, 10.52,
				randomDate()));
		stocks.add(new Stock("AT&T Inc.", "T", 39.7, 39.66, randomDate()));
		stocks.add(new Stock("United States Steel Corporation", "X", 115.81,
				114.62, randomDate()));
		stocks.add(new Stock("Exxon Mobil Corporation", "XOM", 81.77, 81.86,
				randomDate()));
		stocks.add(new Stock("Valero Energy Corporation", "VLO", 72.46, 72.6,
				randomDate()));
		stocks.add(new Stock("Micron Technology, Inc.", "MU", 12.02, 12.27,
				randomDate()));
		stocks.add(new Stock("Verizon Communications Inc.", "VZ", 42.5, 42.61,
				randomDate()));
		stocks.add(new Stock("Avaya Inc.", "AV", 16.96, 16.96, randomDate()));
		stocks.add(new Stock("The Home Depot, Inc.", "HD", 37.66, 37.79,
				randomDate()));
		stocks.add(new Stock("First Data Corporation", "FDC", 32.7, 32.65,
				randomDate()));
		return stocks;

	}

	@Override
	public void onModuleLoad() {

		/**
		 * The GXT example	
		 */
		/*DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
		dock.addNorth(new HTML("header"), 2);
		dock.addSouth(new HTML("footer"), 2);
		dock.add(asWidget());
		RootLayoutPanel.get().add(dock);
		*/

		/**
		 * The way they layout in the examples: Header stays, however horizontal scrolling fails
		 * due to resize information getting lost somewhere along the way of adding characters -> grid refresh
		 */
		/*IMatrixServiceAsync matrixService = GWT.create(IMatrixService.class);
		// TaxonMatrix taxonMatrix = createSampleMatrix();
		matrixService.getMatrix(new AsyncCallback<TaxonMatrix>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(TaxonMatrix result) {
				TaxonMatrixView taxonMatrixView = new TaxonMatrixView();
				taxonMatrixView.init(result);

				// simulate etc site
				DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
				dock.addNorth(new HTML("header"), 2);
				dock.addSouth(new HTML("footer"), 2);
				VerticalLayoutContainer container = new VerticalLayoutContainer();
				//HorizontalLayoutContainer hcontainer = new HorizontalLayoutContainer();
				container.getElement().getStyle().setBackgroundColor("red");
				container.add(taxonMatrixView,  new VerticalLayoutData(1, 1));
				//hcontainer.add(taxonMatrixView, new HorizontalLayoutData(1, 1));
				dock.add(container);
				
				//ScrollPanel container = new ScrollPanel();
				//container.getElement().getStyle().setBackgroundColor("red");
				//container.add(taxonMatrixView.asWidget());
				//dock.add(container);
				
				// RootPanel.get().add(taxonMatrixView.asWidget());3
				RootLayoutPanel.get().add(dock);
			}
		});*/

		/**
		 * The current for impl. of basic grid funct.
		 */
		IMatrixServiceAsync matrixService = GWT.create(IMatrixService.class);
		// TaxonMatrix taxonMatrix = createSampleMatrix();
		matrixService.getMatrix(new AsyncCallback<TaxonMatrix>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(TaxonMatrix result) {
				TaxonMatrixView taxonMatrixView = new TaxonMatrixView();
				taxonMatrixView.init(result);

				// simulate etc site
				DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
				dock.addNorth(new HTML("header"), 2);
				dock.addSouth(new HTML("footer"), 2);
				dock.add(taxonMatrixView);
				RootLayoutPanel.get().add(dock);
			}
		});
	}

}
