package com.sencha.gxt.core.client.dom;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.sencha.gxt.core.client.util.BaseEventPreview;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.client.util.Rectangle;

/**
 * Automatically scrolls an element when the mouse is near the left or right of the element.
 * 
 * <p /> Use {@link #start()} and {@link #stop()} enable and disable.
 */
public class MyHorizontalAutoScrollSupport {

  private boolean autoScroll = true;
  private int scrollDelay = 400;
  private int scrollRepeatDelay = 100;
  private Rectangle leftBounds, rightBounds;
  private int scrollRegionHeight = 50;
  private XElement scrollElement;
  private boolean active;
  private int scrollZone = 250;

  private DelayedTask scrollLeftTask = new DelayedTask() {

    @Override
    public void onExecute() {
      onScrollLeft();
    }
  };

  private DelayedTask scrollRightTask = new DelayedTask() {

    @Override
    public void onExecute() {
      onScrollRight();
    }
  };

  private BaseEventPreview preview = new BaseEventPreview() {
    protected boolean onPreview(NativePreviewEvent pe) {
      super.onPreview(pe);
      if (pe.getTypeInt() == Event.ONMOUSEMOVE) {
        onMove(pe.getNativeEvent());
      }
      return true;

    };

  };

  /**
   * Creates a new scroll support instance. The scroll element must be set, see
   * {@link #setScrollElement(XElement)}.
   */
  public MyHorizontalAutoScrollSupport() {
    preview.setAutoHide(false);
  }

  /**
   * Creates a new scroll support instance.
   * 
   * @param scrollElement the scroll element
   */
  public MyHorizontalAutoScrollSupport(XElement scrollElement) {
    this();
    setScrollElement(scrollElement);
  }

  /**
   * Returns the scroll delay.
   * 
   * @return the scroll delay in milliseconds
   */
  public int getScrollDelay() {
    return scrollDelay;
  }

  /**
   * Returns the scroll element.
   * 
   * @return the scroll element
   */
  public XElement getScrollElement() {
    return scrollElement;
  }

  /**
   * Returns the scroll region height.
   * 
   * @return the scroll region height
   */
  public int getScrollRegionHeight() {
    return scrollRegionHeight;
  }

  /**
   * Returns the scroll repeat delay.
   * 
   * @return the scroll repeat delay
   */
  public int getScrollRepeatDelay() {
    return scrollRepeatDelay;
  }

  /**
   * Returns true if auto scroll is enabled.
   * 
   * @return true if auto scroll is enabled, otherwise false
   */
  public boolean isAutoScroll() {
    return autoScroll;
  }

  /**
   * True to enable auto scroll (defaults to true).
   * 
   * @param autoScroll true if auto scroll is enabled
   */
  public void setAutoScroll(boolean autoScroll) {
    this.autoScroll = autoScroll;
  }

  /**
   * Sets the amount of time before auto scroll is activated (defaults to 400).
   * 
   * @param scrollDelay the scroll delay in milliseconds
   */
  public void setScrollDelay(int scrollDelay) {
    this.scrollDelay = scrollDelay;
  }

  /**
   * Sets the scroll element.
   * 
   * @param scrollElement the scroll element
   */
  public void setScrollElement(XElement scrollElement) {
    assert scrollElement != null;
    this.scrollElement = scrollElement;
  }

  /**
   * Sets the height of the scroll region (defaults to 25).
   * 
   * @param scrollRegionHeight the scroll region in pixels
   */
  public void setScrollRegionHeight(int scrollRegionHeight) {
    this.scrollRegionHeight = scrollRegionHeight;
  }

  /**
   * Sets the amount of time between scroll changes after auto scrolling is
   * activated (defaults to 300).
   * 
   * @param scrollRepeatDelay the repeat delay in milliseconds
   */
  public void setScrollRepeatDelay(int scrollRepeatDelay) {
    this.scrollRepeatDelay = scrollRepeatDelay;
  }

  /**
   * Starts monitoring for auto scroll.
   */
  public void start() {
    if (!active) {
      active = true;
      onStart();
    }
  }

  /**
   * Stops monitoring for auto scroll.
   */
  public void stop() {
    active = false;
    preview.remove();
    scrollRightTask.cancel();
    scrollLeftTask.cancel();
  }

  protected void onMove(NativeEvent event) {
    Point p = new Point(event.getClientX(), event.getClientY());
    
    /*
    System.out.println(p.toString());
    System.out.println(rightBounds.toString());
    */
    
    if (leftBounds.contains(p)) {
      scrollLeftTask.delay(scrollDelay);
      scrollRightTask.cancel();
    } else if (rightBounds.contains(p)) {
      scrollRightTask.delay(scrollDelay);
      scrollLeftTask.cancel();
    } else {
      scrollLeftTask.cancel();
      scrollRightTask.cancel();
    }
  }

  protected void onScrollRight() {
	/*
	System.out.println("scroll right");
	System.out.println(scrollElement.getScrollWidth());
	System.out.println(scrollElement.getScrollLeft());
    */
    scrollElement.setScrollLeft(scrollElement.getScrollLeft() + scrollRegionHeight);
    scrollRightTask.delay(scrollRepeatDelay);
  }

  protected void onScrollLeft() {
    scrollElement.setScrollLeft(Math.max(0, scrollElement.getScrollLeft() - scrollRegionHeight));
    scrollLeftTask.delay(scrollRepeatDelay);
  }

  protected void onStart() {
    if (!autoScroll) return;

    /*
    System.out.println(scrollElement.getBounds());
    System.out.println(scrollElement.getBounds(true));
    System.out.println(scrollElement.getBounds(true, true));
    System.out.println(scrollElement.getBounds(false, true));
    */
    
    leftBounds = scrollElement.getBounds();
    leftBounds.setWidth(scrollZone);

    rightBounds = scrollElement.getBounds();
    rightBounds.setX(rightBounds.getX() + rightBounds.getWidth() - scrollZone);
    rightBounds.setWidth(scrollZone);

    //return;
    preview.add();
    
  }
}
