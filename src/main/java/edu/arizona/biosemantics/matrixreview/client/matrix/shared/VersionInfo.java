package edu.arizona.biosemantics.matrixreview.client.matrix.shared;

import java.io.Serializable;
import java.util.Date;

/**
 * Contains version metadata for a TaxonMatrix.
 *  
 * @author Andrew Stockton
 */

public class VersionInfo implements Serializable {

	private static final long serialVersionUID = 2094535724220782329L;
	
	private String versionID;
	private String author;
	private Date created;
	private String comment;
	
	public VersionInfo(){}
	
	public VersionInfo(String versionID, String author,
			String comment) {
		this.versionID = versionID;
		this.author = author;
		this.comment = comment;
		this.created = new Date();
	}
	
	public String getVersionID() {
		return versionID;
	}
	public String getAuthor() {
		return author;
	}
	public Date getCreated() {
		return created;
	}
	public String getComment() {
		return comment;
	}
	public String getKey(){
		return versionID;
	}
}
