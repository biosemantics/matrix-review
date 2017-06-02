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
import edu.arizona.biosemantics.matrixreview.client.event.*;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent.AddCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class ConsoleManager extends AbstractWindowManager {

	private class ConsoleEventsHandler implements AddCharacterEventHandler, 
		AddTaxonEvent.AddTaxonEventHandler, AnalyzeCharacterEvent.AnalyzeCharacterEventHandler, HideCharacterEvent.HideCharacterEventHandler, 
		HideTaxaEvent.HideTaxaEventHandler, LoadModelEvent.LoadModelEventHandler, LockCharacterEvent.LockCharacterEventHandler,
		LockMatrixEvent.LockMatrixEventHandler, SetColorsEvent.SetColorsEventHandler,
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
		SetValueColorEvent.SetValueColorEventHandler, SetValueEvent.SetValueEventHandler, MatrixModeEvent.MatrixModeEventHandler,
		CollapseTaxaEvent.CollapseTaxaEventHandler, ExpandTaxaEvent.ExpandTaxaEventHandler
		{
		
		public ConsoleEventsHandler() {
			addEventHandlers();
		}
	
		private void addEventHandlers() {
			//TODO
			//fullMatrixEventBus.addHandler(type, handler)
			EventBus[] busses = { fullMatrixEventBus, subMatrixEventBus };
			for(EventBus bus : busses) {
				bus.addHandler(AddCharacterEvent.TYPE, this);
				bus.addHandler(SetColorsEvent.TYPE, this);
				bus.addHandler(AddTaxonEvent.TYPE, this);
				bus.addHandler(AnalyzeCharacterEvent.TYPE, this);
				bus.addHandler(HideCharacterEvent.TYPE, this);
				bus.addHandler(HideTaxaEvent.TYPE, this);
				bus.addHandler(LoadModelEvent.TYPE, this);
				bus.addHandler(LockCharacterEvent.TYPE, this);
				bus.addHandler(LockMatrixEvent.TYPE, this);
				bus.addHandler(MergeCharactersEvent.TYPE, this);
				bus.addHandler(MoveCharacterEvent.TYPE, this);
				bus.addHandler(MoveTaxonFlatEvent.TYPE, this);
				bus.addHandler(RemoveCharacterEvent.TYPE, this);
				bus.addHandler(RemoveTaxaEvent.TYPE, this);
				bus.addHandler(SetColorsEvent.TYPE, this);
				bus.addHandler(ModifyCharacterEvent.TYPE, this);
				bus.addHandler(ModifyTaxonEvent.TYPE, this);
				bus.addHandler(SetCharacterColorEvent.TYPE, this);
				bus.addHandler(SetCharacterCommentEvent.TYPE, this);
				bus.addHandler(SetControlModeEvent.TYPE, this);
				bus.addHandler(SetTaxonColorEvent.TYPE, this);
				bus.addHandler(SetTaxonCommentEvent.TYPE, this);
				bus.addHandler(ShowDesktopEvent.TYPE, this);
				bus.addHandler(ShowNumericalDistributionEvent.TYPE, this);
				bus.addHandler(ShowTermFrequencyEvent.TYPE, this);
				bus.addHandler(SortCharactersByCoverageEvent.TYPE, this);
				bus.addHandler(SortCharactersByNameEvent.TYPE, this);
				bus.addHandler(SortCharactersByOrganEvent.TYPE, this);
				bus.addHandler(SortTaxaByCharacterEvent.TYPE, this);
				bus.addHandler(SortTaxaByCoverageEvent.TYPE, this);
				bus.addHandler(SortTaxaByNameEvent.TYPE, this);
				bus.addHandler(ToggleDesktopEvent.TYPE, this);
				bus.addHandler(SetValueColorEvent.TYPE, this);
				bus.addHandler(SetValueCommentEvent.TYPE, this);
				bus.addHandler(AnalyzeTaxonEvent.TYPE, this);
				bus.addHandler(LockTaxonEvent.TYPE, this);
				bus.addHandler(ShowDescriptionEvent.TYPE, this);	
				//bus.addHandler(ShowSentenceEvent.TYPE, this);
				bus.addHandler(SetValueEvent.TYPE, this);
				bus.addHandler(MatrixModeEvent.TYPE, this);
				bus.addHandler(CollapseTaxaEvent.TYPE, this);
				bus.addHandler(ExpandTaxaEvent.TYPE, this);
			}
			
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
		public void onLoad(LoadModelEvent event) {
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
		public void onSet(SetColorsEvent event) {
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
		public void onMode(MatrixModeEvent event) {
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
	
	public ConsoleManager(EventBus fullModelEventBus, EventBus subModelEventBus, Window window) {
		super(fullModelEventBus, subModelEventBus, window);
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
