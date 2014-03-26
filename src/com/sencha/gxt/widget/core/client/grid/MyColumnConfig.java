package com.sencha.gxt.widget.core.client.grid;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.client.ValueProvider;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class MyColumnConfig extends ColumnConfig<Taxon, String> {

	private Character character;

	public MyColumnConfig(final Character character) {
		super(new ValueProvider<Taxon, String>() {
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
				return "/" + character.getName() + "/value";
			}
			
		});
		this.character = character;
	}

	public MyColumnConfig(int width, SafeHtml header, final Character character) {
		super(new ValueProvider<Taxon, String>() {
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
				return "/" + character.getName() + "/value";
			}
			
		}, width, header);
		this.character = character;
	}

	public MyColumnConfig(int width, final Character character) {
		this(width, SafeHtmlUtils.fromString(character.getName()), character);
	}

	public Character getCharacter() {
		return character;
	}

	
}
