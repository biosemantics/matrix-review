package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.thirdparty.guava.common.collect.ComparisonChain;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.BeforeStartEditHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;

import edu.arizona.biosemantics.matrixreview.client.event.AddCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.AddTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LoadTaxonMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockMatrixEvent;
import edu.arizona.biosemantics.matrixreview.client.event.LockTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MergeCharactersEvent.MergeMode;
import edu.arizona.biosemantics.matrixreview.client.event.AddColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.HideCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.HideTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModelModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxaEvent;
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxonFlatEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveColorsEvent;
import edu.arizona.biosemantics.matrixreview.client.event.RemoveTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.ModifyTaxonEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetCharacterCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetControlModeEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetTaxonCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueColorEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueCommentEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SetValueEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortCharactersByOrganEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCharacterEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByCoverageEvent;
import edu.arizona.biosemantics.matrixreview.client.event.SortTaxaByNameEvent;
import edu.arizona.biosemantics.matrixreview.client.matrix.CharacterColumnConfig.CharacterValueProvider;
import edu.arizona.biosemantics.matrixreview.client.matrix.cells.ValueCell;
import edu.arizona.biosemantics.matrixreview.client.matrix.editing.LockableControlableMatrixEditing;
import edu.arizona.biosemantics.matrixreview.client.matrix.filters.CharactersGridFilters;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Color;
import edu.arizona.biosemantics.matrixreview.shared.model.HasControlMode.ControlMode;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon.Level;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

public class MatrixView implements IsWidget {
	
	public enum ModelMode {
		TAXONOMIC_HIERARCHY, CUSTOM_HIERARCHY, FLAT
	}

	
	/**
	 * TODO: maintain a map character -> charactercolumnconfig? to avoid iterating over configs to get the one corresponding to a given character
	 * this is done a couple of times in this class and could be avoided
	 * @author rodenhausen
	 */
	public class ModelControler {
				
		private TaxonMatrix taxonMatrix;
		private ValueCell valueCell;
		private ModelMode modelMode = ModelMode.TAXONOMIC_HIERARCHY;
		private LockableControlableMatrixEditing editing;
		private CharactersGridFilters charactersFilters; 
		
		public ModelControler() {
			addEventHandlers();
		}
		
		public void addEventHandlers() {
			eventBus.addHandler(ModelModeEvent.TYPE, new ModelModeEvent.ModelModeEventHandler() {
				@Override
				public void onMode(ModelModeEvent event) {
					modelMode = event.getMode();
					load(taxonMatrix);
				}
			});
			eventBus.addHandler(LoadTaxonMatrixEvent.TYPE, new LoadTaxonMatrixEvent.LoadTaxonMatrixEventHandler() {
				@Override
				public void onLoad(LoadTaxonMatrixEvent loadTaxonMatrixEvent) {
					load(loadTaxonMatrixEvent.getTaxonMatrix());
				} 
			});
			eventBus.addHandler(AddTaxonEvent.TYPE, new AddTaxonEvent.AddTaxonEventHandler() {
				@Override
				public void onAdd(AddTaxonEvent event) {
					if(event.getParent() == null) {
						addRootTaxon(event.getTaxon());
					} else {
						addTaxon(event.getParent(), event.getTaxon());
					}
				}
			});
			eventBus.addHandler(RemoveTaxonEvent.TYPE, new RemoveTaxonEvent.RemoveTaxonEventHandler() {
				@Override
				public void onRemove(final RemoveTaxonEvent event) {
					removeTaxon(event.getTaxon());
				}
			});
			eventBus.addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
				@Override
				public void onModify(ModifyTaxonEvent event) {
					if(Level.isValidParentChild(event.getParent() == null ? null : event.getParent().getLevel(), event.getLevel()))
						modifyTaxon(event.getTaxon(), event.getParent(), event.getLevel(), event.getName(), event.getAuthor(), event.getYear());
					else {
						AlertMessageBox alertMessageBox = new AlertMessageBox("Modify Taxon", "Unable to modify: Incompatible rank of taxon with parent.");
						alertMessageBox.show();
					}
				}
			});
			eventBus.addHandler(MoveTaxonFlatEvent.TYPE, new MoveTaxonFlatEvent.MoveTaxonEventHandler() {
				@Override
				public void onMove(MoveTaxonFlatEvent event) {
					moveTaxonFlat(event.getTaxa(), event.getAfter());
				}
			});
			eventBus.addHandler(MoveTaxaEvent.TYPE, new MoveTaxaEvent.MoveTaxaEventHandler() {
				@Override
				public void onMove(MoveTaxaEvent event) {
					moveTaxaHierarchically(false, event.getParent(), event.getIndex(), event.getTaxa());
				}
			});
			eventBus.addHandler(HideTaxonEvent.TYPE, new HideTaxonEvent.HideCharacterEventHandler() {
				@Override
				public void onHide(HideTaxonEvent event) {
					hideTaxon(event.getTaxon(), event.isHide());
				}
			});
			//---------------------
			
