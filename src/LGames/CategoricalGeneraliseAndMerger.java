package LGames;

import java.util.List;

import Util.Utils;

public class CategoricalGeneraliseAndMerger implements GeneraliseAndMerger {

	private Grammar<CategoricalRule> grammar;

	public CategoricalGeneraliseAndMerger(Grammar<CategoricalRule> grammar) {
		this.grammar = grammar;
	}

	@Override
	public void generalise() {

	}

	@Override
	public void generaliseAndMerge() {
		int i = 0, j = 0;
		boolean changed = false;
		CategoricalRule rule1;
		CategoricalRule rule2;
		while (i < this.grammar.size() - 1) {
			rule1 = this.grammar.get(i);
			j = i + 1;
			while (j < this.grammar.size()) {
				rule2 = this.grammar.get(j);
				if(mergeRules(rule1, rule2)){
//					grammar.recordMerged(rule1, j);
					grammar.remove(j);
					changed = true;
				}
				else if(generaliseRules(rule1, rule2)){
					grammar.remove(j);
					changed = true;
					grammar.recordGeneralised(rule1.getOriginalNumber());
				}
				j++;
			}
			i++;
		}
		if (changed) {
			this.grammar.resetNumbers();
		}
	}

	/**
	 * If two rules cover the same semantic space, in the same order and only
	 * vary in one meaning, add that meaning.
	 */

	public static boolean canGeneralise(CategoricalRule r1, CategoricalRule r2) {
		if (r1.coveredDimensions.length == 1 || (r1.coveredDimensions.length != r2.coveredDimensions.length))
			return false;
		for (int i = 0; i < r1.coveredDimensions.length; i++) {
			if (r1.coveredDimensions[i] != r2.coveredDimensions[i]) {
				return false;
			}
		}
		
		List<CategoryMeaning> diff = Utils.getDifference(r1.getMeaningsSet(), r2.getMeaningsSet());
		if(diff.size() == 1){
			return true;
		}
		if(diff.size() ==2){
			if(diff.get(0).getCategory() == diff.get(1).getCategory()){
				return true;
			}
		}
		return false;
	}
	
	public static boolean generaliseRules(CategoricalRule r1, CategoricalRule r2){
		if(canGeneralise(r1, r2)){
			r1.iScore = Math.max(r1.iScore, r2.iScore);
			r1.getMeaningsSet().addAll(r2.getMeaningsSet());
			return true;
		}
		return false;
	}

	/**
	 * If two rules cover the same semantic space and use the same expression,
	 * then they are the same
	 */

	public static boolean mergeRules(CategoricalRule rule1, CategoricalRule rule2) {

		if (canMerge(rule1, rule2)) {
			rule1.getMeaningsSet().addAll(rule2.getMeaningsSet());
			rule1.iScore = Math.max(rule1.iScore, rule2.iScore);
			return true;
		}
		return false;
	}

	private static boolean canMerge(CategoricalRule rule1, CategoricalRule rule2) {
		return rule1.isSameAs(rule2);
	}

}
