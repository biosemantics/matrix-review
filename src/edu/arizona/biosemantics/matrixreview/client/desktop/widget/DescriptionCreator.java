package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class DescriptionCreator extends WidgetCreator {

	private Taxon taxon;
	private TaxonMatrix taxonMatrix;
	private EventBus eventBus;

	public DescriptionCreator(EventBus eventBus, Taxon taxon, TaxonMatrix taxonMatrix) {
		this.eventBus = eventBus;
		this.taxon = taxon;
		this.taxonMatrix = taxonMatrix;
	}
	
	@Override
	public Widget create() {		
		final TextArea textArea = new TextArea();
		textArea.setText(taxon.getDescription());
		//textArea.setEnabled(false);
		DragSource source = new DragSource(textArea);
		source.addDragStartHandler(new DndDragStartHandler() {
			@Override
			public void onDragStart(DndDragStartEvent event) {
				event.setData(textArea.getSelectedText());
			}
		});
		
		
		contextMenu = new Menu();
		MenuItem item = new MenuItem("Set State");
		Menu characterMenu = new Menu();
		for(final Character character : taxonMatrix.getCharacters()) {
			MenuItem characterItem = new MenuItem(character.toString());
			characterMenu.add(characterItem);
			characterItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new SetValueEvent(taxon.get(character), new Value(textArea.getSelectedText()), false));
				}
			});
		}
		item.setSubMenu(characterMenu);
		contextMenu.add(item);
		return textArea;
	}
	
}
