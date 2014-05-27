/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared.loader;

/**
 * A {@link FilterHandler} that provides support for <code>String</code> values.
 */
public class StringFilterHandler extends FilterHandler<String> {

  @Override
  public String convertToObject(String value) {
    return value;
  }

  @Override
  public String convertToString(String object) {
    return object;
  }

}
