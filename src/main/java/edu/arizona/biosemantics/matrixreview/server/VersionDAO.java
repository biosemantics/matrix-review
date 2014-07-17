package edu.arizona.biosemantics.matrixreview.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.thirdparty.guava.common.io.Files;

import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleTaxonMatrix;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.VersionInfo;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

/**
 * Handles data access for the various versions of a matrix.
 * 
 * Versions are saved as serialized MatrixVersion objects in the <matrixDirectory> folder. 
 * Filenames follow the format: <matrixBaseName>_<versionID>.ser . (e.g. MyMatrix_4.ser)
 * The first (original) version of the file should be called <matrixBaseName>_0.ser .
 * 
 * Versions are not meant to be modified after they are created - instead, new versions are created.
 * The only exception is the 'current' version (<matrixBaseName>_current.ser), which holds temporary
 * changes that have not yet been saved in a new version. 
 * 
 * Upon initialization, VersionDAO scans the <matrixDirectory> folder for the most recent version 
 * file. (This will be the file with the highest numerical version id at the end.) It stores the id
 * of that file in the lastVersionNumber variable. This value is used to name new versions and is 
 * incremented every time a new version is created successfully. 
 * 
 * @author Andrew Stockton
 */

public class VersionDAO {
	private static VersionDAO instance;
	private static final String extension = "ser";
	
	private String matrixDirectory;
	private String matrixBaseName;
	
	private int lastVersionNumber; //the version id of the most recent version.
	
	public VersionDAO(){
		matrixDirectory = "/home/biosemantics/Desktop/MyMatrix/";
		matrixBaseName = "MyMatrix";
		
		if (!matrixDirectory.endsWith("/")){
			matrixDirectory += "/";
		}
		
		initializeLastVersionNumber();
	}
	
	public static VersionDAO getInstance(){
		if (instance == null)
			instance = new VersionDAO();
		return instance;
	}
	
	/**
	 * Assembles a filename in the proper manner. E.g. /home/andrew/mymatrix/mymatrix_1.ser
	 */
	private String createFileName(String directory, String baseName, String versionID){
		return directory + baseName + "_" + versionID + "." + extension;
	}
	
	/**
	 * Sets lastVersionNumber to the version id of the most recent version file.
	 * This value is used to name new versions and is incremented every time a new version 
	 * is created successfully.
	 * 
	 * example: If the most recent version is MyMatrix_4.ser, lastVersionNumber should be set to 4.
	 */
	private void initializeLastVersionNumber(){
		try {
			lastVersionNumber = getAvailableVersions().size()-1; 
			//System.out.println("Last version number initialized to " + lastVersionNumber);
		} catch (IOException e) {
			System.err.println("VersionDAO: Failed to initialize lastVersionNumber. Setting lastVersionNumber to 0. The error was:");
			e.printStackTrace();
			lastVersionNumber = 0;
		}
	}
	
	/**
	 * Scans <matrixDirectory> for existing version files, starting at <matrixBaseName>_0.ser and 
	 * incrementing the version id until it cannot find a version with that id.
	 * 
	 * If a file exists but cannot be read, it is ignored. 
	 *  
	 * @return a List of the VersionInfo for each available version file. 
	 */
	public List<VersionInfo> getAvailableVersions() throws IOException{
		List<VersionInfo> results = new ArrayList<VersionInfo>();
		
		int i = 0;
		String fileName = createFileName(matrixDirectory, matrixBaseName, String.valueOf(i));
		while((new File(fileName)).exists()){
			try{
				VersionInfo info = getVersion(String.valueOf(i)).getVersionInfo();
				results.add(info);
			} catch(IOException e){ //file exists but cannot be read.
				System.err.println("Read version info failed on file: " + fileName);
			}
			i++;
			fileName = createFileName(matrixDirectory, matrixBaseName, String.valueOf(i));
		}
		
		return results;
	}
	
	/**
	 * Checks to see if <matrixBaseName>_current.ser exists. If it does not, copies it from 
	 * <matrixBaseName>_0.ser. 
	 * 
	 * @throws IOException if <matrixBaseName>_0.ser does not exist.
	 */
	private void tryCreateCurrentVersion() throws IOException {
		String filePath = createFileName(matrixDirectory, matrixBaseName, "current");
		
		File file = new File(filePath);
		if (!file.exists()){
			// version 'current' does not exist. Copy it from version 0.
			String origPath = createFileName(matrixDirectory, matrixBaseName, "0");
			File orig = new File(origPath);
			if (!orig.exists()){
				throw new IOException("Original file '" + file.getAbsolutePath() + "' does not exist.");
			}
			Files.copy(orig, file);
		}
	}
	
