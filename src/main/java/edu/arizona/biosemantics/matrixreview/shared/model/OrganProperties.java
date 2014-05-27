package edu.arizona.biosemantics.matrixreview.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface OrganProperties extends PropertyAccess<Organ> {

	  @Path("name")
	  ModelKeyProvider<Organ> key();
	   
	  @Path("name")
	  LabelProvider<Organ> nameLabel();
	 
	  ValueProvider<Organ, String> name();

}
