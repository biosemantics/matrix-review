package edu.arizona.biosemantics.matrixreview.client.matrix.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.data.shared.TreeStore.TreeModel;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.dnd.core.client.DND.Feedback;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.TreeDragSource;
import com.sencha.gxt.dnd.core.client.TreeDropTarget;
import com.sencha.gxt.messages.client.DefaultMessages;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.AdapterField;
import com.sencha.gxt.widget.core.client.form.DualListField.DualListFieldAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree;

public class DualTreeField<M, T> extends AdapterField<List<M>> {

	public interface DualTreeFieldAppearance {
		IconConfig allLeft();

		IconConfig allRight();

		IconConfig down();

		IconConfig left();

		IconConfig right();

		IconConfig up();

	}

	/**
	 * The locale-sensitive messages used by this class.
	 */
	public interface DualTreeFieldMessages {
		String addAll();

		String addSelected();

		String moveDown();

		String moveUp();

		String removeAll();

		String removeSelected();
	}

	/**
	 * The DND mode enumeration.
	 */
	public enum Mode {
		INSERT, INSERT_MAINTAIN_ANCESTRY
	}

	protected class DualTreeFieldDefaultMessages implements
			DualTreeFieldMessages {

		@Override
		public String addAll() {
			return "add all message";
			//return DefaultMessages.getMessages().listField_addAll();
		}

		@Override
		public String addSelected() {
			return "add all selected message";
			//return DefaultMessages.getMessages().listField_addSelected();
		}

		@Override
		public String moveDown() {
			return "move selecdted down message";
			//return DefaultMessages.getMessages().listField_moveSelectedDown();
		}

		@Override
		public String moveUp() {
			return "move selecdted up message";
			//return DefaultMessages.getMessages().listField_moveSelectedUp();
		}

		@Override
		public String removeAll() {
			return "remove all message";
			//return DefaultMessages.getMessages().listField_removeAll();
		}

		@Override
		public String removeSelected() {
			return "move selecdted message";
			//return DefaultMessages.getMessages().listField_removeSelected();
		}

	}

	protected Mode mode = Mode.INSERT;
	private TreeStore<M> toStore;
	private TreeStore<M> fromStore;
	private DualTreeFieldAppearance appearance;
	private VerticalPanel buttonBar;
	private Tree<M, T> fromView;
	private Tree<M, T> toView;
	private IconButton up;
	private IconButton allRight;
	private IconButton allLeft;
	private IconButton right;
	private IconButton left;
	private DualTreeFieldMessages messages;
	private IconButton down;
	private String dndGroup;
	private TreeDragSource<M> sourceFromField;
	private TreeDragSource<M> sourceToField;
	private TreeDropTarget<M> targetFromField;
	private TreeDropTarget<M> targetToField;
	private boolean enableDnd = true;

	public DualTreeField(ModelKeyProvider<? super M> keyProvider,
			ValueProvider<? super M, T> valueProvider, Cell<T> cell) {
		this(new TreeStore<M>(keyProvider), new TreeStore<M>(keyProvider),
				valueProvider, cell);
	}

	@UiConstructor
	public DualTreeField(TreeStore<M> fromStore, TreeStore<M> toStore,
			ValueProvider<? super M, T> valueProvider, Cell<T> cell) {
		this(fromStore,	toStore, valueProvider,	cell, 
				GWT.<DualTreeFieldAppearance> create(DualTreeFieldAppearance.class));
	}

