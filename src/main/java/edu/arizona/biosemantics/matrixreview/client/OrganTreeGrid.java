package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;

import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganProperties;

public class OrganTreeGrid extends MaintainListStoreTreeGrid<Organ>{
	
	private EventBus eventBus;
	
	private OrganTreeGrid(EventBus eventBus, TreeStore<Organ> treeStore, ColumnModel<Organ> model, ColumnConfig<Organ, String> column){
		super(treeStore, model, column);
		this.eventBus = eventBus;
	}
	
	public static OrganTreeGrid createNew(EventBus eventBus, TreeStore<Organ> store){
		final OrganProperties OrganProperties = GWT.create(OrganProperties.class);
		ColumnConfig<Organ, String> column = new ColumnConfig<Organ, String>(OrganProperties.name(), 150);
		column.setHeader("Characters");
		
		List<ColumnConfig<Organ, ?>> columns = new ArrayList<ColumnConfig<Organ, ?>>();
		columns.add(column);
		ColumnModel<Organ> columnModel = new ColumnModel<Organ>(columns);
		
		return new OrganTreeGrid(eventBus, store, columnModel, column);
	}
}
