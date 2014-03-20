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
import com.sencha.gxt.cell.core.client.SliderCell.HorizontalSliderAppearance;
import com.sencha.gxt.theme.base.client.slider.SliderHorizontalBaseAppearance;

public class BlueSliderHorizontalAppearance extends SliderHorizontalBaseAppearance implements HorizontalSliderAppearance {

  public interface BlueHorizontalSliderStyle extends SliderHorizontalStyle, CssResource {
  }

  public interface BlueSliderHorizontalResources extends SliderHorizontalResources, ClientBundle {

    @Source({
        "com/sencha/gxt/theme/base/client/slider/Slider.css",
        "com/sencha/gxt/theme/base/client/slider/SliderHorizontal.css", "BlueSliderHorizontal.css"})
    BlueHorizontalSliderStyle style();

    ImageResource thumbHorizontal();

    ImageResource thumbHorizontalDown();

    ImageResource thumbHorizontalOver();

    ImageResource trackHorizontalLeft();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource trackHorizontalMiddle();

    ImageResource trackHorizontalRight();

  }

  public BlueSliderHorizontalAppearance() {
    this(GWT.<BlueSliderHorizontalResources> create(BlueSliderHorizontalResources.class),
        GWT.<SliderHorizontalTemplate> create(SliderHorizontalTemplate.class));
  }

  public BlueSliderHorizontalAppearance(BlueSliderHorizontalResources resources,
      SliderHorizontalTemplate template) {
    super(resources, template);
  }

}
