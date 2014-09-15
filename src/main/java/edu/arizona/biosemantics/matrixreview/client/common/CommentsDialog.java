package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.ListFilter;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import edu.arizona.biosemantics.matrixreview.client.common.ColorsDialog.ColorEntry;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class CommentsDialog extends Dialog {
	
	private final static String taxonCharacterValueType = "Taxon-Character-Value";
	private final static String characterType = "Character";
	private final static String taxonType = "Taxon";
	
	public class Comment {

		private Object object;
		private String source;
		private String value;
		private String text;
		private String type;

		public Comment(Object object, String source, String value, String text) {
			this.object = object;
			if(object instanceof Value)
				type = "Taxon-Character-Value";
			if(object instanceof Character)
				type = "Character";
			if(object instanceof Taxon)
				type = "Taxon";
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

		public String getType() {
			return type;
		}
	}
	

	public interface CommentProperties extends PropertyAccess<Comment> {

		@Path("source")
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
		ValueProvider<Comment, String> type();

	}

	private EventBus eventBus;
	private Model model;
	private ListStore<Comment> commentStore;
	private Grid<Comment> grid;

	public CommentsDialog(EventBus eventBus, Model model) {
		this.eventBus = eventBus;
		this.model = model;
		CommentProperties commentProperties = GWT.create(CommentProperties.class);

		IdentityValueProvider<Comment> identity = new IdentityValueProvider<Comment>();
	    final CheckBoxSelectionModel<Comment> checkBoxSelectionModel = new CheckBoxSelectionModel<Comment>(identity);
	    
	    checkBoxSelectionModel.setSelectionMode(SelectionMode.MULTI);
	    
	    ColumnConfig<Comment, String> typeCol = new ColumnConfig<Comment, String>(
				commentProperties.type(), 0, "Type");
		ColumnConfig<Comment, String> sourceCol = new ColumnConfig<Comment, String>(
				commentProperties.source(), 190, "Source");
		ColumnConfig<Comment, String> valueCol = new ColumnConfig<Comment, String>(
				commentProperties.value(), 190, "Value(s)");
		ColumnConfig<Comment, String> textCol = new ColumnConfig<Comment, String>(
				commentProperties.text(), 400, "Comment");
		
		List<ColumnConfig<Comment, ?>> columns = new ArrayList<ColumnConfig<Comment, ?>>();
	      columns.add(checkBoxSelectionModel.getColumn());
	      columns.add(typeCol);
	      columns.add(sourceCol);
	      columns.add(valueCol);
	      columns.add(textCol);
	      ColumnModel<Comment> cm = new ColumnModel<Comment>(columns);
	      
		commentStore = new ListStore<Comment>(commentProperties.key());

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

	    StringFilter<Comment> textFilter = new StringFilter<Comment>(commentProperties.text());
	    StringFilter<Comment> sourceFilter = new StringFilter<Comment>(commentProperties.source());
	    StringFilter<Comment> valueFilter = new StringFilter<Comment>(commentProperties.value());
	    
	    ListStore<String> typeFilterStore = new ListStore<String>(new ModelKeyProvider<String>() {
			@Override
			public String getKey(String item) {
				return item;
			}
	    });
	    typeFilterStore.add(taxonType);
	    typeFilterStore.add(characterType);
	    typeFilterStore.add(taxonCharacterValueType);
	    
	    ListFilter<Comment, String> typeFilter = new ListFilter<Comment, String>(commentProperties.type(), typeFilterStore);
	    
		GridFilters<Comment> filters = new GridFilters<Comment>();
		filters.initPlugin(grid);
		filters.setLocal(true);
		
		filters.addFilter(textFilter);
		filters.addFilter(sourceFilter);
		filters.addFilter(valueFilter);
		filters.addFilter(typeFilter);
	    
		setBodyBorder(false);
		setHeadingText("Comments");
		setWidth(800);
		setHeight(600);
		setHideOnButtonClick(true);

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
				for(Comment comment : grid.getSelectionModel().getSelectedItems()) {
					commentStore.remove(comment);
					Object object = comment.getObject();
					if(object instanceof Value) {
						eventBus.fireEvent(new SetValueCommentEvent((Value)object, ""));
					}
					if(object instanceof Character) {
						eventBus.fireEvent(new SetCharacterCommentEvent((Character)object, ""));
					}
					if(object instanceof Taxon) {
						eventBus.fireEvent(new SetTaxonCommentEvent((Taxon)object, ""));
					}
				}
			}
		});
		return menu;
	}

	private List<Comment> createComments() {
		for(Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			System.out.println(taxon.getFullName());
		}
		
		List<Comment> comments = new LinkedList<Comment>();
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			if (model.hasComment(taxon))
				comments.add(new Comment(taxon, taxon.getFullName(), "", model
						.getComment(taxon)));
		}
		for (Character character : model.getTaxonMatrix()
				.getHierarchyCharactersBFS()) {
			if (model.hasComment(character))
				comments.add(new Comment(character, character.toString(), "", model
						.getComment(character)));
		}
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			for (Character character : model.getTaxonMatrix()
					.getHierarchyCharactersBFS()) {
				Value value = model.getTaxonMatrix().getValue(taxon, character);
				if (model.hasComment(value))
					comments.add(new Comment(value, "Value of " + character.toString()
							+ " of " + taxon.getFullName(), value.getValue(),
							model.getComment(value)));
			}
		}
		return comments;
	}

}
