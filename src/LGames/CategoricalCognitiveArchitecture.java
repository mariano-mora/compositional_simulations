package LGames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import LGames.Agent.Role;
import LGames.ContextStrategic.Destination;
import LGames.InteractionGame.InteractionElements;
import LGames.InteractionGame.InteractionOutcome;
import LGames.InteractionGame.ProductionMode;
import Util.Utils;

public class CategoricalCognitiveArchitecture extends Cognition {

	public class ContributingRule {
		public CategoricalRule rule;
		public double contribution = 0.0;

		public ContributingRule(CategoricalRule r, double c) {
			this.rule = r;
			this.contribution = c;
		}

		public double getContribution() {
			return this.contribution;
		}

		public String toString() {
			return new String(rule.toString() + ": " + Double.toString(contribution));
		}
	}

	public Ontology<CategoryMeaning> ontology = new Ontology<CategoryMeaning>();
	protected Grammar<CategoricalRule> grammar = new Grammar<CategoricalRule>();
	protected RuleCreator ruleCreator = new RuleCreator(grammar);

	private Context cxt;
	private Destination destination;
	private ContextObject object;
	private List<CategoryMeaning> distinctiveMeanings;
	List<Action> actionsPerformed;
	List<Action> possibleActions;
	Action chosenAction;
	List<CategoryMeaning> chosenMeanings = null;
	CategoricalRule chosenRule = null;
	public Map<Action, List<CompositionCombinations>> compositionsEmployed = null;
	int times = 0;
	public int nInteractions = 0;
	double total = 0.;
	private boolean removeRules = false;
	private Comparator<CompositionCombinations> actionComparator = Comparator
			.comparingDouble(CompositionCombinations::getContribution)
			.thenComparingDouble(CompositionCombinations::getScore);

	public CategoricalCognitiveArchitecture() {
		super();
	}

	public CategoricalCognitiveArchitecture(CategoricalCognitiveArchitecture other) {
		this.ontology = other.ontology;
		this.grammar = other.grammar;
		this.ruleCreator = other.ruleCreator;
	}

	public void prepareForInteraction(Role role) {
		distinctiveMeanings = new ArrayList<CategoryMeaning>();
		chosenMeanings = null;
		chosenRule = null;
		chosenAction = null;
		this.grammar.stream().forEach(r -> r.initGame());
		if (role == Role.HEARER) {
			this.actionsPerformed = new ArrayList<Action>();
			this.possibleActions = new ArrayList<Action>();
			this.compositionsEmployed = new HashMap<Action, List<CompositionCombinations>>();
		}
	}

	public void resetArchitecture() {
		distinctiveMeanings = new ArrayList<CategoryMeaning>();
		chosenMeanings = null;
		chosenRule = null;
		chosenAction = null;
		this.compositionsEmployed = new HashMap<Action, List<CompositionCombinations>>();
		this.grammar.stream().forEach(r -> r.initGame());
	}

	public long countInOntology(Category cat, IsCategorical value) {
		return ontology.getMeanings().stream().filter(m -> m.getCategory() == cat).filter(m -> m.getValue() == value)
				.count();
	}

	public List<CategoryMeaning> findInOntology(Category cat, IsCategorical value) {
		return ontology.getMeanings().stream().filter(m -> m.getCategory() == cat).filter(m -> m.getValue() == value)
				.collect(Collectors.toList());
	}

	private long countCategoryInObjects(Category cat, IsCategorical value) {
		Stream<ContextObject> objects = Arrays.stream(cxt.getObjects());
		return objects.filter(m -> m.values.get(cat) == value).count();
	}

	private Stream<ContextObject> filterObjectsByCategory(Stream<ContextObject> objects, Category cat,
			IsCategorical value) {
		return objects.filter(m -> m.values.get(cat) == value);
	}

	public void categoriseObjects() {
		for (ContextObject object : this.cxt.getObjects()) {
			for (Entry<Category, IsCategorical> entry : object.values.entrySet()) {
				if (countInOntology(entry.getKey(), entry.getValue()) == 0) {
					ontology.addMeaning(new CategoryMeaning(entry.getKey(), entry.getValue()));
				}
			}
		}
	}

	public void categoriseDestination(Destination dest) {
		if (countInOntology(dest.getCategory(), dest) == 0) {
			ontology.addMeaning(new CategoryMeaning(dest.getCategory(), dest));
		}
	}

	public void categoriseDestinations() {
		for (Destination dest : Destination.values()) {
			categoriseDestination(dest);
		}
	}

