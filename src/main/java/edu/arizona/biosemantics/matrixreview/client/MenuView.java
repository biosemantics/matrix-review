package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.ColorSettingsDialog;
import edu.arizona.biosemantics.matrixreview.client.common.ColorsDialog;
import edu.arizona.biosemantics.matrixreview.client.common.CommentsDialog;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SaveEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowModifyEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class MenuView extends MenuBar {

	protected Model model;
	protected EventBus fullModelBus;
	protected EventBus subModelBus;

	public MenuView(EventBus fullModelBus, EventBus subModelBus) {
		this.fullModelBus = fullModelBus;
		this.subModelBus = subModelBus;
		addStyleName(ThemeStyles.get().style().borderBottom());
		addItems();
		
		bindEvents();
	}

	private void bindEvents() {
		fullModelBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
			@Override
			public void onLoad(LoadModelEvent event) {
				model = event.getModel();
			}
		});
	}

	protected void addItems() {
		add(createMatrixItem());
		add(createAnnotationsItem());
		add(createQuestionItem());
	}

	protected Widget createMatrixItem() {
		Menu sub = new Menu();

		MenuBarItem matrixItem = new MenuBarItem("Matrix", sub);

		/*
		 * MenuItem subMatrixItem = new MenuItem("Load");
		 * subMatrixItem.addSelectionHandler(new SelectionHandler<Item>() {
		 * 
		 * @Override public void onSelection(SelectionEvent<Item> event) { final
		 * SelectMatrixDialog selectMatrixDialog = new
		 * SelectMatrixDialog(fullMatrix); selectMatrixDialog.show();
		 * selectMatrixDialog.addHideHandler(new HideHandler() {
		 * 
		 * @Override public void onHide(HideEvent event) {
		 * if(!selectMatrixDialog.getSelectedCharacters().isEmpty() &&
		 * !selectMatrixDialog.getSelectedRootTaxa().isEmpty()) { TaxonMatrix
		 * taxonMatrix = matrixMerger.createSubMatrix(fullMatrix,
		 * selectMatrixDialog.getSelectedCharacters(),
		 * selectMatrixDialog.getSelectedRootTaxa()); subMatrixBus.fireEvent(new
		 * LoadTaxonMatrixEvent(taxonMatrix)); setContent(matrixView); } } }); }
		 * });
		 */

		MenuItem modifyMatrixItem = new MenuItem("Configure");
		modifyMatrixItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fullModelBus.fireEvent(new ShowModifyEvent(model));
			}
		});

		MenuItem exportItem = new MenuItem("Save");
		exportItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fullModelBus.fireEvent(new SaveEvent(model));
			}
		});

		// sub.add(subMatrixItem);
		sub.add(modifyMatrixItem);
		sub.add(exportItem);
		return matrixItem;
	}

	protected Widget createAnnotationsItem() {
		Menu sub = new Menu();
		MenuBarItem annotationsItem = new MenuBarItem("Annotation", sub);
		MenuItem colorSettingsItem = new MenuItem("Color Settings");
		colorSettingsItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				ColorSettingsDialog dialog = new ColorSettingsDialog(
						fullModelBus, model);
				dialog.show();
			}
		});
		sub.add(colorSettingsItem);
		MenuItem colorsItem = new MenuItem("Colorations");
		colorsItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				ColorsDialog dialog = new ColorsDialog(fullModelBus,
						subModelBus, model);
				dialog.show();
			}
		});
		sub.add(colorsItem);
		MenuItem commentsItem = new MenuItem("Comments");
		commentsItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				CommentsDialog dialog = new CommentsDialog(fullModelBus,
						subModelBus, model);
				dialog.show();
			}
		});
		sub.add(commentsItem);
		return annotationsItem;
	}

	protected Widget createQuestionItem() {
		Menu sub = new Menu();
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
		return questionsItem;
	}
}