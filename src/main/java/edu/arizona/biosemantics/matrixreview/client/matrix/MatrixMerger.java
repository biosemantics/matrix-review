package edu.arizona.biosemantics.matrixreview.client.matrix;

import java.util.List;
import java.util.Set;

import edu.arizona.biosemantics.matrixreview.shared.model.Character;
import edu.arizona.biosemantics.matrixreview.shared.model.Taxon;
import edu.arizona.biosemantics.matrixreview.shared.model.TaxonMatrix;

public class MatrixMerger {

	public TaxonMatrix createSubMatrix(TaxonMatrix fullMatrix, List<Character> characters, List<Taxon> rootTaxa) {
		TaxonMatrix result = new TaxonMatrix(characters);
		result.addRootTaxa(rootTaxa);
		return result;
		
		/*fullMatrix.clear();
		for(Character character : characters)
			fullMatrix.addCharacter(character);
		for(Taxon taxon : rootTaxa)
			fullMatrix.addRootTaxa(rootTaxa);
		return fullMatrix;*/
	}
	
	public void mergeToFullMatrix(TaxonMatrix fullMatrix, TaxonMatrix subMatrix) {
		//TODO
	}

}
