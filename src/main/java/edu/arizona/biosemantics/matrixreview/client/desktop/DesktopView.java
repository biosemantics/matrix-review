package edu.arizona.biosemantics.matrixreview.client.desktop;

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
		private Model model;
		
		public DesktopController() {
			// TODO let is show all interesting events - also for debugging
			Window consoleWindow = new Window(false);
			ConsoleManager manager = new ConsoleManager(fullModelBus, subMatrixBus, consoleWindow);
			consoleWindow.setWindowManager(manager);
			addWindow(consoleWindow);
			
			addEventHandlers();
		}

		private void addEventHandlers() {
			fullModelBus.addHandler(AnalyzeCharacterEvent.TYPE, new AnalyzeCharacterEvent.AnalyzeCharacterEventHandler() {
				@Override
				public void onAnalyze(AnalyzeCharacterEvent event) {
					onAnalyzeCharacter(event);
				}
			});
			//or take matrix from subMatrixBus here?
			fullModelBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
				@Override
				public void onLoad(LoadModelEvent event) {
					model = event.getModel();
				}
			});
			subMatrixBus.addHandler(ShowDescriptionEvent.TYPE, new ShowDescriptionEvent.ShowDescriptionEventHandler() {
				@Override
				public void onShow(ShowDescriptionEvent event) {
					showDescription(event.getTaxon());
				}
			});
			subMatrixBus.addHandler(AnalyzeCharacterEvent.TYPE, new AnalyzeCharacterEvent.AnalyzeCharacterEventHandler() {
				@Override
				public void onAnalyze(AnalyzeCharacterEvent event) {
					onAnalyzeCharacter(event);
				}
			});
		}
				
		protected void onAnalyzeCharacter(AnalyzeCharacterEvent event) {
			Model model = event.getModel() == null ? DesktopController.this.model : event.getModel();
			Character character = event.getCharacter();
			switch(model.getControlMode(character)) {
			case CATEGORICAL:
				showTermFrequencyChart(character, model);
				break;
			case NUMERICAL:
				showNumericalDistribution(character, model);
				break;
			case OFF:
				showTermFrequencyChart(character, model);
				break;
			default:
				break;
			}
		}

		protected void showDescription(Taxon taxon) {
			Window descriptionWindow = new Window(false);
			DescriptionManager manager = new DescriptionManager(subMatrixBus, descriptionWindow, taxon, model);
			descriptionWindow.setWindowManager(manager);
			addWindow(descriptionWindow);		
			subMatrixBus.fireEvent(new ShowDesktopEvent());
		}
		
		protected void showTermFrequencyChart(Character character, Model model) {
			Window termFrequencyWindow = new Window(true);
			TermFrequencyManager manager = new TermFrequencyManager(subMatrixBus, termFrequencyWindow, model, character);
			termFrequencyWindow.setWindowManager(manager);
			addWindow(termFrequencyWindow);
			subMatrixBus.fireEvent(new ShowTermFrequencyEvent(character));
			subMatrixBus.fireEvent(new ShowDesktopEvent());
		}

		protected void showNumericalDistribution(Character character, Model model) {
			Window numericalSeriesWindow = new Window(true);
			NumericalSeriesManager manager = new NumericalSeriesManager(subMatrixBus, numericalSeriesWindow, character, model);
			numericalSeriesWindow.setWindowManager(manager);
			addWindow(numericalSeriesWindow);
			subMatrixBus.fireEvent(new ShowNumericalDistributionEvent(character));
			subMatrixBus.fireEvent(new ShowDesktopEvent());
		}
		
	}
	
	private EventBus fullModelBus;
	private EventBus subMatrixBus;
	private int defaultMargin = 5;
	private int widgetId = 0;
	private int marginIncrement = 20;

	public DesktopView(EventBus fullModelBus, EventBus subModelBus) {
		this.fullModelBus = fullModelBus;
		this.subMatrixBus = subModelBus;
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
