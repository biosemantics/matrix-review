package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.series.LineSeries;
import com.sencha.gxt.chart.client.chart.series.Primitives;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.path.PathSprite;
import com.sencha.gxt.chart.client.draw.sprite.Sprite;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import edu.arizona.biosemantics.matrixreview.client.desktop.Window;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class NumericalSeriesManager extends AbstractWindowManager {

	public class NameNumerical {
		private int id;
		private String name;
		private double numerical;

		public NameNumerical(int id, String name, double numerical) {
			super();
			this.id = id;
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

		public int getId() {
			return id;
		}
	}

	public interface NameNumericalAccess extends PropertyAccess<NameNumerical> {
		ValueProvider<NameNumerical, Double> numerical();

		ValueProvider<NameNumerical, String> name();

		@Path("id")
		ModelKeyProvider<NameNumerical> id();
	}

	private static final NameNumericalAccess dataAccess = GWT
			.create(NameNumericalAccess.class);
	private Model model;
	private Character character;
	private List<Taxon> taxa;

	public NumericalSeriesManager(EventBus fullModelEventBus, EventBus subModelEventBus, Window window, Character character, Model model, List<Taxon> taxa) {
		super(fullModelEventBus, subModelEventBus, window);
		this.model = model;
		this.character = character;
		this.taxa = taxa;
		init();
	}

	@Override
	public void refreshContent() {
		// TODO Auto-generated method stub
		final ListStore<NameNumerical> store = new ListStore<NameNumerical>(
				dataAccess.id());

		double max = 0.0;
		for (Taxon taxon : taxa) {
			Value value = model.getTaxonMatrix().getValue(taxon, character);
			if (!value.getValue().isEmpty()) {
				double doubleValue = Double.parseDouble(value.getValue());
				if(doubleValue > max)
					max = doubleValue;
				store.add(new NameNumerical(taxon.getId(), taxon.getBiologicalName(), doubleValue));
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
		
		window.setWidget(chart);
		window.forceLayout();
	}

	@Override
	protected void addEventHandlers() {
		EventBus[] busses = { this.fullMatrixEventBus, this.subMatrixEventBus };
		for(EventBus bus : busses) {
			bus.addHandler(SetValueEvent.TYPE, new SetValueEvent.SetValueEventHandler() {
				@Override
				public void onSet(SetValueEvent event) {
					if(event.getCharacters().contains(character)) {
						Timer timer = new Timer() {
							@Override
							public void run() {
								refreshContent();
							}
						};
						timer.schedule(10);
					}
				}
			});
			bus.addHandler(ModifyCharacterEvent.TYPE, new ModifyCharacterEvent.ModifyCharacterEventHandler() {
				@Override
				public void onModify(ModifyCharacterEvent event) {
					if(event.getOldCharacter().equals(character)) {
						refreshTitle();
					}
				}
			});
		}
	}

	@Override
	public void refreshTitle() {
		window.setHeading("Numerical Distribution: " + character.toString());
	}

}
