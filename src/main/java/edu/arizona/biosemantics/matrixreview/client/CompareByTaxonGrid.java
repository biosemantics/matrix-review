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

import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CompareViewValueChangedEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersionProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class CompareByTaxonGrid extends ContentPanel{
	private EventBus eventBus;
	private CharacterTreeGrid characterGrid;
	private Grid<CharacterTreeNode> oldVersionsGrid;
	private Grid<CharacterTreeNode> currentVersionGrid;
	
	private TreeStore<CharacterTreeNode> characterStore;
	private List<SimpleMatrixVersion> oldVersions;
	private MatrixVersion currentVersion;
	
	private HorizontalLayoutContainer content;
	private HorizontalLayoutContainer oldVersionsContent;
	private HorizontalLayoutContainer currentVersionContent;
	
	public CompareByTaxonGrid(EventBus eventBus, TreeStore<CharacterTreeNode> store, List<SimpleMatrixVersion> oldVersions, MatrixVersion currentVersion, Taxon selectedTaxon){
		this.eventBus = eventBus;
		this.characterStore = store;
		this.oldVersions = oldVersions;
		this.currentVersion = currentVersion;
			
		characterGrid = CharacterTreeGrid.createNew(this.eventBus, this.characterStore);
		characterGrid.getSelectionModel().setLocked(true);		
		
		oldVersionsContent = new HorizontalLayoutContainer();
		oldVersionsContent.getScrollSupport().setScrollMode(ScrollMode.AUTOX);
		oldVersionsContent.setWidth(400);
		
		currentVersionContent = new HorizontalLayoutContainer();
			
		content = new HorizontalLayoutContainer();
		content.getScrollSupport().setScrollMode(ScrollMode.ALWAYS);
		content.add(characterGrid, new HorizontalLayoutData(-1, 1));
		content.add(oldVersionsContent, new HorizontalLayoutData(-1, 1));
		content.add(currentVersionContent, new HorizontalLayoutData(-1, 1));
		content.getElement().getStyle().setBackgroundColor("orange");
		
		this.add(content);
		
		updateSelectedTaxon(selectedTaxon);
		
		addEventHandlers();
	}
	
	private void addEventHandlers(){
		eventBus.addHandler(ChangeComparingSelectionEvent.TYPE, new ChangeComparingSelectionEvent.ChangeComparingSelectionEventHandler() {
			@Override
			public void onChange(ChangeComparingSelectionEvent event) {
				Object selection = event.getSelection();
				if (selection instanceof Taxon){
					Taxon newTaxon = (Taxon)selection;
					updateSelectedTaxon(newTaxon);
				}
			}
		});
		
		eventBus.addHandler(CompareViewValueChangedEvent.TYPE, new CompareViewValueChangedEvent.CompareViewValueChangedEventHandler() {
			@Override
			public void onCompareViewValueChanged(CompareViewValueChangedEvent event) {
				System.out.println("");
				currentVersionGrid.getView().refresh(false);
			}
		});
	}
	
	private void setHeadingTaxon(Taxon taxon){
		this.setHeadingText("Viewing Taxon: " + taxon);
	}
	
	private void updateSelectedTaxon(Taxon newTaxon){
		setHeadingTaxon(newTaxon);
		
		//TODO: save scroller x and y and set them again after adding the rows. 
		oldVersionsContent.clear();
		
		oldVersionsGrid = createOldVersionsGrid(this.characterGrid, this.oldVersions, newTaxon);
		oldVersionsContent.add(oldVersionsGrid, new HorizontalLayoutData(-1, 1));
		
		currentVersionContent.clear();
		currentVersionGrid = createCurrentVersionGrid(this.characterGrid, this.currentVersion, newTaxon);
		currentVersionContent.add(currentVersionGrid, new HorizontalLayoutData(-1, 1));
		
		this.forceLayout();
	}
	
	private Grid<CharacterTreeNode> createOldVersionsGrid(CharacterTreeGrid controlColumn, List<SimpleMatrixVersion> oldVersions, Taxon selectedTaxon){
		List<ColumnConfig<CharacterTreeNode, String>> columnConfigs = new ArrayList<ColumnConfig<CharacterTreeNode, String>>();
		
		// create a column for each old matrix version.
		for (SimpleMatrixVersion version: oldVersions){
			SimpleMatrixVersionProperties versionProperties = new SimpleMatrixVersionProperties(version);
			ColumnConfig<CharacterTreeNode, String> column = new ColumnConfig<CharacterTreeNode, String>(versionProperties.valueOfTaxon(selectedTaxon));
			column.setHeader(version.getVersionInfo().getCreated().toString());
			
			columnConfigs.add(column);
		}
		
		List<ColumnConfig<CharacterTreeNode, ?>> columns = new ArrayList<ColumnConfig<CharacterTreeNode, ?>>();
		columns.addAll(columnConfigs);
		
		ColumnModel<CharacterTreeNode> columnModel = new ColumnModel<CharacterTreeNode>(columns);
		
		Grid<CharacterTreeNode> grid = new Grid<CharacterTreeNode>(controlColumn.getListStore(), columnModel);
		//grid.getElement().setAttribute("autoHeight", "true");
		return grid;
	}
	
	private Grid<CharacterTreeNode> createCurrentVersionGrid(CharacterTreeGrid controlColumn, MatrixVersion currentVersion, Taxon selectedTaxon){
		MatrixVersionProperties versionProperties = new MatrixVersionProperties(currentVersion);
		ColumnConfig<CharacterTreeNode, String> column = new ColumnConfig<CharacterTreeNode, String>(versionProperties.valueOfTaxon(selectedTaxon));
		column.setHeader("Current");
		
		List<ColumnConfig<CharacterTreeNode, ?>> columns = new ArrayList<ColumnConfig<CharacterTreeNode, ?>>();
		columns.add(column);
		
		ColumnModel<CharacterTreeNode> columnModel = new ColumnModel<CharacterTreeNode>(columns);
		
		Grid<CharacterTreeNode> grid = new Grid<CharacterTreeNode>(controlColumn.getListStore(), columnModel);
		
		
		final CurrentVersionEditing editing = new CurrentVersionEditing(eventBus, grid, currentVersion, selectedTaxon);
		editing.addEditor(column, new TextField());
		
		return grid;
	}
	
	private class CurrentVersionEditing extends GridInlineEditing<CharacterTreeNode>{
		
		private EventBus eventBus;
		private MatrixVersion version;
		private Taxon selectedTaxon;
		
		public CurrentVersionEditing(EventBus eventBus, Grid<CharacterTreeNode> editableGrid, MatrixVersion version, Taxon selectedTaxon) {
			super(editableGrid);
			this.eventBus = eventBus;
			this.version = version;
			this.selectedTaxon = selectedTaxon;
		}

		protected <N, O> void doCompleteEditing() {

			if (activeCell != null) {

				ListStore<CharacterTreeNode> store = getEditableGrid().getStore();
				ListStore<CharacterTreeNode>.Record r = store.getRecord(store.get(activeCell.getRow()));
				CharacterTreeNode node = r.getModel();

				if (node.getData() instanceof Character) {
					Character selectedCharacter = (Character)node.getData();
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

}
