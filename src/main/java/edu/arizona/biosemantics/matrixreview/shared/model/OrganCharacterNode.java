package edu.arizona.biosemantics.matrixreview.shared.model;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public class OrganCharacterNode {

	public static class OrganNode extends OrganCharacterNode {
		private Organ organ;

		public OrganNode(Organ organ) {
			this.organ = organ;
		}

		public Organ getOrgan() {
			return organ;
		}
	}

	public static class CharacterNode extends OrganCharacterNode {
		private Character character;

		public CharacterNode(Character character) {
			this.character = character;
		}

		public Character getCharacter() {
			return character;
		}

		public void setCharacter(Character character) {
			this.character = character;
		}
	}

	public static class OrganCharacterNodeProperties implements
			PropertyAccess<OrganCharacterNode> {
		public ValueProvider<OrganCharacterNode, String> name() {
			return new ValueProvider<OrganCharacterNode, String>() {
				@Override
				public String getValue(OrganCharacterNode object) {
					if (object instanceof OrganNode)
						return ((OrganNode) object).getOrgan().getName();
					if (object instanceof CharacterNode)
						return ((CharacterNode) object).getCharacter()
								.getName();
					return "";
				}

				@Override
				public void setValue(OrganCharacterNode object, String value) {
				}

				@Override
				public String getPath() {
					return "organCharacterNode";
				}
			};
		}

		public ModelKeyProvider<OrganCharacterNode> key() {
			return new ModelKeyProvider<OrganCharacterNode>() {
				@Override
				public String getKey(OrganCharacterNode item) {
					if (item instanceof OrganNode)
						return "organ-"
								+ ((OrganNode) item).getOrgan().getId();
					if (item instanceof CharacterNode)
						return "character-"
								+ ((CharacterNode) item).getCharacter()
										.getId();
					return "";
				}
			};
		}
	}

}
