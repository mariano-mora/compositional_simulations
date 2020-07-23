package LGames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import LGames.Agent.Role;
import LGames.Cognition.Category;
import LGames.ContextStrategic.Destination;
import Util.Parameters;

public class InteractionGame {

	public enum InteractionOutcome {
		SUCCESS(0), NO_UTTERANCE(1), HEARER_INTERRUPT(2), FAILURE(3);

		public int value;
		private static Map<Integer, InteractionOutcome> map = new HashMap<Integer, InteractionOutcome>();

		private InteractionOutcome(int value) {
			this.value = value;
		}

		static {
			for (InteractionOutcome outcome : InteractionOutcome.values()) {
				map.put(outcome.value, outcome);
			}
		}

		public static InteractionOutcome valueOf(int outcome) {
			return (InteractionOutcome) map.get(outcome);
		}
	};

	public enum ProductionMode {
		HOLISTIC_CREATION(0), HOLISTIC_EXISTING(1), INVENTION(2), COMPOSITION(3);

		public int value;

		ProductionMode(int v) {
			this.value = v;
		}
	}

	public class InteractionElements {
		protected ContextObject object;
		protected Destination destination;
		protected ContextStrategic cxt;
		protected List<CategoryMeaning> distinctiveMeanings;
		protected CategoricalSymbols utteranceSpeaker = null;
		protected boolean isInteractionFinished = false;
		protected InteractionOutcome outcome;
		protected int attempts = 0;
		protected Action action;
		public int interactionIndex;

		public InteractionElements(ContextStrategic cxt, ContextObject topic, Destination dest, int interaction) {
			this.cxt = cxt;
			this.object = topic;
			this.destination = dest;
			this.action = new Action(topic, dest);
			this.interactionIndex = interaction;
		}
	}

	private static final int MAX_ATTEMPTS = 8;

	private Umpire umpire;
	private Population population;
	private Parameters parameters;
	private StrategyAgent speaker;
	private StrategyAgent hearer;
	private InteractionElements elements;
	protected final double noiseCxt = 0.0;
	private Random random;
	private boolean reduced = false;
	private int samplingInterval = 50;
	private List<Action> possibleActions;

	public InteractionGame(Umpire umpire, Population population, Parameters parameters, boolean reduced) {
		this.umpire = umpire;
		this.population = population;
		this.parameters = parameters;
		long seed = (long) (umpire.actionCost + umpire.coordinationCost + umpire.testNumber + population.size());
		random = new Random(seed);
		this.reduced = reduced;
		this.possibleActions = getAllPossibleActions();
	}

	private void selectAgents() {
		int s = random.nextInt(population.size());
		int h = s;
		while (h == s) {
			h = random.nextInt(population.size());
		}
		this.speaker = (StrategyAgent) population.get(s);
		this.hearer = (StrategyAgent) population.get(h);
		this.speaker.recordInteraction();
		this.hearer.recordInteraction();
		this.speaker.setRole(Role.SPEAKER);
		this.hearer.setRole(Role.HEARER);
	}

	private ContextStrategic createContext(Random random) {
		ContextStrategic cxt = this.reduced
				? new ReducedContextStrategic(parameters.getMaxCxtSize(), parameters.getFeatures(), random)
				: new ContextStrategic(parameters.getMaxCxtSize(), parameters.getFeatures(), this.random);
		cxt.buildContext();
		return cxt;
	}

	private ContextObject selectObject(ContextStrategic cxt) {
		return cxt.getObjects()[random.nextInt(cxt.cxtSize)];
	}

	private Destination selectDestination() {
		return Destination.values()[random.nextInt(Destination.values().length)];
	}

	private InteractionElements initialiseElements(int index) {
		ContextStrategic cxt = createContext(this.random);
		return new InteractionElements(cxt, selectObject(cxt), selectDestination(), index);
	}

	public void onInteractionStart(int interactionIndex) {
		selectAgents();
		this.umpire.recordInteractionAgents(speaker.getID(), hearer.getID());
		this.elements = initialiseElements(interactionIndex);
		this.speaker.prepareForInteraction();
		this.speaker.setContext(elements.cxt);
		this.speaker.setObject(elements.object);
		this.speaker.setDestination(elements.destination);
		this.speaker.categorise();
		this.elements.distinctiveMeanings = speaker.getDistinctiveMeanings();
		this.elements.utteranceSpeaker = speaker.speak(true, interactionIndex);
		this.hearer.setContext(elements.cxt);
		this.hearer.prepareForInteraction();
	}

