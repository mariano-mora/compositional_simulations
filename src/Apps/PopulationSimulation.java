package Apps;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import LGames.GrammarException;
import LGames.GrammarReader;
import LGames.InteractionGame;
import LGames.Population;
import LGames.Strategy.StrategyType;
import LGames.StrategyAgent;
import LGames.Umpire;
import Util.Parameters;
import Util.Utils;

public class PopulationSimulation {

	protected static Parameters parameters = new Parameters();
	private static int lastID = 0;
	private static InteractionGame game;
	private static int numberTests = 1;
	private static int imitateRate = 50;
	private static double rateImitators = 0.1; 

	private final static Logger LOGGER = Logger.getLogger(PopulationSimulation.class.getName());
    private static FileHandler fh;  

    private static void initLogging(){
    	LOGGER.setLevel(Level.INFO);
        try {  
        	String dirName = Utils.buildPopulationDirectoryNameAction(parameters.getStoreDir(), parameters.getActionCostRate());
    		File directory = new File(dirName);
    		if (!directory.exists()) {
    			directory.mkdirs();
    		}

    		String fileName = dirName.concat("simulation.log");
//    		File logFile = new File(fileName);
//    		if (!logFile.exists()){
//    			logFile.createNewFile();
//    			System.out.println("LOG FILE CREATED");
//    		}  
            fh = new FileHandler(fileName, true);  
            LOGGER.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();  
            fh.setFormatter(formatter);  
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  

    }
	/**
	 * Initialise a list of agents
	 */
	public static List<StrategyAgent> createAgents() {
		double ratioAlt = parameters.getRatioAltruistic();
		int numberAlt = (int)Math.floor(parameters.getNAgents() * ratioAlt);
		
		List<StrategyAgent> agents = new ArrayList<StrategyAgent>();
		for (int i = 0; i < numberAlt; i++) {
			StrategyAgent agent = new StrategyAgent(lastID, 0, parameters.getEtaN(), parameters.getEtaS(),
					parameters.getFeatures(), parameters.getNNoLearning(), parameters.getAlphabetSize(),
					StrategyType.ALTRUISTIC, parameters.getMemorySize());
			agent.architecture.ontology = Utils.getSingletonOntology();
			agents.add(agent);
			lastID++;
		}
		for (int i = numberAlt; i < parameters.getNAgents(); i++){
			StrategyAgent agent = new StrategyAgent(lastID, 0, parameters.getEtaN(), parameters.getEtaS(),
					parameters.getFeatures(), parameters.getNNoLearning(), parameters.getAlphabetSize(),
					StrategyType.MUTUALISTIC, parameters.getMemorySize());
			agent.architecture.ontology = Utils.getSingletonOntology();
			agents.add(agent);
			lastID++;
		}
		Collections.shuffle(agents);
		return agents;
	}
	
	private static void storeUmpireAndPopulation(Umpire umpire, Population population, int testNumber,
			int interaction) {
		double actionRate = umpire.actionCost / umpire.reward;
		double coordRate = umpire.coordinationCost / umpire.actionCost;
		String dirName = Utils.buildPopulationDirectoryName(parameters.getStoreDir(), actionRate, coordRate, testNumber);
		File directory = new File(dirName);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		Utils.storeUmpire(umpire, dirName, interaction);
		Utils.storeAgents(population, dirName, interaction);
		Utils.storeSyntaxMap(dirName);
		try {
			population.storeRuleChanges(dirName);
		} catch (IOException | GrammarException e) {
			e.printStackTrace();
			LOGGER.info("ERROR STORING RULE CHANGES");
		}
	}
	

	private static void imitateProcess(Population population, Random random){
		
		int numImitators = (int)Math.floor(population.size() * rateImitators);
		StrategyAgent imitator;
		StrategyAgent model;
		for (int i = 0; i < numImitators; i++){
			int s = random.nextInt(population.size());
			int h = s;
			while (h == s) {
				h = random.nextInt(population.size());
			}
			imitator = (StrategyAgent) population.get(s);
			model = (StrategyAgent) population.get(h);
			if (imitator.getStrategy() != model.getStrategy()){
				if (model.getFitness() > imitator.getFitness()){
					imitator.setStrategy(model.getStrategy());
				}
			}
		}
	}
	
	public static void main(final String[] pArgs) throws IOException {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		Duration d;
		String info; 
		boolean reduced = true;
		parameters.parseParameters(pArgs);
		initLogging();
		int firstTest = parameters.getTestNumber();
		LocalDateTime begin = LocalDateTime.now();
		LocalDateTime old = LocalDateTime.now();

		for (int testNumber = firstTest; testNumber <= numberTests; testNumber++) {
			System.out.println("Performing test " + testNumber);
			LOGGER.info("Performing test " + testNumber);
			LocalDateTime now = LocalDateTime.now();
			System.out.println(dtf.format(now));
			d = Duration.between(old, now);
			long seconds = d.getSeconds();
			info = String.format("That took %02d:%02d minutes", seconds / 60, seconds % 60);
			System.out.println(info);
			LOGGER.info(info);
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
					info = String.format("Writing after interaction %d, for coordination rate %.2f", interaction,
							parameters.getCoordinationCostRate());
					System.out.println(info);
					LOGGER.info(info);
					if(umpire.isGameFinished(parameters.getNAgents())){
						LOGGER.info("Game finished at interaction " + interaction);
						break;
					}
				}
				if (interaction % imitateRate == 0 && interaction > firstRun){
					imitateProcess(population, game.getRandom());
				}
				game.runInteraction(interaction);
			}

			storeUmpireAndPopulation(umpire, population, testNumber, parameters.getNInteractions());
		}
		d = Duration.between(begin, LocalDateTime.now());
		long seconds = d.getSeconds();
		info = String.format("The whole thing took %02d:%02d minutes", seconds / 60, seconds % 60);
		System.out.println(info);
		LOGGER.info(info);
		System.exit(0);
	}
}
