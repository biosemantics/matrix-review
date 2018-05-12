package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.List;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeMode;

public class MergeDialog extends Dialog {

	private class MergeModeProperties {
		public ModelKeyProvider<MergeMode> key() {
			return new ModelKeyProvider<MergeMode>() {
				@Override
				public String getKey(MergeMode item) {
					return item.name();
				}
			};
		}

		public LabelProvider<MergeMode> name() {
			return new LabelProvider<MergeMode>() {
				@Override
				public String getLabel(MergeMode item) {
					return item.name();
				}
			};
		}
	}
	
	private EventBus eventBus;
	private Model model;

	public MergeDialog(final EventBus eventBus, Model model, final Character characterA, final List<Character> characterBs) {
		this.eventBus = eventBus;
		this.model = model;

		MergeModeProperties props = new MergeModeProperties();
	    ListStore<MergeMode> mergeModesStore = new ListStore<MergeMode>(props.key());
	    for (MergeMode mode : MergeMode.values())
	    	mergeModesStore.add(mode);
		final ComboBox<MergeMode> controlModeCombo = new ComboBox<MergeMode>(mergeModesStore, props.name());
		controlModeCombo.setValue(MergeMode.MIX);
		controlModeCombo.setAllowBlank(false);
		controlModeCombo.setForceSelection(true);
		controlModeCombo.setTriggerAction(TriggerAction.ALL);
		controlModeCombo.addSelectionHandler(new SelectionHandler<MergeMode>() {
			@Override
			public void onSelection(SelectionEvent<MergeMode> event) {
		
			}
		});
		
		FieldSet fieldSet = new FieldSet();
	    fieldSet.setHeading("Merge Mode");
	    fieldSet.setCollapsible(false);
	    this.add(fieldSet, new MarginData(10));
	    
	    VerticalLayoutContainer p = new VerticalLayoutContainer();
	    fieldSet.add(p);
	    p.add(new Label("Select Merge Mode"));
	    p.add(controlModeCombo);
		
		setBodyBorder(false);
		setHeading("Merge");
		setHideOnButtonClick(true);
		this.setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		this.getButton(PredefinedButton.OK).addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				for(Character characterB : characterBs) 
					eventBus.fireEvent(new MergeCharactersEvent(characterA, characterB, controlModeCombo.getValue()));
			}
		});
	}

}
