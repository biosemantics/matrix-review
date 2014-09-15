package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public class CommentsDialog extends Dialog {

	public class Comment {

		private String source;
		private String value;
		private String text;

		public Comment(String source, String value, String text) {
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
	}

	public interface CommentProperties extends PropertyAccess<Comment> {

		@Path("source")
		ModelKeyProvider<Comment> key();

		@Path("source")
		ValueProvider<Comment, String> source();

		@Path("value")
		ValueProvider<Comment, String> value();

		@Path("text")
		ValueProvider<Comment, String> text();

	}

	private EventBus eventBus;
	private Model model;

	public CommentsDialog(EventBus eventBus, Model model) {
		this.eventBus = eventBus;
		this.model = model;
		CommentProperties commentProperties = GWT
				.create(CommentProperties.class);
		ListStore<Comment> commentStore = new ListStore<Comment>(
				commentProperties.key());

		List<Comment> comments = createComments();
		for (Comment comment : comments)
			commentStore.add(comment);

		List<ColumnConfig<Comment, ?>> columns = new ArrayList<ColumnConfig<Comment, ?>>();
		ColumnConfig<Comment, String> sourceCol = new ColumnConfig<Comment, String>(
				commentProperties.source(), 190, "Source");
		ColumnConfig<Comment, String> valueCol = new ColumnConfig<Comment, String>(
				commentProperties.value(), 190, "Value(s)");
		ColumnConfig<Comment, String> textCol = new ColumnConfig<Comment, String>(
				commentProperties.text(), 400, "Comment");
		columns.add(sourceCol);
		columns.add(valueCol);
		columns.add(textCol);
		ColumnModel<Comment> cm = new ColumnModel<Comment>(columns);
		final Grid<Comment> grid = new Grid<Comment>(commentStore, cm);

		setBodyBorder(false);
		setHeadingText("Comments");
		setWidth(800);
		setHeight(600);
		setHideOnButtonClick(true);

		ContentPanel panel = new ContentPanel();
		panel.add(grid);
		this.add(panel);
	}

	private List<Comment> createComments() {
		for(Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			System.out.println(taxon.getFullName());
		}
		
		List<Comment> comments = new LinkedList<Comment>();
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			if (model.hasComment(taxon))
				comments.add(new Comment(taxon.getFullName(), "", model
						.getComment(taxon)));
		}
		for (Character character : model.getTaxonMatrix()
				.getHierarchyCharactersBFS()) {
			if (model.hasComment(character))
				comments.add(new Comment(character.toString(), "", model
						.getComment(character)));
		}
		for (Taxon taxon : model.getTaxonMatrix().getHierarchyTaxaDFS()) {
			for (Character character : model.getTaxonMatrix()
					.getHierarchyCharactersBFS()) {
				Value value = model.getTaxonMatrix().getValue(taxon, character);
				if (model.hasComment(value))
					comments.add(new Comment("Value of " + character.toString()
							+ " of " + taxon.getFullName(), value.getValue(),
							model.getComment(value)));
			}
		}
		return comments;
	}

}
