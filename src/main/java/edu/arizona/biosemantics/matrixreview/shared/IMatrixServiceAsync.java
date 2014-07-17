package edu.arizona.biosemantics.matrixreview.shared;


import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.VersionInfo;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public interface IMatrixServiceAsync {
	
	public void getMatrix(AsyncCallback<TaxonMatrix> callback);

	void commitCurrentVersion(TaxonMatrix matrix, AsyncCallback<Boolean> callback);
	
	void commitNewVersion(TaxonMatrix matrix, String author, String comment,
			AsyncCallback<Boolean> callback);

	void getAvailableVersions(AsyncCallback<List<VersionInfo>> callback);

	void getVersion(String versionID, AsyncCallback<MatrixVersion> callback);

	void getVersions(List<String> versionIDs,
			AsyncCallback<List<SimpleMatrixVersion>> callback);
	
}
