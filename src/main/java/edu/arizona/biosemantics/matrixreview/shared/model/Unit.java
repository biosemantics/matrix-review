package edu.arizona.biosemantics.matrixreview.shared.model;

public enum Unit {
		nm("0.00001"), 
		um("0.001"), 
		mm("1.0"), 
		cm("10.0"), 
		dm("100.0"),
		m("1000.0"), 
		km("1000000.0"), 
		in("25.4"), 
		ft("304.8"), 
		yd("914.4");
		
		private String toMMFactor;

		private Unit(String toMMFactor) {
			this.toMMFactor = toMMFactor;
		}
		
		public String getToMMFactor() {
			return toMMFactor;
		}
		
	}