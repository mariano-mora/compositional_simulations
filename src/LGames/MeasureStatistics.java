package LGames;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import LGames.InteractionGame.ProductionMode;
import Util.Utils;

public class MeasureStatistics {

	public class StatsBookKeeping {
		public int interactionIndex;
		public int speakerID;
		public int listenerID;
		public List<CategoricalRule> speakerRules;
		public Set<CategoricalRule> listenerRules;
		public Action action;
		public int actionNumber;
		public ProductionMode mode;

		public StatsBookKeeping(int index, int sID, int lID, List<CategoricalRule> sRules, Set<CategoricalRule> lRules,
				Action action, int actionNumber, ProductionMode mode) {
			this.interactionIndex = index;
			this.speakerID = sID;
			this.listenerID = lID;
			this.speakerRules = sRules;
			this.listenerRules = lRules;
			this.action = action;
			this.actionNumber = actionNumber;
			this.mode = mode;
		}

		public String toString() {
			return new String(interactionIndex + " " + speakerID + " " + listenerID + " " + action + " " + speakerRules
					+ listenerRules);
		}

		@Override
		public boolean equals(Object other) {
			StatsBookKeeping o = (StatsBookKeeping) other;
			if (this.speakerID != o.speakerID || this.listenerID != o.listenerID) {
				return false;
			}
			if (!this.action.equals(o.action) || this.mode != o.mode) {
				return false;
			}
			return (this.speakerRules.equals(o.speakerRules) && this.listenerRules.equals(o.listenerRules));
		}

		public String toCSV() {
			// List<String> sRules = this.speakerRules.stream().map(r ->
			// Integer.toString(r.getNumber()))
			// .collect(Collectors.toList());
			// List<String> lRules = this.listenerRules.stream().map(r ->
			// Integer.toString(r.getNumber()))
			// .collect(Collectors.toList());
			int isCompositional = listenerRules.size() == 1 ? 0 : 1;
			List<String> params = Arrays.asList(Integer.toString(interactionIndex), Integer.toString(actionNumber),
					Integer.toString(speakerID), Integer.toString(listenerID), Integer.toString(mode.value),
					Integer.toString(isCompositional));

			return String.join(Utils.tab_delimiter, params);
		}
	}

	public List<Double> accuracy;
	public List<Double> weakAccuracy;
	public List<Double> avgExpressivity;
	public List<List<Double>> agentsExpressivity;
	private double nPossiblePairs;
	private double weakPossiblePairs;
	public List<List<StatsBookKeeping>> bookkeeping;
	public List<List<StatsBookKeeping>> differences;
	public List<List<StatsBookKeeping>> newStats;
	public static String BOOK_FIELDS = String.join(Utils.tab_delimiter,
			Arrays.asList("interaction", "action", "s_id", "h_id", "mode", "compositional"));
	public static String STATS_FIELDS = String.join(Utils.tab_delimiter,
			Arrays.asList("avgExpres", "accuracy"));

	public MeasureStatistics(int nPossiblePairs) {
		this.accuracy = new ArrayList<Double>();
//		this.weakAccuracy = new ArrayList<Double>();
		this.avgExpressivity = new ArrayList<Double>();
		this.agentsExpressivity = new ArrayList<List<Double>>();
		this.nPossiblePairs = (double) nPossiblePairs;
		this.weakPossiblePairs = this.nPossiblePairs * 2;
		this.bookkeeping = new ArrayList<List<StatsBookKeeping>>();
		this.differences = new ArrayList<List<StatsBookKeeping>>();
		this.newStats = new ArrayList<List<StatsBookKeeping>>();
		
	}

