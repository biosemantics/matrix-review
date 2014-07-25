package edu.arizona.biosemantics.matrixreview.client.matrix.shared;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.matrixreview.client.CharacterTreeNode;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class SimpleMatrixVersionProperties implements PropertyAccess<Taxon>{
		private SimpleMatrixVersion version;
		
		public SimpleMatrixVersionProperties(SimpleMatrixVersion version){
			this.version = version;
		}
		
		public ValueProvider<Taxon, String> valueOfCharacter(final Character character){
			return new ValueProvider<Taxon, String>(){

				@Override
				public String getValue(Taxon taxon) {
					Taxon t = version.getMatrix().getTaxonById(taxon.getId());
					if (t == null)
						return "---";
					Value result = t.get(character);
					if (result == null)
						return "---";
					return result.getValue();
				}

				@Override
				public void setValue(Taxon object, String value) {}

				@Override
				public String getPath() {
					return null;
				}
			};
		}
		public ValueProvider<CharacterTreeNode, String> valueOfTaxon(final Taxon taxon){
			return new ValueProvider<CharacterTreeNode, String>(){

				@Override
				public String getValue(CharacterTreeNode node) {
					if (node.getData() instanceof Character){ //if this node is a character node (rather than an organ node)
						Taxon t = version.getMatrix().getTaxonById(taxon.getId());
						Character c = (Character)node.getData();
						if (t == null)
							return "---";
						Value result = t.get(c);
						if (result == null)
							return "---";
						return result.getValue();
					} else { //this is an organ node - there is no value. 
						return "---";
					}					
				}

				@Override
				public void setValue(CharacterTreeNode object, String value) {}

				@Override
				public String getPath() {
					return null;
				}
			};
		}
	}