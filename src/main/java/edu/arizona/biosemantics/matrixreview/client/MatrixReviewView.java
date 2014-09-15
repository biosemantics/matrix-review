package edu.arizona.biosemantics.matrixreview.client;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.user.client.ui.AnimatedLayout;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.ColorSettingsDialog;
import edu.arizona.biosemantics.matrixreview.client.common.ColorsDialog;
import edu.arizona.biosemantics.matrixreview.client.common.CommentsDialog;
import edu.arizona.biosemantics.matrixreview.client.config.ManageMatrixView;
import edu.arizona.biosemantics.matrixreview.client.config.ModelControler;
import edu.arizona.biosemantics.matrixreview.client.desktop.DesktopView;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowModifyEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ToggleDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class MatrixReviewView extends SplitLayoutPanel {

	public class MenuView extends MenuBar {

		public MenuView() {
			addStyleName(ThemeStyles.get().style().borderBottom());
	
			Menu sub = new Menu();
			
			MenuBarItem item = new MenuBarItem("Matrix", sub);
			
			/*
			MenuItem subMatrixItem = new MenuItem("Load");
			subMatrixItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					final SelectMatrixDialog selectMatrixDialog = new SelectMatrixDialog(fullMatrix);
					selectMatrixDialog.show();
					selectMatrixDialog.addHideHandler(new HideHandler() {
						@Override
						public void onHide(HideEvent event) {
							if(!selectMatrixDialog.getSelectedCharacters().isEmpty() && !selectMatrixDialog.getSelectedRootTaxa().isEmpty()) {
								TaxonMatrix taxonMatrix = matrixMerger.createSubMatrix(fullMatrix,
										selectMatrixDialog.getSelectedCharacters(), 
										selectMatrixDialog.getSelectedRootTaxa());		
								subMatrixBus.fireEvent(new LoadTaxonMatrixEvent(taxonMatrix));
								setContent(matrixView);
							}
						}
					});
				}
			}); */
			
			MenuItem modifyMatrixItem = new MenuItem("Configure");
			modifyMatrixItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					fullModelBus.fireEvent(new ShowModifyEvent(fullModel));
				}
			});
			
			MenuItem exportItem = new MenuItem("Export");
			exportItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {}
			});

			//sub.add(subMatrixItem);
			sub.add(modifyMatrixItem);
			sub.add(exportItem);
			add(item);
			
			sub = new Menu();
			MenuBarItem annotationsItem = new MenuBarItem("Annotation", sub);
			MenuItem colorSettingsItem = new MenuItem("Color Settings");
			colorSettingsItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> arg0) {
					ColorSettingsDialog dialog = new ColorSettingsDialog(fullModelBus, fullModel);
					dialog.show();
				}
			});
			sub.add(colorSettingsItem);
			MenuItem colorsItem = new MenuItem("Colorations");
			colorsItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> arg0) {
					ColorsDialog dialog = new ColorsDialog(fullModelBus, fullModel);
					dialog.show();
				}
			});
			sub.add(colorsItem);
			MenuItem commentsItem = new MenuItem("Comments");
			commentsItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> arg0) {
					CommentsDialog dialog = new CommentsDialog(fullModelBus, fullModel);
					dialog.show();
				}
			});
			sub.add(commentsItem);
			add(annotationsItem);
			
			
			sub = new Menu();
			MenuBarItem questionsItem = new MenuBarItem("?", sub);
			MenuItem helpItem = new MenuItem("Help");
			helpItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> arg0) {
					final Dialog dialog = new Dialog();
					dialog.setBodyBorder(false);
					dialog.setHeadingText("Help");
					dialog.setHideOnButtonClick(true);
					dialog.setWidget(new HelpView());
					dialog.setWidth(400);
					dialog.setHeight(225);
					dialog.setResizable(false);
					dialog.setShadow(true);
					dialog.show();
				}
			});
			sub.add(helpItem);
			add(questionsItem);
		}
	}

	private Model fullModel;
	private Model subModel;
	private Model subModelOriginal;
	private SimpleEventBus subModelBus;
	private SimpleEventBus fullModelBus;

	private MenuView menuView = new MenuView();
	private SimpleContainer contentContainer = new SimpleContainer();
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

		VerticalLayoutContainer verticalLayoutContainer = new VerticalLayoutContainer();
		verticalLayoutContainer.add(menuView, new VerticalLayoutData(1,-1));
		verticalLayoutContainer.add(contentContainer, new VerticalLayoutData(1,1));
		setContent(manageMatrixView);
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
				modelMerger.commitEvents();
				fullModelBus.fireEvent(new LoadModelEvent(fullModel));
				setContent(manageMatrixView);
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



