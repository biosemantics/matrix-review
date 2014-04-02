package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;

public class Character implements Serializable, HasColor, HasComment, HasDirty {

	private String name = "";
	private String organ= "";
	private String comment = "";
	private Color color;
	private boolean dirty = false;

	public Character() { }
	
	public Character(String name) {
		this.name = name;
	}
	
	public Character(String name, String organ) {
		this.name = name;
		this.organ = organ;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}
	
	public String getOrgan() {
		return organ;
	}
	
	public boolean hasOrgan() {
		return organ != null && !organ.isEmpty();
	}
	
	protected void setOrgan(String organ) {
		this.organ = organ;
	}
	
	public String toString() {
		if(organ == null || organ.isEmpty())
			return name;
		return name + " of " + organ;
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
