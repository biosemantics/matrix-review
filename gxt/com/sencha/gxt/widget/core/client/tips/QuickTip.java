/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.tips;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.event.XEvent;

/**
 * A specialized tooltip class for tooltips that can be specified in markup.
 * 
 * <p />
 * Quicktips can be configured via tag attributes directly in markup. Below is the summary of the configuration
 * properties which can be used.
 * 
 * <ul>
 * <li>text (required)</li>
 * <li>title</li>
 * <li>width</li>
 * </ul>
 * 
 * <p />
 * To register a quick tip in markup, you simply add one or more of the valid QuickTip attributes. The HTML element
 * itself is automatically set as the quick tip target. 
 * 
 *<p />
 * Tag attribute options: <p />
 * <ul>
 * <li><b>qtip (required)</b>: The quick tip text (equivalent to the 'text' target element config).</li>
 * <li><b>qtitle</b>: The quick tip title (equivalent to the 'title' target element config).</li>
 * <li><b>qwidth</b>: The quick tip width (equivalent to the 'width' target element config).</li>
 * </ul>
 * 
 * Example using QuickTip tag attributes:
 * <code><pre>
 * private void exampleQuickTip() {
 *   HTML html = new HTML("&lt;span qtitle='Title for Tip' qtip='Display this tip.' qwidth='50px'&gt;Some text.&lt;/span&gt;");
 *   new QuickTip(html);
 *   RootPanel.get().add(html);
 * }
 * </pre></code>
 */
public class QuickTip extends ToolTip {

  private boolean initialized;
  private boolean interceptTitles;
  private Element targetElem;
  private Element showElem;

  /**
   * Creates a new quick tip instance.
   * 
   * @param component the source component
   */
  public QuickTip(Widget component) {
    super(component);
  }
  
  /**
   * Creates a new quick tip instance.
   * 
   * @param component the source component
   * @param appearance the appearance
   */
  public QuickTip(Widget component, TipAppearance appearance) {
    super(component, appearance);
  }

  /**
   * Returns true if intercept titles is enabled.
   * 
   * @return the intercept title state
   */
  public boolean isInterceptTitles() {
    return interceptTitles;
  }

  /**
   * True to automatically use the element's DOM title value if available (defaults to false).
   * 
   * @param interceptTitles true to to intercept titles
   */
  public void setInterceptTitles(boolean interceptTitles) {
    this.interceptTitles = interceptTitles;
  }

  @Override
  protected void onHide() {
    super.onHide();
    targetElem = null;
    bodyHtml = null;
    titleHtml = null;
  }

  @Override
  protected void onTargetMouseMove(MouseMoveEvent event) {
    onMouseMove(event.getNativeEvent().<Event> cast());
  }

  @Override
  protected void onTargetMouseOut(MouseOutEvent event) {
    if (showElem != null) {
      XEvent xe = event.getNativeEvent().cast();
      if (!xe.within(showElem)) {
        clearTimer("show");
      }
    }
    onTargetOut(event.getNativeEvent().<Event> cast());
  }

  @Override
  protected void onTargetMouseOver(MouseOverEvent event) {
    onTargetOver(event.getNativeEvent().<Event> cast());
  }

  @Override
  protected void onTargetOut(Event ce) {
    EventTarget to = ce.getRelatedEventTarget();
    if (to == null || (Element.is(target) && Element.is(to)
            && !target.isOrHasChild(Element.as(to)))) {
      super.onTargetOut(ce);
    }
  }

  @Override
  protected void onTargetOver(Event ce) {
    if (disabled) {
      return;
    }

    Element t = ce.getEventTarget().cast();
    while (t != null && t != target) {
      if (hasTip(t)) {
        break;
      }
      t = (Element) t.getParentElement();
    }

    boolean hasTip = t != null && hasTip(t);

    if (!initialized && !hasTip) {
      return;
    }
    initialized = true;

    if ((targetElem == null || !isAttached()) && hasTip) {
      updateTargetElement(t);
    } else {
      if (hasTip && targetElem != t) {
        updateTargetElement(t);
      } else if (targetElem != null && getElement().isOrHasChild(targetElem)) {
        return;
      } else {
        delayHide();
        return;
      }
    }
    clearTimers();
    targetXY = ce.<XEvent> cast().getXY();

    XEvent xe = ce.cast();
    if (!xe.within(t)) {
      return;
    }

    showElem = t;
    delayShow();
  }

  private String getAttributeValue(Element target, String attr) {
    String v = target.getAttribute(attr);
    return hasAttributeValue(v) ? v : null;
  }

  private boolean hasAttributeValue(String v) {
    return v != null && !v.equals("");
  }

  private boolean hasTip(Element target) {
    return hasAttributeValue(target.getAttribute("qtip"))
        || (interceptTitles && hasAttributeValue(target.getAttribute("title")));
  }

  private void updateTargetElement(Element target) {
    targetElem = target;
    bodyHtml = getAttributeValue(target, interceptTitles ? "title" : "qtip");
    titleHtml = interceptTitles ? null : getAttributeValue(target, "qtitle");

    String width = getAttributeValue(target, "qwidth");
    if (width != null && !"".equals(width)) {
      setWidth(Util.parseInt(width, 100));
    }
  }

}
