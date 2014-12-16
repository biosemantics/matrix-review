package edu.arizona.biosemantics.matrixreview.client.matrix.shared;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.matrixreview.client.compare.CharacterTreeNode;
import edu.arizona.biosemantics.matrixreview.client.compare.ComparisonGridCell;
import edu.arizona.biosemantics.matrixreview.client.compare.TaxonTreeNode;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class SimpleMatrixVersionProperties implements PropertyAccess<TaxonTreeNode>{
		private SimpleMatrixVersion version;
		
		public SimpleMatrixVersionProperties(SimpleMatrixVersion version){
			this.version = version;
		}
		
		public ValueProvider<TaxonTreeNode, String> valueOfCharacter(final Character character){
			return new ValueProvider<TaxonTreeNode, String>(){

				@Override
				public String getValue(TaxonTreeNode taxon) {
					Taxon t = version.getMatrix().getTaxonById(taxon.getData().getId());
					if (t == null)
						return ComparisonGridCell.CELL_BLOCKED;
					Value result = t.get(character);
					if (result == null)
						return ComparisonGridCell.CELL_BLOCKED;
					if (t.getParent() != null && taxon.getData().getParent() != null && !t.getParent().getId().equals(taxon.getData().getParent().getId()))
						return ComparisonGridCell.CELL_MOVED;
						//return ComparisonGridCell.CELL_MOVED_SHOW_VALUE + "[parent:" + t.getParent().toString() + "]" + result.getValue();
					return result.getValue();
				}

				@Override
				public void setValue(TaxonTreeNode object, String value) {}

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
							return ComparisonGridCell.CELL_BLOCKED;
						Value result = t.get(c);
						if (result == null)
							return ComparisonGridCell.CELL_BLOCKED;
						if (t.getParent() != null && taxon.getParent() != null && !t.getParent().getId().equals(taxon.getParent().getId()))
							return ComparisonGridCell.CELL_MOVED;
							//return ComparisonGridCell.CELL_MOVED_SHOW_VALUE + "[parent:" + t.getParent().toString() + "]" + result.getValue();
						
						return result.getValue();
					} else { //this is an organ node - there is no value. 
						return ComparisonGridCell.CELL_BLOCKED;
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