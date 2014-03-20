/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.slider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.cell.core.client.SliderCell.VerticalSliderAppearance;
import com.sencha.gxt.theme.base.client.slider.SliderVerticalBaseAppearance;

public class BlueSliderVerticalAppearance extends SliderVerticalBaseAppearance implements VerticalSliderAppearance {

  public interface BlueSliderVerticalResources extends SliderVerticalResources, ClientBundle {

    @Override
    @Source({
        "com/sencha/gxt/theme/base/client/slider/Slider.css",
        "com/sencha/gxt/theme/base/client/slider/SliderVertical.css",
        "BlueSliderVertical.css"})
    BlueVerticalSliderStyle style();

    ImageResource thumbVertical();

    ImageResource thumbVerticalDown();

    ImageResource thumbVerticalOver();

    ImageResource trackVerticalBottom();

    @ImageOptions(repeatStyle = RepeatStyle.Vertical)
    ImageResource trackVerticalMiddle();

    ImageResource trackVerticalTop();

  }

  public interface BlueVerticalSliderStyle extends BaseSliderVerticalStyle, CssResource {
  }

  public BlueSliderVerticalAppearance() {
    this(GWT.<BlueSliderVerticalResources> create(BlueSliderVerticalResources.class),
        GWT.<SliderVerticalTemplate> create(SliderVerticalTemplate.class));
  }

  public BlueSliderVerticalAppearance(BlueSliderVerticalResources resources, SliderVerticalTemplate template) {
    super(resources, template);
  }

}
