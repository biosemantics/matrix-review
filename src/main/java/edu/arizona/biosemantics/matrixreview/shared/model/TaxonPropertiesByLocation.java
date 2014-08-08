package edu.arizona.biosemantics.matrixreview.shared.model;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;

/**
 * Provides a key using the location of the taxon rather than just the taxon's id, so that 
 * the same taxon can be in a tree twice if it has been moved to a different parent at some point.
 * @author biosemantics
 *
 */

public class TaxonPropertiesByLocation implements PropertyAccess<Taxon>{
	private MatrixVersion currentVersion;
	
	public TaxonPropertiesByLocation(MatrixVersion currentVersion){
		this.currentVersion = currentVersion;
	}
	
	public ModelKeyProvider<Taxon> key() {
		return new ModelKeyProvider<Taxon>(){
			@Override
			public String getKey(Taxon taxon) {
				String parentId = taxon.getParent() == null ? "root" : taxon.getParent().getId();
				return taxon.getId()+":"+parentId; //this will allow the same model to be in the tree multiple times if it has been moved to a different parent.
			}
		};
	}
	
	public ValueProvider<Taxon, String> currentTaxonName() {
		return new ValueProvider<Taxon, String>(){
			@Override
			public String getValue(Taxon taxon) {
				Taxon currentTaxon = currentVersion.getTaxonMatrix().getTaxonById(taxon.getId());
				if (currentTaxon == null){
					System.err.println("Failed to find current version of taxon: " + taxon.getFullName() + ", id: " + taxon.getId() + ". (TaxonPropertiesByLocation.java)");
					return "ERROR";
				}
				return currentTaxon.getName();
			}
			@Override
			public void setValue(Taxon object, String value) {
				
			}
			@Override
			public String getPath() {
				return null;
			}
		};
	}
}
