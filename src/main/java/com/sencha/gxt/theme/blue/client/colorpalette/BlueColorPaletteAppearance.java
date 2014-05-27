/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.colorpalette;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.theme.base.client.colorpalette.ColorPaletteBaseAppearance;

public class BlueColorPaletteAppearance extends ColorPaletteBaseAppearance {

  public interface BlueColorPaletteResources extends ColorPaletteBaseAppearance.ColorPaletteResources, ClientBundle {

    @Override
    @Source({"com/sencha/gxt/theme/base/client/colorpalette/ColorPalette.css", "BlueColorPalette.css"})
    BlueColorPaletteStyle style();

  }

  public interface BlueColorPaletteStyle extends ColorPaletteStyle {
  }

  public BlueColorPaletteAppearance() {
    this(GWT.<BlueColorPaletteResources> create(BlueColorPaletteResources.class),
        GWT.<BaseColorPaletteTemplate> create(BaseColorPaletteTemplate.class));
  }

  public BlueColorPaletteAppearance(BlueColorPaletteResources resources, BaseColorPaletteTemplate template) {
    super(resources, template);
  }

}
