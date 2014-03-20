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
import com.sencha.gxt.theme.base.client.menu.MenuItemBaseAppearance;

public class GrayMenuItemAppearance extends MenuItemBaseAppearance {

  public interface GrayMenuItemResources extends MenuItemBaseAppearance.MenuItemResources, ClientBundle {

    ImageResource menuParent();

    @Override
    @Source({"com/sencha/gxt/theme/base/client/menu/Item.css",
            "com/sencha/gxt/theme/gray/client/menu/GrayItem.css",
            "com/sencha/gxt/theme/base/client/menu/MenuItem.css",
            "GrayMenuItem.css"})
    GrayMenuItemStyle style();

    ImageResource itemOver();
  }

  public interface GrayMenuItemStyle extends MenuItemBaseAppearance.MenuItemStyle {
  }

  public GrayMenuItemAppearance() {
    this(GWT.<GrayMenuItemResources> create(GrayMenuItemResources.class),
        GWT.<MenuItemTemplate> create(MenuItemTemplate.class));
  }

  public GrayMenuItemAppearance(GrayMenuItemResources resources, MenuItemTemplate template) {
    super(resources, template);
  }

}
