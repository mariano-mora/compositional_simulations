/* Implementation of the Parameters class.

   (c) Paul Vogt, 2001
*/
package Util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import LGames.Strategy.StrategyType;

/**
 * This class stores the parameters that are used at various places in the
 * simulation. Most of them can be set both from the control panel and the
 * command line. As they are well described in the manual, they won't be
 * discussed here.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */
public class Parameters {

	public enum AgentType {
		HOLISTIC(0), COMPOSITIONAL1(1), COMPOSITIONAL2(2), COMPOSITIONAL3(3), STRATEGIC(4);
		private int value;
		private static Map<Integer, AgentType> map = new HashMap<Integer, AgentType>();

		private AgentType(int value) {
			this.value = value;
		}

		static {
			for (AgentType agentType : AgentType.values()) {
				map.put(agentType.value, agentType);
			}
		}

		public static AgentType valueOf(int agentType) {
			return (AgentType) map.get(agentType);
		}

		public int getValue() {
			return this.value;
		}

	};

	private int nAgents = 30;
	private int nInteractions = 50000;
	private int firstRun = 0;
	private int firstInteraction = 0;
	private int maxRuns = 30000;
	private int maxCxtSize = 4;
	private double creationProb = 1.0;
	private int foa = 4;

	private double noiseCxt = 0.0;
	private int nPerceptual = 6;
	private int nDimensions = 7;
	private boolean[] features = new boolean[nPerceptual];

	private boolean run = true;
	private boolean newGame = false;
	private boolean fixedCxtSize = true;
	private boolean logFile = false;
	private boolean lexSave = true;
	private boolean printLexicon = true;
	private boolean step = false;
	private int stepSize = 1;
	private boolean printGame = true;

	private boolean saveLexicon = true;

	private boolean quit = false;
	private boolean stop = false;
	private boolean visible = false;

	private String logFileName = new String("log.txt");
	private String lexFileName = new String("lex.txt");
	private String lexIIFileName = new String("lexII.txt");
	private String featureString = "RGBSXY";
	private boolean forget = false;
	private int forgetRate = 30;
	private boolean incrementalForgetting = true;

	private PrintWriter outfile = null;
	private PrintWriter lexFile = null;
	private PrintWriter lexIIFile = null;

	private int nIterations = 1;
	private boolean ILM = false;
	private char gameType = 'g';
	private boolean ilmIncremental = false;
	private int popGrowth = 2;
	private int maxAgents = 2000;

	private boolean variation = false;
	private double ogProbability = 0.0;
	private double ggProbability = 1.0;

	private boolean speakerToSpeaker = true;
	private double pAdultSpeaker = 0.5;
	private double pAdultHearer = 0.5;
	private boolean adaptation = true;

	private char updatePType = 'c';
	private char updateScore = 's';
	private boolean selectFixedColours = true;
	private AgentType agentType = AgentType.COMPOSITIONAL2;// 0 - holistic, 1 -
														// compositional1, 2 -
														// compositional2
	private StrategyType strategyType = StrategyType.ALTRUISTIC;
	private double reward = 100.0;
	private double actionCostRate = 0.45;
	private double coordinationCostRate = 0.05;
	private double ratioAltruistic = 0.5;
	private int memorySize = 1;
	private int testNumber = 1;

	private boolean test = false;
	private int trainingSet = 0;

	private double etaS = 0.9;
	private double etaN = 0.9;

	private int nMeanings = 1000;
	private int nSymbols = 1000;

	private String workingDir = null;
//	private String storeDir = "/Users/mariano/developing/eclipse/WS/results/population_game/";
	private String storeDir = "/homes/mmm31/developing/game_results/population_game/";
	private boolean printScore = false;
	private boolean testPopulation = false;

	private boolean iHolistic = false;
	private int nNoLearning = 0;
	private int alphabetSize = 26;
	private boolean showLexicon = false;

	public Parameters() {
		for (int i = 0; i < features.length; i++)
			features[i] = true;
	}

	private void parseFeatures() {

		for (int i = 0; i < features.length; i++)
			features[i] = false;

		for (int i = 0; i < featureString.length(); i++)
			switch (featureString.charAt(i)) {
			case 'R':
				features[0] = true;
				break;
			case 'G':
				features[1] = true;
				break;
			case 'B':
				features[2] = true;
				break;
			case 'S':
				features[3] = true;
				break;
			case 'X':
				features[4] = true;
				break;
			case 'Y':
				features[5] = true;
				break;
			default:
				System.out.println(featureString + " is an invalid feature string, it should be made of RGBSXY");
				System.exit(1);
				break;
			}
	}

