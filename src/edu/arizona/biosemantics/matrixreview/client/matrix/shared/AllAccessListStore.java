package edu.arizona.biosemantics.matrixreview.client.matrix.shared;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;


public class AllAccessListStore<M> extends ListStore<M> {

	/**
	 * Record that always auto commits to the model;
	 * Further it always indicates to be dirty once a change to the model has been made, in contrast to the default implementation, which shows
	 * dirty only when there's outstanding commits to be made to the underlying model
	 * @author rodenhausen
	 */
	/*public class MyRecord extends Record {

		protected final Map<Object, Change<M, ?>> outstandingCommitChanges = new HashMap<Object, Store.Change<M, ?>>();
		
		public MyRecord(M model) {
			super(model);
		}

		public <V> void addChange(ValueProvider<? super M, V> property, V value) {
			//if (!isAutoCommit) {
				Change<M, V> c = new PropertyChange<M, V>(property, value);
				if (c.isCurrentValue(model)) {
					outstandingCommitChanges.remove(c.getChangeTag());
					if (outstandingCommitChanges.size() == 0) {
						modifiedRecords.remove(this);
					}
				} else {
					outstandingCommitChanges.put(c.getChangeTag(), c);
					modifiedRecords.add(this);
				}
				fireEvent(new StoreRecordChangeEvent<M>(this, property));
			//} else {
				property.setValue(model, value);
				fireEvent(new StoreUpdateEvent<M>(
						Collections.singletonList(this.model)));
			//}
		}

		@Override
		public void commit(boolean fireEvent) {
			changes.putAll(outstandingCommitChanges);
			
			if (hasOutstandingChanges()) {
				for (Change<M, ?> c : outstandingCommitChanges.values()) {
					assert c.isCurrentValue(model) == false : "Current value was somehow stored in a record's change set!";
					c.modify(model);
				}
				outstandingCommitChanges.clear();
				if (fireEvent) {
					fireEvent(new StoreUpdateEvent<M>(
							Collections.singletonList(this.model)));
				}
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public <V> Change<M, V> getChange(ValueProvider<? super M, V> property) {
			// This will be typesafe ONLY if only addChange(ValueProvider<M,V>,
			// V) is
			// called
			// if we keep this, kill the other addChange, or the Change
			// interface
			// itself
			return (Change<M, V>) outstandingCommitChanges.get(property.getPath());
		}

		public Collection<Change<M, ?>> getOutstandingCommitChanges() {
			return outstandingCommitChanges.values();
		}

		public boolean hasOutstandingChanges() {
			return !outstandingCommitChanges.isEmpty();
		}

		@Override
		public void revert(ValueProvider<? super M, ?> property) {
			if (outstandingCommitChanges.remove(property.getPath()) != null) {
				fireEvent(new StoreUpdateEvent<M>(Collections.singletonList(this.model)));
			}
		}
		
		@Override
		public void revert() {
			outstandingCommitChanges.clear();
			fireEvent(new StoreUpdateEvent<M>(Collections.singletonList(this.model)));
		}
	}*/
	
	public AllAccessListStore(ModelKeyProvider<? super M> keyProvider) {
		super(keyProvider);
	}
	
	public void enableAndRefreshFilters() {
	    this.filtersEnabled = true;
	    applyFilters();
	}
	
	public int sizeOfAllItems() {
		return this.allItems.size();
	}

	public M getFromAllItems(int indexOfAllItems) {
		return this.allItems.get(indexOfAllItems);
	}
	
	/*@Override
	public Record getRecord(M data) {
		String key = getKeyProvider().getKey(data);
		MyRecord rec = (MyRecord)records.get(key);
		if (rec == null) {
			rec = new MyRecord(data);
			records.put(key, rec);
		}
		return rec;
	}*/

}
