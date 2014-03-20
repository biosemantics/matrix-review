/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.cell.core.client;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.sencha.gxt.cell.core.client.form.FieldCell;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.BaseEventPreview;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

public class SliderCell extends FieldCell<Integer> {

  public interface HorizontalSliderAppearance extends SliderAppearance {
  }

  public interface SliderAppearance extends FieldAppearance {

    int getClickedValue(Context context, Element parent, Point location);

    int getSliderLength(XElement parent);

    Element getThumb(Element parent);

    boolean isVertical();

    void onMouseDown(Context context, Element parent);

    void onMouseOut(Context context, Element parent);

    void onMouseOver(Context context, Element parent);

    void onMouseUp(Context context, Element parent);

    void render(double fractionalValue, int width, int height, SafeHtmlBuilder sb);

    void setThumbPosition(Element parent, int left);

  }

  public interface VerticalSliderAppearance extends SliderAppearance {
  }

  protected class DragPreview extends BaseEventPreview {

    private final Context context;
    private final Element parent;
    private int thumbWidth;
    private int thumbHeight;

    private final ValueUpdater<Integer> valueUpdater;

    public DragPreview(Context context, Element parent, ValueUpdater<Integer> valueUpdater, NativeEvent e) {
      super();
      this.context = context;
      this.parent = parent;
      this.valueUpdater = valueUpdater;

      XElement t = getAppearance().getThumb(parent).cast();
      thumbWidth = t.getOffsetWidth();
      thumbHeight = t.getOffsetHeight();

      positionTip(e);
    }

    @Override
    protected boolean onPreview(NativePreviewEvent event) {
      boolean allow = super.onPreview(event);

      switch (event.getTypeInt()) {
        case Event.ONMOUSEMOVE:
          positionTip(event.getNativeEvent());
          break;
        case Event.ONMOUSEUP:
          this.remove();
          XElement p = XElement.as(parent);
          int v = setValue(p, reverseValue(p, getAppearance().getClickedValue(context, p, new Point(event.getNativeEvent().getClientX(), event.getNativeEvent().getClientY()))));
          valueUpdater.update(v);
          getAppearance().onMouseUp(context, parent);
          getAppearance().onMouseOut(context, parent);
          tip.hide();
          break;
      }

      return allow;
    }

    private void positionTip(NativeEvent event) {
      if (!showMessage) {
        return;
      }

      XElement p = XElement.as(parent);
      int v = setValue(p, reverseValue(p, getAppearance().getClickedValue(context, p, new Point(event.getClientX(), event.getClientY()))));
      Element thumb = getAppearance().getThumb(parent);
      tip.setToolTipConfig(toolTipConfig);
      tip.setText(onFormatValue(v));

      if (!tip.isAttached()) {
        tip.onMouseMove(thumbWidth, thumbHeight, thumb);
      }
      tip.onMouseMove(thumbWidth, thumbHeight, thumb);
    }

  }

  private class ToolTipExt extends ToolTip {
    public ToolTipExt(ToolTipConfig config) {
      super(config);
    }

    public void setText(String text) {
      bodyHtml = text;
    }

    public void onMouseMove(int thumbWidth, int thumbHeight, Element target) {
      this.target = target;
      Side origAnchor = toolTipConfig.getAnchor();
      Point p = getTargetXY(0);
      p.setX(p.getX() - (thumbWidth / 2));
      p.setY(p.getY() - (thumbHeight / 2));
      super.showAt(p.getX(), p.getY());
      toolTipConfig.setAnchor(origAnchor);
    }
  }

  private String message = "{0}";
  private boolean showMessage = true;
  private boolean vertical = false;
  private int maxValue = 100;
  private int minValue = 0;
  private ToolTipExt tip;
  private ToolTipConfig toolTipConfig;
  private int increment = 10;

  public SliderCell() {
    this(GWT.<SliderAppearance>create(SliderAppearance.class));
  }

