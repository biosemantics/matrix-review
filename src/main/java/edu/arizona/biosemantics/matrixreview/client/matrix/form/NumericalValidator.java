package edu.arizona.biosemantics.matrixreview.client.matrix.form;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;

import edu.arizona.biosemantics.matrixreview.client.common.SetValueValidator.ValidationResult;

public class NumericalValidator implements Validator<String> {

	private edu.arizona.biosemantics.matrixreview.client.common.SetValueValidator.NumericalValidator validator = new 
			edu.arizona.biosemantics.matrixreview.client.common.SetValueValidator.NumericalValidator();
	
	@Override
	public List<EditorError> validate(Editor<String> editor, String value) {
		List<EditorError> result = new LinkedList<EditorError>();
		ValidationResult validationResult = validator.validate(value);
		if (!validationResult.isValid()) {
			result.add(new DefaultEditorError(editor, validationResult.getReason(), value));
		}
		return result;
	}

}
