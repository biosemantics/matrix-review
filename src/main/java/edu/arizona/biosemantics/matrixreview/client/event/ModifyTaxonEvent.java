package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent.ModifyTaxonEventHandler;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon.Rank;

public class ModifyTaxonEvent extends GwtEvent<ModifyTaxonEventHandler> {

	public interface ModifyTaxonEventHandler extends EventHandler {
		void onModify(ModifyTaxonEvent event);
	}
	
	public static Type<ModifyTaxonEventHandler> TYPE = new Type<ModifyTaxonEventHandler>();
	private Taxon taxon;
	private Taxon parent;
	private Rank level;
	private String name;
	private String author;
	private String year;
	
	public ModifyTaxonEvent(Taxon taxon, Taxon parent, Rank level, String name, String author, String year) {
		this.taxon = taxon;
		this.parent = parent;
		this.level = level;
		this.name = name;
		this.author = author;
		this.year = year;
	}
	
	@Override
	public GwtEvent.Type<ModifyTaxonEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ModifyTaxonEventHandler handler) {
		handler.onModify(this);
	}

	public Taxon getTaxon() {
		return taxon;
	}
	
	public Rank getRank() {
		return level;
	}

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	public String getYear() {
		return year;
	}

	public Taxon getParent() {
		return parent;
	}	
	
	
	
	
}