			eventBus.addHandler(AddCharacterEvent.TYPE, new AddCharacterEvent.AddCharacterEventHandler() {
				@Override
				public void onAdd(AddCharacterEvent event) {
					if(event.getAfter() != null) {
						addCharacterAfter(taxonMatrix.getIndexOf(event.getAfter()), event.getCharacter());
					} else {
						addCharacter(event.getCharacter());
					}
				}
			});
			eventBus.addHandler(LockTaxonEvent.TYPE, new LockTaxonEvent.LockTaxonEventHandler() {
				@Override
				public void onLock(LockTaxonEvent event) {
					taxonMatrix.setLocked(event.getTaxon(), event.isLock());
				}
			});
			eventBus.addHandler(LockCharacterEvent.TYPE, new LockCharacterEvent.LockCharacterEventHandler() {
				@Override
				public void onLock(LockCharacterEvent event) {
					taxonMatrix.setLocked(event.getCharacter(), event.isLock());
				}
			});
			eventBus.addHandler(LockMatrixEvent.TYPE, new LockMatrixEvent.LockMatrixEventHandler() {
				@Override
				public void onLock(LockMatrixEvent event) {
					taxonMatrix.setLocked(event.isLock());
				}
			});

			eventBus.addHandler(RemoveCharacterEvent.TYPE, new RemoveCharacterEvent.RemoveCharacterEventHandler() {
				@Override
				public void onRemove(RemoveCharacterEvent event) {
					removeCharacter(event.getCharacter());
				}
			});
			eventBus.addHandler(ModifyCharacterEvent.TYPE, new ModifyCharacterEvent.ModifyCharacterEventHandler() {
				@Override
				public void onRename(ModifyCharacterEvent event) {
					modifyCharacter(event.getCharacter(), event.getName(), event.getOrgan());
				}
			});
			eventBus.addHandler(MoveCharacterEvent.TYPE, new MoveCharacterEvent.MoveCharacterEventHandler() {
				@Override
				public void onMove(MoveCharacterEvent event) {
					moveCharacter(event.getCharacter(), event.getAfter());
				}
			});
			eventBus.addHandler(SetTaxonCommentEvent.TYPE, new SetTaxonCommentEvent.SetTaxonCommentEventHandler() {
				@Override
				public void onSet(SetTaxonCommentEvent event) {
					setTaxonComment(event.getTaxon(), event.getComment());
				}
			});
			eventBus.addHandler(SetCharacterCommentEvent.TYPE, new SetCharacterCommentEvent.SetCharacterCommentEventHandler() {
				@Override
				public void onSet(SetCharacterCommentEvent event) {
					taxonMatrix.setComment(event.getCharacter(), event.getComment());
				}
			});
			eventBus.addHandler(SetValueCommentEvent.TYPE, new SetValueCommentEvent.SetValueCommentEventHandler() {
				@Override
				public void onSet(SetValueCommentEvent event) {
					setValueComment(event.getValue(), event.getComment());
				}
			});
			eventBus.addHandler(SetValueColorEvent.TYPE, new SetValueColorEvent.SetValueColorEventHandler() {
				@Override
				public void onSet(SetValueColorEvent event) {
					setValueColor(event.getValue(), event.getColor());
				}
			});
			eventBus.addHandler(SetTaxonColorEvent.TYPE, new SetTaxonColorEvent.SetTaxonColorEventHandler() {
				@Override
				public void onSet(SetTaxonColorEvent event) {
					setTaxonColor(event.getTaxon(), event.getColor());
				}
			});
			eventBus.addHandler(SetCharacterColorEvent.TYPE, new SetCharacterColorEvent.SetCharacterColorEventHandler() {
				@Override
				public void onSet(SetCharacterColorEvent event) {
					setCharacterColor(event.getCharacter(), event.getColor());
				}
			});
			eventBus.addHandler(MergeCharactersEvent.TYPE, new MergeCharactersEvent.MergeCharactersEventHandler() {
				@Override
				public void onMerge(MergeCharactersEvent event) {
					mergeCharacters(event.getCharacter(), event.getTarget(), event.getMergeMode());
				}
			});
			eventBus.addHandler(SetControlModeEvent.TYPE, new SetControlModeEvent.SetControlModeEventHandler() {
				@Override
				public void onSet(SetControlModeEvent event) {
					setControlMode(event.getCharacter(), event.getControlMode(), event.getStates());
				}
			});
			eventBus.addHandler(SetValueEvent.TYPE, new SetValueEvent.SetValueEventHandler() {
				@Override
				public void onSet(SetValueEvent event) {
					setValue(event.getOldValue(), event.getNewValue(), event.isChangeRecordedInModel());
	 			}
			});
			eventBus.addHandler(AddColorEvent.TYPE, new AddColorEvent.AddColorEventHandler() {
				@Override
				public void onAdd(AddColorEvent event) {
					taxonMatrix.addColor(event.getColor());
				}
			});
			eventBus.addHandler(RemoveColorsEvent.TYPE, new RemoveColorsEvent.RemoveColorsEventHandler() {
				@Override
				public void onRemove(RemoveColorsEvent event) {
					taxonMatrix.removeColors(event.getColors());
				}
			});			
			eventBus.addHandler(SortCharactersByNameEvent.TYPE, new SortCharactersByNameEvent.SortCharatersByNameEventHandler() {
				@Override
				public void onSort(final SortCharactersByNameEvent event) {
					sortCharactersByName(event.getSortDir().equals(SortDir.DESC));
				}
			});
			eventBus.addHandler(SortCharactersByCoverageEvent.TYPE, new SortCharactersByCoverageEvent.SortCharatersByCoverageEventHandler() {
				@Override
				public void onSort(final SortCharactersByCoverageEvent event) {
					sortCharactersByCoverage(event.getSortDir().equals(SortDir.DESC));
				}
			});
			eventBus.addHandler(SortCharactersByOrganEvent.TYPE, new SortCharactersByOrganEvent.SortCharatersByOrganEventHandler() {
				@Override
				public void onSort(final SortCharactersByOrganEvent event) {
					sortCharactersByOrgan(event.getSortDir().equals(SortDir.DESC));
				}
			});
			eventBus.addHandler(HideCharacterEvent.TYPE, new HideCharacterEvent.HideCharacterEventHandler() {
				@Override
				public void onHide(HideCharacterEvent event) {
					hideCharacter(event.getCharacter(), event.isHide());
				}
			});
			eventBus.addHandler(SortTaxaByCharacterEvent.TYPE, new SortTaxaByCharacterEvent.SortTaxaByCharacterEventHandler() {
				@Override
				public void onSort(SortTaxaByCharacterEvent event) {
					sortTaxaByCharacter(event.getCharacter(), event.getSortDirection());
				}
			});
			eventBus.addHandler(SortTaxaByCoverageEvent.TYPE, new SortTaxaByCoverageEvent.SortTaxaByCoverageEventHandler() {
				@Override
				public void onSort(SortTaxaByCoverageEvent event) {
					sortTaxaByCoverage(event.getSortDirection());
				}
			});
			eventBus.addHandler(SortTaxaByNameEvent.TYPE, new SortTaxaByNameEvent.SortTaxaByNameEventHandler() {
				@Override
				public void onSort(SortTaxaByNameEvent event) {
					sortTaxaByName(event.getSortDirection());
				}
			});
		}
		
		protected void setTaxonComment(Taxon taxon, String comment) {
			taxonMatrix.setComment(taxon, comment);
			taxonStore.update(taxon);
		}

		protected void setValueComment(Value value, String comment) {
			taxonMatrix.setComment(value, comment);
			taxonStore.update(value.getTaxon());
		}

		protected void load(TaxonMatrix taxonMatrix) {
			this.taxonMatrix = taxonMatrix;
			this.valueCell = new ValueCell(eventBus, taxonMatrix);
			List<Taxon> taxa = taxonMatrix.list();
			
			if(taxonTreeGrid.isInitialized())
				taxonStore.clear();
			
			switch(modelMode) {
				case FLAT:
					for(Taxon taxon : taxa) {
						taxonStore.add(taxon);
					}
					break;
				case CUSTOM_HIERARCHY:
				case TAXONOMIC_HIERARCHY:
					for(Taxon taxon : taxonMatrix.getRootTaxa()) {
						insertToStoreRecursively(taxon);
					}
					break;
			}

			if(taxonTreeGrid.isInitialized()) {
				List<CharacterColumnConfig> characterColumnConfigs = taxonTreeGrid.getColumnModel().getCharacterColumns();
				for(CharacterColumnConfig characterColumnConfig : characterColumnConfigs)
					characterColumnConfig.setCell(valueCell);
				taxonTreeGrid.reconfigure(characterColumnConfigs);
				valueCell.setListStore(taxonTreeGrid.getTreeGrid().getListStore());
			} else {
				List<ColumnConfig<Taxon, ?>> characterColumnConfigs = new ArrayList<ColumnConfig<Taxon, ?>>();
				for(Character character : taxonMatrix.getCharacters())
					characterColumnConfigs.add(this.createCharacterColumnConfig(character));
				taxonTreeGrid.init(characterColumnConfigs, new CharactersGridView(eventBus, taxonMatrix));
				valueCell.setListStore(taxonTreeGrid.getTreeGrid().getListStore());
				editing = new LockableControlableMatrixEditing(eventBus, taxonTreeGrid.getGrid(), taxonTreeGrid.getTreeGrid().getListStore(), taxonMatrix);
				initCharacterEditing();
				initCharacterFiltering();
			}
		}
		
		private void initCharacterFiltering() {
			// init control and filtering for charactersGrid
			charactersFilters = new CharactersGridFilters(eventBus, taxonMatrix, taxonStore, taxonTreeGrid.getGrid());
			charactersFilters.initPlugin(taxonTreeGrid.getGrid());
		}

		protected void moveTaxaHierarchically(boolean storeOnly, Taxon parent, int index, List<Taxon> taxa) {
			for(Taxon taxon : taxa)
				removeFromStoreRecursively(taxon);
			if (parent == null) {
				insertToStoreRecursively(index, taxa);
				if(!storeOnly)
					taxonMatrix.moveToRootTaxon(index, taxa);
			} else {
				insertToStoreRecursively(parent, index, taxa);
				if(!storeOnly)
					taxonMatrix.addTaxon(parent, index, taxa);
			}
		}

		protected void moveTaxonFlat(List<Taxon> taxa, Taxon after) {
			switch(modelMode) {
			case FLAT:
				for(Taxon taxon : taxa)
					taxonStore.remove(taxon);
				if(after == null || taxonStore.getRootItems().indexOf(after) == -1) {
					taxonStore.insert(0, taxa);
				} else {
					taxonStore.insert(taxonStore.getRootItems().indexOf(after) + 1, taxa);
				}
				break;
			case CUSTOM_HIERARCHY:
			case TAXONOMIC_HIERARCHY:
				for(Taxon taxon : taxa)
					taxonStore.remove(taxon);
				if(!taxa.isEmpty() && taxa.get(0).hasParent()) {
					Taxon parent = taxa.get(0).getParent();
					if(after == null) {
						this.insertToStoreRecursively(parent, 0, taxa);
					} else {
						this.insertToStoreRecursively(parent, parent.getChildren().indexOf(after), taxa);
					}
				} else {
					if(after == null) 
						this.insertToStoreRecursively(0, taxa);
					else
						this.insertToStoreRecursively(taxonStore.getRootItems().indexOf(after) + 1, taxa);
				}
				break;
			}
		}
		
		private void removeFromStoreRecursively(Taxon taxon) {
			taxonStore.remove(taxon);
			for(Taxon child : taxon.getChildren()) {
				taxonStore.remove(child);
			}
		}
		
		private void insertToStoreRecursively(int index, List<Taxon> taxa) {
			taxonStore.insert(index, taxa);
			
			for(Taxon taxon : taxa) 
				insertToStoreRecursively(taxon, 0, taxon.getChildren());
		}
		
		private void insertToStoreRecursively(Taxon parent, int index,	List<Taxon> taxa) {
			taxonStore.insert(parent, index, taxa);
			
			for(Taxon taxon : taxa) 
				insertToStoreRecursively(taxon, 0, taxon.getChildren());
		}
		
		private void  insertToStoreRecursively(Taxon taxon) {
			if(!taxon.hasParent())
				taxonStore.add(taxon);
			else 
				taxonStore.add(taxon.getParent(), taxon);
			for(Taxon child : taxon.getChildren())
				insertToStoreRecursively(child);
		}
		
		protected void modifyTaxon(Taxon taxon, Taxon parent, Level level, String name, String author, String year) {
			boolean updateStore = false;
			updateStore = (taxon.getParent() == null && parent != null) || (taxon.getParent() == null || !taxon.getParent().equals(parent));
			taxonMatrix.modifyTaxon(taxon, level, name, author, year);
	
			if(updateStore && modelMode.equals(ModelMode.TAXONOMIC_HIERARCHY)) {
				List<Taxon> taxa = new LinkedList<Taxon>();
				taxa.add(taxon);
				this.moveTaxaHierarchically(true, parent, 0, taxa);
			}
		}
		
		protected void addRootTaxon(Taxon taxon) {
			taxonMatrix.addRootTaxon(taxon);
			taxonStore.add(taxon);
		}
		
		protected void addTaxon(Taxon parent, int index, Taxon taxon) {
			taxonMatrix.addTaxon(parent, index, taxon);
			switch(modelMode){
			case FLAT:
				taxonStore.add(taxon);
				break;
			case TAXONOMIC_HIERARCHY:
			case CUSTOM_HIERARCHY:
				taxonStore.insert(parent, index, taxon);
				break;
			}
		}
		
		protected void addTaxon(Taxon parent, Taxon taxon) {
			taxonMatrix.addTaxon(parent, taxon);
			switch(modelMode){
			case FLAT:
				taxonStore.add(taxon);
				break;
			case TAXONOMIC_HIERARCHY:
			case CUSTOM_HIERARCHY:
				taxonStore.add(parent, taxon);
				break;
			}
		}
		
		protected void removeTaxon(final Taxon taxon) {
			if (!taxon.getChildren().isEmpty()) {
				String childrenString = "";
				for (Taxon child : taxon.getChildren()) {
					childrenString += child.getFullName() + ", ";
				}
				childrenString = childrenString.substring(0, childrenString.length() - 2);
				ConfirmMessageBox box = new ConfirmMessageBox(
						"Remove Taxon",
						"Removing the taxon will also remove all of it's descendants: "
								+ childrenString);
				box.addDialogHideHandler(new DialogHideHandler() {
					@Override
					public void onDialogHide(DialogHideEvent event) {
						if(event.getHideButton().equals(PredefinedButton.YES)) {
							removeFromStoreRecursively(taxon);
							taxonMatrix.removeTaxon(taxon);
						}
					}
					
				});
				box.show();
			}
		}
		
		protected void setValue(Value oldValue, Value newValue, boolean changeRecordedInModel) {
			if(!changeRecordedInModel) {
				Taxon taxon = oldValue.getTaxon();
				Character character = oldValue.getCharacter();
				setValue(taxon, character, newValue);
			}
		}

		protected void setCharacterColor(Character character, Color color) {
			taxonMatrix.setColor(character, color);
			taxonStore.fireEvent(new StoreDataChangeEvent<Taxon>());
		}

		protected void setTaxonColor(Taxon taxon, Color color) {
			taxonMatrix.setColor(taxon, color);
			taxonStore.update(taxon);
		}
		
		protected void setValueColor(Value value, Color color) {
			taxonMatrix.setColor(value, color);
			taxonStore.fireEvent(new StoreDataChangeEvent<Taxon>());
		}

		protected void sortCharactersByName(final boolean descending) {
			Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
				@Override
				public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
					if (descending)
						return doCompare(o1, o2);
					else
						return doCompare(o2, o1);
				}

				// first by name then by organ
				private int doCompare(CharacterColumnConfig o1,	CharacterColumnConfig o2) {
					return (o2.getCharacter().getName() + o2.getCharacter().getOrgan()).compareTo(o1.getCharacter().getName() + o1.getCharacter().getOrgan());
				}
			};
			sortCharacters(comparator);
		}

		protected void sortCharactersByCoverage(final boolean descending) {
			Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
				@Override
				public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
					if (descending) {
						int diff =  doCompare(o2, o1);
						if(diff == 0)
							diff = (o1.getCharacter().getOrgan() + o1.getCharacter().getName()).compareTo(o2.getCharacter().getOrgan() + o2.getCharacter().getName());
						return diff;
					} else {
						int diff =  doCompare(o1, o2);
						if(diff == 0)
							diff = (o1.getCharacter().getOrgan() + o1.getCharacter().getName()).compareTo(o2.getCharacter().getOrgan() + o2.getCharacter().getName());
						return diff;
					}
				}

				//first by coverage, then by name 
				private int doCompare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
					return taxonMatrix.getCharacterValueCount(o1.getCharacter()) - taxonMatrix.getCharacterValueCount(o2.getCharacter());
				}
			};
			sortCharacters(comparator);
		}

		protected void sortCharactersByOrgan(final boolean descending) {
			Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
				@Override
				public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
					if (descending)
						return doCompare(o1, o2);
					else
						return doCompare(o2, o1);
				}

				// first by organ then by character name
				private int doCompare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
					return (o2.getCharacter().getOrgan() + o2.getCharacter().getName()).compareTo(o1.getCharacter().getOrgan() + o1.getCharacter().getName());
				}
			};
			sortCharacters(comparator);
		}

		protected void hideTaxon(Taxon taxon, boolean hide) {
			taxonMatrix.setHidden(taxon, hide);
			taxonTreeGrid.hide(taxon, hide);
		}

		protected void hideCharacter(Character character, boolean hide) {
			taxonMatrix.setHidden(character, hide);
			List<CharacterColumnConfig> characterColumnConfigs = taxonTreeGrid.getColumnModel().getCharacterColumns();
			int columnIndex = -1;
			for(int i=0; i<characterColumnConfigs.size(); i++) {
				CharacterColumnConfig config = characterColumnConfigs.get(i);
				if(config.getCharacter().equals(character)) {
					columnIndex = i;
					break;
				}
			}
			if(columnIndex != - 1);
				taxonTreeGrid.getColumnModel().setHidden(columnIndex, hide);
		}

		protected void sortTaxaByCharacter(final Character character, SortDir sortDirection) {
			taxonStore.clearSortInfo();
			StoreSortInfo<Taxon> sortInfo = new StoreSortInfo<Taxon>(new Comparator<Taxon>() {
				@Override
				public int compare(Taxon o1, Taxon o2) {
					int diff = o1.get(character).getValue().compareTo(o2.get(character).getValue());
					if(diff == 0)
						diff = o1.getFullName().compareTo(o2.getFullName());
					return diff;
				}
			}, sortDirection);
			taxonStore.addSortInfo(sortInfo);
		}

		protected void sortTaxaByCoverage(SortDir sortDirection) {
			taxonStore.clearSortInfo();
			StoreSortInfo<Taxon> sortInfo = new StoreSortInfo<Taxon>(new Comparator<Taxon>() {
				@Override
				public int compare(Taxon o1, Taxon o2) {
					//first by coverage, then by name 
					int diff = taxonMatrix.getTaxonValueCount(o1) - taxonMatrix.getTaxonValueCount(o2);
					if(diff == 0)
						diff = o1.getFullName().compareTo(o2.getFullName());
					return diff;
				}
			}, sortDirection);
			taxonStore.addSortInfo(sortInfo);
		}

		protected void sortTaxaByName(SortDir sortDirection) {
			taxonStore.clearSortInfo();
			StoreSortInfo<Taxon> sortInfo = new StoreSortInfo<Taxon>(new Comparator<Taxon>() {
				@Override
				public int compare(Taxon o1, Taxon o2) {
					return o1.getFullName().compareTo(o2.getFullName());
				}
			}, sortDirection);
			taxonStore.addSortInfo(sortInfo);
		}


		protected void sortCharacters(Comparator<CharacterColumnConfig> comparator) {
			List<CharacterColumnConfig> characterColumnConfigs = taxonTreeGrid.getColumnModel().getCharacterColumns();
			Collections.sort(characterColumnConfigs, comparator);
			taxonTreeGrid.reconfigure(characterColumnConfigs);
		}
		
		protected void setValue(Taxon taxon, Character character, Value newValue) {
			taxonMatrix.setValue(taxon, character, newValue);
			taxonStore.update(taxon);
		}

		protected void setControlMode(Character character, ControlMode controlMode, List<String> states) {
			taxonMatrix.setControlMode(character, controlMode);
			taxonMatrix.setCharacterStates(character, states);
			editing.setControlMode(character, controlMode, states);
			charactersFilters.setControlMode(character, controlMode, states);
		}

		protected void mergeCharacters(Character characterA, Character characterB, MergeMode mergeMode) {
			String mergedName = mergeName(characterA.getName(), characterB.getName(), mergeMode);
			Organ mergedOrgan = mergeOrgan(characterA.getOrgan(), characterB.getOrgan(), mergeMode);
			for(Taxon taxon : taxonMatrix.list()) {
				Value a = taxon.get(characterA);
				Value b = taxon.get(characterB);
				
				String mergedValue = mergeValues(a, b, mergeMode);
				Color mergedColor = mergeColors(a.getColor(), b.getColor(), mergeMode);
				String mergedComment = mergeComment(a.getComment(), b.getComment(), mergeMode);
				
				Value newValue = new Value(mergedValue);
				taxonMatrix.setValue(taxon, characterA, newValue);
				taxonMatrix.setColor(newValue, mergedColor);
				taxonMatrix.setComment(newValue, mergedComment);
			}
			this.modifyCharacter(characterA, mergedName, mergedOrgan);
			
			//set control to off and clean up
			editing.setControlMode(characterA, ControlMode.OFF, null);
			this.removeCharacter(characterB);
		}

		private String mergeName(String a, String b, MergeMode mergeMode) {
			a = a.trim();
			b = b.trim();
			if(a.isEmpty())
				return b;
			if(b.isEmpty())
				return a;

			switch(mergeMode) {
			case A_OVER_B:
				return a;
			case B_OVER_A:
				return b;
			case MIX:
			default:
				return a + " ; " + b;
			}
		}
		
		private Organ mergeOrgan(Organ a, Organ b, MergeMode mergeMode) {
			if(a == null)
				return b;
			if(b == null)
				return a;

			switch(mergeMode) {
			case A_OVER_B:
				return a;
			case B_OVER_A:
				return b;
			case MIX:
			default:
				return new Organ(a + " ; " + b);
			}
		}

		private String mergeValues(Value a, Value b, MergeMode mergeMode) {
			String aValue = a.getValue().trim();
			String bValue = b.getValue().trim();
			if(aValue.isEmpty())
				return bValue;
			if(bValue.isEmpty())
				return aValue;

			switch(mergeMode) {
			case A_OVER_B:
				return aValue;
			case B_OVER_A:
				return bValue;
			case MIX:
			default:
				return a.getValue() + " ; " + b.getValue();
			}
		}

		private Color mergeColors(Color a, Color b, MergeMode mergeMode) {
			switch(mergeMode) {
			case A_OVER_B:
				return a;
			case B_OVER_A:
				return b;
			case MIX:
			default:
				return null;
			}
		}

		private String mergeComment(String a, String b, MergeMode mergeMode) {
			a = a.trim();
			b = b.trim();
			if(a.isEmpty())
				return b;
			if(b.isEmpty())
				return a;

			switch(mergeMode) {
			case A_OVER_B:
				return a;
			case B_OVER_A:
				return b;
			case MIX:
			default:
				return a + " ; " + b;
			}
		}
		
		protected void moveCharacter(Character character, Character after) {
			taxonMatrix.moveCharacter(character, after);
			CharacterColumnConfig charactersConfig = null;
			CharacterColumnConfig afterConfig = null;
			List<CharacterColumnConfig> columns = new LinkedList<CharacterColumnConfig>(taxonTreeGrid.getColumnModel().getCharacterColumns());
			Iterator<CharacterColumnConfig> iterator = columns.iterator();
			while(iterator.hasNext()) {
				CharacterColumnConfig config = iterator.next();
				if(config.getCharacter().equals(character)) {
					charactersConfig = config;
					iterator.remove();
				}
				if(config.getCharacter().equals(after))
					afterConfig = config;
			}
			if(charactersConfig != null)
				if(afterConfig == null)
					columns.add(0, charactersConfig);
				else
					columns.add(columns.indexOf(afterConfig) + 1, charactersConfig);
			taxonTreeGrid.reconfigure(columns);
		}
		
		protected void modifyCharacter(Character character, String name, Organ organ) {
			taxonMatrix.modifyCharacter(character, name, organ);
			CharactersColumnModel columnModel = taxonTreeGrid.getColumnModel();
			CharacterColumnConfig config = taxonTreeGrid.getGrid().getCharacterColumnConfig(character);
			config.setHeader(SafeHtmlUtils.fromString(character.toString()));
			taxonTreeGrid.updateCharacterGridHeads();
		}
		
		private void initCharacterEditing() {
			EditEventsHandler editEventsHandler = new EditEventsHandler();
			for(CharacterColumnConfig config : taxonTreeGrid.getGrid().getColumnModel().getCharacterColumns()) {
				editing.addEditor(config);
				editing.addBeforeStartEditHandler(editEventsHandler);
				editing.addCompleteEditHandler(editEventsHandler);
			}
		}
		
		private CharacterColumnConfig createCharacterColumnConfig(final Character character) {
			CharacterColumnConfig characterColumnConfig = new CharacterColumnConfig(200, character);
			CharacterValueProvider characterValueProvider = (CharacterValueProvider) characterColumnConfig.getValueProvider();
			characterValueProvider.setCharacterColumnConfig(characterColumnConfig);
			characterColumnConfig.setCell(valueCell);
			return characterColumnConfig;
		}
		
		protected void addCharacter(Character character) {
			this.addCharacterAfter(taxonMatrix.getCharacterCount() - 1, character);
		}

		protected void removeCharacter(Character character) {
			taxonMatrix.removeCharacter(character);
			List<CharacterColumnConfig> columns = new LinkedList<CharacterColumnConfig>(taxonTreeGrid.getColumnModel().getCharacterColumns());
			Iterator<CharacterColumnConfig> iterator = columns.iterator();
			while(iterator.hasNext()) {
				CharacterColumnConfig config = iterator.next();
				if(config.getCharacter().equals(character)) {
					editing.removeEditor(config);
					iterator.remove();
				}
			}
			taxonTreeGrid.reconfigure(columns);
		}
		
		protected void addCharacterAfter(int colIndex, Character character) {
			taxonMatrix.addCharacter(colIndex, character);
			List<CharacterColumnConfig> columns = new LinkedList<CharacterColumnConfig>(taxonTreeGrid.getColumnModel().getCharacterColumns());
			CharacterColumnConfig columnConfig = createCharacterColumnConfig(character);
			columns.add(colIndex + 1, columnConfig);
			editing.addEditor(columnConfig);
			taxonTreeGrid.reconfigure(columns);
		}
	}
	
	public class EditEventsHandler implements CompleteEditHandler<Taxon>, BeforeStartEditHandler<Taxon> {
		private Value oldValue;
		//underlying GXT implementation fires CompleteEditEvent multiple times for some reason
		private boolean fired = true;
		@Override
		public void onCompleteEdit(CompleteEditEvent<Taxon> event) {
			if(!fired) {
				GridCell cell = event.getEditCell();
				Taxon taxon = taxonTreeGrid.getTreeGrid().getListStore().get(cell.getRow());
				ColumnConfig<Taxon, Value> config = taxonTreeGrid.getColumnModel().getColumn(cell.getCol());
				Value value = config.getValueProvider().getValue(taxon);
				eventBus.fireEvent(new SetValueEvent(oldValue, value, true));
				fired = true;
				oldValue = null;
			}
		}
		@Override
		public void onBeforeStartEdit(BeforeStartEditEvent<Taxon> event) {
			if(fired) {
				GridCell cell = event.getEditCell();
				Taxon taxon = taxonTreeGrid.getTreeGrid().getListStore().get(cell.getRow());
				ColumnConfig<Taxon, Value> config = taxonTreeGrid.getColumnModel().getColumn(cell.getCol());
				oldValue = config.getValueProvider().getValue(taxon);
				fired = false;
			}
		}
		
	}
	
	private TaxonStore taxonStore;
	private FrozenFirstColumTaxonTreeGrid taxonTreeGrid;
	//private TaxonCell taxonCell;
	private SimpleEventBus eventBus;
	
	private ModelControler modelControler;
	private TaxonMatrix taxonMatrix;

	public MatrixView(SimpleEventBus eventBus, TaxonMatrix taxonMatrix) {
		this.eventBus = eventBus;
		this.taxonMatrix = taxonMatrix;
		//this.taxonCell = new TaxonCell(eventBus, taxonMatrix);
		
		// create store: 
		taxonStore = new TaxonStore();
		//-> reuse internal liststore of treegrid
		//AllAccessListStore<Taxon> store = new AllAccessListStore<Taxon>(taxonProperties.key());
		// yes or no? store.setAutoCommit(false); 
		// this also has influence on
		// the dirty icon that comes out of the box; true wont show?
		// also one may not directly use the model to calculate view related
		// things, such as coverage, because it is not yet represented in model
		// if autocommit is set to false
		taxonStore.setAutoCommit(true);
		//store.setAutoCommit(true);
		
		taxonTreeGrid = new FrozenFirstColumTaxonTreeGrid(eventBus, taxonMatrix, taxonStore, createTaxaColumnConfig());
		
		addEventHandlers();
		
		//NewDataManager manager = new NewDataManager(taxonTreeGrid);
		//manager.load(taxonMatrix);
		
		//this.initWidget(taxonTreeGrid.asWidget());
	}

	private void addEventHandlers() {
		modelControler = new ModelControler();
		
	}

	private TaxaColumnConfig createTaxaColumnConfig() {
		TaxaColumnConfig taxaColumnConfig = new TaxaColumnConfig();
		taxaColumnConfig.setCell(new AbstractCell<Taxon>() {
			@Override
			public void render(Context context,	Taxon value, SafeHtmlBuilder sb) {
				sb.append(SafeHtmlUtils.fromTrustedString("<i>" + value.getFullName() + "</i>"));
			}
		});
		return taxaColumnConfig;
	}

	@Override
	public Widget asWidget() {
		return taxonTreeGrid.asWidget();
	}
	
	public FrozenFirstColumTaxonTreeGrid getTaxonTreeGrid() {
		return taxonTreeGrid;
	}
	
	public TaxonStore getTaxonStore() {
		return taxonStore;
	}
}