	public void measureAccuracy(Population population, List<Action> possibleActions, int interactionIndex)
			throws GrammarException {
		List<Map<Action, List<Integer>>> coincides = new ArrayList<Map<Action, List<Integer>>>();
		List<Double> expressAgents = new ArrayList<Double>();
		List<StatsBookKeeping> keeping = new ArrayList<StatsBookKeeping>();
		double express = 0.;
		for (StrategyAgent speaker : population) {
			express = 0.;
			Map<Action, List<Integer>> agentCoincides = new HashMap<Action, List<Integer>>();
			for (Action action : possibleActions) {
				speaker.resetCognition();
				speaker.adoptDistinctiveMeanings(action.getMeanings());
				CategoricalSymbols utt = speaker.speak();
				if (utt.mode == ProductionMode.COMPOSITION) {
					express += 1. / possibleActions.size();
				}
				List<Integer> others = new ArrayList<Integer>();
				for (StrategyAgent hearer : population) {
					if (speaker == hearer) {
						continue;
					}
					hearer.resetCognition();
					hearer.createPossibleActions(possibleActions);
					hearer.orderActionsThroughRules(utt.getForm());
					if (hearer.getPossibleActions().get(0).equals(action) && hearer.isChosenActionUnique()) {
						Set<CategoricalRule> listRules = hearer.architecture.compositionsEmployed.get(action)
								.get(0).rules;

						keeping.add(new StatsBookKeeping(interactionIndex, speaker.getID(), hearer.getID(),
								getAgentRules(speaker), listRules, action, possibleActions.indexOf(action), utt.mode));
						others.add(hearer.getID());
					}
				}
				agentCoincides.put(action, others);
			}
			coincides.add(agentCoincides);
			expressAgents.add(express);
		}

		double count = 0.;
		for (Map<Action, List<Integer>> speakerMap : coincides) {
			for (Entry<Action, List<Integer>> entry : speakerMap.entrySet()) {
				Action ac = entry.getKey();
				if (!entry.getValue().isEmpty()) {
					int speaker = coincides.indexOf(speakerMap);
					for (Integer hearer : entry.getValue()) {
						List<Integer> hearerList = coincides.get(hearer).get(ac);
						if (hearerList.contains(speaker)) {
							count += 1;
						}
					}
				}
			}
		}

		List<StatsBookKeeping> previous = bookkeeping.size() > 1 ? bookkeeping.get(bookkeeping.size() - 1) : null;
		List<StatsBookKeeping> different = previous != null
				? previous.stream().filter(s -> !keeping.contains(s)).collect(Collectors.toList()) : null;
		List<StatsBookKeeping> newStat = previous != null
				? keeping.stream().filter(b -> !previous.contains(b)).collect(Collectors.toList()) : null;
		this.bookkeeping.add(keeping);
		if (different != null) {
			this.differences.add(different);
			this.newStats.add(newStat);
		}
//		double weakAcc = ((double) keeping.size() / weakPossiblePairs) / possibleActions.size();
//		weakAccuracy.add(weakAcc);
		count /= 2;
		double accuracy = (count / nPossiblePairs) / possibleActions.size();
		this.accuracy.add(accuracy);
		double expressivity = 0.;
		agentsExpressivity.add(expressAgents);
		for (Double ex : expressAgents) {
			expressivity += ex;
		}
		expressivity /= population.size();
		this.avgExpressivity.add(expressivity);

	}



	private boolean hearerAndSpeakerCoincides(StrategyAgent speaker, StrategyAgent hearer, Action action,
			List<Map<Action, List<Integer>>> coincides) {

		int hearerId = hearer.getID();
		if (coincides.size() <= hearerId) {
			return false;
		}
		Map<Action, List<Integer>> hearersMap = coincides.get(hearerId);
		if (!hearersMap.containsKey(action)) {
			return false;
		}
		List<Integer> hearersCoins = hearersMap.get(action);
		if (!hearersCoins.contains(speaker.getID())) {
			return false;
		}
		return true;
	}

	private List<CategoricalRule> getAgentRules(StrategyAgent agent) {
		List<CategoricalRule> sRules = new ArrayList<CategoricalRule>();
		sRules.add(agent.getArchitecture().chosenRule);
		if (agent.getArchitecture().chosenRule.coveredRules != null) {
			for (List<CategoricalRule> rulz : agent.getArchitecture().chosenRule.coveredRules.values()) {
				sRules.addAll(rulz);
			}
		}
		return sRules;
	}

}
