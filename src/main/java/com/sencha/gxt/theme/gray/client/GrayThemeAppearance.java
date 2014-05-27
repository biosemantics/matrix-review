/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.gray.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.resources.ThemeStyles.Styles;
import com.sencha.gxt.core.client.resources.ThemeStyles.ThemeAppearance;

public class GrayThemeAppearance implements ThemeAppearance {

  static interface Bundle extends ClientBundle {

    @Source({"com/sencha/gxt/theme/base/client/BaseTheme.css", "GrayTheme.css"})
    GrayStyles css();

    ImageResource more();
  }

  interface GrayStyles extends Styles {
    String borderColor();

    String borderColorLight();

    String backgroundColorLight();
  }

  private Bundle bundle;
  private GrayStyles style;

  @Override
  public Styles style() {
    return style;
  }

  public GrayThemeAppearance() {
    this.bundle = GWT.create(Bundle.class);
    this.style = bundle.css();

    StyleInjectorHelper.ensureInjected(this.style, true);
  }

  @Override
  public ImageResource moreIcon() {
    return bundle.more();
  }

  @Override
  public String borderColor() {
    return style.borderColor();
  }

  @Override
  public String borderColorLight() {
    return style.borderColorLight();
  }

  @Override
  public String backgroundColorLight() {
    return style.backgroundColorLight();
  }
}
