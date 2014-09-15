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
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon.Rank;

public class TaxonInformationContainer extends SimpleContainer {
	
	private TextField nameField;
	private TextField authorField;
	private TextField yearField;
	
	private AllAccessListStore<Rank> levelsStore = new AllAccessListStore<Rank>(new ModelKeyProvider<Rank>() {
		@Override
		public String getKey(Rank item) {
			return item.name();
		}
	});	    
	private ComboBox<Rank> levelComboBox = new ComboBox<Rank>(new ComboBoxCell<Rank>(levelsStore, new LabelProvider<Rank>() {
		@Override
		public String getLabel(Rank item) {
			return item.name();
		}
    }));
	private Tree<Taxon, String> taxaTree;
	private Model model;
	
	public class LevelFilter implements StoreFilter<Rank>, SelectionChangedHandler<Taxon> {
		private Set<Rank> selectableLevels = new LinkedHashSet<Rank>();
		private Rank defaultLevel;
		public LevelFilter(Rank defaultLevel) {
			this.defaultLevel = defaultLevel;
			init();
		}
		private void init() {
			for(Rank level : Rank.values())
				selectableLevels.add(level);
			if(defaultLevel == null)
				levelComboBox.setValue(Rank.GENUS);
			else 
				levelComboBox.setValue(defaultLevel);
		}
		@Override
		public boolean select(Store<Rank> store, Rank parent, Rank item) {
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
				for(Rank level : Rank.values()) {
					if(collect)
						selectableLevels.add(level);
					if(level.equals(taxon.getRank())) 
						collect = true;
				}
				if(selectableLevels.isEmpty()) {
					selectableLevels.add(Rank.values()[Rank.values().length - 1]);
				}
				if(defaultLevel == null || !selectableLevels.contains(defaultLevel))
					levelComboBox.setValue(selectableLevels.iterator().next());
				else 
					levelComboBox.setValue(defaultLevel);						
			}
			levelsStore.enableAndRefreshFilters();
		}
	}

	public TaxonInformationContainer(Model model, Taxon initialParent, final Taxon taxon) {
		this.model = model; 
		
		FieldSet fieldSet = new FieldSet();
	    fieldSet.setHeadingText("Taxon Information");
	    fieldSet.setCollapsible(false);
	    this.add(fieldSet, new MarginData(10));
	 
	    VerticalLayoutContainer p = new VerticalLayoutContainer();
	    fieldSet.add(p);

	    TaxonStore taxonStore = new TaxonStore();
		for(Taxon t : model.getTaxonMatrix().getHierarchyRootTaxa()) {
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
		
		LevelFilter levelFilter = new LevelFilter(taxon == null ? null : taxon.getRank());			
	    
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
	    
		levelsStore.addAll(Arrays.asList(Rank.values()));
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
		if(!taxon.hasParent()) { 
			taxonStore.add(taxon);
		} else {
			Taxon parent = taxon.getParent();
			while(parent != null) {
				if(!model.getTaxonMatrix().isVisiblyContained(parent))
					parent = parent.getParent();
				else 
					break;
			}
			if(parent == null) 
				taxonStore.add(taxon);
			else 
				taxonStore.add(parent, taxon);
		}
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

	public ComboBox<Rank> getLevelComboBox() {
		return levelComboBox;
	}

	public Tree<Taxon, String> getTaxaTree() {
		return taxaTree;
	}
	
	public void selectParent(Taxon taxon) {
		taxaTree.getSelectionModel().select(taxon, false);
	}
	
}

