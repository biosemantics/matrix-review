package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.EventHandler;
import edu.arizona.biosemantics.matrixreview.client.event.ChangeComparingSelectionEvent.ChangeComparingSelectionEventHandler;

public class ChangeComparingSelectionEvent extends GwtEvent<ChangeComparingSelectionEventHandler> {
	
	private Object selection;
	
	public ChangeComparingSelectionEvent(Object newSelection){
		this.selection = newSelection;
	}
	
	public Object getSelection(){
		return selection;
	}
	
	public interface ChangeComparingSelectionEventHandler extends EventHandler{
		public void onChange(ChangeComparingSelectionEvent event);
	}
	
	public static Type<ChangeComparingSelectionEventHandler> TYPE = new Type<ChangeComparingSelectionEventHandler>();
	
	@Override
	public GwtEvent.Type<ChangeComparingSelectionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeComparingSelectionEventHandler handler) {
		handler.onChange(this);
	}

	public static Type<ChangeComparingSelectionEventHandler> getTYPE() {
		return TYPE;
	}
}
