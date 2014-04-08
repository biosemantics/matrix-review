package com.sencha.gxt.widget.core.client.form;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.cell.core.client.form.MyStringComboBoxCell;
import com.sencha.gxt.cell.core.client.form.StringComboBoxCell;
import com.sencha.gxt.cell.core.client.form.TriggerFieldCell.TriggerFieldAppearance;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;

public class MyStringComboBox extends SimpleComboBox<String> {

	/**
	 * Creates an empty combo box in preparation for values to be added to the
	 * selection list using {@link #add}.
	 */
	public MyStringComboBox() {
		this(GWT.<TriggerFieldAppearance> create(TriggerFieldAppearance.class));
	}

	/**
	 * Creates an empty combo box with a given appearance - values can be added
	 * to the selection list with {@link #add}.
	 * 
	 * @param appearance
	 *            the appearance to use when drawing the combo box
	 */
	public MyStringComboBox(TriggerFieldAppearance appearance) {
		super(new MyStringComboBoxCell(new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(String item) {
						return item;
					}
				}), new LabelProvider<String>() {
			@Override
			public String getLabel(String item) {
				return item;
			}
		}, appearance));
	}

	/**
	 * Creates a combo box containing the specified values.
	 * 
	 * @param values
	 *            values to include in the combo box
	 */
	public MyStringComboBox(List<String> values) {
		this();
		add(values);
	}

	/**
	 * Clears the list of values typed by the user.
	 */
	public void clearUserValues() {
		getCell().clearUserValues();
	}

	@Override
	public StringComboBoxCell getCell() {
		return (StringComboBoxCell) super.getCell();
	}

	/**
	 * Returns a list containing values typed by the user in the combo box text
	 * field. These values are saved when the focus leaves the combo box if the
	 * <i>force selection</i> property is false and the <i>add user values</i>
	 * property is true.
	 * 
	 * @return a list of values typed by the user
	 */
	public List<String> getUserValues() {
		return getCell().getUserValues();
	}

	/**
	 * Returns true to indicate that arbitrary values typed by the user will be
	 * added to the drop down list.
	 * 
	 * @return true to indicate values typed by user will be saved in drop down
	 *         list
	 */
	public boolean isAddUserValues() {
		return getCell().isAddUserValues();
	}

	/**
	 * Set to true to add arbitrary values typed by the user to the drop down
	 * list; be sure to also set <i>force selection</i> to false.
	 * <p/>
	 * This value is false by default (i.e. user values are not added to the
	 * drop down list).
	 * 
	 * @param isAddUserValues
	 *            true to add arbitrary values typed by the user to the drop
	 *            down list
	 */
	public void setAddUserValues(boolean isAddUserValues) {
		getCell().setAddUserValues(isAddUserValues);
	}

}
