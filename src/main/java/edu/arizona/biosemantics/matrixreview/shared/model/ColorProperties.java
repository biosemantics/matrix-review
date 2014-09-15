package edu.arizona.biosemantics.matrixreview.shared.model;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public interface ColorProperties extends PropertyAccess<Color> {

	@Path("id")
	ModelKeyProvider<Color> key();
	
	@Path("hex")
	abstract LabelProvider<Character> hex();
	
	@Path("use")
	abstract LabelProvider<Character> use();
}