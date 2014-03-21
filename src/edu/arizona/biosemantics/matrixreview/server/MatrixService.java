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
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.Value;

@SuppressWarnings("serial")
public class MatrixService extends RemoteServiceServlet implements IMatrixService {

	@Override
	public TaxonMatrix getMatrix() {
		return createSampleMatrix();
	}
	
	private TaxonMatrix createSampleMatrix() {
		List<Character> characters = new LinkedList<Character>();
		Character a = new Character("a");
		Character b = new Character("b");
		Character c = new Character("c");
		characters.add(a);
		characters.add(b);
		characters.add(c);

		Taxon t1 = new Taxon("t1", "this is the description about t1");
		Taxon t2 = new Taxon("t2", "this is the description about t2");
		Taxon t3 = new Taxon(
				"t3",
				"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
						+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
						+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
						+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
						+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
						+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
						+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
						+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
						+ "tincidunt diam nec urna. Curabitur velit.");

		TaxonMatrix taxonMatrix = new TaxonMatrix(characters);
		taxonMatrix.addTaxon(t1);
		taxonMatrix.addTaxon(t2);
		taxonMatrix.addTaxon(t3);
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
				Taxon taxon = new Taxon(taxonEntry.getAttributeValue("recordID"), "The description");
				taxonMatrix.addTaxon(taxon);
				
				List<Element> itemsList = taxonEntry.getChildren("Items");
				for(Element itemsElement : itemsList) {
					List<Element> items = itemsElement.getChildren("Item");
					
					String value = "";
					for(Element item : items) {
						value += item.getValue() + " | ";
					}
					taxon.put(characterMap.get(itemsElement.getAttributeValue("name")), new Value(value.substring(0, value.length() - 3)));
				}
				taxonMatrix.addTaxon(taxon);
			}
			System.out.println(taxonMatrix.getCharacters().size());
			System.out.println(taxonMatrix.getTaxa().size());
			return taxonMatrix;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
