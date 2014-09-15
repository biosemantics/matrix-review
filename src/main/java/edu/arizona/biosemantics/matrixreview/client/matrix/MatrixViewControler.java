package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;

import edu.arizona.biosemantics.matrixreview.client.common.compare.CharactersByCoverageComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.CharactersByNameComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.CharactersByOrganComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.TaxaByCharacterComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.TaxaByCoverageComparator;
import edu.arizona.biosemantics.matrixreview.client.common.compare.TaxaByNameComparator;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent.AddCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent.AddTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.HideCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.HideCharacterEvent.HideCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.HideTaxaEvent.HideTaxaEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.HideTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeCharactersEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeMode;
import edu.arizona.biosemantics.matrixreview.client.event.CollapseTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ExpandTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent.SetCharacterColorEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent.SetControlModeEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent.SetTaxonColorEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent.SetValueColorEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent.LoadModelEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent.ModifyCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent.ModifyTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent.RemoveCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent.RemoveTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent.SetTaxonCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent.SetValueCommentEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent.SetValueEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent.SortCharatersByCoverageEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent.SortCharatersByNameEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent.SortCharatersByOrganEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent.SortTaxaByCharacterEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent.SortTaxaByCoverageEventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent.SortTaxaByNameEventHandler;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharacterColumnConfig.CharacterValueProvider;
import edu.arizona.biosemantics.matrixreview.client.matrix.cells.ValueCell;
import edu.arizona.biosemantics.matrixreview.client.matrix.editing.LockableControlableMatrixEditing;
import edu.arizona.biosemantics.matrixreview.client.matrix.filters.CharactersGridFilters;
import edu.arizona.biosemantics.matrixreview.shared.model.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public abstract class MatrixViewControler implements LoadModelEventHandler, 
	AddTaxonEventHandler, RemoveTaxonEventHandler, ModifyTaxonEventHandler, HideTaxaEventHandler, 
	AddCharacterEventHandler, RemoveCharacterEventHandler, ModifyCharacterEventHandler, 
	SetTaxonCommentEventHandler, SetValueCommentEventHandler, 
	SetValueColorEventHandler, SetCharacterColorEventHandler, SetTaxonColorEventHandler,
	SetControlModeEventHandler, SetValueEventHandler, SortCharatersByCoverageEventHandler, 
	SortCharatersByNameEventHandler, SortCharatersByOrganEventHandler, SortTaxaByCoverageEventHandler, 
	SortTaxaByNameEventHandler, SortTaxaByCharacterEventHandler, HideCharacterEventHandler, 
	MergeCharactersEventHandler
	{

	protected EventBus eventBus;
	protected Model model;
	
	protected FrozenFirstColumTaxonTreeGrid taxonTreeGrid;
	protected TaxonStore taxonStore;
	protected LockableControlableMatrixEditing editing;
	protected EditEventsHandler editEventsHandler;
	protected CharactersGridFilters charactersFilters;
	
	protected Set<HandlerRegistration> handlerRegistrations = new HashSet<HandlerRegistration>();
	private ValueCell valueCell;

	
	public MatrixViewControler(EventBus eventBus, FrozenFirstColumTaxonTreeGrid taxonTreeGrid) {
		this.eventBus = eventBus;
		this.taxonTreeGrid = taxonTreeGrid;
		this.taxonStore = taxonTreeGrid.getTaxonStore();
		
		this.addEventHandlers();
	}

	public MatrixViewControler(EventBus eventBus, FrozenFirstColumTaxonTreeGrid taxonTreeGrid, Model model) {
		this(eventBus, taxonTreeGrid);
		this.model = model;
	}

	protected void addEventHandlers() {
		handlerRegistrations.add(eventBus.addHandler(LoadModelEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(AddCharacterEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(HideTaxaEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(RemoveCharacterEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(ModifyCharacterEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SetTaxonCommentEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SetValueCommentEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SetValueColorEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SetCharacterColorEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SetTaxonColorEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SetControlModeEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SetValueEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SortTaxaByNameEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SortTaxaByCharacterEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SortTaxaByCoverageEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SortCharactersByNameEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SortCharactersByOrganEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(SortCharactersByCoverageEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(HideCharacterEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(MergeCharactersEvent.TYPE, this));
		
		handlerRegistrations.add(eventBus.addHandler(AddTaxonEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(RemoveTaxaEvent.TYPE, this));
		handlerRegistrations.add(eventBus.addHandler(ModifyTaxonEvent.TYPE, this));
	}

	@Override
	public void onLoad(LoadModelEvent event) {
		this.model = event.getModel();
		loadModel(model);
	}
	
	@Override
	public void onHide(HideTaxaEvent event) {
		taxonTreeGrid.hide(event.getTaxa(), event.isHide());
	}
	
	@Override
	public void onRemove(RemoveCharacterEvent event) {
		removeCharacters(event.getCharacters());
	}
	
	private void removeCharacters(Collection<Character> characters) {
		List<CharacterColumnConfig> columns = new LinkedList<CharacterColumnConfig>(taxonTreeGrid.getColumnModel().getCharacterColumns());
		Iterator<CharacterColumnConfig> iterator = columns.iterator();
		while(iterator.hasNext()) {
			CharacterColumnConfig config = iterator.next();
			for(Character character : characters) {
				if(config.getCharacter().equals(character)) {
					editing.removeEditor(config);
					iterator.remove();
				}
			}
		}
		
		taxonTreeGrid.reconfigure(columns);
	}

	@Override
	public void onModify(ModifyCharacterEvent event) {
		modifyCharacter(event.getOldCharacter());
	}
	
	private void modifyCharacter(Character oldCharacter) {
		CharacterColumnConfig config =
		taxonTreeGrid.getGrid().getCharacterColumnConfig(oldCharacter);
		config.setHeader(SafeHtmlUtils.fromString(oldCharacter.toString()));
		taxonTreeGrid.updateCharacterGridHeads();
	}

	@Override
	public void onSet(SetTaxonCommentEvent event) {
		taxonStore.update(event.getTaxon());
	}

	@Override
	public void onSet(SetValueCommentEvent event) {
		taxonStore.update(model.getTaxonMatrix().getTaxon(event.getValue()));
	}
	
	protected void loadModel(Model model) {
		valueCell = new ValueCell(eventBus, model);
		List<ColumnConfig<Taxon, ?>> characterColumnConfigs = new ArrayList<ColumnConfig<Taxon, ?>>();
		for(Character character : model.getTaxonMatrix().getVisibleFlatCharacters()) {
			characterColumnConfigs.add(this.createCharacterColumnConfig(character, valueCell));
		}
		if(!taxonTreeGrid.isInitialized()) {
			taxonTreeGrid.init(characterColumnConfigs, new CharactersGridView(eventBus, model));
			//necessary otherwise VerticalLayout that embedds MatrixView and MenuView in MatrixReviewView won't layout correctly correctly
			//taxonTreeGrid.forceLayout();			
		} else {
			taxonTreeGrid.reconfigure(characterColumnConfigs);
			//taxonTreeGrid.forceLayout();
		}

		valueCell.setListStore(taxonTreeGrid.getTreeGrid().getListStore());
		valueCell.setColumnModel(taxonTreeGrid.getColumnModel());
		editing = new LockableControlableMatrixEditing(eventBus, taxonTreeGrid.getGrid(), taxonTreeGrid.getTreeGrid().getListStore(), model);
		editEventsHandler = new EditEventsHandler(eventBus, taxonTreeGrid);
		initCharacterEditing();
		initCharacterFiltering();
			
		loadTaxa(model);
		
		/*Scheduler.get().scheduleDeferred(new ScheduledCommand() {
		    public void execute () {
		    	taxonTreeGrid.getTreeGrid().expandAll();
		    }
		}); */
	}
	
	private void loadTaxa(Model model) {
		taxonStore.clear();
		fillStore(model);
	}

	private void initCharacterEditing() {
		editing.clearEditors();
		for(CharacterColumnConfig config : taxonTreeGrid.getGrid().getColumnModel().getCharacterColumns()) {
			editing.addEditor(config);
			editing.addBeforeStartEditHandler(editEventsHandler);
			editing.addCompleteEditHandler(editEventsHandler);
		}
	}
	
	private void initCharacterFiltering() {
		// init control and filtering for charactersGrid
		if(charactersFilters != null) {
			charactersFilters.clearFilters();
			charactersFilters.removeAll();
		}
		charactersFilters = new CharactersGridFilters(eventBus, model, taxonStore, taxonTreeGrid.getGrid());
		charactersFilters.initPlugin(taxonTreeGrid.getGrid());
	}
	
	private CharacterColumnConfig createCharacterColumnConfig(final Character character, ValueCell valueCell) {
		CharacterColumnConfig characterColumnConfig = new CharacterColumnConfig(200, character, model);
		CharacterValueProvider characterValueProvider = (CharacterValueProvider) characterColumnConfig.getValueProvider();
		characterValueProvider.setCharacterColumnConfig(characterColumnConfig);
		characterColumnConfig.setCell(valueCell);
		return characterColumnConfig;
	}
	
	protected abstract void fillStore(Model model);

	public void remove() {
		for(HandlerRegistration handlerRegistration : handlerRegistrations)
			handlerRegistration.removeHandler();
		handlerRegistrations.clear();
	}
	
	@Override
	public void onSet(SetValueColorEvent event) {
		taxonStore.update(model.getTaxonMatrix().getTaxon(event.getValue()));
	}
	
	@Override
	public void onSet(SetCharacterColorEvent event) {
		for(Taxon visibleTaxon : taxonStore.getAll())
			taxonStore.update(visibleTaxon);
	}

	@Override
	public void onSet(SetTaxonColorEvent event) {
		taxonStore.update(event.getTaxon());
	}
	
	@Override
	public void onSet(SetControlModeEvent event) {
		setControlMode(event.getCharacter(), event.getControlMode(), event.getStates());
	}
	
	private void setControlMode(Character character, ControlMode controlMode, List<String> states) {
		editing.setControlMode(character, controlMode, states);
		charactersFilters.setControlMode(character, controlMode, states);
	}

	@Override
	public void onSet(SetValueEvent event) {
		taxonStore.update(event.getTaxon());
	}
	
	@Override
	public void onSort(final SortCharactersByCoverageEvent event) {
		sortCharactersByCoverage(event.getSortDir());
	}

	private void sortCharactersByCoverage(final SortDir sortDir) {
		Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
		CharactersByCoverageComparator charactersByCoverageComparator = new CharactersByCoverageComparator(model, sortDir);
			@Override
			public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
				return charactersByCoverageComparator.compare(o1.getCharacter(), o2.getCharacter());
			}
		};
		sortCharacters(comparator);
	}

	@Override
	public void onSort(final SortCharactersByNameEvent event) {
		sortCharactersByName(event.getSortDir());
	}

	private void sortCharactersByName(final SortDir sortDir) {
		Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
		CharactersByNameComparator charactersByNameComparator = new CharactersByNameComparator(model, sortDir);
			@Override
			public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
				return charactersByNameComparator.compare(o1.getCharacter(), o2.getCharacter());
			}
		};
		sortCharacters(comparator);
	}

	@Override
	public void onSort(final SortCharactersByOrganEvent event) {
		sortCharactersByOrgan(event.getSortDir());
	}

	private void sortCharactersByOrgan(final SortDir sortDir) {
		Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
		CharactersByOrganComparator charactersByOrganComparator = new CharactersByOrganComparator(model, sortDir);
			@Override
			public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
				return charactersByOrganComparator.compare(o1.getCharacter(), o2.getCharacter());
			}
		};
		sortCharacters(comparator);
	}

	@Override
	public void onSort(SortTaxaByCoverageEvent event) {
		//static SortDir on Comparator, otherwise gxt store will internally equalize our external sortDir
		sortTaxa(new TaxaByCoverageComparator(model, SortDir.ASC), event.getSortDirection());
	}

	@Override
	public void onSort(SortTaxaByNameEvent event) {
		//static SortDir on Comparator, otherwise gxt store will internally equalize our external sortDir
		sortTaxa(new TaxaByNameComparator(SortDir.ASC), event.getSortDirection());
	}

	@Override
	public void onSort(SortTaxaByCharacterEvent event) {
		//static SortDir on Comparator, otherwise gxt store will internally equalize our external sortDir
		sortTaxa(new TaxaByCharacterComparator(model, event.getCharacter(), SortDir.ASC), event.getSortDirection());
	}
	
	protected void sortTaxa(Comparator<Taxon> comparator, SortDir sortDir) {
		taxonStore.clearSortInfo();
		StoreSortInfo<Taxon> sortInfo = new StoreSortInfo<Taxon>(comparator, sortDir);
		taxonStore.addSortInfo(sortInfo);
	}
	
	protected void sortCharacters(Comparator<CharacterColumnConfig> comparator) {
		List<CharacterColumnConfig> characterColumnConfigs = taxonTreeGrid.getColumnModel().getCharacterColumns();
		Collections.sort(characterColumnConfigs, comparator);
		taxonTreeGrid.reconfigure(characterColumnConfigs);
	}

	@Override
	public void onHide(HideCharacterEvent event) {
		hideCharacter(event.getCharacters(), event.isHide());
	}
	
	private void hideCharacter(Set<Character> characters, boolean hide) {
		List<CharacterColumnConfig> characterColumnConfigs = taxonTreeGrid.getColumnModel().getCharacterColumns();
		for(int i=0; i<characterColumnConfigs.size(); i++) {
			CharacterColumnConfig config = characterColumnConfigs.get(i);
			if(characters.contains(config.getCharacter())) {
				taxonTreeGrid.getColumnModel().setHidden(i, hide);
			}
		}
	}

	@Override
	public void onMerge(MergeCharactersEvent event) {
		merge(event.getCharacter(), event.getTarget(), event.getMergeMode());
	}

	private void merge(Character character, Character target, MergeMode mergeMode) {
		editing.setControlMode(character, ControlMode.OFF, null);
		List<Character> charactersToRemove = new LinkedList<Character>();
		charactersToRemove.add(target);
		this.removeCharacters(charactersToRemove);
	}
	
	@Override
	public void onAdd(AddCharacterEvent event) {
		addCharacter(event.getCharacter(), event.getOrgan(), event.getAddAfterCharacter());
	}

	private void addCharacter(Character character, Organ organ, Character addAfterCharacter) {
		List<CharacterColumnConfig> columns = new LinkedList<CharacterColumnConfig>(taxonTreeGrid.getColumnModel().getCharacterColumns());
		CharacterColumnConfig columnConfig = createCharacterColumnConfig(character, valueCell);
		
		int addAt = 0;
		if(addAfterCharacter != null) {
			for(int i=0; i<columns.size(); i++) {
				CharacterColumnConfig config = columns.get(i);
				if(config.getCharacter().equals(addAfterCharacter)) {
					addAt = i + 1;
					break;
				}
			}
		}
		
		columns.add(addAt, columnConfig);
			
		editing.addEditor(columnConfig);
		taxonTreeGrid.reconfigure(columns);
		charactersFilters.addCharacter(character);
	}
		
	public Model getModel() {
		return model;
	}

}
