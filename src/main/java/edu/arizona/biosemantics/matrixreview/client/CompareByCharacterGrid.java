package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersionProperties;

public class CompareByCharacterGrid extends ContentPanel{
	private EventBus eventBus;
	private TaxonTreeGrid taxonGrid;
	private Grid<Taxon> oldVersionsGrid;
	
	private TreeStore<Taxon> taxonStore;
	private List<SimpleMatrixVersion> oldVersions;
	
	private HorizontalLayoutContainer content;
	private HorizontalLayoutContainer oldVersionsContent;
	
	public CompareByCharacterGrid(EventBus eventBus, TreeStore<Taxon> store, List<SimpleMatrixVersion> oldVersions, Character selectedCharacter){
		this.eventBus = eventBus;
		this.taxonStore = store;
		this.oldVersions = oldVersions;
		
		setHeadingCharacter(selectedCharacter);
		
		taxonGrid = TaxonTreeGrid.createNew(eventBus, store);
		oldVersionsGrid = createOldVersionsGrid(this.taxonStore, this.oldVersions, selectedCharacter);
		
		
		oldVersionsContent = new HorizontalLayoutContainer();
		oldVersionsContent.getScrollSupport().setScrollMode(ScrollMode.AUTO);
		oldVersionsContent.setWidth(400);
		oldVersionsContent.add(oldVersionsGrid, new HorizontalLayoutData(-1, 1));
		
		content = new HorizontalLayoutContainer();
		content.add(taxonGrid, new HorizontalLayoutData(-1, 1));
		content.add(oldVersionsContent, new HorizontalLayoutData(-1, 1));
		content.getElement().getStyle().setBackgroundColor("orange");
		
		this.add(content);
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
					updateCharacterSelected(newCharacter);
				}
			}
		});
	}
	
	private void setHeadingCharacter(Character character){
		this.setHeadingText("Viewing Character: " + character);
	}
	
	private void updateCharacterSelected(Character newCharacter){
		setHeadingCharacter(newCharacter);
		oldVersionsContent.clear();
		oldVersionsGrid = createOldVersionsGrid(this.taxonStore, this.oldVersions, newCharacter);
		oldVersionsContent.add(oldVersionsGrid, new HorizontalLayoutData(-1, 1));
	}
	
	private Grid<Taxon> createOldVersionsGrid(TreeStore<Taxon> store, List<SimpleMatrixVersion> oldVersions, Character selectedCharacter){
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
		
		Grid<Taxon> grid = new Grid<Taxon>(taxonGrid.getListStore(), columnModel);
		return grid;
	}
}
