/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.core.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public class CommonIcons {

  public static interface Icons extends ClientBundle {

    ImageResource lessThan();
    
    ImageResource greaterThan();
    
    ImageResource equals();

  }

  private static final Icons instance = GWT.create(Icons.class);

  public static Icons get() {
    return instance;
  }

}
