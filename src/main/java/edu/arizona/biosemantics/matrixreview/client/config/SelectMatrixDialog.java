package edu.arizona.biosemantics.matrixreview.client.config;

import java.util.List;
import java.util.Set;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class SelectMatrixDialog extends Dialog {

	//private SelectTaxaView selectTaxaView;
	//private SelectCharactersView selectCharactersView;
	private SelectMatrixView selectMatrixView;

	public SelectMatrixDialog(TaxonMatrix fullMatrix) {
		//selectTaxaView = new SelectTaxaView(fullMatrixBus, true);
		//selectCharactersView = new SelectCharactersView(fullMatrixBus, true);
		selectMatrixView = new SelectMatrixView(fullMatrix);
		
		/*selectTaxaView.addHideHandler(new HideHandler() {
			@Override
			public void onHide(HideEvent event) {
				setSelectMatrixView();
			}
		});
		selectCharactersView.addHideHandler(new HideHandler() {
			@Override
			public void onHide(HideEvent event) {
				setWidget(selectTaxaView);
			}
		});*/
		selectMatrixView.addHideHandler(new HideHandler() {
			@Override
			public void onHide(HideEvent event) {
				SelectMatrixDialog.this.hide();
			}
		});
		setBodyBorder(false);
		setHeadingText("Select Taxa/Characters to show");
		setWidth(600);
		setHeight(600);
		setPredefinedButtons();
		//setHideOnButtonClick(true);
		
		//setWidget(selectCharactersView);
		setSelectMatrixView();
	}
	
	protected void setSelectMatrixView() {
		setWidget(selectMatrixView);
		forceLayout();
	}

	public List<Character> getSelectedCharacters() {
		return selectMatrixView.getSelectedCharacters();
	}
	
	public List<Taxon> getSelectedRootTaxa() {
		return selectMatrixView.getSelectedRootTaxa();
	}
	
}
