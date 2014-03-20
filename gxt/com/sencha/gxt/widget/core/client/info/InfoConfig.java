/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.info;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;

/**
 * Abstract base class for configuration settings for {@link Info}.
 */
public abstract class InfoConfig {

  /**
   * Defines the locations the Info is displayed.
   */
  public enum InfoPosition {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
  }

  private int display = 2500;
  private int width = 225;
  private int height = -1;
  private InfoPosition position = InfoPosition.TOP_RIGHT;
  private int margin = 10;
  private ShowHandler showHandler;
  private HideHandler hideHandler;

  /**
   * Returns the time the info is displayed.
   * 
   * @return the delay in milliseconds
   */
  public int getDisplay() {
    return display;
  }

  /**
   * Returns the info height.
   * 
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * Returns the hide handler.
   * 
   * @return the hide handler
   */
  public HideHandler getHideHandler() {
    return hideHandler;
  }

  /**
   * Returns the amount of pixels between info's.
   * 
   * @return the margin
   */
  public int getMargin() {
    return margin;
  }

  /**
   * Returns the info position.
   * 
   * @return the info position
   */
  public InfoPosition getPosition() {
    return position;
  }

  /**
   * Returns the show handler.
   * 
   * @return the show handler
   */
  public ShowHandler getShowHandler() {
    return showHandler;
  }

  /**
   * Returns the info width.
   * 
   * @return the info width
   */
  public int getWidth() {
    return width;
  }

  /**
   * Sets the amount of time the info is displayed in milliseconds (defaults to 2500).
   * 
   * @param display
   */
  public void setDisplay(int display) {
    this.display = display;
  }

  /**
   * Sets the info height (defaults to -1).
   * 
   * @param height the pixel height, -1 for auto height
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Sets the handler to be notified when the info is hidden.
   * 
   * @param hideHandler the hide handler
   */
  public void setHideHandler(HideHandler hideHandler) {
    this.hideHandler = hideHandler;
  }

  /**
   * Sets the margin between info's (defaults to 10).
   * 
   * @param margin the margin
   */
  public void setMargin(int margin) {
    this.margin = margin;
  }

  /**
   * Sets the position the info is displayed (defaults to TOP_RIGHT).
   * 
   * @param infoPosition the position
   */
  public void setPosition(InfoPosition infoPosition) {
    this.position = infoPosition;
  }

  /**
   * Sets the handler to be notified when the info is displayed.
   * 
   * @param showHandler the show handler
   */
  public void setShowHandler(ShowHandler showHandler) {
    this.showHandler = showHandler;
  }

  /**
   * Sets the width of the info (defaults to 225).
   * 
   * @param width the width in pixels
   */
  public void setWidth(int width) {
    this.width = width;
  }

  protected abstract SafeHtml render(Info info);

}
