package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.ImageHelper;
import com.sencha.gxt.theme.base.client.tree.TreeBaseAppearance;
import com.sencha.gxt.theme.base.client.tree.TreeBaseAppearance.TreeResources;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;
import com.sencha.gxt.widget.core.client.tree.Tree.CheckState;
import com.sencha.gxt.widget.core.client.tree.Tree.Joint;
import com.sencha.gxt.widget.core.client.tree.TreeView.TreeViewRenderMode;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class TaxonTreeAppearance extends TreeBaseAppearance {

	public interface TaxonTreeBaseStyle extends TreeBaseStyle {
		String check();

		String container();

		String dragOver();

		String drop();

		String element();

		String icon();

		String joint();

		String node();

		String over();

		String selected();

		String text();

		String tree();

		String headButton();

		String headMenuOpen();

		String headOver();

		String menu();

	}

	public interface BlueTreeResources extends TreeResources, ClientBundle {

		@Source({ "com/sencha/gxt/theme/base/client/tree/Tree.css",
				"TaxonTree.css" })
		TaxonTreeBaseStyle style();
	}

	private TaxonTreeBaseStyle taxonTreeBaseStyles;
	private ColumnHeaderStyles columnHeaderStyles;
	private String blackRed = ImageHelper.createModuleBasedUrl("base/images/grid/black_red.gif");
	private String red = ImageHelper.createModuleBasedUrl("base/images/grid/red.gif");
	private String black = ImageHelper.createModuleBasedUrl("base/images/grid/black.gif");

	public TaxonTreeAppearance() {
		super((TreeResources) GWT.create(BlueTreeResources.class));
		this.columnHeaderStyles = GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class).styles();
		this.taxonTreeBaseStyles = (TaxonTreeBaseStyle) style;
	}
	
	public void renderNode(Taxon taxon, SafeHtmlBuilder sb, String id, SafeHtml text,
			TreeStyle ts, ImageResource icon, boolean checkable,
			CheckState checked, Joint joint, int level,
			TreeViewRenderMode renderMode) {
		String grandParentStyleClass = columnHeaderStyles.header() + " " + columnHeaderStyles.head();
		//String parentStyleClass = columnHeaderStyles.headInner();

		if (renderMode == TreeViewRenderMode.ALL
				|| renderMode == TreeViewRenderMode.BUFFER_WRAP) {
			sb.appendHtmlConstant("<div id=\"" + SafeHtmlUtils.htmlEscape(id)
					+ "\" class=\"" + style.node() + "\">");

			String backgroundImage = "";
			if(taxon.isDirty() && taxon.isCommented()) {
				backgroundImage = blackRed.substring(0, blackRed.length()-1);
			}
			if(taxon.isDirty() && !taxon.isCommented()) {
				backgroundImage = black.substring(0, black.length()-1);
			}
			if(taxon.isCommented() && !taxon.isDirty()) {
				backgroundImage = red.substring(0, red.length()-1);
			}
			
			sb.appendHtmlConstant("<div class=\"" + style.element() + " " + grandParentStyleClass + "\" " +
					" qtip=\"" + getQuickTip(taxon) + "\">");
			sb.appendHtmlConstant("<div style=\"background: transparent no-repeat 3px 0px; background-image:" + backgroundImage + "; \">");
			/*background: transparent no-repeat 0 0;*/
		}
		
		SpanElement coverage = Document.get().createSpanElement();
		//coverage.setInnerText(dataManager.getCoverage(((CharacterColumnConfig) column).getCharacter()));
		coverage.setAttribute("style", "position:absolute;right:0px;background-color:#a8d04d;width:35px;");
		coverage.setInnerText(taxon.getTaxonMatrix().getCoverage(taxon));
		sb.appendHtmlConstant(coverage.getString());
		
		if (renderMode == TreeViewRenderMode.ALL
				|| renderMode == TreeViewRenderMode.BUFFER_BODY) {

			sb.appendHtmlConstant(getIndentMarkup(level));

			Element jointElement = null;
			switch (joint) {
			case COLLAPSED:
				jointElement = getImage(ts.getJointCloseIcon() == null ? resources
						.jointCollapsedIcon() : ts.getJointCloseIcon());
				break;
			case EXPANDED:
				jointElement = getImage(ts.getJointOpenIcon() == null ? resources
						.jointExpandedIcon() : ts.getJointOpenIcon());
				break;
			default:
				// empty
			}

			if (jointElement != null) {
				jointElement.addClassName(style.joint());
			}

			sb.appendHtmlConstant(jointElement == null ? "<img src=\""
					+ GXT.getBlankImageUrl()
					+ "\" style=\"width: 16px\" class=\"" + style.joint()
					+ "\" />" : jointElement.getString());

			// checkable
			if (checkable) {
				Element e = null;
				switch (checked) {
				case CHECKED:
					e = getImage(resources.checked());
					break;
				case UNCHECKED:
					e = getImage(resources.unchecked());
					break;
				case PARTIAL:
					e = getImage(resources.partialChecked());
					break;
				}

				e.addClassName(style.check());
				sb.appendHtmlConstant(e.getString());
			} else {
				sb.appendHtmlConstant("<span class='" + style.check()
						+ "'></span>");
			}

			if (icon != null) {
				Element e = getImage(icon);
				e.addClassName(style.icon());
				sb.appendHtmlConstant(e.getString());
			} else {
				sb.appendHtmlConstant("<span class=\"" + style.icon()
						+ "\"></span>");
			}

			sb.appendHtmlConstant("<span class=\"" + style.text() + "\">"
					+ text.asString() + "</span>");
			
			//sb.appendHtmlConstant("<div class=\"" + taxonTreeBaseStyles.menu() + " " + parentStyleClass + "\">");
			sb.appendHtmlConstant("<div class=\"" + taxonTreeBaseStyles.menu() + "\">");
			sb.appendHtmlConstant("<a "  
					+ "class=\"" + taxonTreeBaseStyles.headButton() + "\" "
					+ "style=\"height: 22px; right:0px;\"></a>");
			sb.appendHtmlConstant("</div>");
		}

		if (renderMode == TreeViewRenderMode.ALL
				|| renderMode == TreeViewRenderMode.BUFFER_WRAP) {
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("</div>");
		}

	}
	
	private String getQuickTip(Taxon taxon) {
		String result = "Taxon: " + taxon.getFullName() + "<br>";
		result += "Author: " + taxon.getAuthor() + "<br>";
		result += "Year: " + taxon.getYear() + "<br>";
		result += "Rank: " + taxon.getLevel().toString() + "<br>";
		result += "Character coverage: " + taxon.getTaxonMatrix().getCoverage(taxon) + "<br>";
		String ancestors = getAncestor(taxon);
		if(!ancestors.isEmpty())
			result += "Ancestors: <br>" + ancestors;
		if(taxon.hasColor())
			result += "Color: " + taxon.getColor().getUse() + "<br>";
		if(taxon.isLocked())
			result += "Locked<br>";
		if(taxon.isDirty()) 
			result += "Dirty<br>";
		if(taxon.isCommented()) 
			result += "Comment: " + taxon.getComment();
		return result;
	}

	private String getAncestor(Taxon taxon) {
		String result = "";
		while(taxon.hasParent()) {
			result += "- " + taxon.getParent().getFullName() + "<br>";
			taxon = taxon.getParent();
		}
		return result;
	}

	@Override
	public void renderNode(SafeHtmlBuilder sb, String id, SafeHtml text,
			TreeStyle ts, ImageResource icon, boolean checkable,
			CheckState checked, Joint joint, int level,
			TreeViewRenderMode renderMode) {
		String grandParentStyleClass = columnHeaderStyles.header() + " " + columnHeaderStyles.head();
		//String parentStyleClass = columnHeaderStyles.headInner();

		if (renderMode == TreeViewRenderMode.ALL
				|| renderMode == TreeViewRenderMode.BUFFER_WRAP) {
			sb.appendHtmlConstant("<div id=\"" + SafeHtmlUtils.htmlEscape(id)
					+ "\" class=\"" + style.node() + "\">");

			sb.appendHtmlConstant("<div class=\"" + style.element() + " " + grandParentStyleClass +  "\">");
		}

		if (renderMode == TreeViewRenderMode.ALL
				|| renderMode == TreeViewRenderMode.BUFFER_BODY) {

			sb.appendHtmlConstant(getIndentMarkup(level));

			Element jointElement = null;
			switch (joint) {
			case COLLAPSED:
				jointElement = getImage(ts.getJointCloseIcon() == null ? resources
						.jointCollapsedIcon() : ts.getJointCloseIcon());
				break;
			case EXPANDED:
				jointElement = getImage(ts.getJointOpenIcon() == null ? resources
						.jointExpandedIcon() : ts.getJointOpenIcon());
				break;
			default:
				// empty
			}

			if (jointElement != null) {
				jointElement.addClassName(style.joint());
			}

			sb.appendHtmlConstant(jointElement == null ? "<img src=\""
					+ GXT.getBlankImageUrl()
					+ "\" style=\"width: 16px\" class=\"" + style.joint()
					+ "\" />" : jointElement.getString());

			// checkable
			if (checkable) {
				Element e = null;
				switch (checked) {
				case CHECKED:
					e = getImage(resources.checked());
					break;
				case UNCHECKED:
					e = getImage(resources.unchecked());
					break;
				case PARTIAL:
					e = getImage(resources.partialChecked());
					break;
				}

				e.addClassName(style.check());
				sb.appendHtmlConstant(e.getString());
			} else {
				sb.appendHtmlConstant("<span class='" + style.check()
						+ "'></span>");
			}

			if (icon != null) {
				Element e = getImage(icon);
				e.addClassName(style.icon());
				sb.appendHtmlConstant(e.getString());
			} else {
				sb.appendHtmlConstant("<span class=\"" + style.icon()
						+ "\"></span>");
			}

			sb.appendHtmlConstant("<span class=\"" + style.text() + "\">"
					+ text.asString() + "</span>");
			
			//sb.appendHtmlConstant("<div class=\"" + taxonTreeBaseStyles.menu() + " " + parentStyleClass + "\">");
			sb.appendHtmlConstant("<div class=\"" + taxonTreeBaseStyles.menu() + "\">");
			sb.appendHtmlConstant("<a "  
					+ "class=\"" + taxonTreeBaseStyles.headButton() + "\" "
					+ "style=\"height: 22px; right:0px;\"></a>");
			sb.appendHtmlConstant("</div>");
		}

		if (renderMode == TreeViewRenderMode.ALL
				|| renderMode == TreeViewRenderMode.BUFFER_WRAP) {
			sb.appendHtmlConstant("</div>");
			sb.appendHtmlConstant("</div>");
		}

	}

	public XElement findMenuElement(XElement target) {
		return target.selectNode("." + taxonTreeBaseStyles.menu());
	}

	public void onClickCell(Element cellParent, Event event) {
		Element a = getAElement(cellParent);
		Element aGrandParent = a.getParentElement().getParentElement();
		Element clickedElement = Element.as(event.getEventTarget());
		if(clickedElement.getClassName().equals(taxonTreeBaseStyles.headButton())) {
			aGrandParent.addClassName(taxonTreeBaseStyles.headMenuOpen());
		}
	}

	public void onOutCell(Element cellParent) {
		Element a = getAElement(cellParent);
		Element aGrandParent = a.getParentElement().getParentElement();
		if(!aGrandParent.hasClassName(taxonTreeBaseStyles.headMenuOpen())) {
			aGrandParent.removeClassName(taxonTreeBaseStyles.headOver());
		}
	}

	public void onOverCell(Element cellParent) {
		Element a = getAElement(cellParent);
		Element aGrandParent = a.getParentElement().getParentElement();
		aGrandParent.addClassName(taxonTreeBaseStyles.headOver());
	}
	
	public void onExitMenu(Element menuParent) {
		Element a = menuParent;//(com.google.gwt.user.client.Element) menuParent.getChild(0);
		Element aGrandParent = a.getParentElement().getParentElement();
		aGrandParent.removeClassName(taxonTreeBaseStyles.headOver());
		aGrandParent.removeClassName(taxonTreeBaseStyles.headMenuOpen());
	}
	
	//A is the link used for menu; parent is parent of event
	//client.Element is a newer version of dom.Element. It actually only extends it if you look in source
	//http://stackoverflow.com/questions/9024548/gwt-why-is-there-two-element-types
	private com.google.gwt.user.client.Element getAElement(Element cellParent) {
		try {
			com.google.gwt.user.client.Element aGpGpGp = (com.google.gwt.user.client.Element)cellParent.getChild(0);
			com.google.gwt.user.client.Element aGpGp = (com.google.gwt.user.client.Element)aGpGpGp.getChild(0);
			com.google.gwt.user.client.Element aGp = (com.google.gwt.user.client.Element)aGpGp.getChild(0);
			com.google.gwt.user.client.Element aP = (com.google.gwt.user.client.Element)aGp.getChild(5);
			com.google.gwt.user.client.Element a = (com.google.gwt.user.client.Element)aP.getChild(0);
			return a;
		} catch (Exception e) {
			System.out.println("exception here");
			return null;
		}
	}


	  @Override
	  public XElement onCheckChange(XElement node, XElement checkElement, boolean checkable, CheckState state) {
	    Element e = null;
	    if (checkable) {
	      switch (state) {
	        case CHECKED:
	          e = getImage(resources.checked());
	          break;
	        case UNCHECKED:
	          e = getImage(resources.unchecked());
	          break;
	        case PARTIAL:
	          e = getImage(resources.partialChecked());
	          break;
	      }
	    } else {
	      e = DOM.createSpan();
	    }
	    e.addClassName(style.check());
	    e = (Element) node.getFirstChild().getFirstChild().insertBefore(e, checkElement);
	    checkElement.removeFromParent();
	    return e.cast();
	  }
	
	  @Override
	  public XElement onJointChange(XElement node, XElement jointElement, Joint joint, TreeStyle ts) {
	    Element e;
	    switch (joint) {
	      case COLLAPSED:
	        e = getImage(ts.getJointCloseIcon() == null ? resources.jointCollapsedIcon() : ts.getJointCloseIcon());
	        break;
	      case EXPANDED:
	        e = getImage(ts.getJointOpenIcon() == null ? resources.jointExpandedIcon() : ts.getJointOpenIcon());
	        break;
	      default:
	        e = XDOM.create("<img src=\"" + GXT.getBlankImageUrl() + "\" width=\"16px\"/>");
	    }

	    e.addClassName(style.joint());
	    e = (Element) node.getFirstChild().getFirstChild().insertBefore(e, jointElement);
	    jointElement.removeFromParent();
	    return e.cast();
	  }
	
	/*
	com.google.gwt.user.client.Element aGrandParent = null;
	com.google.gwt.user.client.Element aParent = null;
	if(cellParent.getChildCount() > 0) { 
		//client.Element is a newer version of dom.Element. It actually only extends it if you look in source
		//http://stackoverflow.com/questions/9024548/gwt-why-is-there-two-element-types
		aGrandParent = (com.google.gwt.user.client.Element)cellParent.getChild(0);
		if(aGrandParent.getChildCount() > 0) {
			aParent = (com.google.gwt.user.client.Element)aGrandParent.getChild(0);
		} else {
			//System.out.println("no parent " + parent);
		}
	} else {
		//System.out.println("no grand parent " + parent);
	}*/
	
	/*if(aParent != null && aGrandParent != null) {
	if(event.getType().equals(BrowserEvents.MOUSEOVER)) {							
		aGrandParent.addClassName(appearance..headOver());
		//aGrandParent.getStyle().setRight(XDOM.getScrollBarWidth(), Unit.PX);
		//aParent.addClassName(styles.headInner());
	}
	if(event.getType().equals(BrowserEvents.MOUSEOUT)) {
		aGrandParent.removeClassName(columnHeaderStyles.headOver());
		aGrandParent.removeClassName(columnHeaderStyles.headMenuOpen());
	}
	if (event.getType().equals(BrowserEvents.CLICK)) {
		if (Element.is(event.getEventTarget())) {
			Element clickedElement = Element.as(event.getEventTarget());
			if(clickedElement.getClassName().equals(columnHeaderStyles.headButton())) {
				aGrandParent.addClassName(columnHeaderStyles.headMenuOpen());
				this.showContextMenu(clickedElement, m);
			}
		}
	}
}*/
	
	  /*(@Override
	  public void onSelect(XElement node, boolean select) {
	    node.setClassName(style.selected(), select);
	  }*/

}
