package edu.arizona.biosemantics.matrixreview.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.matrixreview.shared.IMatrixService;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;
import edu.arizona.biosemantics.common.taxonomy.Rank;

@SuppressWarnings("serial")
public class MatrixService extends RemoteServiceServlet implements IMatrixService {

	@Override
	public Model getMatrix() {
		return createSampleModel();
	}
	
	private Model createSampleModel() {
		List<Organ> hierarhicalCharacters = new LinkedList<Organ>();

		Organ o1 = new Organ("stem");
		List<Character> flatCharactersO1 = new LinkedList<Character>();
		Character a1 = new Character("length", o1, 0);
		Character a2 = new Character("shape", o1, 1);
		Character a3 = new Character("architecture", o1, 2);
		flatCharactersO1.add(a1);
		flatCharactersO1.add(a2);
		flatCharactersO1.add(a3);
		o1.setFlatCharacters(flatCharactersO1);
		
		Organ o2 = new Organ("leaf");
		List<Character> flatCharactersO2 = new LinkedList<Character>();
		Character b1 = new Character("width", o2, 0);
		Character b2 = new Character("shape", o2, 1);
		Character b3 = new Character("pubescence", o2, 2);
		flatCharactersO2.add(b1);
		flatCharactersO2.add(b2);
		flatCharactersO2.add(b3);
		o2.setFlatCharacters(flatCharactersO2);
		
		Organ o3 = new Organ("head");
		List<Character> flatCharactersO3 = new LinkedList<Character>();
		Character c1 = new Character("size", o3, 0);
		Character c2 = new Character("color", o3, 1);
		Character c3 = new Character("architecture", o3, 2);
		flatCharactersO3.add(c1);
		flatCharactersO3.add(c2);
		flatCharactersO3.add(c3);
		o3.setFlatCharacters(flatCharactersO3);
		
		
		hierarhicalCharacters.add(o1);
		hierarhicalCharacters.add(o2);
		hierarhicalCharacters.add(o3);		
		

		
		Taxon t1 = new Taxon(Rank.FAMILY, "rosacea", "author1", "1979", "this is the description about t1");
		Taxon t2 = new Taxon(Rank.GENUS, "rosa", "author2", "1985",  "this is the description about t2");
		Taxon t3 = new Taxon(Rank.SPECIES,
				"example", "author3", "2002", 
				"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
						+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
						+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
						+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
						+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
						+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
						+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
						+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
						+ "tincidunt diam nec urna. Curabitur velit.");
		Taxon t4 = new Taxon(Rank.VARIETY,
				"prototype", "author4", "2014", 
				"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
						+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
						+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
						+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
						+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
						+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
						+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
						+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
						+ "tincidunt diam nec urna. Curabitur velit.");
		t1.addChild(t2);
		t2.addChild(t3);
		t2.addChild(t4);
		List<Taxon> hierarchyTaxa = new LinkedList<Taxon>();
		hierarchyTaxa.add(t1);
		for(int i=0; i<100; i++) {
			Taxon t = new Taxon(Rank.FAMILY, "rosacea1", "author1", "1979", "this is the description about t1");
			hierarchyTaxa.add(t);
		}
			
		TaxonMatrix taxonMatrix = new TaxonMatrix(hierarhicalCharacters, hierarchyTaxa);
		
		Random random = new Random();
		taxonMatrix.setValue(t1, b1, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t2, b1, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t3, b1, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t4, b1, new Value(String.valueOf(random.nextInt(50))));
		
		taxonMatrix.setValue(t1, b2, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t2, b2, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t3, b2, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t4, b2, new Value(String.valueOf(random.nextInt(50))));

		taxonMatrix.setValue(t1, a1, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t2, a1, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t3, a1, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t4, a1, new Value(String.valueOf(random.nextInt(50))));
		
		taxonMatrix.setValue(t1, a2, new Value("oblong"));
		taxonMatrix.setValue(t2, a2, new Value("oblong"));
		taxonMatrix.setValue(t3, a2, new Value("squarish"));
		taxonMatrix.setValue(t4, a2, new Value("Squarish"));
		
		taxonMatrix.setValue(t1, a3, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t2, a3, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t3, a3, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t4, a3, new Value(String.valueOf(random.nextInt(50))));
		
		taxonMatrix.setValue(t1, a1, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t2, a1, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t3, a1, new Value(String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t4, a1, new Value(String.valueOf(random.nextInt(50))));
		
		taxonMatrix.setValue(t1, c1, new Value("big"));
		taxonMatrix.setValue(t2, c1, new Value("2mm"));
		taxonMatrix.setValue(t3, c1, new Value("4 - 6 cm"));
		taxonMatrix.setValue(t4, c1, new Value("longer than wide"));
		
		taxonMatrix.setValue(t1, c2, new Value("red"));
		taxonMatrix.setValue(t2, c2, new Value("redish"));
		
		taxonMatrix.setValue(t3, c3, new Value("wide"));
		taxonMatrix.setValue(t4, c3, new Value("spreading"));
			
		return new Model(taxonMatrix);
	}

	/*private TaxonMatrix readButterflyTaxonMatrix() {		
		try {
			SAXBuilder sax = new SAXBuilder();
			Document doc = sax.build("mapQuery.xml");
			Element root = doc.getRootElement();
			Element taxonEntries = root.getChild("TaxonEntries");	
			List<Element> taxonEntryList = taxonEntries.getChildren("TaxonEntry");
			
			Map<String, Character> characterMap = new HashMap<String, Character>();
			List<Character> characters = new LinkedList<Character>();
			for(Element itemsElement : taxonEntryList.get(0).getChildren("Items")) {
				Character character = new Character(itemsElement.getAttributeValue("name"));
				characterMap.put(itemsElement.getAttributeValue("name"), character);
				characters.add(character);
			}
			TaxonMatrix taxonMatrix = new TaxonMatrix(characters);
						
			for(Element taxonEntry : taxonEntryList) {
				Taxon taxon = new Taxon(Rank.SPECIES, taxonEntry.getAttributeValue("recordID"), "author", "2002", "The description");
				taxonMatrix.addRootTaxonHierarchically(taxon);
				
				List<Element> itemsList = taxonEntry.getChildren("Items");
				for(Element itemsElement : itemsList) {
					List<Element> items = itemsElement.getChildren("Item");
					
					String value = "";
					for(Element item : items) {
						value += item.getValue() + " | ";
					}
					taxonMatrix.setValue(taxon, characterMap.get(itemsElement.getAttributeValue("name")), new Value(value.substring(0, value.length() - 3)));
				}
				taxonMatrix.addRootTaxonHierarchically(taxon);
			}
			return taxonMatrix;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}*/

}
