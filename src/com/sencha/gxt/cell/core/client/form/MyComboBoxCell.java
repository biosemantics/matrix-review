package com.sencha.gxt.cell.core.client.form;

import java.text.ParseException;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.text.shared.SafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.form.FieldCell.FieldViewData;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.MyListStore;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;

public class MyComboBoxCell<T> extends ComboBoxCell<T> {

	/*
	private class MyComboPropertyEditor extends PropertyEditor<T> {

		@Override
		public T parse(CharSequence text) throws ParseException {
			return getByValue(text == null ? "" : text.toString());
		}

		@Override
		public String render(T object) {
			if (object == null) {
				return "";
			}
			LabelProvider<? super T> provider = getLabelProvider();
			return provider == null ? object.toString() : provider
					.getLabel(object);
		}

	}*/

	public MyComboBoxCell(MyListStore<T> store,
			LabelProvider<? super T> labelProvider) {
		this(store, labelProvider, GWT
				.<TriggerFieldAppearance> create(TriggerFieldAppearance.class));
	}

	public MyComboBoxCell(MyListStore<T> store,
			LabelProvider<? super T> labelProvider, ListView<T, ?> view) {
		this(store, labelProvider, view, GWT
				.<TriggerFieldAppearance> create(TriggerFieldAppearance.class));
	}

	public MyComboBoxCell(MyListStore<T> store,
			LabelProvider<? super T> labelProvider, ListView<T, ?> view,
			TriggerFieldAppearance appearance) {
		super(store, labelProvider, view, appearance);
		//setPropertyEditor(new MyComboPropertyEditor());
	}

	public MyComboBoxCell(MyListStore<T> store,
			LabelProvider<? super T> labelProvider,
			final SafeHtmlRenderer<T> renderer) {
		this(store, labelProvider, renderer, GWT
				.<TriggerFieldAppearance> create(TriggerFieldAppearance.class));
	}

	public MyComboBoxCell(MyListStore<T> store,
			LabelProvider<? super T> labelProvider,
			final SafeHtmlRenderer<T> renderer,
			TriggerFieldAppearance appearance) {
		super(store, labelProvider, renderer, appearance);
		//setPropertyEditor(new MyComboPropertyEditor());
	}

	public MyComboBoxCell(MyListStore<T> store,
			LabelProvider<? super T> labelProvider,
			TriggerFieldAppearance appearance) {
		super(store, labelProvider, appearance);
		//setPropertyEditor(new MyComboPropertyEditor());
	}
	
	@Override
	protected T getByValue(String value) {
		int count = ((MyListStore<T>)store).sizeOfAllItems();
		for (int i = 0; i < count; i++) {
			T item = ((MyListStore<T>)store).getFromAllItems(i);
			String v = getRenderedValue(item);
			if (v != null && v.equals(value)) {
				return item;
			}
		}
		return null;
	}
	
	@Override
	public void finishEditing(final Element parent, T value, Object key,
			ValueUpdater<T> valueUpdater) {
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
			vd = new FieldViewData(value == null ? "" : getPropertyEditor()
					.render(value));
			setViewData(key, vd);
		}
		vd.setCurrentValue(newValue);
		
		String lastValueBackup = vd.getLastValue();
		// Fire the value updater if the value has changed.
		if (valueUpdater != null && !vd.getCurrentValue().equals(vd.getLastValue())) {
			vd.setLastValue(newValue);
			try {
				if (GXTLogConfiguration.loggingIsEnabled()) {
					logger.finest("finishEditing saving value: " + newValue);
				}

				/*if ("".equals(newValue)) {
					value = null;

					valueUpdater.update(null);
				} else { */
					value = getPropertyEditor().parse(newValue);
					if(value == null)
						value = getByValue(lastValueBackup);

					valueUpdater.update(value);

					// parsing may have changed value
					getInputElement(parent).setValue(
							value == null ? "" : getPropertyEditor().render(
									value));
				//}

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
				logger.finest("finishEditing value not changed: " + newValue
						+ " old: " + vd.getLastValue());
			}

		}

		clearViewData(key);
		clearFocusKey();
		focusedCell = null;

		// not calling super as GWT code does input.blur() which breaks
		// navigation between fields
	}

}
