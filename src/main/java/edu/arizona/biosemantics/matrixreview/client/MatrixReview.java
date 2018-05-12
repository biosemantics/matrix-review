package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import edu.arizona.biosemantics.matrixreview.shared.IMatrixService;
import edu.arizona.biosemantics.matrixreview.shared.IMatrixServiceAsync;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class MatrixReview implements EntryPoint {

	@Override
	public void onModuleLoad() {		  
		final MatrixReviewView view = new MatrixReviewView();

		// simulate etc site 
		DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
		dock.addNorth(new HTML("header"), 2);
		HTML footer = new HTML("footer");
		dock.addSouth(footer, 2);
		dock.add(view.asWidget());
		RootLayoutPanel.get().add(dock);

		IMatrixServiceAsync matrixService = GWT.create(IMatrixService.class);
		// TaxonMatrix taxonMatrix = createSampleMatrix();
		matrixService.getMatrix(new AsyncCallback<Model>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(final Model result) {
				view.setFullModel(result);
			}
		});
	}
}
