package edu.arizona.biosemantics.matrixreview.client;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public class CharacterTreeNode {
	private Object data;
	
	public CharacterTreeNode(Object data){
		this.data = data;
	}
	
	public Object getData(){
		return data;
	}
}
class CharacterTreeNodeProperties implements PropertyAccess<CharacterTreeNode>{
	public ModelKeyProvider<CharacterTreeNode> key() {
		return new ModelKeyProvider<CharacterTreeNode>(){
			@Override
			public String getKey(CharacterTreeNode item) {
				return item.getData().toString();
			}
		};
	}

	public LabelProvider<CharacterTreeNode> nameLabel() {
		return new LabelProvider<CharacterTreeNode>(){
			@Override
			public String getLabel(CharacterTreeNode item) {
				return "label!";
			}
		};
	}

	public ValueProvider<CharacterTreeNode, String> name() {
		return new ValueProvider<CharacterTreeNode, String>(){
			@Override
			public String getValue(CharacterTreeNode item) {
				return item.getData().toString();
			}
			public void setValue(CharacterTreeNode object, String value) {}
			public String getPath() {return null;}
			
		};
	}
}
