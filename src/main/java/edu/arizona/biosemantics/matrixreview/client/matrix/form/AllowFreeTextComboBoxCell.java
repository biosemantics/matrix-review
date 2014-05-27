package edu.arizona.biosemantics.matrixreview.client.matrix.form;

import java.text.ParseException;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.event.ParseErrorEvent;

public class AllowFreeTextComboBoxCell<T> extends ComboBoxCell<T> {

	public AllowFreeTextComboBoxCell(ListStore<T> store, LabelProvider<? super T> labelProvider) {
		super(store, labelProvider);
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

		// Fire the value updater if the value has changed.
		if (valueUpdater != null
				&& !vd.getCurrentValue().equals(vd.getLastValue())) {
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
					getInputElement(parent).setValue(
							value == null ? newValue : getPropertyEditor().render(
									value));
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