	/**
	 * Are there any categories where the selected object is unique in the
	 * context?
	 */
	public void selectDistinctiveCategories() {
		List<ContextObject> filtered = Arrays.asList(cxt.getObjects());
		for (Entry<Category, IsCategorical> entry : object.values.entrySet()) {
			long repeated = countCategoryInObjects(entry.getKey(), entry.getValue());
			if (repeated == 1) {
				distinctiveMeanings.clear();
				distinctiveMeanings.addAll(this.findInOntology(entry.getKey(), entry.getValue()));
			} else {
				filtered = filterObjectsByCategory(filtered.stream(), entry.getKey(), entry.getValue())
						.collect(Collectors.toList());
				if (filtered.size() > 0) {
					distinctiveMeanings.addAll(this.findInOntology(entry.getKey(), entry.getValue()));
				}
			}
		}
		distinctiveMeanings.addAll(findInOntology(Category.DESTINATION, destination));
	}

	// ENCODING AND DECODING ///

	public CategoricalSymbols produceUtterance() {
		CategoricalSymbols utt = null;
		int cover = Utils.calculateCover(distinctiveMeanings);
		utt = createSymbolFromFullRule(cover);
		if (utt == null) {
			chosenRule = null;
			utt = inventFromPartialRules();
		}
		if (utt == null) {
			chosenRule = ruleCreator.createHolisticRule(cover, distinctiveMeanings, false);
			utt = new CategoricalSymbols(chosenRule, chosenRule.getExpression(), ProductionMode.HOLISTIC_CREATION);
		}
		return utt;
	}

	public CategoricalSymbols produceUtterance(boolean store, int interaction) {
		CategoricalSymbols utt = null;
		int cover = Utils.calculateCover(distinctiveMeanings);
		utt = createSymbolFromFullRule(cover);
		if (utt == null) {
			chosenRule = null;
			utt = inventFromPartialRules();
		}
		if (utt == null) {
			chosenRule = ruleCreator.createHolisticRule(cover, distinctiveMeanings, store, interaction);
			utt = new CategoricalSymbols(chosenRule, chosenRule.getExpression(), ProductionMode.HOLISTIC_CREATION);
		}
		return utt;
	}

	private CategoricalSymbols createSymbolFromFullRule(int cover) {
		CategoricalSymbols utt = null;
		List<CategoricalRule> potentialRules = this.grammar.stream().filter(r -> r.match(distinctiveMeanings, cover))
				.collect(Collectors.toList());
		if (!potentialRules.isEmpty()) {
			utt = constructUtteranceFromRule(potentialRules);
		}
		return utt;
	}

	private CategoricalSymbols inventFromPartialRules() {
		CategoricalSymbols utt = null;
		List<CategoricalRule> potentialRules = grammar.getRules().stream()
				.filter(r -> r.getCover() != CategoricalRule.maxCover).collect(Collectors.toList());
		if (potentialRules.isEmpty()) {
			return utt;
		}
		List<CategoryMeaning> common = null;
		int maxSize = 0;
		double maxScore = -1.;
		for (CategoricalRule r : potentialRules) {
			if (r.partialMatch(distinctiveMeanings)) {
				common = Utils.getCommon(distinctiveMeanings, r.getMeaningsSet());
				if (common.size() > maxSize) {
					if (r.getScore() > maxScore) {
						chosenRule = r;
						maxScore = r.getScore();
						maxSize = common.size();
					}
				}
			}
		}
		if (chosenRule != null && common != null && !common.isEmpty()) {
			utt = inventFromRule();
		}
		return utt;
	}

	private CategoricalSymbols inventFromRule() {
		String utt = chosenRule.head ? chosenRule.getExpression().concat(chosenRule.createForm())
				: chosenRule.createForm().concat(chosenRule.getExpression());
		return new CategoricalSymbols(chosenRule, utt, ProductionMode.INVENTION);
	}

	private CategoricalSymbols constructUtteranceFromRule(List<CategoricalRule> rules) {
		CategoricalSymbols utt = null;
		Comparator<CategoricalRule> comp = Comparator.comparingDouble(CategoricalRule::getScore);
		rules.sort(comp.reversed());
		for (CategoricalRule rule : rules) {
			chosenRule = rule;
			if (rule.getHolistic()) {
				return new CategoricalSymbols(chosenRule, chosenRule.getExpression(), ProductionMode.HOLISTIC_EXISTING);
			} else {
				for (Integer cover : chosenRule.coveredDimensions) {
					List<CategoricalRule> compRules = grammar.getRules().stream().filter(r -> r.getCover() == cover)
							.filter(r -> r.matchesSubPart(distinctiveMeanings, cover)).collect(Collectors.toList());
					if (compRules.isEmpty()) {
						break;
					}
					compRules.sort(comp.reversed());
					chosenRule.coveredRules.put(cover, compRules);
				}
				if (chosenRule.isFullyCovered()) {
					return new CategoricalSymbols(chosenRule, chosenRule.getExpression(), ProductionMode.COMPOSITION);
				}
			}
		}
		return utt;
	}

