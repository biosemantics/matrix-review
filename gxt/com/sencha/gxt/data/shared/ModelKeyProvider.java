/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.data.shared;

import com.google.gwt.view.client.ProvidesKey;

/**
 * ModelKeyProviders are responsible for returning a unique key for a given
 * model.
 * 
 * @see PropertyAccess
 * @param <T> the model type
 */
public interface ModelKeyProvider<T> extends ProvidesKey<T> {

  /**
   * Gets a non-null key value that maps to this object. Keys must be consistent and
   * unique for a given model, as a database primary key would be used.
   */
  String getKey(T item);
}