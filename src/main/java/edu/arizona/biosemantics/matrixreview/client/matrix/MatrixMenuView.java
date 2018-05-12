package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.event.shared.EventBus;

import edu.arizona.biosemantics.matrixreview.client.MenuView;
import edu.arizona.biosemantics.matrixreview.client.ModelMerger;
import edu.arizona.biosemantics.matrixreview.client.config.ManageMatrixView;

public class MatrixMenuView extends MenuView {

	public MatrixMenuView(EventBus fullModelBus, EventBus subModelBus, ManageMatrixView manageMatrixView, ModelMerger modelMerger) {
		super(fullModelBus, subModelBus, modelMerger, manageMatrixView);
	}
}