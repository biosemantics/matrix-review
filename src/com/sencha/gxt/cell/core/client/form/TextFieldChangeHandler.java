package com.sencha.gxt.cell.core.client.form;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

public class TextFieldChangeHandler implements ValueChangeHandler<String>, BeforeShowHandler {

	private String backupValue;
	private MyValidator validator;
	
	public TextFieldChangeHandler(MyValidator validator) {
		this.validator = validator;
	}
	
	@Override
	public void onBeforeShow(BeforeShowEvent event) {
		TextField textField = (TextField)event.getSource();
		backupValue = textField.getCurrentValue();
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String newValue = event.getValue();
		TextField textField = (TextField)event.getSource();
		if(!validator.isValid(newValue)) {
			textField.setText(backupValue);
		}
	}

}
