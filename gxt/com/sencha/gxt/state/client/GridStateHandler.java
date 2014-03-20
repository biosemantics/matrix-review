/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.state.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.state.client.GridStateHandler.GridState;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.ColumnWidthChangeHandler;
import com.sencha.gxt.widget.core.client.event.SortChangeEvent;
import com.sencha.gxt.widget.core.client.event.SortChangeEvent.SortChangeHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent.ColumnHiddenChangeHandler;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * State handler for grids
 * 
 * @param <M> the grid model type
 */
public class GridStateHandler<M> extends ComponentStateHandler<GridState, Grid<M>> {

  public interface GridSortState {
    SortDir getSortDir();

    String getSortField();

    void setSortDir(SortDir dir);

    void setSortField(String sortField);
  }

  public interface GridState {
    Set<String> getHidden();

    SortDir getSortDir();

    String getSortField();

    Map<String, Integer> getWidths();

    void setHidden(Set<String> hidden);

    void setSortDir(SortDir sortDir);

    void setSortField(String field);

    void setWidths(Map<String, Integer> widths);
  }

  private class Handler implements ColumnHiddenChangeHandler, ColumnWidthChangeHandler, SortChangeHandler {

    @Override
    public void onColumnHiddenChange(ColumnHiddenChangeEvent event) {
      handleColumnHiddenChange(event);
    }

    @Override
    public void onColumnWidthChange(ColumnWidthChangeEvent event) {
      handleColumnWidthChange(event);
    }

    @Override
    public void onSortChange(SortChangeEvent event) {
      handleSortChange(event);
    }

  }

  /**
   * Creates a new grid state handler instance.
   * 
   * @param stateType the state type
   * @param component the grid
   * @param key the state key
   */
  public GridStateHandler(Class<GridState> stateType, Grid<M> component, String key) {
    super(stateType, component, key);

    init(component);
  }

  /**
   * Creates a nbew grid state handler instance.
   * 
   * @param component the grid
   */
  public GridStateHandler(Grid<M> component) {
    super(GridState.class, component);

    init(component);
  }

  /**
   * Creates a new state handler instance.
   * 
   * @param component the grid
   * @param key the state key
   */
  public GridStateHandler(Grid<M> component, String key) {
    super(GridState.class, component, key);

    init(component);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void applyState() {
    if (getObject().isStateful()) {
      GridState state = getState();
      Set<String> hidden = state.getHidden();
      if (hidden != null) {
        for (String path : hidden) {
          ColumnConfig<M, ?> column = getObject().getColumnModel().findColumnConfig(path);
          if (column != null) {
            column.setHidden(true);
          }
        }
      }

      Map<String, Integer> widths = state.getWidths();
      if (widths != null) {
        for (String path : widths.keySet()) {
          ColumnConfig<M, ?> column = getObject().getColumnModel().findColumnConfig(path);
          if (column != null) {
            column.setWidth(widths.get(path));
          }
        }
      }

      if (state.getSortField() != null) {

        ValueProvider<? super M, Comparable> vp = (ValueProvider) getObject().getColumnModel().findColumnConfig(
            state.getSortField()).getValueProvider();

        if (vp != null) {
          getObject().getStore().clearSortInfo();
          getObject().getStore().addSortInfo(new StoreSortInfo<M>(vp, state.getSortDir()));
        }

      }
    }
  }

  protected void handleColumnHiddenChange(ColumnHiddenChangeEvent event) {
    if (getObject().isStateful()) {
      GridState state = getState();
      Set<String> hidden = state.getHidden();
      if (hidden == null) {
        hidden = new HashSet<String>();
        state.setHidden(hidden);
      }

      if (event.getColumnConfig().isHidden()) {
        hidden.add(event.getColumnConfig().getPath());
      } else {
        hidden.remove(event.getColumnConfig().getPath());
      }

      saveState();
    }
  }

  protected void handleColumnWidthChange(ColumnWidthChangeEvent event) {
    if (getObject().isStateful()) {
      GridState state = getState();
      Map<String, Integer> widths = state.getWidths();
      if (widths == null) {
        widths = new HashMap<String, Integer>();
        state.setWidths(widths);
      }
      widths.put(event.getColumnConfig().getPath(), event.getColumnConfig().getWidth());

      saveState();
    }
  }

  protected void handleSortChange(SortChangeEvent event) {
    if (getObject().isStateful()) {
      GridState state = getState();

      state.setSortField(event.getSortInfo().getSortField());
      state.setSortDir(event.getSortInfo().getSortDir());

      saveState();
    }
  }

  protected void init(Grid<M> component) {
    Handler h = new Handler();
    component.addSortChangeHandler(h);
    component.getColumnModel().addColumnHiddenChangeHandler(h);
    component.getColumnModel().addColumnWidthChangeHandler(h);
  }

}
