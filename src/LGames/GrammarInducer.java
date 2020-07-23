package LGames;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import Util.Utils;

public class GrammarInducer {

	private List<CategoryMeaning> currentMeanings;
	private Grammar<CategoricalRule> grammar;
	private RuleCreator creator;
	private String utterance;
	private boolean remove;
	private int memorySize = 200;

	public GrammarInducer(List<CategoryMeaning> currentMeanings, RuleCreator creator, String utterance,
			Grammar<CategoricalRule> grammar, boolean remove) {
		this.currentMeanings = currentMeanings;
		this.creator = creator;
		this.utterance = utterance;
		this.grammar = grammar;
		this.remove = remove;
	}

	public void induce(Action action, String utt, int interactionIndex) throws GrammarException {
		int cover = Utils.calculateCover(action.getMeanings());
		CategoricalRule tempRule = new CategoricalRule(9999, action.getMeanings(),
				Utils.calculateCover(action.getMeanings()), utt, interactionIndex);
//		CategoricalRule tempRule2 = this.creator.createRule(cover, action.getMeanings(), utt, , interaction)
		if (!grammar.getRules().contains(tempRule)){
			tempRule.number = creator.ruleNumber++;
			tempRule.originalNumber = tempRule.number;
			grammar.getRules().add(tempRule);
		}
		
		int i = 0;
		int j;
		String sub = null;
		CategoricalRule r1;
		CategoricalRule r2;
		boolean changed = false;
		while (i < grammar.size() - 1) {
			r1 = grammar.get(i);
			if (r1.expression == null || r1.getMeaningsSet().size() == 1) {
				i++;
				continue;
			}
			j = i + 1;
			while (j < grammar.size()) {
				r2 = grammar.get(j);
				if (!canBeSplit(r1, r2)) {
					j++;
					continue;
				}
				sub = findChunk(r1.expression, r2.expression);
				if (sub != null) {
					List<CategoryMeaning> common = Utils.getCommon(r1.getMeaningsSet(), r2.getMeaningsSet());
					if (common.size() == 1) {
						createSplitRules(r1, r2, common, sub, interactionIndex);
						if(this.remove){
							grammar.removeRule(r1);
							grammar.removeRule(r2);
							changed = true;
						}
					}
				}
				j++;
			}
			i++;
		}
//		grammar.getRules().remove(tempRule);
		if(changed){
			grammar.resetNumbers();
		}
	}

	private boolean canBeSplit(CategoricalRule r1, CategoricalRule r2) {
		return r2.expression != null && r2.getMeaningsSet().size() > 1 && r1.cover == r2.cover;
	}

	private void createSplitRules(CategoricalRule r1, CategoricalRule r2, List<CategoryMeaning> common, String sub, int interactionIndex)
			throws GrammarException {
		if (r1.split || r2.split) {
			return;
		}
		int coverCommon = Utils.calculateCover(common);
		boolean head = r1.getExpression().startsWith(sub);
		CategoricalRule created = creator.createRule(coverCommon, common, sub, head, interactionIndex);
		grammar.addRule(created);
		grammar.recordSplit(created.getOriginalNumber(), r1.getOriginalNumber(), r2.getOriginalNumber());
		List<CategoryMeaning> nonAligned1 = createSubRule(r1, common, sub, interactionIndex);
		List<CategoryMeaning> nonAligned2 = createSubRule(r2, common, sub, interactionIndex);
		int cover1 = Utils.calculateCover(nonAligned1);
		int cover2 = Utils.calculateCover(nonAligned2);
		r1.split = true;
		r2.split = true;
		CategoricalRule supraRule = createSupraRule(nonAligned1, nonAligned2, common, cover1, coverCommon, head, interactionIndex);
		grammar.addRule(supraRule);
		grammar.recordSupra(supraRule.getOriginalNumber(), r1.getOriginalNumber(), r2.getOriginalNumber());
		if (cover1 == cover2 && (cover1 + coverCommon) != CategoricalRule.maxCover) {
			Integer supraCover = cover1 + coverCommon;
			List<CategoricalRule> composRules = grammar.getRules().stream().filter(r -> r.coveredDimensions.length > 1)
					.collect(Collectors.toList());
			for (CategoricalRule r : composRules) {
				if (r.split)
					continue;
				List<Integer> covers = Arrays.asList(r.coveredDimensions);
				if (covers.size() < 3 && covers.contains(supraCover)) {
					int index = covers.indexOf(supraCover);
					CategoricalRule supraSupra = createSupraSupraRule(r, nonAligned1, nonAligned2, common, cover1, coverCommon, head, index, interactionIndex);
					grammar.addRule(supraSupra);
					grammar.recordSupraSupra(supraSupra.getOriginalNumber(), r.getOriginalNumber());
					r.split = true;
				}
			}
		}
	}

