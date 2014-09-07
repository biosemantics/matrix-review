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
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class CharacterAddDialog extends Dialog {

	private Character after = null;

	public CharacterAddDialog(final EventBus eventBus, TaxonMatrix taxonMatrix,
			Organ initialOrgan) {
		this.setHeadingText("Add Character");
		CharacterInformationContainer characterInformationContainer = new CharacterInformationContainer(
				taxonMatrix, "", initialOrgan);
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

				Organ selected = organComboBox.getValue();
				Character newCharacter = new Character(characterNameField
						.getText(), selected);
				eventBus.fireEvent(new AddCharacterEvent(selected, after,
						newCharacter));
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
