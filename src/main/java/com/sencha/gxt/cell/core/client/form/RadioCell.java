/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import com.google.gwt.core.client.GWT;

public class RadioCell extends CheckBoxCell {

  public interface RadioAppearance extends CheckBoxAppearance {

  }

  public RadioCell() {
    this(GWT.<RadioAppearance> create(RadioAppearance.class));
  }

  public RadioCell(RadioAppearance appearance) {
    super(appearance);
  }

}
