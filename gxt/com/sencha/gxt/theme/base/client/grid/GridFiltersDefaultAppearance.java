/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.widget.core.client.grid.filters.AbstractGridFilters.GridFiltersAppearance;

public class GridFiltersDefaultAppearance implements GridFiltersAppearance {

  public interface GridFiltersResources extends ClientBundle {
    @Source("GridFilters.css")
    GridFiltersStyle style();
  }
  
  public interface GridFiltersStyle extends CssResource {
    String filtered();
  }

  private GridFiltersResources resources;
  private GridFiltersStyle style;
  
  public GridFiltersDefaultAppearance() {
    this(GWT.<GridFiltersResources> create(GridFiltersResources.class));
  }

  public GridFiltersDefaultAppearance(GridFiltersResources resources) {
    this.resources = resources;
    this.style = this.resources.style();

    StyleInjectorHelper.ensureInjected(style, true);
  }

  @Override
  public String filteredStyle() {
    return style.filtered();
  }
}
