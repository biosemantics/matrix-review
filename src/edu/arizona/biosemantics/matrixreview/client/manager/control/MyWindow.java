package edu.arizona.biosemantics.matrixreview.client.manager.control;

import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.DualListField;
import com.sencha.gxt.widget.core.client.form.DualListField.Mode;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class MyWindow extends Window {

	public HandlerRegistration addSaveHandler(SaveCategoriesHandler handler) {
		return addHandler(handler, SaveCategoriesEvent.getType());
	}
	
	public MyWindow(Character character, List<String> states) {
		this.setMaximizable(true);
		this.setModal(true);
		this.setHeadingText("Categorial States");
		this.setWidth(500);
		this.setHeight(200);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		this.add(con, new MarginData(10));
		
		final ListStore<String> fromStates = new ListStore<String>(new ModelKeyProvider<String>() {
			@Override
			public String getKey(String item) {
				return item;
			}
		});
		fromStates.addAll(states);
		final ListStore<String> toStates = new ListStore<String>(new ModelKeyProvider<String>() {
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

		con.add(new FieldLabel(field, "States"), new VerticalLayoutData(-18, -1));
		
		Button saveButton = new Button("Save");
		saveButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MyWindow.this.fireEvent(new SaveCategoriesEvent(toStates.getAll()));
				MyWindow.this.hide();
			}
		});
		con.add(saveButton);
	}

}