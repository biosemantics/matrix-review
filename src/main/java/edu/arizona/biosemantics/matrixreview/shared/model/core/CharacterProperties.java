package edu.arizona.biosemantics.matrixreview.shared.model.core;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public abstract class CharacterProperties implements PropertyAccess<Character> {

	@Path("name")
	ModelKeyProvider<Character> key() {
		return new ModelKeyProvider<Character>() {
			@Override
			public String getKey(Character item) {
				return item.toString();
			}
		};
	}

	@Path("name")
	abstract LabelProvider<Character> nameLabel();

	abstract ValueProvider<Character, String> name();

}