	/**
	 * Attempts to read file <matrixBaseName>_<versionID>.ser from <matrixDirectory> and returns the 
	 * contained MatrixFile if successful. 
	 * 
	 * @param versionID The version to load. (e.g. "0", "4", "current")
	 * @throws IOException if the file with the selected versionID does not exist or cannot be read.
	 */
	public MatrixVersion getVersion(String versionID) throws IOException{
		if (versionID.equals("current"))
			tryCreateCurrentVersion();
		
		String filePath = createFileName(matrixDirectory, matrixBaseName, versionID);
		
		File file = new File(filePath);
		if (!file.exists()){
			throw new IOException("File '" + file.getAbsolutePath() + "' does not exist.");
		}
		
		ObjectInputStream inStream = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
		MatrixVersion version;
		try {
			version = (MatrixVersion)inStream.readObject();
		} catch (Exception e) {
			e.printStackTrace();
			inStream.close();
			throw new IOException("File '" + file.getAbsolutePath() + "' could not be read.");
		}
		inStream.close();
		
		return version;
	}
	
	/**
	 * Creates a list of memory-friendly SimpleMatrixVersions from the MatrixVersions associated 
	 * with the specified version ids. 
	 * 
	 * Attempts to load and handle each version individually so that only one full MatrixVersion is 
	 * open in memory at the same time. If there is an error reading a version, that version is
	 * ignored. 
	 * 
	 * @param versionIDs the ids of the MatrixVersions to be loaded
	 */
	public List<SimpleMatrixVersion> getVersions(List<String> versionIDs){
		List<SimpleMatrixVersion> list = new ArrayList<SimpleMatrixVersion>();
		for (String versionID: versionIDs){
			try{
				MatrixVersion version = getVersion(versionID);
				SimpleTaxonMatrix simpleMatrix = new SimpleTaxonMatrix(version.getTaxonMatrix());
				SimpleMatrixVersion simpleVersion = new SimpleMatrixVersion(simpleMatrix, version.getVersionInfo());
				list.add(simpleVersion);
			} catch(Exception e){
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * Wrapper method for commitVersion.
	 * 
	 * Overwrites the file <matrixBaseName>_current.ser file with a new MatrixVersion, created using 
	 * the specified matrix and dummy VersionInfo. (VersionInfo should never be required for the
	 * 'current' version. Real metadata will be supplied by the user when they commit a new version.) 
	 * 
	 * @param matrix The matrix to be used in the new 'current' version. 
	 * @return whether or not the commit was successful. 
	 */
	public boolean commitCurrentVersion(TaxonMatrix matrix) throws IOException{
		VersionInfo metadata = new VersionInfo("current", "author", "comment");
		return commitVersion(matrix, metadata);
	}
	
	/**
	 * Wrapper method for commitVersion. 
	 * 
	 * Saves a new MatrixVersion to the file <matrixBaseName>_<versionID>.ser. 
	 * The versionID for the file is generated automatically using lastVersionNumber. However, 
	 * lastVersionNumber is not incremented until the new file version has been successfully saved.
	 * 
	 * @param matrix The matrix to use in the new version. 
	 * @param author The author of this version. 
	 * @param comment A comment to help identify this version. 
	 * @return whether or not the cmomit was successful. 
	 */
	public boolean commitNewVersion(TaxonMatrix matrix, String author, String comment) throws IOException{
		String versionID = String.valueOf(lastVersionNumber + 1);
		VersionInfo metadata = new VersionInfo(versionID, author, comment);
		
		boolean success = commitVersion(matrix, metadata);
		if (success) //only increment lastVersionNumber if the new version was saved successfully.
			lastVersionNumber++; 
		
		// update the 'current' version. 
		commitCurrentVersion(matrix);
		
		return success;
	}
	
	/**
	 * Creates a new MatrixVersion using the specified matrix and metadata and saves it to the 
	 * file <matrixBaseName>_<versionID>.ser. 
	 * 
	 * @param matrix The matrix for this version. 
	 * @param metadata The metadata for this version, including version id.
	 * @return whether or not the commit was successful. 
	 */
	private boolean commitVersion(TaxonMatrix matrix, VersionInfo metadata) throws IOException{
		//System.out.println("Attempting to commit version: " + metadata.getVersionID() + ".");
		String versionID = metadata.getVersionID();
		MatrixVersion version = new MatrixVersion(matrix, metadata);
		
		String filePath = createFileName(matrixDirectory, matrixBaseName, versionID);
		
		File file = new File(filePath);
		if (!file.exists()){
			file.createNewFile();
		}
		
		ObjectOutputStream outStream = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		outStream.writeObject(version);
		outStream.close();
		
		//System.out.println("Successfully committed version: " + metadata.getVersionID() + " to file " + filePath);
		return true;
	}
}
