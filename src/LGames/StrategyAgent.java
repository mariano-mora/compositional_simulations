package LGames;

import java.util.ArrayList;
import java.util.List;

import LGames.ContextStrategic.Destination;
import LGames.InteractionGame.InteractionElements;
import LGames.Strategy.StrategyType;

public class StrategyAgent extends CompositionalAgent2 implements Cloneable {

	private Strategy.StrategyType strategy = null;
	private List<Double> fitnessMemory = new ArrayList<Double>();
	private Double fitness = 0.;
	public List<Double> costMemory = new ArrayList<Double>();
	public int memorySize;
	private double initialValue = 0;
	public CategoricalCognitiveArchitecture architecture = new CategoricalCognitiveArchitecture();

	public StrategyAgent(int n, final int a, final double e, final double s, final boolean[] fv, final int nl,
			final int as, StrategyType stratType, int memorySize) {
		super(n, a, e, s, fv, as);
		this.strategy = stratType;
		this.memorySize = memorySize;
		populateMemory();
	}

	public StrategyAgent(StrategyAgent o){
		this.architecture = new CategoricalCognitiveArchitecture(o.architecture);
		this.strategy = o.strategy;
	}
	
	private void populateMemory() {
		for (int i = 0; i < memorySize; i++) {
			costMemory.add(initialValue);
		}
	}

	public double computeAverage() {
		List<Double> sub = costMemory.subList(costMemory.size() - memorySize, costMemory.size());
		double sum = 0;
		for (double value : sub) {
			sum += value;
		}
		return sum / memorySize;
	}

	public boolean shouldInteract(double coordinationCost, double reward, double sample) {
		if (this.strategy == StrategyType.ALTRUISTIC)
			return true;
		double expectedCost = this.computeAverage() + coordinationCost;
		double prob = (reward - expectedCost) / reward;
		return prob > sample;
	}

	public void categorise() {
		categoriseObjects();
		if (this.role == Role.SPEAKER) {
			categoriseDestination();
			architecture.selectDistinctiveCategories();
		} else {
			categoriseDestinations();
		}
	}

	public void recordCostOfInteraction(double cost) {
		this.costMemory.add(cost);
	}

	public void reward(double d) {
		this.fitness += d;
		this.fitnessMemory.add(this.fitness);
	}

	public void adaptLexiconSpeaker(InteractionElements elements) {
		this.architecture.adaptLexiconSpeaker(elements);
	}

	public void adaptLexiconHearer(InteractionElements elements) {
		architecture.adaptLexiconHearer(elements);
	}

	public void categoriseObjects() {
		architecture.categoriseObjects();
	}

	public void categoriseDestination() {
		architecture.categoriseDestination(architecture.getDestination());
	}

	public void categoriseDestinations() {
		architecture.categoriseDestinations();
	}

	public CategoricalSymbols speak(boolean store, int interaction) {
		return architecture.produceUtterance(store, interaction);
	}
	
	public CategoricalSymbols speak(){
		return architecture.produceUtterance();
	}
	public void guess(Symbols utt, int nAttempts) {
		architecture.guess(utt, nAttempts);
	}

	public void orderActionsThroughRules(String utt) {
		architecture.orderActionsThroughRules(utt);
	}

	public boolean isChosenActionUnique() {
		return architecture.isChosenActionUnique();
	}

	public void analyseContext() {
		architecture.analyseContext();
	}

	public void setContext(Context cxt) {
		this.architecture.setContext(cxt);
	}

	public Strategy.StrategyType getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy.StrategyType strategy) {
		this.strategy = strategy;
	}

	public List<Double> getFitnessMemory() {
		return fitnessMemory;
	}

	public void setDestination(Destination destination) {
		this.architecture.setDestination(destination);

	}

	public void setObject(ContextObject object) {
		this.architecture.setObject(object);
	}

	public CategoricalCognitiveArchitecture getArchitecture() {
		return architecture;
	}

	public void prepareForInteraction() {
		this.architecture.prepareForInteraction(this.role);
	}

	public List<CategoryMeaning> getChosenMeanings() {
		return architecture.getChosenMeanings();
	}

	public void learnAction(InteractionElements elements) {
		architecture.learnAction(elements);
	}

	public List<CategoryMeaning> getDistinctiveMeanings() {
		return architecture.getDistinctiveMeanings();
	}

	public Action getChosenAction() {
		return architecture.chosenAction;
	}

	public void setDistinctiveMeanings(List<CategoryMeaning> meanings) {
		this.architecture.setDistinctiveMeanings(meanings);
	}

	public List<Action> getPossibleActions() {
		return this.architecture.getPossibleActions();
	}

	public void setPossibleActions(List<Action> actions) {
		this.architecture.setPossibleActions(actions);
	}

	public void createPossibleActions(List<Action> possibleActions) {
		this.architecture.createPossibleActions(possibleActions);
	}

	public void resetCognition() {
		this.architecture.resetArchitecture();
	}

	public void adoptDistinctiveMeanings(List<CategoryMeaning> meanings) {
		this.architecture.adoptDistinctiveMeanings(meanings);
	}

	public Grammar<CategoricalRule> getGrammar() {
		return this.architecture.getGrammar();
	}

	public void recordInteraction(){
		this.architecture.nInteractions += 1;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public Double getFitness() {
		return fitness;
	}
	
	public boolean isAltruistic(){
		return this.strategy == StrategyType.ALTRUISTIC;
	}
	
	public boolean isMutualistic(){
		return this.strategy == StrategyType.MUTUALISTIC;
	}
}
