package LGames;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import LGames.MeasureStatistics.StatsBookKeeping;
import Util.DataCheckException;
import Util.Utils;

public class Umpire implements Storable {

	public List<InteractionPair> interactingAgents = new ArrayList<InteractionPair>();
	public List<InteractionGame.InteractionOutcome> interactionHistory = new ArrayList<InteractionGame.InteractionOutcome>();
	public List<Integer> attemptsPerInteraction = new ArrayList<Integer>();
	public List<Integer> productionModes = new ArrayList<Integer>();
	public List<Integer> altruisticRatios = new ArrayList<Integer>();
	public List<Double> altruistMeanFitness = new ArrayList<Double>();
	public List<Double> mutMeanFitness = new ArrayList<Double>();
	public List<Double> meanFitness = new ArrayList<Double>();
	public List<List<Integer>> altruistAgents = new ArrayList<List<Integer>>();
	public MeasureStatistics stats;
	public Population population;
	public Random random = null;
	public double reward;
	public double actionCost;
	public double coordinationCost;
	public int testNumber;
	private static List<String> FITNESS_FIELDS = Arrays.asList("mean_fitness", "alt_mean_fitness", "mut_mean_fitness");
	private static String FILE_NAME = "umpire.csv";
	private static String STATS_FILE_NAME = "stats.csv";
	private static String RATIOS_FILE_NAME = "ratios.csv";
	private static String ALTRUISTS_FILE_NAME = "altruists.csv";
	private static String FITNESS_FILE_NAME = "fitness.csv";
	private static String STATS_BOOKEEPING_FILE_NAME_CSV = "stats_bookeeping.csv";
	private static String STATS_BOOKEEPING_FILE_NAME = "stats_bookeeping.txt";
	private static String STATS_DIFFERENCES_FILE_NAME = "stats_differences.txt";
	private static String STATS_NEW_FILE_NAME = "stats_new.txt";

	public class InteractionPair {
		private int speakerId;
		private int hearerId;

		public InteractionPair(int s, int h) {
			this.speakerId = s;
			this.hearerId = h;
		}

		public int getSpeakerId() {
			return speakerId;
		}

		public int getHearerId() {
			return hearerId;
		}
	}

	public Umpire(double reward, double actionCostRate, double coordinationCostRate, int testNumber,
			Population population) {
		this.reward = reward;
		this.actionCost = reward * actionCostRate;
		this.coordinationCost = this.actionCost * coordinationCostRate;
		this.testNumber = testNumber;
		long seed = (long) (actionCost + coordinationCost + this.testNumber);
		this.random = new Random(seed);
		this.population = population;
		int nPossiblePairs = Utils.computeNumberOfPossiblePairs(population.size());
		this.stats = new MeasureStatistics(nPossiblePairs);
		

	}

	public void recordInteractionAgents(int speakerId, int hearerId) {
		interactingAgents.add(new InteractionPair(speakerId, hearerId));
	}

	public void recordInteractionHistory(InteractionGame.InteractionOutcome outcome, int nAttempts, int mode) {
		interactionHistory.add(outcome);
		attemptsPerInteraction.add(nAttempts);
		productionModes.add(mode);
	}

	public double computeCoordinationCost(int attemptNumber) {
		return this.coordinationCost * attemptNumber;
	}

	public void computeStatisticMeasures(Population population, List<Action> possibleActions, int interactionIndex) throws GrammarException {
		this.stats.measureAccuracy(population, possibleActions, interactionIndex);
	}
	
	public void computeAltruisticRatio(Population population){
		int ratio = (int) population.stream().filter(StrategyAgent::isAltruistic).count();
		this.altruisticRatios.add(ratio);
	}
	
