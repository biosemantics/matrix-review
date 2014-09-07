package edu.arizona.biosemantics.matrixreview.client.matrix.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Params;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.MultiLinePromptMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.XEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.CheckMenuItem;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import edu.arizona.biosemantics.matrixreview.client.common.TaxonAddDialog;
import edu.arizona.biosemantics.matrixreview.client.common.TaxonModifyDialog;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.CollapseTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ExpandTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxonFlatEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDescriptionEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView.ModelMode;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.AllAccessListStore;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon.Level;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;

public class TaxonMenu extends Menu {


	
	private EventBus eventBus;
	private TaxonMatrix taxonMatrix;
	private ModelMode modelMode;
	private Taxon taxon;
	private TaxonStore taxonStore;

	public TaxonMenu(EventBus eventBus, TaxonMatrix taxonMatrix, ModelMode modelMode, Taxon taxon, TaxonStore taxonStore)	{
		this.eventBus = eventBus;
		this.taxonMatrix = taxonMatrix;
		this.modelMode = modelMode;
		this.taxon = taxon;
		this.taxonStore = taxonStore;
		
		add(new HeaderMenuItem("Taxon"));
		add(createAddTaxon());
		add(createDeleteTaxon());
		add(createModifyTaxon());
		add(createMoveTaxon());
		add(createLockTaxon());
		add(new HeaderMenuItem("View"));
		if(modelMode.equals(ModelMode.TAXONOMIC_HIERARCHY)) {
			add(createCollapseAll());
			add(createExpandAll());
		}
		add(new HeaderMenuItem("Annotation"));
		add(createComment());
		add(createColorize());
		add(new HeaderMenuItem("Analysis"));
		add(createShowDescription());
		//add(createAnalysis());	
		
		bindEvents();
	}
	
	private void bindEvents() {
		eventBus.addHandler(LoadTaxonMatrixEvent.TYPE, new LoadTaxonMatrixEvent.LoadTaxonMatrixEventHandler() {
			@Override
			public void onLoad(LoadTaxonMatrixEvent event) {
				taxonMatrix = event.getTaxonMatrix();
			}
		});
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
		if(taxonMatrix.getColors().isEmpty())
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
		for(final Color color : taxonMatrix.getColors()) {
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
				box.getTextArea().setValue(taxon.getComment());
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
		lockItem.setChecked(taxon.isLocked());
		lockItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				boolean newValue = !taxon.isLocked();
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
		
		switch(modelMode) {
		case FLAT:
			if(taxonMatrix.list().isEmpty())
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
		case CUSTOM_HIERARCHY:
		case TAXONOMIC_HIERARCHY:		
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
				moveLocations = taxon.getParent().getChildren();
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
				TaxonModifyDialog modifyDialog = new TaxonModifyDialog(eventBus, taxonMatrix, taxon);
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
				TaxonAddDialog addDialog = new TaxonAddDialog(eventBus, taxonMatrix, taxon);
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
