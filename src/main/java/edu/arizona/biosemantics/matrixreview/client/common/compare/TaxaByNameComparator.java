package edu.arizona.biosemantics.matrixreview.client.common.compare;

import java.util.Comparator;

import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class TaxaByNameComparator implements Comparator<Taxon> {

	private SortDir sortDir;

	public TaxaByNameComparator( SortDir sortDir) {
		this.sortDir = sortDir;
	}
	
	@Override
	public int compare(Taxon o1, Taxon o2) {
		if(sortDir.equals(SortDir.ASC)) {
			return doCompare(o1, o2);
		} else {
			return doCompare(o2, o1);
		}
	}

	private int doCompare(Taxon o1, Taxon o2) {
		int diff = o1.getName().compareTo(o2.getName());
		if(diff == 0)
			diff = o1.getBiologicalName().compareTo(o2.getBiologicalName());
		return diff;
	}
}
