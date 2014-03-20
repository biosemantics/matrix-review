/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.dom;

/**
 * Interface for objects that provide scroll support.
 *
 * @see ScrollSupport
 */
public interface HasScrollSupport {

  /**
   * Returns the scroll support instance.
   * @return the scroll support instance
   */
  public ScrollSupport getScrollSupport();
  
  /**
   * Sets the scroll support.
   * 
   * @param scrollSupport the scroll support
   */
  public void setScrollSupport(ScrollSupport scrollSupport);
}
