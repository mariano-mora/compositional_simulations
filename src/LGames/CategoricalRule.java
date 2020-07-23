package LGames;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import Util.Utils;

public class CategoricalRule extends Rules2 implements Comparable<CategoricalRule> {

	protected static int maxCover = 7;
	protected double pos_alpha = 1. + eta;
	protected double neg_alpha = 1. - eta;
	protected static double MAX_SCORE = 1.0;
	protected static double MIN_SCORE = 0.00000001;

	public Set<CategoryMeaning> meaningsSet = new HashSet<CategoryMeaning>();
	public Map<Integer, List<CategoricalRule>> coveredRules;
	// public Map<Integer, CategoricalRule> coveredRules;
	public Integer[] coveredDimensions = new Integer[1];
	public int cover;
	boolean isHolistic;
	public boolean head = false;
	public boolean split = false;
	public double contribution;
	public int creation;
	public int originalNumber;

	public CategoricalRule(int ruleNumber, List<CategoryMeaning> dcs, int cover) {
		super();
		this.number = ruleNumber;
		this.originalNumber = ruleNumber;
		this.meaningsSet.addAll(dcs);
		this.cover = cover;
		this.expression = createForm();
	}

	public CategoricalRule(int ruleNumber, List<CategoryMeaning> dcs, int cover, int interaction) {
		super();
		this.number = ruleNumber;
		this.originalNumber = ruleNumber;
		this.meaningsSet.addAll(dcs);
		this.cover = cover;
		this.expression = createForm();
		this.creation = interaction;
	}

	public CategoricalRule(int ruleNumber, List<CategoryMeaning> dcs, int cover, String expression, int interaction) {
		super();
		this.number = ruleNumber;
		this.originalNumber = ruleNumber;
		this.meaningsSet.addAll(dcs);
		this.cover = cover;
		this.expression = expression;
		this.creation = interaction;
	}

	public CategoricalRule(int ruleNumber, int cHead, int cTail, Collection<CategoryMeaning> r1Meanings,
			Collection<CategoryMeaning> r2Meanings, Collection<CategoryMeaning> common, int interaction) {
		super();
		this.cover = cHead + cTail;
		this.coveredDimensions = new Integer[2];
		this.coveredDimensions[0] = cHead;
		this.coveredDimensions[1] = cTail;
		this.meaningsSet.addAll(r1Meanings);
		this.meaningsSet.addAll(r2Meanings);
		this.meaningsSet.addAll(common);
		this.coveredRules = new HashMap<Integer, List<CategoricalRule>>();
		// this.coveredRules = new HashMap<Integer, CategoricalRule>();
		this.expression = null;
		this.creation = interaction;
		this.number = ruleNumber;
		this.originalNumber = ruleNumber;
	}

	public CategoricalRule(int ruleNumber, CategoricalRule r, int coverHead, int coverTail, List<CategoryMeaning> nonAligned1,
			List<CategoryMeaning> nonAligned2, List<CategoryMeaning> common, int index, int interaction)
			throws GrammarException {
		super();
		this.cover = r.cover;
		this.coveredDimensions = new Integer[3];
		if (r.coveredDimensions[index] != (coverHead + coverTail)) {
			throw new GrammarException("Wrong covers in supra constructor");
		}
		if (index == 0) {
			this.coveredDimensions[0] = coverHead;
			this.coveredDimensions[1] = coverTail;
			this.coveredDimensions[2] = r.coveredDimensions[1];
		} else {
			this.coveredDimensions[0] = r.coveredDimensions[0];
			this.coveredDimensions[1] = coverHead;
			this.coveredDimensions[2] = coverTail;
		}
		this.meaningsSet.addAll(nonAligned1);
		this.meaningsSet.addAll(nonAligned2);
		this.meaningsSet.addAll(common);
		this.meaningsSet.addAll(r.getMeaningsSet());
		this.coveredRules = new HashMap<Integer, List<CategoricalRule>>();
		// this.coveredRules = new HashMap<Integer, CategoricalRule>();
		this.expression = null;
		this.creation = interaction;
		this.number = ruleNumber;
		this.originalNumber = ruleNumber;
	}

