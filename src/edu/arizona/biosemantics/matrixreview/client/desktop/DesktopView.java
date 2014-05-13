package edu.arizona.biosemantics.matrixreview.client.desktop;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.fx.client.Draggable;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.Resizable.Dir;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.desktop.widget.ConsoleCreator;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.DescriptionCreator;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.NumericalSeriesChartCreator;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.TermFrequencyChartCreator;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.ConsoleCreator.Console;
import edu.arizona.biosemantics.matrixreview.client.desktop.widget.WidgetCreator;
import edu.arizona.biosemantics.matrixreview.client.event.*;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent.AddCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class DesktopView extends FlowLayoutContainer { //CssFloatLayoutContainer

	private class ConsoleEventsHandler implements AddCharacterEventHandler, AddColorEvent.AddColorEventHandler, 
		AddTaxonEvent.AddTaxonEventHandler, AnalyzeCharacterEvent.AnalyzeCharacterEventHandler, HideCharacterEvent.HideCharacterEventHandler, 
		HideTaxonEvent.HideCharacterEventHandler, LoadTaxonMatrixEvent.LoadTaxonMatrixEventHandler, LockCharacterEvent.LockCharacterEventHandler,
		LockMatrixEvent.LockMatrixEventHandler, RemoveColorsEvent.RemoveColorsEventHandler,
		MergeCharactersEvent.MergeCharactersEventHandler, MoveCharacterEvent.MoveCharacterEventHandler, MoveTaxonEvent.MoveTaxonEventHandler, 
		RemoveCharacterEvent.RemoveCharacterEventHandler, RemoveTaxonEvent.RemoveTaxonEventHandler, ModifyCharacterEvent.ModifyCharacterEventHandler, 
		ModifyTaxonEvent.ModifyTaxonEventHandler, SetCharacterColorEvent.SetCharacterColorEventHandler, SetCharacterCommentEvent.SetCharacterCommentEventHandler, 
		SetControlModeEvent.SetControlModeEventHandler, SetTaxonColorEvent.SetTaxonColorEventHandler, SetTaxonCommentEvent.SetTaxonCommentEventHandler, 
		ShowDesktopEvent.ShowDesktopEventHandler, ShowNumericalDistributionEvent.ShowNumericalDistributionEventHandler, 
		ShowTermFrequencyEvent.ShowTermFrequencyEventHandler, SortCharactersByCoverageEvent.SortCharatersByCoverageEventHandler, 
		SortCharactersByNameEvent.SortCharatersByNameEventHandler, SortCharactersByOrganEvent.SortCharatersByOrganEventHandler, 
		SortTaxaByCharacterEvent.SortTaxaByCharacterEventHandler, SortTaxaByCoverageEvent.SortTaxaByCoverageEventHandler, 
		SortTaxaByNameEvent.SortTaxaByNameEventHandler, ToggleDesktopEvent.ToggleDesktopEventHandler, 
		AnalyzeTaxonEvent.AnalyzeTaxonEventHandler, LockTaxonEvent.LockCharacterEventHandler, 
		ShowDescriptionEvent.ShowDescriptionEventHandler, SetValueCommentEvent.SetValueCommentEventHandler, 
		SetValueColorEvent.SetValueColorEventHandler, SetValueEvent.SetValueEventHandler, ModelModeEvent.ModelModeEventHandler
		{

		@Override
		public void onAdd(AddCharacterEvent event) {
			printToConsole(event);
		}
		
		private void printToConsole(GwtEvent event) {
			if(event instanceof PrintableEvent)
				appendConsole(((PrintableEvent) event).print());
			else
				appendConsole(event.toDebugString());
		}

		@Override
		public void onToggle(ToggleDesktopEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSort(SortTaxaByNameEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSort(SortTaxaByCoverageEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSort(SortTaxaByCharacterEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSort(SortCharactersByOrganEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSort(SortCharactersByNameEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSort(SortCharactersByCoverageEvent event) {
			printToConsole(event);
		}

		@Override
		public void onShow(ShowTermFrequencyEvent event) {
			printToConsole(event);
		}

		@Override
		public void onShow(ShowNumericalDistributionEvent event) {
			printToConsole(event);
		}

		@Override
		public void onShow(ShowDesktopEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSet(SetTaxonCommentEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSet(SetTaxonColorEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSet(SetControlModeEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSet(SetCharacterCommentEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSet(SetCharacterColorEvent event) {
			printToConsole(event);
		}

		@Override
		public void onModify(ModifyTaxonEvent event) {
			printToConsole(event);
		}

		@Override
		public void onRename(ModifyCharacterEvent event) {
			printToConsole(event);
		}

		@Override
		public void onRemove(RemoveTaxonEvent event) {
			printToConsole(event);
		}

		@Override
		public void onRemove(RemoveCharacterEvent event) {
			printToConsole(event);
		}

		@Override
		public void onMove(MoveTaxonEvent event) {
			printToConsole(event);
		}

		@Override
		public void onMove(MoveCharacterEvent event) {
			printToConsole(event);
		}

		@Override
		public void onMerge(MergeCharactersEvent event) {
			printToConsole(event);
		}

		@Override
		public void onLock(LockCharacterEvent event) {
			printToConsole(event);
		}

		@Override
		public void onLoad(LoadTaxonMatrixEvent event) {
			printToConsole(event);
		}

		@Override
		public void onHide(HideTaxonEvent event) {
			printToConsole(event);
		}

		@Override
		public void onHide(HideCharacterEvent event) {
			printToConsole(event);
		}

		@Override
		public void onAnalyze(AnalyzeCharacterEvent event) {
			printToConsole(event);
		}

		@Override
		public void onAdd(AddTaxonEvent event) {
			printToConsole(event);
		}

		@Override
		public void onAdd(AddColorEvent event) {
			printToConsole(event);
		}

		@Override
		public void onRemove(RemoveColorsEvent event) {
			printToConsole(event);
		}

		@Override
		public void onLock(LockMatrixEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSet(SetValueColorEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSet(SetValueCommentEvent event) {
			printToConsole(event);
		}

		@Override
		public void onShow(ShowDescriptionEvent event) {
			printToConsole(event);
		}

		@Override
		public void onLock(LockTaxonEvent event) {
			printToConsole(event);
		}

		@Override
		public void onAnalyze(AnalyzeTaxonEvent event) {
			printToConsole(event);
		}

		@Override
		public void onSet(SetValueEvent event) {
			printToConsole(event);
		}

		@Override
		public void onMode(ModelModeEvent event) {
			printToConsole(event);
		}
		
	}
	
	private SimpleEventBus eventBus;
	private Console console;
	private int defaultMargin = 5;
	private int widgetId = 0;
	private int marginIncrement = 20;
	private int framedPanelWidth = 300;
	private int framedPanelHeight = 300;

	private TaxonMatrix taxonMatrix;

	public DesktopView(SimpleEventBus eventBus,TaxonMatrix taxonMatrix) {
		this.eventBus = eventBus; 
		this.taxonMatrix = taxonMatrix;
		this.setScrollMode(ScrollMode.AUTO);

		// TODO let is show all interesting events - also for debugging
		ConsoleCreator creator = new ConsoleCreator();
		console = creator.create();
		addAsFramedPanel(creator, "Console");		
		addEventHandlers();
	}

	private void addEventHandlers() {
		addConsoleHandlers();
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
		WidgetCreator creator = new DescriptionCreator(eventBus, taxon, taxonMatrix);
		String title = "Description: " + taxon.getFullName();
		addAsFramedPanel(creator, title);		
		eventBus.fireEvent(new ShowDesktopEvent());
	}

	private void addConsoleHandlers() {
		ConsoleEventsHandler handler = new ConsoleEventsHandler();
		eventBus.addHandler(AddCharacterEvent.TYPE, handler);
		eventBus.addHandler(AddColorEvent.TYPE, handler);
		eventBus.addHandler(AddTaxonEvent.TYPE, handler);
		eventBus.addHandler(AnalyzeCharacterEvent.TYPE, handler);
		eventBus.addHandler(HideCharacterEvent.TYPE, handler);
		eventBus.addHandler(HideTaxonEvent.TYPE, handler);
		eventBus.addHandler(LoadTaxonMatrixEvent.TYPE, handler);
		eventBus.addHandler(LockCharacterEvent.TYPE, handler);
		eventBus.addHandler(LockMatrixEvent.TYPE, handler);
		eventBus.addHandler(MergeCharactersEvent.TYPE, handler);
		eventBus.addHandler(MoveCharacterEvent.TYPE, handler);
		eventBus.addHandler(MoveTaxonEvent.TYPE, handler);
		eventBus.addHandler(RemoveCharacterEvent.TYPE, handler);
		eventBus.addHandler(RemoveTaxonEvent.TYPE, handler);
		eventBus.addHandler(RemoveColorsEvent.TYPE, handler);
		eventBus.addHandler(ModifyCharacterEvent.TYPE, handler);
		eventBus.addHandler(ModifyTaxonEvent.TYPE, handler);
		eventBus.addHandler(SetCharacterColorEvent.TYPE, handler);
		eventBus.addHandler(SetCharacterCommentEvent.TYPE, handler);
		eventBus.addHandler(SetControlModeEvent.TYPE, handler);
		eventBus.addHandler(SetTaxonColorEvent.TYPE, handler);
		eventBus.addHandler(SetTaxonCommentEvent.TYPE, handler);
		eventBus.addHandler(ShowDesktopEvent.TYPE, handler);
		eventBus.addHandler(ShowNumericalDistributionEvent.TYPE, handler);
		eventBus.addHandler(ShowTermFrequencyEvent.TYPE, handler);
		eventBus.addHandler(SortCharactersByCoverageEvent.TYPE, handler);
		eventBus.addHandler(SortCharactersByNameEvent.TYPE, handler);
		eventBus.addHandler(SortCharactersByOrganEvent.TYPE, handler);
		eventBus.addHandler(SortTaxaByCharacterEvent.TYPE, handler);
		eventBus.addHandler(SortTaxaByCoverageEvent.TYPE, handler);
		eventBus.addHandler(SortTaxaByNameEvent.TYPE, handler);
		eventBus.addHandler(ToggleDesktopEvent.TYPE, handler);
		eventBus.addHandler(SetValueColorEvent.TYPE, handler);
		eventBus.addHandler(SetValueCommentEvent.TYPE, handler);
		eventBus.addHandler(AnalyzeTaxonEvent.TYPE, handler);
		eventBus.addHandler(LockTaxonEvent.TYPE, handler);
		eventBus.addHandler(ShowDescriptionEvent.TYPE, handler);	
		eventBus.addHandler(SetValueEvent.TYPE, handler);
		eventBus.addHandler(ModelModeEvent.TYPE, handler);
	}

	private void appendConsole(String text) {
		console.append(text);
	}

	private void showTermFrequencyChart(Character character) {
		WidgetCreator creator = new TermFrequencyChartCreator(taxonMatrix, character);
		addAsFramedPanel(creator, "State Frequency: " + character.toString());
		eventBus.fireEvent(new ShowTermFrequencyEvent(character));
		eventBus.fireEvent(new ShowDesktopEvent());
	}

	private void showNumericalDistribution(Character character) {
		WidgetCreator creator = new NumericalSeriesChartCreator(taxonMatrix, character);
		addAsFramedPanel(creator, "Numerical Distribution: " + character.toString());	
		eventBus.fireEvent(new ShowNumericalDistributionEvent(character));
		eventBus.fireEvent(new ShowDesktopEvent());
	}

	public FramedPanel addAsFramedPanel(WidgetCreator creator, String title) {
		FramedPanel panel = new FramedPanel();
		panel.getElement().getStyle().setMargin(10, Unit.PX);
		panel.setCollapsible(true);
		Menu menu = new Menu();
		menu.add(new MenuItem("Refresh?"));
		menu.add(new MenuItem("Axis layout??? Sorting?"));
		menu.add(new MenuItem("Delte"));
		panel.setContextMenu(menu);
		panel.setHeadingText(title);
		panel.setPixelSize(framedPanelWidth, framedPanelHeight);
		panel.setBodyBorder(true);

		final Resizable resize = new Resizable(panel, Dir.E, Dir.SE, Dir.S);
		// resize.setMinHeight(400);
		// resize.setMinWidth(400);

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

		panel.add(creator.create());
		draggablePanel.setContainer(this);
		
		if(this.getWidgetCount() == 0)
			this.add(panel, new MarginData(defaultMargin, defaultMargin, defaultMargin, defaultMargin));
		else
			this.add(panel, new MarginData(- framedPanelHeight + marginIncrement + defaultMargin, defaultMargin, defaultMargin, ++widgetId * marginIncrement + defaultMargin));
		//this.add(panel);
		
		if(creator.hasContextMenu())
			panel.setContextMenu(creator.getContextMenu());
		
		return panel;
	}
}
