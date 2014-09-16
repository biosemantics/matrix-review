package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.matrixreview.client.MenuView;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class MatrixMenuView extends MenuView {

	public MatrixMenuView(EventBus fullModelBus, EventBus subModelBus) {
		super(fullModelBus, subModelBus);
	}
}