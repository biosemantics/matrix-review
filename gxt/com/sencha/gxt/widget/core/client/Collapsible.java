/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

/**
 * Interface for objects that can be collapsed.
 */
public interface Collapsible {

  /**
   * Collapses the widget.
   */
  void collapse();

  /**
   * Expands the widget.
   */
  void expand();

  /**
   * Returns true if the widget is expanded.
   * 
   * @return true for expanded
   */
  boolean isExpanded();
}
