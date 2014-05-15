package edu.arizona.biosemantics.matrixreview.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.matrixreview.shared.IMatrixService;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon.Level;

@SuppressWarnings("serial")
public class MatrixService extends RemoteServiceServlet implements IMatrixService {

	@Override
	public TaxonMatrix getMatrix() {
		return createSampleMatrix();
	}
	
	private TaxonMatrix createSampleMatrix() {
		List<Character> characters = new LinkedList<Character>();
		Organ o1 = new Organ("o");
		Organ o2 = new Organ("o2");
		Character a = new Character("a");
		Character b = new Character("b", o1);
		Character c = new Character("c", o2);
		characters.add(a);
		characters.add(b);
		characters.add(c);
		
		for(int i=0; i<20; i++) {
			Character o = new Character("o" + i, o2);
			characters.add(o);
		}
		
		TaxonMatrix taxonMatrix = new TaxonMatrix(characters);

		Taxon t1 = new Taxon("server1", Level.GENUS, "t1", "author", "2002", "this is the description about t1");
		Taxon t2 = new Taxon("server2", Level.SPECIES, "t2", "author", "2002",  "this is the description about t2");
		Taxon t3 = new Taxon("server3", Level.LIFE,
				"t3", "author", "2002", 
				"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
						+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
						+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
						+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
						+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
						+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
						+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
						+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
						+ "tincidunt diam nec urna. Curabitur velit.");
		
		taxonMatrix.addRootTaxon(t1);
		taxonMatrix.addTaxon(t1, t2);
		taxonMatrix.addRootTaxon(t3);
		
		for(int i=4; i<50; i++) {
			Taxon t4 = new Taxon("server" + i, Level.SPECIES, "t123", "a", "2", "de");
			taxonMatrix.addRootTaxon(t4);
		}
		
		taxonMatrix.setValue(t1, a, new Value("some value"));
		return taxonMatrix;
	}

	private TaxonMatrix readButterflyTaxonMatrix() {		
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
				Taxon taxon = new Taxon("server" + taxonEntryList.indexOf(taxonEntry), Level.SPECIES, taxonEntry.getAttributeValue("recordID"), "author", "2002", "The description");
				taxonMatrix.addRootTaxon(taxon);
				
				List<Element> itemsList = taxonEntry.getChildren("Items");
				for(Element itemsElement : itemsList) {
					List<Element> items = itemsElement.getChildren("Item");
					
					String value = "";
					for(Element item : items) {
						value += item.getValue() + " | ";
					}
					taxonMatrix.setValue(taxon, characterMap.get(itemsElement.getAttributeValue("name")), new Value(value.substring(0, value.length() - 3)));
				}
				taxonMatrix.addRootTaxon(taxon);
			}
			return taxonMatrix;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
