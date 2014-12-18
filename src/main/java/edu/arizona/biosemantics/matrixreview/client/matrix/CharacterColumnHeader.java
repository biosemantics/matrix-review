package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.ImageHelper;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.dnd.core.client.StatusProxy;
import com.sencha.gxt.fx.client.DragCancelEvent;
import com.sencha.gxt.fx.client.DragEndEvent;
import com.sencha.gxt.fx.client.DragHandler;
import com.sencha.gxt.fx.client.DragMoveEvent;
import com.sencha.gxt.fx.client.DragStartEvent;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.Head;
import com.sencha.gxt.widget.core.client.grid.GridView.GridAppearance;
import com.sencha.gxt.widget.core.client.grid.HeaderGroupConfig;

import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetColorsEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.FrozenFirstColumTaxonTreeGrid.CharactersGrid;
import edu.arizona.biosemantics.matrixreview.client.matrix.dom.HorizontalAutoScrollSupport;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class CharacterColumnHeader extends ColumnHeader<Taxon> {

	public class CharacterHead extends Head {

		protected SpanElement coverage;
		private GridAppearance gridAppearance;
		private com.sencha.gxt.widget.core.client.grid.GridView.GridStyles gridStyles;
		private boolean hasColumnComment = false;
		private boolean isDirty = false;

		public CharacterHead(CharacterColumnConfig column) {
			this(column, GWT.<GridAppearance> create(GridAppearance.class));
		}

		public CharacterHead(CharacterColumnConfig column, GridAppearance gridAppearance) {
			this.gridAppearance = gridAppearance;
			this.gridStyles = gridAppearance.styles();

			this.config = column;
			this.column = cm.indexOf(column);

			setElement(Document.get().createDivElement());

			getElement().setAttribute("qtitle", "Summary");
			//getElement().setAttribute("qtip", dataManager.getQuickTipText(column));

			if (column instanceof CharacterColumnConfig) {
				coverage = Document.get().createSpanElement();
				//coverage.setInnerText(dataManager.getCoverage(((CharacterColumnConfig) column).getCharacter()));
				coverage.setAttribute("style", "position:absolute;right:0px;background-color:#a8d04d;width:35px;");
				getElement().appendChild(coverage);
			}

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

			if (column instanceof CharacterColumnConfig) {
				CharacterColumnConfig myColumnConfig = (CharacterColumnConfig) column;
				Character character = myColumnConfig.getCharacter();
				Color color = model.getColor(character);
				if (color != null)
					getElement().getStyle().setBackgroundColor("#" + color.getHex());

				if (model.isDirty(character))
					addStyleName(gridStyles.cellDirty());
				if (model.isCommented(character))
					addStyleName(gridStyles.cellCommented());
			}

			heads.add(this);
			
			addEventHandlers();
			
			refresh();
		}
		
		public CharacterColumnConfig getColumnConfig() {
			return (CharacterColumnConfig)config;
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
			eventBus.addHandler(ModifyCharacterEvent.TYPE, new ModifyCharacterEvent.ModifyCharacterEventHandler() {
				@Override
				public void onModify(ModifyCharacterEvent event) {
					if(event.getOldCharacter().equals(getColumnConfig().getCharacter()))
						refresh();
				}
			});
			eventBus.addHandler(SetCharacterCommentEvent.TYPE, new SetCharacterCommentEvent.SetCharacterCommentEventHandler() {
				@Override
				public void onSet(SetCharacterCommentEvent event) {
					if(event.getCharacter().equals(getColumnConfig().getCharacter()))
						refresh();
				}
			});
			eventBus.addHandler(SetCharacterColorEvent.TYPE, new SetCharacterColorEvent.SetCharacterColorEventHandler() {
				@Override
				public void onSet(SetCharacterColorEvent event) {
					if(event.getCharacter().equals(getColumnConfig().getCharacter()))
						refresh();
				}
			});
			eventBus.addHandler(SetColorsEvent.TYPE, new SetColorsEvent.SetColorsEventHandler() {
				@Override
				public void onSet(SetColorsEvent event) {
					refresh();
				}
			});
			eventBus.addHandler(LockCharacterEvent.TYPE, new LockCharacterEvent.LockCharacterEventHandler() {
				@Override
				public void onLock(LockCharacterEvent event) {
					if(event.getCharacter().equals(getColumnConfig().getCharacter()))
						refresh();
				}
			});
			eventBus.addHandler(MergeCharactersEvent.TYPE, new MergeCharactersEvent.MergeCharactersEventHandler() {
				@Override
				public void onMerge(MergeCharactersEvent event) {
					if(event.getCharacter().equals(getColumnConfig().getCharacter()))
						refresh();
				}
			});
			eventBus.addHandler(SetValueEvent.TYPE, new SetValueEvent.SetValueEventHandler() {
				@Override
				public void onSet(SetValueEvent event) {
					if(event.getCharacters().contains(getColumnConfig().getCharacter()))
						refresh();
				}
			});
			eventBus.addHandler(SetControlModeEvent.TYPE, new SetControlModeEvent.SetControlModeEventHandler() {
				@Override
				public void onSet(SetControlModeEvent event) {
					if(event.getCharacter().equals(getColumnConfig().getCharacter()))
						refresh();
				}
			});
		}
		
		public void refresh() {
			Character character = getColumnConfig().getCharacter();
			setText(character.toString());
			setCoverage(model.getTaxonMatrix().getCoverage(character));
			setCommented(model.isCommented(character));
			//setBackgroundColor(character.getColor());
			setDirty(model.isDirty(character));
			setQuickTipText(getQuickTipText(character));
		}

		private String getQuickTipText(Character character) {
			String result = "Character " + character.getName();
			if(character.hasOrgan())
				result +=  " of Organ " + character.getOrgan() + "<br>"; 
			else 
				result += "<br>";
			result += "Taxon coverage: " + model.getTaxonMatrix().getCoverage(character) + "<br>";
			result += getControlMode(character);
			if(model.hasColor(character))
				result += "Color: " + model.getColor(character).getUse() + "<br>";
			if(model.isLocked(character))
				result += "Locked<br>";
			if(model.isDirty(character)) 
				result += "Edited<br>";
			if(model.isCommented(character)) 
				result += "Comment: " + model.getComment(character);
			return result;
		}

		private String getControlMode(Character character) {
			switch(model.getControlMode(character)) {
			case CATEGORICAL:
				String result = "Control Mode: Categorical<br>States (" + model.getStates(character).size() + "): <br>";
				for(String state : model.getStates(character)) {
					result += "- " + state + "<br>";
				}
				return result;
			case NUMERICAL:
				return "Control Mode: Numerical<br>";
			case OFF:
				return "";
			default:
				return "";
			}
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
			this.getElement().getStyle().clearBackgroundImage();
			this.getElement().getStyle().clearProperty("background");
			//this.removeStyleName(gridStyles.cellDirty());
			//this.removeStyleName(gridStyles.cellCommented());
			//this.removeStyleName(gridStyles.cellDirtyCommented());
			this.setCommentedDirty();
		}

		private void setCommentedDirty() {
			String backgroundImage = "";		
			if (hasColumnComment && isDirty) {
				backgroundImage = ImageHelper.createModuleBasedUrl("base/images/grid/black_red.gif");
				  
				//this.addStyleName(gridStyles.cellDirtyCommented());
			} else if (hasColumnComment) {
				backgroundImage = ImageHelper.createModuleBasedUrl("base/images/grid/red.gif");
				//this.addStyleName(gridStyles.cellCommented());
			} else if (isDirty) {
				backgroundImage = ImageHelper.createModuleBasedUrl("base/images/grid/black.gif");
				//this.addStyleName(gridStyles.cellDirty());
			}
			if(isDirty || hasColumnComment) {
				this.getElement().getStyle().setProperty("background", "transparent no-repeat 0 0");
				// may not end with a colon, otherwise set style will fail
				backgroundImage = backgroundImage.substring(0, backgroundImage.length()-1);
				this.getElement().getStyle().setBackgroundImage(backgroundImage);
			}
		}

		public void setDirty(boolean isDirty) {
			this.isDirty = isDirty;
			this.getElement().getStyle().clearBackgroundImage();
			this.getElement().getStyle().clearProperty("background");
			//this.removeStyleName(gridStyles.cellDirty());
			//this.removeStyleName(gridStyles.cellCommented());
			//this.removeStyleName(gridStyles.cellDirtyCommented());
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
		private HorizontalAutoScrollSupport scrollSupport;

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
				scrollSupport = new HorizontalAutoScrollSupport(container.getView().getScroller());
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

	private EventBus eventBus;
	private Model model;
	
	public CharacterColumnHeader(EventBus eventBus, Model model, CharactersGrid container, CharactersColumnModel cm) {
		this(eventBus, model, container, cm, GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class));
	}

	public CharacterColumnHeader(EventBus eventBus, Model model, 
			CharactersGrid container, CharactersColumnModel cm, ColumnHeaderAppearance appearance) {
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
	public DragHandler newColumnReorderingDragHandler() {
		return new MyReorderDragHandler();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Head createNewHead(ColumnConfig config) {
		if(config instanceof CharacterColumnConfig)
			return new CharacterHead((CharacterColumnConfig)config);
		return super.createNewHead(config);
	}
	
	@Override
	public CharacterHead getHead(int column) {
		return (column > -1 && column < heads.size()) ? (CharacterHead)heads.get(column) : null;
	}
	
	public void removeSortIcon() {
	    String desc = styles.sortDesc();
	    String asc = styles.sortAsc();
		for (int i = 0; i < heads.size(); i++) {
			Head h = heads.get(i);
			h.getElement().removeClassName(asc, desc);
		}
	}
	
	public CharactersColumnModel getColumnModel() {
		return (CharactersColumnModel)cm;
	}
	
	public void refreshFromModel() {
		for(int i=0; i<heads.size(); i++) {
			CharacterHead head = (CharacterHead)heads.get(i);
			head.refresh();
		}
	}
}
