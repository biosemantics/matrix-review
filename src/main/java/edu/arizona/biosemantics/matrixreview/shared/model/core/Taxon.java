package edu.arizona.biosemantics.matrixreview.shared.model.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;

import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.common.taxonomy.TaxonIdentification;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class Taxon implements Serializable, Comparable<Taxon> {

	private static final long serialVersionUID = 1L;
		
	public static int ID = 0;	
	private int id = ID++;
	
	/**
	 * Taxon hierarchy
	 */
	private Taxon parent;
	private TaxonIdentification taxonIdentification;
	private List<Taxon> children = new LinkedList<Taxon>();
		
	/**
	 * Description
	 */
	private String description = "";
	//Jin add May, 2017
	private List<String> statements = new ArrayList();
	
	public Taxon() { }
	
	public Taxon(TaxonIdentification taxonIdentification) {
		this.taxonIdentification = taxonIdentification;
	}
	
	public Taxon(TaxonIdentification taxonIdentification, String description) {
		this(taxonIdentification);
		this.description = description;
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
		
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getId() {
		return id;
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
		//by convention
		String name = taxonIdentification.getRankData().getLast().getName();
		if(getRank().equals(Rank.GENUS)) {
			return java.lang.Character.toUpperCase(name.charAt(0)) + name.substring(1);
		}
		return name;
	}
	
	public String getBiologicalName() {
		LinkedList<RankData> rankDatas = taxonIdentification.getRankData();
		String name = getName();
		LinkedList<RankData> parentRankDatas = new LinkedList<RankData>(rankDatas);
		while(parentRankDatas.size() > 1) {
			parentRankDatas.removeLast();
			if(Rank.equalOrBelowGenus(parentRankDatas.getLast().getRank())) {
				name = parentRankDatas.getLast().getName() + " " + name;
			} else {
				break;
			}
		}
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String printHierarchy() {
		return print(0);
	}
	
	@Override 
	public String toString() {
		return this.getBiologicalName();
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

	public Rank getRank() {
		return taxonIdentification.getRankData().getLast().getRank();
	}

	public void setRank(Rank rank) {
		taxonIdentification.getRankData().getLast().setRank(rank);
	}

	public void setName(String name) {
		taxonIdentification.getRankData().getLast().setName(name);
	}

	public void setAuthor(String author) {
		taxonIdentification.getRankData().getLast().setAuthor(author);
	}

	public void setYear(String year) {
		taxonIdentification.getRankData().getLast().setDate(year);
	}

	public String getAuthor() {
		return taxonIdentification.getRankData().getLast().getAuthor();
	}

	public String getYear() {
		return taxonIdentification.getRankData().getLast().getDate();
	}

	public TaxonIdentification getTaxonIdentification() {
		return taxonIdentification;
	}

	public void setTaxonIdentification(TaxonIdentification taxonIdentification) {
		this.taxonIdentification = taxonIdentification;
	}
	
	public void addStatement(String statement) {
		this.statements.add(statement);		
	}
	
	public List<String> getStatement(){
		return this.statements;
	}

	public void setStatements(List<String> statements) {
		this.statements = statements;
	}
}
