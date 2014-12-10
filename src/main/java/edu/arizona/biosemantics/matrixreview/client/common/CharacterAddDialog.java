package edu.arizona.biosemantics.matrixreview.client.common;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;

public class CharacterAddDialog extends Dialog {

	private Character after = null;

	public CharacterAddDialog(final EventBus eventBus, Model model,
			Organ initialOrgan) {
		this.setHeadingText("Add Character");
		CharacterInformationContainer characterInformationContainer = new CharacterInformationContainer(
				model, "", initialOrgan);
		this.add(characterInformationContainer);

		final ComboBox<Organ> organComboBox = characterInformationContainer
				.getOrganComboBox();
		final TextField characterNameField = characterInformationContainer
				.getCharacterNameField();

		getButtonBar().clear();
		TextButton add = new TextButton("Add");
		add.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if (!characterNameField.validate()) {
					AlertMessageBox alert = new AlertMessageBox(
							"Character Name", "A character name is required");
					alert.show();
					return;
				}

				Organ organ = organComboBox.getValue();
				int afterIndex = organ.getFlatCharacters().indexOf(after) ;
				int organindex = afterIndex == -1 ? 0 : afterIndex + 1;
				Character newCharacter = new Character(characterNameField
						.getText(), "of", organ, organindex);
				eventBus.fireEvent(new AddCharacterEvent(organ, newCharacter, after));
				//eventBus.fireEvent(new some move event that moves "after");
				CharacterAddDialog.this.hide();
			}
		});
		TextButton cancel = new TextButton("Cancel");
		cancel.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				CharacterAddDialog.this.hide();
			}
		});
		addButton(add);
		addButton(cancel);
	}

	public void setAfter(Character after) {
		this.after = after;
	}
}
