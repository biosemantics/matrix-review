package edu.arizona.biosemantics.matrixreview.shared.model.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;

import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class Taxon implements Serializable, Comparable<Taxon> {
	
	public enum Rank implements Serializable  {
		LIFE(0), 
		SUPERDOMAIN(1), DOMAIN(1), SUBDOMAIN(2), 
		SUPERKINGDOM(3), KINGDOM(4), SUBKINGDOM(5), 
		SUPERPHYLUM(6), PHYLUM(7), SUBPHYLUM(8),
		SUPERCLASS(9), CLASS(10), SUBCLASS(11),
		SUPERORDER(12), ORDER(13), SUBORDER(14),
		SUPERFAMILY(15), FAMILY(16), SUBFAMILY(17),
		SUPERTRIBE(18), TRIBE(19), SUBTRIBE(20),
		SUPERGENUS(21), GENUS(22), SUBGENUS(23),
		SUPERSECTION(24), SECTION(25), SUBSECTION(26),
		SUPERSERIES(27), SERIES(28), SUBSERIES(29), 
		SUPERSPECIES(30), SPECIES(31), SUBSPECIES(32),
		SUPERVARIETY(33), VARIETY(34), SUBVARIETAS(35),
		SUPERFORMA(36), FORMA(37), SUBFORMA(38),
		SUPERGROUP(39), GROUP(40), SUBGROUP(41),
		UNRANKED(42);
		
		private int id;
		
		Rank() { }
	
		Rank(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
		
		public static boolean isValidParentChild(Rank parent, Rank child) {
			int parentLevelId = parent == null? -1 : parent.getId();
			int childLevelId = child == null? -1 : child.getId();
			//special case group allows children of itself as it is the lowest rank
			if(parent != null && child != null && parent.equals(UNRANKED) && child.equals(UNRANKED))
				return true;
			return parentLevelId < childLevelId;
		}
		
		public static boolean equalOrBelowGenus(Rank level) {
			return level.getId() >= GENUS.getId();
		}
		
		public static boolean aboveGenus(Rank level) {
			return level.getId() < GENUS.getId();
		}
	}
	

	public static int ID = 0;	
	private int id = ID++;
	
	/**
	 * Taxon hierarchy
	 */
	private Rank rank;
	private Taxon parent;
	private List<Taxon> children = new LinkedList<Taxon>();
	
	/**
	 * Taxon concept
	 */
	private String name;
	private String author;
	private String year;
	
	/**
	 * Description
	 */
	private String description = "";
	
	public Taxon() { }
	
	public Taxon(Rank rank, String name, String author, String year) {
		this.rank = rank;
		this.name = name;
		this.author = author;
		this.year = year;
	}
	
	public Taxon(Rank rank, String name, String author, String year, String description) {
		this(rank, name, author, year);
		this.description = description;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addChild(Taxon child) {
		child.setParent(this);
		children.add(child);
	}
	
	public void addChild(int index, Taxon child) {
		child.setParent(this);
		children.add(index, child);
	}

	public void removeChild(Taxon taxon) {
		Iterator<Taxon> it = children.iterator();
		while(it.hasNext()) {
			Taxon child = it.next();
			if(child.equals(taxon)) {
				it.remove();
			}
		}
	}
	
	public void removeDescendantRecursively(Taxon taxon) {
		Iterator<Taxon> it = children.iterator();
		while(it.hasNext()) {
			Taxon child = it.next();
			if(child.equals(taxon)) {
				it.remove();
			}
			child.removeDescendantRecursively(taxon);
		}
	}
	
	public void setParent(Taxon parent) {
		this.parent = parent;
	}
	
	public void setChildren(List<Taxon> children) {
		this.children = children;
	}
	
	public void setAuthor(String author) {
		this.author = author;
	}
	
	public void setYear(String year) {
		this.year = year;
	}

	public void setRank(Rank rank) {
		this.rank = rank;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getId() {
		return id;
	}

	public Rank getRank() {
		return rank;
	}

	public String getAuthor() {
		return author;
	}

	public String getYear() {
		return year;
	}

	
	public List<Taxon> getChildren() {
		return children;
	}
	
	public Taxon getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	public String getName() {
		//by scientific convention
		if(rank.equals(Rank.GENUS)) {
			return java.lang.Character.toUpperCase(name.charAt(0)) + name.substring(1);
		}
		return name;
	}
	
	public String getFullName() {
		if(hasParent() && Rank.equalOrBelowGenus(getParent().getRank())) {
			return getParent().getFullName() + " " + getName();
		} else {
			return getName();
		}
	}
	
	public String getDescription() {
		return description;
	}
	
	public String printHierarchy() {
		return print(0);
	}
	
	@Override 
	public String toString() {
		return this.getFullName();
	}
	
	public String print(int ident) {
		String identation =  new String(new char[ident]).replace("\0", " ");
		String result = identation + getName();
		for(Taxon child : children) {
			result +=  "\n " + child.print(ident + 1);
		}
		//result = result.substring(0, result.length() - 2);
		return result;
	}
	
	@Override
	public int compareTo(Taxon o) {
		return this.getName().compareTo(o.getName());
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
		Taxon other = (Taxon) obj;
		if (id != other.id)
			return false;
		return true;
	}

}
