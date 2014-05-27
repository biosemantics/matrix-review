/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.math.BigInteger;

import com.sencha.gxt.cell.core.client.form.SpinnerFieldCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.BigIntegerPropertyEditor;

/**
 * A SpinnerField that accepts BigInteger values.
 *
 */
public class BigIntegerSpinnerField extends SpinnerField<BigInteger> {

  /**
   * Creates a BigIntegerSpinnerField with the default cell and appearance.
   */
  public BigIntegerSpinnerField() {
    super(new BigIntegerPropertyEditor());
  }

  /**
   * Creates a BigIntegerSpinnerField with the given cell instance. This can be used to provide an alternate
   * appearance or otherwise modify how content is rendered or events handled.
   * 
   * @param cell the cell to use to draw the field
   */
  public BigIntegerSpinnerField(SpinnerFieldCell<BigInteger> cell) {
    super(cell);
  }

}
