package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.TreeSelectionModel;

import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.AllAccessListStore;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon.Level;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;

public class TaxonInformationContainer extends SimpleContainer {
	
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
	    fieldSet.setCollapsible(false);
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
			protected void onMouseDown(MouseDownEvent mde) {
				/*				    
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
				
				mouseDown = false;*/
				super.onMouseDown(mde);
			}
		});
		taxaTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
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
	    nameField.setValue(taxon == null ? "" : taxon.getName());
	    nameField.setAllowBlank(false);
	    p.add(new FieldLabel(nameField, "Taxon Name"), new VerticalLayoutData(1, -1));
	 
	    authorField = new TextField();
	    authorField.setValue(taxon == null ? "" : taxon.getAuthor());
	    authorField.setAllowBlank(false);
	    p.add(new FieldLabel(authorField, "Author of Publication"), new VerticalLayoutData(1, -1));
	 
	    yearField = new TextField();
	    yearField.setValue(taxon == null ? "" : taxon.getYear());
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

