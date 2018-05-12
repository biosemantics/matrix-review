package edu.arizona.biosemantics.matrixreview.client.common.compare;

import java.util.Comparator;

import com.sencha.gxt.data.shared.SortDir;

import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class CharactersByOrganComparator implements Comparator<Character> {

	private Model model;
	private SortDir sortDir;

	public CharactersByOrganComparator(Model model, SortDir sortDir) {
		this.model = model;
		this.sortDir = sortDir;
	}
	
	@Override
	public int compare(Character o1, Character o2) {
		if(sortDir.equals(SortDir.ASC)) {
			return doCompare(o1, o2);
		} else {
			return doCompare(o2, o1);
		}
	}

	private int doCompare(Character o1, Character o2) {
		int diff = o1.getOrgan().getName().compareTo(o2.getOrgan().getName());
		if(diff == 0)
			diff = o1.toString().compareTo(o2.toString());
		return diff;
	}
}
