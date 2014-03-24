package com.sencha.gxt.widget.core.client.grid.editing;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderStyles;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.ColumnHeader.ColumnHeaderAppearance;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;

public class MyGridInlineEditing<M> extends GridInlineEditing<M> {

	private ColumnHeaderAppearance columnHeaderAppearance;
	private ColumnHeaderStyles columnHeaderStyles;
	protected Set<M> editorRows = new HashSet<M>();
	private ListStore<M> store;
		
	public MyGridInlineEditing(Grid<M> editableGrid, ListStore<M> store) {
		this(editableGrid, store, GWT.<ColumnHeaderAppearance> create(ColumnHeaderAppearance.class));
	}
	
	public MyGridInlineEditing(Grid<M> editableGrid, ListStore<M> store, ColumnHeaderAppearance columnHeaderAppearance) {
		super(editableGrid);
		this.columnHeaderAppearance = columnHeaderAppearance;
		this.columnHeaderStyles = columnHeaderAppearance.styles();
		this.store = store;
	}
	

	
	/**
	 * Make sure click actually appeared on cell content and not on link for actions menu
	 */
	@Override
	protected void onClick(ClickEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();
		if (Element.is(nativeEvent.getEventTarget())) {
			Element clickedElement = Element.as(nativeEvent.getEventTarget());
			if(clickedElement.getClassName().equals(columnHeaderStyles.headButton())) {
				return;
			}
		}
		
		if (this.getClicksToEdit() == ClicksToEdit.ONE) {
			final GridCell cell = findCell(event.getNativeEvent()
					.getEventTarget().<Element> cast());
			if (cell == null) {
				return;
			}

			// EXTGWT-2019 when starting an edit on the same row of an active
			// edit
			// the active edit value
			// is lost as the active cell does not complete the edit
			// this only happens with TreeGrid, not Grid which could be looked
			// into
			if (activeCell != null && activeCell.getRow() == cell.getRow()) {
				completeEditing();
			}

			// EXTGWT-3334 Edit is starting and stopping immediately when
			// leaving another active edit that completes
			Scheduler.get().scheduleDeferred(new ScheduledCommand() {
				@Override
				public void execute() {
					startEditing(cell);
				}
			});
		}
	}
	
	@SuppressWarnings("unchecked")
	public <O> Field<O> getEditor(ColumnConfig<M, ?> columnConfig, M model) {
		if(editorRows.contains(model))
			return (Field<O>) editorMap.get(columnConfig);
		return null;
	}
	  
	public void addEditor(M m) {
		this.editorRows.add(m);
	}
	  
	public void removeEditor(M m) {
		this.editorRows.remove(m);
	}
	
	public boolean hasEditor(M m) {
		return this.editorRows.contains(m);
	}
	
	/**
	 * Make this use the new getEditor(columnConfig, model) to allow "locked rows"
	 */
	@Override
	public void startEditing(final GridCell cell) {
		M m = store.get(cell.getRow());
		if(this.editorRows.contains(m))
			super.startEditing(cell);
	}



}
