/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared;

/**
 * A concrete <code>LabelProvider</code> implementation that simple calls
 * toString on the target object.
 * 
 * @param <T> the target object type
 */
public class StringLabelProvider<T> implements LabelProvider<T> {

  @Override
  public String getLabel(T item) {
    return item.toString();
  }

}