  public SliderCell(SliderAppearance appearance) {
    super(appearance, "mousedown", "mouseover", "mouseout", "keydown");

    vertical = appearance.isVertical();

    toolTipConfig = new ToolTipConfig();
    toolTipConfig.setAnchorArrow(false);
    toolTipConfig.setMaxWidth(25);
    toolTipConfig.setDismissDelay(1000);
    if (vertical) {
      toolTipConfig.setAnchor(Side.LEFT);
      toolTipConfig.setMouseOffsetX(25);
      toolTipConfig.setMouseOffsetY(0);
    } else {
      toolTipConfig.setAnchor(Side.TOP);
      toolTipConfig.setMouseOffsetX(0);
      toolTipConfig.setMouseOffsetY(25);
    }

    tip = new ToolTipExt(toolTipConfig);
  }

  @Override
  public SliderAppearance getAppearance() {
    return (SliderAppearance) super.getAppearance();
  }

  /**
   * Returns the increment.
   * 
   * @return the increment
   */
  public int getIncrement() {
    return increment;
  }

  /**
   * Returns the max value (defaults to 100).
   * 
   * @return the max value
   */
  public int getMaxValue() {
    return maxValue;
  }

  /**
   * Returns the tool tip message.
   * 
   * @return the tool tip message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Returns the minimum value (defaults to 0).
   * 
   * @return the minimum value
   */
  public int getMinValue() {
    return minValue;
  }

  /**
   * Returns true if the tool tip message is shown
   * 
   * @return the showMessage state
   */
  public boolean isShowMessage() {
    return showMessage;
  }

  @Override
  public void onBrowserEvent(Context context, Element parent, Integer value, NativeEvent event,
      ValueUpdater<Integer> valueUpdater) {
    Element target = event.getEventTarget().cast();
    if (!parent.isOrHasChild(target) || isDisabled()) {
      return;
    }
    super.onBrowserEvent(context, parent, value, event, valueUpdater);

    String eventType = event.getType();

    if ("mousedown".equals(eventType)) {
      onMouseDown(context, parent, event, valueUpdater);
    } else if ("mouseover".equals(eventType)) {
      getAppearance().onMouseOver(context, parent);
    } else if ("mouseout".equals(eventType)) {
      getAppearance().onMouseOut(context, parent);
    } else if ("keydown".equals(eventType)) {
      int key = event.getKeyCode();
      if (!vertical) {
        switch (key) {
          case KeyCodes.KEY_LEFT:
            int v = setValue(parent, value - increment);
            valueUpdater.update(v);
            positionTip(parent, v);
            break;
          case KeyCodes.KEY_RIGHT:
            v = setValue(parent, value + increment);
            valueUpdater.update(v);
            positionTip(parent, v);
            break;
        }
      } else {
        switch (key) {
          case KeyCodes.KEY_DOWN:
            int v = setValue(parent, value - increment);
            valueUpdater.update(v);
            positionTip(parent, v);
            break;
          case KeyCodes.KEY_UP:
            v = setValue(parent, value + increment);
            valueUpdater.update(v);
            positionTip(parent, v);
            break;
        }
      }
    }
  }

  private void positionTip(Element parent, int v) {
    if (!showMessage) {
      return;
    }
    Element thumb = getAppearance().getThumb(parent);

    XElement t = thumb.cast();
    int thumbWidth = t.getOffsetWidth();
    int thumbHeight = t.getOffsetHeight();

    toolTipConfig.setAutoHide(true);
    tip.setToolTipConfig(toolTipConfig);
    tip.setText(onFormatValue(v));

    if (!tip.isAttached()) {
      tip.onMouseMove(thumbWidth, thumbHeight, thumb);
    }
    tip.onMouseMove(thumbWidth, thumbHeight, thumb);
  }

  @Override
  public void onEmpty(XElement parent, boolean empty) {
    getAppearance().onEmpty(parent, empty);
  }

  @Override
  public boolean redrawOnResize() {
    return true;
  }

  @Override
  public void render(Context context, Integer value, SafeHtmlBuilder sb) {
    double fractionalValue;
    if (value == null) {
      fractionalValue = 0.5;
    } else {
      fractionalValue = 1.0 * (value - minValue) / (maxValue - minValue);
    }
    getAppearance().render(fractionalValue, getWidth(), getHeight(), sb);
  }

  /**
   * How many units to change the slider when adjusting by drag and drop. Use this option to enable 'snapping' (default
   * to 10).
   * 
   * @param increment the increment
   */
  public void setIncrement(int increment) {
    this.increment = increment;
  }

