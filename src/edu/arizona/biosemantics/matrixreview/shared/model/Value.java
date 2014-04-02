package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;

public class Value implements Serializable, HasColor, HasComment, HasDirty {

	private String value;
	private String comment = "";
	private Color color;
	private boolean dirty = false;

	public Value() { }
	
	public Value(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	protected void setValue(String value) {
		this.value = value;
	}
	
	public String toString() {
		return value;
	}

	public String getComment() {
		return comment;
	}

	protected void setComment(String comment) {
		this.comment = comment;
	}

	public Color getColor() {
		return color;
	}

	protected void setColor(Color color) {
		this.color = color;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	protected void clearDirty() {
		dirty = false;
	}

	protected void setDirty() {
		dirty = true;
	}
	
}
