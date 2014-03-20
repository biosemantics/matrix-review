/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource.Import;
import com.sencha.gxt.theme.base.client.grid.GridBaseAppearance;
import com.sencha.gxt.widget.core.client.grid.GridView.GridStateStyles;

public class GrayGridAppearance extends GridBaseAppearance {

  public interface GrayGridStyle extends GridStyle {
    
  }
  
  public interface GrayGridResources extends GridResources  {

    @Import(GridStateStyles.class)
    @Source({"com/sencha/gxt/theme/base/client/grid/Grid.css", "GrayGrid.css"})
    @Override
    GrayGridStyle css();
  }
  
  
  public GrayGridAppearance() {
    this(GWT.<GrayGridResources> create(GrayGridResources.class));
  }

  public GrayGridAppearance(GrayGridResources resources) {
    super(resources);
  }
}
