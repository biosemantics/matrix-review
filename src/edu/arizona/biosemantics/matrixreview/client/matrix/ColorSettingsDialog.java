package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ColorPalette;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;

import edu.arizona.biosemantics.matrixreview.client.event.AddColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveColorsEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.cells.ColorCell;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class ColorSettingsDialog extends Dialog {

	public ColorSettingsDialog(final EventBus eventBus, final TaxonMatrix taxonMatrix) {
		final CellTable<Color> colorsTable = new CellTable<Color>();

		this.setBodyBorder(false);
		this.setHeadingText("BorderLayout Dialog");
		this.setWidth(600);
		this.setHeight(275);
		this.setHideOnButtonClick(true);

		BorderLayoutContainer layout = new BorderLayoutContainer();
		this.add(layout);

		// Layout - west
		ContentPanel westPanel = new ContentPanel();
		westPanel.setHeadingText("Create Color");
		BorderLayoutData data = new BorderLayoutData(150);
		data.setMargins(new Margins(0, 5, 0, 0));
		westPanel.setLayoutData(data);
		VerticalPanel colorChoosePanel = new VerticalPanel();
		ColorPalette colorPalette = new ColorPalette();
		colorChoosePanel.add(colorPalette);
		final TextField textField = new TextField();
		final Label colorLabel = new Label();
		colorLabel.setWidth("30px");
		colorLabel.setHeight("30px");
		colorChoosePanel.add(textField);
		colorChoosePanel.add(colorLabel);
		colorPalette.addSelectionHandler(new SelectionHandler<String>() {
			@Override
			public void onSelection(SelectionEvent<String> event) {
				textField.setValue(event.getSelectedItem());
				textField.validate();
			}
		});
		textField.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				textField.validate();
			}
		});
		textField.addValidator(new Validator<String>() {
			@Override
			public List<EditorError> validate(Editor<String> editor,
					String value) {
				List<EditorError> error = new LinkedList<EditorError>();
				RegExp hexPattern = RegExp.compile("^([A-Fa-f0-9]{6})$");
				if (!hexPattern.test(textField.getValue())) {
					error.add(new DefaultEditorError(editor,
							"Not a valid color code", value));
				} else {
					String text = textField.getText();
					if (text.length() == 6) {
						colorLabel.getElement().getStyle()
								.setBackgroundColor("#" + text);
					}
				}
				return error;
			}
		});
		westPanel.add(colorChoosePanel);
		Button addButton = new Button("add");
		westPanel.addButton(addButton);
		addButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				List<Color> colors = taxonMatrix.getColors();
				Color color = new Color(textField.getText(), "");
				colors.add(color);
				colorsTable.setRowData(colors);
				eventBus.fireEvent(new AddColorEvent(color));
			}
		});

		BorderLayoutData westLayoutData = new BorderLayoutData(200);
		westLayoutData.setCollapsible(true);
		westLayoutData.setSplit(true);
		westLayoutData.setCollapseMini(true);
		westLayoutData.setMargins(new Margins(0, 8, 0, 5));
		layout.setWestWidget(westPanel, westLayoutData);

		// Layout - center
		ContentPanel centerPanel = new ContentPanel();
		centerPanel.setHeadingText("Available colors");
		layout.setCenterWidget(centerPanel);

		final CheckboxCell checkboxCell = new CheckboxCell();
		Column<Color, Boolean> checkColumn = new Column<Color, Boolean>(
				checkboxCell) {
			@Override
			public Boolean getValue(Color object) {
				return false;
			}
		};
		final ColorCell customCell = new ColorCell();
		Column<Color, String> colorColumn = new Column<Color, String>(
				customCell) {
			@Override
			public String getValue(Color object) {
				return object.getHex();
			}
		};
		final TextInputCell useCell = new TextInputCell();
		Column<Color, String> useColumn = new Column<Color, String>(useCell) {
			@Override
			public String getValue(Color object) {
				return object.getUse();
			}
		};
		useColumn.setFieldUpdater(new FieldUpdater<Color, String>() {
			@Override
			public void update(int index, Color object, String value) {
				object.setUse(value);
			}
		});

		colorsTable.addColumn(checkColumn, "Select");
		colorsTable.addColumn(colorColumn, "Color");
		colorsTable.addColumn(useColumn, "Usage");
		colorsTable.setRowData(taxonMatrix.getColors());

		ScrollPanel scrollPanel = new ScrollPanel();
		Button removeButton = new Button("remove");

		removeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Set<Color> toRemove = new HashSet<Color>();
				List<Color> colors = taxonMatrix.getColors();
				for (Color color : colors) {
					Boolean checked = checkboxCell.getViewData(color);
					if (checked == null)
						continue;
					if (checked)
						;
					{
						toRemove.add(color);
					}
				}
				eventBus.fireEvent(new RemoveColorsEvent(toRemove));
				for (Color color : toRemove) {
					colors.remove(color);
				}
				colorsTable.setRowData(colors);
			}
		});
		scrollPanel.setWidget(colorsTable);
		centerPanel.setWidget(scrollPanel);
		centerPanel.addButton(removeButton);
	}

}
