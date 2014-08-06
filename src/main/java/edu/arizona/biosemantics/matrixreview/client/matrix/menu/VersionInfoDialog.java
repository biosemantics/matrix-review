package edu.arizona.biosemantics.matrixreview.client.matrix.menu;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.TextArea;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;

import edu.arizona.biosemantics.matrixreview.client.event.CommitNewVersionEvent;

public class VersionInfoDialog extends Dialog {
	
	public VersionInfoDialog(final EventBus eventBus) {
		this.setHeadingText("Version Information");
		final VersionInformationContainer versionInformationContainer = new VersionInformationContainer("");
	    this.add(versionInformationContainer);
	 
	  
	    final TextField authorField = versionInformationContainer.getAuthorField();
	    authorField.addValidator(new EmptyValidator<String>());
	    
	    getButtonBar().clear();
	    TextButton add = new TextButton("Save New Version");
	    add.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				if(!authorField.validate()) {
					AlertMessageBox alert = new AlertMessageBox("Error", "Author name is a required field.");
					alert.show();
					return;
				}
				VersionInfoDialog.this.hide();
				eventBus.fireEvent(new CommitNewVersionEvent(authorField.getText(), versionInformationContainer.getCommentArea().getText()));
			}
	    });
	    TextButton cancel =  new TextButton("Cancel");
	    cancel.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				VersionInfoDialog.this.hide();
			}
	    });
	    addButton(add);
	    addButton(cancel);
	}
	
	public static class VersionInformationContainer extends SimpleContainer {
	
		private TextField authorField;
		private TextArea commentArea;

		public VersionInformationContainer(String initialName) {
		 
		    VerticalLayoutContainer p = new VerticalLayoutContainer();
		 
		    authorField = new TextField();
		    authorField.setValue(initialName);
		    authorField.setAllowBlank(false);
		    Scheduler.get().scheduleDeferred(new ScheduledCommand(){
		    	public void execute(){
		    		authorField.focus();
		    	}
		    });
		    
		    commentArea = new TextArea();
		    commentArea.getElement().getStyle().setProperty("resize", "none");
		    commentArea.setSize("100%", "70px");
		    p.add(new FieldLabel(authorField, "Author"), new VerticalLayoutData(1, -1));
		    p.add(new FieldLabel(commentArea, "Comment"), new VerticalLayoutData(1, -1));
		    add(p);
		}

		public TextField getAuthorField() {
			return authorField;
		}
		
		public TextArea getCommentArea() {
			return commentArea;
		}
		
	}
}
