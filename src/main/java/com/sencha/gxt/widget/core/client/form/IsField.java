/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.user.client.ui.IsWidget;

public interface IsField<T> extends IsWidget, LeafValueEditor<T> {

  /**
   * Clears the value from the field.
   */
  public void clear();

  /**
   * Clear any invalid styles / messages for this field.
   */
  public void clearInvalid();

  /**
   * Resets the current field value to the originally loaded value and clears any validation messages.
   */
  public void reset();

  /**
   * Returns whether or not the field value is currently valid.
   * 
   * @param preventMark {@code true} for silent validation (no invalid event and field is not marked invalid)
   * 
   * @return {@code true} if the value is valid, otherwise false
   */
  public boolean isValid(boolean preventMark);

  /**
   * Validates the field value.
   * 
   * @param preventMark {@code true} to not mark the field valid and fire invalid event when invalid
   * @return {@code true} if valid, otherwise false
   */
  public boolean validate(boolean preventMark);

}
