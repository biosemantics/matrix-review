/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.math.BigDecimal;

import com.sencha.gxt.cell.core.client.form.NumberInputCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.BigDecimalPropertyEditor;

/**
 * A Field that accepts BigDecimal values.
 *
 */
public class BigDecimalField extends NumberField<BigDecimal> {

  /**
   * Creates a BigDecimalField with the default cell and appearance.
   */
  public BigDecimalField() {
    super(new BigDecimalPropertyEditor());
  }

  /**
   * Creates a BigDecimalField with the given cell instance. This can be used to provide an alternate
   * appearance or otherwise modify how content is rendered or events handled.
   * 
   * @param cell the cell to use to draw the field
   */
  public BigDecimalField(NumberInputCell<BigDecimal> cell) {
    super(cell, new BigDecimalPropertyEditor());
  }
}
