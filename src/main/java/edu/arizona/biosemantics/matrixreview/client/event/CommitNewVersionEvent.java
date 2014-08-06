package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.CommitNewVersionEvent.CommitNewVersionEventHandler;

public class CommitNewVersionEvent extends GwtEvent<CommitNewVersionEventHandler> {

	public interface CommitNewVersionEventHandler extends EventHandler {
		void onCommitRequest(CommitNewVersionEvent event);
	}
	
	public static Type<CommitNewVersionEventHandler> TYPE = new Type<CommitNewVersionEventHandler>();
	private String author;
	private String comment;
	
	public CommitNewVersionEvent(String author, String comment) {
		this.author = author;
		this.comment = comment;
	}
	
	@Override
	public GwtEvent.Type<CommitNewVersionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CommitNewVersionEventHandler handler) {
		handler.onCommitRequest(this);
	}

	public static Type<CommitNewVersionEventHandler> getTYPE() {
		return TYPE;
	}

	public String getAuthor(){
		return author;
	}

	public String getComment(){
		return comment;
	}
	
}