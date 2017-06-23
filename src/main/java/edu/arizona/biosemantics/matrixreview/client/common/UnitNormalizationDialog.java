package edu.arizona.biosemantics.matrixreview.client.common;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.dnd.core.client.ListViewDragSource;
import com.sencha.gxt.dnd.core.client.ListViewDropTarget;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CellSelectionEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.DualListField.Mode;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.matrixreview.client.MatrixReviewView;
import edu.arizona.biosemantics.matrixreview.client.config.ManageMatrixView;
import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ShowMatrixEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.MatrixEntry;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.CharacterProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;
import edu.arizona.biosemantics.oto2.oto.client.common.SelectOntologiesDialog;
import edu.arizona.biosemantics.oto2.oto.shared.model.Ontology;
import edu.arizona.biosemantics.common.log.LogLevel;
import edu.arizona.biosemantics.matrixreview.shared.model.Unit;


public class UnitNormalizationDialog extends Dialog {

	
	protected ManageMatrixView manageMatrixView;
	
	private ModelKeyProvider<Character> myProvider = new ModelKeyProvider<Character>() {
		public String getKey(Character item) {
			return item.toString();
		}
	};
	
	
	private final CharacterProperties characterProperties = GWT.create(CharacterProperties.class);

	private Set<Character> characters;
	private List<Taxon> taxa;
	private EventBus eventBus;
	private EventBus subModelBus;
	private Model subModel;
	private Model model;
	
	

