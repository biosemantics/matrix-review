/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

/**
 * A {@link FilterHandler} that provides support for <code>Boolean</code> values.
 */
public class BooleanFilterHandler extends FilterHandler<Boolean> {

  @Override
  public Boolean convertToObject(String value) {
    return Boolean.valueOf(value);
  }

  @Override
  public String convertToString(Boolean object) {
    return object.toString();
  }
}
