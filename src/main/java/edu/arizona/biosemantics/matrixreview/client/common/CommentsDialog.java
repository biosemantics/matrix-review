package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.SetValueValidator.ValidationResult;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class CommentsDialog extends Dialog {

	public enum CommentType {
		taxonType("Taxon"), characterType("Character"), taxonCharacterValueType("Taxon-Character-Value");
		
		private String readable;

		private CommentType(String readable) {
			this.readable = readable;
		}
		
		public String getReadable() {
			return readable;
		}
		
		@Override
		public String toString() {
			return getReadable();
		}
	}
	
	public class Comment {

		private String id;
		private Object object;
		private String source;
		private String value;
		private String text;
		private CommentType type;

		public Comment(String id, Object object, String source, String value, String text) {
			this.id = id;
			this.object = object;
			if (object instanceof Value)
				type = CommentType.taxonCharacterValueType;
			if (object instanceof Character)
				type = CommentType.characterType;
			if (object instanceof Taxon)
				type = CommentType.taxonType;
			this.source = source;
			this.value = value;
			this.text = text;
		}

		public String getSource() {
			return source;
		}

		public String getValue() {
			return value;
		}

		public String getText() {
			return text;
		}

		public Object getObject() {
			return object;
		}

		public CommentType getType() {
			return type;
		}
		
		public void setValue(String value) {
			if(this.type.equals(CommentType.taxonCharacterValueType))
				this.value = value;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setObject(Object object) {
			this.object = object;
		}
		
		public String getId() {
			return id;
		}
	}

	public interface CommentProperties extends PropertyAccess<Comment> {

		@Path("id")
		ModelKeyProvider<Comment> key();

		@Path("object")
		ValueProvider<Comment, Object> object();

		@Path("source")
		ValueProvider<Comment, String> source();

		@Path("value")
		ValueProvider<Comment, String> value();

		@Path("text")
		ValueProvider<Comment, String> text();

		@Path("type")
		ValueProvider<Comment, CommentType> type();

	}

	private EventBus eventBus;
	private EventBus subModelBus;
	private Model model;
	private ListStore<Comment> commentStore;
	private Grid<Comment> grid;

	public CommentsDialog(final EventBus eventBus, final EventBus subModelBus, final Model model) {
		this.eventBus = eventBus;
		this.subModelBus = subModelBus;
		this.model = model;
		CommentProperties commentProperties = GWT
				.create(CommentProperties.class);

		IdentityValueProvider<Comment> identity = new IdentityValueProvider<Comment>();
		final CheckBoxSelectionModel<Comment> checkBoxSelectionModel = new CheckBoxSelectionModel<Comment>(
				identity);

		checkBoxSelectionModel.setSelectionMode(SelectionMode.MULTI);

		ColumnConfig<Comment, CommentType> typeCol = new ColumnConfig<Comment, CommentType>(
				commentProperties.type(), 0, "Type");
		ColumnConfig<Comment, String> sourceCol = new ColumnConfig<Comment, String>(
				commentProperties.source(), 190, "Source");
		final ColumnConfig<Comment, String> valueCol = new ColumnConfig<Comment, String>(
				commentProperties.value(), 190, "Value(s)");
		final ColumnConfig<Comment, String> textCol = new ColumnConfig<Comment, String>(
				commentProperties.text(), 400, "Comment");

		List<ColumnConfig<Comment, ?>> columns = new ArrayList<ColumnConfig<Comment, ?>>();
		columns.add(checkBoxSelectionModel.getColumn());
		columns.add(typeCol);
		columns.add(sourceCol);
		columns.add(valueCol);
		columns.add(textCol);
		ColumnModel<Comment> cm = new ColumnModel<Comment>(columns);

		commentStore = new ListStore<Comment>(commentProperties.key());
		commentStore.setAutoCommit(true);
		
		List<Comment> comments = createComments();
		for (Comment comment : comments)
			commentStore.add(comment);

		final GroupingView<Comment> groupingView = new GroupingView<Comment>();
		groupingView.setShowGroupedColumn(false);
		groupingView.setForceFit(true);
		groupingView.groupBy(typeCol);

		grid = new Grid<Comment>(commentStore, cm);
		grid.setView(groupingView);
		grid.setContextMenu(createContextMenu());
		grid.setSelectionModel(checkBoxSelectionModel);
		grid.getView().setAutoExpandColumn(textCol);
		grid.setBorders(false);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);

		StringFilter<Comment> textFilter = new StringFilter<Comment>(
				commentProperties.text());
		StringFilter<Comment> sourceFilter = new StringFilter<Comment>(
				commentProperties.source());
		StringFilter<Comment> valueFilter = new StringFilter<Comment>(
				commentProperties.value());

		ListStore<CommentType> typeFilterStore = new ListStore<CommentType>(
				new ModelKeyProvider<CommentType>() {
					@Override
					public String getKey(CommentType item) {
						return item.toString();
					}
				});
		for(CommentType type : CommentType.values())
			typeFilterStore.add(type);
		ListFilter<Comment, CommentType> typeFilter = new ListFilter<Comment, CommentType>(
				commentProperties.type(), typeFilterStore);

		GridFilters<Comment> filters = new GridFilters<Comment>();
		filters.initPlugin(grid);
		filters.setLocal(true);

		filters.addFilter(textFilter);
		filters.addFilter(sourceFilter);
		filters.addFilter(valueFilter);
		filters.addFilter(typeFilter);

		GridInlineEditing<Comment> editing = new GridInlineEditing<Comment>(grid);
		editing.addEditor(textCol, new TextField());
		editing.addEditor(valueCol, new TextField());
		final SetValueValidator setValueValidator = new SetValueValidator(model);
		editing.addCompleteEditHandler(new CompleteEditHandler<Comment>() {
			@Override
			public void onCompleteEdit(CompleteEditEvent<Comment> event) {			
				GridCell cell = event.getEditCell();
				Comment comment = grid.getStore().get(cell.getRow());
				ColumnConfig<Comment, String> config = grid.getColumnModel().getColumn(cell.getCol());
				if(config.equals(valueCol)) {
					switch(comment.getType()) {
						case taxonCharacterValueType:
							Value oldValue = (Value)comment.getObject();
							Character character = model.getTaxonMatrix().getCharacter(oldValue);
							Taxon taxon = model.getTaxonMatrix().getTaxon(oldValue);
							String value = config.getValueProvider().getValue(comment);
							
							ValidationResult validationResult = setValueValidator.validValue(value, character);
							if(validationResult.isValid()) {
								Value newValue = new Value(value);
								comment.setObject(newValue);
								eventBus.fireEvent(new SetValueEvent(taxon, character, oldValue, newValue));
								subModelBus.fireEvent(new SetValueEvent(taxon, character, oldValue, newValue));
							} else {
								AlertMessageBox alert = new AlertMessageBox("Set value failed", "Can't set value " +
										value + " for " + character.getName() + " of " +  taxon.getBiologicalName() + ". Control mode " + 
										model.getControlMode(character).toString().toLowerCase() + " was selected for " + character.getName());
								alert.show();
							}
							
							break;
						default:
							break;
					}
				}
				if(config.equals(textCol)) {
					switch(comment.getType()) {
					case characterType:
						Character character = (Character)comment.getObject();
						eventBus.fireEvent(new SetCharacterCommentEvent(character, comment.getText()));
						subModelBus.fireEvent(new SetCharacterCommentEvent(character, comment.getText()));
						break;
					case taxonCharacterValueType:
						Value value = (Value)comment.getObject();
						eventBus.fireEvent(new SetValueCommentEvent(value, comment.getText()));
						subModelBus.fireEvent(new SetValueCommentEvent(value, comment.getText()));
						break;
					case taxonType:
						Taxon taxon = (Taxon)comment.getObject();
						eventBus.fireEvent(new SetTaxonCommentEvent(taxon, comment.getText()));
						subModelBus.fireEvent(new SetTaxonCommentEvent(taxon, comment.getText()));
						break;
					default:
						break;
					
					}
				}
			}
		});

		setBodyBorder(false);
		setHeading("Comments");
		setWidth(800);
		setHeight(600);
		setHideOnButtonClick(true);
		setModal(true);

		ContentPanel panel = new ContentPanel();
		panel.add(grid);
		this.add(panel);
	}

	private Menu createContextMenu() {
		Menu menu = new Menu();
		MenuItem removeItem = new MenuItem("Remove");
		menu.add(removeItem);
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {
			@Override
			public void onSelection(SelectionEvent<Item> event) {
				for (Comment comment : grid.getSelectionModel()
						.getSelectedItems()) {
					commentStore.remove(comment);
					Object object = comment.getObject();
					if (object instanceof Value) {
						eventBus.fireEvent(new SetValueCommentEvent(
								(Value) object, ""));
						subModelBus.fireEvent(new SetValueCommentEvent(
								(Value) object, ""));
					}
					if (object instanceof Character) {
						eventBus.fireEvent(new SetCharacterCommentEvent(
								(Character) object, ""));
						subModelBus.fireEvent(new SetCharacterCommentEvent(
								(Character) object, ""));
					}
					if (object instanceof Taxon) {
						eventBus.fireEvent(new SetTaxonCommentEvent(
								(Taxon) object, ""));
						subModelBus.fireEvent(new SetTaxonCommentEvent(
								(Taxon) object, ""));
					}
				}
			}
		});
		return menu;
	}

	private List<Comment> createComments() {
		List<Comment> comments = new LinkedList<Comment>();
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			if (model.hasComment(taxon))
				comments.add(new Comment("taxon-" + taxon.getId(), taxon, taxon.getBiologicalName(), "", model
						.getComment(taxon)));
		}
		for (Character character : model.getTaxonMatrix()
				.getHierarchyCharactersBFS()) {
			if (model.hasComment(character))
				comments.add(new Comment("character-" + character.getId(), character, character.toString(), "",
						model.getComment(character)));
		}
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			for (Character character : model.getTaxonMatrix()
					.getHierarchyCharactersBFS()) {
				Value value = model.getTaxonMatrix().getValue(taxon, character);
				if (model.hasComment(value))
					comments.add(new Comment("value-" + taxon.getId() + "-" + character.getId(), value, "Value of "
							+ character.toString() + " of "
							+ taxon.getBiologicalName(), value.getValue(), model
							.getComment(value)));
			}
		}
		return comments;
	}

}
