package edu.arizona.biosemantics.matrixreview.shared.model;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.matrixreview.client.compare.TaxonTreeNode;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;

public class TaxonTreeNodeProperties implements PropertyAccess<TaxonTreeNode>{
	private MatrixVersion currentVersion;
	
	public TaxonTreeNodeProperties(MatrixVersion currentVersion){
		this.currentVersion = currentVersion;
	}
	
	public ModelKeyProvider<TaxonTreeNode> key() {
		return new ModelKeyProvider<TaxonTreeNode>(){
			@Override
			public String getKey(TaxonTreeNode taxonNode) {
				String parentId = taxonNode.getData().getParent() == null ? "root" : taxonNode.getData().getParent().getId();
				return taxonNode.getData().getId()+":"+parentId; //this will allow the same model to be in the tree multiple times if it has been moved to a different parent.
			}
		};
	}
	
	public ValueProvider<TaxonTreeNode, String> currentTaxonName() {
		return new ValueProvider<TaxonTreeNode, String>(){
			@Override
			public String getValue(TaxonTreeNode taxonNode) {
				Taxon currentTaxon = currentVersion.getTaxonMatrix().getTaxonById(taxonNode.getData().getId());
				if (currentTaxon == null){
					System.err.println("Failed to find current version of taxon: " + taxonNode.getData().getFullName() + ", id: " + taxonNode.getData().getId() + ". (TaxonTreeNodeProperties.java)");
					return "ERROR";
				}
				return currentTaxon.getName();
			}
			@Override
			public void setValue(TaxonTreeNode object, String value) {
				
			}
			@Override
			public String getPath() {
				return null;
			}
		};
	}
}
