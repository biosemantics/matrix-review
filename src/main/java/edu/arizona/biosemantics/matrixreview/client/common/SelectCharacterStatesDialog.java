package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.DualListField.Mode;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;

import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterStatesEvent.SetCharacterStatesEventHandler;

import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;

public class SelectCharacterStatesDialog extends Dialog {

	public HandlerRegistration addSetCharacterStatesEventHandler(
			SetCharacterStatesEventHandler handler) {
		return addHandler(handler, SetCharacterStatesEvent.TYPE);
	}

	public SelectCharacterStatesDialog(final Character character,
			List<String> states) {
		setBodyBorder(false);
		setHeading("Categorical States");
		setWidth(500);
		setHeight(240);
		setHideOnButtonClick(true);
		setModal(true);
		setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		
		VerticalLayoutContainer verticalContainer = new VerticalLayoutContainer();
		this.add(verticalContainer, new MarginData(10));

		final ListStore<String> fromStates = new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(String item) {
						return item;
					}
				});
		fromStates.addAll(states);
		final ListStore<String> toStates = new ListStore<String>(
				new ModelKeyProvider<String>() {
					@Override
					public String getKey(String item) {
						return item;
					}
				});
		final DualListField<String, String> field = new DualListField<String, String>(
				fromStates, toStates, new ValueProvider<String, String>() {
					@Override
					public String getValue(String object) {
						return object;
					}

					@Override
					public void setValue(String object, String value) {
						object = value;
					}

					@Override
					public String getPath() {
						return "node";
					}
				}, new TextCell());
		field.addValidator(new EmptyValidator<List<String>>());
		field.setEnableDnd(true);
		field.setMode(Mode.INSERT);

		verticalContainer.add(new FieldLabel(field, "States"), new VerticalLayoutData(1.0, -1.0));
		HorizontalLayoutContainer horizontalContainer = new HorizontalLayoutContainer();
		final TextField addField = new TextField();
		TextButton addButton = new TextButton("Add");
		horizontalContainer.add(addField);
		horizontalContainer.add(addButton);
		verticalContainer.add(new FieldLabel(horizontalContainer, "Add States"), new VerticalLayoutData(1.0, -1.0));
		addButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				toStates.add(addField.getText());
				addField.reset();
			}
		});

		TextButton okButton = this.getButton(PredefinedButton.OK);
		okButton.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				List<String> states = toStates.getAll();
				if (!states.isEmpty()) {
					SelectCharacterStatesDialog.this
							.fireEvent(new SetCharacterStatesEvent(character,
									new ArrayList<String>(toStates.getAll())));
					SelectCharacterStatesDialog.this.hide();
				} else {
					AlertMessageBox alert = new AlertMessageBox(
							"No State selected", "At least one state requried");
					alert.show();
				}
			}
		});
	}

}
