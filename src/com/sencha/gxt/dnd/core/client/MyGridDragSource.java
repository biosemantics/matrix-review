package com.sencha.gxt.dnd.core.client;

import java.util.List;

import com.google.gwt.dom.client.Element;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class MyGridDragSource<M> extends GridDragSource<M> {

	public MyGridDragSource(Grid<M> grid) {
		super(grid);
	}

	@Override
	protected void onDragStart(DndDragStartEvent event) {
		Element r = grid.getView().findRow(event.getDragStartEvent().getStartElement()).cast();
		if (r == null) {
			event.setCancelled(true);
			return;
		}

		int index = grid.getView().findRowIndex(event.getDragStartEvent().getStartElement());
		List<M> sel = grid.getSelectionModel().getSelectedItems();
		M sourceDrag = grid.getStore().get(index);
		if (!sel.contains(sourceDrag))
			sel.add(sourceDrag);
		
		if (sel.size() > 0) {
			event.setCancelled(false);
			event.setData(sel);

			if (getStatusText() == null) {
				event.getStatusProxy().update(getMessages().itemsSelected(sel.size()));
			} else {
				event.getStatusProxy().update(Format.substitute(getStatusText(), sel.size()));
			}
		}
	}

}
