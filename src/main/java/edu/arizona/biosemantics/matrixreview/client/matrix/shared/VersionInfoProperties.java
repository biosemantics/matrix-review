package edu.arizona.biosemantics.matrixreview.client.matrix.shared;

import java.util.Date;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * Used for the 'Select Version' dialog grid. 
 * 
 * @author Andrew Stockton
 */

public interface VersionInfoProperties extends PropertyAccess<VersionInfo>{
	
	ModelKeyProvider<VersionInfo> key();
	
	ValueProvider<VersionInfo, String> versionID();
	ValueProvider<VersionInfo, String> author();
	ValueProvider<VersionInfo, String> comment();
	ValueProvider<VersionInfo, Date> created();
}
