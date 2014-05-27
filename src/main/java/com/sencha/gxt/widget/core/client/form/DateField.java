/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form;

import java.util.Date;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.cell.core.client.form.DateCell;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.DatePicker;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.HasCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.HasExpandHandlers;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.form.validator.MaxDateValidator;
import com.sencha.gxt.widget.core.client.form.validator.MinDateValidator;

/**
 * Provides a date input field with a {@link DatePicker} dropdown and automatic
 * date validation.
 */
public class DateField extends TriggerField<Date> implements HasExpandHandlers, HasCollapseHandlers {

  private MaxDateValidator maxDateValidator;
  private MinDateValidator minDateValidator;

  /**
   * Creates a new date field.
   */
  public DateField() {
    this(new DateTimePropertyEditor());
  }

  /**
   * Creates a new date field.
   * 
   * @param cell the date cell
   */
  public DateField(DateCell cell) {
    this(cell, new DateTimePropertyEditor());
  }

  /**
   * Creates a new date field.
   * 
   * @param cell the date cell
   * @param propertyEditor the property editor
   */
  public DateField(DateCell cell, DateTimePropertyEditor propertyEditor) {
    super(cell);
    setPropertyEditor(propertyEditor);
    redraw();
  }

  /**
   * Creates a new date field.
   * 
   * @param propertyEditor the property editor
   */
  public DateField(DateTimePropertyEditor propertyEditor) {
    this(new DateCell(), propertyEditor);
  }

  @Override
  public DateCell getCell() {
    return (DateCell) super.getCell();
  }

  /**
   * Returns the field's date picker.
   * 
   * @return the date picker
   */
  public DatePicker getDatePicker() {
    return getCell().getDatePicker();
  }

  /**
   * Returns the field's max value.
   * 
   * @return the max value
   */
  public Date getMaxValue() {
    if (maxDateValidator != null) {
      return maxDateValidator.getMaxDate();
    }
    return null;
  }

  /**
   * Returns the field's minimum value.
   * 
   * @return the minimum value
   */
  public Date getMinValue() {
    if (minDateValidator != null) {
      minDateValidator.getMinDate();
    }
    return null;
  }

  @Override
  public DateTimePropertyEditor getPropertyEditor() {
    return (DateTimePropertyEditor) super.getPropertyEditor();
  }

  /**
   * The maximum date allowed. Adds a maximum date validator. 
   * 
   * @param maxValue the maximum value. Set maxValue to null to remove the maximum validation.
   */
  public void setMaxValue(Date maxValue) {
    if (maxValue == null) {
      if (maxDateValidator != null) {
        removeValidator(maxDateValidator);
        maxDateValidator = null;
      }
      return;
    }
    if (maxDateValidator == null) {
      maxDateValidator = new MaxDateValidator(maxValue);
      addValidator(maxDateValidator);
    }
    if (maxValue != null) {
      maxValue = new DateWrapper(maxValue).resetTime().asDate();
      maxDateValidator.setMaxDate(maxValue);
    }
  }

  /**
   * The minimum date allowed. Adds a minimum date validator.  
   * 
   * @param minValue the minimum value. Set minValue to null to remove the minimum validation.
   */
  public void setMinValue(Date minValue) {
    if (minValue == null) {
      if (minDateValidator != null) {
        removeValidator(minDateValidator);
        minDateValidator = null;
      }
      return;
    }
    if (minDateValidator == null) {
      minDateValidator = new MinDateValidator(minValue);
      addValidator(minDateValidator);
    }
    if (minValue != null) {
      minValue = new DateWrapper(minValue).resetTime().asDate();
      minDateValidator.setMinDate(minValue);
    }
  }

  protected void expand() {
    getCell().expand(createContext(), getElement(), getValue(), valueUpdater);
  }

  @Override
  protected void onCellParseError(ParseErrorEvent event) {
    super.onCellParseError(event);
    String value = event.getException().getMessage();
    String f = getPropertyEditor().getFormat().getPattern();
    String msg = DefaultMessages.getMessages().dateField_invalidText(value, f);
    parseError = msg;
    forceInvalid(msg);
  }

  @Override
  public HandlerRegistration addExpandHandler(ExpandHandler handler) {
    return addHandler(handler, ExpandEvent.getType());
  }

  @Override
  public HandlerRegistration addCollapseHandler(CollapseHandler handler) {
    return addHandler(handler, CollapseEvent.getType());
  }

}
