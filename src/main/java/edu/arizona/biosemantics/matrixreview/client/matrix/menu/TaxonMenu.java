package edu.arizona.biosemantics.matrixreview.client.matrix.menu;

import java.util.LinkedList;
import java.util.List;

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
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.TaxonAddDialog;
import edu.arizona.biosemantics.matrixreview.client.common.TaxonModifyDialog;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CollapseTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ExpandTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxonFlatEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDescriptionEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.MatrixMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class TaxonMenu extends Menu {
	private EventBus eventBus;
	private Model model;
	private MatrixMode matrixMode;
	private Taxon taxon;
	private TaxonStore taxonStore;

	public TaxonMenu(EventBus eventBus, Model model, MatrixMode matrixMode, Taxon taxon, TaxonStore taxonStore)	{
		this.eventBus = eventBus;
		this.model = model;
		this.matrixMode = matrixMode;
		this.taxon = taxon;
		this.taxonStore = taxonStore;
		
		add(new HeaderMenuItem("Taxon"));
		add(createAddTaxon());
		add(createDeleteTaxon());
		add(createModifyTaxon());
		//add(createMoveTaxon());
		add(createLockTaxon());
		add(new HeaderMenuItem("View"));
		if(matrixMode.equals(MatrixMode.HIERARCHY)) {
			add(createCollapseAll());
			add(createExpandAll());
		}
		add(new HeaderMenuItem("Annotation"));
		add(createComment());
		add(createColorize());
		add(new HeaderMenuItem("Analysis"));
		add(createShowDescription());
		//add(createAnalysis());	
	}

	@Override
	public void add(Widget child) {
		if(child != null)
			super.add(child);
	}
	
	private Widget createExpandAll() {
		MenuItem item = new MenuItem("Expand All");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new ExpandTaxaEvent(taxon));
			}
		});
		return item;
	}

	private Widget createCollapseAll() {
		MenuItem item = new MenuItem("Collapse All");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new CollapseTaxaEvent(taxon));
			}
		});
		return item;
	}
	
	private MenuItem createAnalysis() {
		MenuItem item = new MenuItem("Start");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new AnalyzeTaxonEvent(taxon));
			}
		});
		return item;
	}

	private MenuItem createShowDescription() {
		MenuItem item = new MenuItem("Show description");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new ShowDescriptionEvent(taxon));
			}
		});
		return item;
	}


	private MenuItem createColorize() {
		if(model.getColors().isEmpty())
			return null;
		
		MenuItem item = new MenuItem("Colorize");
		Menu colorMenu = new Menu();
		item.setSubMenu(colorMenu);
		MenuItem offItem = new MenuItem("None");
		offItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new SetTaxonColorEvent(taxon, null));
			}
		});
		colorMenu.add(offItem);
		for(final Color color : model.getColors()) {
			MenuItem colorItem = new MenuItem(color.getUse());
			colorItem.getElement().getStyle().setProperty("backgroundColor", "#" + color.getHex());
			colorItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new SetTaxonColorEvent(taxon, color));
				}
			});
			colorMenu.add(colorItem);
		}
		
		return item;
	}


	private MenuItem createComment() {
		MenuItem item = new MenuItem("Comment");
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				final MultiLinePromptMessageBox box = new MultiLinePromptMessageBox("Comment", "");
				box.getTextArea().setValue(model.getComment(taxon));
				box.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						eventBus.fireEvent(new SetTaxonCommentEvent(taxon, box.getValue()));
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


	private MenuItem createLockTaxon() {
		final CheckMenuItem lockItem = new CheckMenuItem("Lock");
		lockItem.setChecked(model.isLocked(taxon));
		lockItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean newValue = !model.isLocked(taxon);
				lockItem.setChecked(newValue);
				eventBus.fireEvent(new LockTaxonEvent(taxon, newValue));
			}
		});
		return lockItem;
	}


	private MenuItem createMoveTaxon() {
		MenuItem item = new MenuItem("Move after");
		Menu moveMenu = new Menu();
		item.setSubMenu(moveMenu);
		
		switch(matrixMode) {
		case FLAT:
			if(model.getTaxonMatrix().getHierarchyTaxaDFS().isEmpty())
				return null;
			
			MenuItem subItem = new MenuItem("start");
			subItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new MoveTaxonFlatEvent(taxon, null));
				}
			});
			moveMenu.add(subItem);
			
			for(final Taxon after : taxonStore.getRootItems()) {
				if(!after.equals(taxon)) {
					subItem = new MenuItem(after.getFullName());
					subItem.addSelectionHandler(new SelectionHandler<Item>() {
						@Override
						public void onSelection(SelectionEvent<Item> event) {
							eventBus.fireEvent(new MoveTaxonFlatEvent(taxon, after));
						}
					});
					moveMenu.add(subItem);
				}
			}
			break;
		case HIERARCHY:		
			subItem = new MenuItem("start");
			subItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					eventBus.fireEvent(new MoveTaxonFlatEvent(taxon, null));
				}
			});
			moveMenu.add(subItem);
			
			List<Taxon> moveLocations = new LinkedList<Taxon>(taxonStore.getRootItems());
			if(taxon.hasParent()) {
				moveLocations = new LinkedList<Taxon>(taxon.getParent().getChildren());
			}
			moveLocations.remove(taxon);
			
			if(moveLocations.isEmpty())
				return null;
			
			for(final Taxon after : moveLocations) {
				subItem = new MenuItem(after.getFullName());
				subItem.addSelectionHandler(new SelectionHandler<Item>() {
					@Override
					public void onSelection(SelectionEvent<Item> event) {
						eventBus.fireEvent(new MoveTaxonFlatEvent(taxon, after));
					}
				});
				moveMenu.add(subItem);
			}
			break;
		}
		
		return item;
	}


	private MenuItem createModifyTaxon() {
		MenuItem item = new MenuItem();
		item.setText("Modify");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				TaxonModifyDialog modifyDialog = new TaxonModifyDialog(eventBus, model, taxon);
				modifyDialog.show();
				if(taxon.getParent() != null)
					modifyDialog.selectParent(taxon.getParent());
			}
		});
		return item;
	}


	private MenuItem createDeleteTaxon() {
		MenuItem item = new MenuItem();
		item.setText("Delete");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				eventBus.fireEvent(new RemoveTaxaEvent(taxon));
			}
		});
		return item;
	}


	private MenuItem createAddTaxon() {
		MenuItem item = new MenuItem();
		item.setText("Add");
		// item.setIcon(header.getAppearance().sortAscendingIcon());
		item.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				TaxonAddDialog addDialog = new TaxonAddDialog(eventBus, model, taxon);
				addDialog.show();
				addDialog.selectParent(taxon);
			}
		});
		return item;
	}


	/*private void restrictMenu(Menu rows) {
	//private void restrictMenu(ColumnModel<Taxon> cm, Menu columns) {
		//TODO for rows rather than columns
		/*int count = 0;
		for (int i = 0, len = cm.getColumnCount(); i < len; i++) {
			if (hasHeaderValue(i)) {
				ColumnConfig<M, ?> cc = cm.getColumn(i);
				if (cc.isHidden() || !cc.isHideable()) {
					continue;
				}
				count++;
			}
		}

		if (count == 1) {
			for (Widget item : columns) {
				CheckMenuItem ci = (CheckMenuItem) item;
				if (ci.isChecked()) {
					ci.disable();
				}
			}
		} else {
			for (int i = 0, len = columns.getWidgetCount(); i < len; i++) {
				Widget item = columns.getWidget(i);
				ColumnConfig<M, ?> config = cm.getColumn(i);
				if (item instanceof Component && config.isHideable()) {
					((Component) item).enable();
				}
			}
		} */
	//}
}
