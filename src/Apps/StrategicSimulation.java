package Apps;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import LGames.GrammarReader;
import LGames.InteractionGame;
import LGames.Population;
import LGames.StrategyAgent;
import LGames.Umpire;
import Util.Parameters;
import Util.Utils;

public class StrategicSimulation {

	protected static Parameters parameters = new Parameters();
	private static int lastID = 0;
	private static InteractionGame game;
	private static int numberTests = 1;

	/**
	 * Initialise a list of agents
	 */
	public static List<StrategyAgent> createAgents() {
		List<StrategyAgent> agents = new ArrayList<StrategyAgent>();
		for (int i = 0; i < parameters.getNAgents(); i++) {
			StrategyAgent agent = new StrategyAgent(lastID, 0, parameters.getEtaN(), parameters.getEtaS(),
					parameters.getFeatures(), parameters.getNNoLearning(), parameters.getAlphabetSize(),
					parameters.getStrategyType(), parameters.getMemorySize());
			agent.architecture.ontology = Utils.getSingletonOntology();
			agents.add(agent);
			lastID++;
		}
		return agents;
	}

	private static void storeUmpireAndPopulation(Umpire umpire, Population population, int testNumber,
			int interaction) {
		double actionRate = umpire.actionCost / umpire.reward;
		double coordRate = umpire.coordinationCost / umpire.actionCost;
		String dirName = Utils.buildDirectoryName(parameters.getStoreDir(), parameters.getStrategyType(), actionRate,
				coordRate, testNumber);
		File directory = new File(dirName);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		Utils.storeUmpire(umpire, dirName, interaction);
		Utils.storeAgents(population, dirName, interaction);
		Utils.storeSyntaxMap(dirName);
	}

	public static void main(final String[] pArgs) throws IOException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		Duration d;

		boolean reduced = true;
		parameters.parseParameters(pArgs);
		int firstTest = parameters.getTestNumber();
		LocalDateTime begin = LocalDateTime.now();
		LocalDateTime old = LocalDateTime.now();

		for (int testNumber = firstTest; testNumber <= numberTests; testNumber++) {
			System.out.println("Performing test " + testNumber);
			LocalDateTime now = LocalDateTime.now();
			System.out.println(dtf.format(now));
			d = Duration.between(old, now);
			long seconds = d.getSeconds();
			System.out.println(String.format("That took %02d:%02d minutes", seconds / 60, seconds % 60));
			old = now;

			lastID = 0;
			int firstRun = parameters.getFirstRun();
			List<StrategyAgent> agents = createAgents();
			Population population = new Population(agents);
			if (firstRun != 0) {
				double actionRate = parameters.getActionCostRate();
				double coordRate = parameters.getCoordinationCostRate();
				String dirName = Utils.buildDirectoryName(parameters.getStoreDir(), parameters.getStrategyType(),
						actionRate, coordRate, testNumber);
				GrammarReader reader = new GrammarReader(dirName, population);
				reader.parseSyntaxKeyMap();
				reader.parseFile();
			}

			Umpire umpire = new Umpire(parameters.getReward(), parameters.getActionCostRate(),
					parameters.getCoordinationCostRate(), testNumber, population);
			game = new InteractionGame(umpire, population, parameters, reduced);

			for (int interaction = firstRun; interaction < parameters.getNInteractions(); interaction++) {
				if (interaction % 1000 == 0 && interaction > firstRun) {
					storeUmpireAndPopulation(umpire, population, testNumber, interaction);
					System.out.println(String.format("Writing after interaction %d, for coord rate %.2f", interaction,
							parameters.getCoordinationCostRate()));
				}
				game.runInteraction(interaction);
			}

			storeUmpireAndPopulation(umpire, population, testNumber, parameters.getNInteractions());
		}
		d = Duration.between(begin, LocalDateTime.now());
		long seconds = d.getSeconds();
		System.out.println(String.format("The whole thing took %02d:%02d minutes", seconds / 60, seconds % 60));
		System.exit(0);
	}

}