	public void guess(Symbols u, int nAttempts) {

		if (u == null)
			return;
		if (nAttempts == 0) {
			orderActionsThroughRules(u.getForm());
		}
		chosenAction = chooseNextAction();
	}

	public void orderActionsThroughRules(String utt) {
		Collections.shuffle(possibleActions);
		parseAndOrderPossibleActions(utt);
	}

	private void parseAndOrderPossibleActions(String utterance) {

		List<CategoricalRule> potentialRules = this.grammar.stream().filter(r -> r.firstParse(utterance))
				.collect(Collectors.toList());

		if (potentialRules.isEmpty()) {
			return;
		}
		boolean containsTerminals = potentialRules.stream().anyMatch(r -> r.isTerminal());
		if (!containsTerminals) {
			return;
		}

		Map<Action, List<CompositionCombinations>> tempMap = new HashMap<Action, List<CompositionCombinations>>();
		for (Action action : possibleActions) {
			action.probScore = 0.0;
			tempMap.put(action, new ArrayList<CompositionCombinations>());
		}

		for (CategoricalRule rule : potentialRules) {
			rule.initGame();
			if (rule.secondParse(utterance, potentialRules)) {
				if (rule.getHolistic()) {
					Action action = rule.getCorrespondingAction(possibleActions);
					action.probScore += rule.iScore;
					Set<CategoricalRule> s = new HashSet<CategoricalRule>();
					s.add(rule);
					tempMap.get(action).add(new CompositionCombinations(s, 1.0, rule.iScore));
				} else {
					List<Set<CategoricalRule>> productSets = rule.getCompositionRules();
					for (Set<CategoricalRule> comp : productSets) {
						for (Action action : possibleActions) {
							double cont = 0.0;
							double score = 0.0;
							for (CategoricalRule r : comp) {
								double rCommon = r.expression == null ? 0.0
										: (double) Utils.countCommon(r.getMeaningsSet(), action.getMeanings())
												/ action.getMeanings().size();
								if (rCommon != 0.0) {
									cont += rCommon;
									score += r.iScore;
								}
							}
							if (score != 0.0) {
								// if(cont == 1.0){
								// System.out.println("FULL!!!");
								// }
								score *= cont;
								action.probScore += score;
								tempMap.get(action).add(new CompositionCombinations(comp, cont, score));
							}
						}
					}
				}
			}
		}

		for (Entry<Action, List<CompositionCombinations>> ruleEmployed : tempMap.entrySet()) {
			if (!ruleEmployed.getValue().isEmpty()) {
				ruleEmployed.getValue().sort(actionComparator.reversed());
				this.compositionsEmployed.put(ruleEmployed.getKey(), ruleEmployed.getValue());
			}
		}

		possibleActions.sort(Comparator.comparingDouble(Action::getProbScore).reversed());
	}

	private Action chooseNextAction() {
		assert (this.possibleActions.size() != 0);
		Action ac = this.possibleActions.remove(0);
		this.actionsPerformed.add(ac);
		return ac;
	}

	public Action chooseActionRandomly(Random random) {
		assert (this.possibleActions.size() != 0);
		Action ac = this.possibleActions.remove(random.nextInt(this.possibleActions.size()));
		this.actionsPerformed.add(ac);
		return ac;
	}

	public boolean isChosenActionUnique() {
		if (possibleActions.isEmpty()) {
			return false;
		}
		return possibleActions.get(0).probScore > possibleActions.get(1).probScore;
	}

	public void analyseContext() {
		List<List<CategoryMeaning>> contextAnalysis = new ArrayList<List<CategoryMeaning>>();
		CategoryMeaning left = findInOntologyUnique(Category.DESTINATION, Destination.LEFT);
		CategoryMeaning right = findInOntologyUnique(Category.DESTINATION, Destination.RIGHT);
		for (ContextObject object : cxt.getObjects()) {
			List<CategoryMeaning> meanings = new ArrayList<CategoryMeaning>();
			for (Entry<Category, IsCategorical> entry : object.values.entrySet()) {
				meanings.addAll(this.findInOntology(entry.getKey(), entry.getValue()));
			}
			List<CategoryMeaning> m = new ArrayList<CategoryMeaning>(meanings);
			m.add(left);
			meanings.add(right);
			contextAnalysis.add(new ArrayList<CategoryMeaning>(meanings));
			contextAnalysis.add(new ArrayList<CategoryMeaning>(m));
		}
		assert (contextAnalysis.size() == cxt.getObjects().length * 2);
		for (List<CategoryMeaning> action : contextAnalysis) {
			possibleActions.add(new Action(action));
		}
	}

