/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.theme.base.client.menu.ItemBaseAppearance;

public class GrayItemAppearance extends ItemBaseAppearance {

  public interface GrayItemResources extends ItemBaseAppearance.ItemResources, ClientBundle {

    @Override
    @Source({"com/sencha/gxt/theme/base/client/menu/Item.css",
            "com/sencha/gxt/theme/gray/client/menu/GrayItem.css",
            "GrayItem.css"})
    GrayItemStyle style();

    ImageResource itemOver();

  }

  public interface GrayItemStyle extends ItemStyle {

  }

  public GrayItemAppearance() {
    this(GWT.<GrayItemResources> create(GrayItemResources.class));
  }

  public GrayItemAppearance(GrayItemResources resources) {
    super(resources);
  }

}
