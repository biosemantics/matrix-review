package edu.arizona.biosemantics.matrixreview.client.common;

public interface Validator {

	public class ValidationResult {
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
	
	public ValidationResult validate(String value);
	
}
