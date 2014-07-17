package edu.arizona.biosemantics.matrixreview.client.matrix.shared;

import java.io.Serializable;

/**
 * The memory-friendly version of MatrixVersion. Contains a SimpleTaxonMatrix instead of a full 
 * TaxonMatrix.
 * 
 * @author Andrew Stockton
 */

public class SimpleMatrixVersion implements Serializable {

	private static final long serialVersionUID = -3481183748176763053L;
	
	private SimpleTaxonMatrix matrix;
	private VersionInfo metadata;
	
	
	public SimpleMatrixVersion(){}
	
	public SimpleMatrixVersion(SimpleTaxonMatrix matrix, VersionInfo metadata) {
		this.matrix = matrix;
		this.metadata = metadata;
	}
	
	public SimpleTaxonMatrix getMatrix() {
		return matrix;
	}
	public VersionInfo getVersionInfo() {
		return metadata;
	}
}
