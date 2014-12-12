package edu.arizona.biosemantics.matrixreview.shared.model.core;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface TaxonProperties extends PropertyAccess<Taxon> {

	  @Path("id")
	  ModelKeyProvider<Taxon> key();
	   
	  @Path("id")
	  LabelProvider<Taxon> nameLabel();
	 
	  ValueProvider<Taxon, String> description();
	
	  ValueProvider<Taxon, String> fullName();
}