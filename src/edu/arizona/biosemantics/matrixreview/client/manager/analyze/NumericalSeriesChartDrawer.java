package edu.arizona.biosemantics.matrixreview.client.manager.analyze;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Legend;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
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
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class NumericalSeriesChartDrawer implements ChartDrawer {

	public class NameNumerical {
		private String name;
		private double numerical;

		public NameNumerical(String name, double numerical) {
			super();
			this.name = name;
			this.numerical = numerical;
		}

		public String getName() {
			return name;
		}

		public double getNumerical() {
			return numerical;
		}

		public void setNumerical(Double numerical) {
			this.numerical = numerical;
		}
	}

	public interface NameNumericalAccess extends PropertyAccess<NameNumerical> {
		ValueProvider<NameNumerical, Double> numerical();

		ValueProvider<NameNumerical, String> name();

		@Path("name")
		ModelKeyProvider<NameNumerical> nameKey();
	}

	private static final NameNumericalAccess dataAccess = GWT
			.create(NameNumericalAccess.class);
	private TaxonMatrix taxonMatrix;
	private Character character;

	public NumericalSeriesChartDrawer(TaxonMatrix taxonMatrix,
			Character character) {
		this.taxonMatrix = taxonMatrix;
		this.character = character;
	}

	@Override
	public Widget getChart() {
		// TODO Auto-generated method stub
		final ListStore<NameNumerical> store = new ListStore<NameNumerical>(
				dataAccess.nameKey());

		double max = 0.0;
		for (Taxon taxon : taxonMatrix.getTaxa()) {
			Value value = taxon.get(character);
			if (!value.getValue().isEmpty()) {
				double doubleValue = Double.parseDouble(value.getValue());
				if(doubleValue > max)
					max = doubleValue;
				store.add(new NameNumerical(taxon.getName(), doubleValue));
			}
		}

		final Chart<NameNumerical> chart = new Chart<NameNumerical>();
		chart.setStore(store);
		chart.setShadowChart(false);

		NumericAxis<NameNumerical> axis = new NumericAxis<NameNumerical>();
		axis.setPosition(Position.LEFT);
		axis.addField(dataAccess.numerical());
		TextSprite title = new TextSprite("Value");
		title.setFontSize(18);
		axis.setTitleConfig(title);
		//axis.setMinorTickSteps(1);
		axis.setDisplayGrid(true);
		PathSprite odd = new PathSprite();
		odd.setOpacity(1);
		odd.setFill(new Color("#ddd"));
		odd.setStroke(new Color("#bbb"));
		odd.setStrokeWidth(0.5);
		axis.setGridOddConfig(odd);
		axis.setMinimum(0);
		axis.setMaximum(max);
		chart.addAxis(axis);

		CategoryAxis<NameNumerical, String> catAxis = new CategoryAxis<NameNumerical, String>();
		catAxis.setPosition(Position.BOTTOM);
		catAxis.setField(dataAccess.name());
		title = new TextSprite("Taxon Concept");
		title.setFontSize(18);
		catAxis.setTitleConfig(title);
		chart.addAxis(catAxis);

		final LineSeries<NameNumerical> series = new LineSeries<NameNumerical>();
		series.setYAxisPosition(Position.LEFT);
		series.setYField(dataAccess.numerical());
		series.setStroke(new RGB(194, 0, 36));
		series.setShowMarkers(true);
		Sprite marker = Primitives.square(0, 0, 6);
		marker.setFill(new RGB(194, 0, 36));
		series.setMarkerConfig(marker);
		series.setHighlighting(true);
		chart.addSeries(series);

		FramedPanel panel = new FramedPanel();
		panel.getElement().getStyle().setMargin(10, Unit.PX);
		panel.setCollapsible(true);
		panel.setHeadingText("Line Chart");
		panel.setPixelSize(620, 500);
		panel.setBodyBorder(true);


		final Resizable resize = new Resizable(panel, Dir.E, Dir.SE, Dir.S);
		resize.setMinHeight(400);
		resize.setMinWidth(400);

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

		new Draggable(panel, panel.getHeader()).setUseProxy(false);

		panel.add(chart);

		return panel;
	}

}
