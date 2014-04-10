package com.sencha.gxt.dnd.core.client;

import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class MyGridDropTarget<T> extends GridDropTarget<T> {

    private AutoScrollSupport scrollSupport;
    private Container scrollContainer;
    
    public MyGridDropTarget(Grid<T> grid) {
        super(grid);
        this.scrollContainer = scrollContainer;
    }


    @Override
    protected void onDragCancelled(DndDragCancelEvent event) {
        super.onDragCancelled(event);
        scrollSupport.stop();
    }


    @Override
    protected void onDragDrop(DndDropEvent e) {
        super.onDragDrop(e);
        scrollSupport.stop();
    }


    @Override
    protected void onDragEnter(DndDragEnterEvent e) {
        if (scrollSupport == null) {
            scrollSupport = new AutoScrollSupport(grid.getView().getScroller());
        } else if (scrollSupport.getScrollElement() == null) {
            scrollSupport.setScrollElement(grid.getView().getScroller());
        }
        scrollSupport.start();
        super.onDragEnter(e);
    }


    @Override
    protected void onDragFail(DndDropEvent event) {
        super.onDragFail(event);
        scrollSupport.stop();
    }


    @Override
    protected void onDragLeave(DndDragLeaveEvent event) {
        super.onDragLeave(event);
        scrollSupport.stop();
    }
}