	public UnitNormalizationDialog(final EventBus eventBus, final EventBus subModelBus, final Model subModel, final Model model,final ManageMatrixView manageMatrixView) {
	    this.eventBus = eventBus;
		this.subModelBus = subModelBus;
		this.model = model;
		this.subModel = subModel;
		this.characters = subModel.getTaxonMatrix().getVisibleCharacters();//getCharacters();
		this.taxa=manageMatrixView.getSelectedTaxa();
		setHeading("Unit Normalization");
		setPredefinedButtons(PredefinedButton.OK);
		setBodyStyleName("pad-text");
		setHideOnButtonClick(false);
		setWidth("400px");
		setHeight("500px");
		this.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		getButton(PredefinedButton.OK).setText("Normalize");
		
		
		final ListStore<Normalization> gridStore = new ListStore<Normalization>(new ModelKeyProvider<Normalization>() {
			@Override
			public String getKey(Normalization item) {
				return item.character.complete();
			}
		});
		
	
		final ListStore<String> units = new ListStore<String>(new ModelKeyProvider<String>() {
	        @Override
	        public String getKey(String item) {
	          return item;
	        }
	      });
		
		  units.add("original unit");
		  for(Unit unit : Unit.values())
			  units.add(unit.toString());
		
	      ColumnConfig<Normalization, String> unitColumn = 
	    		  new ColumnConfig<Normalization, String>(new ValueProvider<Normalization, String>() {
	    				@Override
	    				public String getValue(Normalization object) {
	    					return object.getUnitSets();
	    				}
	    				@Override
	    				public void setValue(Normalization object, String value) {
	    					
	    				}

	    				@Override
	    				public String getPath() {
	    					return "unit";
	    				}
	    			}, 160, "Normalized to");
	      
	      ComboBoxCell<String> unitCombo = new ComboBoxCell<String>(units, new LabelProvider<String>() {
	          @Override
	          public String getLabel(String item) {
	            return item;
	          }
	        });
	      
	      unitCombo.addSelectionHandler(new SelectionHandler<String>() {
	          @Override
	          public void onSelection(SelectionEvent<String> event) {
	        	CellSelectionEvent<String> sel = (CellSelectionEvent<String>) event;
	            Normalization object = gridStore.get(sel.getContext().getIndex());
	            object.setUnitSets(event.getSelectedItem());
	          }
	        });
	      unitCombo.setForceSelection(true);
	      unitCombo.setTriggerAction(TriggerAction.ALL);
	      unitColumn.setCell(unitCombo);
	      
	      List<ColumnConfig<Normalization, ?>> columns = new LinkedList<ColumnConfig<Normalization, ?>>();
		
	      
	      columns.add(new ColumnConfig<Normalization, String>(new ValueProvider<Normalization, String>() {
				@Override
				public String getValue(Normalization object) {
					return object.character.complete();
				}
				@Override
				public void setValue(Normalization object, String value) {
		
				}

				@Override
				public String getPath() {
					return "character";
				}
			}, 200, "Available characters"));
			
			columns.add(unitColumn);
			
			gridStore.add(new Normalization(new Character("All listed characters below"),"original unit"));
			
			ColumnModel<Normalization> cm = new ColumnModel<Normalization>(columns);
			for (Character item: characters){
				for(Taxon taxon : model.getTaxonMatrix().getTaxa()) {
					Value value = model.getTaxonMatrix().getValue(taxon, item);
					if(!value.getValue().isEmpty()){ 
					    MatchResult matched = splitUnit(value.getValue());
					    if(matched.getGroupCount()>=2){
					    	gridStore.add(new Normalization(item,"original unit"));
					    	break;
					    }
					    
					}
				}

				
			}
			
			
			
			Grid<Normalization> grid = new Grid<Normalization>(gridStore, cm);
			
			HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
			
			hlc.add(grid, new HorizontalLayoutData(1, 1));
			
			add(hlc);
			
		
		
		/*VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		//HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();
		hlc.add(new Label("Available characters"), new HorizontalLayoutData(0.38, 10));
		hlc.add(new Label(""), new HorizontalLayoutData(40, 20));
		
		hlc.add(new Label("To be Normalized"), new HorizontalLayoutData(0.38, 20));
		hlc.add(new Label("Normalization unit"), new HorizontalLayoutData(0.26, 20));
		vlc.add(hlc, new VerticalLayoutData(1, 20));
	    lightCombo.setForceSelection(true);
	    lightCombo.setWidth(120);
		add(vlc);*/
			
			gridStore.addSortInfo(new StoreSortInfo<Normalization>(new ValueProvider<Normalization, String>() {
				@Override
				public String getValue(Normalization object) {
					return object.character.complete();
				}
				@Override
				public void setValue(Normalization object, String value) {
					
				}
				@Override
				public String getPath() {
					return "character";
				}}, SortDir.ASC));
			
			this.getButton(PredefinedButton.OK).addSelectHandler(
					new SelectHandler() {
						@Override
						public void onSelect(SelectEvent event) {
							boolean containAll=false;
						    String allUnit="";
						    List<Normalization> gridNorm = new ArrayList<Normalization>();
						    List<Normalization> gridNormAll = new ArrayList<Normalization>();
						    for (Normalization item: gridStore.getAll()){
						    	if(item.getCharacter().toString().equals("All listed characters below")){
						    		if (!item.getUnitSets().trim().equals("original unit")){
						    			containAll=true;
						    			allUnit=item.getUnitSets();
						    		}	
						    	}
							    else {
							    	gridNormAll.add(item);
								    if (!item.getUnitSets().trim().equals("original unit")){
								    	gridNorm.add(item);
							        }
							    } 
							}
						    
						    if(gridNorm.isEmpty()&&!containAll) {
						    	Alerter.sleclecUnitSets();
						    }
						    else if (gridNorm.isEmpty()&&containAll) {
						    	Alerter.finishAllNormalization();
							    unitNormalize(gridNormAll, model,allUnit);
							    hide();
							}
						    
						    else if(!gridNorm.isEmpty()&&containAll){
						    	Alerter.failedNormalization();
						    }
						    
						    else if (!gridNorm.isEmpty()&&!containAll){
						    	unitNormalize(gridNorm, model,allUnit);
							    Alerter.finishNormalization(gridNorm);
							    hide();
						    }
						    
						}

					private void unitNormalize(List<Normalization> gridNorm, Model model, String allUnit) {
						String unit="";
						if(gridNorm.isEmpty())
							return;
						for(Normalization norm: gridNorm){
							Character character = norm.getCharacter();
							if(allUnit.equals("")){
								unit = norm.getUnitSets();
							}
							else 
							    unit = allUnit;
	
							for(Taxon taxon : model.getTaxonMatrix().getTaxa()) {
								Value value = model.getTaxonMatrix().getValue(taxon, character);
								if(!value.getValue().isEmpty()){
									String newValue = getUnitValue(value.getValue(),unit);
								    if(!newValue.equals("")){
								    	SetValueEvent e = new SetValueEvent(taxon, character, 
								    			value, new Value(newValue));
								    	subModelBus.fireEvent(e);
								        eventBus.fireEvent(e);
								    }
							    }
						    }	
						}
					}	
			});
			
			this.getButton(PredefinedButton.CANCEL).addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					hide();
				}
			});
	}
				
	private MatchResult splitUnit(String value){
		MatchResult matched= RegExp.compile("(.*)\\s(nm|um|mm|cm|dm|m|km|in|ft|yd)$").exec(value);
		return matched;
	}
				
	private  String getUnitValue(String value,String unit){
		String targetUnitFactor = Unit.valueOf(unit).getToMMFactor();
		MatchResult matched = splitUnit(value);
	    //Pattern p = Pattern.compile("(.*)\\s(nm|µ|µm|mm|cm|dm|m|km|in|ft|yd)$");
		//Matcher m =p.matcher(value);
		if(matched.getGroupCount()>=2){
			//if(!matched.getGroup(1).isEmpty()&!matched.getGroup(2).isEmpty()){
			String realValue = matched.getGroup(1);
			String unitvalue = matched.getGroup(2);
			String baseUnitFactor = Unit.valueOf(unitvalue).getToMMFactor();
			BigDecimal baseUnitFactor1= new BigDecimal(baseUnitFactor);
			BigDecimal targetUnitFactor1= new BigDecimal(targetUnitFactor);
			if(isNumeric(realValue)){
				//Double doubleValue = Double.parseDouble(normalizeNumeric(realValue));		
				BigDecimal doubleValue= new BigDecimal(normalizeNumeric(realValue));
				String outputvalue=String.valueOf(doubleValue.multiply(baseUnitFactor1).divide(targetUnitFactor1));
				//unit.replaceAll("um","µm");
				return outputvalue+ " "+ unit ;
			}
						
			if(isNumericcombo(realValue)){
				//System.out.println(realValue);
				String [] valueCombo=realValue.split("[\\\\\\-;,_\\/]");
				BigDecimal doubleValue1= new BigDecimal(normalizeNumeric(valueCombo[0]));
				BigDecimal doubleValue2= new BigDecimal(normalizeNumeric(valueCombo[1]));
				String outputvalue1=String.valueOf(doubleValue1.multiply(baseUnitFactor1).divide(targetUnitFactor1));
				String outputvalue2=String.valueOf(doubleValue2.multiply(baseUnitFactor1).divide(targetUnitFactor1));
				return outputvalue1 +"/"+ outputvalue2+" "+ unit ;	
			}
			
			else return "";	
		}
		return "";
	}
				
				
	private boolean isNumeric(String value) {
		String exponential = "(E[\\+\\-]?\\d+)?$";
		// "+.3"; case or "-111.577" case
		if(value.matches("^[\\+\\-]\\d*[\\.,]\\d+" + exponential))
			return true;
		// "+1", "-4" case
		if(value.matches("^[\\+\\-]\\d+" + exponential))
			return true;
		// ".3" case or "111.577" case
		if(value.matches("^\\d*[\\.,]\\d+" + exponential))
			return true;
			// "123" case
		if(value.matches("^\\d+" + exponential))
			return true;
	    //if(value.matches(""))
		return false;
	}
				
	private boolean isNumericcombo(String value) {
		String exponential = "(E[\\+\\-]?\\d+)?";
		String punctuation = "[\\\\\\-;,_\\/]";
	    // "+.3"; case or "-111.577" case
		String [] values= new String[4];
	    values[0]="[\\+\\-]\\d*[\\.,]\\d+";
		values[1]="[\\+\\-]\\d+";
		values[2]="\\d*[\\.,]\\d+";
		values[3]="\\d+";
		for (int i=0;i<=3;i++){
			for (int j=0;j<=3;j++){
				if(value.matches("^"+values[i]+ exponential+punctuation+values[j]+ exponential +"$"))
					return true;
				}
							
			}
		return false;
	}
	
	private String normalizeNumeric(String value) {
		return value.replaceAll(",", ".");
	}


	
	  protected static final int MAX_HEIGHT = 600;
	  protected static final int MAX_WIDTH = 800;
	  protected static final int MIN_HEIGHT = 320;
	  protected static final int MIN_WIDTH = 480;
	  
	  

	

}