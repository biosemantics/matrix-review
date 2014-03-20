/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.sencha.gxt.cell.core.client.form.SpinnerFieldCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.IntegerPropertyEditor;

/**
 * A SpinnerField that accepts integer values.
 *
 */
public class IntegerSpinnerField extends SpinnerField<Integer> {

  /**
   * Creates an IntegerSpinnerField with the default cell and appearance.
   */
  public IntegerSpinnerField() {
    super(new IntegerPropertyEditor());
  }

  /**
   * Creates an IntegerSpinnerField with the given cell instance. This can be used to provide an alternate
   * appearance or otherwise modify how content is rendered or events handled.
   * 
   * @param cell the cell to use to draw the field
   */
  public IntegerSpinnerField(SpinnerFieldCell<Integer> cell) {
    super(cell);
  }

}
