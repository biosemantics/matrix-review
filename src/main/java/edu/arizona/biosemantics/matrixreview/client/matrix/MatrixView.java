package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MatrixModeEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class MatrixView implements IsWidget {
		
	private SimpleEventBus eventBus;
	private ModelControler modelControler;	
	
	private TaxonStore taxonStore;
	private FrozenFirstColumTaxonTreeGrid taxonTreeGrid;
	private MatrixViewControler matrixViewControler;

	public MatrixView(SimpleEventBus eventBus) {
		this.eventBus = eventBus;
		
		taxonStore = new TaxonStore();

		// this also has influence on
		// the dirty icon that comes out of the box; true wont show?
		// also one may not directly use the model to calculate view related
		// things, such as coverage, because it is not yet represented in model
		// if autocommit is set to false
		taxonStore.setAutoCommit(true);
		taxonTreeGrid = new FrozenFirstColumTaxonTreeGrid(eventBus, taxonStore, createTaxaColumnConfig(), 
				new TaxonTreeAppearance(eventBus));
		
		addEventHandlers();
	}
	
	private TaxaColumnConfig createTaxaColumnConfig() {
		TaxaColumnConfig taxaColumnConfig = new TaxaColumnConfig(eventBus);
		taxaColumnConfig.setCell(new AbstractCell<Taxon>() {
			@Override
			public void render(Context context,	Taxon value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<i>" + value.getBiologicalName() + "</i>"));
			}
		});
		return taxaColumnConfig;
	}
	

	private void addEventHandlers() {
		modelControler = new ModelControler(eventBus);
		matrixViewControler = new HierarchicalMatrixViewControler(eventBus, taxonTreeGrid);
		
		this.eventBus.addHandler(MatrixModeEvent.TYPE, new MatrixModeEvent.MatrixModeEventHandler() {
			@Override
			public void onMode(MatrixModeEvent event) {
				final Model model = matrixViewControler.getModel();
				matrixViewControler.remove();
				switch(event.getMode()) {
				case FLAT:
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							matrixViewControler = new FlatMatrixViewControler(eventBus, taxonTreeGrid);
							eventBus.fireEvent(new LoadModelEvent(model));
						}
					});
					break;
				case HIERARCHY:
					Scheduler.get().scheduleDeferred(new ScheduledCommand() {
						@Override
						public void execute() {
							matrixViewControler = new HierarchicalMatrixViewControler(eventBus, taxonTreeGrid);
							eventBus.fireEvent(new LoadModelEvent(model));
						}
					});					
					break;
				}
			}
		});
	}

	@Override
	public Widget asWidget() {
		return taxonTreeGrid.asWidget();
	}
	
	public FrozenFirstColumTaxonTreeGrid getTaxonTreeGrid() {
		return taxonTreeGrid;
	}
	
	public TaxonStore getTaxonStore() {
		return taxonStore;
	}
	
	public ModelControler getModelControler() {
		return modelControler;
	}
}