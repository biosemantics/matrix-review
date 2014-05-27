package edu.arizona.biosemantics.matrixreview.shared;


import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public interface IMatrixServiceAsync {
	
	public void getMatrix(AsyncCallback<TaxonMatrix> callback);
	
}
