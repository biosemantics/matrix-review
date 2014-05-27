/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import java.text.ParseException;
import java.util.Date;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.Anchor;
import com.sencha.gxt.core.client.Style.AnchorAlignment;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;
import com.sencha.gxt.widget.core.client.DatePicker;
import com.sencha.gxt.widget.core.client.event.CollapseEvent;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.HasCollapseHandlers;
import com.sencha.gxt.widget.core.client.event.ExpandEvent;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.ExpandHandler;
import com.sencha.gxt.widget.core.client.event.ExpandEvent.HasExpandHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.DateTimePropertyEditor;
import com.sencha.gxt.widget.core.client.menu.DateMenu;

public class DateCell extends TriggerFieldCell<Date> implements HasExpandHandlers, HasCollapseHandlers {

  public interface DateCellAppearance extends TriggerFieldAppearance {

  }

  private GroupingHandlerRegistration menuHandler;
  private DateMenu menu;
  private boolean expanded;

  /**
   * Creates a new date cell.
   */
  public DateCell() {
    this(GWT.<DateCellAppearance> create(DateCellAppearance.class));
  }

  /**
   * Creates a new date cell.
   * 
   * @param appearance the date cell appearance
   */
  public DateCell(DateCellAppearance appearance) {
    super(appearance);
    setPropertyEditor(new DateTimePropertyEditor());
  }

  @Override
  public HandlerRegistration addCollapseHandler(CollapseHandler handler) {
    return addHandler(handler, CollapseEvent.getType());
  }

  @Override
  public HandlerRegistration addExpandHandler(ExpandHandler handler) {
    return addHandler(handler, ExpandEvent.getType());
  }

  public void collapse(final Context context, final XElement parent) {
    if (!expanded) {
      return;
    }

    expanded = false;

    menu.hide();
    getInputElement(parent).focus();
    fireEvent(context, new CollapseEvent(context));
  }

  public void expand(final Context context, final XElement parent, Date value, ValueUpdater<Date> valueUpdater) {
    if (expanded) {
      return;
    }

    this.expanded = true;

    // expand may be called without the cell being focused
    // saveContext sets focusedCell so we clear if cell 
    // not currently focused
    boolean focused = focusedCell != null;
    saveContext(context, parent, null, valueUpdater, value);
    if (!focused) {
      focusedCell = null;
    }

    DatePicker picker = getDatePicker();

    Date d = null;
    try {
      d = getPropertyEditor().parse(getText(parent));
    } catch (ParseException e) {
      d = value == null ? new Date() : value;
    }
    
    picker.setValue(d, false);

    // handle case when down arrow is opening menu
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {

      @Override
      public void execute() {
        menu.show(parent, new AnchorAlignment(Anchor.TOP_LEFT, Anchor.BOTTOM_LEFT, true));
        menu.getDatePicker().focus();
        
        fireEvent(context, new ExpandEvent(context));
      }
    });


  }

  /**
   * Returns the cell's date picker.
   * 
   * @return the date picker
   */
  public DatePicker getDatePicker() {
    if (menu == null) {
      setMenu(new DateMenu());
    }
    return menu.getDatePicker();
  }

  /**
   * @return the menu instance used to get the datepicker from
   */
  public DateMenu getMenu() {
    return menu;
  }

  public boolean isExpanded() {
    return expanded;
  }

  /**
   * Sets the DateMenu instance to use in this cell when drawing a datepicker
   * @param menu the menu instance to get the datepicker from
   */
  public void setMenu(final DateMenu menu) {
    if (this.menu != null) {
      menuHandler.removeHandler();
      menuHandler = null;
    }
    this.menu = menu;
    if (this.menu != null) {
      menuHandler = new GroupingHandlerRegistration();

      menuHandler.add(menu.getDatePicker().addValueChangeHandler(new ValueChangeHandler<Date>() {
        @Override
        public void onValueChange(ValueChangeEvent<Date> event) {
          String s = getPropertyEditor().render(event.getValue());
          FieldViewData viewData = ensureViewData(lastContext, lastParent);
          if (viewData != null) {
            viewData.setCurrentValue(s);
          }
          getInputElement(lastParent).setValue(s);
          getInputElement(lastParent).focus();

          Scheduler.get().scheduleFinally(new ScheduledCommand() {

            @Override
            public void execute() {
              getInputElement(lastParent).focus();
            }
          });

          menu.hide();
        }
      }));
      menuHandler.add(menu.addHideHandler(new HideHandler() {
        @Override
        public void onHide(HideEvent event) {
          collapse(lastContext, lastParent);
        }
      }));
    }
  }

  @Override
  protected boolean isFocusedWithTarget(Element parent, Element target) {
    boolean result = parent.isOrHasChild(target)
        || (menu != null && (menu.getElement().isOrHasChild(target) || menu.getDatePicker().getElement().isOrHasChild(
            target)));
    return result;
  }

  @Override
  protected void onNavigationKey(Context context, Element parent, Date value, NativeEvent event,
      ValueUpdater<Date> valueUpdater) {
    if (event.getKeyCode() == KeyCodes.KEY_DOWN && !isExpanded()) {
      event.stopPropagation();
      event.preventDefault();
      onTriggerClick(context, parent.<XElement> cast(), event, value, valueUpdater);
    }
  }

  @Override
  protected void onTriggerClick(Context context, XElement parent, NativeEvent event, Date value,
      ValueUpdater<Date> updater) {
    super.onTriggerClick(context, parent, event, value, updater);
    if (!isReadOnly() && !isDisabled()) {
      // blur is firing after the expand so context info on expand is being cleared
      // when value change fires lastContext and lastParent are null without this code
      if ((GXT.isWebKit()) && lastParent != null && lastParent != parent) {
        getInputElement(lastParent).blur();
      }
      onFocus(context, parent, value, event, updater);
      expand(context, parent, value, updater);
    }
  }

  @Override
  protected void triggerBlur(Context context, XElement parent, Date value, ValueUpdater<Date> valueUpdater) {
    super.triggerBlur(context, parent, value, valueUpdater);

    collapse(context, parent);
  }
}