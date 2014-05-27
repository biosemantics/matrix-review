package edu.arizona.biosemantics.matrixreview.client.matrix.editing;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;

public class TextFieldChangeHandler implements ValueChangeHandler<String>, BeforeShowHandler {

	private String backupValue;
	private Validator<String> validator;
	
	public TextFieldChangeHandler(Validator<String> validator) {
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
		if(!validator.validate(null, newValue).isEmpty()) {
			textField.setText(backupValue);
		}
	}

}
