package edu.arizona.biosemantics.matrixreview.client.desktop.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bfr.client.selection.Selection;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DndDragStartEvent.DndDragStartHandler;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowEvent.BeforeShowHandler;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.Alerter;
import edu.arizona.biosemantics.matrixreview.client.desktop.Window;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;


public class DescriptionManager extends AbstractWindowManager {

	private Taxon taxon;
	private Model fullModel;
	private Model subModel;
	//private TextArea textArea;
	private HTML textArea;
	private Model displayedModel;
	private String selectedText;

	public DescriptionManager(EventBus fullModelEventBus, EventBus subModelEventBus, Window window, Taxon taxon, 
			Model fullModel, Model subModel) {
		super(fullModelEventBus, subModelEventBus, window);
		this.taxon = taxon;
		this.fullModel = fullModel;
		this.subModel = subModel;
		this.displayedModel = subModel;
		init();
	}
	
	@Override
	public void refreshContent() {
		
		textArea = new HTML();
		//textArea.setWidth("100px");
		//textArea.setHeight("100px");
		//textArea = new TextArea();//TextBox
		//textArea.setText(taxon.getDescription());
		//textArea.setReadOnly(true);
		//textArea.addBeforeShowContextMenuHandler(handler)
		/*
		DragSource source = new DragSource(textArea);
		source.addDragStartHandler(new DndDragStartHandler() {
			@Override
			public void onDragStart(DndDragStartEvent event) {
				event.setData(textArea.getSelectedText());
			}
		});
		*/
		textArea.sinkEvents(Event.ONCONTEXTMENU);
		textArea.addHandler(
	      new ContextMenuHandler() {
			@Override
			public void onContextMenu(ContextMenuEvent event) {
				selectedText = Selection.getBrowserRange().getText();
				//Alerter.showAlert("selected", selectedText);
				refreshContextMenu();
			}
	    }, ContextMenuEvent.getType());
		ScrollPanel sp = new ScrollPanel(textArea);
		//sp.setSize("150px", "150px");
		window.setWidget(sp);
	}
	
	public void resetContent(Taxon taxon, Value value){
		String description = taxon.getDescription();
		String valueStr = value.getValue();
		String[] values = valueStr.split("\\|");
		
		Map<String, String> replaceSents = new HashMap();
		for(String aValue:values){
			aValue = aValue.trim();
			if(aValue==null||"".equals(aValue)) continue;
			String sentence = value.getStatements(aValue);
			//Alerter.showAlert(aValue+" sources:", sentence);
			String toSentence = replaceSents.get(sentence);
			if(toSentence==null){
				replaceSents.put(sentence, sentence);
				toSentence = sentence;
			}
			if(sentence!=null&&toSentence!=null){
				//if(toSentence)
				String replacedSentence = highlighter(aValue, toSentence);
				if(replacedSentence==null){
					String[] avalueItems = aValue.split("[\\s]+");
					if(avalueItems.length>1){
						for(String item:avalueItems){
							replacedSentence =  highlighter(item, toSentence);
							if(replacedSentence!=null) toSentence = replacedSentence;
						}
					}
				}else{
					toSentence = replacedSentence;
				}
				if(toSentence!=null) replaceSents.put(sentence, toSentence);
			}else{//replace the description
				//replaceSents.put(aValue, "<span style='background:yellow'>"+aValue+"</span>");
				String replacedSentence = highlighter(aValue, description);
				if(replacedSentence!=null){
					description = replacedSentence;
				}
			}
		}
		
		for(Entry<String, String> entry : replaceSents.entrySet()){
			if(entry.getKey()!=null&&entry.getValue()!=null) description=description.replace(entry.getKey().trim(), entry.getValue().trim());
		}
		textArea.setHTML(description);
	}
	
	public String highlighter(String keywordString, String text){
		String patternString = "^"+keywordString+"\\s|\\s"+keywordString+"\\s|[\\s\\(.?,;:-×]"+keywordString+"[$\\)\\s.?,;:×-]|^"+keywordString+"$";// regular expression pattern
		//Pattern pattern = Pattern.compile(patternString);
		//Matcher matcher = pattern.matcher(text);
		RegExp regExp = RegExp.compile(patternString);
		MatchResult matcher = regExp.exec(text);
		while(matcher!=null){
            //int end = matcher.end();
           // int start = matcher.start();
            //String matchedString = text.substring(start, end);
			for (int i = 0; i < matcher.getGroupCount(); i++) {
		        String matchedString = matcher.getGroup(i);
	            return text.replace(matchedString, matchedString.replace(keywordString, "<span style='background:yellow'>"+keywordString+"</span>"));
		    }
        }
		return null;
	}
	
	public void resetContent(String description){
		textArea.setHTML(description);
	}

