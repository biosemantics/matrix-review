package edu.arizona.biosemantics.matrixreview.client.matrix.shared;

import java.io.Serializable;

import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

/**
 * A MatrixVersion contains a TaxonMatrix and metadata associated with the TaxonMatrix.
 * 
 * @author Andrew Stockton
 */

public class MatrixVersion implements Serializable {
	
	private static final long serialVersionUID = -7448602482770940525L;
	
	private TaxonMatrix taxonMatrix;
	private VersionInfo metadata;
	
	
	public MatrixVersion(){}
	
	public MatrixVersion(TaxonMatrix taxonMatrix, VersionInfo metadata) {
		this.taxonMatrix = taxonMatrix;
		this.metadata = metadata;
	}
	
	public MatrixVersion(MatrixVersion copyFrom){
		metadata = new VersionInfo(copyFrom.getVersionInfo());
		taxonMatrix = new TaxonMatrix(copyFrom.getTaxonMatrix());
	}
	
	public TaxonMatrix getTaxonMatrix() {
		return taxonMatrix;
	}
	public void setTaxonMatrix(TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
	}
	public VersionInfo getVersionInfo() {
		return metadata;
	}
	public void setVersionInfo(VersionInfo metadata) {
		this.metadata = metadata;
	}
}
