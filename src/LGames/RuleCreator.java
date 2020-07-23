package LGames;

import java.util.Collection;
import java.util.List;

public class RuleCreator {

	protected int ruleNumber;
	private Grammar<CategoricalRule> grammar;

	public RuleCreator(Grammar<CategoricalRule> g) {
		this.grammar = g;
		this.ruleNumber = 0;
	}

	protected CategoricalRule createHolisticRule(int cover, List<CategoryMeaning> distinctiveMeanings, boolean store, int interaction) {
		int number = store ? this.ruleNumber++ : 0;
		CategoricalRule rule = new CategoricalRule(number, distinctiveMeanings, cover, interaction);
		if (store)
			grammar.addRule(rule);
		return rule;
	}

	public CategoricalRule createHolisticRule(int cover, List<CategoryMeaning> distinctiveMeanings, boolean store) {
		int number = store ? this.ruleNumber++ : 0;
		CategoricalRule rule = new CategoricalRule(number, distinctiveMeanings, cover);
		if (store)
			grammar.addRule(rule);
		return rule;
	}
	public void createHolisticRule(int cover, List<CategoryMeaning> meanings, String utterance, int interaction) {
		CategoricalRule rule = new CategoricalRule(this.ruleNumber++, meanings, cover, utterance, interaction);
		grammar.addRule(rule);
	}

	protected void createRule(int cover, List<CategoryMeaning> diMeanings, String expression, int interaction) {
		CategoricalRule rule = new CategoricalRule(this.ruleNumber++, diMeanings, cover, expression, interaction);
		grammar.addRule(rule);
	}

	public CategoricalRule createCompositionalRule(int coverHead, int coverTail, Collection<CategoryMeaning> different1,
			Collection<CategoryMeaning> different2, Collection<CategoryMeaning> common, int interaction) {

		CategoricalRule rule = new CategoricalRule(this.ruleNumber++, coverHead, coverTail, different1, different2,
				common, interaction);
		return rule;
	}

	public CategoricalRule createRule(int cover, List<CategoryMeaning> meanings, String expression, boolean b, int interaction) {
		CategoricalRule rule = new CategoricalRule(this.ruleNumber++, meanings, cover, expression, interaction);
		rule.head = b;
		return rule;
	}

	public CategoricalRule createSupraCompositionalRule(CategoricalRule r, int coverHead, int coverTail,
			List<CategoryMeaning> nonAligned1, List<CategoryMeaning> nonAligned2, List<CategoryMeaning> common, int index, int interaction) throws GrammarException {
		CategoricalRule rule = new CategoricalRule(this.ruleNumber++, r, coverHead, coverTail, nonAligned1, nonAligned2, common, index, interaction);
		return rule;
	}



}
