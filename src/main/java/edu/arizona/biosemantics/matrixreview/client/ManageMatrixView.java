package edu.arizona.biosemantics.matrixreview.client;

import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import edu.arizona.biosemantics.matrixreview.client.event.ShowMatrixEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class ManageMatrixView extends VerticalLayoutContainer {

	public ManageMatrixView(final EventBus fullMatrixBus, final EventBus subMatrixBus) {
		final ManageTaxaView taxaView = new ManageTaxaView(fullMatrixBus, false);
		final ManageCharactersView charactersView = new ManageCharactersView(fullMatrixBus, false);
		
		HorizontalLayoutContainer horizontalLayoutContainer = new HorizontalLayoutContainer();
		horizontalLayoutContainer.add(taxaView, new HorizontalLayoutData(0.5, 1.0));
		horizontalLayoutContainer.add(charactersView, new HorizontalLayoutData(0.5, 1.0));
		add(horizontalLayoutContainer, new VerticalLayoutData(1.0, 1.0));
		
		
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setMinButtonWidth(75);
		buttonBar.setPack(BoxLayoutPack.END);
		//taxaButtonBar.setVisible(false);
		TextButton loadButton = new TextButton("Load Selection");
		loadButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final List<Taxon> taxa = taxaView.getSelectedTaxa();
				final List<Character> characters = charactersView.getSelectedCharacters();
				final List<Organ> organs = charactersView.getSelectedOrgans();
				if(!taxa.isEmpty() && !characters.isEmpty()) {
					 ConfirmMessageBox box = new ConfirmMessageBox("Loading Matrix", getLoadMessage(taxa, characters, organs));
					 box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							fullMatrixBus.fireEvent(new ShowMatrixEvent(taxa, characters));
						}
					 });
			         box.show();
				} else {
					AlertMessageBox alert = new AlertMessageBox("Load impossible", "You have to select at least one taxon and character");
					alert.show();
				}
			}
		});
		buttonBar.add(loadButton);
		add(buttonBar, new VerticalLayoutData(1.0, -1.0));
	}

	protected String getLoadMessage(List<Taxon> taxa, List<Character> characters, List<Organ> organs) {
		return "Do you want to continue loading a sub-matrix with the following dimensions?</br></br>" +
				"<table><tr><td><p><b>Selected Taxa: </b></td><td>" + taxa.size() + "</p></td></tr>" +
		"<tr><td><p><b>Selected Characters: </b></td><td>" + characters.size() + " (of " + organs.size() + " organ(s))</p></tr></td>" +
		"<tr><td><p><b>Matrix values: </b></td><td>" + taxa.size() * characters.size() + "</p></tr></td>";
	}
	
	
	
}
