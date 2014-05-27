/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.core.client.GWT;

public class TextButtonCell extends ButtonCell<String> {

  public TextButtonCell() {
    this(GWT.<ButtonCellAppearance<String>> create(ButtonCellAppearance.class));
  }

  public TextButtonCell(ButtonCellAppearance<String> appearance) {
    super(appearance);
  }

}
