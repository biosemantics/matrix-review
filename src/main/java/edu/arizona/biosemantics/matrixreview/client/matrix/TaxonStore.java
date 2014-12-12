package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonProperties;

public class TaxonStore extends TreeStore<Taxon> {

	public class TaxonNode implements TreeStore.TreeNode<Taxon> {

		private Taxon taxon;
		private List<TaxonNode> children;

		public TaxonNode(Taxon taxon, List<TaxonNode> children) {
			this.taxon = taxon;
			this.children = children;
		}
		
		@Override
		public List<TaxonNode> getChildren() {
			/*List<TaxonNode> result = new ArrayList<TaxonNode>(taxon.getChildren().size());
			for(Taxon child : taxon.getChildren())
				result.add(new TaxonNode(child)); 
			return result;*/
			return children;
		}

		@Override
		public Taxon getData() {
			return taxon;
		}
		
	}
	
	private static final TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);
	
	public TaxonStore() {
		super(taxonProperties.key());
	}
	
	public void enableFilters(boolean value) {
		this.filtersEnabled = value;
	}
	
	public void enableAndRefreshFilters() {
	    this.filtersEnabled = true;
	    applyFilters();
	}

}
