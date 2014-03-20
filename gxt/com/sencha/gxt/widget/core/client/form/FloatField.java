/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.sencha.gxt.cell.core.client.form.NumberInputCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.FloatPropertyEditor;

/**
 * A Field that accepts float values.
 *
 */
public class FloatField extends NumberField<Float> {

  /**
   * Creates a FloatField with the default cell and appearance.
   */
  public FloatField() {
    super(new FloatPropertyEditor());
  }

  /**
   * Creates a FloatField with the given cell instance. This can be used to provide an alternate
   * appearance or otherwise modify how content is rendered or events handled.
   * 
   * @param cell the cell to use to draw the field
   */
  public FloatField(NumberInputCell<Float> cell) {
    super(cell, new FloatPropertyEditor());
  }

}
