package edu.arizona.biosemantics.matrixreview.client.compare;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;

/**
 * Holds either a Character or an Organ. Used as the data type for a TreeStore of characters
 * to allow both characters and organs to exist in the same store. 
 * 
 * @author Andrew Stockton
 */

public class CharacterTreeNode implements CellIdentifier.CellIdentifierObject{
	private Object data;
	
	public CharacterTreeNode(Object data){
		this.data = data;
	}
	
	public Object getData(){
		return data;
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof CharacterTreeNode)
			return ((CharacterTreeNode)o).getData().equals(data);
		return false;
	}

	@Override
	public boolean matches(Object other) {
		return this.equals(other);
	}
}
class CharacterTreeNodeProperties implements PropertyAccess<CharacterTreeNode>{
	public ModelKeyProvider<CharacterTreeNode> key() {
		return new ModelKeyProvider<CharacterTreeNode>(){
			@Override
			public String getKey(CharacterTreeNode item) {
				Object data = item.getData();
				if (data instanceof Character)
					return ((Character)data).getId();
				return data.toString();
			}
		};
	}

	public LabelProvider<CharacterTreeNode> nameLabel() {
		return new LabelProvider<CharacterTreeNode>(){
			@Override
			public String getLabel(CharacterTreeNode item) {
				return null;
			}
		};
	}

	public ValueProvider<CharacterTreeNode, String> name() {
		return new ValueProvider<CharacterTreeNode, String>(){
			@Override
			public String getValue(CharacterTreeNode item) {
				Object data = item.getData();
				if (data instanceof Character)
					return ((Character)data).toString(); //TODO: should this provide the ORIGINAL character name, before name changes? 
				return item.getData().toString();
			}
			public void setValue(CharacterTreeNode object, String value) {}
			public String getPath() {return null;}
			
		};
	}
}
