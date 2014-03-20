/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.toolbar;

import com.google.gwt.dom.client.Document;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;

/**
 * Fills the toolbar width, pushing any newly added items to the right.
 */
public class FillToolItem extends Component {

  /**
   * Creates a new fill item.
   */
  public FillToolItem() {
    setElement(Document.get().createDivElement());
    
    BoxLayoutData data = new BoxLayoutData();
    data.setFlex(1.0);
    setLayoutData(data);
  }

}
