package edu.arizona.biosemantics.matrixreview.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import edu.arizona.biosemantics.matrixreview.client.event.DownloadEvent.DownloadHandler;
import edu.arizona.biosemantics.matrixreview.client.matrix.MatrixFormat;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;

public class DownloadEvent extends GwtEvent<DownloadHandler> {

	public interface DownloadHandler extends EventHandler {
		void onDownload(DownloadEvent event);
	}
	
    public static Type<DownloadHandler> TYPE = new Type<DownloadHandler>();
	private Model model;
	private MatrixFormat format;

    public DownloadEvent(Model model) {
    	this.model = model;
    }
    
    public DownloadEvent(Model model, MatrixFormat format) {
    	this.model = model;
    	this.format = format;
    }
    
    
	@Override
	public Type<DownloadHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DownloadHandler handler) {
		handler.onDownload(this);
	}

	public Model getModel() {
		return model;
	}
	
	public MatrixFormat getFormat(){
		return format;
	}
}
