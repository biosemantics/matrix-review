package com.sencha.gxt.dnd.core.client;

import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.dnd.core.client.DndDragCancelEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class MyGridDropTarget<T> extends GridDropTarget<T> {

    private AutoScrollSupport scrollSupport;
    private Container scrollContainer;
    
    public MyGridDropTarget(Grid<T> grid, Container scrollContainer) {
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
            scrollSupport = new AutoScrollSupport(scrollContainer.getElement());
        } else if (scrollSupport.getScrollElement() == null) {
            scrollSupport.setScrollElement(scrollContainer.getElement());
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