/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.DelayedTask;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent;
import com.sencha.gxt.data.shared.loader.LoadExceptionEvent.LoadExceptionHandler;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.Loader;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.event.LiveGridViewUpdateEvent;
import com.sencha.gxt.widget.core.client.event.LiveGridViewUpdateEvent.HasLiveGridViewUpdateHandlers;
import com.sencha.gxt.widget.core.client.event.LiveGridViewUpdateEvent.LiveGridViewUpdateHandler;

/**
 * LiveGridView for displaying large amount of data. Data is loaded on demand as the user scrolls the grid.
 */
public class LiveGridView<M> extends GridView<M> implements HasLiveGridViewUpdateHandlers {

  private static Logger logger = Logger.getLogger(LiveGridView.class.getName());

  private static class NavKeyCallback<M> implements LoadHandler<Object, ListLoadResult<M>>, LoadExceptionHandler<Object> {
    private HandlerRegistration registration;
    private Grid<M> grid;
    private int index;

    public NavKeyCallback(Grid<M> grid, int index) {
      this.grid = grid;
      this.index = index;
      @SuppressWarnings("unchecked")
      Loader<Object, ListLoadResult<M>> l = (Loader<Object, ListLoadResult<M>>) grid.getLoader();
      registration = l.addLoadHandler(this);
    }

    @Override
    public void onLoad(LoadEvent<Object, ListLoadResult<M>> event) {
      LiveGridView<M> view = (LiveGridView<M>)grid.getView();
      grid.getSelectionModel().select(index, false);
      view.ensureVisible(index, -1, false);
      registration.removeHandler();
    }

    @Override
    public void onLoadException(LoadExceptionEvent<Object> event) {
      registration.removeHandler();
    }

  }


  /**
   * The secondary store.
   */
  protected ListStore<M> cacheStore;

  protected XElement liveScroller;

  /**
   * The paging offset.
   */
  protected int liveStoreOffset = 0;

  /**
   * The total rows in data set. The total count from the loader when using a paging loader.
   */
  protected int totalCount = 0;

  /**
   * The current index of the view.
   */
  protected int viewIndex;
  private int cacheSize = 200;
  private StoreDataChangeHandler<M> cacheStoreHandler;
  private HandlerRegistration cacheStoreHandlerRegistration;
  private boolean ignoreScroll;
  private boolean isLoading;
  // to prevent flickering
  private boolean isMasked;
  private int loadDelay = 200;
  private HandlerRegistration loaderHandlerRegistration;
  private int loaderOffset;
  private DelayedTask loaderTask;
  private double prefetchFactor = .2;
  private int rowHeight = 20;
  private int viewIndexReload = -1;
  private int barWidth = XDOM.getScrollBarWidth() == 0 ? 16 : XDOM.getScrollBarWidth() + 2;

  /**
   * Creates a new live grid view.
   */
  public LiveGridView() {
    super();
  }

  /**
   * Creates a new live grid view instance with the given appearance.
   * 
   * @param appearance the appearance to use when rendering the grid view
   */
  public LiveGridView(GridAppearance appearance) {
    super(appearance);
  }

  @Override
  public HandlerRegistration addLiveGridViewUpdateHandler(LiveGridViewUpdateHandler handler) {
    return addHandler(LiveGridViewUpdateEvent.getType(), handler);
  }

  /**
   * Returns the numbers of rows that should be cached.
   * 
   * @return the cache size
   */
  public int getCacheSize() {
    int c = -1;
    if (grid.isViewReady()) {
      c = getVisibleRowCount();
    }
    return Math.max(c, cacheSize);
  }

  /**
   * Returns the amount of time before loading is done.
   * 
   * @return the load delay in milliseconds
   */
  public int getLoadDelay() {
    return loadDelay;
  }

  /**
   * Returns the prefetchFactor.
   * 
   * @return the prefetchFactor
   */
  public double getPrefetchFactor() {
    return prefetchFactor;
  }

  /**
   * Returns the height of one row.
   * 
   * @return the height of one row
   */
  public int getRowHeight() {
    return rowHeight;
  }

