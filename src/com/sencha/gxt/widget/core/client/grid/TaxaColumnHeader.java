package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.MyHorizontalAutoScrollSupport;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.fx.client.DragCancelEvent;
import com.sencha.gxt.fx.client.DragEndEvent;
import com.sencha.gxt.fx.client.DragHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.DragStartEvent;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;

import edu.arizona.biosemantics.matrixreview.client.manager.DataManager;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxaColumnHeader extends ColumnHeader<Taxon> {

	public class TaxaHead extends Head {

		protected SpanElement coverage;
		private GridAppearance gridAppearance;
		private com.sencha.gxt.widget.core.client.grid.GridView.GridStyles gridStyles;
		private boolean hasColumnComment = false;
		private boolean isDirty = false;

		public TaxaHead(TaxaColumnConfig column, DataManager dataManager) {
			this(column, dataManager, GWT.<GridAppearance> create(GridAppearance.class));
		}

		public TaxaHead(TaxaColumnConfig column, DataManager dataManager, GridAppearance gridAppearance) {
			this.gridAppearance = gridAppearance;
			this.gridStyles = gridAppearance.styles();

			this.config = column;
			this.column = cm.indexOf(column);

			setElement(Document.get().createDivElement());

			getElement().setAttribute("qtitle", "Summary");
			getElement().setAttribute("qtip", dataManager.getQuickTipText(column));

			/*if (column instanceof TaxaColumnConfig) {
				coverage = Document.get().createSpanElement();
				coverage.setInnerText(dataManager.getCoverage(((CharacterColumnConfig) column).getCharacter()));
				coverage.setAttribute("style", "position:absolute;right:0px;background-color:#b0e0e6;width:35px;");
				getElement().appendChild(coverage);
			}*/

			btn = Document.get().createAnchorElement();
			btn.setHref("#");
			btn.setClassName(styles.headButton());
			btn.getStyle().setRight(XDOM.getScrollBarWidth() - 5, Unit.PX);

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
				text = new InlineHTML(config.getHeader() != null ? config.getHeader() : SafeHtmlUtils.fromString(""));
				getElement().appendChild(text.getElement());
			}

			getElement().appendChild(img);

			SafeHtml tip = config.getToolTip();
			if (tip != null) {
				getElement().setAttribute("qtip", tip.asString());
			}

			sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS | Event.FOCUSEVENTS | Event.ONKEYPRESS);

			String s = config.getCellClassName() == null ? "" : " " + config.getCellClassName();
			addStyleName(styles.headInner() + s);
			if (column.getColumnHeaderClassName() != null) {
				addStyleName(column.getColumnHeaderClassName());
			}

			/*if (column instanceof CharacterColumnConfig) {
				CharacterColumnConfig myColumnConfig = (CharacterColumnConfig) column;
				Character character = myColumnConfig.getCharacter();
				Color color = character.getColor();
				if (color != null)
					getElement().getStyle().setBackgroundColor("#" + color.getHex());

				if (character.isDirty())
					addStyleName(gridStyles.cellDirty());
				if (character.isCommented())
					addStyleName(gridStyles.cellCommented());
			}*/

			heads.add(this);
		}

		public void setCoverage(String text) {
			this.coverage.setInnerText(text);
		}

		public String getCoverage() {
			return coverage.getInnerText();
		}

		public void setQuickTipText(String text) {
			getElement().setAttribute("qtip", text);
		}

		public void setBackgroundColor(Color color) {
			if (color != null)
				getElement().getStyle().setBackgroundColor("#" + color.getHex());
		}

		public void setText(String text) {
			this.text.setText(text);
		}

		public void setCommented(boolean hasColumnComment) {
			this.hasColumnComment = hasColumnComment;
			this.removeStyleName(gridStyles.cellDirty());
			this.removeStyleName(gridStyles.cellCommented());
			this.removeStyleName(gridStyles.cellDirtyCommented());
			this.setCommentedDirty();
		}

		private void setCommentedDirty() {
			if (hasColumnComment && isDirty) {
				this.addStyleName(gridStyles.cellDirtyCommented());
			} else if (hasColumnComment) {
				this.addStyleName(gridStyles.cellCommented());
			} else if (isDirty) {
				this.addStyleName(gridStyles.cellDirty());
			}
		}

		public void setDirty(boolean isDirty) {
			this.isDirty = isDirty;
			this.removeStyleName(gridStyles.cellDirty());
			this.removeStyleName(gridStyles.cellCommented());
			this.removeStyleName(gridStyles.cellDirtyCommented());
			setCommentedDirty();
		}
	}

	protected class MyReorderDragHandler implements DragHandler {
		protected Head active;
		protected int newIndex = -1;
		protected Head start;
		protected XElement statusIndicatorBottom;
		protected XElement statusIndicatorTop;
		protected StatusProxy statusProxy = StatusProxy.get();
		private MyHorizontalAutoScrollSupport scrollSupport;

		@Override
		public void onDragCancel(DragCancelEvent event) {
			if (scrollSupport != null)
				scrollSupport.stop();
			afterDragEnd();
		}

		@Override
		public void onDragEnd(DragEndEvent event) {
			if (scrollSupport != null)
				scrollSupport.stop();

			if (statusProxy.getStatus()) {
				cm.moveColumn(start.column, newIndex);
			}
			afterDragEnd();
		}

		@Override
		public void onDragMove(DragMoveEvent event) {
			event.setX(event.getNativeEvent().getClientX() + 12 + XDOM.getBodyScrollLeft());
			event.setY(event.getNativeEvent().getClientY() + 12 + XDOM.getBodyScrollTop());

			Element target = event.getNativeEvent().getEventTarget().cast();

			Head h = getHeadFromElement(adjustTargetElement(target));

			if (h != null && !h.equals(start)) {
				HeaderGroupConfig g = cm.getGroup(h.row - 1, h.column);
				HeaderGroupConfig s = cm.getGroup(start.row - 1, start.column);
				if ((g == null && s == null) || (g != null && g.equals(s))) {
					active = h;
					boolean before = event.getNativeEvent().getClientX() < active.getAbsoluteLeft() + active.getOffsetWidth() / 2;
					showStatusIndicator(true);

					if (before) {
						statusIndicatorTop.alignTo(active.getElement(), new AnchorAlignment(Anchor.BOTTOM, Anchor.TOP_LEFT), -1, 0);
						statusIndicatorBottom.alignTo(active.getElement(), new AnchorAlignment(Anchor.TOP, Anchor.BOTTOM_LEFT), -1, 0);
					} else {
						statusIndicatorTop.alignTo(active.getElement(), new AnchorAlignment(Anchor.BOTTOM, Anchor.TOP_RIGHT), 1, 0);
						statusIndicatorBottom.alignTo(active.getElement(), new AnchorAlignment(Anchor.TOP, Anchor.BOTTOM_RIGHT), 1, 0);
					}

					int i = active.column;
					if (!before) {
						i++;
					}

					int aIndex = i;

					if (start.column < active.column) {
						aIndex--;
					}
					newIndex = i;
					if (aIndex != start.column) {
						statusProxy.setStatus(true);
					} else {
						showStatusIndicator(false);
						statusProxy.setStatus(false);
					}
				} else {
					active = null;
					showStatusIndicator(false);
					statusProxy.setStatus(false);
				}

			} else {
				active = null;
				showStatusIndicator(false);
				statusProxy.setStatus(false);
			}
		}

		@Override
		public void onDragStart(DragStartEvent event) {
			if (scrollSupport == null) {
				scrollSupport = new MyHorizontalAutoScrollSupport(scrollContainer.getElement());
			} else if (scrollSupport.getScrollElement() == null) {
				scrollSupport.setScrollElement(container.getView().getScroller());
			}
			scrollSupport.start();

			Element target = event.getNativeEvent().getEventTarget().cast();

			Head h = getHeadFromElement(target);
			if (h != null && !h.config.isFixed()) {
				headerDisabled = true;
				quickTip.disable();
				if (bar != null) {
					bar.hide();
				}

				if (statusIndicatorBottom == null) {
					statusIndicatorBottom = XElement.createElement("div");
					statusIndicatorBottom.addClassName(styles.columnMoveBottom());
					statusIndicatorTop = XElement.createElement("div");
					statusIndicatorTop.addClassName(styles.columnMoveTop());
				}

				Document.get().getBody().appendChild(statusIndicatorTop);
				Document.get().getBody().appendChild(statusIndicatorBottom);

				start = h;
				statusProxy.setStatus(false);
				statusProxy.update(start.config.getHeader());
			} else {
				event.setCancelled(true);
			}
		}

		protected Element adjustTargetElement(Element target) {
			return (Element) (target.getFirstChildElement() != null ? target.getFirstChildElement() : target);
		}

		protected void afterDragEnd() {
			start = null;
			active = null;
			newIndex = -1;
			removeStatusIndicator();

			headerDisabled = false;

			if (bar != null) {
				bar.show();
			}

			quickTip.enable();
		}

		@SuppressWarnings("unchecked")
		protected Head getHeadFromElement(Element element) {
			Widget head = ComponentHelper.getWidgetWithElement(element);
			Head h = null;
			if (head instanceof ColumnHeader.Head && heads.contains(head)) {
				h = (Head) head;
			}
			return h;
		}

		protected void removeStatusIndicator() {
			if (statusIndicatorBottom != null) {
				statusIndicatorBottom.removeFromParent();
				statusIndicatorTop.removeFromParent();
			}
		}

		protected void showStatusIndicator(boolean show) {
			if (statusIndicatorBottom != null) {
				statusIndicatorBottom.setVisibility(show);
				statusIndicatorTop.setVisibility(show);
			}
		}
	}

	private Container scrollContainer;
	private DataManager dataManager;

	public TaxaColumnHeader(Grid<Taxon> container, ColumnModel<Taxon> cm, DataManager dataManager) {
		this(container, cm, GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class), dataManager);
	}

	public TaxaColumnHeader(Grid<Taxon> container, ColumnModel<Taxon> cm, ColumnHeaderAppearance appearance, DataManager dataManager) {
		super(container, cm, appearance);
		this.dataManager = dataManager;
	}

	@Override
	public DragHandler newColumnReorderingDragHandler() {
		return new MyReorderDragHandler();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Head createNewHead(ColumnConfig config) {
		if(config instanceof TaxaColumnConfig)
			return new TaxaHead((TaxaColumnConfig)config, dataManager);
		return super.createNewHead(config);
	}
	
	@Override
	public TaxaHead getHead(int column) {
		return (column > -1 && column < heads.size()) ? (TaxaHead)heads.get(column) : null;
	}
	
}
