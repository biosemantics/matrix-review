/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.widget.core.client.event.HeaderMouseDownEvent.HeaderMouseDownHandler;

public final class HeaderMouseDownEvent extends GridEvent<HeaderMouseDownHandler> {

  public interface HasHeaderMouseDownHandlers extends HasHandlers {
    HandlerRegistration addHeaderMouseDownHandler(HeaderMouseDownHandler handler);
  }

  public interface HeaderMouseDownHandler extends EventHandler {
    void onHeaderMouseDown(HeaderMouseDownEvent event);
  }

  private static GwtEvent.Type<HeaderMouseDownHandler> TYPE;

  public static GwtEvent.Type<HeaderMouseDownHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<HeaderMouseDownHandler>();
    }
    return TYPE;
  }

  private int columnIndex;
  private Event event;

  public HeaderMouseDownEvent(int columnIndex, Event event) {
    this.columnIndex = columnIndex;
    this.event = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<HeaderMouseDownHandler> getAssociatedType() {
    return (GwtEvent.Type) TYPE;
  }
  
  public int getColumnIndex() {
    return columnIndex;
  }

  public Event getEvent() {
    return event;
  }

  @Override
  protected void dispatch(HeaderMouseDownHandler handler) {
    handler.onHeaderMouseDown(this);
  }
}