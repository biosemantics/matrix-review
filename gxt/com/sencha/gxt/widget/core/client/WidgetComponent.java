/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * Creates a widget from a widget. This allows widgets to be treated as
 * components.
 */
public class WidgetComponent extends Composite {

  /**
   * Creates a {@link Component} from the specified {@link Widget}.
   * 
   * @param widget the widget to wrap
   */
  public WidgetComponent(Widget widget) {
    super();
    initWidget(widget);
  }
  
  public Widget getWidget() {
    // make public
    return super.getWidget();
  }

  @Override
  public void setStyleName(String style, boolean add) {
    getWidget().setStyleName(style, add);
  }

  @Override
  public void setStyleName(String style) {
    getWidget().setStyleName(style);
  }

  @Override
  public void removeStyleName(String style) {
    getWidget().removeStyleName(style);
  }

}
