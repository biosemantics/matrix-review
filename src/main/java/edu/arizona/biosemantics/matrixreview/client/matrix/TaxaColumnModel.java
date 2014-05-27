package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class TaxaColumnModel extends ColumnModel<Taxon> {

	public TaxaColumnModel(List<TaxaColumnConfig> columnConfigs) {
		super();
		List<ColumnConfig<Taxon, ?>> list = new ArrayList<ColumnConfig<Taxon, ?>>(columnConfigs);
		this.configs = Collections.unmodifiableList(list);
	}
	
	public List<TaxaColumnConfig> getTaxaColumns() {
		List<TaxaColumnConfig> list = new ArrayList<TaxaColumnConfig>();
		for(ColumnConfig<Taxon, ?> config : configs) 
			list.add((TaxaColumnConfig)config);
		return Collections.unmodifiableList(list);
	}

	@Override
	@SuppressWarnings("unchecked")
	public TaxaColumnConfig getColumn(int colIndex) {
		return (TaxaColumnConfig) (colIndex >= 0 && colIndex < configs.size() ? configs.get(colIndex) : null);
	}
	
}
