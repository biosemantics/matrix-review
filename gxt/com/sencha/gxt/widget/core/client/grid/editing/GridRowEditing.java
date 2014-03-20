/**
 * Sencha GXT 3.1.0-beta - Sencha for GWT
 * Copyright(c) 2007-2014, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package com.sencha.gxt.widget.core.client.grid.editing;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.GXTLogConfiguration;
import com.sencha.gxt.core.client.Style.Side;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ComponentHelper;
import com.sencha.gxt.widget.core.client.WidgetComponent;
import com.sencha.gxt.widget.core.client.button.ButtonBar;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent;
import com.sencha.gxt.widget.core.client.event.BodyScrollEvent.BodyScrollHandler;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent;
import com.sencha.gxt.widget.core.client.event.ColumnWidthChangeEvent.ColumnWidthChangeHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent;
import com.sencha.gxt.widget.core.client.event.RefreshEvent.RefreshHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.event.StartEditEvent;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.form.error.TitleErrorHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnHiddenChangeEvent.ColumnHiddenChangeHandler;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * Displays an editor for all cells in a row and allows all fields in row to be edited at the same time.
 * 
 * @param <M> the model type
 */
public class GridRowEditing<M> extends AbstractGridEditing<M> {

  public class DefaultRowEditorMessages implements RowEditorMessages {

    @Override
    public String cancelText() {
      return DefaultMessages.getMessages().rowEditor_cancelText();
    }

    @Override
    public String dirtyText() {
      return DefaultMessages.getMessages().rowEditor_dirtyText();
    }

    @Override
    public String errorTipTitleText() {
      return DefaultMessages.getMessages().rowEditor_tipTitleText();
    }

    @Override
    public String saveText() {
      return DefaultMessages.getMessages().rowEditor_saveText();
    }

  }

  public interface RowEditorAppearance {
    XElement getButtonWrap(XElement parent);

    XElement getContentWrap(XElement parent);

    String labelClass();

    void onResize(XElement parent, int width, int height);

    void render(SafeHtmlBuilder sb);
  }

  public interface RowEditorMessages {
    String cancelText();

    String dirtyText();

    String errorTipTitleText();

    String saveText();
  }

  protected class Handler extends AbstractGridEditing<M>.Handler implements ColumnWidthChangeHandler,
      ColumnHiddenChangeHandler, ResizeHandler, BodyScrollHandler, RefreshHandler {

    @Override
    public void onBodyScroll(BodyScrollEvent event) {
      positionButtons();
    }

    @Override
    public void onColumnHiddenChange(ColumnHiddenChangeEvent event) {
      cancelEditing();
    }

    @Override
    public void onColumnWidthChange(ColumnWidthChangeEvent event) {
      verifyLayout();

    }

    @Override
    public void onRefresh(RefreshEvent event) {
      cancelEditing();
    }

    @Override
    public void onResize(ResizeEvent event) {
      verifyLayout();
    }

  }

  protected class RowEditorComponent extends Component {

    private final RowEditorAppearance appearance;
    private ButtonBar buttonBar;
    private TextButton cancelBtn, saveBtn;
    private HBoxLayoutContainer con;

    public RowEditorComponent() {
      this(GWT.<RowEditorAppearance> create(RowEditorAppearance.class));
    }

    public RowEditorComponent(RowEditorAppearance appearance) {
      this.appearance = appearance;

      SafeHtmlBuilder sb = new SafeHtmlBuilder();
      appearance.render(sb);

      setElement((Element) XDOM.create(sb.toSafeHtml()));

      con = new HBoxLayoutContainer();
      con.setEnableOverflow(false);
      appearance.getContentWrap(getElement()).appendChild(con.getElement());

      buttonBar = new ButtonBar();
      buttonBar.setEnableOverflow(false);
      buttonBar.setHorizontalSpacing(4);
      buttonBar.setVerticalSpacing(1);
      appearance.getButtonWrap(getElement()).appendChild(buttonBar.getElement());

      cancelBtn = new TextButton(messages.cancelText());
      buttonBar.add(cancelBtn);

      saveBtn = new TextButton(messages.saveText());
      buttonBar.add(saveBtn);

      sinkEvents(Event.ONMOUSEDOWN);
    }
    
