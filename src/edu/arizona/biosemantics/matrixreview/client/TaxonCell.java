package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.widget.core.client.grid.MyGrid;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxonCell<C> extends MenuExtendedCell<C> {

	private MyGrid<Taxon> grid;

	public TaxonCell(MyGrid<Taxon> grid) {
		this.grid = grid;
	}

	@Override
	public void render(Context context, C value, SafeHtmlBuilder sb) {
		if (value == null)
			return;

		SafeHtml rendered = templates.cell(columnHeaderStyles.header() + " "
				+ columnHeaderStyles.head(), columnHeaderStyles.headInner(),
				columnHeaderStyles.headButton(), value.toString());
		sb.append(rendered);
	}

	@Override
	protected Menu createContextMenu(final int colIndex, final int rowIndex) {
		final Menu menu = new Menu();
		
		MenuItem item = new MenuItem("Move after");
		menu.add(item);
		Menu moveMenu = new Menu();
		item.setSubMenu(moveMenu);

		item = new MenuItem("start");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Taxon taxon =  grid.getStore().remove(rowIndex);
				grid.getStore().add(0, taxon);
			}
		});
		moveMenu.add(item);
		
		int rows = grid.getStore().size();
		for (int i = 0; i < rows; i++) {
			final int theI = i;
			item = new MenuItem(grid.getStore().get(i).getName());
			item.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					Taxon taxon =  grid.getStore().remove(rowIndex);
					int finalI = theI;
					if(rowIndex < theI)
						finalI--;				
					grid.getStore().add(finalI + 1, taxon);
				}
			});
			moveMenu.add(item);
		}
		
		
		return menu;
	}
}
