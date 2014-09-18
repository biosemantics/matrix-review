package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;

import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class CharacterColumnConfig extends ColumnConfig<Taxon, Value> {
	
	public static class CharacterValueProvider implements ValueProvider<Taxon, Value> {
		private Character character;
		private Model model;
		private CharacterColumnConfig characterColumnConfig;

		public CharacterValueProvider(Character character, Model model) {
			this.character = character;
			this.model = model;
		}

		@Override
		public Value getValue(Taxon object) {
			return model.getTaxonMatrix().getValue(object, character);
		}

		@Override
		public void setValue(Taxon object, Value value) {
			model.getTaxonMatrix().setValue(object, character, value);
		}

		@Override
		public String getPath() {
			return "/" + character.toString() + "/value";
		}

		public void setCharacterColumnConfig(CharacterColumnConfig characterColumnConfig) {
			this.characterColumnConfig = characterColumnConfig;
		}
	}
	
	private Filter<Taxon, ?> filter;
	private Character character;
	private Model model;

	public CharacterColumnConfig(final Model model, final Character character) {
		super(new CharacterValueProvider(character, model));
		this.character = character;
		this.model = model;
	}

	public CharacterColumnConfig(int width, SafeHtml header, final Character character, Model model) {
		super(new CharacterValueProvider(character, model), width, header);
		this.character = character;
	}

	public CharacterColumnConfig(int width, final Character character, Model model) {
		this(width, SafeHtmlUtils.fromString(character.toString()), character, model);
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

	public boolean hasFilter() {
		return filter != null;
	}

}
