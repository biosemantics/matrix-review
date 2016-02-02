package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.desktop.Window;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class DescriptionManager extends AbstractWindowManager {

	private Taxon taxon;
	private Model fullModel;
	private Model subModel;
	private TextArea textArea;
	private Model displayedModel;

	public DescriptionManager(EventBus fullModelEventBus, EventBus subModelEventBus, Window window, Taxon taxon, 
			Model fullModel, Model subModel) {
		super(fullModelEventBus, subModelEventBus, window);
		this.taxon = taxon;
		this.fullModel = fullModel;
		this.subModel = subModel;
		this.displayedModel = subModel;
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
		EventBus[] busses = { fullMatrixEventBus, subMatrixEventBus };
		for(EventBus bus : busses) {
			bus.addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
				@Override
				public void onModify(ModifyTaxonEvent event) {
					if(event.getTaxon().equals(taxon))
						refreshTitle();
				}
			});
			bus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
				@Override
				public void onLoad(LoadModelEvent event) {
					displayedModel = event.getModel();
				}
			});
		}
	}
	
	@Override
	public void refreshContextMenu() {
		Menu contextMenu = new Menu();
		MenuItem setStateItem = new MenuItem("Set State");
		contextMenu.add(setStateItem);	
		final Menu characterMenu = new Menu();
		setStateItem.setSubMenu(characterMenu);
		
		contextMenu.addBeforeShowHandler(new BeforeShowHandler() {

			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				characterMenu.clear();
				for(Organ organ : displayedModel.getTaxonMatrix().getHierarchyCharacters()) {
					MenuItem organItem = new MenuItem(organ.getName());
					Menu sub = new Menu();
					organItem.setSubMenu(sub);
					for(final Character character : organ.getFlatCharacters()) {
						if(displayedModel.getTaxonMatrix().isVisiblyContained(character)) {
							MenuItem characterItem = new MenuItem(character.getName());
							sub.add(characterItem);
							characterItem.addSelectionHandler(new SelectionHandler<Item>() {
								@Override
								public void onSelection(SelectionEvent<Item> event) {
									subMatrixEventBus.fireEvent(new SetValueEvent(taxon, character, 
											displayedModel.getTaxonMatrix().getValue(taxon, character), new Value(textArea.getSelectedText())));
								}
							});
						}
					}
					characterMenu.add(organItem);
				}
			}
			
		});
		
		window.setContextMenu(contextMenu);
	}

	@Override
	public void refreshTitle() {
		window.setHeadingText("Description of " + taxon.getBiologicalName());
	}
	
}
