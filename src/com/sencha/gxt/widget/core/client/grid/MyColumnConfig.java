package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;

import edu.arizona.biosemantics.matrixreview.client.TaxonMatrixView;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class MyColumnConfig extends ColumnConfig<Taxon, Value> {

	private Filter<Taxon, ?> filter;
	private Character character;
	
	public static class CharacterValueProvider implements ValueProvider<Taxon, Value> {
		private Character character;
		private TaxonMatrixView taxonMatrixView;
		private MyColumnConfig myColumnConfig;
		public CharacterValueProvider(Character character, TaxonMatrixView taxonMatrixView) {
			this.character = character;
			this.taxonMatrixView = taxonMatrixView;
		}
		@Override
		public Value getValue(Taxon object) {
			return object.get(character);
		}

		@Override
		public void setValue(Taxon object, Value value) {
			object.setValue(character, value);
			if(myColumnConfig != null)
				taxonMatrixView.refreshColumnHeader(myColumnConfig);
		}

		@Override
		public String getPath() {
			return "/" + character.toString() + "/value";
		}
		
		public void setMyColumnConfig(MyColumnConfig myColumnConfig) {
			this.myColumnConfig = myColumnConfig;
		}
		
	}

	public MyColumnConfig(final Character character, TaxonMatrixView taxonMatrixView) {
		super(new CharacterValueProvider(character, taxonMatrixView));
		this.character = character;
	}

	public MyColumnConfig(int width, SafeHtml header, final Character character, TaxonMatrixView taxonMatrixView) {
		super(new CharacterValueProvider(character, taxonMatrixView), width, header);
		this.character = character;
	}

	public MyColumnConfig(int width, final Character character, TaxonMatrixView taxonMatrixView) {
		this(width, SafeHtmlUtils.fromString(character.toString()), character, taxonMatrixView);
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
