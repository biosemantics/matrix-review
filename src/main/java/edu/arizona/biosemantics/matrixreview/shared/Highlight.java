package edu.arizona.biosemantics.matrixreview.shared;

import java.io.Serializable;

public class Highlight implements Serializable {

	private String text;
	private String colorHex;
	
	public Highlight() {
		
	}

	public Highlight(String text, String colorHex) {
		super();
		this.text = text;
		this.colorHex = colorHex;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getColorHex() {
		return colorHex;
	}

	public void setColorHex(String colorHex) {
		this.colorHex = colorHex;
	}
	
	public String toString() {
		return this.text + " " + this.colorHex;
	}
	
	
}