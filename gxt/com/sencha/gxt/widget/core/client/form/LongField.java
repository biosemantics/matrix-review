/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.sencha.gxt.cell.core.client.form.NumberInputCell;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.LongPropertyEditor;

/**
 * A Field that accepts long integer values.
 *
 */
public class LongField extends NumberField<Long> {

  /**
   * Creates an LongField with the default cell and appearance.
   */
  public LongField() {
    super(new LongPropertyEditor());
  }

  /**
   * Creates a LongField with the given cell instance. This can be used to provide an alternate
   * appearance or otherwise modify how content is rendered or events handled.
   * 
   * @param cell the cell to use to draw the field
   */
  public LongField(NumberInputCell<Long> cell) {
    super(cell, new LongPropertyEditor());
  }

}