	public CategoryMeaning findInOntologyUnique(Category cat, IsCategorical value) {
		List<CategoryMeaning> list = findInOntology(cat, value);
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}

	public void adaptLexiconSpeaker(InteractionElements elements) {
		this.chosenRule.updateScore(true);
		if (chosenRule.coveredRules != null) {
			for (List<CategoricalRule> rules : chosenRule.coveredRules.values()) {
				CategoricalRule r = rules.get(0);
				r.updateScore(true);
				if (rules.size() > 1) {
					for (int i = 1; i < rules.size(); i++) {
						r = rules.get(i);
						r.updateScore(false);
					}
				}
			}
		}

	}

	public void adaptLexiconHearer(InteractionElements elements) {
		if (elements.outcome != InteractionOutcome.SUCCESS) {
			return;
		}

		if (compositionsEmployed != null && compositionsEmployed.containsKey(chosenAction)) {
			List<CompositionCombinations> rules = compositionsEmployed.get(chosenAction);
			CompositionCombinations first = rules.remove(0);
			for (CategoricalRule comp : first.rules) {
				this.total = 0.;
				List<CategoricalRule> others = this.grammar.stream().filter(r -> r.cover == comp.cover)
						.filter(r -> r.sameSemantics(comp))
						.filter(r -> r != comp)
						.collect(Collectors.toList());
				comp.updateScore(true);
				for (CategoricalRule other : others) {
					other.updateScore(false);
				}
				this.total = others.stream().mapToDouble(CategoricalRule::getIScore).sum();
				this.total += comp.iScore;
				others.stream().forEach(r -> r.normalise(this.total));
				comp.normalise(this.total);
			}
		} else {
			learnAction(elements);
		}

		List<CategoryMeaning> meanings = (chosenMeanings != null) ? chosenMeanings : this.chosenAction.getMeanings();

		GrammarInducer inducer = new GrammarInducer(meanings, this.ruleCreator, elements.utteranceSpeaker.getForm(),
				this.grammar, this.removeRules);

		try {
			inducer.induce(elements.action, elements.utteranceSpeaker.getForm(), elements.interactionIndex);
			if (this.nInteractions % 20 == 0) {
				CategoricalGeneraliseAndMerger merger = new CategoricalGeneraliseAndMerger(this.grammar);
				merger.generaliseAndMerge();
				// inducer.forgetRules();
			}
		} catch (GrammarException e) {
			System.out.println(e);
			System.exit(-1);
		}

	}

	public void learnAction(InteractionElements elements) {
		if (chosenMeanings == null)
			chosenMeanings = new ArrayList<CategoryMeaning>();
		CategoryMeaning meaning;
		for (IsCategorical value : elements.action.values.values()) {
			meaning = findInOntologyUnique(value.getCategory(), value);
			chosenMeanings.add(meaning);
		}
		this.ruleCreator.createHolisticRule(Utils.calculateCover(chosenMeanings), chosenMeanings,
				elements.utteranceSpeaker.getForm(), elements.interactionIndex);
	}

	public Ontology<CategoryMeaning> getOntology() {
		return ontology;
	}

	public Grammar<CategoricalRule> getGrammar() {
		return grammar;
	}

	public Destination getDestination() {
		return destination;
	}

	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public ContextObject getObject() {
		return object;
	}

	public void setObject(ContextObject object) {
		this.object = object;
	}

	public Context getCxt() {
		return cxt;
	}

	public void setContext(Context cxt) {
		this.cxt = cxt;
	}

	public List<CategoryMeaning> getChosenMeanings() {
		return chosenMeanings;
	}

	public List<CategoryMeaning> getDistinctiveMeanings() {
		return distinctiveMeanings;
	}

	public void setDistinctiveMeanings(List<CategoryMeaning> meanings) {
		this.distinctiveMeanings = meanings;
	}

	public List<Action> getPossibleActions() {
		return possibleActions;
	}

	public void setPossibleActions(List<Action> possibleActions) {
		this.possibleActions = possibleActions;
	}

	public void adoptDistinctiveMeanings(List<CategoryMeaning> meanings) {
		for (CategoryMeaning m : meanings) {
			CategoryMeaning meaning = this.findInOntologyUnique(m.category, m.getValue());
			if (meaning == null) {
				meaning = new CategoryMeaning(m.category, m.getValue());
				ontology.addMeaning(meaning);
			}
			this.distinctiveMeanings.add(meaning);
		}
	}

	public void createPossibleActions(List<Action> possibleActions2) {
		this.possibleActions = new ArrayList<Action>();
		for (Action action : possibleActions2) {
			this.possibleActions.add(new Action(action.getMeanings()));
		}
	}

}
