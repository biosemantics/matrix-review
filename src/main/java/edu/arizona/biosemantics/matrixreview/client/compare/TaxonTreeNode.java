package edu.arizona.biosemantics.matrixreview.client.compare;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxonTreeNode implements CellIdentifier.CellIdentifierObject{
	private Taxon data;
	
	public TaxonTreeNode(Taxon taxon){
		this.data = taxon;
	}
	
	public Taxon getData(){
		return data;
	}

	@Override
	public boolean matches(Object other) {
		if (other instanceof TaxonTreeNode)
			return ((TaxonTreeNode)other).getData().equals(data);
		return false;
	}
}
