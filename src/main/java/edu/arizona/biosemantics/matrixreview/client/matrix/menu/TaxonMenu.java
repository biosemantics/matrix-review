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

import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxonFlatEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDescriptionEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView.ModelMode;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.AllAccessListStore;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon.Level;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;

public class TaxonMenu extends Menu {

	public static class TaxonInformationContainer extends SimpleContainer {
		
		private TextField nameField;
		private TextField authorField;
		private TextField yearField;
		
		private AllAccessListStore<Level> levelsStore = new AllAccessListStore<Level>(new ModelKeyProvider<Level>() {
			@Override
			public String getKey(Level item) {
				return item.name();
			}
		});	    
		private ComboBox<Level> levelComboBox = new ComboBox<Level>(new ComboBoxCell<Level>(levelsStore, new LabelProvider<Level>() {
			@Override
			public String getLabel(Level item) {
				return item.name();
			}
	    }));
		private Tree<Taxon, String> taxaTree;
		
		public class LevelFilter implements StoreFilter<Level>, SelectionChangedHandler<Taxon> {
			private Set<Level> selectableLevels = new LinkedHashSet<Level>();
			private Level defaultLevel;
			public LevelFilter(Level defaultLevel) {
				this.defaultLevel = defaultLevel;
				init();
			}
			private void init() {
				for(Level level : Level.values())
					selectableLevels.add(level);
				if(defaultLevel == null)
					levelComboBox.setValue(Level.GENUS);
				else 
					levelComboBox.setValue(defaultLevel);
			}
			@Override
			public boolean select(Store<Level> store, Level parent, Level item) {
				return selectableLevels.contains(item);
			}
			@Override
			public void onSelectionChanged(SelectionChangedEvent<Taxon> event) {
				selectableLevels.clear();
				List<Taxon> selection = event.getSelection();
				if(selection.isEmpty())
					init();
				else {
					Taxon taxon = selection.get(0);
					boolean collect = false;
					for(Level level : Level.values()) {
						if(collect)
							selectableLevels.add(level);
						if(level.equals(taxon.getLevel())) 
							collect = true;
					}
					if(selectableLevels.isEmpty()) {
						selectableLevels.add(Level.values()[Level.values().length - 1]);
					}
					if(defaultLevel == null || !selectableLevels.contains(defaultLevel))
						levelComboBox.setValue(selectableLevels.iterator().next());
					else 
						levelComboBox.setValue(defaultLevel);						
				}
				levelsStore.enableAndRefreshFilters();
			}
		}

		public TaxonInformationContainer(TaxonMatrix taxonMatrix, Taxon initialParent, final Taxon taxon) {
			FieldSet fieldSet = new FieldSet();
		    fieldSet.setHeadingText("Taxon Information");
		    fieldSet.setCollapsible(true);
		    this.add(fieldSet, new MarginData(10));
		 
		    VerticalLayoutContainer p = new VerticalLayoutContainer();
		    fieldSet.add(p);

		    TaxonStore taxonStore = new TaxonStore();
			for(Taxon t : taxonMatrix.list()) {
				if(t.hasParent())
					continue;
				insertToStoreRecursively(taxonStore, t);
			}
			taxonStore.setEnableFilters(true);
			taxonStore.addFilter(new StoreFilter<Taxon>() {
				@Override
				public boolean select(Store<Taxon> store, Taxon parent, Taxon item) {
					return !filter(item);
				}
				public boolean filter(Taxon a) {
					return a.equals(taxon) || a.hasParent() && filter(a.getParent());
				}
				
			});
			
			LevelFilter levelFilter = new LevelFilter(taxon == null ? null : taxon.getLevel());			
		    
		    TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);
		    taxaTree = new Tree<Taxon, String>(taxonStore, taxonProperties.fullName());
		    //SelectionModel.SINGLE doesnt deselect upon second click (also not with CTRL), see TreeSelectionModel impl.
		    //SelectionModel.SIMPLE doesnt restrict to a single selection, hence custom implementation
		    //taxaTree.getSelectionModel().setSelectionMode(SelectionMode.SIMPLE);
			taxaTree.setSelectionModel(new TreeSelectionModel<Taxon>() {
				@Override
				protected void onMouseDown(NativeEvent e) {
					XEvent xe = e.<XEvent> cast();
					Element target = e.getEventTarget().cast();
					TreeNode<Taxon> node = tree.findNode(target);
					if (node == null) {
						return;
					}
					Taxon item = (Taxon) node.getModel();
					if (item == null)
						return;
					if (!tree.getView().isSelectableTarget(item, target)) {
						return;
					}
					if (e.<XEvent> cast().isRightClick()
							&& isSelected((Taxon) item)) {
						return;
					}
					mouseDown = true;
					Taxon sel = item;
					
					tree.focus();
					if (isSelected(sel)) {
			          deselect(sel);
			        } else {
			        	doSingleSelect(sel, false);
			        }
					
					mouseDown = false;
				}
			});
			taxaTree.getSelectionModel().addSelectionChangedHandler(levelFilter);
		    
		    //if(initialParent != null)
		   // 	taxaTree.getSelectionModel().select(initialParent, false);
		    ScrollPanel scrollPanel = new ScrollPanel(taxaTree);
		    scrollPanel.getElement().getStyle().setBackgroundColor("white");
		    scrollPanel.setHeight("200px");
		    p.add(new FieldLabel(scrollPanel, "Parent"), new VerticalLayoutData(1, -1));
		    
			levelsStore.addAll(Arrays.asList(Level.values()));
			levelsStore.addFilter(levelFilter);
		    levelComboBox.setAllowBlank(false);
		    levelComboBox.setForceSelection(true);
		    levelComboBox.setTriggerAction(TriggerAction.ALL);
		    p.add(new FieldLabel(levelComboBox, "Level"), new VerticalLayoutData(1, -1));
		 
