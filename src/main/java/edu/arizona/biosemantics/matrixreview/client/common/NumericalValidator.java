package edu.arizona.biosemantics.matrixreview.client.common;

import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;

public class NumericalValidator implements Validator {

	@Override
	public ValidationResult validate(String value) {
		if (value == null || !value.matches("[0-9]*")) {
			return new ValidationResult(false, "Value not numeric");
		}
		return new ValidationResult(true, "");
	}


}
