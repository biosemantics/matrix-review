package edu.arizona.biosemantics.matrixreview.shared.model;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface ValueProperties extends PropertyAccess<Value> {
	   	
	  ValueProvider<Value, String> value();

}
