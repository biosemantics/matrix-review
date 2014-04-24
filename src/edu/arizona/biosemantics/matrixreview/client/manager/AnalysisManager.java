package edu.arizona.biosemantics.matrixreview.client.manager;

import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.chart.series.LineSeries;
import com.sencha.gxt.chart.client.chart.series.Primitives;
import com.sencha.gxt.chart.client.chart.series.SeriesHighlighter;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.DrawFx;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.AllAccessListStore;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

import edu.arizona.biosemantics.matrixreview.client.TaxonMatrixView;
import edu.arizona.biosemantics.matrixreview.client.manager.ControlManager.ControlMode;
import edu.arizona.biosemantics.matrixreview.client.manager.analyze.NumericalSeriesChartDrawer;
import edu.arizona.biosemantics.matrixreview.client.manager.analyze.TermFrequencyChartDrawer;
import edu.arizona.biosemantics.matrixreview.client.manager.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.manager.event.DataChangeEventHandler;
import edu.arizona.biosemantics.matrixreview.client.manager.event.ValueChangedEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class AnalysisManager implements DataChangeEventHandler {

	private TaxonMatrixView taxonMatrixView;
	private TaxonMatrix taxonMatrix;
	private AllAccessListStore<Taxon> store;
	private DataManager dataManager;
	private Analysis currentAnalysis;
	private ControlManager controlManager;

	public enum AnalysisType {
		CHARACTER, TAXON, ALL
	}

	public class Analysis {
		private AnalysisType analysisType;
		private int index;

		public Analysis(AnalysisType analysisType, int index) {
			this.analysisType = analysisType;
			this.index = index;
		}

		public AnalysisType getAnalysisType() {
			return analysisType;
		}

		public int getIndex() {
			return index;
		}
	}

	public AnalysisManager(TaxonMatrixView taxonMatrixView,
			TaxonMatrix taxonMatrix, AllAccessListStore<Taxon> store) {
		this.taxonMatrixView = taxonMatrixView;
		this.taxonMatrix = taxonMatrix;
		this.store = store;
	}

	public void setDataManager(DataManager dataManager) {
		this.dataManager = dataManager;
		dataManager.addValueChangeHandler(this);
	}

	public void setControlManager(ControlManager controlManager) {
		this.controlManager = controlManager;
	}

	public void analyze() {
		currentAnalysis = new Analysis(AnalysisType.ALL, -1);
		taxonMatrixView.showFooter();
		taxonMatrixView.setFooterPanel(new Label("analyze"));
	}

	public void analyzeRow(int row) {
		currentAnalysis = new Analysis(AnalysisType.TAXON, row);
		// possibly say how many characters are not filled with are filled? what
		// else there to analyze?
		taxonMatrixView.showFooter();
		taxonMatrixView.setFooterPanel(new Label("analyze row " + row));
	}

	public void analyzeColumn(int col) {
		currentAnalysis = new Analysis(AnalysisType.CHARACTER, col);
		ControlMode controlMode = controlManager.getControlMode(col);
		switch (controlMode) {
		case NUMERICAL:
			showNumericalDistribution(col);
			break;
		case CATEGORICAL:
		case OFF:
		default:
			showTermFrequencyChart(col);
			break;
		}
	}

	private void showNumericalDistribution(int col) {
		NumericalSeriesChartDrawer drawer = new NumericalSeriesChartDrawer(taxonMatrix, dataManager.getCharacter(col));
		Widget chart = drawer.getChart();
		addToFooterPanel(chart, "Numerical Distribution: " + dataManager.getCharacter(col).toString());
	}
	
	public void addToFooterPanel(Widget chart, String title) {
		FramedPanel panel = new FramedPanel();
		panel.getElement().getStyle().setMargin(10, Unit.PX);
		panel.setCollapsible(true);
		Menu menu = new Menu();
		menu.add(new MenuItem("Refresh?"));
		menu.add(new MenuItem("Axis layout??? Sorting?"));
		menu.add(new MenuItem("Delte"));
		panel.setContextMenu(menu);
		panel.setHeadingText(title);
		panel.setPixelSize(300, 300);
		panel.setBodyBorder(true);

		final Resizable resize = new Resizable(panel, Dir.E, Dir.SE, Dir.S);
		//resize.setMinHeight(400);
		//resize.setMinWidth(400);

		panel.addExpandHandler(new ExpandHandler() {
			@Override
			public void onExpand(ExpandEvent event) {
				resize.setEnabled(true);
			}
		});
		panel.addCollapseHandler(new CollapseHandler() {
			@Override
			public void onCollapse(CollapseEvent event) {
				resize.setEnabled(false);
			}
		});

		Draggable draggablePanel = new Draggable(panel, panel.getHeader());
		draggablePanel.setUseProxy(false);
		
		panel.add(chart);
		
		Container footerPanel = taxonMatrixView.getFooterPanel();
		draggablePanel.setContainer(footerPanel);
		
		taxonMatrixView.showFooter();
		footerPanel.add(panel);
	}
		
	private void showTermFrequencyChart(int col) {
		TermFrequencyChartDrawer drawer = new TermFrequencyChartDrawer(taxonMatrix, dataManager.getCharacter(col));
		Widget chart = drawer.getChart();
		addToFooterPanel(chart, "State Frequency: " + dataManager.getCharacter(col).toString());
	}

	@Override
	public void onValueChanged(ValueChangedEvent valueChangedEvent) {
		// TODO refresh analysis
		if(currentAnalysis != null) {
			switch (currentAnalysis.getAnalysisType()) {
			case ALL:
				break;
			case CHARACTER:
				if (currentAnalysis.getIndex() == valueChangedEvent.getColumn()) {
					refreshCurrentAnalysis();
				}
				break;
			case TAXON:
				break;
			default:
				break;
			}
		}
	}

	private void refreshCurrentAnalysis() {
		switch (currentAnalysis.getAnalysisType()) {
		case ALL:
			this.analyze();
			break;
		case CHARACTER:
			this.analyzeColumn(currentAnalysis.getIndex());
			break;
		case TAXON:
			this.analyzeRow(currentAnalysis.getIndex());
			break;
		default:
			break;
		}
	}

	@Override
	public void onAddTaxon(AddTaxonEvent addTaxonEvent) {
		refreshCurrentAnalysis();
	}

}
