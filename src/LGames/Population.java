package LGames;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import LGames.Grammar.RuleChange;
import Util.DataCheckException;
import Util.Utils;

public class Population implements Storable, Iterable<StrategyAgent> {

	private List<StrategyAgent> agents;
	private static String FILE_NAME = "agents.csv";
	private static String GRAMMARS_FILE_NAME = "grammars.txt";
	private static String RULE_CHANGES_FILE_NAME = "rule_changes.txt";

	public Population(List<StrategyAgent> agents) {
		this.agents = agents;
	}

	@Override
	public void store(String dirName) throws DataCheckException, IOException {
		// TODO Auto-generated method stub

	}

	private int completeLengths() {
		int maxSize = agents.stream().mapToInt(agent -> agent.getFitnessMemory().size()).max().getAsInt();
		for (StrategyAgent agent : agents) {
			int size = agent.getFitnessMemory().size();
			double value = agent.getFitnessMemory().get(size - 1);
			for (int i = size; i < maxSize; i++) {
				agent.getFitnessMemory().add(value);
			}
		}
		return maxSize;
	}

	private String convertToRow(int index) {
		List<String> values = new ArrayList<String>();
		for (StrategyAgent agent : agents) {
			values.add(Double.toString(agent.getFitnessMemory().get(index)));
		}
		return String.join(Utils.tab_delimiter, values);

	}

	private void storeFitness(String dirName) throws IOException {
		int maxSize = completeLengths();
		File file = new File(dirName + "/" + FILE_NAME);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		List<String> columns = new ArrayList<String>();
		for (Agent agent : agents) {
			columns.add(Integer.toString(agent.id));
		}
		bw.write(String.join(Utils.tab_delimiter, columns));

		for (int i = 0; i < maxSize; i++) {
			bw.newLine();
			bw.write(convertToRow(i));
		}
		bw.close();
	}

	private void storeGrammars(String dirName, int interaction) throws IOException, GrammarException {
		File file = new File(dirName + "/" + GRAMMARS_FILE_NAME);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(Integer.toString(interaction));
		bw.newLine();
		for (StrategyAgent agent : agents) {
			bw.newLine();
			String agentData = new String("AGENT " + Integer.toString(agent.getID() + 1) + ":"
					+ Integer.toString(agent.memorySize) + ":" + Double.toString(agent.computeAverage()) + ":"
					+ Integer.toString(agent.architecture.nInteractions));
			bw.write(agentData);
			Grammar<CategoricalRule> gram = agent.getGrammar();
			Collections.sort(gram.getRules());
			for (CategoricalRule rule : gram) {
				bw.newLine();
				bw.write(rule.toString());
			}
		}
		bw.close();
		fos.close();
	}

	public void storeRuleChanges(String dirName) throws IOException, GrammarException {
		File file = new File(dirName + "/" + RULE_CHANGES_FILE_NAME);
		FileOutputStream fos = new FileOutputStream(file);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		for (StrategyAgent agent : agents) {
			bw.newLine();
			String agentData = new String("AGENT " + Integer.toString(agent.getID() + 1));
			bw.write(agentData);
			Grammar<CategoricalRule> gram = agent.getGrammar();
			List<Grammar<CategoricalRule>.RuleChange> changes = gram.getChangedRules();
			for (RuleChange change : changes) {
				bw.newLine();
				bw.write(change.toString());
			}
		}
		bw.close();
		fos.close();

	}
	
	public Stream<StrategyAgent> stream(){
		return agents.stream();
	}
	
	@Override
	public void storeAsCSV(String dirName, int interaction) throws DataCheckException, IOException, GrammarException {
		storeFitness(dirName);
		storeGrammars(dirName, interaction);
	}

	@Override
	public Iterator<StrategyAgent> iterator() {
		return agents.iterator();
	}

	public StrategyAgent next() {
		return agents.iterator().next();
	}

	public Agent get(int index) {
		return agents.get(index);
	}

	public int size() {
		return this.agents.size();
	}

	public List<StrategyAgent> getAgents() {
		return this.agents;
	}
}
