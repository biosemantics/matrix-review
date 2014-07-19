package edu.arizona.biosemantics.matrixreview.shared.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.HasColor;
import edu.arizona.biosemantics.matrixreview.shared.model.HasComment;
import edu.arizona.biosemantics.matrixreview.shared.model.HasDirty;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class Taxon implements Serializable, Comparable<Taxon>, HasColor, HasComment, HasDirty, HasLocked {
	
	public static int currentId = 0;	
	
	public enum Level implements Serializable  {
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
		
		Level() { }
	
		Level(int id) {
			this.id = id;
		}
		
		public int getId() {
			return id;
		}
		
		
		public static boolean isValidParentChild(Level parent, Level child) {
			int parentLevelId = parent == null? -1 : parent.getId();
			int childLevelId = child == null? -1 : child.getId();
			//special case group allows children of itself as it is the lowest rank
			if(parent != null && child != null && parent.equals(UNRANKED) && child.equals(UNRANKED))
				return true;
			return parentLevelId < childLevelId;
		}
		
		public static boolean equalOrBelowGenus(Level level) {
			return level.getId() >= GENUS.getId();
		}
		
		public static boolean aboveGenus(Level level) {
			return level.getId() < GENUS.getId();
		}
	}
	
	/**
	 * Taxon hierarchy
	 */
	private Level level;
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
	private String description;
	
	/**
	 * Matrix related
	 */
	private String id;
	private TaxonMatrix taxonMatrix;
	private Map<Character, Value> values = new HashMap<Character, Value>();
	private String comment = "";
	private Color color;
	private boolean dirty = false;
	private boolean locked = false;
	private boolean hidden = false;
	
	public Taxon() { }
	
	public Taxon(String id, Level level, String name, String author, String year) {
		this.id = id;
		this.level = level;
		this.name = name;
		this.author = author;
		this.year = year;
	}
	
	public Taxon(String id, Level level, String name, String author, String year, String description) {
		this(id, level, name, author, year);
		this.description = description;
	}
	
	public Taxon(String id, Level level, String name, String author, String year, String description, Collection<Character> characters) {
		this(id, level, name, author, year, description);
		this.init(characters);
	}
	
	public Taxon(String id, Level level, String name, String author, String year, Map<Character, Value> values) {
		this(id, level, name, author, year);
		this.values = values;
	}
		
	protected void addChild(Taxon child) {
		children.add(child);
	}
	
	protected void addChild(int index, Taxon child) {
		children.add(index, child);
	}
	
	public List<Taxon> getChildren() {
		return new LinkedList<Taxon>(children);
	}
	
	public Taxon getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	protected void setParent(Taxon parent) {
		this.parent = parent;
	}
	
	protected void setChildren(List<Taxon> children) {
		this.children = children;
	}
		
	public String getName() {
		//by scientific convention
		if(level.equals(Level.GENUS)) {
			return java.lang.Character.toUpperCase(name.charAt(0)) + name.substring(1);
		}
		return name;
	}
	
	public String getFullName() {
		if(hasParent() && Level.equalOrBelowGenus(getParent().getLevel())) {
			return getParent().getFullName() + " " + getName();
		} else {
			return getName();
		}
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	protected Value put(Character key, Value value) {
		value.setCharacter(key);
		value.setTaxon(this);
		return values.put(key, value);
	}

	protected Value remove(Character key) {
		return values.remove(key);
	}

	public Value get(Character key) {
		return values.get(key);
	}

	protected void init(Collection<Character> characters) {
		for(Character character : characters) {
			this.addCharacter(character);
		}
	}

	protected void addCharacter(Character character) {
		if(!values.containsKey(character)) {
			Value value = new Value("");
			value.setCharacter(character);
			value.setTaxon(this);
			values.put(character, value);
		}
	}

	public String getDescription() {
		return description;
	}
	
	protected void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return getName();// + ": " + values.toString();
	}

	@Override
	public int compareTo(Taxon o) {
		return this.getName().compareTo(o.getName());
	}

	public String getComment() {
		return comment;
	}

	protected void setComment(String comment) {
		this.comment = comment;
	}
	
	public boolean isCommented() {
		return !comment.trim().isEmpty();
	}

	public Color getColor() {
		return color;
	}

	protected void setColor(Color color) {
		this.color = color;
		for(Value value : this.values.values()) {
			value.setColor(color);
		}
	}
	
	public boolean hasColor() {
		return color != null;
	}

	public TaxonMatrix getTaxonMatrix() {
		return taxonMatrix;
	}

	protected void setTaxonMatrix(TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;
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

	public void setValue(Character character, Value value) {
		taxonMatrix.setValue(this, character, value);
	}
	
	//TODO, key really ID in order?
	public String getId() {
		return String.valueOf(id);
	}
	
	public boolean isLocked() {
		return locked;
	}

	protected void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	protected void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public Level getLevel() {
		return level;
	}

	public String getAuthor() {
		return author;
	}

	public String getYear() {
		return year;
	}

	protected void setAuthor(String author) {
		this.author = author;
	}
	
	protected void setYear(String year) {
		this.year = year;
	}

	protected void setLevel(Level level) {
		this.level = level;
	}

	protected void removeChild(Taxon taxon) {
		Iterator<Taxon> it = children.iterator();
		while(it.hasNext()) {
			Taxon child = it.next();
			if(child.equals(taxon)) 
				it.remove();
		}
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Taxon)
			return this.id.equals(((Taxon)o).getId());
		return false;
	}
}
