package edu.arizona.biosemantics.matrixreview.client;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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

import edu.arizona.biosemantics.matrixreview.client.config.ManageMatrixView;
import edu.arizona.biosemantics.matrixreview.client.config.MatrixModelControler;
import edu.arizona.biosemantics.matrixreview.client.desktop.DesktopView;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowModifyEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ToggleDesktopEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixMerger;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixView;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

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
					setContent(manageMatrixView);
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

	private TaxonMatrix fullMatrix;
	private SimpleEventBus subMatrixBus;
	private SimpleEventBus fullMatrixBus;

	private MenuView menuView = new MenuView();
	private SimpleContainer contentContainer = new SimpleContainer();
	private ManageMatrixView manageMatrixView;
	private MatrixView matrixView;
	private DesktopView desktopView;
	private int desktopHeight = 300;
	private MatrixMerger matrixMerger = new MatrixMerger();
	private MatrixModelControler matrixModelControler;

	public MatrixReviewView() {	
		subMatrixBus = new SimpleEventBus();
		fullMatrixBus = new SimpleEventBus();
		
		matrixModelControler = new MatrixModelControler(fullMatrixBus);
		manageMatrixView = new ManageMatrixView(fullMatrixBus, subMatrixBus);
		matrixView = new MatrixView(subMatrixBus);
		desktopView = new DesktopView(fullMatrixBus, subMatrixBus);

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
		contentContainer.forceLayout();
	}
		
	private void addEventHandlers() {
		fullMatrixBus.addHandler(ShowModifyEvent.TYPE, new ShowModifyEvent.ShowModifyEventHandler() {
			@Override
			public void onShow(ShowModifyEvent event) {
				setContent(manageMatrixView);
			}
		});
		fullMatrixBus.addHandler(ShowMatrixEvent.TYPE, new ShowMatrixEvent.ShowMatrixEventHandler() {
			@Override
			public void onShow(ShowMatrixEvent event) {
				TaxonMatrix taxonMatrix = matrixMerger.createSubMatrix
						(fullMatrix, event.getCharacters(), event.getTaxa());
				subMatrixBus.fireEvent(new LoadTaxonMatrixEvent(taxonMatrix));
				setContent(matrixView);
			}
		});
		subMatrixBus.addHandler(ShowDesktopEvent.TYPE, new ShowDesktopEvent.ShowDesktopEventHandler() {
			@Override
			public void onShow(ShowDesktopEvent showDesktopEvent) {
				showDesktop();
			}
		});
		subMatrixBus.addHandler(ToggleDesktopEvent.TYPE, new ToggleDesktopEvent.ToggleDesktopEventHandler() {
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

	public void setFullMatrix(TaxonMatrix fullMatrix) {
		this.fullMatrix = fullMatrix;
		fullMatrixBus.fireEvent(new LoadTaxonMatrixEvent(fullMatrix));
	}
	
}



