/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.base.client.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;
import com.sencha.gxt.widget.core.client.form.StoreFilterField.StoreFilterFieldAppearance;

public class StoreFilterFieldDefaultAppearance extends TriggerFieldDefaultAppearance implements StoreFilterFieldAppearance {

  public interface StoreFilterFieldResources extends TriggerFieldResources {

    @Source({"ValueBaseField.css", "TextField.css", "TriggerField.css", "StoreFilterField.css"})
    StoreFilterFieldStyle css();

    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource textBackground();

    @Source("clearTrigger.gif")
    ImageResource triggerArrow();

    @Source("clearTriggerOver.gif")
    ImageResource triggerArrowOver();

    @Source("clearTriggerClick.gif")
    ImageResource triggerArrowClick();

    @Source("clearTriggerFocus.gif")
    ImageResource triggerArrowFocus();

  }
  
  public interface StoreFilterFieldStyle extends TriggerFieldStyle {
    
  }

  public StoreFilterFieldDefaultAppearance() {
    this(GWT.<StoreFilterFieldResources> create(StoreFilterFieldResources.class));
  }

  public StoreFilterFieldDefaultAppearance(StoreFilterFieldResources resources) {
    super(resources);
  }
}
