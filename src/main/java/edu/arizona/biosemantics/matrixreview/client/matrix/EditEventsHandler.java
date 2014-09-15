package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.BeforeStartEditHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;

import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class EditEventsHandler implements CompleteEditHandler<Taxon>, BeforeStartEditHandler<Taxon> {
	
	private Value oldValue;
	// underlying GXT implementation fires CompleteEditEvent multiple times for
	// some reason
	private boolean fired = true;
	private EventBus eventBus;
	private FrozenFirstColumTaxonTreeGrid taxonTreeGrid;
	
	public EditEventsHandler(EventBus eventBus, FrozenFirstColumTaxonTreeGrid taxonTreeGrid) {
		this.eventBus = eventBus;
		this.taxonTreeGrid = taxonTreeGrid;
	}

	@Override
	public void onCompleteEdit(CompleteEditEvent<Taxon> event) {
		if (!fired) {
			GridCell cell = event.getEditCell();
			Taxon taxon = taxonTreeGrid.getTreeGrid().getListStore()
					.get(cell.getRow());
			CharacterColumnConfig config = taxonTreeGrid.getColumnModel()
					.getColumn(cell.getCol());
			Value value = config.getValueProvider().getValue(taxon);
			eventBus.fireEvent(new SetValueEvent(taxon, config.getCharacter(), oldValue, value));
			fired = true;
			oldValue = null;
		}
	}

	@Override
	public void onBeforeStartEdit(BeforeStartEditEvent<Taxon> event) {
		if (fired) {
			GridCell cell = event.getEditCell();
			Taxon taxon = taxonTreeGrid.getTreeGrid().getListStore()
					.get(cell.getRow());
			ColumnConfig<Taxon, Value> config = taxonTreeGrid.getColumnModel()
					.getColumn(cell.getCol());
			oldValue = config.getValueProvider().getValue(taxon);
			fired = false;
		}
	}

}