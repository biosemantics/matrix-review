package com.sencha.gxt.widget.core.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.BeforeFilterEvent.BeforeFilterHandler;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;

public class BeforeFilterEvent<Filter> extends GwtEvent<BeforeFilterHandler<Filter>> {

	private static Type<BeforeFilterHandler<?>> TYPE;

	public static Type<BeforeFilterHandler<?>> getType() {
		return TYPE != null ? TYPE : (TYPE = new Type<BeforeFilterHandler<?>>());
	}

	private Filter item;

	public BeforeFilterEvent(Filter item) {
		this.item = item;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Type<BeforeFilterHandler<Filter>> getAssociatedType() {
		return (Type) TYPE;
	}

	public Filter getItem() {
		return item;
	}

	@Override
	public Component getSource() {
		return (Component) super.getSource();
	}

	@Override
	protected void dispatch(BeforeFilterHandler<Filter> handler) {
		handler.onBeforeFilter(this);

	}

	public interface BeforeFilterHandler<Filter> extends EventHandler {

		void onBeforeFilter(BeforeFilterEvent<Filter> event);
	}


	public interface HasBeforeFilterHandlers<Filter> {

		public HandlerRegistration addBeforeFilterHandler(BeforeFilterHandler<Filter> handler);

	}

}
