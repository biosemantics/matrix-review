package edu.arizona.biosemantics.matrixreview.shared;


import java.util.Collection;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

public interface IMatrixServiceAsync {
	
	public void getMatrix(AsyncCallback<Model> callback);

	public void getHighlighted(String description, Collection<Character> highlight,
			Collection<Value> list, AsyncCallback<SafeHtml> asyncCallback);
	
}
