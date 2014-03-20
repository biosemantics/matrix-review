/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.theme.blue.client.status;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.sencha.gxt.theme.base.client.status.BoxStatusBaseAppearance;
import com.sencha.gxt.widget.core.client.Status.BoxStatusAppearance;

public class BlueBoxStatusAppearance extends BoxStatusBaseAppearance implements BoxStatusAppearance {

  public interface BlueBoxStatusStyle extends BoxStatusStyle {

  }

  public interface BlueBoxStatusResources extends BoxStatusResources, ClientBundle {

    @Override
    @Source({"com/sencha/gxt/theme/base/client/status/Status.css", "BlueBoxStatus.css"})
    BlueBoxStatusStyle style();
  }

  public BlueBoxStatusAppearance() {
    this(GWT.<BlueBoxStatusResources> create(BlueBoxStatusResources.class), GWT.<BoxTemplate> create(BoxTemplate.class));
  }

  public BlueBoxStatusAppearance(BlueBoxStatusResources resources, BoxTemplate template) {
    super(resources, template);
  }

}
