package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

import edu.arizona.biosemantics.matrixreview.client.config.ManageMatrixView;
import edu.arizona.biosemantics.matrixreview.client.config.ManageMenuView;
import edu.arizona.biosemantics.matrixreview.client.config.ModelControler;
import edu.arizona.biosemantics.matrixreview.client.desktop.DesktopView;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowModifyEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ToggleDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixMenuView;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class MatrixReviewView extends SplitLayoutPanel {
	
	private Model fullModel;
	private SimpleEventBus subModelBus;
	private SimpleEventBus fullModelBus;

	private SimpleContainer contentContainer = new SimpleContainer();
	private SimpleContainer menuContainer = new SimpleContainer();
	private ManageMenuView manageMenuView;
	private MatrixMenuView matrixMenuView;
	private ManageMatrixView manageMatrixView;
	private MatrixView matrixView;
	private DesktopView desktopView;
	private int desktopHeight = 300;
	private ModelMerger modelMerger;
	private ModelControler modelControler;

	public MatrixReviewView() {	
		subModelBus = new SimpleEventBus();
		fullModelBus = new SimpleEventBus();
		
		modelControler = new ModelControler(fullModelBus);
		modelMerger = new ModelMerger(fullModelBus, subModelBus);
		manageMatrixView = new ManageMatrixView(fullModelBus, subModelBus);
		matrixView = new MatrixView(subModelBus);
		desktopView = new DesktopView(fullModelBus, subModelBus);
		manageMenuView = new ManageMenuView(fullModelBus, subModelBus, manageMatrixView);
		matrixMenuView = new MatrixMenuView(fullModelBus, subModelBus);

		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(menuContainer, new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(contentContainer, new VerticalLayoutData(1,1));
		setContent(manageMatrixView);
		setMenu(manageMenuView);
		addSouth(desktopView, 0);
		add(verticalLayoutContainer);
		
		addEventHandlers();
		
		/*desktopView.addClickHandler(new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			toggleFooter();
		}
		});*/
	}

	private void setMenu(MenuView menuView) {
		menuContainer.setWidget(menuView);
	}
	
	private void setContent(IsWidget content) {
		contentContainer.setWidget(content);
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				contentContainer.forceLayout();
				forceLayout();
			}
		}); 
	}
		
	private void addEventHandlers() {
		fullModelBus.addHandler(ShowModifyEvent.TYPE, new ShowModifyEvent.ShowModifyEventHandler() {
			@Override
			public void onShow(ShowModifyEvent event) {
				//modelMerger.mergeToFullModel(fullModel, subModel, subModelOriginal);
				//modelMerger.commitEvents();
				fullModelBus.fireEvent(new LoadModelEvent(fullModel));
				setContent(manageMatrixView);
				setMenu(manageMenuView);
			}
		});
		fullModelBus.addHandler(ShowMatrixEvent.TYPE, new ShowMatrixEvent.ShowMatrixEventHandler() {
			@Override
			public void onShow(ShowMatrixEvent event) {
				Model subModel = modelMerger.getSubModel(event.getCharacters(), event.getTaxa());
				/*// the order is critical, because characters bind themselves to an organ  instance
				// merger will create new organ's per create sub model but characters are always original
				// character's need to be bound to submodel's organ's to proceed in showing matrix 
				// and merging results back to full model; Needs improvement eventually so this is less error-prone	
				subModelOriginal = modelMerger.createSubModel(event.getCharacters(), event.getTaxa());
				subModel = modelMerger.createSubModel(event.getCharacters(), event.getTaxa());
				*/
				
				subModelBus.fireEvent(new LoadModelEvent(subModel));
				setContent(matrixView);
				setMenu(matrixMenuView);
				
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				    public void execute () {
				    	matrixView.getTaxonTreeGrid().getTreeGrid().expandAll();
				    }
				});
			}
		});
		subModelBus.addHandler(ShowDesktopEvent.TYPE, new ShowDesktopEvent.ShowDesktopEventHandler() {
			@Override
			public void onShow(ShowDesktopEvent showDesktopEvent) {
				showDesktop();
			}
		});
		subModelBus.addHandler(ToggleDesktopEvent.TYPE, new ToggleDesktopEvent.ToggleDesktopEventHandler() {
			@Override
			public void onToggle(ToggleDesktopEvent toggleDesktopEvent) {
				toggleDesktop();
			}
		});
	}

	protected void showDesktop() {
		forceLayout();
		if(getWidgetSize(desktopView) < desktopHeight) 
			setWidgetSize(desktopView, desktopHeight);
		animate(500);
	}

	protected void toggleDesktop() {
		forceLayout();
		if(getWidgetSize(desktopView) == desktopHeight) 
			setWidgetSize(desktopView, 0);
		else if(getWidgetSize(desktopView) == 0) 
			setWidgetSize(desktopView, desktopHeight);
		animate(500);
	}

	public void setFullModel(Model model) {
		this.fullModel = model;
		fullModelBus.fireEvent(new LoadModelEvent(fullModel));
	}
	
}



