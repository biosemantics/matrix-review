/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.field;

import com.sencha.gxt.cell.core.client.form.RadioCell.RadioAppearance;

public class RadioDefaultAppearance extends CheckBoxDefaultAppearance implements RadioAppearance {

  public RadioDefaultAppearance() {
    super();
    type = "radio";
  }

}