  /**
   * Sets the max value (defaults to 100).
   * 
   * @param maxValue the max value
   */
  public void setMaxValue(int maxValue) {
    this.maxValue = maxValue;
  }

  /**
   * Sets the tool tip message (defaults to '{0}'). "{0} will be substituted with the current slider value.
   * 
   * @param message the tool tip message
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Sets the minimum value (defaults to 0).
   * 
   * @param minValue the minimum value
   */
  public void setMinValue(int minValue) {
    this.minValue = minValue;
  }

  /**
   * Sets if the tool tip message should be displayed (defaults to true, pre-render).
   * 
   * @param showMessage true to show tool tip message
   */
  public void setShowMessage(boolean showMessage) {
    this.showMessage = showMessage;
  }

  /**
   * Set the tooltip config. This is the tooltip for the message configuration. Set {@link #setShowMessage(boolean)} to
   * true to use this feature.
   * <p/>
   * {@link ToolTipConfig#setAnchor(com.sencha.gxt.core.client.Style.Side)} is a required setting for toolTipConfig.
   * 
   * @param toolTipConfig is the tooltip configuration.
   */
  public void setToolTipConfig(ToolTipConfig toolTipConfig) {
    assert toolTipConfig != null : "The toolTipConfig parameter is null and it is required.";
    assert toolTipConfig.getAnchor() != null : "The toolTipConfig must have an anchor Side set. "
        + "Like toolTipConfig.setAnchor(Side.RIGHT);";
    this.toolTipConfig = toolTipConfig;
  }

  protected int constrain(int value) {
    return Util.constrain(value, minValue, maxValue);
  }

  protected int doSnap(int v) {
    if (increment == 1) {
      return v;
    }
    int m = v % increment;
    if (m != 0) {
      v -= m;
      if (m * 2 > increment) {
        v += increment;
      } else if (m * 2 < -increment) {
        v -= increment;
      }
    }
    return v;
  }

  protected double getRatio(XElement parent) {
    int v = maxValue - minValue;
    int length = getAppearance().getSliderLength(parent);
    return v == 0 ? length : ((double) length / v);
  }

  protected int normalizeValue(int value) {
    value = doSnap(value);
    value = constrain(value);
    return value;
  }

  protected String onFormatValue(int value) {
    return Format.substitute(getMessage(), value);
  }

  protected void onMouseDown(final Context context, final Element parent, NativeEvent event,
      final ValueUpdater<Integer> valueUpdater) {
    Element target = Element.as(event.getEventTarget());
    if (!getAppearance().getThumb(parent).isOrHasChild(target)) {
      int value = getAppearance().getClickedValue(context, parent, new Point(event.getClientX(), event.getClientY()));
      value = reverseValue(parent.<XElement> cast(), value);
      value = normalizeValue(value);
      valueUpdater.update(value);
      return;
    }

    BaseEventPreview preview = new DragPreview(context, parent, valueUpdater, event);
    getAppearance().onMouseDown(context, parent);
    preview.add();
  }

  protected int reverseValue(XElement parent, int pos) {
    double ratio = getRatio(parent);
    if (vertical) {
      int length = getAppearance().getSliderLength(parent);
      return (int) (((minValue * ratio) + length - pos) / ratio);
    } else {
      int halfThumb = getAppearance().getThumb(parent).getOffsetWidth() / 2;
      return (int) ((pos + halfThumb + (minValue * ratio)) / ratio);
    }
  }

  protected int translateValue(XElement parent, int v) {
    int halfThumb;
    if (vertical) {
      halfThumb = getAppearance().getThumb(parent).getOffsetHeight() / 2;
    } else {
      halfThumb = getAppearance().getThumb(parent).getOffsetWidth() / 2;
    }

    double ratio = getRatio(parent);

    return (int) ((v * ratio) - (minValue * ratio) - halfThumb);
  }

  private int setValue(Element parent, int value) {
    value = normalizeValue(value);

    // move thumb
    int pos = translateValue(parent.<XElement> cast(), value);
    getAppearance().setThumbPosition(parent, pos);

    return value;
  }

}