	@Override
	protected void addEventHandlers() {
		EventBus[] busses = { fullMatrixEventBus, subMatrixEventBus };
		for(EventBus bus : busses) {
			bus.addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
				@Override
				public void onModify(ModifyTaxonEvent event) {
					if(event.getTaxon().equals(taxon))
						refreshTitle();
				}
			});
			bus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
				@Override
				public void onLoad(LoadModelEvent event) {
					displayedModel = event.getModel();
				}
			});
		}
	}
	
	@Override
	public void refreshContextMenu() {
		Menu contextMenu = new Menu();
		MenuItem addStateItem = new MenuItem("Add State");
		contextMenu.add(addStateItem);	
		final Menu characterMenu = new Menu();
		addStateItem.setSubMenu(characterMenu);
		
		contextMenu.addBeforeShowHandler(new BeforeShowHandler() {

			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				characterMenu.clear();
				for(Organ organ : displayedModel.getTaxonMatrix().getHierarchyCharacters()) {
					String organName = organ.getName();
					if(organName==null||"".equals(organName)) organName = "whole_organism";
					MenuItem organItem = new MenuItem(organName);
					Menu sub = new Menu();
					organItem.setSubMenu(sub);
					for(final Character character : organ.getFlatCharacters()) {
						if(displayedModel.getTaxonMatrix().isVisiblyContained(character)) {
							MenuItem characterItem = new MenuItem(character.getName());
							sub.add(characterItem);
							characterItem.addSelectionHandler(new SelectionHandler<Item>() {
								@Override
								public void onSelection(SelectionEvent<Item> event) {
									//subMatrixEventBus.fireEvent(new SetValueEvent(taxon, character, 
									//		displayedModel.getTaxonMatrix().getValue(taxon, character), new Value(selectedText)));//textArea.getSelectedText()
									Value newValue = displayedModel.getTaxonMatrix().getValue(taxon, character);
									if(newValue ==null){
										newValue = new Value(selectedText); 
									}else{
										String valueStr = newValue.getValue();
										if(valueStr==null||"".equals(valueStr)){
											newValue.setValue(selectedText);
										}else{
											newValue.setValue(valueStr+"|"+selectedText);
										}
									}
									
									subMatrixEventBus.fireEvent(new SetValueEvent(taxon, character, 
											displayedModel.getTaxonMatrix().getValue(taxon, character), newValue));//textArea.getSelectedText()
									
								}
							});
						}
					}
					characterMenu.add(organItem);
				}
			}
			
		});
		
		MenuItem setStateItem = new MenuItem("Set State");
		contextMenu.add(setStateItem);	
		final Menu setCharacterMenu = new Menu();
		setStateItem.setSubMenu(setCharacterMenu);
		
		setCharacterMenu.addBeforeShowHandler(new BeforeShowHandler() {

			@Override
			public void onBeforeShow(BeforeShowEvent event) {
				setCharacterMenu.clear();
				for(Organ organ : displayedModel.getTaxonMatrix().getHierarchyCharacters()) {
					String organName = organ.getName();
					if(organName==null||"".equals(organName)) organName = "whole_organism";
					MenuItem organItem = new MenuItem(organName);
					Menu sub = new Menu();
					organItem.setSubMenu(sub);
					for(final Character character : organ.getFlatCharacters()) {
						if(displayedModel.getTaxonMatrix().isVisiblyContained(character)) {
							MenuItem characterItem = new MenuItem(character.getName());
							sub.add(characterItem);
							characterItem.addSelectionHandler(new SelectionHandler<Item>() {
								@Override
								public void onSelection(SelectionEvent<Item> event) {
									subMatrixEventBus.fireEvent(new SetValueEvent(taxon, character, 
											displayedModel.getTaxonMatrix().getValue(taxon, character), new Value(selectedText)));//textArea.getSelectedText()
								}
							});
						}
					}
					setCharacterMenu.add(organItem);
				}
			}
		});
		
		window.setContextMenu(contextMenu);
	}

	@Override
	public void refreshTitle() {
		window.setHeading("Description of " + taxon.getBiologicalName());
	}
	
	
	public static void main(String[] args){
		String keywordString ="99.9";
		String patternString = "^"+keywordString+"\\s|[\\s-]"+keywordString+"[\\s-]|[\\s\\(.?,;:-]"+keywordString+"[$\\)\\s.?,;:-]|^"+keywordString+"$";// regular expression pattern
		//Pattern pattern = Pattern.compile(patternString);
		//Matcher matcher = pattern.matcher(text);
		String text = "99.4-99.9 ";
		RegExp regExp = RegExp.compile(patternString);
		MatchResult matcher = regExp.exec(text);
		while(matcher!=null){
            //int end = matcher.end();
           // int start = matcher.start();
            //String matchedString = text.substring(start, end);
			for (int i = 0; i < matcher.getGroupCount(); i++) {
		        String matchedString = matcher.getGroup(i);
	            text =  text.replace(matchedString, matchedString.replace(keywordString, "<span style='background:yellow'>"+keywordString+"</span>"));
		    }
        }
		System.out.println(text);
	}
}
