package edu.arizona.biosemantics.matrixreview.old.control;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fires after a widget is activated.
 */
public class SaveCategoriesEvent extends GwtEvent<SaveCategoriesHandler> {

	private static Type<SaveCategoriesHandler> TYPE = new Type<SaveCategoriesHandler>();

	private List<String> states;

	public SaveCategoriesEvent(List<String> states) {
		this.states = states;
	}

	public List<String> getStates() {
		return states;
	}

	public static Type<SaveCategoriesHandler> getType() {
		return TYPE;
	}

	@Override
	public Type<SaveCategoriesHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SaveCategoriesHandler handler) {
		handler.onSaveCategories(this);
	}

}
