/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.sencha.gxt.cell.core.client.form.RadioCell;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.util.ToggleGroup;

/**
 * Single radio field.
 * <p/>
 * {@link ValueChangeEvent}s are fired when the checkbox state is changed by the user, instead of waiting for a
 * {@link BlurEvent}.
 * <p/>
 * Group radios together using the {@link ToggleGroup}.
 */
public class Radio extends CheckBox {

  /**
   * Creates a new radio field.
   */
  public Radio() {
    this(new RadioCell());
  }

  /**
   * Creates a new radio field.
   *
   * @param cell the radio cell
   */
  public Radio(RadioCell cell) {
    super(cell);
  }

  /**
   * Sets the group name of the radios.
   * <ul>
   * <li>When grouping radios, also use {@link ToggleGroup} to group them.</li>
   * <li>Setting the name is not required in a {@link ToggleGroup}</li>
   * </ul>
   *
   * @param name is the group name of the radios.
   */
  @Override
  public void setName(String name) {
    // can't change name after input element is created
    if (GXT.isIE6() || GXT.isIE7()) {
      this.name = name;
      redraw();
    } else {
      super.setName(name);
    }
  }

}
