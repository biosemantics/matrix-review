package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import edu.arizona.biosemantics.matrixreview.shared.IMatrixService;
import edu.arizona.biosemantics.matrixreview.shared.IMatrixServiceAsync;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class MatrixReview implements EntryPoint {
	
	public void onModuleLoad() {
		IMatrixServiceAsync matrixService = GWT.create(IMatrixService.class);
		// TaxonMatrix taxonMatrix = createSampleMatrix();
		matrixService.getMatrix(new AsyncCallback<TaxonMatrix>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(TaxonMatrix result) {
				TaxonMatrixView taxonMatrixView = new TaxonMatrixView();
				taxonMatrixView.init(result);

				// simulate etc site
				DockLayoutPanel dock = new DockLayoutPanel(Unit.EM);
				dock.addNorth(new HTML("header"), 2);
				dock.addSouth(new HTML("footer"), 2);
				dock.add(taxonMatrixView);

				// RootPanel.get().add(taxonMatrixView.asWidget());3
				RootLayoutPanel.get().add(dock);
			}
			
		});
	}
}
