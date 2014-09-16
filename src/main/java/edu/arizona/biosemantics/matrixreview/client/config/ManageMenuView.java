package edu.arizona.biosemantics.matrixreview.client.config;

import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuBarItem;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.MenuView;
import edu.arizona.biosemantics.matrixreview.client.ModelMerger;
import edu.arizona.biosemantics.matrixreview.client.event.AnalyzeCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class ManageMenuView extends MenuView {
	
	private ManageMatrixView manageMatrixView;
	private ModelMerger modelMerger;
	private Model model;

	public ManageMenuView(EventBus fullModelBus, EventBus subModelBus, ManageMatrixView manageMatrixView) {
		super(fullModelBus, subModelBus);
		this.manageMatrixView = manageMatrixView;
		this.modelMerger = new ModelMerger(fullModelBus, subModelBus);
	}

	@Override
	protected void addItems() {
		add(createMatrixItem());
		add(createAnnotationsItem());
		add(createAnalyzeItem());
		add(createQuestionItem());
	}

	public Widget createAnalyzeItem() {
		Menu sub = new Menu();
		MenuBarItem analyzeItem = new MenuBarItem("Analyze", sub);

		MenuItem characterItem = new MenuItem("Character");
		Menu characterSub = new Menu();
		sub.add(characterItem);
		characterItem.setSubMenu(characterSub);

		MenuItem fullMatrixItem = new MenuItem("Full Matrix");
		MenuItem subMatrixItem = new MenuItem("Selected Sub-Matrix");
		fullMatrixItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				List<Character> characters = manageMatrixView.getSelectedCharacters();
				if (characters.isEmpty()) {
					AlertMessageBox alert = new AlertMessageBox(
							"No Characters selected",
							"You have to select one or more characters to analze");
					alert.show();
				}
				for (Character character : characters)
					fullModelBus.fireEvent(new AnalyzeCharacterEvent(character,
							model));
			}
		});
		subMatrixItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> arg0) {
				List<Taxon> taxa = manageMatrixView.getSelectedTaxa();
				List<Character> characters = manageMatrixView
						.getSelectedCharacters();
				if (characters.isEmpty()) {
					AlertMessageBox alert = new AlertMessageBox(
							"No Characters selected",
							"You have to select one or more characters to analze");
					alert.show();
				} else {
					if (!taxa.isEmpty()) {
						Model subModel = modelMerger.getSubModel(characters, taxa);
						for (Character character : characters)
							fullModelBus.fireEvent(new AnalyzeCharacterEvent(
									character, subModel));
					} else {
						AlertMessageBox alert = new AlertMessageBox(
								"No Taxa selected",
								"You have to select one or more taxa which provide the data "
										+ "to analyze the selected characters");
						alert.show();
					}
				}
			}
		});
		characterSub.add(fullMatrixItem);
		characterSub.add(subMatrixItem);
		return analyzeItem;
	}
}