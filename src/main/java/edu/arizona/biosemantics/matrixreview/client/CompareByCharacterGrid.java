package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CompareViewValueChangedEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersionProperties;

public class CompareByCharacterGrid extends ContentPanel{
	private EventBus eventBus;
	private TaxonTreeGrid taxonGrid;
	private Grid<Taxon> oldVersionsGrid;
	private Grid<Taxon> currentVersionGrid;
	
	private TreeStore<Taxon> taxonStore;
	private List<SimpleMatrixVersion> oldVersions;
	private MatrixVersion currentVersion;
	
	private HorizontalLayoutContainer content;
	private HorizontalLayoutContainer oldVersionsContent;
	private HorizontalLayoutContainer currentVersionContent;
	
	public CompareByCharacterGrid(EventBus eventBus, TreeStore<Taxon> store, List<SimpleMatrixVersion> oldVersions, MatrixVersion currentVersion, Character selectedCharacter){
		this.eventBus = eventBus;
		this.taxonStore = store;
		this.oldVersions = oldVersions;
		this.currentVersion = currentVersion;
		
		taxonGrid = TaxonTreeGrid.createNew(eventBus, this.taxonStore);
		oldVersionsGrid = createOldVersionsGrid(this.taxonGrid, this.oldVersions, selectedCharacter);
		
		
		oldVersionsContent = new HorizontalLayoutContainer();
		oldVersionsContent.getScrollSupport().setScrollMode(ScrollMode.AUTOX);
		oldVersionsContent.setWidth(400);
		
		currentVersionContent = new HorizontalLayoutContainer();
		
		content = new HorizontalLayoutContainer();
		content.getScrollSupport().setScrollMode(ScrollMode.ALWAYS);
		content.add(taxonGrid, new HorizontalLayoutData(-1, 1));
		content.add(oldVersionsContent, new HorizontalLayoutData(-1, 1));
		content.add(currentVersionContent, new HorizontalLayoutData(-1, 1));
		content.getElement().getStyle().setBackgroundColor("green");
		
		this.add(content);
		
		updateSelectedCharacter(selectedCharacter);
		
		addEventHandlers();
	}
	
	private void addEventHandlers(){
		eventBus.addHandler(ChangeComparingSelectionEvent.TYPE, new ChangeComparingSelectionEvent.ChangeComparingSelectionEventHandler() {
			@Override
			public void onChange(ChangeComparingSelectionEvent event) {
				Object selection = event.getSelection();
				if (selection instanceof Character){
					Character newCharacter = (Character)selection;
					//a new character was selected. 
					updateSelectedCharacter(newCharacter);
				}
			}
		});
		
		eventBus.addHandler(CompareViewValueChangedEvent.TYPE, new CompareViewValueChangedEvent.CompareViewValueChangedEventHandler() {
			@Override
			public void onCompareViewValueChanged(CompareViewValueChangedEvent event) {
				currentVersionGrid.getView().refresh(false);
			}
		});
	}
	
	private void setHeadingCharacter(Character character){
		this.setHeadingText("Viewing Character: " + character);
	}
	
	private void updateSelectedCharacter(Character newCharacter){
		setHeadingCharacter(newCharacter);
		
		//TODO: save scroller x and y and set them again after adding the rows. 
		oldVersionsContent.clear();
		
		oldVersionsGrid = createOldVersionsGrid(this.taxonGrid, this.oldVersions, newCharacter);
		oldVersionsContent.add(oldVersionsGrid, new HorizontalLayoutData(-1, 1));
		
		currentVersionContent.clear();
		currentVersionGrid = createCurrentVersionGrid(this.taxonGrid, this.currentVersion, newCharacter);
		currentVersionContent.add(currentVersionGrid, new HorizontalLayoutData(-1, 1));
		
		this.forceLayout();
	}
	
	private Grid<Taxon> createOldVersionsGrid(TaxonTreeGrid controlColumn, List<SimpleMatrixVersion> oldVersions, Character selectedCharacter){
		List<ColumnConfig<Taxon, String>> columnConfigs = new ArrayList<ColumnConfig<Taxon, String>>();
		
		// create a column for each old matrix version.
		for (SimpleMatrixVersion version: oldVersions){
			SimpleMatrixVersionProperties versionProperties = new SimpleMatrixVersionProperties(version);
			ColumnConfig<Taxon, String> column = new ColumnConfig<Taxon, String>(versionProperties.valueOfCharacter(selectedCharacter));
			column.setHeader(version.getVersionInfo().getCreated().toString());
			
			columnConfigs.add(column);
		}
		
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>();
		columns.addAll(columnConfigs);
		
		ColumnModel<Taxon> columnModel = new ColumnModel<Taxon>(columns);
		
		Grid<Taxon> grid = new Grid<Taxon>(controlColumn.getListStore(), columnModel);
		
		return grid;
	}
	
	private Grid<Taxon> createCurrentVersionGrid(TaxonTreeGrid controlColumn, MatrixVersion currentVersion, Character selectedCharacter){
		MatrixVersionProperties versionProperties = new MatrixVersionProperties(currentVersion);
		ColumnConfig<Taxon, String> column = new ColumnConfig<Taxon, String>(versionProperties.valueOfCharacter(selectedCharacter));
		column.setHeader("Current");
		
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>();
		columns.add(column);
		
		ColumnModel<Taxon> columnModel = new ColumnModel<Taxon>(columns);
		
		Grid<Taxon> grid = new Grid<Taxon>(controlColumn.getListStore(), columnModel);
		
		
		final CurrentVersionEditing editing = new CurrentVersionEditing(eventBus, grid, currentVersion, selectedCharacter);
		editing.addEditor(column, new TextField());
		
		return grid;
	}
	
	private class CurrentVersionEditing extends GridInlineEditing<Taxon>{
		
		private EventBus eventBus;
		private MatrixVersion version;
		private Character selectedCharacter;
		
		public CurrentVersionEditing(EventBus eventBus, Grid<Taxon> editableGrid, MatrixVersion version, Character selectedCharacter) {
			super(editableGrid);
			this.eventBus = eventBus;
			this.version = version;
			this.selectedCharacter = selectedCharacter;
		}

		protected <N, O> void doCompleteEditing() {

			if (activeCell != null) {

				ListStore<Taxon> store = getEditableGrid().getStore();
				ListStore<Taxon>.Record r = store.getRecord(store.get(activeCell.getRow()));
				Taxon selectedTaxon = r.getModel();

				TaxonMatrix matrix = version.getTaxonMatrix();
				
				//make sure that this taxon and character exist in the current version. 
				Taxon t = matrix.getTaxonById(selectedTaxon.getId()); 
				if (t == null)
					return;
				Character c = matrix.getCharacterById(selectedCharacter.getId());
				if (c == null)
					return;
				
				Field<O> field = getEditor(columnModel.getColumn(activeCell.getCol()));
					O fieldValue = ((ValueBaseField<O>) field).getCurrentValue();
				
				
				if (fieldValue instanceof String){
				matrix.setValue(t, c, new Value((String)fieldValue));
					eventBus.fireEvent(new CompareViewValueChangedEvent());
				}
			}
		}
	}
}
