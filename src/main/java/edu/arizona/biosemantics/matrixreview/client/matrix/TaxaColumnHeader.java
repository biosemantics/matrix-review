package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.InlineHTML;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.FrozenFirstColumTaxonTreeGrid.TaxaTreeGrid;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class TaxaColumnHeader extends ColumnHeader<Taxon> {

	public class TaxaHead extends Head {

		private GridAppearance gridAppearance;
		private com.sencha.gxt.widget.core.client.grid.GridView.GridStyles gridStyles;

		public TaxaHead(TaxaColumnConfig column) {
			this(column, GWT.<GridAppearance> create(GridAppearance.class));
		}

		public TaxaHead(TaxaColumnConfig column, GridAppearance gridAppearance) {
			this.gridAppearance = gridAppearance;
			this.gridStyles = gridAppearance.styles();
			this.config = column;
			this.column = cm.indexOf(column);

			setElement(Document.get().createDivElement());
			getElement().setAttribute("qtitle", "Summary");

			btn = Document.get().createAnchorElement();
			btn.setHref("#");
			btn.setClassName(styles.headButton());

			img = Document.get().createImageElement();
			img.setSrc(GXT.getBlankImageUrl());
			img.setClassName(styles.sortIcon());

			getElement().appendChild(btn);

			if (config.getWidget() != null) {
				Element span = Document.get().createSpanElement().cast();
				widget = config.getWidget();
				span.appendChild(widget.getElement());
				getElement().appendChild(span);
			} else {
				text = new InlineHTML(
						config.getHeader() != null ? config.getHeader()
								: SafeHtmlUtils.fromString(""));
				getElement().appendChild(text.getElement());
			}

			getElement().appendChild(img);

			SafeHtml tip = config.getToolTip();
			if (tip != null) {
				getElement().setAttribute("qtip", tip.asString());
			}

			sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS
					| Event.ONKEYPRESS);

			String s = config.getCellClassName() == null ? "" : " "
					+ config.getCellClassName();
			addStyleName(styles.headInner() + s);
			if (column.getColumnHeaderClassName() != null) {
				addStyleName(column.getColumnHeaderClassName());
			}
			heads.add(this);
			
			addEventHandlers();
			
			refresh();
		}
		
		private void addEventHandlers() {
			eventBus.addHandler(AddTaxonEvent.TYPE, new AddTaxonEvent.AddTaxonEventHandler() {
				@Override
				public void onAdd(AddTaxonEvent event) {
					refresh();
				}
			});
			eventBus.addHandler(RemoveTaxaEvent.TYPE, new RemoveTaxaEvent.RemoveTaxonEventHandler() {
				@Override
				public void onRemove(RemoveTaxaEvent event) {
					refresh();
				}
			});
			eventBus.addHandler(AddCharacterEvent.TYPE, new AddCharacterEvent.AddCharacterEventHandler() {
				@Override
				public void onAdd(AddCharacterEvent event) {
					refresh();
				}
			});
			eventBus.addHandler(RemoveCharacterEvent.TYPE, new RemoveCharacterEvent.RemoveCharacterEventHandler() {
				@Override
				public void onRemove(RemoveCharacterEvent event) {
					refresh();
				}
			});
			eventBus.addHandler(ModifyCharacterEvent.TYPE, new ModifyCharacterEvent.ModifyCharacterEventHandler() {
				@Override
				public void onModify(ModifyCharacterEvent event) {
					refresh();
				}
			});
			eventBus.addHandler(MergeCharactersEvent.TYPE, new MergeCharactersEvent.MergeCharactersEventHandler() {
				@Override
				public void onMerge(MergeCharactersEvent event) {
					refresh();
				}
			});
		}

		public void refresh() {
			setQuickTipText();
		}
		
		public void setQuickTipText() {
			getElement().setAttribute("qtip", "The matrix contains " + 
					model.getTaxonMatrix().getHierarchyTaxaDFS().size() + " taxa and " + model.getTaxonMatrix().getCharacterCount() + " characters of " +
					model.getTaxonMatrix().getHierarchyCharacters().size() + " organs");
		}
	}

	private Model model;
	private EventBus eventBus;

	public TaxaColumnHeader(EventBus eventBus, TaxaTreeGrid container, ColumnModel<Taxon> cm, Model model) {
		this(eventBus, container, cm, model, GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class));
	}

	public TaxaColumnHeader(EventBus eventBus, TaxaTreeGrid container, ColumnModel<Taxon> cm, Model model, ColumnHeaderAppearance appearance) {
		super(container, cm, appearance);
		this.eventBus = eventBus;
		this.model = model;
		
		bindEvents();
	}


	private void bindEvents() {
		eventBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
			@Override
			public void onLoad(LoadModelEvent event) {
				model = event.getModel();
			}
		});
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Head createNewHead(ColumnConfig config) {
		if(config instanceof TaxaColumnConfig)
			return new TaxaHead((TaxaColumnConfig)config);
		return super.createNewHead(config);
	}
	
	@Override
	public TaxaHead getHead(int column) {
		return (column > -1 && column < heads.size()) ? (TaxaHead)heads.get(column) : null;
	}
	
	public void removeSortIcon() {
	    String desc = styles.sortDesc();
	    String asc = styles.sortAsc();
		for (int i = 0; i < heads.size(); i++) {
			Head h = heads.get(i);
			h.getElement().removeClassName(asc, desc);
		}
	}
}
