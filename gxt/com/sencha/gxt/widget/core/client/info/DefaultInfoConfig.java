/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * Configuration settings for {@link Info} which supports a title and text.
 */
public class DefaultInfoConfig extends InfoConfig {

  public interface DefaultInfoConfigAppearance {

    void render(SafeHtmlBuilder sb, SafeHtml title, SafeHtml message);
  }

  private DefaultInfoConfigAppearance appearance;
  private SafeHtml title;
  private SafeHtml message;

  /**
   * Creates a new config for an Info to display.
   * 
   * @param title the title text
   * @param message the message text
   */
  public DefaultInfoConfig(String title, String message) {
    this((DefaultInfoConfigAppearance) GWT.create(DefaultInfoConfigAppearance.class), title, message);
  }

  /**
   * Creates a new config for an Info to display.
   * 
   * @param title the title as HTML
   * @param message the message as HTML
   */
  public DefaultInfoConfig(SafeHtml title, SafeHtml message) {
    this((DefaultInfoConfigAppearance) GWT.create(DefaultInfoConfigAppearance.class), title, message);
  }

  /**
   * Creates a new config for an Info to display.
   * 
   * @param appearance the appearance to use
   * @param title the title text
   * @param message the message text
   */
  public DefaultInfoConfig(DefaultInfoConfigAppearance appearance,String title, String message) {
    this(appearance, SafeHtmlUtils.fromString(title), SafeHtmlUtils.fromString(message));
  }

  /**
   * Creates a new config for an Info to display.
   * 
   * @param appearance the appearance to use
   * @param title the title as HTML
   * @param message the message as HTML
   */
  public DefaultInfoConfig(DefaultInfoConfigAppearance appearance, SafeHtml title, SafeHtml message) {
    this.appearance = appearance;
    this.title = title;
    this.message = message;
  }

  public DefaultInfoConfigAppearance getAppearance() {
    return appearance;
  }

  public SafeHtml getMessage() {
    return message;
  }

  public SafeHtml getTitle() {
    return title;
  }

  public void setMessage(String message) {
    setMessage(SafeHtmlUtils.fromString(message));
  }

  public void setTitle(String title) {
    setTitle(SafeHtmlUtils.fromString(title));
  }

  public void setTitle(SafeHtml title) {
    this.title = title;
  }

  public void setMessage(SafeHtml message) {
    this.message = message;
  }

  @Override
  protected SafeHtml render(Info info) {
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    appearance.render(builder, title, message);
    return builder.toSafeHtml();
  }

}
