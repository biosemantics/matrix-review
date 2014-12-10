package edu.arizona.biosemantics.matrixreview.client.matrix.filters;

import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.data.shared.event.StoreFilterEvent;
import com.sencha.gxt.data.shared.event.StoreFilterEvent.StoreFilterHandler;
import com.sencha.gxt.widget.core.client.event.BeforeFilterEvent;
import com.sencha.gxt.widget.core.client.event.BeforeFilterEvent.BeforeFilterHandler;
import com.sencha.gxt.widget.core.client.grid.GridView;

public class ScrollStateMaintainer<M> implements StoreFilterHandler<M>, BeforeFilterHandler {

	private Point maintainScrollState = new Point(0, 0);
	private GridView<M> gridView;
	
	public ScrollStateMaintainer(GridView<M> gridView) {
		this.gridView = gridView;
	}

	@Override
	public void onFilter(StoreFilterEvent<M> event) {
		gridView.restoreScroll(maintainScrollState);
	}

	@Override
	public void onBeforeFilter(BeforeFilterEvent event) {
		maintainScrollState = gridView.getScrollState();
	}
}