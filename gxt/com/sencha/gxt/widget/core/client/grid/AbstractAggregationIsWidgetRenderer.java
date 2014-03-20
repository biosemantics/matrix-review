/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.safehtml.shared.SafeHtml;

public abstract class AbstractAggregationIsWidgetRenderer<M> implements AggregationRenderer<M> {

  @Override
  public final SafeHtml render(int colIndex, Grid<M> grid) {
    return null;
  }

}
