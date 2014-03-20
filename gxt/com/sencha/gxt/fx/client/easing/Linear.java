/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.easing;

/**
 * A one to one {@link EasingFunction}.
 * 
 */
public class Linear implements EasingFunction {

  @Override
  public double func(double n) {
    return n;
  }

}
