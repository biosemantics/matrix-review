/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.menu.ItemBaseAppearance;

public class BlueItemAppearance extends ItemBaseAppearance {

  public interface BlueItemResources extends ItemBaseAppearance.ItemResources, ClientBundle {

    @Override
    @Source({"com/sencha/gxt/theme/base/client/menu/Item.css",
            "com/sencha/gxt/theme/blue/client/menu/BlueItem.css",
            "BlueItem.css"})
    BlueItemStyle style();

    ImageResource itemOver();

  }

  public interface BlueItemStyle extends ItemStyle {

  }

  public BlueItemAppearance() {
    this(GWT.<BlueItemResources> create(BlueItemResources.class));
  }

  public BlueItemAppearance(BlueItemResources resources) {
    super(resources);
  }

}
