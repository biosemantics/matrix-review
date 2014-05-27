/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form.error;

import java.util.List;

import com.google.gwt.editor.client.EditorError;

/**
 * Defines the interface for objects that can display a field's error.
 * 
 * <p />
 * See the following concrete implementations:
 * <ul>
 * <li>{@link SideErrorHandler}</li>
 * <li>{@link ToolTipErrorHandler}</li>
 * <li>{@link TitleErrorHandler}</li>
 * <li>{@link ElementErrorHandler}</li>
 * </ul>
 */
public interface ErrorHandler {

  /**
   * Clears the errors from the field.
   */
  void clearInvalid();

  /**
   * Assigns errors to be displayed on the field.
   * 
   * @param errors the errors to display to the user
   */
  void markInvalid(List<EditorError> errors);

  /**
   * Called to indicate that the instance will no longer be used, and should clean itself up. The 
   * {@link #clearInvalid()} method should be called prior to this to clean up any leftover errors.
   */
  void release();
}
