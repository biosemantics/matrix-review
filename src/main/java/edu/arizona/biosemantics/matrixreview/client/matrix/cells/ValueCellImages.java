package edu.arizona.biosemantics.matrixreview.client.matrix.cells;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.ImageResource;

public interface ValueCellImages extends ClientBundle {

	@Source("black.gif")
	ImageResource black();

	@Source("red.gif")
	ImageResource red();
	
	@Source("black_red.gif")
	ImageResource blackRed();
	
}