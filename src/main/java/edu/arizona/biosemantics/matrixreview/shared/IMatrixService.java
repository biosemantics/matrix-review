package edu.arizona.biosemantics.matrixreview.shared;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.VersionInfo;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

@RemoteServiceRelativePath("matrix")
public interface IMatrixService extends RemoteService {

	public TaxonMatrix getMatrix();
	
	public boolean commitCurrentVersion(TaxonMatrix matrix);
	
	boolean commitNewVersion(TaxonMatrix matrix, String author, String comment);

	List<VersionInfo> getAvailableVersions();

	MatrixVersion getVersion(String versionID);

	List<SimpleMatrixVersion> getVersions(List<String> versionIDs);
	
}
