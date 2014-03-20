/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.container;

import java.util.logging.Logger;

import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Rectangle;

/**
 * A layout container that centers its single widget.
 * 
 * <p/>
 * Code Snippet:
 * 
 * <pre>
  public void onModuleLoad() {
    CenterLayoutContainer c = new CenterLayoutContainer();
    c.add(new Label("Hello world"));
    Viewport v = new Viewport();
    v.add(c);
    RootPanel.get().add(v);
  }
 * </pre>
 */
public class CenterLayoutContainer extends SimpleContainer {

  private static Logger logger = Logger.getLogger(CenterLayoutContainer.class.getName());

  @Override
  protected void doLayout() {
    if (widget != null) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("doLayout");
      }
      XElement con = getContainerTarget();
      XElement e = widget.getElement().cast();
      e.makePositionable(true);
      Point p = e.getAlignToXY(con, new AnchorAlignment(Anchor.CENTER, Anchor.CENTER), 0, 0);
      p = e.translatePoints(p);
      applyLayout(widget, new Rectangle(p.getX(), p.getY(), -1, -1));
    }
  }

}
