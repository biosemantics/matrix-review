/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.sencha.gxt.cell.core.client.form.NumberInputCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.DoublePropertyEditor;

/**
 * A Field that accepts double values.
 */
public class DoubleField extends NumberField<Double> {

  /**
   * Creates a DoubleField with the default cell and appearance.
   *
   * @param cell the number input cell.
   */
  public DoubleField() {
    super(new DoublePropertyEditor());
  }

  /**
   * Creates a DoubleField with the given cell instance. This can be used to provide an alternate
   * appearance or otherwise modify how content is rendered or events handled.
   */
  public DoubleField(NumberInputCell<Double> cell) {
    super(cell, new DoublePropertyEditor());
  }

}
