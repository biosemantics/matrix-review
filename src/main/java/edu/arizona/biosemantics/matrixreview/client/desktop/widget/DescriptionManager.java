package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.desktop.Window;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class DescriptionManager extends AbstractWindowManager {

	private Taxon taxon;
	private Model model;
	private TextArea textArea;

	public DescriptionManager(EventBus eventBus, Window window, Taxon taxon, Model model) {
		super(eventBus, window);
		this.taxon = taxon;
		this.model = model;
		init();
	}
	
	@Override
	public void refreshContent() {
		textArea = new TextArea();
		textArea.setText(taxon.getDescription());
		//textArea.setEnabled(false);
		DragSource source = new DragSource(textArea);
		source.addDragStartHandler(new DndDragStartHandler() {
			@Override
			public void onDragStart(DndDragStartEvent event) {
				event.setData(textArea.getSelectedText());
			}
		});
		window.setWidget(textArea);
	}

	@Override
	protected void addEventHandlers() {
		subMatrixEventBus.addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
			@Override
			public void onModify(ModifyTaxonEvent event) {
				if(event.getTaxon().equals(taxon))
					refreshTitle();
			}
		});
	}
	
	@Override
	public void refreshContextMenu() {
		Menu contextMenu = new Menu();
		MenuItem item = new MenuItem("Set State");
		Menu characterMenu = new Menu();
		for(final Character character : model.getTaxonMatrix().getVisibleFlatCharacters()) {
			MenuItem characterItem = new MenuItem(character.toString());
			characterMenu.add(characterItem);
			characterItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					subMatrixEventBus.fireEvent(new SetValueEvent(taxon, character, 
							model.getTaxonMatrix().getValue(taxon, character), new Value(textArea.getSelectedText())));
				}
			});
		}
		item.setSubMenu(characterMenu);
		contextMenu.add(item);
		window.setContextMenu(contextMenu);
	}

	@Override
	public void refreshTitle() {
		window.setHeadingText("Description of " + taxon.getFullName());
	}
	
}
