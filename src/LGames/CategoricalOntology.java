package LGames;

import java.util.List;
import java.util.stream.Collectors;

import LGames.Cognition.Category;

public class CategoricalOntology extends Ontology<CategoryMeaning>{
	
	public List<CategoryMeaning> findMatchCategoricalValue(Category category, IsCategorical value) {
		
		return  meanings.stream()
				.filter(m -> m.getCategory() == category).filter(m -> m.getValue() == value)
				.collect(Collectors.toList());

	}
	
}