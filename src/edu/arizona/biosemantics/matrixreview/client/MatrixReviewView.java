package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.matrixreview.client.desktop.DesktopView;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ToggleDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class MatrixReviewView implements IsWidget {

	private TaxonMatrix taxonMatrix;
	private TaxonStore taxonStore;
	
	private SimpleEventBus eventBus;
	
	private SplitLayoutPanel splitLayoutPanel;
	private MatrixView matrixView;
	private DesktopView desktopView;
	private int desktopHeight = 500;

	public MatrixReviewView(TaxonMatrix taxonMatrix) {
		this.taxonMatrix = taxonMatrix;	
	}
	
	public Widget asWidget() {
		eventBus = new SimpleEventBus();
		matrixView = new MatrixView(eventBus, taxonMatrix);
		desktopView = new DesktopView(eventBus, taxonMatrix);
		//modelManager = new StoreModelManager(eventBus, matrixView, desktopView);
		eventBus.fireEvent(new LoadTaxonMatrixEvent(taxonMatrix));
		
		splitLayoutPanel = new SplitLayoutPanel();
		splitLayoutPanel.addSouth(desktopView, 0);
		splitLayoutPanel.add(matrixView);
		
		addEventHandlers();

		/*desktopView.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			toggleFooter();
		}
		});*/
		
		return splitLayoutPanel;
	}
	
	private void addEventHandlers() {
		eventBus.addHandler(ShowDesktopEvent.TYPE, new ShowDesktopEvent.ShowDesktopEventHandler() {
			@Override
			public void onShow(ShowDesktopEvent showDesktopEvent) {
				showDesktop();
			}
		});
		eventBus.addHandler(ToggleDesktopEvent.TYPE, new ToggleDesktopEvent.ToggleDesktopEventHandler() {
			@Override
			public void onToggle(ToggleDesktopEvent toggleDesktopEvent) {
				toggleDesktop();
			}
		});
	}

	protected void showDesktop() {
		splitLayoutPanel.forceLayout();
		if(splitLayoutPanel.getWidgetSize(desktopView) == 0) 
			splitLayoutPanel.setWidgetSize(desktopView, desktopHeight);
		splitLayoutPanel.animate(500);
	}

	protected void toggleDesktop() {
		splitLayoutPanel.forceLayout();
		if(splitLayoutPanel.getWidgetSize(desktopView) == desktopHeight) 
			splitLayoutPanel.setWidgetSize(desktopView, 0);
		else if(splitLayoutPanel.getWidgetSize(desktopView) == 0) 
			splitLayoutPanel.setWidgetSize(desktopView, desktopHeight);
		splitLayoutPanel.animate(500);
	}
	
}
