package edu.arizona.biosemantics.matrixreview.client.config;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.sencha.gxt.core.shared.ExpandedHtmlSanitizer;
import com.sencha.gxt.widget.core.client.ContentPanel;
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
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowMatrixEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.MatrixEntry;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.OrganCharacterNode.CharacterNode;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class ManageMatrixView extends VerticalLayoutContainer {
	
	private EventBus fullModelBus;
	private Model model;
	private ManageTaxaView taxaView;
	private ManageCharactersView charactersView;
	protected List<Taxon> taxaSelection = new LinkedList<Taxon>();
	protected List<OrganCharacterNode> characterSelection = new LinkedList<OrganCharacterNode>();

	public ManageMatrixView(final EventBus fullModelBus, final EventBus subModelBus) {
		this.fullModelBus = fullModelBus;
		
		taxaView = new ManageTaxaView(fullModelBus, false, this);
		charactersView = new ManageCharactersView(fullModelBus, false, this);
		
		HorizontalLayoutContainer horizontalLayoutContainer = new HorizontalLayoutContainer();
		horizontalLayoutContainer.add(taxaView, new HorizontalLayoutData(0.5, 1.0));
		horizontalLayoutContainer.add(charactersView, new HorizontalLayoutData(0.5, 1.0));
		add(horizontalLayoutContainer, new VerticalLayoutData(1.0, 1.0));
		
		ButtonBar buttonBar = new ButtonBar();
		buttonBar.setMinButtonWidth(75);
		buttonBar.setPack(BoxLayoutPack.CENTER);
		TextButton loadButton = new TextButton("Load");
		loadButton.setTitle("Load Selected Taxa and Characters in a matrix");
		loadButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				final List<Taxon> taxa = taxaView.getSelectedTaxa();
				final List<Character> characters = charactersView.getSelectedCharacters();
				final LinkedHashSet<Organ> organs = charactersView.getSelectedOrgans();
				if(!taxa.isEmpty() && !characters.isEmpty()) {
					 ConfirmMessageBox box = new ConfirmMessageBox(SafeHtmlUtils.fromString("Loading Matrix")
							 , getLoadMessage(taxa, characters, organs));
					 box.getButton(PredefinedButton.YES).addSelectHandler(new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							fullModelBus.fireEvent(new ShowMatrixEvent(taxa, characters));
						}
					 });
			         box.show();
				} else {
					AlertMessageBox alert = new AlertMessageBox("Nothing to Load", "You need to select at least one taxon and one character");
					alert.show();
				}
			}
		});
		
		/*TextButton analyzeButton = new TextButton("Analyze Selected Taxa/Characters");
		analyzeButton.setTitle("Analyze Selected Taxa/Characters");
		analyzeButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				List<Taxon> taxaSelection = taxaView.getSelectedTaxa();
				List<Character> characterSelection = charactersView.getSelectedCharacters(); 
				
				Model subModel = null;
				if(!taxaSelection.isEmpty()) 
					subModel = modelMerger.getSubModel(characterSelection, taxaSelection);
				else
					subModel = modelMerger.getFullModel();
				
				for(Character character : characterSelection)
					fullModelBus.fireEvent(new AnalyzeCharacterEvent(character, subModel));
			}
		});*/	
		
		buttonBar.add(loadButton);
		
		ContentPanel contentPanel = new ContentPanel();
		contentPanel.setHeading("Load Selected Taxa and Characters");
		contentPanel.add(buttonBar);
		add(contentPanel, new VerticalLayoutData(1.0, -1.0));
		
		bindEvents();
	}

	
	private void bindEvents() {
		fullModelBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
			@Override
			public void onLoad(LoadModelEvent event) {
				model = event.getModel();
			}
		});
		charactersView.addSelectionChangeHandler(new SelectionChangedHandler<OrganCharacterNode>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<OrganCharacterNode> event) {
				characterSelection = event.getSelection();
				updateValueField();
			}
		});
		taxaView.addSelectionChangeHandler(new SelectionChangedHandler<Taxon>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<Taxon> event) {
				taxaSelection = event.getSelection();
				updateValueField();
			}
		});
		fullModelBus.addHandler(SetValueEvent.TYPE, new SetValueEvent.SetValueEventHandler() {
			@Override
			public void onSet(SetValueEvent event) {
				updateValueField();
			}
		});
		fullModelBus.addHandler(SetValueColorEvent.TYPE, new SetValueColorEvent.SetValueColorEventHandler() {
			@Override
			public void onSet(SetValueColorEvent event) {
				updateValueField();
			}
		});
	}

	protected void updateValueField() {
		MatrixEntry entry = getCurrentSelectedMatrixEntry();
		if(entry != null)
			charactersView.setMatrixEntry(getCurrentSelectedMatrixEntry());
	}

	public MatrixEntry getCurrentSelectedMatrixEntry() {
		if(taxaSelection.size() >= 1 && characterSelection.size() >= 1) {
			Taxon lastSelectedTaxon = taxaSelection.get(taxaSelection.size() - 1);
			OrganCharacterNode lastSelectedOrganCharacterNode = characterSelection.get(characterSelection.size() - 1);
			if(lastSelectedOrganCharacterNode instanceof CharacterNode) {
				Character character = ((CharacterNode)lastSelectedOrganCharacterNode).getCharacter();
				Value value = model.getTaxonMatrix().getValue(lastSelectedTaxon, character);
				return new MatrixEntry(lastSelectedTaxon, character, value);
			}
		}
		return null;
	}

	protected SafeHtml getLoadMessage(List<Taxon> taxa, List<Character> characters, LinkedHashSet<Organ> organs) {
		StringBuffer sb = new StringBuffer("Continue loading a matrix with the selected taxa and characters?</br></br>");
		sb.append("<ol>");
		sb.append("<li><b>Selected Taxa: </b>"+ taxa.size() +"</li>");
		sb.append("<li><b>Selected Characters: </b>"+ characters.size() + " (of " + organs.size() + " organ(s))</li>");
		sb.append("<li><b>Matrix values: </b>"+ taxa.size() * characters.size()+"</li>");
		sb.append("</ol>");
		return ExpandedHtmlSanitizer.sanitizeHtml(sb.toString());
		/*return "" +
				"<table><tr><td><p><b>Selected Taxa: </b></td><td>" + taxa.size() + "</p></td></tr>" +
		"<tr><td><p><b>Selected Characters: </b></td><td>" + characters.size() + " (of " + organs.size() + " organ(s))</p></tr></td>" +
		"<tr><td><p><b>Matrix values: </b></td><td>" + taxa.size() * characters.size() + "</p></tr></td>";
		*/
	}

	public List<Character> getSelectedCharacters() {
		return charactersView.getSelectedCharacters();
	}
	
	public List<Taxon> getSelectedTaxa() {
		return taxaView.getSelectedTaxa();
	}
		
}