		    nameField = new TextField();
		    nameField.setText(taxon == null ? "" : taxon.getName());
		    nameField.setAllowBlank(false);
		    p.add(new FieldLabel(nameField, "Taxon Name"), new VerticalLayoutData(1, -1));
		 
		    authorField = new TextField();
		    authorField.setText(taxon == null ? "" : taxon.getAuthor());
		    authorField.setAllowBlank(false);
		    p.add(new FieldLabel(authorField, "Author of Publication"), new VerticalLayoutData(1, -1));
		 
		    yearField = new TextField();
		    yearField.setText(taxon == null ? "" : taxon.getYear());
		    yearField.setAllowBlank(false);
		    p.add(new FieldLabel(yearField, "Year of Publication"), new VerticalLayoutData(1, -1));
		}
		
		private void insertToStoreRecursively(TaxonStore taxonStore, Taxon taxon) {
			if(!taxon.hasParent())
				taxonStore.add(taxon);
			else 
				taxonStore.add(taxon.getParent(), taxon);
			for(Taxon child : taxon.getChildren())
				insertToStoreRecursively(taxonStore, child);
		}

		public TextField getNameField() {
			return nameField;
		}

		public TextField getAuthorField() {
			return authorField;
		}

		public TextField getYearField() {
			return yearField;
		}

		public ComboBox<Level> getLevelComboBox() {
			return levelComboBox;
		}

		public Tree<Taxon, String> getTaxaTree() {
			return taxaTree;
		}
		
		public void selectParent(Taxon taxon) {
			taxaTree.getSelectionModel().select(taxon, false);
		}
		
	}
	
	public static class TaxonAddDialog extends Dialog {
		
		private TaxonInformationContainer taxonInformationContainer;

		public TaxonAddDialog(final EventBus eventBus, TaxonMatrix taxonMatrix, Taxon initialParent) {
			this.setHeadingText("Add Taxon");
			taxonInformationContainer = new TaxonInformationContainer(taxonMatrix, initialParent, null);
		    this.add(taxonInformationContainer);
		 
		    final Tree<Taxon, String> taxaTree = taxonInformationContainer.getTaxaTree();
		    final ComboBox<Level> levelCombo = taxonInformationContainer.getLevelComboBox(); 
		    final TextField nameField = taxonInformationContainer.getNameField();
		    final TextField authorField = taxonInformationContainer.getAuthorField();
		    final TextField yearField = taxonInformationContainer.getYearField();
		    
		    getButtonBar().clear();
		    TextButton add = new TextButton("Add");
		    add.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					if(!nameField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Name empty", "A name is required");
						alert.show();
						return;
					}
					if(!authorField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Author empty", "An author is required");
						alert.show();
						return;
					}
					if(!yearField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Year empty", "A year is required");
						alert.show();
						return;
					}
					
					Taxon newTaxon = new Taxon(String.valueOf(Taxon.currentId++), 
							levelCombo.getValue(), nameField.getText(), authorField.getText(), yearField.getText());
					eventBus.fireEvent(new AddTaxonEvent(newTaxon, taxaTree.getSelectionModel().getSelectedItem()));
					TaxonAddDialog.this.hide();
				}
		    });
		    TextButton cancel =  new TextButton("Cancel");
		    cancel.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					TaxonAddDialog.this.hide();
				}
		    });
		    addButton(add);
		    addButton(cancel);
		}

		public void selectParent(Taxon taxon) {
			taxonInformationContainer.selectParent(taxon);
		}
	}
	
	public static class TaxonModifyDialog extends Dialog {
		
		private TaxonInformationContainer taxonInformationContainer;

		public TaxonModifyDialog(final EventBus eventBus, final TaxonMatrix taxonMatrix, final Taxon taxon) {
			this.setHeadingText("Modify Taxon");
			taxonInformationContainer = new TaxonInformationContainer(taxonMatrix, taxon.getParent(), taxon);
		    this.add(taxonInformationContainer);
			
		    final Tree<Taxon, String> taxaTree = taxonInformationContainer.getTaxaTree();
			final ComboBox<Level> levelCombo = taxonInformationContainer.getLevelComboBox(); 
		    final TextField nameField = taxonInformationContainer.getNameField();
		    final TextField authorField = taxonInformationContainer.getAuthorField();
		    final TextField yearField = taxonInformationContainer.getYearField();
		 
		    getButtonBar().clear();
		    TextButton save = new TextButton("Save");
		    save.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					if(!nameField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Name empty", "A name is required");
						alert.show();
						return;
					}
					if(!authorField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Author empty", "An author is required");
						alert.show();
						return;
					}
					if(!yearField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Year empty", "A year is required");
						alert.show();
						return;
					}
					eventBus.fireEvent(new ModifyTaxonEvent(taxon, taxaTree.getSelectionModel().getSelectedItem(), 
							levelCombo.getValue(), nameField.getText(), authorField.getText(), yearField.getText()));
					TaxonModifyDialog.this.hide();
				}
		    });
		    TextButton cancel =  new TextButton("Cancel");
		    cancel.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					TaxonModifyDialog.this.hide();
				}
		    });
		    addButton(save);
		    addButton(cancel);
		}
		
		public void selectParent(Taxon taxon) {
			taxonInformationContainer.selectParent(taxon);
		}
	}

	
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
				box.setValue(taxon.getComment());
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
				eventBus.fireEvent(new RemoveTaxonEvent(taxon));
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
				TaxonMenu.TaxonAddDialog addDialog = new TaxonMenu.TaxonAddDialog(eventBus, taxonMatrix, taxon);
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
