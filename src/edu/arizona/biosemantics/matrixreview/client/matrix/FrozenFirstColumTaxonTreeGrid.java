package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.TreeGridDragSource;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.BeforeStartEditHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.editing.MyGridInlineEditing;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.treegrid.FrozenFirstColumnTreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.MaintainListStoreTreeGrid;
import com.sencha.gxt.widget.core.client.treegrid.TreeGridView;

import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModelModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharactersGridView.SortInfo;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView.ModelMode;
import edu.arizona.biosemantics.matrixreview.client.matrix.editing.LockableControlableMatrixEditing;
import edu.arizona.biosemantics.matrixreview.client.matrix.editing.ValueConverter;
import edu.arizona.biosemantics.matrixreview.client.matrix.filters.HideTaxonStoreFilter;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.TaxonMenu;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.TaxonCharacterMenu;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.dnd.UpdateModelDragSource;
import edu.arizona.biosemantics.matrixreview.client.matrix.menu.dnd.UpdateModelDropTarget;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class FrozenFirstColumTaxonTreeGrid extends FrozenFirstColumnTreeGrid<Taxon> {
	
	public class TaxaTreeGrid extends MaintainListStoreTreeGrid<Taxon> {
			
		public class TaxonTreeGridNode extends TreeGridNode<Taxon> {

			private Element menuElement;
			
			protected TaxonTreeGridNode(String modelId, Taxon taxon, String domId) {
				super(modelId, taxon, domId);
			}

			@Override
			public void clearElements() {
				super.clearElements();
				setContainerElement(null);
				setElContainer(null);
				element = null;
			}
			

			public void setMenuElement(Element menuElement) {
				this.menuElement = menuElement;
			}

			public Element getMenuElement() {
				return menuElement;
			}

		}
		
		public class TaxonTreeGridView extends TreeGridView<Taxon> {
			
			private EventBus eventBus;
			
			public TaxonTreeGridView(EventBus eventBus) {
				this.eventBus = eventBus;
				addEventHandlers();
			}

			@Override
			protected Menu createContextMenu(final int colIndex) {
				return new TaxonCharacterMenu(eventBus, taxonMatrix, modelMode, treeStore);
			}

			public Element getMenuElement(TreeNode<Taxon> node) {
				if(node instanceof TaxonTreeGridNode) {
					TaxonTreeGridNode taxonTreeGridNode = (TaxonTreeGridNode)node;
					if (taxonTreeGridNode.getMenuElement() == null) {
						Element row = getRowElement(node);
						if (row != null) {
							XElement r = row.cast();
							XElement menuElement = ((TaxaTreeGrid)tree).getTreeAppearance().findMenuElement(r);
							taxonTreeGridNode.setMenuElement(menuElement);
						}
					}
					return taxonTreeGridNode.getMenuElement();
				} else
					return null;
			}
			
			
			@Override
			protected void initHeader() {
				if (header == null) {
					header = new TaxaColumnHeader((TaxaTreeGrid)grid, cm);
				}
				super.initHeader();
			}
			
			private SortDir sortDir = null;
			
			@Override
			protected void onHeaderClick(int column) {
				this.headerColumnIndex = column;
				if (!headerDisabled && cm.isSortable(column)) {
					SortDir sortDir = SortDir.ASC;
					if(this.sortDir != null)
						sortDir = this.sortDir.equals(SortDir.ASC) ? SortDir.DESC : SortDir.ASC;
					this.sortDir = sortDir;
					eventBus.fireEvent(new SortTaxaByNameEvent(sortDir));
					updateSortIcon(column, sortDir);
				}
			}
			
			private void addEventHandlers() {
				eventBus.addHandler(SortTaxaByCharacterEvent.TYPE, new SortTaxaByCharacterEvent.SortTaxaByCharacterEventHandler() {
					@Override
					public void onSort(SortTaxaByCharacterEvent event) {
						getHeader().removeSortIcon();
						sortDir = null;
					}
				});
			}
			
			@Override
			public TaxaColumnHeader getHeader() {
				return (TaxaColumnHeader) header;
			}
		}
		
		private ModelMode modelMode = ModelMode.TAXONOMIC_HIERARCHY;
		private TaxonStore taxonStore;
		
		public TaxaTreeGrid(TaxonStore store, TaxaColumnModel cm, TaxaColumnConfig treeColumn) {
			super(store, cm, treeColumn, GWT.<GridAppearance> create(GridAppearance.class), 
				GWT.<TreeAppearance> create(TaxonTreeAppearance.class));
			this.setView(new TaxonTreeGridView(eventBus));
			this.taxonStore = store;
			
			eventBus.addHandler(ModelModeEvent.TYPE, new ModelModeEvent.ModelModeEventHandler() {
				@Override
				public void onMode(ModelModeEvent event) {
					modelMode = event.getMode();
				}
			});
		}
		
		@Override
		protected void onClick(Event event) {
			EventTarget eventTarget = event.getEventTarget();
			if(Element.is(eventTarget)) {
				Taxon taxon = store.get(getView().findRowIndex(Element.as(eventTarget)));
				if(taxon != null) {
					TreeNode<Taxon> node = findNode(taxon);
					if (node != null) {
						Element menuEl = ((TaxonTreeGridView)treeGridView).getMenuElement(node);
						if (menuEl != null && menuEl.isOrHasChild((Element.as(eventTarget)))) {
							showContextMenu(menuEl, taxon);
							return;
						}
					}
				}
			}
			super.onClick(event);
		}
		
		@Override
		protected <N> Cell<?> fireEventToCell(Event event, String eventType,
				Element cellParent, final Taxon m, Context context,
				final ColumnConfig<Taxon, N> column) {
			Cell<?> cell = super.fireEventToCell(event, eventType, cellParent, m, context, column);
			
			//treeAppearance;
			handleEventForEntireCell(event, eventType, cellParent, m, context);
			
			return cell;
		}
		
		public TaxonTreeAppearance getTaxonTreeAppearance() {
			return (TaxonTreeAppearance)treeAppearance;
		}

		private void handleEventForEntireCell(Event event, String eventType,
				Element cellParent, Taxon m, Context context) {
			TaxonTreeAppearance appearance = getTaxonTreeAppearance();
			if(eventType.equals(BrowserEvents.MOUSEOVER)) {
				appearance.onOverCell(cellParent);
			} else if(eventType.equals(BrowserEvents.MOUSEOUT)) {
				appearance.onOutCell(cellParent);
			} else if(eventType.equals(BrowserEvents.CLICK)) { 
				appearance.onClickCell(cellParent, event);
			}
		} 

		private void showContextMenu(final Element parent, Taxon taxon) {
			Menu menu = new TaxonMenu(eventBus, taxonMatrix, modelMode, taxon, taxonStore);
			if (menu != null) {
				menu.setId("taxon" + taxon.toString() + "-menu");
				menu.addHideHandler(new HideHandler() {
					@Override
					public void onHide(HideEvent event) {
						getTaxonTreeAppearance().onExitMenu(parent);
						//h.activateTrigger(false);
						//if (container instanceof Component) {
						//	((Component) container).focus();
						//}
					}
				});
				menu.show(parent, new AnchorAlignment(Anchor.TOP_LEFT,
						Anchor.BOTTOM_LEFT, true));
			}
		}
		
		@Override
		public TaxonTreeAppearance getTreeAppearance() {
			return (TaxonTreeAppearance)treeAppearance;
		}
		
		@Override
		protected String register(Taxon taxon) {
			String id = generateModelId(taxon);
			if (!nodes.containsKey(id)) {
				String domId = XDOM.getUniqueId();
				TaxonTreeGridNode node = new TaxonTreeGridNode(id, taxon, domId);
				nodes.put(id, node);
				nodesByDomId.put(domId, node);
			}
			return id;
		}
	}
	
	public class CharactersGrid extends Grid<Taxon> {

		public CharactersGrid(ListStore<Taxon> listStore,
				CharactersColumnModel columnModel, CharactersGridView gridView) {
			super(listStore, columnModel, gridView);
		}
		
		@Override
		public CharactersColumnModel getColumnModel() {
			return (CharactersColumnModel)cm;
		}
		
		@Override 
		public CharactersGridView getView() {
			return (CharactersGridView)view;
		}
		
	}
	
	private EventBus eventBus;
	private TaxonMatrix taxonMatrix;
	private HideTaxonStoreFilter hideTaxonFilter;
	
	public FrozenFirstColumTaxonTreeGrid(EventBus eventBus, TaxonMatrix taxonMatrix, TaxonStore taxonStore, TaxaColumnConfig taxaColumnConfig) {
		super(taxonStore, taxaColumnConfig);
		this.eventBus = eventBus;
		this.taxonMatrix = taxonMatrix;
		this.hideTaxonFilter = new HideTaxonStoreFilter(eventBus);
		this.store.addFilter(hideTaxonFilter);
	}
	
	@Override
	public void init(List<ColumnConfig<Taxon, ?>> otherColumnConfigs, GridView<Taxon> gridView) {
		for(ColumnConfig<Taxon, ?> config : otherColumnConfigs) {
			assert config instanceof CharacterColumnConfig;
		}
		assert gridView instanceof CharactersGridView;
		
		super.init(otherColumnConfigs, gridView);
		
		UpdateModelDragSource dragSource = new UpdateModelDragSource(super.getTreeGrid());		
		UpdateModelDropTarget dropTarget = new UpdateModelDropTarget(eventBus, super.getTreeGrid(), taxonMatrix);
		//let event handling take care of "move" behaviour
		dropTarget.setOperation(Operation.COPY);
	}

	@Override
	protected TaxaColumnModel createTreeGridColumnModel(List<ColumnConfig<Taxon, ?>> columns) {
		List<TaxaColumnConfig> taxaColumnConfigs = new LinkedList<TaxaColumnConfig>();
		for(ColumnConfig<Taxon, ?> config : columns) {
			assert config instanceof TaxaColumnConfig;
			taxaColumnConfigs.add((TaxaColumnConfig)config);
		}
		return new TaxaColumnModel(taxaColumnConfigs);
	}
	
	@Override
	protected CharactersColumnModel createGridColumnModel(List<ColumnConfig<Taxon, ?>> columns) {
		List<CharacterColumnConfig> characterColumnConfigs = new LinkedList<CharacterColumnConfig>();
		for(ColumnConfig<Taxon, ?> config : columns) {
			assert config instanceof CharacterColumnConfig;
			characterColumnConfigs.add((CharacterColumnConfig)config);
		}
		return new CharactersColumnModel(characterColumnConfigs);
	}
	
	@Override
	public void reconfigure(List<? extends ColumnConfig<Taxon, ?>> characterColumnConfigs) {
		for(ColumnConfig<Taxon, ?> config : characterColumnConfigs) {
			assert config instanceof CharacterColumnConfig;
		}
		super.reconfigure(characterColumnConfigs);
	}

	@Override
	protected TaxaTreeGrid createTreeGrid(TreeStore<Taxon> store,
			ColumnModel<Taxon> columnModel, ColumnConfig<Taxon, ?> treeColumn) {
		assert store instanceof TaxonStore;
		assert columnModel instanceof TaxaColumnModel;
		assert treeColumn instanceof TaxaColumnConfig;
		return new TaxaTreeGrid((TaxonStore)store, (TaxaColumnModel)columnModel, (TaxaColumnConfig)treeColumn);
	}

	@Override
	protected CharactersGrid createGrid(ListStore<Taxon> listStore, ColumnModel<Taxon> columnModel, GridView<Taxon> gridView) {
		assert gridView instanceof CharactersGridView;
		assert columnModel instanceof CharactersColumnModel;
		return new CharactersGrid(listStore, (CharactersColumnModel)columnModel, (CharactersGridView)gridView);
	}
	
	public TaxonStore getTaxonStore() {
		return (TaxonStore)store;
	}

	public TaxaTreeGrid getTaxaTreeGrid() {
		return (TaxaTreeGrid)this.treeGrid;
	}

	public CharactersGrid getCharactersGrid() {
		return (CharactersGrid)this.grid;
	}
	
	@Override
	public TaxonStore getTreeStore() {
		return (TaxonStore)store;
	}
	
	@Override
	public TaxaTreeGrid getTreeGrid() {
		return (TaxaTreeGrid)treeGrid;
	}
	
	@Override
	public CharactersGrid getGrid() {
		return (CharactersGrid)grid;
	}
	
	public CharactersColumnModel getColumnModel() {
		return getCharactersGrid().getColumnModel();
	}

	public void updateCharacterGridHeads() {
		getGrid().getView().getHeader().refresh();
	}

	public void hide(Taxon taxon, boolean hide) {
		if(hide)
			hideTaxonFilter.addHiddenTaxa(taxon);
		else
			hideTaxonFilter.removeHiddenTaxa(taxon);
		//force filter refresh
		store.setEnableFilters(true);
		store.applyFilters();
	}
	
}
