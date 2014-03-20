/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.cell.core.client.SliderCell;
import com.sencha.gxt.cell.core.client.SliderCell.HorizontalSliderAppearance;
import com.sencha.gxt.cell.core.client.SliderCell.VerticalSliderAppearance;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * Lets the user select a value by sliding an indicator within a bounded range.
 */
public class Slider extends Field<Integer> {

  /**
   * Creates a slider with the default slider cell.
   */
  public Slider() {
    super(new SliderCell());
    setAllowTextSelection(false);
    redraw();
  }

  /**
   * Creates a slider with the specified orientation.
   * 
   * @param vertical true to create a vertical slider
   */
  public Slider(boolean vertical) {
    super(new SliderCell(vertical ? GWT.<VerticalSliderAppearance> create(VerticalSliderAppearance.class)
        : GWT.<HorizontalSliderAppearance> create(HorizontalSliderAppearance.class)));
    redraw();
  }

  /**
   * Creates a slider with the specified slider cell.
   * 
   * @param cell the cell for this slider
   */
  public Slider(SliderCell cell) {
    super(cell);
    redraw();
  }

  @Override
  public SliderCell getCell() {
    return (SliderCell) super.getCell();
  }

  /**
   * Returns the increment.
   * 
   * @return the increment
   */
  public int getIncrement() {
    return getCell().getIncrement();
  }

  /**
   * Returns the max value (defaults to 100).
   * 
   * @return the max value
   */
  public int getMaxValue() {
    return getCell().getMaxValue();
  }

  /**
   * Returns the tool tip message.
   * 
   * @return the tool tip message
   */
  public String getMessage() {
    return getCell().getMessage();
  }

  /**
   * Returns the minimum value (defaults to 0).
   * 
   * @return the minimum value
   */
  public int getMinValue() {
    return getCell().getMinValue();
  }
  
  /**
   * Returns true if the tool tip message is shown 
   * 
   * @return the showMessage state
   */
  public boolean isShowMessage() {
    return getCell().isShowMessage();
  }

  /**
   * How many units to change the slider when adjusting by drag and drop. Use
   * this option to enable 'snapping' (default to 10).
   * 
   * @param increment the increment
   */
  public void setIncrement(int increment) {
    getCell().setIncrement(increment);
  }

  /**
   * Sets the max value (defaults to 100).
   * 
   * @param maxValue the max value
   */
  public void setMaxValue(int maxValue) {
    getCell().setMaxValue(maxValue);
  }

  /**
   * Sets the tool tip message (defaults to '{0}'). "{0} will be substituted
   * with the current slider value.
   * 
   * @param message the tool tip message
   */
  public void setMessage(String message) {
    getCell().setMessage(message);
  }

  /**
   * Sets the minimum value (defaults to 0).
   * 
   * @param minValue the minimum value
   */
  public void setMinValue(int minValue) {
    getCell().setMinValue(minValue);
  }
  
  /**
   * Sets if the tool tip message should be displayed (defaults to true,
   * pre-render).
   * 
   * @param showMessage true to show tool tip message
   */
  public void setShowMessage(boolean showMessage) {
    getCell().setShowMessage(showMessage);
  }

  /**
   * Set the tooltip config. This is the tooltip for the message configuration.
   * {@link SliderCell#setShowMessage(boolean)} has to be on to use this.
   *
   * @param toolTipConfig is the tooltip configuration.
   */
  public void setToolTipConfig(ToolTipConfig toolTipConfig) {
    getCell().setToolTipConfig(toolTipConfig);
  }

}
