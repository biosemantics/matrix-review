package edu.arizona.biosemantics.matrixreview.client.desktop;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;

import edu.arizona.biosemantics.matrixreview.client.desktop.widget.ConsoleManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.DescriptionManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.NumericalSeriesManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.TermFrequencyManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.ConsoleManager.Console;
import edu.arizona.biosemantics.matrixreview.client.event.*;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class DesktopView extends FlowLayoutContainer { //CssFloatLayoutContainer
	
	public class DesktopController {
				
		private Console console;
		private Model fullModel;
		private Model subModel;
		
		public DesktopController() {
			// TODO let is show all interesting events - also for debugging
			Window consoleWindow = new Window(false);
			ConsoleManager manager = new ConsoleManager(fullModelBus, subModelBus, consoleWindow);
			consoleWindow.setWindowManager(manager);
			addWindow(consoleWindow);
			
			addEventHandlers();
		}

		private void addEventHandlers() {
			EventBus[] busses = { fullModelBus, subModelBus };
			for(EventBus bus : busses) {
				bus.addHandler(AnalyzeCharacterEvent.TYPE, new AnalyzeCharacterEvent.AnalyzeCharacterEventHandler() {
					@Override
					public void onAnalyze(AnalyzeCharacterEvent event) {
						onAnalyzeCharacter(event);
					}
				});
			}
			
			fullModelBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
				@Override
				public void onLoad(LoadModelEvent event) {
					fullModel = event.getModel();
				}
			});
			subModelBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
				@Override
				public void onLoad(LoadModelEvent event) {
					subModel = event.getModel();
				}
			});
			subModelBus.addHandler(ShowDescriptionEvent.TYPE, new ShowDescriptionEvent.ShowDescriptionEventHandler() {
				@Override
				public void onShow(ShowDescriptionEvent event) {
					showDescription(event.getTaxon());
				}
			});
			fullModelBus.addHandler(ShowDescriptionEvent.TYPE, new ShowDescriptionEvent.ShowDescriptionEventHandler() {
				@Override
				public void onShow(ShowDescriptionEvent event) {
					showDescription(event.getTaxon());
				}
			});
		}
				
		protected void onAnalyzeCharacter(AnalyzeCharacterEvent event) {
			List<Taxon> taxaToConsider = event.getTaxaToConsider() == null ? 
					new ArrayList<Taxon>(fullModel.getTaxonMatrix().getTaxa()) : event.getTaxaToConsider();
			Character character = event.getCharacter();
			switch(fullModel.getControlMode(character)) {
			case CATEGORICAL:
				showTermFrequencyChart(character, taxaToConsider);
				break;
			case NUMERICAL:
				showNumericalDistribution(character, taxaToConsider);
				break;
			case OFF:
				showTermFrequencyChart(character, taxaToConsider);
				break;
			default:
				break;
			}
		}

		protected void showDescription(Taxon taxon) {
			Window descriptionWindow = new Window(false);
			DescriptionManager manager = new DescriptionManager(fullModelBus, subModelBus, descriptionWindow, taxon, fullModel, subModel);
			descriptionWindow.setWindowManager(manager);
			addWindow(descriptionWindow);		
			subModelBus.fireEvent(new ShowDesktopEvent());
		}
		
		protected void showTermFrequencyChart(Character character, List<Taxon> taxa) {
			Window termFrequencyWindow = new Window(true);
			TermFrequencyManager manager = new TermFrequencyManager(fullModelBus, subModelBus, termFrequencyWindow, fullModel, character, taxa);
			termFrequencyWindow.setWindowManager(manager);
			addWindow(termFrequencyWindow);
			subModelBus.fireEvent(new ShowTermFrequencyEvent(character));
			subModelBus.fireEvent(new ShowDesktopEvent());
		}

		protected void showNumericalDistribution(Character character, List<Taxon> taxa) {
			Window numericalSeriesWindow = new Window(true);
			NumericalSeriesManager manager = new NumericalSeriesManager(fullModelBus, subModelBus, numericalSeriesWindow, character, fullModel, taxa);
			numericalSeriesWindow.setWindowManager(manager);
			addWindow(numericalSeriesWindow);
			subModelBus.fireEvent(new ShowNumericalDistributionEvent(character));
			subModelBus.fireEvent(new ShowDesktopEvent());
		}
		
	}
	
	private EventBus fullModelBus;
	private EventBus subModelBus;
	private int defaultMargin = 5;
	private int widgetId = 0;
	private int marginIncrement = 20;

	public DesktopView(EventBus fullModelBus, EventBus subModelBus) {
		this.fullModelBus = fullModelBus;
		this.subModelBus = subModelBus;
		this.setScrollMode(ScrollMode.AUTO);
		
		DesktopController controller = new DesktopController();
	}
		
	public Window addWindow(Window window) {		
		if(this.getWidgetCount() == 0)
			this.add(window, new MarginData(defaultMargin, defaultMargin, defaultMargin, defaultMargin));
		else
			this.add(window, new MarginData(- window.getFramedPanelHeight() + marginIncrement + defaultMargin, defaultMargin, defaultMargin, ++widgetId * marginIncrement + defaultMargin));
		
		final Resizable resize = new Resizable(window, Dir.E, Dir.SE, Dir.S);
		Draggable draggablePanel = new Draggable(window, window.getHeader());
		draggablePanel.setUseProxy(false);
		draggablePanel.setContainer(this);
				
		// resize.setMinHeight(400);
		// resize.setMinWidth(400);

		/*addExpandHandler(new ExpandHandler() {
			@Override
			public void onExpand(ExpandEvent event) {
				resize.setEnabled(true);
			}
		});
		addCollapseHandler(new CollapseHandler() {
			@Override
			public void onCollapse(CollapseEvent event) {
				resize.setEnabled(false);
			}
		});*/
		return window;
	}
}
