/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.sencha.gxt.cell.core.client.form.SpinnerFieldCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.FloatPropertyEditor;

/**
 * A SpinnerField that accepts float values.
 *
 */
public class FloatSpinnerField extends SpinnerField<Float> {

  /**
   * Creates a FloatSpinnerField with the default cell and appearance.
   */
  public FloatSpinnerField() {
    super(new FloatPropertyEditor());
  }

  /**
   * Creates a FloatSpinnerField with the given cell instance. This can be used to provide an alternate
   * appearance or otherwise modify how content is rendered or events handled.
   * 
   * @param cell the cell to use to draw the field
   */
  public FloatSpinnerField(SpinnerFieldCell<Float> cell) {
    super(cell);
  }

}
