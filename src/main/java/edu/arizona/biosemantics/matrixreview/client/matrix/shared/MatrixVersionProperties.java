package edu.arizona.biosemantics.matrixreview.client.matrix.shared;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.matrixreview.client.compare.CharacterTreeNode;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class MatrixVersionProperties implements PropertyAccess<Taxon>{
		private MatrixVersion version;
		
		public MatrixVersionProperties(MatrixVersion version){
			this.version = version;
		}
		
		public ValueProvider<Taxon, String> valueOfCharacter(final Character character){
			return new ValueProvider<Taxon, String>(){
				
				@Override
				public String getValue(Taxon taxon) {
					Taxon t = version.getTaxonMatrix().getTaxonById(taxon.getId());
					if (t == null)
						return "<empty>";
					if (t.getParent() != null && taxon.getParent() != null && !t.getParent().getId().equals(taxon.getParent().getId()))
						return "<moved>";
					Value result = t.get(character);
					if (result == null)
						return "<empty>";
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
						Taxon t = version.getTaxonMatrix().getTaxonById(taxon.getId());
						Character c = (Character)node.getData();
						if (t == null)
							return "<empty>";
						if (t.getParent() != null && taxon.getParent() != null && !t.getParent().getId().equals(taxon.getParent().getId()))
							return "<moved>";
						Value result = t.get(c);
						if (result == null)
							return "<empty>";
						return result.getValue();
					} else { //this is an organ node - there is no value. 
						return "<empty>";
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