package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.desktop.Window;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CollapseTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ExpandTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.HideCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.HideTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModelModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxonFlatEvent;
import edu.arizona.biosemantics.matrixreview.client.event.PrintableEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveColorsEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDescriptionEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowNumericalDistributionEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowTermFrequencyEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ToggleDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent.AddCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class ConsoleManager extends AbstractWindowManager {

	private class ConsoleEventsHandler implements AddCharacterEventHandler, AddColorEvent.AddColorEventHandler, 
		AddTaxonEvent.AddTaxonEventHandler, AnalyzeCharacterEvent.AnalyzeCharacterEventHandler, HideCharacterEvent.HideCharacterEventHandler, 
		HideTaxaEvent.HideCharacterEventHandler, LoadTaxonMatrixEvent.LoadTaxonMatrixEventHandler, LockCharacterEvent.LockCharacterEventHandler,
		LockMatrixEvent.LockMatrixEventHandler, RemoveColorsEvent.RemoveColorsEventHandler,
		MergeCharactersEvent.MergeCharactersEventHandler, MoveCharacterEvent.MoveCharacterEventHandler, MoveTaxonFlatEvent.MoveTaxonEventHandler, 
		RemoveCharacterEvent.RemoveCharacterEventHandler, RemoveTaxaEvent.RemoveTaxonEventHandler, ModifyCharacterEvent.ModifyCharacterEventHandler, 
		ModifyTaxonEvent.ModifyTaxonEventHandler, SetCharacterColorEvent.SetCharacterColorEventHandler, SetCharacterCommentEvent.SetCharacterCommentEventHandler, 
		SetControlModeEvent.SetControlModeEventHandler, SetTaxonColorEvent.SetTaxonColorEventHandler, SetTaxonCommentEvent.SetTaxonCommentEventHandler, 
		ShowDesktopEvent.ShowDesktopEventHandler, ShowNumericalDistributionEvent.ShowNumericalDistributionEventHandler, 
		ShowTermFrequencyEvent.ShowTermFrequencyEventHandler, SortCharactersByCoverageEvent.SortCharatersByCoverageEventHandler, 
		SortCharactersByNameEvent.SortCharatersByNameEventHandler, SortCharactersByOrganEvent.SortCharatersByOrganEventHandler, 
		SortTaxaByCharacterEvent.SortTaxaByCharacterEventHandler, SortTaxaByCoverageEvent.SortTaxaByCoverageEventHandler, 
		SortTaxaByNameEvent.SortTaxaByNameEventHandler, ToggleDesktopEvent.ToggleDesktopEventHandler, 
		AnalyzeTaxonEvent.AnalyzeTaxonEventHandler, LockTaxonEvent.LockTaxonEventHandler, 
		ShowDescriptionEvent.ShowDescriptionEventHandler, SetValueCommentEvent.SetValueCommentEventHandler, 
		SetValueColorEvent.SetValueColorEventHandler, SetValueEvent.SetValueEventHandler, ModelModeEvent.ModelModeEventHandler,
		CollapseTaxaEvent.CollapseTaxaEventHandler, ExpandTaxaEvent.ExpandTaxaEventHandler
		{
		
		public ConsoleEventsHandler() {
			addEventHandlers();
		}
	
		private void addEventHandlers() {
			//TODO
			//fullMatrixEventBus.addHandler(type, handler)
			
			subMatrixEventBus.addHandler(AddCharacterEvent.TYPE, this);
			subMatrixEventBus.addHandler(AddColorEvent.TYPE, this);
			subMatrixEventBus.addHandler(AddTaxonEvent.TYPE, this);
			subMatrixEventBus.addHandler(AnalyzeCharacterEvent.TYPE, this);
			subMatrixEventBus.addHandler(HideCharacterEvent.TYPE, this);
			subMatrixEventBus.addHandler(HideTaxaEvent.TYPE, this);
			subMatrixEventBus.addHandler(LoadTaxonMatrixEvent.TYPE, this);
			subMatrixEventBus.addHandler(LockCharacterEvent.TYPE, this);
			subMatrixEventBus.addHandler(LockMatrixEvent.TYPE, this);
			subMatrixEventBus.addHandler(MergeCharactersEvent.TYPE, this);
			subMatrixEventBus.addHandler(MoveCharacterEvent.TYPE, this);
			subMatrixEventBus.addHandler(MoveTaxonFlatEvent.TYPE, this);
			subMatrixEventBus.addHandler(RemoveCharacterEvent.TYPE, this);
			subMatrixEventBus.addHandler(RemoveTaxaEvent.TYPE, this);
			subMatrixEventBus.addHandler(RemoveColorsEvent.TYPE, this);
			subMatrixEventBus.addHandler(ModifyCharacterEvent.TYPE, this);
			subMatrixEventBus.addHandler(ModifyTaxonEvent.TYPE, this);
			subMatrixEventBus.addHandler(SetCharacterColorEvent.TYPE, this);
			subMatrixEventBus.addHandler(SetCharacterCommentEvent.TYPE, this);
			subMatrixEventBus.addHandler(SetControlModeEvent.TYPE, this);
			subMatrixEventBus.addHandler(SetTaxonColorEvent.TYPE, this);
			subMatrixEventBus.addHandler(SetTaxonCommentEvent.TYPE, this);
			subMatrixEventBus.addHandler(ShowDesktopEvent.TYPE, this);
			subMatrixEventBus.addHandler(ShowNumericalDistributionEvent.TYPE, this);
			subMatrixEventBus.addHandler(ShowTermFrequencyEvent.TYPE, this);
			subMatrixEventBus.addHandler(SortCharactersByCoverageEvent.TYPE, this);
			subMatrixEventBus.addHandler(SortCharactersByNameEvent.TYPE, this);
			subMatrixEventBus.addHandler(SortCharactersByOrganEvent.TYPE, this);
			subMatrixEventBus.addHandler(SortTaxaByCharacterEvent.TYPE, this);
			subMatrixEventBus.addHandler(SortTaxaByCoverageEvent.TYPE, this);
			subMatrixEventBus.addHandler(SortTaxaByNameEvent.TYPE, this);
			subMatrixEventBus.addHandler(ToggleDesktopEvent.TYPE, this);
			subMatrixEventBus.addHandler(SetValueColorEvent.TYPE, this);
			subMatrixEventBus.addHandler(SetValueCommentEvent.TYPE, this);
			subMatrixEventBus.addHandler(AnalyzeTaxonEvent.TYPE, this);
			subMatrixEventBus.addHandler(LockTaxonEvent.TYPE, this);
			subMatrixEventBus.addHandler(ShowDescriptionEvent.TYPE, this);	
			subMatrixEventBus.addHandler(SetValueEvent.TYPE, this);
			subMatrixEventBus.addHandler(ModelModeEvent.TYPE, this);
			subMatrixEventBus.addHandler(CollapseTaxaEvent.TYPE, this);
			subMatrixEventBus.addHandler(ExpandTaxaEvent.TYPE, this);
		}

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
		
		private void appendConsole(String text) {
			if(instance != null)
				instance.append(text);
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
		public void onModify(ModifyCharacterEvent event) {
			printToConsole(event);
		}
	
		@Override
		public void onRemove(RemoveTaxaEvent event) {
			printToConsole(event);
		}
	
		@Override
		public void onRemove(RemoveCharacterEvent event) {
			printToConsole(event);
		}
	
		@Override
		public void onMove(MoveTaxonFlatEvent event) {
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
		public void onHide(HideTaxaEvent event) {
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

		@Override
		public void onExpand(ExpandTaxaEvent event) {
			printToConsole(event);
		}

		@Override
		public void onCollapse(CollapseTaxaEvent event) {
			printToConsole(event);
		}
		
	}

	private EventBus fullMatrixEventBus;
	
	public ConsoleManager(EventBus fullMatrixEventBus, EventBus subMatrixEventBus, Window window) {
		super(subMatrixEventBus, window);
		this.fullMatrixEventBus = fullMatrixEventBus;
		init();
	}

	public class Console extends TextArea {
		
		public void append(String text) {
			String oldText = this.getText();
			this.setText(oldText + "\n" + text);
		}
	}

	private Console instance;

	@Override
	public void refreshContent() {
		//if(instance == null)
		instance = new Console();
		window.setWidget(instance);
	}

	@Override
	protected void addEventHandlers() {
		ConsoleEventsHandler consoleEventsHandler = new ConsoleEventsHandler();
	}
	
	@Override
	public void refreshContextMenu() {
		Menu menu = new Menu();
		MenuItem closeItem = new MenuItem("Close");
		menu.add(closeItem);
		closeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				window.hide();
			}
		});
		window.setContextMenu(menu);
	}

	@Override
	public void refreshTitle() {
		window.setHeadingText("Activity Log");
	}

}
