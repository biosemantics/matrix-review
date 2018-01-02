package edu.arizona.biosemantics.matrixreview.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.HeaderMenuItem;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBar;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.ColorSettingsDialog;
import edu.arizona.biosemantics.matrixreview.client.common.ColorsDialog;
import edu.arizona.biosemantics.matrixreview.client.common.CommentsDialog;
import edu.arizona.biosemantics.matrixreview.client.common.UnitNormalizationDialog;
import edu.arizona.biosemantics.matrixreview.client.config.ManageMatrixView;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.DownloadEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SaveEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowModifyEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixFormat;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.oto2.oto.client.common.SelectOntologiesDialog;

public class MenuView extends MenuBar {

	protected Model model;
	protected EventBus fullModelBus;
	protected EventBus subModelBus;
	protected ModelMerger modelMerger;
	protected ManageMatrixView manageMatrixView;

	public MenuView(EventBus fullModelBus, EventBus subModelBus, ModelMerger modelMerger, ManageMatrixView manageMatrixView) {
		this.fullModelBus = fullModelBus;
		this.subModelBus = subModelBus;
		this.modelMerger = modelMerger;
		this.manageMatrixView = manageMatrixView;
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
		add(createNormalizationItem());
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

		MenuItem modifyMatrixItem = new MenuItem("Go to Preview and Selection Interface");
		modifyMatrixItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fullModelBus.fireEvent(new ShowModifyEvent(model));
			}
		});

		MenuItem saveItem = new MenuItem("Save Progress");
		saveItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fullModelBus.fireEvent(new SaveEvent(model));
			}
		});
		
		MenuItem downloadItem = new MenuItem("Download Matrix (with all existing taxa and characters)");
		downloadItem.setTitle("please set your browser to allow popup windows to use this function");
		downloadItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fullModelBus.fireEvent(new DownloadEvent(model));
			}
		});
		
		//Hong todo
		MenuItem downloadSourceItem = new MenuItem("Download Matrix (with all existing taxa and characters, plus source sentences)");
		downloadSourceItem.setTitle("please set your browser to allow popup windows to use this function");
		downloadSourceItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fullModelBus.fireEvent(new DownloadEvent(model,MatrixFormat.CSVS));
			}
		});
		
		/*no significant difference from simple csv
		MenuItem downloadMCCSVItem = new MenuItem("Download MatrixConverter Matrix (with all existing taxa and characters)");
		downloadMCCSVItem.setTitle("please set your browser to allow popup windows to use this function");
		downloadMCCSVItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				fullModelBus.fireEvent(new DownloadEvent(model, MatrixFormat.MCCSV));
			}
		});
		*/
		
		MenuItem downloadSelectionItem = new MenuItem("Download Selected Part of Matrix");
		downloadSelectionItem.setTitle("please set your browser to allow popup windows to use this function");
		downloadSelectionItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Model subModel = modelMerger.getSubModel(manageMatrixView.getSelectedCharacters(), manageMatrixView.getSelectedTaxa());
				//subModel.getTaxonMatrix().
				fullModelBus.fireEvent(new DownloadEvent(subModel));
			}
		});
		
		//Hong: todo
		MenuItem downloadSelectionSourceItem = new MenuItem("Download Selected Part of Matrix, plus source sentences");
		downloadSelectionSourceItem.setTitle("please set your browser to allow popup windows to use this function");
		downloadSelectionSourceItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Model subModel = modelMerger.getSubModel(manageMatrixView.getSelectedCharacters(), manageMatrixView.getSelectedTaxa());
				//subModel.getTaxonMatrix().
				fullModelBus.fireEvent(new DownloadEvent(subModel,MatrixFormat.CSVS));
			}
		});
		
		/*
		MenuItem downloadSelectionItemAsMC = new MenuItem("Download Selected Part of MatrixConverter Matrix");
		downloadSelectionItemAsMC.setTitle("please set your browser to allow popup windows to use this function");
		downloadSelectionItemAsMC.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				Model subModel = modelMerger.getSubModel(manageMatrixView.getSelectedCharacters(), manageMatrixView.getSelectedTaxa());
				//subModel.getTaxonMatrix().
				fullModelBus.fireEvent(new DownloadEvent(subModel, MatrixFormat.MCCSV));
			}
		});
		*/

		// sub.add(subMatrixItem);
		sub.add(modifyMatrixItem);
		sub.add(saveItem);
		sub.add(downloadItem);
		//sub.add(downloadMCCSVItem);
		sub.add(downloadSelectionItem);
		//sub.add(downloadSelectionItemAsMC);
		return matrixItem;
	}

	protected Widget createAnnotationsItem() {
		Menu sub = new Menu();
		MenuBarItem annotationsItem = new MenuBarItem("Annotation", sub);
		sub.add(new HeaderMenuItem("Configure"));
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
		sub.add(new HeaderMenuItem("Show"));
		MenuItem colorsItem = new MenuItem("Color Use on Taxa/Characters");
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
	
	protected Widget createNormalizationItem() {
		Menu sub = new Menu();
		MenuBarItem normalizationItem = new MenuBarItem("Normalization", sub);
		sub.add(new HeaderMenuItem("Unit Normalization"));
		MenuItem normalizationSelectedItem = new MenuItem("Select Characters to be Normalized");
		normalizationSelectedItem.addSelectionHandler(new SelectionHandler<Item>() {
				@Override
				public void onSelection(SelectionEvent<Item> event) {
					Model subModel = modelMerger.getSubModel(manageMatrixView.getSelectedCharacters(), manageMatrixView.getSelectedTaxa());
					UnitNormalizationDialog dialog = new UnitNormalizationDialog(fullModelBus,
							subModelBus, subModel,model,manageMatrixView);
					dialog.show();
				}
			});
		sub.add(normalizationSelectedItem);
		return normalizationItem;
	}
}
