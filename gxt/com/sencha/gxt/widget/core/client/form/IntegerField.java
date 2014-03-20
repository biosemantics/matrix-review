/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.sencha.gxt.cell.core.client.form.NumberInputCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.IntegerPropertyEditor;

/**
 * A Field that accepts integer values.
 *
 */
public class IntegerField extends NumberField<Integer> {

  /**
   * Creates an IntegerField with the default cell and appearance.
   */
  public IntegerField() {
    super(new IntegerPropertyEditor());
    setAllowDecimals(false);
  }

  /**
   * Creates an IntegerField with the given cell instance. This can be used to provide an alternate
   * appearance or otherwise modify how content is rendered or events handled.
   * 
   * @param cell the cell to use to draw the field
   */
  public IntegerField(NumberInputCell<Integer> cell) {
    super(cell, new IntegerPropertyEditor());
  }
}
