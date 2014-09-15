package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.Set;

public class CategoricalValidator implements Validator {
	
	private Set<String> states;

	public CategoricalValidator(Set<String> states) {
		this.states = states;
	}

	@Override
	public ValidationResult validate(String value) {
		if (!states.contains(value)) {
			return new ValidationResult(false, "Value entered not part of the character's vocabulary");
		}
		return new ValidationResult(true, "");
	}


}
