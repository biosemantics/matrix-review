package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class MyColumnConfig extends ColumnConfig<Taxon, String> {

	private Filter<Taxon, ?> filter;
	private Character character;
	
	public static class CharacterValueProvider implements ValueProvider<Taxon, String> {
		private Character character;
		public CharacterValueProvider(Character character) {
			this.character = character;
		}
		@Override
		public String getValue(Taxon object) {
			return object.get(character).getValue();
		}

		@Override
		public void setValue(Taxon object, String value) {
			object.put(character, new Value(value));
		}

		@Override
		public String getPath() {
			return "/" + character.toString() + "/value";
		}
		
	}

	public MyColumnConfig(final Character character) {
		super(new CharacterValueProvider(character));
		this.character = character;
	}

	public MyColumnConfig(int width, SafeHtml header, final Character character) {
		super(new CharacterValueProvider(character), width, header);
		this.character = character;
	}

	public MyColumnConfig(int width, final Character character) {
		this(width, SafeHtmlUtils.fromString(character.toString()), character);
	}

	public Character getCharacter() {
		return character;
	}

	public Filter<Taxon, ?> getFilter() {
		return filter;
	}

	public void setFilter(Filter<Taxon, ?> filter) {
		this.filter = filter;
	}
	
}
