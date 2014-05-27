package edu.arizona.biosemantics.matrixreview.client.matrix.menu;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class ValueMenu extends Menu {

	private EventBus eventBus;
	private TaxonMatrix taxonMatrix;
	private Taxon taxon;
	private Character character;
	private Value value;

	public ValueMenu(EventBus eventBus, TaxonMatrix taxonMatrix, Value value, Taxon taxon, Character character) {
		this.eventBus = eventBus;
		this.taxonMatrix = taxonMatrix;
		this.value = value;
		this.taxon = taxon;
		this.character = character;
		
		add(new HeaderMenuItem("Annotation"));
		add(createComment());
		add(createColorize());
	}
	
	@Override
	public void add(Widget child) {
		if(child != null)
			super.add(child);
	}

	private Widget createColorize() {
		if(taxonMatrix.getColors().isEmpty())
			return null;
		
		MenuItem item = new MenuItem("Colorize");
		Menu colorMenu = new Menu();
		item.setSubMenu(colorMenu);
		MenuItem offItem = new MenuItem("None");
		offItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SetValueColorEvent(value, null));
			}
		});
		colorMenu.add(offItem);
		for(final Color color : taxonMatrix.getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new SetValueColorEvent(value, color));
				}
			});
			colorMenu.add(colorItem);
		}
		return item;
	}

	private Widget createComment() {
		MenuItem item = new MenuItem("Comment");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
				box.setValue(value.getComment());
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						eventBus.fireEvent(new SetValueCommentEvent(value, box.getValue()));
						String comment = Format.ellipse(box.getValue(), 80);
						String message = Format.substitute("'{0}' saved", new Params(comment));
						Info.display("Comment", message);
					}
				});
				box.show();
			}
		});
		return item;
	}
	
}
