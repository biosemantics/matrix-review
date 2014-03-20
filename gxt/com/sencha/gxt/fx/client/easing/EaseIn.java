/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.fx.client.easing;

/**
 * {@link EasingFunction} that produces an easing at the start of the animation.
 */
public class EaseIn implements EasingFunction {

  @Override
  public double func(double n) {
    return Math.pow(n, 1.7);
  }

}