	public DualTreeField(TreeStore<M> fromStore, TreeStore<M> toStore,
			ValueProvider<? super M, T> valueProvider, Cell<T> cell,
			DualTreeFieldAppearance appearance) {
		super(new HorizontalPanel());

		this.appearance = appearance;
		
		this.fromStore = fromStore;
		this.toStore = toStore;
		HorizontalPanel panel = (HorizontalPanel) getWidget();
		this.buttonBar = new VerticalPanel();

		fromView = new Tree<M, T>(this.fromStore, valueProvider);
		fromView.setCell(cell);
		fromView.setWidth(125);
		toView = new Tree<M, T>(this.toStore, valueProvider);
		toView.setCell(cell);
		toView.setWidth(125);

		buttonBar.setSpacing(3);
		buttonBar.getElement().getStyle().setProperty("margin", "7px");
		buttonBar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		up = new IconButton(appearance.up());
		up.setToolTip(getMessages().moveUp());
		up.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				onUp();
			}
		});

		allRight = new IconButton(appearance.allRight());
		allRight.setToolTip(getMessages().addAll());
		allRight.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				onAllRight();
			}
		});

		right = new IconButton(appearance.right());
		right.setToolTip(getMessages().addSelected());
		right.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				onRight(fromView.getSelectionModel().getSelectedItems());
			}
		});

		left = new IconButton(appearance.left());
		left.setToolTip(getMessages().removeSelected());
		left.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				onLeft(toView.getSelectionModel().getSelectedItems());
			}
		});

		allLeft = new IconButton(appearance.allLeft());
		allLeft.setToolTip(getMessages().removeAll());
		allLeft.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				onAllLeft();
			}
		});

		down = new IconButton(appearance.down());
		down.setToolTip(getMessages().moveDown());
		down.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				onDown();
			}
		});

		buttonBar.add(up);
		buttonBar.add(allRight);
		buttonBar.add(right);
		buttonBar.add(left);
		buttonBar.add(allLeft);
		buttonBar.add(down);

		panel.add(fromView);
		panel.add(buttonBar);
		panel.add(toView);

		setMode(mode);
		setPixelSize(200, 125);
	}
	
	public DualTreeFieldAppearance getAppearance() {
		return appearance;
	}

	/**
	 * Returns the DND group name.
	 * 
	 * @return the group name
	 */
	public String getDndGroup() {
		return dndGroup;
	}

	/**
	 * Returns the from field's drag source instance.
	 * 
	 * @return the drag source
	 */
	public TreeDragSource<M> getDragSourceFromField() {
		return sourceFromField;
	}

	/**
	 * Returns the to field's drag source instance.
	 * 
	 * @return the drag source
	 */
	public TreeDragSource<M> getDragSourceToField() {
		return sourceToField;
	}

	/**
	 * Returns the from field's drop target instance.
	 * 
	 * @return the drag source
	 */
	public TreeDropTarget<M> getDropTargetFromField() {
		return targetFromField;
	}

	/**
	 * Returns the to field's drop target instance.
	 * 
	 * @return the drag source
	 */
	public TreeDropTarget<M> getDropTargetToField() {
		return targetToField;
	}

	/**
	 * Returns the list view that provides the source of selectable items.
	 * 
	 * @return the list view that provides the source of selectable items
	 */
	public Tree<M, T> getFromView() {
		return fromView;
	}

	/**
	 * Returns the ListStore that manages the source of selectable items.
	 * 
	 * @return the list store that manages the source of selectable items
	 */
	public TreeStore<M> getFromStore() {
		return fromStore;
	}

	/**
	 * Returns the list field's mode.
	 * 
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * Returns the list view that provides the destination for selectable items.
	 * 
	 * @return the list view that provides the destination for selectable items
	 */
	public Tree<M, T> getToView() {
		return toView;
	}
	
	public TreeStore<M> getToStore() {
		return toStore;
	}

	@Override
	public List<M> getValue() {
		return toStore.getAll();
	}
	
	public List<M> getRootValues() {
		return toStore.getRootItems();
	}

	public DualTreeFieldMessages getMessages() {
		if (messages == null) {
			messages = new DualTreeFieldDefaultMessages();
		}
		return messages;
	}

	/**
	 * Returns true if drag and drop is enabled.
	 * 
	 * @return true if drag and drop is enabled
	 */
	public boolean isEnableDnd() {
		return enableDnd;
	}

	/**
	 * Sets the drag and drop group name. A group name will be generated if none
	 * is specified.
	 * 
	 * @param group
	 *            the group name
	 */
	public void setDndGroup(String group) {
		if (group == null) {
			group = getId() + "-group";
		}
		this.dndGroup = group;
		if (sourceFromField != null) {
			sourceFromField.setGroup(dndGroup);
		}
		if (sourceToField != null) {
			sourceToField.setGroup(dndGroup);
		}
		if (targetFromField != null) {
			targetFromField.setGroup(dndGroup);
		}
		if (targetToField != null) {
			targetToField.setGroup(dndGroup);
		}
	}

	/**
	 * True to allow selections to be dragged and dropped between lists
	 * (defaults to true).
	 * 
	 * @param enableDnd
	 *            true to enable drag and drop
	 */
	public void setEnableDnd(boolean enableDnd) {
		if (enableDnd) {
			if (sourceFromField == null) {
				sourceFromField = new TreeDragSource<M>(fromView) {
					@Override
					protected void onDragDrop(DndDropEvent event) {
						//already taken care of by target drop
					}
				};
				sourceToField = new TreeDragSource<M>(toView) {
					@Override
					protected void onDragDrop(DndDropEvent event) {
						//already taken care of by target drop
					}
				};

				targetFromField = new TreeDropTarget<M>(fromView) {
					@Override
					protected void onDragDrop(DndDropEvent event) {
						// just so super class won't handle any actions
						activeItem = null;
						status = -2;

						List<TreeNode<M>> droppedItems = (List<TreeNode<M>>) event.getData();
						List<M> selection = new LinkedList<M>();
						for(TreeNode<M> item : droppedItems) 
							selection.add(item.getData());
						if (selection.size() > 0) {
							onLeft(selection);
						}
						super.onDragDrop(event);
					}
				};
				//none really needed since 'simple' drop implemented that only mimics button press
				//targetFromField.setAutoScroll(true);
				//targetFromField.setAutoExpand(true);
				targetToField = new TreeDropTarget<M>(toView) {
					@Override
					protected void onDragDrop(DndDropEvent event) {
						// just so super class won't handle any actions
						activeItem = null;
						status = -2;

						List<TreeNode<M>> droppedItems = (List<TreeNode<M>>) event.getData();
						List<M> selection = new LinkedList<M>();
						for(TreeNode<M> item : droppedItems) 
							selection.add(item.getData());
						if (selection.size() > 0) {
							onRight(selection);
						}
						super.onDragDrop(event);
					}
				};
				//none really needed since 'simple' drop implemented that only mimics button press
				//targetToField.setAutoExpand(true);
				//targetToField.setAutoScroll(true);

				if (mode == Mode.INSERT || mode == Mode.INSERT_MAINTAIN_ANCESTRY) {
					targetToField.setAllowSelfAsSource(true);
					targetFromField.setFeedback(Feedback.APPEND);
					targetToField.setFeedback(Feedback.APPEND);
				}

				setDndGroup(dndGroup);
			}

		} else {
			if (sourceFromField != null) {
				sourceFromField.release();
				sourceFromField = null;
			}
			if (sourceToField != null) {
				sourceToField.release();
				sourceToField = null;
			}
			if (targetFromField != null) {
				targetFromField.release();
				targetFromField = null;
			}
			if (targetToField != null) {
				targetToField.release();
				targetToField = null;
			}
		}

		this.enableDnd = enableDnd;
	}

	/**
	 * Sets the local-sensitive messages used by this class.
	 * 
	 * @param messages
	 *            the locale sensitive messages used by this class.
	 */
	public void setMessages(DualTreeFieldMessages messages) {
		this.messages = messages;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
		switch (mode) {
		//case APPEND:
		//	up.setVisible(false);
		//	down.setVisible(false);
		//	break;
		case INSERT:
		case INSERT_MAINTAIN_ANCESTRY:
			up.setVisible(true);
			down.setVisible(true);
			break;
		}
	}

	@Override
	public void setValue(List<M> value) {
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		fromView.disable();
		toView.disable();
		allLeft.disable();
		allRight.disable();
		right.disable();
		left.disable();
		up.disable();
		down.disable();
	}

	protected void onDown() {
		List<M> selection = toView.getSelectionModel().getSelectedItems();
		for(M item : selection) {
			M parent = toStore.getParent(item);
			if(parent == null) {
				List<M> children = toStore.getRootItems();
				toStore.addSubTree(0, moveDown(item, children));
			} else {
				/*
				currently not possible to use replaceSubTree, see bug:
				http://www.sencha.com/forum/showthread.php?282015-The-following-method-does-not-work-replaceSubTree-in-class-TreeStore.
				
				TreeNode<M> subTree = toStore.getSubTree(parent);
				List<TreeNode<M>> childrenSubtrees = new ArrayList<TreeNode<M>>(subTree.getChildren());
				for(TreeNode<M> child : childrenSubtrees)
					System.out.println(toStore.getKeyProvider().getKey(child.getData()));
						
				int oldIndex = childrenSubtrees.indexOf(toStore.getSubTree(item));
				if(oldIndex >= 0 && oldIndex < childrenSubtrees.size() - 1) 
					Collections.swap(childrenSubtrees, oldIndex, oldIndex + 1);
				toStore.replaceSubTree(parent, childrenSubtrees);*/
				
				//workaround:
				boolean expand = toView.isExpanded(parent);
				List<M> children = toStore.getChildren(parent);
				toStore.addSubTree(parent, 0, moveDown(item, children));
				toView.setExpanded(parent, expand);
			}
		}
		toView.getSelectionModel().setSelection(selection);
	}

	private List<TreeNode<M>> moveDown(M item, List<M> children) {
		List<TreeNode<M>> childrenSubtrees = new ArrayList<TreeNode<M>>();
		int oldIndex = -1;
		for(int i=0; i<children.size(); i++) {
			M child = children.get(i);
			if(child.equals(item))
				oldIndex = i;
			TreeNode<M> childSubTree = toStore.getSubTree(child);
			toStore.remove(child);
			TreeStore<M>.TreeModel newChildSubtree = toStore. new TreeModel(child);
			newChildSubtree.addChildren(0, new ArrayList<TreeStore<M>.TreeModel>((List<TreeStore<M>.TreeModel>)childSubTree.getChildren()));
			childrenSubtrees.add(newChildSubtree);
		}
		if(oldIndex >= 0 && oldIndex < childrenSubtrees.size() - 1) 
			Collections.swap(childrenSubtrees, oldIndex, oldIndex + 1);
		return childrenSubtrees;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		fromView.enable();
		toView.enable();
		allLeft.enable();
		allRight.enable();
		right.enable();
		left.enable();
		up.enable();
		down.enable();
	}

	protected void onLeft(List<M> selection) {
		if(mode == Mode.INSERT_MAINTAIN_ANCESTRY)
			for(M item : selection) {
				toLeftMaintain(item);
			}
		else {
			for(M item : selection) {
				toLeftNonMaintain(item);
			}
		}
	}
	
	private void toLeftMaintain(M item) {
		M parent = fromStore.getParent(item);
		M currentAncestor = parent;
		LinkedList<M> ancestors = new LinkedList<M>();
		while(currentAncestor != null) {
			ancestors.add(currentAncestor);
			currentAncestor = fromStore.getParent(currentAncestor);
		}

		toStore.remove(item);
		ListIterator<M> listIterator = ancestors.listIterator();
		while(listIterator.hasNext()) {
			M ancestor = listIterator.next();
			if(toStore.getChildCount(ancestor) > 0) {
				break;
			}
			toStore.remove(ancestor);
		}
	}

	private void toLeftNonMaintain(M item) {
		toStore.remove(item);
	}

	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);

		int w = (width - (buttonBar.getOffsetWidth() + 14)) / 2;

		fromView.setPixelSize(w, height);
		toView.setPixelSize(w, height);
	}

	private void swapBetweenStoresAndViewsMaintain(List<M> selection, Tree<M, T> sourceTree, Tree<M, T> destinationTree, 
			TreeStore<M> source, TreeStore<M> destination) {
		for(M item : selection) {
			TreeNode<M> subTree = source.getSubTree(item);
			M parent = source.getParent(item);
			M currentAncestor = parent;
			LinkedList<M> ancestors = new LinkedList<M>();
			while(currentAncestor != null) {
				ancestors.add(currentAncestor);
				currentAncestor = source.getParent(currentAncestor);
			}
			
			List<TreeNode<M>> children = new LinkedList<TreeNode<M>>();
			children.add(subTree);
			
			if(parent == null) 
				destination.addSubTree(destination.getRootCount(), children);
			else {
				//create parent (hierarchy), if it does not exist yet in fromStore
				ListIterator<M> listIterator = ancestors.listIterator(ancestors.size());
				M first = listIterator.previous();
				if(!destination.getRootItems().contains(first))
					destination.add(first);
				M before = parent;
				while(listIterator.hasPrevious()) {
					M current = listIterator.previous();
					if(!destination.getChildren(before).contains(current)) {
						destination.add(before, current);
					}
					before = current;
				}
				//end create parent hierarchy
				destination.addSubTree(parent, destination.getChildCount(parent), children);
			}
			source.remove(item);
			ListIterator<M> listIterator = ancestors.listIterator();
			while(listIterator.hasNext()) {
				M ancestor = listIterator.next();
				if(source.getChildCount(ancestor) > 0) {
					break;
				}
				source.remove(ancestor);
			}
		}
		destinationTree.getSelectionModel().select(selection, false);
	}
	
	protected void onRight(List<M> selection) {
		if(mode == Mode.INSERT_MAINTAIN_ANCESTRY)
			for(M item : selection) {
				this.toRightMaintain(item);
			}
		else {
			for(M item : selection) {
				this.toRightNonMaintain(item);
			}
		}
	}
	
	private void toRightMaintain(M item) {
		TreeNode<M> subTree = fromStore.getSubTree(item);
		M parent = fromStore.getParent(item);
		M currentAncestor = parent;
		LinkedList<M> ancestors = new LinkedList<M>();
		while(currentAncestor != null) {
			ancestors.add(currentAncestor);
			currentAncestor = fromStore.getParent(currentAncestor);
		}
			
		List<TreeNode<M>> children = new LinkedList<TreeNode<M>>();
		children.add(subTree);
			
		if(parent == null) 
			toStore.addSubTree(toStore.getRootCount(), children);
		else {
			//create parent (hierarchy), if it does not exist yet in fromStore
			ListIterator<M> listIterator = ancestors.listIterator(ancestors.size());
			M first = listIterator.previous();
			if(!toStore.getRootItems().contains(first))
				toStore.add(first);
			M before = parent;
			while(listIterator.hasPrevious()) {
				M current = listIterator.previous();
				if(!toStore.getChildren(before).contains(current)) {
					toStore.add(before, current);
				}
				before = current;
			}
			//end create parent hierarchy
			toStore.addSubTree(parent, toStore.getChildCount(parent), children);
		}
			
		toView.getSelectionModel().select(item, false);
	}

	private void toRightNonMaintain(M item) {
		if(toStore.findModel(item) != null) {
			//alert something possibly, already contained
		} else {
			TreeNode<M> subtree = fromStore.getSubTree(item);
			List<TreeNode<M>> children = new LinkedList<TreeNode<M>>();
			children.add(subtree);
			removeSubtreeItems(toStore, subtree);
			toStore.addSubTree(toStore.getRootCount(), children);
		}
	}

	private void removeSubtreeItems(TreeStore<M> toStore, TreeNode<M> subtree) {
		if(toStore.findModel(subtree.getData()) != null) {
			toStore.remove(subtree.getData());
		}
		for(TreeNode<M> child : subtree.getChildren())	{
			removeSubtreeItems(toStore, child);
		}
	}

	protected void onUp() {
		List<M> selection = toView.getSelectionModel().getSelectedItems();
		for(M item : selection) {
			M parent = toStore.getParent(item);
			if(parent == null) {
				List<M> children = toStore.getRootItems();
				toStore.addSubTree(0, moveUp(item, children));
			} else {
				/*
				currently not possible to use replaceSubTree, see bug:
				http://www.sencha.com/forum/showthread.php?282015-The-following-method-does-not-work-replaceSubTree-in-class-TreeStore.
				
				TreeNode<M> subTree = toStore.getSubTree(parent);
				List<TreeNode<M>> childrenSubtrees = new ArrayList<TreeNode<M>>(subTree.getChildren());
				for(TreeNode<M> child : childrenSubtrees)
					System.out.println(toStore.getKeyProvider().getKey(child.getData()));
						
				int oldIndex = childrenSubtrees.indexOf(toStore.getSubTree(item));
				if(oldIndex >= 0 && oldIndex < childrenSubtrees.size() - 1) 
					Collections.swap(childrenSubtrees, oldIndex, oldIndex + 1);
				toStore.replaceSubTree(parent, childrenSubtrees);*/
				
				//workaround:
				boolean expand = toView.isExpanded(parent);
				List<M> children = toStore.getChildren(parent);
				toStore.addSubTree(parent, 0, moveUp(item, children));
				toView.setExpanded(parent, expand);
			}
		}
		toView.getSelectionModel().setSelection(selection);
	}

	private List<TreeNode<M>> moveUp(M item, List<M> children) {
		List<TreeNode<M>> childrenSubtrees = new ArrayList<TreeNode<M>>();
		int oldIndex = -1;
		for(int i=0; i<children.size(); i++) {
			M child = children.get(i);
			if(child.equals(item))
				oldIndex = i;
			TreeNode<M> childSubTree = toStore.getSubTree(child);
			toStore.remove(child);
			TreeStore<M>.TreeModel newChildSubtree = toStore. new TreeModel(child);
			newChildSubtree.addChildren(0, new ArrayList<TreeStore<M>.TreeModel>((List<TreeStore<M>.TreeModel>)childSubTree.getChildren()));
			childrenSubtrees.add(newChildSubtree);
		}
		if(oldIndex > 0 && oldIndex < childrenSubtrees.size()) 
			Collections.swap(childrenSubtrees, oldIndex, oldIndex - 1);
		return childrenSubtrees;
	}
	
	protected void onAllLeft() {
		toStore.clear();
	}

	protected void onAllRight() {
		cloneStore(fromStore, toStore);
	}
	
	private void cloneStore(TreeStore<M> source, TreeStore<M> destination) {
		destination.clear();
		for(M m : source.getRootItems()) {
			destination.add(m);
			cloneStoreSubTree(source, destination, m);
		}
	}

	private void cloneStoreSubTree(TreeStore<M> source, TreeStore<M> destination, M m) {
		destination.add(m, source.getChildren(m));
		for(M child : source.getChildren(m)) {
			cloneStoreSubTree(source, destination, child);
		}
	}
}
