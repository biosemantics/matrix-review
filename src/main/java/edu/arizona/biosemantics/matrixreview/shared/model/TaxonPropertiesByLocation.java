package edu.arizona.biosemantics.matrixreview.shared.model;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;

/**
 * Provides a key using the location of the taxon rather than just the taxon's id, so that 
 * the same taxon can be in a tree twice if it has been moved to a different parent at some point.
 * @author biosemantics
 *
 */

public class TaxonPropertiesByLocation implements TaxonProperties{
	@Override
	public ModelKeyProvider<Taxon> key() {
		return new ModelKeyProvider<Taxon>(){
			@Override
			public String getKey(Taxon taxon) {
				String parentId = taxon.getParent() == null ? "root" : taxon.getParent().getId();
				return taxon.getId()+":"+parentId; //this will allow the same model to be in the tree twice if it has been moved to a different parent.
			}
		};
	}
	@Override
	public LabelProvider<Taxon> nameLabel() {
		return null;
	}
	@Override
	public ValueProvider<Taxon, String> description() {
		return null;
	}
	@Override
	public ValueProvider<Taxon, String> fullName() {
		return new ValueProvider<Taxon, String>(){
			@Override
			public String getValue(Taxon taxon) {
				return taxon.getFullName();
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