	public void computeFitnessMeans(Population population){
		List<StrategyAgent> altAgents = population.stream().filter(StrategyAgent::isAltruistic).collect(Collectors.toList());
		List<StrategyAgent> mutAgents = population.stream().filter(StrategyAgent::isMutualistic).collect(Collectors.toList());
		double altFitness = altAgents.stream().mapToDouble(i -> i.getFitness()).sum()/altAgents.size();
		double mutFitness = mutAgents.stream().mapToDouble(i -> i.getFitness()).sum()/mutAgents.size();
		double meanFitness = population.stream().mapToDouble(i -> i.getFitness()).sum()/population.size();
		this.altruistMeanFitness.add(altFitness);
		this.mutMeanFitness.add(mutFitness);
		this.meanFitness.add(meanFitness);
	}

	public void storeAgentStrategies(Population population) {
		List<Integer> altruists = new ArrayList<Integer>();
		for (StrategyAgent agent : population){
			int alt = agent.isAltruistic() ? 1 : 0;
			altruists.add(alt);
		}
		this.altruistAgents.add(altruists);
	}
	
	private String convertToRow(int index) {
		return String.join(Utils.tab_delimiter,
				Arrays.asList(Integer.toString(interactingAgents.get(index).getSpeakerId()),
						Integer.toString(interactingAgents.get(index).getHearerId()),
						Integer.toString(interactionHistory.get(index).value),
						Integer.toString(attemptsPerInteraction.get(index)),
						Integer.toString(productionModes.get(index))));
	}
	
	private String convertFitnessToRow(int index){
		return String.join(Utils.tab_delimiter, Arrays.asList(Double.toString(this.meanFitness.get(index)),
				Double.toString(this.altruistMeanFitness.get(index)),
				Double.toString(this.mutMeanFitness.get(index))));
	}

	private String convertStatsToRow(int index) {
		String agentExp = String.join(Utils.tab_delimiter, this.stats.agentsExpressivity.get(index).stream()
				.map(exp -> Utils.formatStat(exp)).collect(Collectors.toList()));
		StringBuilder builder = new StringBuilder(agentExp);
		builder.append(Utils.tab_delimiter);
		String average = String.join(Utils.tab_delimiter,
				Arrays.asList(Utils.formatStat(stats.avgExpressivity.get(index)),
						Utils.formatStat(stats.accuracy.get(index))));
		builder.append(average);
		return builder.toString();
	}

	private boolean checkLengths() {
		if (interactingAgents.size() != interactionHistory.size()
				|| interactingAgents.size() != attemptsPerInteraction.size()) {
			return false;
		}
		return (stats.accuracy.size() == stats.avgExpressivity.size());
	}

	@Override
	public void store(String dirName) throws DataCheckException, IOException {
		// TODO
	}

	@Override
	public void storeAsCSV(String dirName, int interaction) throws DataCheckException, IOException {
		if (!checkLengths()) {
			throw new DataCheckException("Umpire lists are not of same length!");
		}
		storeInteractions(dirName, interaction);
		storeStatistics(dirName, interaction);
		storeAltruistIds(dirName, interaction);
		storeFitnesses(dirName, interaction);
		storeRatios(dirName, interaction);
	}

	private void storeStatKeeping(String dirName) throws IOException{
		File file = new File(dirName + "/" + STATS_BOOKEEPING_FILE_NAME_CSV);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(MeasureStatistics.BOOK_FIELDS);
		for(int i = 0; i<stats.bookkeeping.size(); i++){
			List<StatsBookKeeping> books = stats.bookkeeping.get(i);
			for (StatsBookKeeping stat : books){
				bw.newLine();
				bw.write(stat.toCSV());
			}
		}
		bw.close();

	}
	
	private void storeStatArrays(List<List<StatsBookKeeping>> arr, String fn, String dirName) throws IOException{
		File file = new File(dirName + "/" + fn);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.newLine();
		for(int i = 0; i<arr.size(); i++){
			List<StatsBookKeeping> stats = arr.get(i);
			for (StatsBookKeeping stat : stats){
				bw.newLine();
				bw.write(stat.toString());
			}
			bw.newLine();
			bw.newLine();
		}
		bw.close();

	}
	
