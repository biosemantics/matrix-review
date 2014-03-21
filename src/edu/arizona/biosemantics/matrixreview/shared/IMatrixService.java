package edu.arizona.biosemantics.matrixreview.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

@RemoteServiceRelativePath("matrix")
public interface IMatrixService extends RemoteService {

	public TaxonMatrix getMatrix();
	
}
