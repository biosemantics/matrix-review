package edu.arizona.biosemantics.matrixreview.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.MatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleMatrixVersion;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.SimpleTaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonProperties;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;

public class MatrixCompareView extends Composite {
	private List<SimpleMatrixVersion> oldVersions;
	private MatrixVersion currentVersion;
	
	private SimpleContainer content;
	
	public MatrixCompareView(List<SimpleMatrixVersion> old, MatrixVersion current){
		this.oldVersions = old;
		this.currentVersion = current;
		/*for (SimpleMatrixVersion version: oldVersions){
			System.out.println("VERSION: " + version.getVersionInfo().getVersionID());
			System.err.println(currentVersion.getTaxonMatrix().getTaxaCount());
			SimpleTaxonMatrix matrix = version.getMatrix();
			
			Character first = matrix.getCharacter(0);
			
			for(Taxon taxon : matrix.list()) {
				Value a = taxon.get(first);
				System.out.println("Taxon: " + taxon.getName() + ". " + first.getName() + " " + a.getValue());
			}
		}*/
		content = GWT.create(SimpleContainer.class);
		
		ContentPanel westPanel = new ContentPanel();
		westPanel.add(new Label("WEST!"));
		ContentPanel centerPanel = new ContentPanel();
		
		TaxonStore store = new TaxonStore(); //this extends TreeStore<Taxon>. Awesome!
		for (SimpleMatrixVersion version: oldVersions){
			SimpleTaxonMatrix matrix = version.getMatrix();
			//store.add(matrix.getRootTaxa());
		}
		
		List<ColumnConfig<Taxon, ?>> columns = new ArrayList<ColumnConfig<Taxon, ?>>();
		final TaxonProperties taxonProperties = GWT.create(TaxonProperties.class);
		ColumnConfig<Taxon, String> column = new ColumnConfig<Taxon, String>(taxonProperties.fullName());
		columns.add(column);
		
		ColumnModel<Taxon> model = new ColumnModel<Taxon>(columns);
		
		final TreeGrid<Taxon> treeGrid = new TreeGrid<Taxon>(store, model, column);
		centerPanel.add(treeGrid.asWidget());
		
		final BorderLayoutContainer container = new BorderLayoutContainer();
		
		BorderLayoutData westData = new BorderLayoutData(150);
		westData.setCollapsible(true);
		westData.setSplit(true);
		westData.setCollapseMini(true);
		
		MarginData centerData = new MarginData();
		
		container.setWestWidget(westPanel, westData);
		container.setCenterWidget(centerPanel, centerData);
		
		content.add(container);
		container.setBorders(true);
		
	}
	
	public Widget asWidget(){
		return content.asWidget();
	}
	
}
