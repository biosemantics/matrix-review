package edu.arizona.biosemantics.matrixreview.client.common;

import java.util.List;
import com.google.gwt.core.client.GWT;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;

import edu.arizona.biosemantics.matrixreview.client.matrix.form.AllowFreeTextComboBoxCell;
import edu.arizona.biosemantics.matrixreview.client.matrix.shared.AllAccessListStore;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.OrganProperties;

public class CharacterInformationContainer extends SimpleContainer {
	
		private ComboBox<Organ> organComboBox;
		private TextField characterNameField;

		public CharacterInformationContainer(final Model model, String initialName, Organ initialOrgan) {
			FieldSet fieldSet = new FieldSet();
		    fieldSet.setHeading("Character Information");
		    fieldSet.setCollapsible(false);
		    this.add(fieldSet, new MarginData(10));
		 
		    VerticalLayoutContainer p = new VerticalLayoutContainer();
		    fieldSet.add(p);
		    
		    OrganProperties organProperties = GWT.create(OrganProperties.class);
		    AllAccessListStore<Organ> store = new AllAccessListStore<Organ>(organProperties.key());
		    List<Organ> organs = model.getTaxonMatrix().getHierarchyCharacters();
		    store.addAll(organs);
		    organComboBox = new ComboBox<Organ>(new AllowFreeTextComboBoxCell<Organ>(store, organProperties.nameLabel(), 
		    		new Converter<Organ, String>() {
						@Override
						public Organ convertFieldValue(String name) {
							return model.getTaxonMatrix().getOrCreateOrgan(name);
						}

						@Override
						public String convertModelValue(Organ organ) {
							return organ.getName();
						}
		    }));
		    
		    organComboBox.setAllowBlank(true);
		    organComboBox.setForceSelection(false);
		    organComboBox.setTriggerAction(TriggerAction.ALL);
		    organComboBox.setValue(initialOrgan);
		    p.add(new FieldLabel(organComboBox, "Organ"), new VerticalLayoutData(1, -1));
		 
		    characterNameField = new TextField();
		    characterNameField.setValue(initialName);
		    characterNameField.setAllowBlank(false);
		    p.add(new FieldLabel(characterNameField, "Character Name"), new VerticalLayoutData(1, -1));
		}

		public ComboBox<Organ> getOrganComboBox() {
			return organComboBox;
		}

		public TextField getCharacterNameField() {
			return characterNameField;
		}
		
	}
	
	