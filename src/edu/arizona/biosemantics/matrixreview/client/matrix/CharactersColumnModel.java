package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class CharactersColumnModel extends ColumnModel<Taxon> {

	public CharactersColumnModel(List<CharacterColumnConfig> columnConfigs) {
		super();
		List<ColumnConfig<Taxon, ?>> list = new ArrayList<ColumnConfig<Taxon, ?>>(columnConfigs);
		this.configs = Collections.unmodifiableList(list);
	}
	
	public List<CharacterColumnConfig> getCharacterColumns() {
		List<CharacterColumnConfig> list = new ArrayList<CharacterColumnConfig>();
		for(ColumnConfig<Taxon, ?> config : configs) 
			list.add((CharacterColumnConfig)config);
		//return Collections.unmodifiableList(list);
		return list;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public CharacterColumnConfig getColumn(int colIndex) {
		return (CharacterColumnConfig) (colIndex >= 0 && colIndex < configs.size() ? configs.get(colIndex) : null);
	}
		
}
