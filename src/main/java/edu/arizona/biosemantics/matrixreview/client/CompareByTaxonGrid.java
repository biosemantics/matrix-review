package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.ContentPanel;

public class CompareByTaxonGrid extends ContentPanel{
	public CompareByTaxonGrid(){
		this.setHeadingText("Viewing Taxon: ");
		this.getElement().getStyle().setBackgroundColor("orange");
		this.add(new Label("Three"));
		this.add(new Label("Four!"));
	}
}
