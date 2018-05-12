package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.HashSet;
import java.util.Set;

import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class SetValueValidator {
	
	public static class ValidationResult {
		private boolean valid;
		private String reason;
				
		public ValidationResult(boolean valid, String reason) {
			super();
			this.valid = valid;
			this.reason = reason;
		}
		public boolean isValid() {
			return valid;
		}
		public String getReason() {
			return reason;
		}
	}
	
	public static interface Validator {
		
		public ValidationResult validate(String value);
		
	}
	
	public static class CategoricalValidator implements Validator {
		
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
	
	public static class NumericalValidator implements Validator {

		@Override
		public ValidationResult validate(String value) {
			if (value == null || !value.matches("[0-9]*")) {
				return new ValidationResult(false, "Value not numeric");
			}
			return new ValidationResult(true, "");
		}


	}
	
	private Model model;

	public SetValueValidator(Model model) {
		super();
		this.model = model;
	}

	public ValidationResult validValue(String value, Character character) {
		switch(model.getControlMode(character)) {
		case CATEGORICAL:
			Set<String> states = new HashSet<String>(model.getStates(character));
			Validator validator = new CategoricalValidator(states);
			ValidationResult result = validator.validate(value);
			return result;
		case NUMERICAL:
			validator = new NumericalValidator();
			result = validator.validate(value);
			return result;
		case OFF:
			break;
		default:
			break;
		}
		return new ValidationResult(true, "");
	}

}
