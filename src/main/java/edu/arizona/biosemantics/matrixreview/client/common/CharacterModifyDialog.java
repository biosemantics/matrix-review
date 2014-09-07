package edu.arizona.biosemantics.matrixreview.client.common;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class CharacterModifyDialog extends Dialog {
			
		public CharacterModifyDialog(final EventBus eventBus, TaxonMatrix taxonMatrix, final Character character) {
			this.setHeadingText("Modify Character");	
			CharacterInformationContainer characterInformationContainer = new CharacterInformationContainer(taxonMatrix, character.getName(), character.getOrgan());
		    this.add(characterInformationContainer);
		 
		    final ComboBox<Organ> organComboBox = characterInformationContainer.getOrganComboBox();
		    final TextField characterNameField = characterInformationContainer.getCharacterNameField();
		    
		    getButtonBar().clear();
		    TextButton save = new TextButton("Save");
		    save.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					if(!characterNameField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Character Name", "A character name is required");
						alert.show();
						return;
					}
					Organ selected = organComboBox.getValue();
					eventBus.fireEvent(new ModifyCharacterEvent(character, characterNameField.getText(), selected));
					CharacterModifyDialog.this.hide();
				}
		    });
		    TextButton cancel =  new TextButton("Cancel");
		    cancel.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					CharacterModifyDialog.this.hide();
				}
		    });
		    addButton(save);
		    addButton(cancel);
		}
	
	}
	
	