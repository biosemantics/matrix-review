package edu.arizona.biosemantics.matrixreview.client.matrix;

import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.filters.Filter;

import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;

public class TaxaColumnConfig extends ColumnConfig<Taxon, Taxon> {

	public static class TaxonNameValueProvider implements ValueProvider<Taxon, Taxon> {
		
		//private Model model;
		//private EventBus eventBus;

		public TaxonNameValueProvider(EventBus eventBus) {
			//this.eventBus = eventBus;
			
			//addEventHandlers();
		}
		
		/*private void addEventHandlers() {
			eventBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
				@Override
				public void onLoad(LoadModelEvent event) {
					model = event.getModel();
				}
			});
		}*/

		@Override
		public Taxon getValue(Taxon object) {
			return object;
		}

		@Override
		public void setValue(Taxon object, Taxon value) {
			//eventBus.fireEvent(new ModifyTaxonEvent(object, object.getParent(), value.getLevel(), 
			//		value.getName(), value.getAuthor(), value.getYear()));
		}

		@Override
		public String getPath() {
			return "/name";
		}

	}
	
	private Filter<Taxon, ?> filter;
	
	public TaxaColumnConfig(EventBus eventBus) {
		super(new TaxonNameValueProvider(eventBus), 300, "Taxon Concept / Character");
	}

	public Filter<Taxon, ?> getFilter() {
		return filter;
	}

	public void setFilter(Filter<Taxon, ?> filter) {
		this.filter = filter;
	}
	
}
