package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import java.util.TreeMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.chart.client.chart.Chart;
import com.sencha.gxt.chart.client.chart.Chart.Position;
import com.sencha.gxt.chart.client.chart.axis.CategoryAxis;
import com.sencha.gxt.chart.client.chart.axis.NumericAxis;
import com.sencha.gxt.chart.client.chart.series.BarSeries;
import com.sencha.gxt.chart.client.draw.RGB;
import com.sencha.gxt.chart.client.draw.sprite.TextSprite;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;

import edu.arizona.biosemantics.matrixreview.client.desktop.Window;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class TermFrequencyManager extends AbstractWindowManager {

	public class NameFrequency {
		private String name;
		private int frequency;

		public NameFrequency(String name, int frequency) {
			super();
			this.name = name;
			this.frequency = frequency;
		}

		public String getName() {
			return name;
		}

		public int getFrequency() {
			return frequency;
		}
	}
	
	public interface DataPropertyAccess extends PropertyAccess<NameFrequency> {
		ValueProvider<NameFrequency, Integer> frequency();

		ValueProvider<NameFrequency, String> name();

		@Path("name")
		ModelKeyProvider<NameFrequency> nameKey();
	}

	private static final DataPropertyAccess dataAccess = GWT.create(DataPropertyAccess.class);
	
	private TaxonMatrix taxonMatrix;
	private Character character;

	public TermFrequencyManager(EventBus eventBus, Window window, TaxonMatrix taxonMatrix, Character character) {
		super(eventBus, window);
		this.taxonMatrix = taxonMatrix;
		this.character = character;
		init();
	}

	@Override
	public void refreshContent() {
		// draw bar chart for categorical values and free-text
		// draw curve for numerical values
		TreeMap<String, Integer> counts = new TreeMap<String, Integer>();
		for (Taxon taxon : taxonMatrix.list()) {
			Value value = taxon.get(character);
			if (!counts.containsKey(value.toString()))
				counts.put(value.toString(), 0);
			counts.put(value.toString(), counts.get(value.toString()) + 1);
		}

		int max = 0;
		final ListStore<NameFrequency> store = new ListStore<NameFrequency>(
				dataAccess.nameKey());
		for (String value : counts.keySet()) {
			NameFrequency nameFrequency = new NameFrequency(value,
					counts.get(value));
			store.add(nameFrequency);
			if (nameFrequency.getFrequency() > max)
				max = nameFrequency.getFrequency();
		}

		final Chart<NameFrequency> chart = new Chart<NameFrequency>();
		chart.setStore(store);
		chart.setShadowChart(false);

		NumericAxis<NameFrequency> axis = new NumericAxis<NameFrequency>();
		axis.setPosition(Position.BOTTOM);
		axis.addField(dataAccess.frequency());
		TextSprite title = new TextSprite("Occurences");
		title.setFontSize(18);
		axis.setTitleConfig(title);
		axis.setDisplayGrid(true);
		axis.setMinimum(0);
		axis.setMaximum(max);
		chart.addAxis(axis);

		CategoryAxis<NameFrequency, String> catAxis = new CategoryAxis<NameFrequency, String>();
		catAxis.setPosition(Position.LEFT);
		catAxis.setField(dataAccess.name());
		title = new TextSprite("State");
		title.setFontSize(18);
		catAxis.setTitleConfig(title);
		chart.addAxis(catAxis);

		final BarSeries<NameFrequency> bar = new BarSeries<NameFrequency>();
		bar.setYAxisPosition(Position.BOTTOM);
		bar.addYField(dataAccess.frequency());
		bar.addColor(new RGB(148, 174, 10));
		bar.setHighlighting(true);
		chart.addSeries(bar);
		chart.setAnimated(true);

		window.setWidget(chart);
		window.forceLayout();
		//window.fireEvent(event)
		//window.hide();
		//window.show();
	}

	@Override
	protected void addEventHandlers() {
		subMatrixEventBus.addHandler(SetValueEvent.TYPE, new SetValueEvent.SetValueEventHandler() {
			@Override
			public void onSet(SetValueEvent event) {
				if(event.getOldValue().getCharacter().equals(character)) {
					refreshContent();
				}
			}
		});
		subMatrixEventBus.addHandler(ModifyCharacterEvent.TYPE, new ModifyCharacterEvent.ModifyCharacterEventHandler() {
			@Override
			public void onModify(ModifyCharacterEvent event) {
				if(event.getOldCharacter().equals(character)) {
					refreshTitle();
				}
			}
		});
	}

	@Override
	public void refreshTitle() {
		window.setHeadingText("State Frequency: " + character.toString());
	}

}