    /**
     * Returns the row editor appearance.
     * 
     * @return the appearance
     */
    public RowEditorAppearance getAppearance() {
      return appearance;
    }

    /**
     * Returns the row button bar.
     *
     * @return the {@link ButtonBar}.
     */
    public ButtonBar getButtonBar() {
      return buttonBar;
    }

    /**
     * Returns the cancel button.
     * 
     * @return the cancel button
     */
    public TextButton getCancelButton() {
      return cancelBtn;
    }

    /**
     * Returns the field container.
     * 
     * @return the field container
     */
    public HBoxLayoutContainer getFieldContainer() {
      return con;
    }

    /**
     * Returns the save button.
     * 
     * @return the save button
     */
    public TextButton getSaveButton() {
      return saveBtn;
    }

    @Override
    public void onBrowserEvent(Event event) {
      super.onBrowserEvent(event);
      if (event.getTypeInt() == Event.ONMOUSEDOWN) {
        // stop the mouse down to bubble to grid
        event.stopPropagation();
      }
    }

    @Override
    protected void doAttachChildren() {
      super.doAttachChildren();
      ComponentHelper.doAttach(buttonBar);
      ComponentHelper.doAttach(con);
    }

    @Override
    protected void doDetachChildren() {
      super.doDetachChildren();
      ComponentHelper.doDetach(buttonBar);
      ComponentHelper.doDetach(con);
    }

    @Override
    protected void onAfterFirstAttach() {
      super.onAfterFirstAttach();

      if (GXT.isIE7()) {
        buttonBar.forceLayout();
      }
    }

    @Override
    protected void onResize(final int width, final int height) {
      super.onResize(width, height);
      getAppearance().onResize(getElement(), width, height);
    }

  }

  protected RowEditorMessages messages = new DefaultRowEditorMessages();

  private final RowEditorComponent rowEditor;

  private static Logger logger = Logger.getLogger(GridRowEditing.class.getName());

  protected WidgetComponent toolTipAlignWidget;

  /**
   * Creates a new row editing instance.
   * 
   * @param editableGrid the target grid
   */
  public GridRowEditing(Grid<M> editableGrid) {
    setEditableGrid(editableGrid);

    rowEditor = createRowEditor();
    rowEditor.addAttachHandler(new AttachEvent.Handler() {

      @Override
      public void onAttachOrDetach(AttachEvent event) {
        positionButtons();
      }
    });
  }

  @Override
  public void cancelEditing() {
    if (activeCell != null) {
      final GridCell editCell = activeCell;
      removeEditor();
      fireEvent(new CancelEditEvent<M>(editCell));
    }
  }