	public void onInteractionEnd() {
		int mode = elements.utteranceSpeaker.mode.value;
		this.umpire.recordInteractionHistory(elements.outcome, elements.attempts, mode);
		double coordCost = umpire.coordinationCost * elements.attempts;
		double totalCost = umpire.actionCost + coordCost;
		if (elements.outcome == InteractionOutcome.SUCCESS) {
			totalCost /= 2;
			this.speaker.recordCostOfInteraction(totalCost);
			this.hearer.recordCostOfInteraction(totalCost);
			this.speaker.reward(umpire.reward - totalCost);
			this.hearer.reward(umpire.reward - totalCost);
			updateScores();
		} else {
			this.speaker.recordCostOfInteraction(totalCost);
			this.speaker.reward(umpire.reward - totalCost);
			this.hearer.recordCostOfInteraction(coordCost);
			this.hearer.reward(-(coordCost));
		}
	}

	private void reactToNoUtterance() {
		elements.outcome = InteractionOutcome.NO_UTTERANCE;
		elements.attempts = 0;
		elements.isInteractionFinished = true;
	}

	private void updateScores() {
//		speaker.adaptLexiconSpeaker(elements);
		hearer.adaptLexiconHearer(elements);
	}

	public void runInteraction(int interactionIndex) {
		if (interactionIndex % this.samplingInterval == 0 && interactionIndex > 1) {
			try {
				this.umpire.computeStatisticMeasures(this.population, this.possibleActions, interactionIndex);
				this.umpire.computeAltruisticRatio(this.population);
				this.umpire.storeAgentStrategies(this.population);
				this.umpire.computeFitnessMeans(this.population);
			} catch (GrammarException e) {
				System.out.println(e.getMessage());
				System.exit(-1);
			}
		}		
		onInteractionStart(interactionIndex);

		if (elements.utteranceSpeaker == null) {
			reactToNoUtterance();
		}

		while (!elements.isInteractionFinished) {
			if (hearer.shouldInteract(umpire.computeCoordinationCost(elements.attempts), umpire.reward,
					umpire.random.nextDouble())) {
				if (elements.attempts == 0) {
					hearer.categorise();
					hearer.analyseContext();
				}
				hearer.guess(elements.utteranceSpeaker, elements.attempts++);
				if (checkInteractionSuccess(hearer.getChosenAction())) {
					elements.isInteractionFinished = true;
					elements.outcome = InteractionOutcome.SUCCESS;
				} else {
					if (elements.attempts == MAX_ATTEMPTS) {
						elements.isInteractionFinished = true;
						elements.outcome = InteractionOutcome.FAILURE;
					}
				}
			} else {
				elements.isInteractionFinished = true;
				elements.outcome = InteractionOutcome.HEARER_INTERRUPT;
			}
		}
		onInteractionEnd();
	}

	private boolean checkInteractionSuccess(Action chosenAction) {
		if (chosenAction == null) {
			return false;
		}
		return this.elements.action.equals(chosenAction);
	}

	private List<Action> getAllPossibleActions() {
		List<Action> actions = new ArrayList<Action>();
		List<List<CategoryMeaning>> contextAnalysis = new ArrayList<List<CategoryMeaning>>();
		Context cxt = createContext(this.random);
		CategoryMeaning left = new CategoryMeaning(Category.DESTINATION, Destination.LEFT);
		CategoryMeaning right = new CategoryMeaning(Category.DESTINATION, Destination.RIGHT);
		for (ContextObject object : cxt.getObjects()) {
			List<CategoryMeaning> meanings = new ArrayList<CategoryMeaning>();
			for (Entry<Category, IsCategorical> entry : object.values.entrySet()) {
				meanings.add(new CategoryMeaning(entry.getKey(), entry.getValue()));
			}
			List<CategoryMeaning> m = new ArrayList<CategoryMeaning>(meanings);
			m.add(left);
			meanings.add(right);
			contextAnalysis.add(new ArrayList<CategoryMeaning>(meanings));
			contextAnalysis.add(new ArrayList<CategoryMeaning>(m));
		}
		assert (contextAnalysis.size() == cxt.getObjects().length * 2);
		for (List<CategoryMeaning> action : contextAnalysis) {
			actions.add(new Action(action));
		}

		return actions;
	}

	public Random getRandom() {
		return this.random;
	}

	public InteractionElements getElements() {
		return elements;
	}

	public void setElements(InteractionElements elements) {
		this.elements = elements;
	}

	public boolean isReduced() {
		return this.reduced;
	}
}