	private List<CategoryMeaning> createSubRule(CategoricalRule rule, List<CategoryMeaning> common, String sub, int index) {
		String expression = rule.getExpression();
		boolean head = expression.startsWith(sub);
		int begin = head ? sub.length() : 0;
		int end = head ? expression.length() : expression.length() - sub.length();
		String subExpression = expression.substring(begin, end);
		List<CategoryMeaning> nonAligned = Utils.getComplement(common, rule.getMeaningsSet());
		int cover = Utils.calculateCover(nonAligned);
		CategoricalRule r = creator.createRule(cover, nonAligned, subExpression, !head, index);
		grammar.addRule(r);
		grammar.recordSub(r.getOriginalNumber(), rule.getOriginalNumber());
		rule.split = true;
		return nonAligned;
	}

	private CategoricalRule createSupraRule(List<CategoryMeaning> nonAligned1, List<CategoryMeaning> nonAligned2,
			List<CategoryMeaning> common, int cover, int coverCommon, boolean head, int index) {
		int coverHead = head ? coverCommon : cover;
		int coverTail = head ? cover : coverCommon;
		CategoricalRule created = creator.createCompositionalRule(coverHead, coverTail, nonAligned1, nonAligned2, common, index);
		return created;
	}

	private CategoricalRule createSupraSupraRule(CategoricalRule r, List<CategoryMeaning> nonAligned1,
			List<CategoryMeaning> nonAligned2, List<CategoryMeaning> common, int cover, int coverCommon, boolean head,
			int index, int interaction) throws GrammarException {
		int coverHead = head ? coverCommon : cover;
		int coverTail = head ? cover : coverCommon;
		CategoricalRule supra = creator.createSupraCompositionalRule(r, coverHead, coverTail, nonAligned1, nonAligned2, common, index, interaction);
		return supra;
	}

	public String splitInstance(Instance instance, String utterance) {
		String sub = findChunk(instance.getExpression(), utterance);
		if (sub == null) {
			return null;
		}
		if (Collections.disjoint(currentMeanings, instance.getMeanings())
				|| currentMeanings.containsAll(instance.getMeanings())) {
			return null;
		}
		return sub;
	}

	private String findChunk(String inst, String utt) {
		String sub = Utils.largestSubString(inst, utt);
		if (sub.equals("") || sub.equals(utt)) {
			return null;
		}
		return sub;
	}

	public void forgetRules(){
		if(grammar.size() <= memorySize){
			return;
		}
		grammar.getRules().sort(Comparator.comparingInt(CategoricalRule::getTotalUse).thenComparingDouble(CategoricalRule::getIScore).reversed());
		for(int i = grammar.size()-1; i >= memorySize; i--){
			grammar.remove(i);
		}
	}
	
	public List<CategoryMeaning> getCurrentMeanings() {
		return currentMeanings;
	}

	public void setCurrentMeanings(List<CategoryMeaning> currentMeanings) {
		this.currentMeanings = currentMeanings;
	}
}
