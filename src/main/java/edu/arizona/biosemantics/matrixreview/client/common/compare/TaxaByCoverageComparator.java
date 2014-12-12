package edu.arizona.biosemantics.matrixreview.client.common.compare;

import java.util.Comparator;

import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class TaxaByCoverageComparator implements Comparator<Taxon> {

	private Model model;
	private SortDir sortDir;

	public TaxaByCoverageComparator(Model model, SortDir sortDir) {
		this.model = model;
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
		int diff = model.getTaxonMatrix().getTaxonValueCount(o1) - model.getTaxonMatrix().getTaxonValueCount(o2);
		if(diff == 0)
			diff = o1.getFullName().compareTo(o2.getFullName());
		return diff;
	}
}
