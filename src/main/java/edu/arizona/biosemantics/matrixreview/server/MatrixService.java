package edu.arizona.biosemantics.matrixreview.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.arizona.biosemantics.matrixreview.shared.Highlight;
import edu.arizona.biosemantics.matrixreview.shared.IMatrixService;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Organ;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Value;
import edu.arizona.biosemantics.common.ling.transform.IInflector;
import edu.arizona.biosemantics.common.taxonomy.Rank;
import edu.arizona.biosemantics.common.taxonomy.RankData;
import edu.arizona.biosemantics.common.taxonomy.TaxonIdentification;

@SuppressWarnings("serial")
public class MatrixService extends RemoteServiceServlet implements IMatrixService {

	@Override
	public Model getMatrix() {
		/**/
		Model model = null;
		try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(
				new File("C:/etcsitebase/etcsite/data/matrixGeneration/347/TaxonMatrix.ser"))))) {
			model = (Model)input.readObject();
			return model;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
		//return createSampleModel();
	}
	
	private Model createSampleModel() {
		List<Organ> hierarhicalCharacters = new LinkedList<Organ>();

		Organ o1 = new Organ("stem");
		List<Character> flatCharactersO1 = new LinkedList<Character>();
		Character a1 = new Character("length of something", "at", o1, 0);
		Character a2 = new Character("shape", "of", o1, 1);
		Character a3 = new Character("architecture", "of", o1, 2);
		flatCharactersO1.add(a1);
		flatCharactersO1.add(a2);
		flatCharactersO1.add(a3);
		o1.setFlatCharacters(flatCharactersO1);
		
		Organ o2 = new Organ("leaf / stem");
		List<Character> flatCharactersO2 = new LinkedList<Character>();
		Character b1 = new Character("width", "of", o2, 0);
		Character b2 = new Character("shape", "of", o2, 1);
		Character b3 = new Character("pubescence", "of", o2, 2);
		flatCharactersO2.add(b1);
		flatCharactersO2.add(b2);
		flatCharactersO2.add(b3);
		o2.setFlatCharacters(flatCharactersO2);
		
		Organ o3 = new Organ("head");
		List<Character> flatCharactersO3 = new LinkedList<Character>();
		Character c1 = new Character("size", "of", o3, 0);
		Character c2 = new Character("color", "of", o3, 1);
		Character c3 = new Character("architecture", "of", o3, 2);
		flatCharactersO3.add(c1);
		flatCharactersO3.add(c2);
		flatCharactersO3.add(c3);
		o3.setFlatCharacters(flatCharactersO3);
		
		
		hierarhicalCharacters.add(o1);
		hierarhicalCharacters.add(o2);
		hierarhicalCharacters.add(o3);		
		

		LinkedList<RankData> rankData = new LinkedList<RankData>();
		rankData.add(new RankData(Rank.FAMILY, "rosacea", null, "", ""));
		TaxonIdentification taxonIdentification = new TaxonIdentification(rankData, "author1", "1979");
		Taxon t1 = new Taxon(taxonIdentification, "this is the stem description width about t1");
		
		rankData = new LinkedList<RankData>();
		rankData.add(new RankData(Rank.GENUS, "rosa", null, "", ""));
		taxonIdentification = new TaxonIdentification(rankData, "author2", "1985");
		Taxon t2 = new Taxon(taxonIdentification, "this is the description about t2");
		
		rankData = new LinkedList<RankData>();
		rankData.add(new RankData(Rank.SPECIES, "example", null, "", ""));
		taxonIdentification = new TaxonIdentification(rankData, "author3", "2002");
		Taxon t3 = new Taxon(taxonIdentification,
				"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. "
						+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
						+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
						+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
						+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
						+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
						+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
						+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
						+ "tincidunt diam nec urna. Curabitur velit.");
		rankData = new LinkedList<RankData>();
		rankData.add(new RankData(Rank.VARIETY, "prototype", null, "", ""));
		taxonIdentification = new TaxonIdentification(rankData, "author4", "2014");
		Taxon t4 = new Taxon(taxonIdentification,
				"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. The highlighting of misinformation online."
						+ "Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl. "
						+ "Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus. Vestibulum auctor, "
						+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
						+ "vel lacus. In pellentesque nisl non sem. Suspendisse nunc sem, pretium eget, cursus a, "
						+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
						+ "porttitor laoreet. Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi. "
						+ "Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam. Vivamus "
						+ "tincidunt diam nec urna. Curabitur velit. To open to a fuller extent or wide range; stretch: spread out the tablecloth; a bird spreading its wings.");
		t4.addStatement("Lorem ipsum dolor sit amet, consectetuer adipiscing elit.");
		t4.addStatement("The highlighting of misinformation online.");
		t4.addStatement("Sed metus nibh, sodales a, porta at, vulputate eget, dui. Pellentesque ut nisl.");
		t4.addStatement("Maecenas tortor turpis, interdum non, sodales non, iaculis ac, lacus.");
		t4.addStatement("Vestibulum auctor, "
						+ "tortor quis iaculis malesuada, libero lectus bibendum purus, sit amet tincidunt quam turpis "
						+ "vel lacus.");
		t4.addStatement("In pellentesque nisl non sem. ");
		t4.addStatement("Suspendisse nunc sem, pretium eget, cursus a, "
						+ "fringilla vel, urna.<br/><br/>Aliquam commodo ullamcorper erat. Nullam vel justo in neque "
						+ "porttitor laoreet.");
		t4.addStatement("Aenean lacus dui, consequat eu, adipiscing eget, nonummy non, nisi.");
		t4.addStatement("Morbi nunc est, dignissim non, ornare sed, luctus eu, massa. Vivamus eget quam.");
		t4.addStatement("Vivamus "
						+ "tincidunt diam nec urna. Curabitur velit. ");
		t4.addStatement("To open to a fuller extent or wide range; stretch: spread out the tablecloth; a bird spreading its wings.");
		t1.addChild(t2);
		t2.addChild(t3);
		t2.addChild(t4);
		List<Taxon> hierarchyTaxa = new LinkedList<Taxon>();
		hierarchyTaxa.add(t1);
		for(int i=0; i<1; i++) {
			rankData = new LinkedList<RankData>();
			rankData.add(new RankData(Rank.FAMILY, "rosacea1", null, "", ""));
			taxonIdentification = new TaxonIdentification(rankData, "author1", "1979");
			Taxon t = new Taxon(taxonIdentification, "this is the description about t1");
			
			hierarchyTaxa.add(t);
		}
			
		TaxonMatrix taxonMatrix = new TaxonMatrix(hierarhicalCharacters, hierarchyTaxa);
		
		Random random = new Random();
		taxonMatrix.setValue(t1, b1, new Value(String.valueOf(random.nextInt(50)) + " / " + String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t2, b1, new Value(String.valueOf(random.nextInt(50)) + " / " + String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t3, b1, new Value(String.valueOf(random.nextInt(50)) + " / " + String.valueOf(random.nextInt(50))));
		taxonMatrix.setValue(t4, b1, new Value(String.valueOf(random.nextInt(50)) + " / " + String.valueOf(random.nextInt(50))));
		
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
		
		Value wideValue = new Value("wide");
		wideValue.addValueStatement("wide", "To open to a fuller extent or wide range; stretch: spread out the tablecloth; a bird spreading its wings. ");
		taxonMatrix.setValue(t3, c3, wideValue);
		
		Value v = new Value("spreading|highlighting");
		v.addValueStatement("spreading", "To open to a fuller extent or wide range; stretch: spread out the tablecloth; a bird spreading its wings. ");
		v.addValueStatement("highlighting", "The highlighting of misinformation online.");
		taxonMatrix.setValue(t4, c3, v);
			
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
	
	
	@Override
	public SafeHtml getHighlighted(String content, Collection<Character> characters, Collection<Value> values) {
		//IPOSKnowledgeBase posKnowledgeBase = new 
		//IInflector inflector = new edu.arizona.biosemantics.common.ling.transform.lib.SomeInflector(posKnowledgeBase, singulars, plurals)
		content = content.replaceAll("\n", "</br>");
		
		Set<Highlight> highlights = new HashSet<Highlight>();
		Set<String> usedOrgans = new HashSet<String>();
		Set<String> usedCharacters = new HashSet<String>();
		Set<String> usedValues = new HashSet<String>();
		for(Character character : characters) {
			if(!usedOrgans.contains(character.getOrgan().getName())) {
				usedOrgans.add(character.getOrgan().getName());
				highlights.add(new Highlight(character.getOrgan().getName(), "ff3300"));
			}
			if(!usedCharacters.contains(character.getName())) {
				usedCharacters.add(character.getName());
				highlights.add(new Highlight(character.getName(), "0033cc"));
			}
		}
		for(Value value : values) {
			if(!usedValues.contains(value.getValue())) {
				usedValues.add(value.getValue());
				highlights.add(new Highlight(value.getValue(), "009933"));
			}
		}
		for(Highlight highlight : highlights) {
			org.jsoup.nodes.Document document = Jsoup.parseBodyFragment(content);
			List<Node> result = new ArrayList<Node>();
			for(Node node : document.body().childNodes()) {
				if(node instanceof TextNode) {
					TextNode textNode = ((TextNode)node);
					String regex = createRegex(highlight);
					if(regex == null) {
						result.add(node);
					} else {
						List<Node> newNodes = createHighlightedNodes(textNode, regex, highlight.getColorHex());
						result.addAll(newNodes);
					}
				} else {
					result.add(node);
				}
			}
			org.jsoup.nodes.Element body = new org.jsoup.nodes.Element(Tag.valueOf("body"), "");
			for(Node newNode : result)
				body.appendChild(newNode);
			content = body.toString();
		}
		return SafeHtmlUtils.fromTrustedString(content);
	}
	

	private String createRegex(Highlight highlight) {
		System.out.println(highlight.getText());
		String parts = "";
		for(String part : highlight.getText().trim().split(" ")) {
			if(!part.isEmpty())
				parts += Pattern.quote(part) + "|";
		}
		if(!parts.isEmpty())
			parts = parts.substring(0, parts.length() - 1);
		if(!parts.isEmpty())
			return "\\b(" + parts + ")\\b";
		return null;
	}

	private List<Node> createHighlightedNodes(TextNode textNode, String regex, String colorHex) {
		List<Node> result = new ArrayList<Node>();
		String text = textNode.text();
		StringBuilder textBuilder = new StringBuilder(text);
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		if(!matcher.find()) {
			result.add(textNode);
			return result;
		}
		StringBuilder beforeReplaceText = new StringBuilder(textBuilder.substring(0, matcher.start(1)));
		String afterReplaceText = textBuilder.substring(matcher.end(1), textBuilder.length());
		result.add(new TextNode(beforeReplaceText.toString(), ""));
		org.jsoup.nodes.Element fontElement = new org.jsoup.nodes.Element(Tag.valueOf("font"), "");
		fontElement.attr("color", "#" + colorHex);
		fontElement.text(text.substring(matcher.start(1), matcher.end(1)));
		result.add(fontElement);
		result.addAll(createHighlightedNodes(new TextNode(afterReplaceText, ""), regex, colorHex));
		return result;
	}

}
