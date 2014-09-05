package edu.arizona.biosemantics.matrixreview.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface ValueProperties extends PropertyAccess<Value> {
	   	
	  ValueProvider<Value, String> value();


}
