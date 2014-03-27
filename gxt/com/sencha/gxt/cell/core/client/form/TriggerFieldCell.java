/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client.form;

import java.text.ParseException;
import java.util.logging.Logger;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.event.TriggerClickEvent;

/**
 * A base cell for an input field and a clickable trigger. The purpose of the trigger is defined by the
 * derived class (e.g. displaying a drop down or modifying the value of the input field).
 *
 * @param <T> the cell model type
 */
public class TriggerFieldCell<T> extends ValueBaseInputCell<T> {

  public interface TriggerFieldAppearance extends ValueBaseFieldAppearance {
    void onResize(XElement parent, int width, int height, boolean hideTrigger);

    void onTriggerClick(XElement parent, boolean click);

    void onTriggerOver(XElement parent, boolean over);

    void render(SafeHtmlBuilder sb, String value, FieldAppearanceOptions options);

    void setEditable(XElement parent, boolean editable);

    boolean triggerIsOrHasChild(XElement parent, Element target);

  }

  class MouseDownHandler extends DelayedTask implements NativePreviewHandler {

    private final Context context;
    private final XElement parent;
    private final ValueUpdater<T> updater;
    private final T value;
    private HandlerRegistration reg;

    public MouseDownHandler(Context context, XElement parent, T value, ValueUpdater<T> updater) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("add event preview");
      }

