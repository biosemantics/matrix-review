/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.math.BigInteger;

import com.sencha.gxt.cell.core.client.form.NumberInputCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.BigIntegerPropertyEditor;

/**
 * A Field that accepts BigInteger values.
 *
 */
public class BigIntegerField extends NumberField<BigInteger> {

  /**
   * Creates a BigIntegerField with the default cell and appearance.
   */
  public BigIntegerField() {
    super(new BigIntegerPropertyEditor());
  }

  /**
   * Creates a BigIntegerField with the given cell instance. This can be used to provide an alternate
   * appearance or otherwise modify how content is rendered or events handled.
   * 
   * @param cell the cell to use to draw the field
   */
  public BigIntegerField(NumberInputCell<BigInteger> cell) {
    super(cell, new BigIntegerPropertyEditor());
  }
}
