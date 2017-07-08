package edu.arizona.biosemantics.matrixreview.shared.model.core;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface CharacterProperties extends PropertyAccess<Character> {

	//@Path("complete")
	public class KeyProvider implements ModelKeyProvider<Character> {
		@Override
		public String getKey(Character item) {
			return item.toString();
		}
	}

	@Path("name")
	LabelProvider<Character> nameLabel();
	
	//ValueProvider<Character, String> organ(); Hong 618
	ValueProvider<Character, Organ> organ();
	
	ValueProvider<Character, String> name();
	
	ValueProvider<Character, String> complete();

}
