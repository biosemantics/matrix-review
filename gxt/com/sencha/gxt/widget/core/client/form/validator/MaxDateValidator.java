/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.form.validator;

import java.util.Date;
import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.sencha.gxt.core.client.util.DateWrapper;
import com.sencha.gxt.messages.client.DefaultMessages;

/**
 * Tests if the value is on the same day or earlier than the specified max date.
 */
public class MaxDateValidator extends AbstractValidator<Date> {

  public interface MaxDateMessages {
    String dateMaxText(String max);
  }

  protected class DefaultMaxDateMessages implements MaxDateMessages {

    @Override
    public String dateMaxText(String max) {
      return DefaultMessages.getMessages().dateField_maxText(max);
    }

  }

  protected Date maxDate;
  private MaxDateMessages messages;
  private DateTimeFormat format = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);

  public MaxDateValidator(Date maxDate) {
    setMaxDate(maxDate);
  }

  public MaxDateValidator(Date maxDate, DateTimeFormat format) {
    this(maxDate);
    this.format = format;
  }

  public Date getMaxDate() {
    return maxDate;
  }

  public MaxDateMessages getMessages() {
    if (messages == null) {
      messages = new DefaultMaxDateMessages();
    }
    return messages;
  }

  /**
   * Sets the max date. Hours, minutes, seconds, and milliseconds are cleared.
   * 
   * @param maxDate the max date
   */
  public void setMaxDate(Date maxDate) {
    this.maxDate = new DateWrapper(maxDate).clearTime().asDate();
  }

  public void setMessages(MaxDateMessages messages) {
    this.messages = messages;
  }

  @Override
  public List<EditorError> validate(Editor<Date> editor, Date value) {
    if (value != null && value.after(maxDate)) {
      return createError(editor, getMessages().dateMaxText(format.format(maxDate)), value);
    }
    return null;
  }
}