  /**
   * Returns the total number of rows that are visible given the current grid height.
   * 
   * @return the visible row count
   */
  public int getVisibleRowCount() {
    int rh = getCalculatedRowHeight();
    int visibleHeight = getLiveScrollerHeight();
    int result = (int) ((visibleHeight < 1) ? 0 : Math.floor((double) visibleHeight / rh));

    int calcHeight = rh * result;

    while (calcHeight < visibleHeight) {
      result++;
      calcHeight = rh * result;
    }

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("row height: " + rh + " visibleHeight: " + visibleHeight + " visible rows: " + result
          + " calcHeight: " + calcHeight);
    }

    return result;
  }

  /**
   * Refreshed the view. Reloads the store based on the current settings
   */
  public void refresh() {
    maskView();
    loadLiveStore(liveStoreOffset);
  }

  @Override
  public void refresh(boolean headerToo) {
    super.refresh(headerToo);
    if (headerToo) {
      positionLiveScroller();
    }
    if (!preventScrollToTopOnRefresh) {
      // we scrolled to the top
      updateRows(0, false);
    }
  }

  @Override
  public void scrollToTop() {
    liveScroller.setScrollTop(0);
  }

  /**
   * Sets the amount of rows that should be cached (default to 200). The cache size is the number of rows that are
   * retrieved each time a data request is made. The cache size should always be greater than the number of visible rows
   * of the grid. The number of visible rows will vary depending on the grid height and the height of each row. If the
   * set cache size is smaller than the number of visible rows of the grid than it gets set to the number of visible
   * rows of the grid.
   * 
   * @param cacheSize the new cache size
   */
  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  /**
   * Sets the amount of time before loading is done (defaults to 200).
   * 
   * @param loadDelay the new load delay in milliseconds
   */
  public void setLoadDelay(int loadDelay) {
    this.loadDelay = loadDelay;
  }

  /**
   * Sets the pre-fetch factor (defaults to .2). The pre-fetch factor is used to determine when new data should be
   * fetched as the user scrolls the grid. The factor is used with the cache size.
   * 
   * <p />
   * For example, if the cache size is 1000 with a pre-fetch of .20, the grid will request new data when the 800th (1000
   * * .20) row of the grid becomes visible.
   * 
   * @param prefetchFactor the pre-fetch factor
   */
  public void setPrefetchFactor(double prefetchFactor) {
    this.prefetchFactor = prefetchFactor;
  }

  /**
   * Sets the height of one row (defaults to 20). <code>LiveGridView</code> will only work with fixed row heights with
   * all rows being the same height. Changing this value will not physically resize the row heights, rather, the
   * specified height will be used internally for calculations.
   * 
   * @param rowHeight the new row height.
   */
  public void setRowHeight(int rowHeight) {
    this.rowHeight = rowHeight;
  }

  @Override
  protected void calculateVBar(boolean force) {
    if (force) {
      layout();
    }
  }

  protected void doLoad() {
    if (grid.isLoadMask()) {
      grid.setLoadMask(false);
      Scheduler.get().scheduleFinally(new ScheduledCommand() {
        @Override
        public void execute() {
          grid.setLoadMask(true);
        }
      });
    }
    if (grid.getLoader() instanceof PagingLoader<?, ?>) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("doLoad() with paging loader, loaderOffset: " + loaderOffset + " cacheSize: " + getCacheSize());
      }
      ((PagingLoader<?, ?>) grid.getLoader()).load(loaderOffset, getCacheSize());
    } else {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("doLoad() with non paging loader");
      }
      grid.getLoader().load();
    }
  }

  @Override
  public void doSort(int colIndex, SortDir sortDir) {
    cacheStore.clear();
    ColumnConfig<M, ?> column = cm.getColumn(colIndex);

    ValueProvider<? super M, ?> vp = column.getValueProvider();

    SortInfoBean bean = new SortInfoBean(vp, sortDir);

    if (sortDir == null && sortState != null && vp.getPath().equals(sortState.getSortField())) {
      bean.setSortDir(sortState.getSortDir() == SortDir.ASC ? SortDir.DESC : SortDir.ASC);

    } else if (sortDir == null) {
      bean.setSortDir(SortDir.ASC);
    }

    grid.getLoader().clearSortInfo();
    grid.getLoader().addSortInfo(bean);
    maskView();
    loadLiveStore(getLiveStoreCalculatedIndex(viewIndex));
  }

  protected int getCalculatedRowHeight() {
    return rowHeight + borderWidth; // TODO: Use a height field or rename
                                    // borderWidth
  }

  protected int getLiveScrollerHeight() {
    return XElement.as(liveScroller).getHeight(true);
  }

  protected int getLiveStoreCalculatedIndex(int index) {
    int calcIndex = index - (getCacheSize() / 2) + getVisibleRowCount();
    calcIndex = Math.min(totalCount - getCacheSize(), calcIndex);
    calcIndex = Math.min(index, calcIndex);
    return Math.max(0, calcIndex);
  }

  @Override
  protected int getScrollAdjust() {
    return scrollOffset;
  }

  @Override
  protected void handleComponentEvent(Event ge) {
    super.handleComponentEvent(ge);
    int type = ge.getTypeInt();
    EventTarget t = ge.getEventTarget();

    if (!ignoreScroll && Element.is(t) && (type == Event.ONSCROLL && liveScroller.isOrHasChild(Element.as(t)))
        || (type == Event.ONMOUSEWHEEL && dataTable.isOrHasChild(Element.as(t)))) {
      ge.stopPropagation();
      ge.preventDefault();
      if (type == Event.ONMOUSEWHEEL) {
        int v = ge.getMouseWheelVelocityY() * getCalculatedRowHeight();
        liveScroller.setScrollTop(liveScroller.getScrollTop() + v);
      } else {
        updateRows((int) Math.ceil((double) liveScroller.getScrollTop() / getCalculatedRowHeight()), false);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void initData(ListStore<M> ds, ColumnModel<M> cm) {
    if (cacheStoreHandler == null) {
      cacheStoreHandler = new StoreDataChangeHandler<M>() {

        @Override
        public void onDataChange(StoreDataChangeEvent<M> event) {
          liveStoreOffset = getLoaderOffset();

          if (GXTLogConfiguration.loggingIsEnabled()) {
            logger.finest("cache store handle onDataChanged liveStoreOffset: " + liveStoreOffset + " totalCount: "
                + getLoaderTotalCount());
          }

          if (totalCount != getLoaderTotalCount()) {
            totalCount = getLoaderTotalCount();
            int height = totalCount * getCalculatedRowHeight();
            // 1000000 as browser maxheight hack
            int count = height / 1000000;
            int h = 0;

            StringBuilder sb = new StringBuilder();

            if (count > 0) {
              h = height / count;

              for (int i = 0; i < count; i++) {
                sb.append("<div style=\"width: ");
                sb.append(barWidth);
                sb.append("px; height:");
                sb.append(h);
                sb.append("px;\">&nbsp;</div>");
              }
            }
            int diff = height - count * h;
            if (diff != 0) {
              sb.append("<div style=\"width: ");
              sb.append(barWidth);
              sb.append("px; height:");
              sb.append(diff);
              sb.append("px;\"></div>");
            }
            liveScroller.setInnerHTML(sb.toString());

          }
          if (totalCount > 0 && viewIndexReload != -1 && !isCached(viewIndexReload)) {
            loadLiveStore(getLiveStoreCalculatedIndex(viewIndexReload));
          } else {
            viewIndexReload = -1;
            ignoreScroll = true;
            int scrollTop = liveScroller.getScrollTop();
            liveScroller.setScrollTop(scrollTop - 1);
            liveScroller.setScrollTop(scrollTop);
            ignoreScroll = false;
            updateRows(viewIndex, true);
            isLoading = false;
            if (isMasked) {
              isMasked = false;
              scroller.unmask();
            }
          }

        }
      };

    }
    if (cacheStoreHandlerRegistration != null) {
      cacheStoreHandlerRegistration.removeHandler();
      cacheStoreHandlerRegistration = null;
      cacheStore = null;
    }
    if (loaderHandlerRegistration != null) {
      loaderHandlerRegistration.removeHandler();
      loaderHandlerRegistration = null;
    }
    if (ds != null) {
      cacheStore = new ListStore<M>(ds.getKeyProvider());
      cacheStoreHandlerRegistration = cacheStore.addStoreDataChangeHandler(cacheStoreHandler);
      Loader<Object, ListLoadResult<M>> l = (Loader<Object, ListLoadResult<M>>) grid.getLoader();
      loaderHandlerRegistration = l.addLoadHandler(new LoadResultListStoreBinding<Object, M, ListLoadResult<M>>(
          cacheStore));
    }
    super.initData(ds, cm);
  }

  protected boolean isCached(int index) {
    if ((cacheStore.size() == 0 && totalCount > 0) || (index < liveStoreOffset)
        || (index > (liveStoreOffset + getCacheSize() - getVisibleRowCount()))) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("isCached() FALSE index: " + index + " cacheSize: " + cacheStore.size() + " totalCount: "
            + totalCount + " liveStoreOffset: " + liveStoreOffset);
      }
      return false;
    }
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("isCached() TRUE index: " + index + " cacheSize: " + cacheStore.size() + " totalCount: "
          + totalCount + " liveStoreOffset: " + liveStoreOffset);
    }
    return true;
  }

  protected boolean isHorizontalScrollBarShowing() {
    return cm.getTotalWidth() + getScrollAdjust() > scroller.getOffsetWidth();
  }

  protected boolean loadLiveStore(int offset) {
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("loadLiveStore() offset: " + offset);
    }
    if (loaderTask == null) {
      loaderTask = new DelayedTask() {
        @Override
        public void onExecute() {
          doLoad();
        }
      };
    }
    loaderOffset = offset;
    loaderTask.delay(loadDelay);
    if (isLoading) {
      return true;
    } else {
      isLoading = true;
      return false;
    }
  }

  @Override
  protected void notifyShow() {
    super.notifyShow();
    updateRows((int) Math.ceil((double) liveScroller.getScrollTop() / getCalculatedRowHeight()), true);
  }

  @Override
  protected void onNoNext(final int index) {
    if (isLoading) {
      return;
    }
    if (viewIndex + 1 < getLoaderTotalCount()) {
      updateRows(viewIndex + 1, false);
      liveScroller.setScrollTop(liveScroller.getScrollTop() + getCalculatedRowHeight());
      // updateRow call may initiate new async call therefore we need to wait till data is returned before
      // updating the selection and scrolling into view
      if (!isLoading) {
        grid.getSelectionModel().select(index, false);
        ensureVisible(index, -1, false);
      } else {
        new NavKeyCallback<M>(grid, index);
      }
    }
  }

  @Override
  protected void onNoPrev() {
    if (isLoading) {
      return;
    }
    if (viewIndex > 0) {
      updateRows(viewIndex - 1, false);
      
      // updateRow call may initiate new async call therefore we need to wait till data is returned before
      // updating the selection and scrolling into view
      if (!isLoading) {
        grid.getSelectionModel().select(0, false);
        liveScroller.setScrollTop(liveScroller.getScrollTop() - getCalculatedRowHeight());
      } else {
        new NavKeyCallback<M>(grid, 0);
      }
    }
  }

  @Override
  protected void renderUI() {
    super.renderUI();
    scroller.getStyle().setOverflowY(Overflow.HIDDEN);

    liveScroller = grid.getElement().insertFirst(
        "<div style=\"position: absolute; right: 0px; overflow-y: scroll; overflow-x: hidden;z-index: 1;width: "
            + barWidth + "px;\"><div style=\"width: " + barWidth + "px;\">&nbsp;</div></div>");
    positionLiveScroller();

    liveScroller.addEventsSunk(Event.ONSCROLL);
    body.addEventsSunk(Event.ONMOUSEWHEEL);
  }

  @Override
  protected void resize() {
    final int oldCount = getVisibleRowCount();
    super.resize();
    if (dataTable != null) {
      resizeLiveScroller();
      scroller.setWidth(grid.getOffsetWidth() - getScrollAdjust(), true);
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          if (oldCount != getVisibleRowCount()) {
            updateRows(LiveGridView.this.viewIndex, true);
          }
        }
      });
    }
  }

  protected boolean shouldCache(int index) {
    int cz = getCacheSize();
    int i = (int) (cz * prefetchFactor);
    double low = liveStoreOffset + i;
    double high = liveStoreOffset + cz - getVisibleRowCount() - i;
    if ((index < low && liveStoreOffset > 0) || (index > high && liveStoreOffset != totalCount - cz)) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("shouldCache() TRUE index: " + index + " cache size: " + cz + " low: " + low + " high: " + high);
      }
      return true;
    }
    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("shouldCache() FALSE index: " + index + " cache size: " + cz + " low: " + low + " high: " + high);
    }
    return false;
  }

  @Override
  protected void updateAllColumnWidths() {
    super.updateAllColumnWidths();
    resizeLiveScroller();
    updateRows(viewIndex, false);
  }

  @Override
  protected void updateColumnHidden(int index, boolean hidden) {
    super.updateColumnHidden(index, hidden);
    resizeLiveScroller();
    updateRows(viewIndex, false);
  }

  @Override
  protected void updateColumnWidth(int col, int width) {
    super.updateColumnWidth(col, width);
    resizeLiveScroller();
    updateRows(viewIndex, false);
  }

  /**
   * Updates the rows based on the new index.
   * 
   * @param newIndex the new index
   * @param reload true to reload the data
   */
  protected void updateRows(int newIndex, boolean reload) {
    // the number of rows visible within the grid's viewport
    int rowCount = getVisibleRowCount();

    newIndex = Math.min(newIndex, Math.max(0, totalCount - rowCount));

    int diff = newIndex - viewIndex;

    // the difference from the view index and the new index
    int delta = Math.abs(diff);

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("updateRows() newIndex: " + newIndex + " viewIndex: " + viewIndex + " visible rows: " + rowCount
          + " diff: " + diff);
    }

    // nothing has changed and we are not forcing a reload
    if (delta == 0 && !reload) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("updateRows() nothing changed and not forcing reload");
      }
      return;
    }

    viewIndex = newIndex;
    int liveStoreIndex = Math.max(0, viewIndex - liveStoreOffset);

    // load data if not already cached
    if (!isCached(viewIndex)) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("updateRows() not cached, loading data");
      }
      maskView();
      if (loadLiveStore(getLiveStoreCalculatedIndex(viewIndex))) {
        viewIndexReload = viewIndex;
      }
      return;
    }

    // do pre caching
    if (shouldCache(viewIndex) && !isLoading) {
      loadLiveStore(getLiveStoreCalculatedIndex(viewIndex));
    }

    int rc = getVisibleRowCount();
    if (delta > rc - 1) {
      reload = true;
    }

    if (reload) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("updateRows() newIndex: " + newIndex + " viewIndex: " + viewIndex + " visible rows: " + rowCount
            + " diff: " + diff);
      }
      delta = diff = rc;
      if (ds.size() > 0) {
        boolean p = preventScrollToTopOnRefresh;
        preventScrollToTopOnRefresh = true;
        ds.clear();
        preventScrollToTopOnRefresh = p;
      }
    }

    if (delta == 0) {
      return;
    }

    int count = ds.size();
    if (diff > 0) {
      // rolling forward
      for (int c = 0; c < delta && c < count; c++) {
        ds.remove(0);
      }
      count = ds.size();
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("updateRows() forward adding to store from cache, liveStoreIndex: " + liveStoreIndex + " count: "
            + count);
      }
      ds.addAll(cacheStore.subList(liveStoreIndex + count, liveStoreIndex + count + delta));
    } else {
      // rolling back
      for (int c = 0; c < delta && c < count; c++) {
        ds.remove(count - c - 1);
      }

      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("updateRows() reverse adding to store from cache, liveStoreIndex: " + liveStoreIndex + " count: "
            + count);
      }

      ds.addAll(0, cacheStore.subList(liveStoreIndex, liveStoreIndex + delta));
    }

    fireEvent(new LiveGridViewUpdateEvent(liveStoreOffset, viewIndex, totalCount, rowCount));
  }

  private int getLoaderOffset() {
    if (grid.getLoader() instanceof PagingLoader<?, ?>) {
      return ((PagingLoader<?, ?>) grid.getLoader()).getOffset();
    } else {
      return 0;
    }
  }

  private int getLoaderTotalCount() {
    if (grid.getLoader() instanceof PagingLoader<?, ?>) {
      return ((PagingLoader<?, ?>) grid.getLoader()).getTotalCount();
    } else {
      return cacheStore.size();
    }
  }

  private void maskView() {
    if (!isMasked && grid.isLoadMask()) {
      scroller.mask(DefaultMessages.getMessages().loadMask_msg());
      isMasked = true;
    }
  }

  private void positionLiveScroller() {
    liveScroller.setTop(headerElem.getOffsetHeight());
  }

  private void resizeLiveScroller() {
    int h = grid.getElement().getHeight(true) - headerElem.getHeight(true);
    if (isHorizontalScrollBarShowing()) {
      h -= barWidth;
    }
    if (footer != null) {
      h -= footer.getOffsetHeight();
    }
    liveScroller.setHeight(h, true);

  }
}