  @Override
  public void completeEditing() {
    if (activeCell != null) {
      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("completeEditing activeCell not null");
      }
      for (int i = 0, len = columnModel.getColumnCount(); i < len; i++) {
        ColumnConfig<M, ?> c = columnModel.getColumn(i);
        doCompleteEditing(c);
      }
      final GridCell editCell = activeCell;
      removeEditor();
      fireEvent(new CompleteEditEvent<M>(editCell));
    }
  }

  /**
   * Returns the row editor button bar with the Cancel and Save buttons. This can be used to add a button.
   * <p/>
   * <ul>
   * <li>Add a custom button to the button bar.</li>
   * <li>Get the model with {@link com.sencha.gxt.widget.core.client.grid.Grid#getSelectionModel()}</li>
   * <li>Close the editing with {@link #cancelEditing()}</li>
   * </ul>
   * <p/>
   * <pre>
   * // example creating a delete button
   * TextButton deleteBtn = new TextButton("Delete");
   * deleteBtn.addSelectHandler(new SelectEvent.SelectHandler() {
   *   public void onSelect(SelectEvent event) {
   *     // close the editing
   *     editing.cancelEditing();
   *     // get the model
   *     StateModel item = grid.getSelectionModel().getSelectedItem();
   *   }
   * });
   * ButtonBar buttonBar = rowEditing.getButtonBar();
   * buttonBar.add(deleteBtn);
   * </pre>
   *
   * @return the {@link ButtonBar}.
   */
  public ButtonBar getButtonBar() {
    return rowEditor.getButtonBar();
  }

  /**
   * Returns the cancel button.
   *
   * @return the cancel button
   */
  public TextButton getCancelButton() {
    return rowEditor.getCancelButton();
  }

  /**
   * Returns the row editor messages.
   * 
   * @return the messages
   */
  public RowEditorMessages getMessages() {
    return messages;
  }

  /**
   * Returns the save button.
   *
   * @return the save button
   */
  public TextButton getSaveButton() {
    return rowEditor.getSaveButton();
  }

  @Override
  public void setEditableGrid(Grid<M> editableGrid) {
    super.setEditableGrid(editableGrid);
    if (editableGrid != null) {
      groupRegistration.add(columnModel.addColumnHiddenChangeHandler(ensureInternHandler()));
      groupRegistration.add(columnModel.addColumnWidthChangeHandler(ensureInternHandler()));
      groupRegistration.add(editableGrid.addRefreshHandler(ensureInternHandler()));
      groupRegistration.add(editableGrid.addResizeHandler(ensureInternHandler()));
      groupRegistration.add(editableGrid.addBodyScrollHandler(ensureInternHandler()));
    }
  }

  /**
   * Sets the row editor messages.
   * 
   * @param messages the messages
   */
  public void setMessages(RowEditorMessages messages) {
    this.messages = messages;

    rowEditor.getCancelButton().setText(messages.cancelText());
    rowEditor.getSaveButton().setText(messages.saveText());
  }

  @Override
  public void startEditing(GridCell cell) {
    if (getEditableGrid() != null && getEditableGrid().isAttached() && isEditing()) {
      if (!isValid()) {
        showTooltip(getErrorHtml());
        return;
      }
    }

    if (getEditableGrid() != null && getEditableGrid().isAttached() && cell != null) {
      BeforeStartEditEvent<M> ce = new BeforeStartEditEvent<M>(cell);
      fireEvent(ce);
      if (ce.isCancelled()) {
        return;
      }
      cancelEditing();
      M value = getEditableGrid().getStore().get(cell.getRow());

      if (GXTLogConfiguration.loggingIsEnabled()) {
        logger.finest("startEditing cell = " + cell + " start value = " + value);
      }

      activeCell = cell;
      getEditableGrid().getView().getEditorParent().appendChild(rowEditor.getElement());
      ComponentHelper.doAttach(rowEditor);

      Container con = rowEditor.getFieldContainer();
      con.clear();

      int adj = 1;
      for (int i = 0, len = columnModel.getColumnCount(); i < len; i++) {
        ColumnConfig<M, ?> c = columnModel.getColumn(i);

        final Widget w = doStartEditing(c, value);
        if (w != null) {
          BoxLayoutData ld = new BoxLayoutData();
          ld.setMargins(new Margins(1, 2, 2, 2 + adj));
          w.setLayoutData(ld);
          con.add(w);
          adj = 0;
        }
      }

      Point p = XElement.as(getEditableGrid().getView().getRow(cell.getRow())).getXY();
      rowEditor.setPagePosition(p.getX(), p.getY());

      verifyLayout();

      startMonitoring();
      positionButtons();

      focusField(activeCell);
      fireEvent(new StartEditEvent<M>(activeCell));
    }
  }

  protected void bindHandler() {
    if (toolTipAlignWidget.getElement().getOffsetParent() == null
        || !this.editableGrid.getElement().getBounds().contains(toolTipAlignWidget.getElement().getXY())) {
      hideTooltip();
      return;
    }

    super.bindHandler();
    if (rowEditor.getSaveButton() != null) {
      rowEditor.getSaveButton().setEnabled(isValid());
    }
  }

  protected RowEditorComponent createRowEditor() {
    RowEditorComponent rowEditor = new RowEditorComponent();
    rowEditor.getSaveButton().addSelectHandler(new SelectHandler() {

      @Override
      public void onSelect(SelectEvent event) {
        completeEditing();

      }
    });

    rowEditor.getCancelButton().addSelectHandler(new SelectHandler() {
      @Override
      public void onSelect(SelectEvent event) {
        cancelEditing();
      }
    });
    return rowEditor;
  }

  @SuppressWarnings("unchecked")
  protected <N, O> void doCompleteEditing(ColumnConfig<M, N> c) {
    if (activeCell != null) {
      Field<O> field = getEditor(c);

      if (field != null) {
        Converter<N, O> converter = getConverter(c);

        O fieldValue = field.getValue();

        N convertedValue;
        if (converter != null) {
          convertedValue = converter.convertFieldValue(fieldValue);
        } else {
          convertedValue = (N) fieldValue;
        }

        if (GXTLogConfiguration.loggingIsEnabled()) {
          logger.finest("doCompleteEditng convertedValue = " + convertedValue);
        }

        ListStore<M> store = getEditableGrid().getStore();
        ListStore<M>.Record r = store.getRecord(store.get(activeCell.getRow()));
        r.addChange(c.getValueProvider(), convertedValue);
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected <N, O> Widget doStartEditing(ColumnConfig<M, N> c, M value) {
    if (c.isHidden()) {
      return null;
    }

    Field<O> f = getEditor(c);

    Converter<N, O> converter = getConverter(c);

    ValueProvider<? super M, N> v = c.getValueProvider();
    N colValue = getEditableGrid().getStore().hasRecord(value)
        ? getEditableGrid().getStore().getRecord(value).getValue(v) : v.getValue(value);
    O convertedValue;
    if (converter != null) {
      convertedValue = converter.convertModelValue(colValue);
    } else {
      convertedValue = (O) colValue;
    }

    if (GXTLogConfiguration.loggingIsEnabled()) {
      logger.finest("doStartEditing convertedValue = " + convertedValue);
    }

    if (f != null) {
      f.setValue(convertedValue);

      if (!(f.getErrorSupport() == null || f.getErrorSupport() instanceof TitleErrorHandler)) {
        f.setErrorSupport(new TitleErrorHandler(f));
      }

      return f;
    } else {
      Label l = new Label();
      l.addStyleName(rowEditor.getAppearance().labelClass());
      l.setText(convertedValue != null ? convertedValue.toString() : "");
      return l;
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  protected Handler ensureInternHandler() {
    if (handler == null) {
      handler = new Handler();
    }
    return (Handler) handler;
  }

  @Override
  protected SafeHtml getErrorHtml() {
    SafeHtmlBuilder sb = new SafeHtmlBuilder();
    sb.appendHtmlConstant("<ul>");

    for (int i = 0; i < rowEditor.getFieldContainer().getWidgetCount(); i++) {
      Widget widget = rowEditor.getFieldContainer().getWidget(i);

      if (!(widget instanceof Field)) {
        continue;
      }

      Field<?> f = (Field<?>) widget;

      getErrorMessage(f, sb, getEditableGrid().getColumnModel().getColumn(i).getHeader().asString());

    }
    sb.appendHtmlConstant("</ul>");
    return sb.toSafeHtml();
  }

  protected boolean isValid() {
    for (int i = 0, len = rowEditor.getFieldContainer().getWidgetCount(); i < len; i++) {

      Widget w = rowEditor.getFieldContainer().getWidget(i);
      if (w instanceof ValueBaseField<?>) {
        ValueBaseField<?> f = (ValueBaseField<?>) w;
        if (!f.isCurrentValid(true)) {
          return false;
        }
      } else if (w instanceof Field<?>) {
        Field<?> f = (Field<?>) w;
        if (!f.isValid(true)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  protected void onEnter(NativeEvent evt) {
    //
  }

  @Override
  protected void onScroll(ScrollEvent event) {
    positionButtons();
  }

  protected void positionButtons() {
    if (rowEditor.isVisible()) {
      GridView<M> view = getEditableGrid().getView();
      int scroll = view.getScrollState().getX();
      int mainBodyWidth = view.getScroller().getWidth(true);
      int h = rowEditor.getElement().getClientHeight();

      if (toolTipAlignWidget == null) {
        toolTipAlignWidget = new WidgetComponent(new HTML());
        rowEditor.getAppearance().getContentWrap(rowEditor.getElement()).insertFirst(toolTipAlignWidget.getElement());
        ComponentHelper.doAttach(toolTipAlignWidget);
      }

      if (toolTipAlignWidget != null) {
        toolTipAlignWidget.getElement().getStyle().setProperty("position", "absolute");
        toolTipAlignWidget.setPixelSize(mainBodyWidth
            - (view.getScroller().isScrollableY() ? XDOM.getScrollBarWidth() : 0), h);
        toolTipAlignWidget.setPosition(scroll, -1);
      }
    }
  }

  protected void showTooltip(SafeHtml msg) {
    if (tooltip == null) {
      ToolTipConfig config = new ToolTipConfig();
      config.setAutoHide(false);
      config.setMouseOffsetX(0);
      config.setMouseOffsetY(0);
      config.setAnchor(Side.LEFT);
      config.setTitleHtml(getMessages().errorTipTitleText());
      tooltip = new ToolTip(toolTipAlignWidget, config);
      tooltip.setMaxWidth(600);
    }
    ToolTipConfig config = tooltip.getToolTipConfig();
    config.setBodyHtml(msg);
    tooltip.update(config);
    tooltip.enable();
    if (!tooltip.isAttached()) {
      tooltip.show();
      tooltip.getElement().updateZIndex(0);
    }
  }

  protected void verifyLayout() {
    if (isEditing()) {
      // border both sides
      int adj = 2;
      rowEditor.setWidth(columnModel.getTotalWidth(false) + adj);

      for (int i = 0, j = 0, len = columnModel.getColumnCount(); i < len; i++) {
        ColumnConfig<M, ?> c = columnModel.getColumn(i);
        if (c.isHidden()) {
          continue;
        }

        Widget w = rowEditor.getFieldContainer().getWidget(j++);
        int width = c.getWidth();

        Object layoutData = w.getLayoutData();
        if (layoutData instanceof MarginData) {
          Margins m = ((MarginData) layoutData).getMargins();
          if (m != null) {
            width -= m.getLeft();
            width -= m.getRight();
          }
        }
        if (w instanceof Field<?>) {

          ((Field<?>) w).setWidth(width);
        } else {
          XElement.as(w.getElement()).setWidth(c.getWidth(), true);
        }

      }
      rowEditor.getFieldContainer().forceLayout();

    }
    positionButtons();
  }

  private void focusField(GridCell activeCell) {
    Widget w = activeCell.getCol() < 0 || activeCell.getCol() > rowEditor.getFieldContainer().getWidgetCount() ? null
        : getEditor(columnModel.getColumn(activeCell.getCol()));

    if (!(w instanceof Field<?>)) {
      for (Widget widget : rowEditor.getFieldContainer()) {
        if (widget instanceof Field<?>) {
          w = widget;
          break;
        }
      }
    }
    if (w instanceof Field<?>) {
      final Field<?> field = (Field<?>) w;
      Scheduler.get().scheduleDeferred(new ScheduledCommand() {
        @Override
        public void execute() {
          if (isEditing()) {
            field.focus();
          }
        }
      });
    }

  }

  private void removeEditor() {
    stopMonitoring();
    hideTooltip();
    activeCell = null;
    ComponentHelper.doDetach(rowEditor);
    rowEditor.getElement().removeFromParent();
    rowEditor.getFieldContainer().clear();
  }
}