	private void storeStatChanges(String dirName) throws IOException{
		storeStatKeeping(dirName);
		storeStatArrays(stats.bookkeeping, STATS_BOOKEEPING_FILE_NAME, dirName);
		storeStatArrays(stats.differences, STATS_DIFFERENCES_FILE_NAME, dirName);
		storeStatArrays(stats.newStats, STATS_NEW_FILE_NAME, dirName);
	}
	
	private void storeStatistics(String dirName, int interaction) throws IOException {
		File file = new File(dirName + "/" + STATS_FILE_NAME);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(Integer.toString(interaction));
		bw.newLine();
		this.population.getAgents().stream().map(a -> a.getID()+1).collect(Collectors.toList());
		StringBuilder ids = new StringBuilder(String.join(Utils.tab_delimiter, this.population.getAgents().stream()
				.map(a -> Integer.toString(a.getID())).collect(Collectors.toList())));
		ids.append(Utils.tab_delimiter);
		ids.append(MeasureStatistics.STATS_FIELDS);
		bw.write(ids.toString());
		for (int i = 0; i < stats.accuracy.size(); i++) {
			bw.newLine();
			bw.write(convertStatsToRow(i));
		}
		bw.close();
		fos.close();
//		storeStatChanges(dirName);
	}

	private void storeInteractions(String dirName, int interaction) throws IOException {
		File file = new File(dirName + "/" + FILE_NAME);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(Integer.toString(interaction));
		bw.newLine();
		bw.write(String.join(Utils.tab_delimiter,
				Arrays.asList("SpeakerID", "HearerID", "Outcome", "Attempts", "Mode")));
		for (int i = 0; i < this.altruistAgents.size(); i++) {
			bw.newLine();
			bw.write(convertToRow(i));
		}
		bw.close();
		fos.close();
	}
	
	private void storeRatios(String dirName, int interaction) throws IOException {
		File file = new File(dirName + "/" + RATIOS_FILE_NAME);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(Integer.toString(interaction));
		bw.newLine();
		for (Integer ratio : altruisticRatios){
			bw.newLine();
			bw.write(Integer.toString(ratio));
		}
		bw.close();
		fos.close();
	}
	
	private void storeAltruistIds(String dirName, int interaction) throws IOException {
		File file = new File(dirName + "/" + ALTRUISTS_FILE_NAME);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(Integer.toString(interaction));
		bw.newLine();
		StringBuilder ids = new StringBuilder(String.join(Utils.tab_delimiter, this.population.getAgents().stream()
				.map(a -> Integer.toString(a.getID())).collect(Collectors.toList())));
		bw.write(ids.toString());
		for (List<Integer> alts : this.altruistAgents) {
			bw.newLine();
			List<String> altStrings = alts.stream().map(Object::toString)
                    .collect(Collectors.toList());
			String line = String.join(Utils.tab_delimiter,altStrings);
			bw.write(line);
		}
		bw.close();
		fos.close();
	}

	public void storeFitnesses(String dirName, int interaction) throws IOException{
		File file = new File(dirName + "/" + FITNESS_FILE_NAME);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(Integer.toString(interaction));
		bw.newLine();
		StringBuilder fields = new StringBuilder(String.join(Utils.tab_delimiter, FITNESS_FIELDS));
		bw.write(fields.toString());
		for (int i = 0; i < this.meanFitness.size(); i++) {
			bw.newLine();
			bw.write(convertFitnessToRow(i));
		}
		bw.close();
		fos.close();

		
	}
	
	public boolean isGameFinished(int nAgents) {
		boolean fixated = altruisticRatios.get(altruisticRatios.size()-1) == nAgents;
		List<Double> acc = stats.accuracy;
		boolean consis = acc.get(acc.size()-1) == 1.0;
		return fixated && consis;
	}

}
