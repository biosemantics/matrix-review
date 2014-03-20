/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.easing;

/**
 * The default {@link EasingFunction} for animation.
 */
public class Default implements EasingFunction {

  @Override
  public double func(double n) {
    return (1 + Math.cos(Math.PI + n * Math.PI)) / 2;
  }

}
