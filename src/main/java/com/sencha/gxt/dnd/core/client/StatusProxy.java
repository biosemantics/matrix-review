/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.dnd.core.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.widget.core.client.Component;

/**
 * A custom widget used to display the status of the drag operation and
 * information about the data being dragged. The widget is displayed next to the
 * cursor as the user drags data.
 */
public class StatusProxy extends Component {

  public interface StatusProxyAppearance {

    void render(SafeHtmlBuilder builder);

    void setStatus(Element parent, boolean allowed);

    void setStatus(Element parent, ImageResource icon);

    void update(Element parent, String html);

  }

  private static final StatusProxy instance = GWT.create(StatusProxy.class);

  /**
   * Returns the singleton instance.
   * 
   * @return the status proxy
   */
  public static StatusProxy get() {
    return instance;
  }

  private boolean status;
  private final StatusProxyAppearance appearance;

  protected StatusProxy() {
    this(GWT.<StatusProxyAppearance> create(StatusProxyAppearance.class));
  }

  protected StatusProxy(StatusProxyAppearance appearance) {
    this.appearance = appearance;
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    appearance.render(builder);
    setElement((Element) XDOM.create(builder.toSafeHtml()));
    setStatus(false);

    setShadow(true);
  }

  public StatusProxyAppearance getAppearance() {
    return appearance;
  }

  /**
   * Returns true if the drop is allowed.
   * 
   * @return the status
   */
  public boolean getStatus() {
    return status;
  }

  /**
   * Updates the proxy's visual element to indicate the status of whether or not
   * drop is allowed over the current target element.
   * 
   * @param allowed true for the standard ok icon, false for standard no icon
   */
  public void setStatus(boolean allowed) {
    appearance.setStatus(getElement(), allowed);
    this.status = allowed;
  }

  /**
   * Updates the proxy's visual element to indicate the status of whether or not
   * drop is allowed over the current target element.
   * 
   * @param allowed drop is allowed
   * @param icon icon to display
   */
  public void setStatus(boolean allowed, ImageResource icon) {
    appearance.setStatus(getElement(), icon);
    this.status = allowed;
  }

  /**
   * Updates the contents of the ghost element.
   * 
   * @param html the html that will replace the current contents of the ghost
   *          element
   */
  public void update(SafeHtml html) {
    update(html.asString());
  }

  /**
   * Updates the contents of the ghost element.
   * 
   * @param html the html that will replace the current contents of the ghost
   *          element
   */
  public void update(String html) {
    appearance.update(getElement(), html);
  }

}
