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
import com.sencha.gxt.widget.core.client.Component;

public class TitleErrorHandler implements ErrorHandler {

  protected Component target;
  
  public TitleErrorHandler(Component target) {
    this.target = target;
  }

  @Override
  public void clearInvalid() {
    target.setTitle("");
  }

  @Override
  public void markInvalid(List<EditorError> errors) {
    target.setTitle(errors.get(0).getMessage());
  }

  @Override
  public void release() {
    //no handlers to remove
  }
}