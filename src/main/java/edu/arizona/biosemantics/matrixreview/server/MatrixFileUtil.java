package edu.arizona.biosemantics.matrixreview.server;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;
import edu.arizona.biosemantics.matrixreview.shared.model.Model;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.core.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.core.TaxonMatrix;


/**
 * convert matrix model to multiple format of CSVs
 * @author maojin, hong
 *
 */
public class MatrixFileUtil {
	
	public void generateSimpleCSVwithSentences(String filePath, TaxonMatrix matrix) throws Exception{
		File file = new File(filePath);
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception();
		}
		
		int columns = matrix.getCharacterCount()*2 + 1;
		String[] characters = new String[columns];
		List<Character> flatCharacters = matrix.getFlatCharacters();
		characters[0] = "Taxa/Characters";
		int i=1;
		for(Character character : flatCharacters) {
			if(matrix.isVisiblyContained(character)) 
				characters[i++] = character.toString();
			    characters[i++] = "source sentence"; //add header
		}
		
		FileOutputStream outputStream = new FileOutputStream(file,false);
		CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8")), ',','"',CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);	
		
		outputStream.write(239);
		outputStream.write(187);
		outputStream.write(191);
		
		csvWriter.writeNext(characters);
		for(Taxon taxon : matrix.getFlatTaxa()) {
			String[] line = new String[columns];
			line[0] = taxon.getBiologicalName();
			i = 1;
			for(Character character : flatCharacters) {
				if(matrix.isVisiblyContained(character)) 
					line[i++] = matrix.getValue(taxon, character).toString();
				    System.out.println("value="+matrix.getValue(taxon, character).toString());
				    line[i++] = matrix.getValue(taxon, character).getStatements(); //add sentence
				    System.out.println("sentence="+matrix.getValue(taxon, character).getStatements());
			}
			csvWriter.writeNext(line);
		}
		
		csvWriter.flush();
		csvWriter.close();
	}
	
	
	/**
	 * generate simple csv file with UTF-8
	 * 
	 * TODO: deal with "whole organism"
	 * 
	 * @param filePath
	 * @param model
	 * @throws Exception
	 */
	public void generateSimpleCSV(String filePath, TaxonMatrix matrix) throws Exception{
		File file = new File(filePath);
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception();
		}
		
		int columns = matrix.getCharacterCount() + 1;
		String[] characters = new String[columns];
		List<Character> flatCharacters = matrix.getFlatCharacters();
		characters[0] = "Taxa/Characters";
		int i=1;
		for(Character character : flatCharacters) {
			if(matrix.isVisiblyContained(character)) 
				characters[i++] = character.toString();
		}
		
		FileOutputStream outputStream = new FileOutputStream(file,false);
		CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8")), ',','"', CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);	
		
		outputStream.write(239);
		outputStream.write(187);
		outputStream.write(191);
		
		csvWriter.writeNext(characters);
		for(Taxon taxon : matrix.getFlatTaxa()) {
			String[] line = new String[columns];
			line[0] = taxon.getBiologicalName();
			i = 1;
			for(Character character : flatCharacters) {
				if(matrix.isVisiblyContained(character)) 
					line[i++] = matrix.getValue(taxon, character).toString();
			}
			csvWriter.writeNext(line);
		}
		
		csvWriter.flush();
		csvWriter.close();
	}
	
	
	
	/**
	 * 
	 * generate a format of csv that could be used by MatrixConverter
	 * MatrixConverter has a conversion UI.
	 * 
	 * https://github.com/gburleigh/MatrixConverter
	 * 
	 * @param filePath
	 * @param model
	 * @throws Exception
	 */
	public void generateMatrixConverterCSV(String filePath, TaxonMatrix matrix) throws Exception{
		File file = new File(filePath);
		file.getParentFile().mkdirs();
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new Exception();
		}
		
		int columns = matrix.getCharacterCount() + 1;
		String[] characters = new String[columns];
		List<Character> flatCharacters = matrix.getFlatCharacters();
		characters[0] = "Taxa/Characters";
		int i=1;
		for(Character character : flatCharacters) {
			if(matrix.isVisiblyContained(character)) 
				characters[i++] = character.toString();
		}
		
		FileOutputStream outputStream = new FileOutputStream(file,false);
		CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8")), ',','"', CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);	
			
		outputStream.write(239);
		outputStream.write(187);
		outputStream.write(191);
		
		csvWriter.writeNext(characters);
		for(Taxon taxon : matrix.getFlatTaxa()) {
			String[] line = new String[columns];
			line[0] = taxon.getBiologicalName();
			i = 1;
			for(Character character : flatCharacters) {
				if(matrix.isVisiblyContained(character)) 
					line[i++] = matrix.getValue(taxon, character).toString().replace(",", " ");
			}
			csvWriter.writeNext(line);
		}
		
		csvWriter.flush();
		csvWriter.close();
	}
	
	
	
	
	public static void main(String[] args){
		Model model = null;
		try(ObjectInput input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(
				new File("C:/Users/hongcui/Documents/etcsite/data/matrixGeneration/112/TaxonMatrix.ser"))))
				/*new File("C:/Users/hongcui/Documents/etc-development/matrices/TaxonMatrix.ser"))))*/) {
			Object o = input.readObject();
			model = (Model) o;
		
			MatrixFileUtil matrixFileUtil = new MatrixFileUtil();
			//matrixFileUtil.generateSimpleCSV("C:/Users/hongcui/Documents/etc-development/matrices/simple.csv", model.getTaxonMatrix());
			matrixFileUtil.generateMatrixConverterCSV("C:/Users/hongcui/Documents/etc-development/matrices/matrixconverter.csv", model.getTaxonMatrix());
			//matrixFileUtil.generateSimpleCSVwithSentences("C:/Users/hongcui/Documents/etc-development/matrices/matrixsentences.csv", model.getTaxonMatrix());

		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
