/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.CollapseEvent.CollapseHandler;

/**
 * Fires after the source is collapsed.
 */
public class CollapseEvent extends GwtEvent<CollapseHandler> {

  /**
   * Handler class for {@link CollapseEvent} events.
   */
  public interface CollapseHandler extends EventHandler {

    /**
     * Called after a panel is collapsed.
     */
    void onCollapse(CollapseEvent event);
  }
  
  /**
   * A widget that implements this interface is a public source of
   * {@link CollapseEvent} events.
   */
  public interface HasCollapseHandlers {

    /**
     * Adds a {@link CollapseHandler} handler for {@link CollapseEvent}
     * events.
     * 
     * @param handler the handler
     * @return the registration for the event
     */
    HandlerRegistration addCollapseHandler(CollapseHandler handler);
  }
  
  /**
   * Handler type.
   */
  private static Type<CollapseHandler> TYPE;

  /**
   * Gets the type associated with this event.
   * 
   * @return returns the handler type
   */
  public static Type<CollapseHandler> getType() {
    return TYPE != null ? TYPE : (TYPE = new Type<CollapseHandler>());
  }
  
  private Context context;
  
  public CollapseEvent() {
    
  }
  
  public CollapseEvent(Context context) {
    this.context = context;
  }
  
  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Type<CollapseHandler> getAssociatedType() {
    return (Type) TYPE;
  }

  /**
   * Returns the cell context.
   * 
   * @return the cell context or null if event not cell based
   */
  public Context getContext() {
    return context;
  }

  @Override
  public Component getSource() {
    return (Component) super.getSource();
  }

  @Override
  protected void dispatch(CollapseHandler handler) {
    handler.onCollapse(this);
  }

}
