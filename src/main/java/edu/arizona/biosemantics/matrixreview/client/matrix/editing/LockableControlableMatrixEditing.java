package edu.arizona.biosemantics.matrixreview.client.matrix.editing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import edu.arizona.biosemantics.matrixreview.client.event.LockCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharacterColumnConfig;
import edu.arizona.biosemantics.matrixreview.client.matrix.FrozenFirstColumTaxonTreeGrid.CharactersGrid;
import edu.arizona.biosemantics.matrixreview.client.matrix.form.ResetOldValueComboBoxCell;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.AllAccessListStore;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class LockableControlableMatrixEditing extends GridInlineEditing<Taxon> {

	protected Set<Taxon> lockedTaxa = new HashSet<Taxon>();
	protected Set<Character> lockedCharacters = new HashSet<Character>();
	private ListStore<Taxon> store;
	private EventBus eventBus;
	private ColumnHeaderStyles columnHeaderStyles;
	private TaxonMatrix taxonMatrix;
	
	public LockableControlableMatrixEditing(EventBus eventBus, CharactersGrid editableGrid, ListStore<Taxon> store, TaxonMatrix taxonMatrix) {
		this(eventBus, editableGrid, store, GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class), taxonMatrix);
	}
	
	public LockableControlableMatrixEditing(EventBus eventBus, CharactersGrid editableGrid, ListStore<Taxon> store, ColumnHeaderAppearance columnHeaderAppearance, TaxonMatrix taxonMatrix) {
		super(editableGrid);
		this.eventBus = eventBus;
		this.store = store;
		this.columnHeaderStyles = columnHeaderAppearance.styles();
		this.taxonMatrix = taxonMatrix;
		
		addEventHandlers();
	}
	
	private void addEventHandlers() {
		eventBus.addHandler(LockTaxonEvent.TYPE, new LockTaxonEvent.LockTaxonEventHandler() {
			@Override
			public void onLock(LockTaxonEvent event) {
				if(event.isLock())
					lockedTaxa.add(event.getTaxon());
				else {
					lockedTaxa.remove(event.getTaxon());
				}
			}
		});
		eventBus.addHandler(LockCharacterEvent.TYPE, new LockCharacterEvent.LockCharacterEventHandler() {
			@Override
			public void onLock(LockCharacterEvent event) {
				if(event.isLock())
					lockedCharacters.add(event.getCharacter());
				else {
					lockedCharacters.remove(event.getCharacter());
				}
			}
		});
		eventBus.addHandler(LockMatrixEvent.TYPE, new LockMatrixEvent.LockMatrixEventHandler() {
			@Override
			public void onLock(LockMatrixEvent event) {
				if(event.isLock()) {
					lockedTaxa.addAll(taxonMatrix.list());
					lockedCharacters.addAll(taxonMatrix.getCharacters());
				} else {
					lockedTaxa.clear();
					lockedCharacters.clear();
				}
			}
		});
	}

	public void lockTaxon(Taxon taxon) {
		lockedTaxa.add(taxon);
	}
	
	public void unlockTaxon(Taxon taxon) {
		lockedTaxa.remove(taxon);
	}
	
	public void lockCharacter(Character character) {
		lockedCharacters.add(character);
	}
	
	public void unlockCharacter(Character character) {
		lockedCharacters.remove(character);
	}
	
	public boolean isLockedCharacter(Character character) {
		return lockedCharacters.contains(character);
	}
	
	public boolean isLockedTaxon(Taxon taxon) {
		return lockedTaxa.contains(taxon);
	}
		  	
	@Override
	public void startEditing(final GridCell cell) {
		Taxon taxon = store.get(cell.getRow());
		CharacterColumnConfig config = ((CharactersGrid)editableGrid).getColumnModel().getColumn(cell.getCol());
		if(!this.isLockedTaxon(taxon) && !this.isLockedCharacter(config.getCharacter()))
			super.startEditing(cell);
	}

	public CharactersGrid getEditableGrid() {
		return (CharactersGrid)editableGrid;
	}
	
	public void setControlMode(Character character, ControlMode controlMode, List<String> states) {
		CharacterColumnConfig config = getEditableGrid().getCharacterColumnConfig(character);
		removeEditor(config);
		addEditor(config, new ValueConverter(), getEditorField(character, controlMode, states));
	}
	
	private Field<String> getEditorField(Character character, ControlMode controlMode, final List<String> states) {	
		switch(controlMode) {
		case CATEGORICAL:
			TaxonMatrix taxonMatrix = character.getTaxonMatrix();
			ValueConverter converter = new ValueConverter();			
			final AllAccessListStore<String> comboValues = new AllAccessListStore<String>(new ModelKeyProvider<String>() {
				@Override
				public String getKey(String item) {
					return item;
				}
			});
			List<String> sortValues = new ArrayList<String>(states);
			Collections.sort(sortValues);
			comboValues.addAll(sortValues);

			// http://www.sencha.com/forum/showthread.php?196281-GXT-3-rc2-ComboBox-setForceSelection(false)-does-not-work/page2
			// for ComboBox vs StringComboBox: ComboBox does not allow
			// "new values" -> StringComboBox is made for this use case
			// MyStringComboBox editComboBox = new
			// MyStringComboBox(sortValues);
			ComboBox<String> editComboBox = new ComboBox<String>(new ResetOldValueComboBoxCell<String>(comboValues, new LabelProvider<String>() {
				@Override
				public String getLabel(String item) {
					return item;
				}
			}));
			editComboBox.addValidator(new Validator<String>() {
				@Override
				public List<EditorError> validate(Editor<String> editor, String value) {
					List<EditorError> result = new LinkedList<EditorError>();
					if (!states.contains(value)) {
						result.add(new DefaultEditorError(editor, "Value entered not part of the character's vocabulary", value));
					}
					return result;
				}
			});
			// editComboBox.setAddUserValues(true);
			// editComboBox.setFinishEditOnEnter(true);
			//editComboBox.setForceSelection(false);
			// editComboBox.setAutoValidate(true);
			// editComboBox.setEditable(true);
			//editComboBox.setTypeAhead(true);
			// editComboBox.setAllowBlank(false);
			// editComboBox.setClearValueOnParseError(false);
			// editComboBox.setForceSelection(true);
			editComboBox.setTriggerAction(TriggerAction.ALL); 
			//upon trigger ("open combo"), a query is constructred that retrievaes all values of the underlying list. 
			// otherwise upon trigger only the selected value will be queried and displayed in the open box
			editComboBox.setForceSelection(false);
			return editComboBox;
		case NUMERICAL:
			// TODO add validation to only allow numerical values from there on
			final TextField textField = new TextField();
			textField.setAllowBlank(false);
			Validator<String> validator = new Validator<String>() {
				@Override
				public List<EditorError> validate(Editor<String> editor, String value) {
					List<EditorError> result = new LinkedList<EditorError>();
					if (value == null || !value.matches("[0-9]*")) {
						result.add(new DefaultEditorError(editor, "Value not numeric", value));
					}
					return result;
				}
			};
			textField.addValidator(validator);
			TextFieldChangeHandler changeHandler = new TextFieldChangeHandler(validator);
			textField.addValueChangeHandler(changeHandler);
			textField.addBeforeShowHandler(changeHandler);
			return textField;
		case OFF:
		default:
			return new TextField();
		}
	}

	public void addEditor(CharacterColumnConfig columnConfig) {
		Character character = columnConfig.getCharacter();
		addEditor(columnConfig, new ValueConverter(), getEditorField(character, character.getControlMode(), null));
	}
	
	protected void onClick(final ClickEvent event) {
		if (clicksToEdit == ClicksToEdit.ONE) {
			Element clickSource = event.getNativeEvent().getEventTarget().<Element> cast();
			final GridCell cell = findCell(clickSource);
			if (cell == null) {
				return;
			}

			// EXTGWT-2019 when starting an edit on the same row of an active
			// edit
			// the active edit value
			// is lost as the active cell does not complete the edit
			// this only happens with TreeGrid, not Grid which could be looked
			// into
			if (activeCell != null && activeCell.getRow() == cell.getRow()) {
				completeEditing();
			}

			// EXTGWT-3334 Edit is starting and stopping immediately when
			// leaving another active edit that completes
			if(!clickSource.getClassName().equals(columnHeaderStyles.headButton())) {
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {
					@Override
					public void execute() {
						startEditing(cell);
					}
				});
			}
		}
	}

}
