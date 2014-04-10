package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;

import edu.arizona.biosemantics.matrixreview.client.manager.ViewManager;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class CharacterColumnConfig extends ColumnConfig<Taxon, Value> {

	private Filter<Taxon, ?> filter;
	private Character character;

	public static class CharacterValueProvider implements ValueProvider<Taxon, Value> {
		private Character character;
		private CharacterColumnConfig characterColumnConfig;
		private ViewManager viewManager;

		public CharacterValueProvider(Character character, ViewManager viewManager) {
			this.character = character;
			this.viewManager = viewManager;
		}

		@Override
		public Value getValue(Taxon object) {
			return object.get(character);
		}

		@Override
		public void setValue(Taxon object, Value value) {
			object.setValue(character, value);
			if (characterColumnConfig != null)
				viewManager.refreshCharacterHeader(characterColumnConfig);
		}

		@Override
		public String getPath() {
			return "/" + character.toString() + "/value";
		}

		public void setCharacterColumnConfig(CharacterColumnConfig characterColumnConfig) {
			this.characterColumnConfig = characterColumnConfig;
		}

	}

	public CharacterColumnConfig(final Character character, ViewManager viewManager) {
		super(new CharacterValueProvider(character, viewManager));
		this.character = character;
	}

	public CharacterColumnConfig(int width, SafeHtml header, final Character character, ViewManager viewManager) {
		super(new CharacterValueProvider(character, viewManager), width, header);
		this.character = character;
	}

	public CharacterColumnConfig(int width, final Character character, ViewManager viewManager) {
		this(width, SafeHtmlUtils.fromString(character.toString()), character, viewManager);
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
