package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.LinkedList;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tree.Tree;

import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.common.taxonomy.TaxonIdentification;

public class TaxonAddDialog extends Dialog {
		
		private TaxonInformationContainer taxonInformationContainer;

		public TaxonAddDialog(final EventBus eventBus, Model model, final Taxon initialParent) {
			this.setHeading("Add Taxon");
			taxonInformationContainer = new TaxonInformationContainer(model, initialParent, null);
		    this.add(taxonInformationContainer);
		 
		    final Tree<Taxon, String> taxaTree = taxonInformationContainer.getTaxaTree();
		    final ComboBox<Rank> levelCombo = taxonInformationContainer.getLevelComboBox(); 
		    final TextField nameField = taxonInformationContainer.getNameField();
		    final TextField authorField = taxonInformationContainer.getAuthorField();
		    final TextField yearField = taxonInformationContainer.getYearField();
		    
		    getButtonBar().clear();
		    TextButton add = new TextButton("Add");
		    add.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					if(!nameField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Name empty", "A name is required");
						alert.show();
						return;
					}
					if(!authorField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Author empty", "An author is required");
						alert.show();
						return;
					}
					if(!yearField.validate()) {
						AlertMessageBox alert = new AlertMessageBox("Year empty", "A year is required");
						alert.show();
						return;
					}
					
					LinkedList<RankData> rankData = new LinkedList<RankData>();
					Taxon parent = taxaTree.getSelectionModel().getSelectedItem();
					String author = authorField.getText();
					String date = yearField.getText();
					if(parent != null) {
						rankData = new LinkedList<RankData>(parent.getTaxonIdentification().getRankData());
						rankData.add(new RankData(levelCombo.getValue(), nameField.getText(), rankData.getLast(), "", ""));
					}
					TaxonIdentification taxonIdentification = new TaxonIdentification(rankData, author, date);
					Taxon newTaxon = new Taxon(taxonIdentification);
					eventBus.fireEvent(new AddTaxonEvent(newTaxon, parent));
					TaxonAddDialog.this.hide();
				}
		    });
		    TextButton cancel =  new TextButton("Cancel");
		    cancel.addSelectHandler(new SelectHandler() {
				@Override
				public void onSelect(SelectEvent event) {
					TaxonAddDialog.this.hide();
				}
		    });
		    addButton(add);
		    addButton(cancel);
		}

		//Only call this once the taxonInformationContains is already attached/displayed, otherwise the call will fail with an AssertionExcpeption, because
		//the node to expand simply does not exist yet
		public void selectParent(Taxon taxon) {
			taxonInformationContainer.selectParent(taxon);
		}
	}
	
	