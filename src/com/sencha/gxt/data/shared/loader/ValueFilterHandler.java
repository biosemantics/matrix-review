package com.sencha.gxt.data.shared.loader;

import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class ValueFilterHandler extends FilterHandler<Value> {

	@Override
	public Value convertToObject(String value) {
		//How to do this? Cell rendering gets passed in from valueprovider. If not Value instance passed in, coverage can not be display in same cell
		//because that information would not be easily accessible. Therefore ValueProvider has to provide Value, which in turn requires to create own
		//filter that works StringFilter-like on Value cells. The filter then requires this filterHandler.
		
		//according to references in code this is never called anyway
		return null;
	}

	@Override
	public String convertToString(Value object) {
		return object.getValue();
	}

}
