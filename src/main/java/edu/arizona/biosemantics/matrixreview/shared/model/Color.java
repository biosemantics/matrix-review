package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;

public class Color implements Serializable {

	private String hex;
	private String use;
	
	public Color() { }
	
	public Color(String hex, String use) {
		super();
		this.hex = hex;
		this.use = use;
	}
	public String getHex() {
		return hex;
	}
	public void setHex(String hex) {
		this.hex = hex;
	}
	public String getUse() {
		return use;
	}
	public void setUse(String use) {
		this.use = use;
	}
	

}
