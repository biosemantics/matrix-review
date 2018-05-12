package edu.arizona.biosemantics.matrixreview.shared;

import java.util.Collection;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;

@RemoteServiceRelativePath("matrix")
public interface IMatrixService extends RemoteService {

	public Model getMatrix();
	
	public SafeHtml getHighlighted(String description, Collection<Character> highlight, Collection<Value> values);
	
}
