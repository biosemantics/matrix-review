package edu.arizona.biosemantics.matrixreview.shared.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Character implements Serializable {

	private static final long serialVersionUID = 1L;

	private static int ID = 0;
	private int id = ID++;
	
	private String name = "";
	private String connector = "of";
	private Organ organ = null;
	
	public Character() { }
	
	public Character(String name) {
		this.name = name;
	}
	
	public Character(String name, String connector, Organ organ, int flatIndex) {
		this.name = name;
		this.connector = connector;
		this.organ = organ;
		organ.ensureContained(this, flatIndex);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getConnector() {
		return connector;
	}

	public void setConnector(String connector) {
		this.connector = connector;
	}

	public Organ getOrgan() {
		return organ;
	}
	
	public boolean hasOrgan() {
		return organ != null;
	}
	
	public void setOrgan(Organ organ, int flatIndex) {
		if(this.organ != null && !this.organ.equals(organ)) {
			this.organ.remove(this);
			organ.ensureContained(this, flatIndex);
			this.organ = organ;
		}
	}
	
	/**
	 * Hong 618
	 * required by gen\edu\arizona\biosemantics\matrixreview\shared\model\core\Character_organ_ValueProviderImpl.java
	 * organ needs a set and a get method 
	 * @param organ
	 * @param flatIndex
	 */
	public void setOrgan(Organ organ) {
		if(this.organ != null && !this.organ.equals(organ)) {
			this.organ.remove(this);
			organ.ensureContained(this, 0);
			this.organ = organ;
		}
	}
	
	public String toString() {
		if(organ == null)
			return name;
		if(organ.toString().trim().isEmpty())
			return name;
		return name + " " + connector + " " + organ.toString();
	}
	
	public String complete() {
		return this.toString();
	}
	
	public int getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Character other = (Character) obj;
		if (id != other.id)
			return false;
		return true;
	}
		
}
