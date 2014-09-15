package edu.arizona.biosemantics.matrixreview.client.matrix.menu.dnd;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.core.client.dom.AutoScrollSupport;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.dnd.core.client.DndDragCancelEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragLeaveEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.Insert;
import com.sencha.gxt.dnd.core.client.TreeGridDropTarget;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.Container;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

import edu.arizona.biosemantics.matrixreview.client.event.LoadModelEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MatrixModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxonFlatEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.TaxonStore;
import edu.arizona.biosemantics.matrixreview.shared.model.MatrixMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon.Rank;

public class UpdateModelDropTarget extends TreeGridDropTarget<Taxon> {

	private TaxonStore taxonStore;
	private Model model;
	private EventBus eventBus;
	private MatrixMode matrixMode = MatrixMode.HIERARCHY; 
    private AutoScrollSupport scrollSupport;
	private TreeGrid<Taxon> tree;
	
	public UpdateModelDropTarget(EventBus eventBus, TreeGrid<Taxon> tree, Model model, TaxonStore taxonStore) {
		super(tree);
		this.tree = tree;
		this.eventBus = eventBus;
		this.model = model;
		this.taxonStore = taxonStore;
		setAllowSelfAsSource(true);
		setAllowDropOnLeaf(true);
		setFeedback(Feedback.BOTH);
		
		addEventHandlers();
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
    protected void onDragFail(DndDropEvent event) {
        super.onDragFail(event);
        scrollSupport.stop();
    }


    @Override
    protected void onDragLeave(DndDragLeaveEvent event) {
        super.onDragLeave(event);
        scrollSupport.stop();
    }
    
    @Override
    protected void onDragEnter(DndDragEnterEvent e) {
        if (scrollSupport == null) {
            scrollSupport = new AutoScrollSupport(tree.getView().getScroller());
        } else if (scrollSupport.getScrollElement() == null) {
            scrollSupport.setScrollElement(tree.getView().getScroller());
        }
        scrollSupport.start();
        super.onDragEnter(e);
    }
    
	
	private void addEventHandlers() {
		eventBus.addHandler(LoadModelEvent.TYPE, new LoadModelEvent.LoadModelEventHandler() {
			@Override
			public void onLoad(LoadModelEvent event) {
				model = event.getModel();
			}
		});
		eventBus.addHandler(MatrixModeEvent.TYPE, new MatrixModeEvent.MatrixModeEventHandler() {
			@Override
			public void onMode(MatrixModeEvent event) {
				matrixMode = event.getMode();
				
				switch(matrixMode) {
				case FLAT:
					setFeedback(Feedback.INSERT);
					break;
				case HIERARCHY:
					setFeedback(Feedback.BOTH);
					break;
				default:
					break;
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected void appendModel(Taxon p, List<?> items, int index) {
		if (items.size() == 0)
			return;
		
		List<Taxon> models = null;
		if (items.get(0) instanceof TreeStore.TreeNode) {
			@SuppressWarnings("unchecked")
			List<TreeStore.TreeNode<Taxon>> nodes = (List<TreeStore.TreeNode<Taxon>>) items;			
			models = new LinkedList<Taxon>();
			for(TreeStore.TreeNode<Taxon> node : nodes) {
				models.add(node.getData());
			}
		} else {
			models = (List<Taxon>) items;
		}
		
		boolean validDrop = true;
		for(int i=0; i<models.size(); i++)
			if(!Rank.isValidParentChild(p == null ? null : p.getRank(), models.get(i).getRank()))
				validDrop = false;
		
		if(validDrop) {
			switch(matrixMode) {
			case FLAT:
				eventBus.fireEvent(new MoveTaxonFlatEvent(models, index == 0 ? null : taxonStore.getChild(index - 1)));
				break;
			case HIERARCHY:
				eventBus.fireEvent(new MoveTaxaEvent(p, index, models));
				break;
			
			}
		}
		
		/*
		if (p == null) {
			getWidget().getTreeStore().addSubTree(index, nodes);
			for(int i=0; i<nodes.size(); i++) {
				//eventBus.fireEvent(new ModifyTaxonEvent());
				taxonMatrix.addTaxon(index + i, nodes.get(i).getData());
			}
		} else {
			getWidget().getTreeStore().addSubTree(p, index, nodes);
			for(int i=0; i<nodes.size(); i++) {
				taxonMatrix.addChild(p, index + i, nodes.get(i).getData());
			}
		}*/
		
		/*if (p == null) {
			getWidget().getTreeStore().insert(index, models);
			for(int i=0; i<models.size(); i++)
				taxonMatrix.addTaxon(index + i, models.get(i));
		} else {
			getWidget().getTreeStore().insert(p, index, models);
			for(int i=0; i<models.size(); i++)
				taxonMatrix.addChild(p, index + i, models.get(i));
		}*/
	}
	
	  @Override
	  protected void showFeedback(DndDragMoveEvent event) {
		switch (matrixMode) {
		case FLAT:
			super.showFeedback(event);
			break;
		case HIERARCHY:
			// TODO this might not get the right element
		    final TreeNode<Taxon> item = getWidget().findNode(
		        event.getDragMoveEvent().getNativeEvent().getEventTarget().<Element> cast());
		    if (item == null) {
		      if (activeItem != null) {
		        clearStyle(activeItem);
		      }
		    }

		    if (item != null && event.getDropTarget().getWidget() == event.getDragSource().getWidget()) {
		      @SuppressWarnings("unchecked")
		      TreeGrid<Taxon> source = (TreeGrid<Taxon>) event.getDragSource().getWidget();
		      List<Taxon> list = source.getSelectionModel().getSelection();
		      Taxon overModel = item.getModel();
		      for (int i = 0; i < list.size(); i++) {
		        Taxon sel = list.get(i);
		        
		        if(!Rank.isValidParentChild(overModel == null ? null : overModel.getRank(), sel.getRank())) {
		          Insert.get().hide();
			      event.getStatusProxy().setStatus(false);
			      return;
		        }
		        if (overModel == sel) {
		          Insert.get().hide();
		          event.getStatusProxy().setStatus(false);
		          return;
		        }
		        List<Taxon> children = getWidget().getTreeStore().getAllChildren(sel);
		        if (children.contains(item.getModel())) {
		          Insert.get().hide();
		          event.getStatusProxy().setStatus(false);
		          return;
		        }
		      }
		    }

		    boolean append = feedback == Feedback.APPEND || feedback == Feedback.BOTH;
		    boolean insert = feedback == Feedback.INSERT || feedback == Feedback.BOTH;

		    if (item == null) {
		      handleAppend(event, item);
		    } else if (insert) {
		      handleInsert(event, item);
		    } else if ((!getWidget().isLeaf(item.getModel()) || allowDropOnLeaf) && append) {
		      handleAppend(event, item);
		    } else {
		      if (activeItem != null) {
		        clearStyle(activeItem);
		      }
		      status = -1;
		      activeItem = null;
		      appendItem = null;
		      Insert.get().hide();
		      event.getStatusProxy().setStatus(false);
		    }
			break;

		}
	  }

}
