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
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.TreeStore.TreeNode;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.BeforeStartEditHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.TextField;
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
import edu.arizona.biosemantics.matrixreview.client.event.MoveTaxonEvent;
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
import edu.arizona.biosemantics.matrixreview.client.matrix.editing.ValueConverter;
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
						addTaxon(event.getTaxon());
					} else {
						addTaxon(event.getParent(), event.getTaxon());
					}
				}
			});
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
			eventBus.addHandler(LockTaxonEvent.TYPE, new LockTaxonEvent.LockCharacterEventHandler() {
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
			eventBus.addHandler(RemoveTaxonEvent.TYPE, new RemoveTaxonEvent.RemoveTaxonEventHandler() {
				@Override
				public void onRemove(RemoveTaxonEvent event) {
					removeTaxon(event.getTaxon());
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
			eventBus.addHandler(ModifyTaxonEvent.TYPE, new ModifyTaxonEvent.ModifyTaxonEventHandler() {
				@Override
				public void onModify(ModifyTaxonEvent event) {
					if(taxonMatrix.isValidParentChildRelation(event.getTaxon(), event.getParent()))
						modifyTaxon(event.getTaxon(), event.getParent(), event.getLevel(), event.getName(), event.getAuthor(), event.getYear());
					else {
						AlertMessageBox alertMessageBox = new AlertMessageBox("Modify Taxon", "Unable to modify: Incompatible rank of taxon with parent.");
						alertMessageBox.show();
					}
				}
			});
			eventBus.addHandler(MoveCharacterEvent.TYPE, new MoveCharacterEvent.MoveCharacterEventHandler() {
				@Override
				public void onMove(MoveCharacterEvent event) {
					moveCharacter(event.getCharacter(), event.getAfter());
				}
			});
			eventBus.addHandler(MoveTaxonEvent.TYPE, new MoveTaxonEvent.MoveTaxonEventHandler() {
				@Override
				public void onMove(MoveTaxonEvent event) {
					moveTaxon(event.getTaxon(), event.getAfter(), event.getAftersParent());
				}
			});
			eventBus.addHandler(SetTaxonCommentEvent.TYPE, new SetTaxonCommentEvent.SetTaxonCommentEventHandler() {
				@Override
				public void onSet(SetTaxonCommentEvent event) {
					taxonMatrix.setComment(event.getTaxon(), event.getComment());
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
					taxonMatrix.setComment(event.getValue(), event.getComment());
				}
			});
			eventBus.addHandler(SetValueColorEvent.TYPE, new SetValueColorEvent.SetValueColorEventHandler() {
				@Override
				public void onSet(SetValueColorEvent event) {
					taxonMatrix.setColor(event.getValue(), event.getColor());
				}
			});
			eventBus.addHandler(SetTaxonColorEvent.TYPE, new SetTaxonColorEvent.SetTaxonColorEventHandler() {
				@Override
				public void onSet(SetTaxonColorEvent event) {
					taxonMatrix.setColor(event.getTaxon(), event.getColor());
					taxonStore.update(event.getTaxon());
				}
			});
			eventBus.addHandler(SetCharacterColorEvent.TYPE, new SetCharacterColorEvent.SetCharacterColorEventHandler() {
				@Override
				public void onSet(SetCharacterColorEvent event) {
					taxonMatrix.setColor(event.getCharacter(), event.getColor());
					taxonStore.fireEvent(new StoreDataChangeEvent<Taxon>());
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
					setControlMode(event.getCharacter(), event.getControlMode());
				}
			});
			eventBus.addHandler(SetValueEvent.TYPE, new SetValueEvent.SetValueEventHandler() {
				@Override
				public void onSet(SetValueEvent event) {
					if(!event.isChangeRecordedInModel()) {
						Taxon taxon = event.getOldValue().getTaxon();
						Character character = event.getOldValue().getCharacter();
						setValue(taxon, character, event.getNewValue());
					}
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
					Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
						@Override
						public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
							if (event.isDescending())
								// first by name then by organ
								return (o2.getCharacter().getName() + o2.getCharacter().getOrgan()).compareTo(o1.getCharacter().getName() + o1.getCharacter().getOrgan());
							else
								return (o1.getCharacter().getName() + o1.getCharacter().getOrgan()).compareTo(o2.getCharacter().getName() + o2.getCharacter().getOrgan());
						}
					};
					sortCharacters(comparator);
				}
			});
			eventBus.addHandler(SortCharactersByCoverageEvent.TYPE, new SortCharactersByCoverageEvent.SortCharatersByCoverageEventHandler() {
				@Override
				public void onSort(final SortCharactersByCoverageEvent event) {
					Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
						@Override
						public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
							if (event.isDescending())
								return taxonMatrix.getCharacterValueCount(o2.getCharacter()) - taxonMatrix.getCharacterValueCount(o1.getCharacter());
							else
								return taxonMatrix.getCharacterValueCount(o1.getCharacter()) - taxonMatrix.getCharacterValueCount(o2.getCharacter());
						}
					};
					sortCharacters(comparator);
				}
			});
			eventBus.addHandler(SortCharactersByOrganEvent.TYPE, new SortCharactersByOrganEvent.SortCharatersByOrganEventHandler() {
				@Override
				public void onSort(final SortCharactersByOrganEvent event) {
					Comparator<CharacterColumnConfig> comparator = new Comparator<CharacterColumnConfig>() {
						@Override
						public int compare(CharacterColumnConfig o1, CharacterColumnConfig o2) {
							if (event.isDescending())
								// first by organ then by character name
								return (o2.getCharacter().getOrgan() + o2.getCharacter().getName()).compareTo(o1.getCharacter().getOrgan() + o1.getCharacter().getName());
							else
								return (o1.getCharacter().getOrgan() + o1.getCharacter().getName()).compareTo(o2.getCharacter().getOrgan() + o2.getCharacter().getName());
						}
					};
					sortCharacters(comparator);
				}
			});
			eventBus.addHandler(HideCharacterEvent.TYPE, new HideCharacterEvent.HideCharacterEventHandler() {
				@Override
				public void onHide(HideCharacterEvent event) {
					taxonMatrix.setHidden(event.getCharacter(), event.isHide());
					List<CharacterColumnConfig> characterColumnConfigs = taxonTreeGrid.getColumnModel().getCharacterColumns();
					int columnIndex = -1;
					for(int i=0; i<characterColumnConfigs.size(); i++) {
						CharacterColumnConfig config = characterColumnConfigs.get(i);
						if(config.getCharacter().equals(event.getCharacter())) {
							columnIndex = i;
							break;
						}
					}
					if(columnIndex != - 1);
						taxonTreeGrid.getColumnModel().setHidden(columnIndex, event.isHide());
				}
			});
			eventBus.addHandler(HideTaxonEvent.TYPE, new HideTaxonEvent.HideCharacterEventHandler() {
				@Override
				public void onHide(HideTaxonEvent event) {
					taxonMatrix.setHidden(event.getTaxon(), event.isHide());
					taxonTreeGrid.hide(event.getTaxon(), event.isHide());
				}
			});
			eventBus.addHandler(SortTaxaByCharacterEvent.TYPE, new SortTaxaByCharacterEvent.SortTaxaByCharacterEventHandler() {
				@Override
				public void onSort(SortTaxaByCharacterEvent event) {
					final Character character = event.getCharacter();
					taxonStore.clearSortInfo();
					StoreSortInfo<Taxon> sortInfo = new StoreSortInfo<Taxon>(new Comparator<Taxon>() {
						@Override
						public int compare(Taxon o1, Taxon o2) {
							return o1.get(character).getValue().compareTo(o2.get(character).getValue());
						}
					}, event.getSortDirection());
					taxonStore.addSortInfo(sortInfo);
				}
			});
			eventBus.addHandler(SortTaxaByCoverageEvent.TYPE, new SortTaxaByCoverageEvent.SortTaxaByCoverageEventHandler() {
				@Override
				public void onSort(SortTaxaByCoverageEvent event) {
					taxonStore.clearSortInfo();
					StoreSortInfo<Taxon> sortInfo = new StoreSortInfo<Taxon>(new Comparator<Taxon>() {
						@Override
						public int compare(Taxon o1, Taxon o2) {
							return taxonMatrix.getTaxonValueCount(o1) - taxonMatrix.getTaxonValueCount(o2);
						}
					}, event.getSortDirection());
					taxonStore.addSortInfo(sortInfo);
				}
			});
			eventBus.addHandler(SortTaxaByNameEvent.TYPE, new SortTaxaByNameEvent.SortTaxaByNameEventHandler() {
				@Override
				public void onSort(SortTaxaByNameEvent event) {
					taxonStore.clearSortInfo();
					StoreSortInfo<Taxon> sortInfo = new StoreSortInfo<Taxon>(new Comparator<Taxon>() {
						@Override
						public int compare(Taxon o1, Taxon o2) {
							return o1.getFullName().compareTo(o2.getFullName());
						}
					}, event.getSortDirection());
					taxonStore.addSortInfo(sortInfo);
				}
			});
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

		protected void setControlMode(Character character, ControlMode controlMode) {
			taxonMatrix.setControlMode(character, controlMode);
			editing.setControlMode(character, controlMode);
		}

		private void mergeCharacters(Character characterA, Character characterB, MergeMode mergeMode) {
			String mergedName = mergeName(characterA.getName(), characterB.getName(), mergeMode);
			Organ mergedOrgan = mergeOrgan(characterA.getOrgan(), characterB.getOrgan(), mergeMode);
			for(Taxon taxon : taxonMatrix.getTaxa()) {
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
			editing.setControlMode(characterA, ControlMode.OFF);
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
		
		private void moveCharacter(Character character, Character after) {
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
		
		private void moveTaxon(Taxon taxon, Taxon after, Taxon aftersParent) {
			taxonMatrix.moveTaxon(taxon, after, aftersParent);
			taxonStore.remove(taxon);
			switch(modelMode) {
			case FLAT:
				if(after == null) {
					taxonStore.insert(0, taxon);
				} else {
					taxonStore.insert(taxonStore.getRootItems().indexOf(after) + 1, taxon);
				}
				break;
			case CUSTOM_HIERARCHY:
			case TAXONOMIC_HIERARCHY:
				if(aftersParent == null) { 
					if(after == null) {
						insertToStoreRecursively(0, taxon);
					} else {
						insertToStoreRecursively(taxonStore.getRootItems().indexOf(after) + 1, taxon);
					}
				} else {
					if(after == null) {
						insertToStoreRecursively(aftersParent, 0, taxon);
					} else {
						insertToStoreRecursively(aftersParent, aftersParent.getChildren().indexOf(after) + 1, taxon);
					}
				}
				break;
			}
		}
		
		private void insertToStoreRecursively(Taxon aftersParent, int index, Taxon taxon) {
			taxonStore.insert(aftersParent, index, taxon);
			for(Taxon child : taxon.getChildren())
				insertToStoreRecursively(child);
		}

		private void insertToStoreRecursively(int index, Taxon taxon) {
			taxonStore.insert(index, taxon);
			for(Taxon child : taxon.getChildren())
				insertToStoreRecursively(child);
		}
		
		private void  insertToStoreRecursively(Taxon taxon) {
			if(!taxon.hasParent())
				taxonStore.add(taxon);
			else 
				taxonStore.add(taxon.getParent(), taxon);
			for(Taxon child : taxon.getChildren())
				insertToStoreRecursively(child);
		}
		
		public void modifyCharacter(Character character, String name, Organ organ) {
			taxonMatrix.renameCharacter(character, name);
			taxonMatrix.setOrgan(character, organ);
			CharactersColumnModel columnModel = taxonTreeGrid.getColumnModel();
			for(CharacterColumnConfig config: columnModel.getCharacterColumns()) {
				if(config.getCharacter().equals(character)) {
					config.setHeader(SafeHtmlUtils.fromString(character.toString()));
				}
			}
			taxonTreeGrid.updateCharacterGridHeads();
		}

		public void modifyTaxon(Taxon taxon, Taxon parent, Level level, String name, String author, String year) {
			boolean updateStore = false;
			updateStore = (taxon.getParent() == null && parent != null) || (taxon.getParent() == null || !taxon.getParent().equals(parent));
			taxonMatrix.modifyTaxon(taxon, parent, level, name, author, year);
			
			if(updateStore)
				switch(modelMode) {
				case FLAT:
					break;
				case TAXONOMIC_HIERARCHY:
				case CUSTOM_HIERARCHY:
					taxonStore.remove(taxon);
					if(parent != null)
						taxonStore.add(parent, taxon);
					else
						taxonStore.add(taxon);
					break;
				}
		}
		
		public void load(TaxonMatrix taxonMatrix) {
			this.taxonMatrix = taxonMatrix;
			this.valueCell = new ValueCell(eventBus, taxonMatrix);
			List<Taxon> taxa = taxonMatrix.getTaxa();
			
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
					for(Taxon taxon : taxa) {
						if(taxon.hasParent())
							continue;
						insertToStoreRecursively(taxon);
					}
					break;
			}

			if(taxonTreeGrid.isInitialized()) {
				List<CharacterColumnConfig> characterColumnConfigs = taxonTreeGrid.getColumnModel().getCharacterColumns();
				for(CharacterColumnConfig characterColumnConfig : characterColumnConfigs)
					characterColumnConfig.setCell(valueCell);
				taxonTreeGrid.reconfigure(characterColumnConfigs);
			} else {
				List<ColumnConfig<Taxon, ?>> characterColumnConfigs = new ArrayList<ColumnConfig<Taxon, ?>>();
				for(Character character : taxonMatrix.getCharacters())
					characterColumnConfigs.add(this.createCharacterColumnConfig(character));
				taxonTreeGrid.init(characterColumnConfigs, new CharactersGridView(eventBus, taxonMatrix));
				editing = new LockableControlableMatrixEditing(eventBus, taxonTreeGrid.getCharactersGrid(), taxonTreeGrid.getTreeGrid().getListStore());
				initCharacterEditing();
			}
		}
		
		private void initCharacterEditing() {
			for(CharacterColumnConfig config : taxonTreeGrid.getCharactersGrid().getColumnModel().getCharacterColumns()) {
				editing.addEditor(config);
				EditEventsHandler editEventsHandler = new EditEventsHandler();
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

		public void addTaxon(Taxon taxon) {
			taxonMatrix.addTaxon(taxon);
			taxonStore.add(taxon);
		}
		
		public void addTaxon(Taxon parent, int index, Taxon taxon) {
			taxonMatrix.addChild(parent, index, taxon);
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
		
		public void addTaxon(Taxon parent, Taxon taxon) {
			taxonMatrix.addChild(parent, taxon);
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
		
		private void removeTaxon(Taxon taxon) {
			taxonStore.remove(taxon);
			taxonMatrix.removeTaxon(taxon);
		}
		
		public void addCharacter(Character character) {
			this.addCharacterAfter(taxonMatrix.getCharacterCount() - 1, character);
		}

		private void removeCharacter(Character character) {
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
		
		public void addCharacterAfter(int colIndex, Character character) {
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
		@Override
		public void onCompleteEdit(CompleteEditEvent<Taxon> event) {
			GridCell cell = event.getEditCell();
			Taxon taxon = taxonTreeGrid.getTreeGrid().getListStore().get(cell.getRow());
			ColumnConfig<Taxon, Value> config = taxonTreeGrid.getColumnModel().getColumn(cell.getCol());
			Value value = config.getValueProvider().getValue(taxon);
			eventBus.fireEvent(new SetValueEvent(oldValue, value, true));
		}
		@Override
		public void onBeforeStartEdit(BeforeStartEditEvent<Taxon> event) {
			GridCell cell = event.getEditCell();
			Taxon taxon = taxonTreeGrid.getTreeGrid().getListStore().get(cell.getRow());
			ColumnConfig<Taxon, Value> config = taxonTreeGrid.getColumnModel().getColumn(cell.getCol());
			oldValue = config.getValueProvider().getValue(taxon);
		}
		
	}
	
	private TaxonStore taxonStore;
	private FrozenFirstColumTaxonTreeGrid taxonTreeGrid;
	private LockableControlableMatrixEditing editing;
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
				System.out.println("<i>" + value.getFullName() + "</i>");
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
