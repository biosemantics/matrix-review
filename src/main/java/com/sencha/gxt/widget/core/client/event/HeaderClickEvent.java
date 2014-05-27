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
import com.sencha.gxt.widget.core.client.event.HeaderClickEvent.HeaderClickHandler;

public final class HeaderClickEvent extends GridEvent<HeaderClickHandler> {

  public interface HasHeaderClickHandlers extends HasHandlers {
    HandlerRegistration addHeaderClickHandler(HeaderClickHandler handler);
  }

  public interface HeaderClickHandler extends EventHandler {
    void onHeaderClick(HeaderClickEvent event);
  }

  private static GwtEvent.Type<HeaderClickHandler> TYPE;

  public static GwtEvent.Type<HeaderClickHandler> getType() {
    if (TYPE == null) {
      TYPE = new GwtEvent.Type<HeaderClickHandler>();
    }
    return TYPE;
  }

  private int columnIndex;
  private Event event;

  public HeaderClickEvent(int columnIndex, Event event) {
    this.columnIndex = columnIndex;
    this.event = event;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public GwtEvent.Type<HeaderClickHandler> getAssociatedType() {
    return (GwtEvent.Type) TYPE;
  }
  
  public int getColumnIndex() {
    return columnIndex;
  }

  public Event getEvent() {
    return event;
  }

  @Override
  protected void dispatch(HeaderClickHandler handler) {
    handler.onHeaderClick(this);
  }
}