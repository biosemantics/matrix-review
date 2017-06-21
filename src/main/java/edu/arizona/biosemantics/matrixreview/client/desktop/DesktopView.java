package edu.arizona.biosemantics.matrixreview.client.desktop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bfr.client.selection.Selection;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.Alerter;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.ConsoleManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.DescriptionManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.NumericalSeriesManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.TermFrequencyManager;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.ConsoleManager.Console;
import edu.arizona.biosemantics.matrixreview.client.event.*;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class DesktopView extends FlowLayoutContainer { //CssFloatLayoutContainer
	
	public class DesktopController {
				
		private Console console;
		private Model fullModel;
		private Model subModel;
		private int zindex =1;
		
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
			subModelBus.addHandler(ShowSentenceEvent.TYPE, new ShowSentenceEvent.ShowSentenceEventHandler() {
				@Override
				public void onShow(ShowSentenceEvent event) {
					showSentence(event.getTaxon(), event.getValue());
				}
			});
			fullModelBus.addHandler(ShowSentenceEvent.TYPE, new ShowSentenceEvent.ShowSentenceEventHandler() {
				@Override
				public void onShow(ShowSentenceEvent event) {
					showSentence(event.getTaxon(), event.getValue());
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
			
			String taxonName = taxon.getName();
			DescriptionManager manager = descriptionViews.get(taxonName);
			if(manager==null){
				Window descriptionWindow = new Window(false);
				manager = new DescriptionManager(fullModelBus, subModelBus, descriptionWindow, taxon, fullModel, subModel);
				descriptionWindow.setWindowManager(manager);
				descriptionWindow.getElement().getStyle().setZIndex(zindex++);
				addWindow(descriptionWindow);
				
				descriptionViews.put(taxonName,manager);
			}else{
				Window descriptionWindow  = manager.getWindow();
				descriptionWindow.getElement().getStyle().setZIndex(zindex++);
				addWindow(descriptionWindow);
			}
			manager.resetContent(taxon.getDescription());
			subModelBus.fireEvent(new ShowDesktopEvent());
		}
		
		/**
		 * show the description and highlight the values.
		 * @param taxon
		 * @param value
		 */
		protected void showSentence(Taxon taxon, Value value) {
			String taxonName = taxon.getName();
			DescriptionManager manager = descriptionViews.get(taxonName);
			if(manager==null){
				Window descriptionWindow = new Window(false);
				manager = new DescriptionManager(fullModelBus, subModelBus, descriptionWindow, taxon, fullModel, subModel);
				descriptionWindow.setWindowManager(manager);
				addWindow(descriptionWindow);
				descriptionWindow.getElement().getStyle().setZIndex(zindex++);
				descriptionViews.put(taxonName,manager);
			}else{
				Window descriptionWindow  = manager.getWindow();
				descriptionWindow.show();
				descriptionWindow.getElement().getStyle().setZIndex(zindex++);
				//descriptionWindow.setTabIndex(1);
				//addWindow(descriptionWindow);
			}
			
			manager.resetContent(taxon, value);
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
	
	private Map<String, DescriptionManager> descriptionViews = new HashMap();

	public DesktopView(EventBus fullModelBus, EventBus subModelBus) {
		this.fullModelBus = fullModelBus;
		this.subModelBus = subModelBus;
		this.setScrollMode(ScrollMode.AUTO);
		
		DesktopController controller = new DesktopController();
		
		Menu contextMenu = new Menu();
		MenuItem closeItem = new MenuItem("Close all description windows",new SelectionHandler(){

			@Override
			public void onSelection(SelectionEvent event) {
				Set<Entry<String, DescriptionManager>> managers = descriptionViews.entrySet();
				for(Entry<String, DescriptionManager> dm:managers){
					DescriptionManager oneDescManager = dm.getValue();
					oneDescManager.getWindow().hide();
				}
			}
		});
		contextMenu.add(closeItem);	

		this.setContextMenu(contextMenu);
		this.setScrollMode(ScrollMode.AUTOY);
		
		this.setToolTip("Right click to close all description windows");
	}
		
	public Window addWindow(Window window) {
		if(this.getWidgetCount() == 0)
			this.add(window, new MarginData(defaultMargin, defaultMargin, defaultMargin, defaultMargin));
		else
			this.add(window, new MarginData(- window.getFramedPanelHeight() + marginIncrement + defaultMargin, defaultMargin, defaultMargin, ++widgetId * marginIncrement + defaultMargin));
		//int top = window.getAbsoluteTop();
		//Alerter.showAlert("top", this.getAbsoluteTop()+" vs "+window.getAbsoluteTop());
		if(this.getAbsoluteTop()>window.getAbsoluteTop()){
			window.setPosition(window.getAbsoluteLeft(), this.getAbsoluteTop()+defaultMargin);
		}
		
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
	
	public void closeWindow(Window window){
		window.clear();
	}
}