	public CategoricalRule() {
	}

	public void initGame() {
		if (this.coveredRules != null) {
			this.coveredRules = new HashMap<Integer, List<CategoricalRule>>();
			// this.coveredRules = new HashMap<Integer, CategoricalRule>();
		}
		currentScore = 0.0;
	}

	public boolean match(List<CategoryMeaning> distinctiveMeanings, int cover) {
		if (this.cover != cover || !this.meaningsSet.containsAll(distinctiveMeanings))
			return false;
		maxScore = 1.0;
		currentScore = iScore * maxScore;
		return true;
	}

	public boolean partialMatch(List<CategoryMeaning> disMeanings) {
		if (!disMeanings.containsAll(this.meaningsSet))
			return false;
		maxScore = 1.0;
		currentScore = iScore * maxScore;
		return true;
	}

	public boolean matchesSubPart(List<CategoryMeaning> distinctiveMeanings, int cover) {
		if (this.cover != cover)
			return false;
		return this.partialMatch(distinctiveMeanings);
	}

	public boolean firstParse(String utterance) {
		if (expression != null) {
			if (!utterance.contains(expression))
				return false;
		}
		return true;
	}

	/**
	 * The second parse means that this rule is either compositional or the
	 * string terminal contains or is equal to the utterance
	 * 
	 * @param potentialRules
	 */
	public boolean secondParse(String utterance, List<CategoricalRule> potentialRules) {
		maxScore = 1.0;
		contribution = 0.0;
		currentScore = 0.0;

		if (this.getHolistic()) {
			if (!utterance.equals(this.expression)) {
				return false;
			}
			currentScore = iScore * maxScore;
			contribution = 1.;
			return true;
		}
		if (this.coveredDimensions.length > 1) {
			for (int i = 0; i < this.coveredDimensions.length; i++) {
//				double maxScore = 0.0;
				// CategoricalRule ruleCovered = null;
				int c = this.coveredDimensions[i];
				List<CategoricalRule> rulesCovered = new ArrayList<CategoricalRule>();
				List<CategoricalRule> filteredRules = potentialRules.stream().filter(r -> r.cover == c)
						.collect(Collectors.toList());
				for (CategoricalRule rule : filteredRules) {
					if (rule.coveredDimensions.length > 1) {
						for (int j = 0; j < rule.coveredDimensions.length; j++) {
							int subCover = rule.coveredDimensions[j];
							List<CategoricalRule> filteredSubRules = potentialRules.stream()
									.filter(r -> r.cover == subCover).collect(Collectors.toList());
							for (CategoricalRule r : filteredSubRules) {
								if (r.testSyntax(utterance, r.coveredDimensions.length, j)) {
									// ruleCovered = r;
									// maxScore = r.iScore;
									rulesCovered.add(r);
								}
							}
						}
					} else {
						if (rule.testSyntax(utterance, this.coveredDimensions.length, i)) {
							// ruleCovered = rule;
							// maxScore = rule.iScore;
							rulesCovered.add(rule);
						}
					}
				}
				// TODO: TRY THIS: FOR A RULE TO BE ACCEPTED ALL POSITIONS MUST
				// BE FULFILLED
				// if(rulesCovered.isEmpty()){
				// return false;
				// }
				if (!rulesCovered.isEmpty()) {
					this.coveredRules.put(c, rulesCovered);
				}

			}
			if (this.coveredRules.isEmpty()) {
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean testSyntax(String utterance, int coverLength, int pos) {
		if (pos == 0) {
			if (!utterance.startsWith(expression)) {
				return false;
			}
		} else {
			if (pos == coverLength - 1) {
				if (!utterance.endsWith(expression)) {
					return false;
				}
			} else {
				String sub = utterance.substring(1, utterance.length() - 1);
				if (!sub.contains(this.expression)) {
					return false;
				}
			}
		}
//		return meanings.containsAll(this.getMeaningsSet());
		return true;
	}

	public List<Set<CategoricalRule>> getCompositionRules() {
		List<Set<CategoricalRule>> rules = new ArrayList<Set<CategoricalRule>>();
		if (this.coveredDimensions.length == 1) {
			Set<CategoricalRule> set = new HashSet<CategoricalRule>();
			set.add(this);
			rules.add(set);
			
		} else {
			rules = combineRules();
		}
		return rules;
	}

	private List<Set<CategoricalRule>> combineRules() {
		
		List<Set<CategoricalRule>> layers = getLayers();
		List<Set<CategoricalRule>> productSets = cartesianProduct(0, layers);
		return productSets;
	}

	private List<Set<CategoricalRule>> getLayers() {
		List<Set<CategoricalRule>> layers = new ArrayList<Set<CategoricalRule>>();
		Set<CategoricalRule> root = new HashSet<CategoricalRule>();
		root.add(this);
		layers.add(root);
		for (int i = 0; i < this.coveredDimensions.length; i++) {
			Set<CategoricalRule> layer = new HashSet<CategoricalRule>();
			Integer c = this.coveredDimensions[i];
			if (this.coveredRules.containsKey(c) && !this.coveredRules.get(c).isEmpty()) {
				List<CategoricalRule> cRules = this.coveredRules.get(c);
				for (CategoricalRule r : cRules) {
					layer.add(r);
					if (r.coveredDimensions.length > 1 && !r.coveredRules.isEmpty()) {
						Set<CategoricalRule> nextLayer = layers.get(i + 1);
						if (nextLayer == null) {
							nextLayer = new HashSet<CategoricalRule>();
							layers.add(nextLayer);
						}
						for (List<CategoricalRule> third : r.coveredRules.values()) {
							for (CategoricalRule t : third) {
								nextLayer.add(t);
							}
						}
					}
				}
				layers.add(layer);
			}
		}
		if (layers.size() == 1) {
			return null;
		}
		return layers;
	}

	private List<Set<CategoricalRule>> cartesianProduct(int index, List<Set<CategoricalRule>> sets) {
		List<Set<CategoricalRule>> ret = new ArrayList<Set<CategoricalRule>>();
		if (index == sets.size()) {
			ret.add(new HashSet<CategoricalRule>());
		} else {
			for (CategoricalRule rule : sets.get(index)) {
				for (Set<CategoricalRule> set : cartesianProduct(index + 1, sets)) {
					set.add(rule);
					ret.add(set);
				}
			}
		}
		return ret;
	}

	private boolean testTwoSplitExpression(String utterance) {

		if (this.expression == null) {
			return false;
		}
		if (this.head) {
			if (!utterance.startsWith(this.expression)) {
				return false;
			}
		} else {
			if (!utterance.endsWith(this.expression)) {
				return false;
			}
		}
		return true;
	}

	private boolean testThreeSplitExpression(String utt, int position) {
		if (this.expression == null) {
			return false;
		}
		if (position == 0) {
			return utt.startsWith(this.expression);
		} else if (position == 2) {
			return utt.endsWith(this.expression);
		}
		String sub = utt.substring(1, utt.length() - 1);
		return sub.contains(this.expression);
	}

	public void updateScore(boolean correct) {
		if (correct) {
			iScore *= this.pos_alpha;
			totalUse++;
			// iScore = 1+eta * iScore + 1. - eta;
		} else {
			iScore *= this.neg_alpha;
			if (iScore < MIN_SCORE) {
				iScore = 0.0;
			}
			// iScore = eta * iScore;
		}
	}

	private boolean isHolistic() {
		return (this.coveredDimensions.length == 1 && this.cover == CategoricalRule.maxCover);
	}

	public Set<CategoryMeaning> getMeaningsSet() {
		return meaningsSet;
	}

	public double getScore() {
		return currentScore;
	}

	public int getCover() {
		return cover;
	}

	public double getIScore() {
		return iScore;
	}

	public void normalise(double total) {
		iScore /= total;
	}

	public String toString() {
		if (this.coveredDimensions.length == 1) {
			return new String("R_" + number + ": " + Utils.syntaxMap.get(this.cover) + " \u27fc" + " \"" + expression
					+ "\" / " + meaningsSet + " /" + Utils.format(iScore) + "/ " + getTotalUse() + " / "
					+ Integer.toString(this.creation)) + " / " + Integer.toString(this.originalNumber);
		} else {
			String covers = coversToString();
			return new String("R_" + number + ": " + Utils.syntaxMap.get(this.cover) + " \u27fc " + covers + "/ "
					+ meaningsSet + "/ " + Utils.format(iScore) + "/ " + getTotalUse() + " / "
					+ Integer.toString(this.creation) + " / " + Integer.toString(this.originalNumber));

		}
	}

	public String prettyString() throws GrammarException {
		if (this.coveredDimensions.length == 1) {
			return this.toString();
		} else {
			String covers = coversToString();
			return new String("R_" + number + ": " + cover + " \u27fc " + covers + "/ " + meaningsSet + "/ " + " / ("
					+ Utils.format(iScore) + ")/ " + getTotalUse() + " / " + Integer.toString(this.creation) + " / " + Integer.toString(this.originalNumber));
		}
	}

	private String coversToString() {
		StringBuilder cov = new StringBuilder();
		int counter = 0;
		for (Integer c : coveredDimensions) {
			if (counter > 0)
				cov.append("\u00b7");
			cov.append(Utils.syntaxMap.get(c));
			// List<Category> cats = Utils.calculateCategory(c);
			// for (Category cat : cats) {
			// cov.append(cat.getShortName());
			// }
			counter++;
		}
		return cov.toString();
	}

	public boolean getHolistic() {
		if (coveredDimensions.length == 1 && cover == maxCover)
			return true;
		return false;
	}

	public String getExpression() {
		String retval = null;
		if (coveredDimensions.length == 1 && expression != null)
			return expression;
		else {
			if (coveredRules.isEmpty()) {
				return retval;
			}
			if (this.coveredRules.values().size() != coveredDimensions.length) {
				return retval;
			}
			retval = new String();
			for (Integer c : this.coveredDimensions) {
				retval = retval.concat(this.coveredRules.get(c).get(0).getExpression());
			}
		}
		return retval;
	}

	public boolean isSameAs(CategoricalRule r2) {
		if (this.expression != null && !this.expression.equals(r2.expression)) {
			return false;
		}
		if (this.cover != r2.cover || this.coveredDimensions.length != r2.coveredDimensions.length) {
			return false;
		}
		if (this.coveredDimensions.length > 1) {
			int counter = 0;
			for (Integer c : this.coveredDimensions) {
				if (c != r2.coveredDimensions[counter++]) {
					return false;
				}
			}
		}
		return this.meaningsSet.equals(r2.getMeaningsSet());
	}

	@Override
	public int compareTo(CategoricalRule o) {
		if (this.coveredDimensions.length == o.coveredDimensions.length) {
			return Comparator.comparingInt(CategoricalRule::getTotalUse).thenComparing((CategoricalRule r) -> r.iScore)
					.thenComparingInt(CategoricalRule::getNumber).reversed().compare(this, o);
		}
		return this.coveredDimensions.length > o.coveredDimensions.length ? -1 : 1;
	}

	@Override
	public boolean equals(Object o) {
		return this.isSameAs((CategoricalRule) o);
	}

	public boolean isTerminal() {
		return this.expression != null;
	}

	public boolean isFullyCovered() {
		if (this.coveredRules.isEmpty()) {
			return false;
		}
		for (Integer c : coveredDimensions) {
			if (!this.coveredRules.containsKey(c)) {
				return false;
			}
		}
		return true;
	}

	public int getTotalUse() {
		return this.totalUse;
	}

	public Action getCorrespondingAction(List<Action> possibleActions) {
		for(Action action : possibleActions){
			if(Utils.countCommon(this.getMeaningsSet(), action.getMeanings()) == action.getMeanings().size()){
				return action;
			}
		}
		return null;
	}

	public boolean sameSemantics(CategoricalRule comp) {
		return this.meaningsSet.equals(comp.getMeaningsSet());
	}

	public int getOriginalNumber() {
		return originalNumber;
	}

}
