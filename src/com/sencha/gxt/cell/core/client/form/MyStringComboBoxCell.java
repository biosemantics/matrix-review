package com.sencha.gxt.cell.core.client.form;

import java.util.LinkedList;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;

public class MyStringComboBoxCell extends StringComboBoxCell {

	public MyStringComboBoxCell(ListStore<String> store,
			LabelProvider<? super String> labelProvider) {
		super(store, labelProvider);
	}

	public MyStringComboBoxCell(ListStore<String> listStore,
			LabelProvider<String> labelProvider,
			TriggerFieldAppearance appearance) {
		super(listStore, labelProvider, appearance);
	}

	/**
	 * Is passed in the old, previously stored value not the current one
	 */
	@Override
	public void finishEditing(Element parent, String value, Object key, ValueUpdater<String> valueUpdater) {
		super.finishEditing(parent, value, key, valueUpdater);
		
		// Get current value rather than previous one.. CURRENTLY DOES NOT WORK
		value = getText(XElement.as(parent));
		
		String selection = getByValue(value);
		if (selection == null && !isForceSelection()) {
			selection = value;
			if (isAddUserValues) {
				if (userValues == null) {
					userValues = new LinkedList<String>();
				}
				getStore().add(selection);
				if (!userValues.contains(selection)) {
					userValues.add(selection);
				}
			}
		}
	}
	
	/**
	 * Some validation timer causes this to be called not only after user has finished to input
	 * But also during the time he's typing. This causes the userValues to be populated with more than the final input..
	 * Hence override StringComboBoxCell's behavior and only add on finishEditing
	 */	
	@Override
	protected String getByValue(String value) {
		int count = store.size();
		for (int i = 0; i < count; i++) {
			String item = store.get(i);
			String v = getRenderedValue(item);
			if (v != null && v.equals(value)) {
				return item;
			}
		}
		return null;
	}

}
