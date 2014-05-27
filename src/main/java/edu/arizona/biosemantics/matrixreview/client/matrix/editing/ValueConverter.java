package edu.arizona.biosemantics.matrixreview.client.matrix.editing;

import com.sencha.gxt.data.shared.Converter;

import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class ValueConverter implements Converter<Value, String> {
	@Override
	public Value convertFieldValue(String object) {
		return new Value(object);
	}

	@Override
	public String convertModelValue(Value object) {
		return object.getValue();
	}
}