	public void parseParameters(final String[] pArgs) throws IllegalArgumentException {
		int i = 0;
		boolean help = false;
		while (i < pArgs.length) {
			String value = "-111";
			if (i < pArgs.length - 1)
				value = pArgs[i + 1];
			else
				help = true;
			switch (pArgs[i].charAt(1)) {
			case 'a':
				try {
					nAgents = Integer.parseInt(value);
					if (nAgents > 1)
						System.out.println("param popSize=" + nAgents);
					else {
						System.out.println("popSize should be an integer > 1.");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					throw new IllegalArgumentException("popSize should be an integer > 1.");
				}
				break;
			case 'B':
				try {
					firstInteraction = Integer.parseInt(value);
					if (firstInteraction >= 0)
						System.out.println("param firstInteraction=" + firstInteraction);
					else {
						throw new IllegalArgumentException("first interaction should be an integer >= 0.");
					}
				} catch (final NumberFormatException p) {
					throw new IllegalArgumentException("first interaction should be an integer >= 0.");
				}
				break;
			case 'T':
				try {
					int parseStrategyType = Integer.parseInt(value);
					if (parseStrategyType >= 0 && parseStrategyType < 2) {
						strategyType = StrategyType.valueOf(parseStrategyType);
						System.out.println("param strategyType=" + strategyType);
					} else {
						throw new IllegalArgumentException("strategyType should be either 0 or 1.");
					}
				} catch (final NumberFormatException p) {
					throw new IllegalArgumentException("strategyType should be either 0 or 1.");
				}
				break;

			case 'r':
				try {
					nInteractions = Integer.parseInt(value);
					if (nInteractions > 0 && nInteractions < maxRuns)
						System.out.println("param nGames=" + nInteractions);
					else {
						throw new IllegalArgumentException("nGames should be an integer > 0 and < " + maxRuns);
					}
				} catch (final NumberFormatException p) {
					throw new IllegalArgumentException("nGames should be an integer > 0 and < " + maxRuns);
				}
				break;

			case 'A':
				try {
					actionCostRate = Double.parseDouble(value);
					if (0.0 <= actionCostRate && actionCostRate <= 1.20)
						System.out.println("param actionCostRate=" + actionCostRate);
					else {
						throw new IllegalArgumentException("actionCostRate should be a double value in [0.0,1.15]");
					}
				} catch (final NumberFormatException p) {
					throw new IllegalArgumentException("actionCostRate should be a double value in [0.0,1.15]");
				}
				break;

			case 'C':
				try {
					coordinationCostRate = Double.parseDouble(value);
					if (0.0 <= coordinationCostRate && coordinationCostRate <= 1.20)
						System.out.println("param coordinationCostRate=" + coordinationCostRate);
					else {
						throw new IllegalArgumentException(
								"coordinationCostRate should be a double value in [0.0,1.15]");
					}
				} catch (final NumberFormatException p) {
					throw new IllegalArgumentException("coordinationCostRate should be a double value in [0.0,1.15]");
				}
				break;

			case 'f':
				try {
					firstRun = Integer.parseInt(value);
				} catch (final NumberFormatException p) {
					throw new IllegalArgumentException("firstRun was not of the right format " + firstRun);
				}
				break;

			case 'S':
				try {
					memorySize = Integer.parseInt(value);
				} catch (final NumberFormatException p) {
					throw new IllegalArgumentException("Memory size was not the right format");
				}
				break;

			case 'F':
				try {
					alphabetSize = Integer.parseInt(value);
					if (alphabetSize > 1 && alphabetSize <= 26)
						System.out.println("param alphabetSize=" + alphabetSize);
					else {
						System.out.println("alphabetSize should be an integer between <1,26].");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("alphabetSize should be an integer > 1.");
					System.exit(1);
				}
				break;
			case 'c':
				try {
					maxCxtSize = Integer.parseInt(value);
					if (maxCxtSize > 0)
						System.out.println("param cxtSize=" + maxCxtSize);
					else {
						System.out.println("cxtSize should be an integer > 0.");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("cxtSize should be an integer > 0.");
					System.exit(1);
				}
				break;
			case 't':
				testNumber = Integer.parseInt(value);
				System.out.println("param test=" + test);
				break;
			case 'H':
				selectFixedColours = (new Boolean(value)).booleanValue();
				System.out.println("param fCol=" + selectFixedColours);
				break;
			case 'D':
				nDimensions = Integer.parseInt(value);
				System.out.println("param nDimensions=" + nDimensions);
				break;
			case 'i':
				try {
					nIterations = Integer.parseInt(value);
					if (nIterations > 0) {
						System.out.println("param nIter=" + nIterations);
						if (nIterations > 1)
							ILM = true;
						System.out.println("param ILM=" + ILM);
					} else {
						System.out.println("nIter should be an integer > 0.");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("nIter should be an integer > 0.");
					System.exit(1);
				}
				break;
			case 'I':
				iHolistic = (new Boolean(value)).booleanValue();
				System.out.println("param iHolistic=" + iHolistic);
				break;
			case 'j':
				ilmIncremental = (new Boolean(value)).booleanValue();
				System.out.println("param incrPop=" + ilmIncremental);
				break;
			case 'J':
				try {
					popGrowth = Integer.parseInt(value);
					if (popGrowth <= 0 || popGrowth > (maxAgents - nAgents))
						Utils.error("0<popGrowth<=(maxAgents-nAgents), but popGrowth=" + popGrowth);
					else
						System.out.println("param popGrowth=" + popGrowth);
				} catch (final NumberFormatException p) {
					Utils.error("popGrowth should be an integer");
				}
				break;
			case 'N':
				try {
					maxAgents = Integer.parseInt(value);
					if (maxAgents < nAgents)
						Utils.error("maxAgents>=nAgents, but maxAgents=" + maxAgents);
					else
						System.out.println("param maxAgents=" + maxAgents);
				} catch (final NumberFormatException p) {
					Utils.error("maxAgents should be an integer");
				}
				break;
			case 'K':
				try {
					nNoLearning = Integer.parseInt(value);
					if (nNoLearning >= 0) {
						System.out.println("param preLing=" + nNoLearning);
					} else {
						System.out.println("preLing should be an integer >= 0.");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("preLing should be an integer >= 0.");
					System.exit(1);
				}
				break;
			case 'm':
				try {
					foa = Integer.parseInt(value);
					if (foa > 0) {
						System.out.println("param foa=" + foa);
					} else {
						System.out.println("foa should be an integer > 0.");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("foa should be an integer > 0.");
					System.exit(1);
				}
				break;
			case 'M':
				try {
					nMeanings = Integer.parseInt(value);
					if (nMeanings > 0) {
						System.out.println("param nMeanings=" + nMeanings);
					} else {
						System.out.println("nMeanings should be an integer > 0.");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("nMeanings should be an integer > 0.");
					System.exit(1);
				}
				break;
			case 'W':
				try {
					nSymbols = Integer.parseInt(value);
					if (nSymbols > 0) {
						System.out.println("param nWords=" + nSymbols);
					} else {
						System.out.println("nWords should be an integer > 0.");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("nWords should be an integer > 0.");
					System.exit(1);
				}
				break;
			case 'n':
				try {
					noiseCxt = Double.parseDouble(value);
					if (noiseCxt >= 0.0)
						System.out.println("param pNoise=" + noiseCxt);
					else {
						System.out.println("pNoise should be a double value >= 0");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("pNoise should be a double value >= 0");
					System.exit(1);
				}
				break;
			case 'p':
				try {
					creationProb = Double.parseDouble(value);
					if (0.0 <= creationProb && creationProb <= 1.0)
						System.out.println("param pWC=" + creationProb);
					else {
						System.out.println("pWC should be a double value in [0.0,1.0]");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("pWC should be a double value in [0.0,1.0]");
					System.exit(1);
				}
				break;
			case 'P':
				printScore = (new Boolean(value)).booleanValue();
				System.out.println("param printScore=" + printScore);
				break;
			case 'R':
				try {
					ratioAltruistic = Double.parseDouble(value);
					if (0.0 <= ratioAltruistic && ratioAltruistic <= 1.0)
						System.out.println("param ratioAltruistic=" + ratioAltruistic);
					else {
						System.out.println("ratioAltruistic should be a double value in [0.0,1.0]");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("ratioAltruistic should be a double value in [0.0,1.0]");
					System.exit(1);
				}
				break;

			case 's':
				run = (new Boolean(value)).booleanValue();
				System.out.println("param start=" + run);
				break;
//			case 'd':
//				workingDir = new String(value);
//				File n = new File(workingDir);
//				if (!n.isDirectory())
//					if (!n.mkdirs())
//						Utils.error("Could not create the workDir" + workingDir);
//				System.out.println("param workDir=" + workingDir);
//				break;
			case 'b':
				adaptation = (new Boolean(value)).booleanValue();
				System.out.println("param adaptSG=" + adaptation);
				break;

			case 'l':
				lexFileName = new String(value);
				printLexicon = true;
				if (lexFileName != "-111")
					System.out.println("param lexFile=" + lexFileName);
				else {
					System.out.println("You should specify a filename");
					System.exit(1);
				}
				break;
			case 'L':
				lexIIFileName = new String(value);
				saveLexicon = true;
				if (lexIIFileName != "-111")
					System.out.println("param lexIIFile=" + lexIIFileName);
				else {
					System.out.println("You should specify a filename");
					System.exit(1);
				}
				break;
			case 'g':
				gameType = (new String(value)).charAt(0);
				if (gameType == 'g' || gameType == 'o' || gameType == 's' || gameType == 'S')
					System.out.println("param gameType=" + gameType);
				else {
					System.out.println("gameType should be either 'o', 'g' or 's' (without quotes)");
					System.exit(1);
				}
				break;
			case 'u':
				updatePType = (new String(value)).charAt(0);
				if (updatePType == 'c' || updatePType == 'w' || updatePType == 's' || updatePType == 'n')
					System.out.println("param uPType=" + updatePType);
				else {
					System.out.println("uPType should be either 'c', 's', 'w', 'n' (without quotes)");
					System.exit(1);
				}
				break;
			case 'U':
				updateScore = (new String(value)).charAt(0);
				if (updateScore == 's' || updateScore == 'u')
					System.out.println("param uScore=" + updateScore);
				else {
					System.out.println("uScore should be either 's' or 'u'");
					System.exit(1);
				}
				break;
			case 'k':
				featureString = new String(value);
				parseFeatures();
				System.out.println("param features=" + featureString);
				break;
			case 'v':
				visible = (new Boolean(value)).booleanValue();
				System.out.println("param uInterface=" + visible);
				break;
			case 'V':
				variation = (new Boolean(value)).booleanValue();
				System.out.println("param varGames=" + variation);
				break;
			case 'G':
				ggProbability = Double.parseDouble(value);
				System.out.println("param pGG=" + ggProbability);
				break;
			case 'O':
				ogProbability = Double.parseDouble(value);
				System.out.println("param pOG=" + ogProbability);
				break;

			case 'e':
				try {
					etaN = Double.parseDouble(value);
					if (0.0 <= etaN && etaN <= 1.0)
						System.out.println("param etaN=" + etaN);
					else {
						System.out.println("etaN should be a double value in [0.0,1.0]");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("param etaN should be a double value in [0.0,1.0]");
					System.exit(1);
				}
				break;
			case 'E':
				try {
					etaS = Double.parseDouble(value);
					if (0.0 <= etaS && etaS <= 1.0)
						System.out.println("param etaS=" + etaS);
					else {
						System.out.println("etaS should be a double value in [0.0,1.0]");
						System.exit(1);
					}
				} catch (final NumberFormatException p) {
					System.out.println("etaS should be a double value in [0.0,1.0]");
					System.exit(1);
				}
				break;
			default:
				System.out.println("param Help:");
				help = true;
				break;
			}
			if (help == true) {
				System.out.println();
				System.out.println("Usage: >java THSim -option value [-option value] ...");
				System.out.println();
				System.out.println("    option value\tParameter");
				// System.out.println();
				System.out.println("\t-a " + nAgents + "\t\tpopSize");
				System.out.println("\t-A " + pAdultSpeaker + " \t\tpAdultSpeaker");
				System.out.println("\t-b " + adaptation + "\t\tadaptSG");
				System.out.println("\t-B " + trainingSet + "\t\ttrainingSetSize (0=whole set)");
				System.out.println("\t-c " + maxCxtSize + "\t\tcxtSize");
				System.out.println("\t-C " + selectFixedColours + "\t\tfCol");
//				System.out.println("\t-d " + workingDir + "\t\tworkDir");
				System.out.println("\t-D " + testPopulation + "\t\ttestPop");
				System.out.println("\t-e " + etaN + " \t\tetaN");
				System.out.println("\t-E " + etaS + " \t\tetaS");
				System.out.println("\t-f " + logFileName + "\tlogFile");
				System.out.println("\t-F " + alphabetSize + "\t\talphabetSize");
				System.out.println("\t-g " + gameType + "\t\tgameType (o,g,s)");
				System.out.println("\t-G " + ggProbability + "\t\tpGG");
				System.out.println("\t-H " + pAdultHearer + " \t\tpAdultHearer");
				System.out.println("\t-i " + nIterations + "\t\tnIter");
				System.out.println("\t-I " + iHolistic + "\tiHolistic");
				System.out.println("\t-j " + ilmIncremental + "\tincrPop");
				System.out.println("\t-J " + popGrowth + "\t\tpopGrowth");
				System.out.println("\t-k " + featureString + "\tfeatures");
				System.out.println("\t-K " + nNoLearning + "\t\tpreLing");
				System.out.println("\t-l " + lexFileName + "\tlexFile");
				System.out.println("\t-L " + lexIIFileName + "\tlexIIFile");
				System.out.println("\t-m " + foa + "\t\tfoa");
				System.out.println("\t-M " + nMeanings + "\t\tmemMeanings");
				System.out.println("\t-n " + noiseCxt + "\t\tpNoise");
				System.out.println("\t-N " + maxAgents + "\t\tmaxAgents");
				System.out.println("\t-O " + ogProbability + "\t\tpOG");
				System.out.println("\t-p " + creationProb + "\t\tpWC");
				System.out.println("\t-P " + printScore + "\tprintScore");
				System.out.println("\t-r " + nInteractions + "\t\tnGames");
				System.out.println("\t-R " + printGame + "\tprintGame");
				System.out.println("\t-s " + run + "\tstart");
				System.out.println("\t-S " + speakerToSpeaker + "\t\tS2S");
				System.out.println("\t-t " + test + "\ttest");
				System.out.println("\t-T " + agentType + " \t\tagentType (0: HA, 1: HA2, 2: CA)");
				System.out.println("\t-u " + updatePType + "\t\tuPType (c,s,w,n)");
				System.out.println("\t-U " + updateScore + "\t\tuScore (s,u)");
				System.out.println("\t-v " + visible + "\t\tuInterface");
				System.out.println("\t-V " + variation + "\tvarGames");
				System.out.println("\t-W " + nSymbols + "\t\tmemWords");
				System.out.println("\t-h \t\tthis text");
				System.out.println("========================================");
				System.out.println();
				System.exit(0);
			}
			i += 2;
		}
	}

	public int getNAgents() {
		return nAgents;
	}

	public void setNAgents(int n) {
		nAgents = n;
	}

	public int getNInteractions() {
		return nInteractions;
	}

	public void setNInteractions(int n) {
		nInteractions = n;
	}

	public int getMaxCxtSize() {
		return maxCxtSize;
	}

	public void setMaxCxtSize(int n) {
		maxCxtSize = n;
	}

	public double getNoiseCxt() {
		return noiseCxt;
	}

	public void setNoiseCxt(double x) {
		noiseCxt = x;
	}

	public boolean getRun() {
		return run;
	}

	public void setRun(boolean b) {
		run = b;
	}

	public boolean getNewGame() {
		return newGame;
	}

	public void setNewGame(boolean b) {
		newGame = b;
	}

	public boolean getFixedCxtSize() {
		return fixedCxtSize;
	}

	public void setFixedCxtSize(boolean b) {
		fixedCxtSize = b;
	}

	public boolean getLogFile() {
		return logFile;
	}

	public void setLogFile(boolean b) {
		logFile = b;
	}

	public boolean getPrintLexicon() {
		return printLexicon;
	}

	public void setPrintLexicon(boolean b) {
		printLexicon = b;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public void setLogFileName(String f) {
		logFileName = f;
	}

	public String getLexFileName() {
		return lexFileName;
	}

	public String getLexIIFileName() {
		return lexIIFileName;
	}

	public void openFile(File file) throws IOException {
		if (file != null && outfile == null)
			outfile = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	}

	public void openFile(String filename) throws IOException {
		if (logFileName != null && outfile == null)
			outfile = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
	}

	public void closeFile() {
		if (outfile != null)
			outfile.close();
	}

	public void killFile() {
		if (outfile != null) {
			outfile.close();
			outfile = null;
		}
	}

	public void flushFile() {
		if (outfile != null)
			outfile.flush();
	}

	public void openLexFile(File file) throws IOException {
		if (file != null)
			lexFile = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	}

	public void openLexFile(String filename) throws IOException {
		if (filename != null)
			lexFile = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
	}

	public PrintWriter getLexFile() {
		return lexFile;
	}

	public void closeLexFile() {
		if (lexFile != null) {
			lexFile.close();
			lexFile = null;
		}
	}

	public PrintWriter getFile() {
		return outfile;
	}

	public boolean getStop() {
		return stop;
	}

	public void setStop(boolean b) {
		stop = b;
	}

	public boolean getQuit() {
		return quit;
	}

	public void setQuit(boolean b) {
		quit = b;
	}

	public void setShowLexicon(boolean b) {
		showLexicon = b;
	}

	public boolean getShowLexicon() {
		return showLexicon;
	}

	public boolean getVisible() {
		return visible;
	}

	public double getCreationProb() {
		return creationProb;
	}

	public void setCreationProb(double b) {
		creationProb = b;
	}

	public boolean[] getFeatures() {
		return features;
	}

	public void setFeatures(boolean[] b) {
		features = b;
	}

	public void setILM(boolean b) {
		ILM = b;
	}

	public boolean getILM() {
		return ILM;
	}

	public void setNIterations(int i) {
		nIterations = i;
	}

	public int getNIterations() {
		return nIterations;
	}

	public char getGameType() {
		return gameType;
	}

	public void setGameType(char c) {
		gameType = c;
	}

	public void setForget(boolean b) {
		forget = b;
	}

	public boolean getForget() {
		return forget;
	}

	public void setForgetRate(int i) {
		forgetRate = i;
	}

	public int getForgetRate() {
		return forgetRate;
	}

	public void setIncrementalForgetting(boolean b) {
		incrementalForgetting = b;
	}

	public boolean getIncrementalForgetting() {
		return incrementalForgetting;
	}

	public void setIlmIncremental(boolean b) {
		ilmIncremental = b;
	}

	public boolean ilmIncremental() {
		return ilmIncremental;
	}

	public void setPopGrowth(int i) {
		popGrowth = i;
	}

	public int getPopGrowth() {
		return popGrowth;
	}

	public void setMaxAgents(int i) {
		maxAgents = i;
	}

	public int getMaxAgents() {
		return maxAgents;
	}

	public void setVariation(boolean b) {
		variation = b;
	}

	public boolean getVariation() {
		return variation;
	}

	public void setOGProbability(double d) {
		ogProbability = d;
	}

	public double getOGProbability() {
		return ogProbability;
	}

	public void setGGProbability(double d) {
		ggProbability = d;
	}

	public double getGGProbability() {
		return ggProbability;
	}

	public void setSpeakerToSpeaker(boolean d) {
		speakerToSpeaker = d;
	}

	public boolean getSpeakerToSpeaker() {
		return speakerToSpeaker;
	}

	public void setPAdultSpeaker(double d) {
		pAdultSpeaker = d;
	}

	public double getPAdultSpeaker() {
		return pAdultSpeaker;
	}

	public void setPAdultHearer(double d) {
		pAdultHearer = d;
	}

	public double getPAdultHearer() {
		return pAdultHearer;
	}

	public void setEtaN(double d) {
		etaN = d;
	}

	public double getEtaN() {
		return etaN;
	}

	public void setEtaS(double d) {
		etaS = d;
	}

	public double getEtaS() {
		return etaS;
	}

	public boolean getAdaptation() {
		return adaptation;
	}

	public void setAdaptation(boolean b) {
		adaptation = b;
	}

	public boolean getSaveLexicon() {
		return saveLexicon;
	}

	public void setSaveLexicon(boolean b) {
		saveLexicon = b;
	}

	public void openLexIIFile(File file) throws IOException {
		if (file != null)
			lexIIFile = new PrintWriter(new BufferedWriter(new FileWriter(file)));
	}

	public void openLexIIFile(String filename) throws IOException {
		if (filename != null)
			lexIIFile = new PrintWriter(new BufferedWriter(new FileWriter(filename)));
	}

	public PrintWriter getLexIIFile() {
		return lexIIFile;
	}

	public void closeLexIIFile() {
		if (lexIIFile != null) {
			lexIIFile.close();
			lexIIFile = null;
		}
	}

	public void setUpdatePType(char c) {
		updatePType = c;
	}

	public char getUpdatePType() {
		return updatePType;
	}

	public void setUpdateScore(char c) {
		updateScore = c;
	}

	public char getUpdateScore() {
		return updateScore;
	}

	public void setFixedColours(boolean b) {
		selectFixedColours = b;
	}

	public boolean getFixedColours() {
		return selectFixedColours;
	}

	public void setAgentType(AgentType n) {
		agentType = n;
	}

	public AgentType getAgentType() {
		return agentType;
	}

	public StrategyType getStrategyType() {
		return strategyType;
	}

	public void setStrategyType(StrategyType strategyType) {
		this.strategyType = strategyType;
	}

	public boolean getHolistic() {
		return iHolistic;
	}

	public int getNNoLearning() {
		return nNoLearning;
	}

	public void setNNoLearning(int n) {
		nNoLearning = n;
	}

	public boolean getTest() {
		return test;
	}

	public boolean getStep() {
		return step;
	}

	public void setStep(boolean b) {
		step = b;
	}

	public void setFOA(int l) {
		foa = l;
	}

	public int getFOA() {
		return foa;
	}

	public int getNMeanings() {
		return nMeanings;
	}

	public void setNMeanings(int n) {
		nMeanings = n;
	}

	public int getNSymbols() {
		return nSymbols;
	}

	public void setNSymbols(int n) {
		nSymbols = n;
	}

	public void setStepSize(int n) {
		stepSize = n;
	}

	public int getStepSize() {
		return stepSize;
	}

	public boolean getPrintGame() {
		return printGame;
	}

	public void setPrintGame(boolean b) {
		printGame = b;
	}

	public boolean getPrintScore() {
		return printScore;
	}

	public void setPrintScore(boolean b) {
		printScore = b;
	}

	public String getDir() {
		return workingDir;
	}

	public void setDir(final String d) {
		workingDir = d;
	}

	public void setTestPopulation(boolean b) {
		testPopulation = b;
	}

	public boolean getTestPopulation() {
		return testPopulation;
	}

	public int trainingSet() {
		return trainingSet;
	}

	public void setTrainingSet(final int n) {
		trainingSet = n;
	}

	public void setAlphabetSize(final int n) {
		alphabetSize = n;
	}

	public int getAlphabetSize() {
		return alphabetSize;
	}

	public double getReward() {
		return reward;
	}

	public void setReward(double reward) {
		this.reward = reward;
	}

	public double getActionCostRate() {
		return actionCostRate;
	}

	public void setActionCostRate(double actionCostRate) {
		this.actionCostRate = actionCostRate;
	}

	public double getCoordinationCostRate() {
		return coordinationCostRate;
	}

	public void setCoordinationCostRate(double coordinationCostRate) {
		this.coordinationCostRate = coordinationCostRate;
	}

	public int getMaxRuns() {
		return maxRuns;
	}

	public void setMaxRuns(int maxRuns) {
		this.maxRuns = maxRuns;
	}

	public int getFirstRun() {
		return firstRun;
	}

	public void setFirstRun(int firstRun) {
		this.firstRun = firstRun;
	}

	public int getMemorySize() {
		return memorySize;
	}

	public void setMemorySize(int memorySize) {
		this.memorySize = memorySize;
	}

	public String getStoreDir() {
		return storeDir;
	}

	public void setStoreDir(String storeDir) {
		this.storeDir = storeDir;
	}

	public int getnDimensions() {
		return nDimensions;
	}

	public int getnPerceptual() {
		return nPerceptual;
	}

	public int getTestNumber() {
		return testNumber;
	}

	public int getFirstInteraction() {
		return firstInteraction;
	}

	public void setFirstInteraction(int firstInteraction) {
		this.firstInteraction = firstInteraction;
	}

	public double getRatioAltruistic() {
		return ratioAltruistic;
	}

	public void setRatioAltruistic(double ratioAltruistic) {
		this.ratioAltruistic = ratioAltruistic;
	}

}