      this.context = context;
      this.parent = parent;
      this.value = value;
      this.updater = updater;
      reg = Event.addNativePreviewHandler(this);
      delay(100);
    }

    @Override
    public void onExecute() {
      Element activeElement = XDOM.getActiveElement();
      if (parent.getOffsetParent() == null ||
              (activeElement != null &&
                      !isFocusedWithTarget(parent, activeElement))) {
        //either we are invisible, or someone else has focus, drop the appearance of focus and trigger the blur
        triggerBlur(context, parent, value, updater);
        onBlur(context, parent, value, null, updater);
      } else {
        delay(100);
      }
    }

    @Override
    public void onPreviewNativeEvent(NativePreviewEvent event) {
      NativeEvent e = event.getNativeEvent();
      XElement target = event.getNativeEvent().getEventTarget().cast();
      if ("mousedown".equals(e.getType())) {
        if (GXTLogConfiguration.loggingIsEnabled()) {
          logger.finest("preview mouse down");
        }

        if (!isFocusedWithTarget(parent.<XElement>cast(), target)) {
          if (GXTLogConfiguration.loggingIsEnabled()) {
            logger.finest("preview mouse down not a focus click, remove preview");
          }

          remove();
          lastValueUpdater = updater;
          lastContext = context;

          // rather than calling triggerBlur, we just set the mimic flag and
          // wait
          // for onBlur to be called, then we finish the edit
          mimicking = false;
        }
      }
    }
    public void remove() {
      cancel();
      reg.removeHandler();
      reg = null;
      focusManagerRegistration = null;
    }
  }

  protected static TriggerFieldCell<?> focusedCell;
  protected boolean finishEditOnEnter = true;
  
  private static void ensureBlur() {
    if (focusedCell != null) {
      focusedCell.doTriggerBlur();
    }
  }

  protected boolean mimicking;

  private boolean editable = true;
  private boolean hideTrigger;
  private boolean monitorTab = true;
  protected MouseDownHandler focusManagerRegistration;
  private static Logger logger = Logger.getLogger(TriggerFieldCell.class.getName());

  /**
   * Creates a new trigger cell instance.
   */
  public TriggerFieldCell() {
    this(GWT.<TriggerFieldAppearance> create(TriggerFieldAppearance.class));
  }

  /**
   * Creates a new trigger cell instance.
   * 
   * @param appearance the appearance
   */
  public TriggerFieldCell(TriggerFieldAppearance appearance) {
    super(appearance);
    finishEditOnBlur = false;
  }

  @Override
  public void finishEditing(final Element parent, T value, Object key, ValueUpdater<T> valueUpdater) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("finishEditing");
    }

    if (focusManagerRegistration != null) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("TriggerFieldCell finishEditing remove event preview");
      }

      focusManagerRegistration.remove();
    }

    String newValue = getText(XElement.as(parent));

    // Get the view data.
    FieldViewData vd = getViewData(key);
    if (vd == null) {
      vd = new FieldViewData(value == null ? "" : getPropertyEditor().render(value));
      setViewData(key, vd);
    }
    vd.setCurrentValue(newValue);

    // Fire the value updater if the value has changed.
    if (valueUpdater != null && !vd.getCurrentValue().equals(vd.getLastValue())) {
      vd.setLastValue(newValue);
      try {
        if (GXTLogConfiguration.loggingIsEnabled()) {
          logger.finest("finishEditing saving value: " + newValue);
        }

        if ("".equals(newValue)) {
          value = null;

          valueUpdater.update(null);
        } else {
          value = getPropertyEditor().parse(newValue);

          valueUpdater.update(value);

          // parsing may have changed value
          getInputElement(parent).setValue(value == null ? "" : getPropertyEditor().render(value));
        }

      } catch (ParseException e) {
        if (GXTLogConfiguration.loggingIsEnabled()) {
          logger.finest("finishEditing parseError: " + e.getMessage());
        }

        if (isClearValueOnParseError()) {
          vd.setCurrentValue("");
          valueUpdater.update(null);
          setText(parent.<XElement> cast(), "");
        }

        fireEvent(new ParseErrorEvent(newValue, e));
      }
    } else {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("finishEditing value not changed: " + newValue + " old: " + vd.getLastValue());
      }

    }

    clearViewData(key);
    clearFocusKey();
    focusedCell = null;

    // not calling super as GWT code does input.blur() which breaks navigation between fields
  }

  @Override
  public TriggerFieldAppearance getAppearance() {
    return (TriggerFieldAppearance) super.getAppearance();
  }

  /**
   * Returns true if the field is editable.
   * 
   * @return true if editable
   */
  public boolean isEditable() {
    return editable;
  }

  /**
   * Returns the finish on enter key state.
   * 
   * @return {@code true} if editing finished on enter key
   */
  public boolean isFinishEditOnEnter() {
    return finishEditOnEnter;
  }

  /**
   * Returns {@code true} if the trigger is hidden.
   * 
   * @return {@code true} if hidden
   */
  public boolean isHideTrigger() {
    return hideTrigger;
  }

  /**
   * Returns true if tab key events are being monitored.
   * 
   * @return true if monitoring
   */
  public boolean isMonitorTab() {
    return monitorTab;
  }

  public void onBrowserEvent(Context context, Element parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
    Element target = event.getEventTarget().cast();
    if (!parent.isOrHasChild(target)) {
      return;
    }
    if (isReadOnly() || isDisabled()) {
      return;
    }
    super.onBrowserEvent(context, parent, value, event, valueUpdater);
    String eventType = event.getType();
    Object key = context.getKey();
    if ("change".equals(eventType)) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("onBrowserEvent change event fired mimmicking = " + mimicking);
      }
      if (!mimicking) {
        finishEditing(parent, value, key, valueUpdater);
      }
    } else if ("click".equals(eventType)) {
      onClick(context, parent.<XElement> cast(), event, value, valueUpdater);
    } else if ("keyup".equals(eventType)) {
      // Record keys as they are typed.
      FieldViewData vd = getViewData(key);
      if (vd == null) {
        vd = new FieldViewData(value == null ? "" : getPropertyEditor().render(value));
        setViewData(key, vd);
      }
      vd.setCurrentValue(getText(XElement.as(parent)));
    }
  }

  @Override
  public void render(Context context, T value, SafeHtmlBuilder sb) {
    String v = "";
    if (value != null) {
      v = getPropertyEditor().render(value);
    }

    FieldViewData viewData = checkViewData(context, v);

    FieldAppearanceOptions options = new FieldAppearanceOptions(width, height, isReadOnly());
    options.setEmptyText(getEmptyText());
    options.setHideTrigger(isHideTrigger());
    options.setEditable(editable);
    options.setName(name);
    options.setDisabled(isDisabled());

    String s = (viewData != null) ? viewData.getCurrentValue() : v;
    getAppearance().render(sb, s == null ? "" : s, options);

  }

  public void setEditable(XElement parent, boolean editable) {
    this.editable = editable;

    getAppearance().setEditable(parent, editable);

    InputElement inputElem = getAppearance().getInputElement(parent).cast();
    if (!isReadOnly()) {
      inputElem.setReadOnly(!editable);
    }
  }

  /**
   * Determines if the current edit should be completed when the enter key is pressed (defaults to true). When enabled,
   * the cell will be blurred causing {@link #finishEditing(Element, Object, Object, ValueUpdater)} to be called.
   * 
   * @param finishEditOnEnter {@code true} to call {@link #finishEditing(Element, Object, Object, ValueUpdater)} when
   *          enter key is pressed
   */
  public void setFinishEditOnEnter(boolean finishEditOnEnter) {
    this.finishEditOnEnter = finishEditOnEnter;
  }

  /**
   * Controls the visibility of the cells trigger.
   * 
   * @param hideTrigger {@code true} to hide
   */
  public void setHideTrigger(boolean hideTrigger) {
    this.hideTrigger = hideTrigger;
  }

  /**
   * True to monitor tab key events to force the bluring of the field (defaults to true).
   * 
   * @param monitorTab true to monitor tab key events
   */
  public void setMonitorTab(boolean monitorTab) {
    this.monitorTab = monitorTab;
  }

  @Override
  public void setSize(XElement parent, int width, int height) {
    super.setSize(parent, width, height);
    getAppearance().onResize(parent, width, height, isHideTrigger());
  }

  @Override
  protected void clearContext() {
    super.clearContext();
    focusedCell = null;
  }

  /**
   * Checks if an element can be focused within the current parent. Allows subclasses to let external elements
   * take the focus and still consider this field to be focused.
   *
   * @param parent the parent element of the active cell
   * @param target the element which may have or receive focus
   * @return true of this cell can still have logical focus if the target has dom focus
   */
  protected boolean isFocusedWithTarget(Element parent, Element target) {
    return parent.isOrHasChild(target);
  }

  @Override
  protected void onBlur(Context context, XElement parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
    // do nothing
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("TriggerFieldCell onBlur - mimicking = " + mimicking);
    }

    if (!mimicking) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("TriggerFieldCell onBlur no mimicking so calling super");
      }

      finishEditing(parent, value, context.getKey(), valueUpdater);
      super.onBlur(context, parent, value, event, valueUpdater);
    }
  }

  protected void onClick(Context context, XElement parent, NativeEvent event, T value, ValueUpdater<T> updater) {
    Element target = event.getEventTarget().cast();

    if (!isReadOnly() && (!isEditable() && getInputElement(parent).isOrHasChild(target))
        || getAppearance().triggerIsOrHasChild(parent.<XElement> cast(), target)) {
      onTriggerClick(context, parent, event, value, updater);
    }

    if (!editable) {
      event.preventDefault();
      event.stopPropagation();
    }
  }

  protected void onEnterKeyDown(Context context, Element parent, T value, NativeEvent event,
      ValueUpdater<T> valueUpdater) {
    Element target = event.getEventTarget().cast();
    if (!finishEditOnEnter) {
      event.preventDefault();
      event.stopPropagation();
      return;
    }
    if (getInputElement(parent).isOrHasChild(target)) {
      mimicking = false;
      getInputElement(parent).blur();
    } else {
      super.onEnterKeyDown(context, parent, value, event, valueUpdater);
    }
  }

  @Override
  protected void onFocus(Context context, XElement parent, T value, NativeEvent event, ValueUpdater<T> valueUpdater) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("TriggerFieldCell onFocus " + parent.getId());
    }

    if (mimicking) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("TriggerFieldCell onFocus mimic = true so exiting " + parent.getId());
      }

      return;
    }

    if (!mimicking) {
      // EXTGWT-2183 IE8 is not firing blur when switching focus to another
      // application then clicking another trigger cell to regain focus
      // me manually finish the edit. we can't call onBlur has the attempt the
      // blur the field throws an exception
      if ((GXT.isIE8() || GXT.isWebKit()) && focusedCell != null) {
        if (GXTLogConfiguration.loggingIsEnabled()) {
          logger.finest("TriggerFieldCell onFocus manually finishing edit as blur not fired");
        }

        finishEditing(parent, value, context.getKey(), valueUpdater);

        return;
      }

      super.onFocus(context, parent, value, event, valueUpdater);
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("onFocus setting mimicking to true " + parent.getId());
      }

      saveContext(context, parent, event, valueUpdater, value);
      mimicking = true;
      getAppearance().onFocus(parent, true);
      focusManagerRegistration = new MouseDownHandler(context, parent, value, valueUpdater);
    }
  }

  protected void onKeyDown(final Context context, final Element parent, final T value, NativeEvent event,
      final ValueUpdater<T> valueUpdater) {
    super.onKeyDown(context, parent, value, event, valueUpdater);

    int key = event.getKeyCode();

    // IE8 backspace causes navigation away from page when input is read only
    if (!isEditable() && key == KeyCodes.KEY_BACKSPACE) {
      event.preventDefault();
      event.stopPropagation();
    }

    if (monitorTab && event.getKeyCode() == KeyCodes.KEY_TAB) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("onKeyDown Tab " + parent.getId());
      }

      mimicking = false;
    }
  }

  @Override
  protected void onMouseDown(XElement parent, NativeEvent event) {
    super.onMouseDown(parent, event);

    Element target = event.getEventTarget().cast();
    if (!isReadOnly() && (!isEditable() && getInputElement(parent).isOrHasChild(target))
            || getAppearance().triggerIsOrHasChild(parent, target)) {
      getAppearance().onTriggerClick(parent, true);
      event.preventDefault();
    }
  }

  @Override
  protected void onMouseOut(XElement parent, NativeEvent event) {
    super.onMouseOut(parent, event);
    XElement target = event.getEventTarget().cast();
    if (getAppearance().triggerIsOrHasChild(parent.<XElement> cast(), target)) {
      getAppearance().onTriggerOver(parent.<XElement> cast(), false);
    }
  }

  @Override
  protected void onMouseOver(XElement parent, NativeEvent event) {
    super.onMouseOver(parent, event);
    XElement target = event.getEventTarget().cast();
    if (getAppearance().triggerIsOrHasChild(parent.<XElement> cast(), target)) {
      getAppearance().onTriggerOver(parent.<XElement> cast(), true);
    }
  }

  protected void onTriggerClick(Context context, XElement parent, NativeEvent event, T value, ValueUpdater<T> updater) {
    fireEvent(context, new TriggerClickEvent());
    getAppearance().onTriggerClick(parent, false);
  }

  protected void saveContext(Context context, Element parent, NativeEvent event, ValueUpdater<T> valueUpdater, T value) {
    super.saveContext(context, parent, event, valueUpdater, value);
    focusedCell = this;
  }

  protected void triggerBlur(Context context, final XElement parent, T value, ValueUpdater<T> valueUpdater) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("TriggerFieldCell triggerBlur " + parent.getId());
      logger.finest("TriggerFieldCell triggerBlur mimicking = false");
    }

    if (focusManagerRegistration != null) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("TriggerFieldCell triggerBlur remove event preview");
      }

      focusManagerRegistration.remove();
    }

    mimicking = false;

    finishEditing(parent, value, context.getKey(), valueUpdater);
  }

  protected boolean validateBlur(Element target) {
    return true;
  }

  void doTriggerBlur() {
    triggerBlur(lastContext, lastParent, lastValue, lastValueUpdater);
    onBlur(lastContext, lastParent, lastValue, null, lastValueUpdater);
  }

  protected native void clearFocusKey() /*-{
		this.@com.google.gwt.cell.client.AbstractInputCell::focusedKey = null;
  }-*/;

}
