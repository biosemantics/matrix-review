package edu.arizona.biosemantics.matrixreview.client.desktop;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.desktop.widget.ConsoleManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.DescriptionManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.NumericalSeriesManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.TermFrequencyManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.ConsoleManager.Console;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.AbstractWindowManager;
import edu.arizona.biosemantics.matrixreview.client.event.*;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent.AddCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class DesktopView extends FlowLayoutContainer { //CssFloatLayoutContainer
	
	public class DesktopController {
				
		private Console console;
		
		public DesktopController() {
			// TODO let is show all interesting events - also for debugging
			Window consoleWindow = new Window(false);
			ConsoleManager manager = new ConsoleManager(eventBus, consoleWindow);
			consoleWindow.setWindowManager(manager);
			addWindow(consoleWindow);
			
			addEventHandlers();
		}

		private void addEventHandlers() {
			eventBus.addHandler(ShowDescriptionEvent.TYPE, new ShowDescriptionEvent.ShowDescriptionEventHandler() {
				@Override
				public void onShow(ShowDescriptionEvent event) {
					showDescription(event.getTaxon());
				}
			});
			eventBus.addHandler(AnalyzeCharacterEvent.TYPE, new AnalyzeCharacterEvent.AnalyzeCharacterEventHandler() {
				@Override
				public void onAnalyze(AnalyzeCharacterEvent event) {
					Character character = event.getCharacter();
					switch(character.getControlMode()) {
					case CATEGORICAL:
						showTermFrequencyChart(character);
						break;
					case NUMERICAL:
						showNumericalDistribution(character);
						break;
					case OFF:
						showTermFrequencyChart(character);
						break;
					default:
						break;
					}
				}
			});
		}
				
		protected void showDescription(Taxon taxon) {
			Window descriptionWindow = new Window(false);
			DescriptionManager manager = new DescriptionManager(eventBus, descriptionWindow, taxon, taxonMatrix);
			descriptionWindow.setWindowManager(manager);
			addWindow(descriptionWindow);		
			eventBus.fireEvent(new ShowDesktopEvent());
		}
		
		protected void showTermFrequencyChart(Character character) {
			Window termFrequencyWindow = new Window(true);
			TermFrequencyManager manager = new TermFrequencyManager(eventBus, termFrequencyWindow, taxonMatrix, character);
			termFrequencyWindow.setWindowManager(manager);
			addWindow(termFrequencyWindow);
			eventBus.fireEvent(new ShowTermFrequencyEvent(character));
			eventBus.fireEvent(new ShowDesktopEvent());
		}

		protected void showNumericalDistribution(Character character) {
			Window numericalSeriesWindow = new Window(true);
			NumericalSeriesManager manager = new NumericalSeriesManager(eventBus, numericalSeriesWindow, character, taxonMatrix);
			numericalSeriesWindow.setWindowManager(manager);
			addWindow(numericalSeriesWindow);
			eventBus.fireEvent(new ShowNumericalDistributionEvent(character));
			eventBus.fireEvent(new ShowDesktopEvent());
		}
		
	}
	
	private SimpleEventBus eventBus;
	private int defaultMargin = 5;
	private int widgetId = 0;
	private int marginIncrement = 20;

	private TaxonMatrix taxonMatrix;

	public DesktopView(SimpleEventBus eventBus,TaxonMatrix taxonMatrix) {
		this.eventBus = eventBus; 
		this.taxonMatrix = taxonMatrix;
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
