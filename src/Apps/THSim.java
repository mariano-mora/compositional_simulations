package Apps;

import Interface.GeomJComponent;
import Interface.LanguageJComponent;
import Interface.ControlDialog;
import Util.Utils;
import Util.Parameters;
import Util.Parameters.AgentType;
import LGames.Agent;
import LGames.CompositionalAgent2;
import LGames.Context;
import LGames.DGame;
import LGames.HolisticAgent;
import LGames.HolisticAgent2;
import LGames.Meaning;
import LGames.Rules2;
import LGames.StrategyAgent;
import LGames.Symbols;
import Interface.PopMenu;
import Interface.Statistics;
import Interface.CompositionalStatistics;
import Interface.Canvas;
import Util.Stats;
import Util.CompositionalStats;
import Util.HolisticStats;
import Util.DoubleArray;
import Util.IntArray;

import java.util.*;
import java.io.*;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Color;

import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.swing.*;
import javax.swing.JMenuBar;

/**
 * The main class for THSim v4.0.3
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class THSim {

	protected static Context cxt;
	protected static List<Agent> lAgents = new ArrayList<Agent>();
	protected static int nAgents = 2;
	protected final static double noiseCxt = 0.0;
	protected static Agent speaker;
	protected static Agent hearer;
	protected static int lastID = 0;
	protected static Parameters parameters = new Parameters();
	private static boolean[] views = new boolean[3];
	protected static Stats pStats;
	protected static List lScoreFiles = new ArrayList();
	protected static List lWordList = new ArrayList();
	protected static List lWords = new ArrayList();
	protected static Random random = new Random();

	/**
	 * Initialise a list of agents
	 */
	protected static void create_agents() {
		for (int i = 0; i < parameters.getNAgents(); i++) {
			lAgents.add(
					new CompositionalAgent2(lastID, 0, parameters.getEtaN(), parameters.getEtaS(), parameters.getFeatures(),
							parameters.getNNoLearning(), parameters.getAlphabetSize()));
			lastID++;
			if (parameters.getPrintScore()) {
				lScoreFiles.add(new ArrayList());
				lWordList.add(new ArrayList());
			}
		}
	}

	/**
	 * Reinitialise the list of agents
	 */
	protected static void reinitializeAgents(int lg) {

		int maxAge = 0;
		int oldest = -1;

		if (parameters.getNAgents() > lAgents.size()) {
			for (int i = lAgents.size(); i < parameters.getNAgents(); i++) {
				lAgents.add(new StrategyAgent(lastID, 0, parameters.getEtaN(), parameters.getEtaS(),
						parameters.getFeatures(), parameters.getNNoLearning(), parameters.getAlphabetSize(),
						parameters.getStrategyType(), parameters.getMemorySize()));
				lastID++;
				if (parameters.getPrintScore()) {
					lScoreFiles.add(new ArrayList());
					lWordList.add(new ArrayList());
				}
			}
		} else {
			while (parameters.getNAgents() < lAgents.size()) {
				maxAge = 1000000000;
				oldest = -1;
				for (int i = 0; i < lAgents.size(); i++) {
					if (((Agent) lAgents.get(i)).getID() <= maxAge) {
						maxAge = ((Agent) lAgents.get(i)).getID();
						oldest = i;
					}
				}
				if (oldest >= 0) {
					lAgents.remove(oldest);
					if (parameters.getPrintScore()) {
						lScoreFiles.remove(oldest);
						lWordList.remove(oldest);
					}
				}
			}
		}
	}

	private static void replaceHolistics() {
		List tmpList = new ArrayList();
		for (int i = 0; i < lAgents.size(); i++) {// for the moment only into
													// agents of type 2
			tmpList.add(new CompositionalAgent2((HolisticAgent) lAgents.get(i), parameters.getFeatures(),
					parameters.getNNoLearning(), parameters.getAlphabetSize()));
		}
		lAgents = tmpList;
	}

	private static Context createContext(int[][] trainingSet) {
		
		Context cxt;
		if (parameters.trainingSet() > 0){// with bottleneck
			cxt = new Context(parameters.getMaxCxtSize(), parameters.getFeatures(), parameters.getFixedColours(),
					trainingSet, parameters.trainingSet());
			cxt.buildContextTrainingSet();
		}
		else{
			cxt = new Context(parameters.getMaxCxtSize(), parameters.getFeatures(), parameters.getFixedColours(), random);
			cxt.buildContext();
		}
		return cxt;
	}

	/**
	 * The main function of THSim. Here is where it all starts.
	 * <p>
	 * THsim starts with initialising all kinds of things, such as setting the
	 * parameters following those given, or following the default settings. The
	 * parameters are mostly stored in the Utils.Parameters class. The
	 * interactive canvas is initialised when required.
	 * <p>
	 * Then, the program waits for a start-signal. This may be given as an
	 * argument, or from the user-interface.
	 * <p>
	 * Given the start signal, the simulation starts, possibly after
	 * (re)initialising some data structures.
	 * <p>
	 * During the simulation, output is written to the standard output, which I
	 * usually redirect into a file for further analysis (only for linux users
	 * though). If the user interface is active, additional output is written
	 * there. Further output can be stored in files as specified with the
	 * arguments.
	 */

	public static void main(final String[] pArgs) throws IOException {

		Canvas canvas = null;
		parameters.parseParameters(pArgs);

		create_agents();

		// initialising statistics class
		if (parameters.getAgentType() == AgentType.COMPOSITIONAL2 || parameters.getAgentType() == AgentType.STRATEGIC)
			pStats = new CompositionalStats();
		else
			pStats = new HolisticStats();

		if (parameters.getVisible())
			canvas = new Canvas(pStats, parameters);

		int N = 0;
		Symbols utteranceSpeaker = null;
		Symbols interpretationHearer = null;
		int nrRuns = 0;
		File logFile = null;
		boolean openedLogFile = false;
		boolean openedLexFile = false;

		if (parameters.getLogFile()) {
			parameters.openFile(parameters.getLogFileName());
			openedLogFile = true;
		}

		if (parameters.getPrintLexicon()) {
			parameters.openLexFile(parameters.getLexFileName());
			openedLexFile = true;
		}
		if (parameters.getSaveLexicon()) {
			parameters.openLexIIFile(parameters.getLexIIFileName());
		}

		int nAdults = 0;
		int nLearners = 0;

		char updateScore = 's';
		int ns = 0, nh = 1;
		int iter = 0;
		int layer = 0;
		int[][] trainingSet = new int[0][0];
		AgentType oldAgentType = parameters.getAgentType();

		while (!parameters.getQuit()) {

			parameters.setRun(true);
			nAgents = parameters.getNAgents();

			// if getRun -> run simulation for nGames
			// if getStep -> run simulation for nStep games
			if (parameters.getRun() || parameters.getStep()) {

				if (!parameters.getILM())
					iter = 0;

				// iterate for nIterations or until quit, stop or newGame is
				// pressed.
				while (iter < parameters.getNIterations() && !parameters.getQuit() && !parameters.getStop()
						&& !parameters.getNewGame()) {

					// setting the bottleneck
					if (parameters.trainingSet() > 0)
						trainingSet = (new Context()).setTrainingSet(parameters.trainingSet());

					// Again some initialisations, necessary in iterated
					// learning model
					// throwing away adult population, assigning learners to new
					// adults,
					// and assigning new fresh agents as learners.
					if (parameters.getILM() && N == nrRuns) {

						if (iter == 0 && parameters.getSpeakerToSpeaker() && N == 0) {
							if (parameters.ilmIncremental())
								nAdults = nAgents / 2;
							else
								nAdults = nAgents;
							nLearners = nAdults;
							parameters.setNAgents(nAgents);
							reinitializeAgents(0);
							pStats.reinitialize('i');
						} else if (N % parameters.getNInteractions() == 0) {
							if (parameters.getHolistic()) {
								if (iter == 0) {
									oldAgentType = parameters.getAgentType();
									parameters.setAgentType(AgentType.HOLISTIC);
									lAgents.clear();
								} else if (iter == 1) {
									parameters.setAgentType(oldAgentType);
									replaceHolistics();
								}
							}
							if (parameters.ilmIncremental()) {
								nAdults = nLearners;
								nLearners = Math.min(parameters.getMaxAgents() - nAdults,
										nLearners + parameters.getPopGrowth());
							} else {
								nAdults = nAgents / 2;
								nLearners = nAgents - nAdults;
							}
							nAgents = nAdults + nLearners;
							parameters.setNAgents(nAdults);
							reinitializeAgents(N);
							parameters.setNAgents(nAgents);
							reinitializeAgents(N);
							pStats.reinitialize('i');

						}
					} else if (parameters.getNAgents() != lAgents.size())
						reinitializeAgents(N);

					// step or run
					if (parameters.getStep())
						nrRuns = N + parameters.getStepSize();
					else
						nrRuns = (iter + 1) * parameters.getNInteractions();

					System.out.println("info nIter=" + parameters.getNIterations() + " nRuns=" + nrRuns + " iter="
							+ iter + " lg=" + N + " popSize=" + lAgents.size() + " nAdults=" + nAdults + " nLearners="
							+ nLearners);

					// here we go....
					for (int n = N; n < nrRuns && !parameters.getStop() && !parameters.getQuit(); n++) {

						cxt = createContext(trainingSet);

						int h = 0, s = 1;
						// assigning the speaker and hearer
						if (iter == 0 && parameters.getSpeakerToSpeaker()) {
							// in this case: both speakers and hearers from
							// total population
							s = (int) Math.round((double) parameters.getNAgents() * Math.random() - 0.50001);

							h = s;
							while (h == s) {
								h = (int) Math.round((double) parameters.getNAgents() * Math.random() - 0.50001);
								if (h < 0)
									h = 0;
							}
						} else {
							// in this case: speaker from adult population (or
							// with a certain
							// prob. from learner population).
							// hearer from learner population (or with certain
							// prob. from
							// adult population).
							if (Math.random() <= parameters.getPAdultSpeaker()) {
								s = (int) Math.round(nAdults * Math.random() - 0.50001);
								if (s < 0)
									s = 0;
							} else {
								s = (int) Math.round(nLearners * Math.random() - 0.50001);
								if (s < 0)
									s = 0;
								s += nAdults;
							}
							if (Math.random() < parameters.getPAdultHearer()) {
								h = s;
								while (h == s) {
									h = (int) Math.round(nAdults * Math.random() - 0.50001);
									if (h < 0)
										h = 0;
								}
							} else {
								h = s;
								while (h == s) {
									h = (int) Math.round(nLearners * Math.random() - 0.50001);
									if (h < 0)
										h = 0;
									h += nAdults;
								}
							}
							// just in case:
							if (s == h)
								Utils.error("something went wrong");
						}
						if (s < 0)
							s = 0;

						// initialising speaker and hearer with right class
						speaker = lAgents.get(s);
						hearer = lAgents.get(h);

						// selecting topic
						int t = (int) Math.round((double) cxt.distinctive.length * Math.random() - 0.50001);
						if (t < 0)
							t = 0;
						speaker.setTopic(t);

						// setting the focus of attention
						cxt.setFOA(t, parameters.getFOA());

						// adding perceptual noise to the features
						speaker.setContext(cxt, parameters.getNoiseCxt());

						speaker.playDGame(parameters.getGameType(), parameters.getUpdatePType(), n, true);
						// produce an utterance
						utteranceSpeaker = speaker.speak(parameters.getCreationProb(), parameters.getUpdateScore());

						hearer.setContext(cxt, noiseCxt);

						// if there is an utterance, the hearer has to act!
						if (utteranceSpeaker != null) {
							if (parameters.getVariation()) {
								// If we want to vary the type of language game
								// during
								// a run, we select the type here

								double select = Math.random();

								if (select <= parameters.getOGProbability())
									parameters.setGameType('o');
								else if (select <= parameters.getOGProbability() + parameters.getGGProbability())
									parameters.setGameType('g');
								else
									parameters.setGameType('s');
							}

							if (parameters.getGameType() == 'o') {// observational
																	// game
								// establishing joint attention
								hearer.setTopic(speaker.getTopic());
								// same for holistic & compositional agent
								hearer.playDGame('o', parameters.getUpdatePType(), n, true);
								interpretationHearer = hearer.hear(utteranceSpeaker, parameters.getUpdateScore());
							} else {// guessing or selfish game
								if (parameters.getAgentType() == AgentType.COMPOSITIONAL1) {
									// our hierarchical agent
									interpretationHearer = null;
									layer = 1;
									while (interpretationHearer == null && layer <= hearer.getMaxLayer()) {
										// resetting the distintiveness of
										// categories
										hearer.initDGame();
										for (t = 0; t < cxt.distinctive.length; t++) {
											hearer.setTopic(t);
											hearer.playDGame(parameters.getGameType(), parameters.getUpdatePType(),
													layer, n, true);
										}
										hearer.setTopic(-1);
										interpretationHearer = hearer.guess(utteranceSpeaker,
												parameters.getUpdateScore());
										layer++;
									}
								} else {
									if (parameters.getAgentType() == AgentType.COMPOSITIONAL2)
										// making sure the dist.cat.set is empty
										((CompositionalAgent2) hearer).initDCS();
									for (t = 0; t < cxt.distinctive.length; t++) {
										hearer.setTopic(t);
										hearer.playDGame(parameters.getGameType(), parameters.getUpdatePType(), n,
												true);
									}
									hearer.setTopic(-1);
									interpretationHearer = hearer.guess(utteranceSpeaker, parameters.getUpdateScore());
								}
							}
							// adaptation of scores
							speaker.adaptLexiconSpeaker(interpretationHearer, hearer.getTopic(),
									parameters.getGameType(), parameters.getUpdateScore());
							// adaptation of scores, and in case of holistic
							// guys,
							// adoption of words.
							// not, however, if adaptSG=false and playing a
							// selfish game.
							if (parameters.getAdaptation() || parameters.getGameType() != 's')
								hearer.adaptLexiconHearer(utteranceSpeaker, speaker.getTopic(),
										parameters.getGameType(), parameters.getUpdateScore());
						} else {
							hearer.setTopic(-1);
							if (parameters.getAgentType() == AgentType.COMPOSITIONAL2)
								((CompositionalAgent2) hearer).setRule(-1);
						}

						// updating the statistics
						if (parameters.getAgentType() == AgentType.COMPOSITIONAL2
								|| parameters.getAgentType() == AgentType.STRATEGIC)
							((CompositionalStats) pStats).update(speaker, hearer, parameters.getAgentType().getValue());
						else
							((HolisticStats) pStats).update(speaker, hearer, parameters.getAgentType().getValue());

						// if we want to keep track of scores, this is where it
						// happens
						// produces many files for some words, for each agent.
						// in each game the words used - when in the list -
						// are updated.
						// The files thus keep track of the evolution of scores.
						if (parameters.getPrintScore()) {

							if (!(parameters.getDir()).equals("null"))
								Utils.error("if you want to print scores, please set the workDir parameter.");

							String us = speaker.getUtterance();
							String uh = hearer.getUtterance();
							if (us != null) {
								if (lWords.contains(us) || lWords.contains(us)) {
									if (lWords.contains(us)) {
										ns = ((ArrayList) lWordList.get(s)).indexOf(us);
										if (ns < 0) {
											ns = ((ArrayList) lWordList.get(s)).size();
											((ArrayList) lWordList.get(s)).add(new String(us));
											((ArrayList) lScoreFiles.get(s)).add(new PrintWriter(
													new BufferedWriter(new FileWriter(parameters.getDir() + "/scoreA"
															+ speaker.getID() + us + ".txt"))));
										}
										speaker.printScore((PrintWriter) ((ArrayList) lScoreFiles.get(s)).get(ns),
												parameters.getUpdateScore(), us, N);
									}
									if (lWords.contains(uh)) {
										nh = ((ArrayList) lWordList.get(h)).indexOf(uh);
										if (nh < 0) {
											nh = ((ArrayList) lWordList.get(h)).size();
											((ArrayList) lWordList.get(h)).add(new String(uh));
											((ArrayList) lScoreFiles.get(h)).add(new PrintWriter(
													new BufferedWriter(new FileWriter(parameters.getDir() + "/scoreA"
															+ hearer.getID() + uh + ".txt"))));
										}
										hearer.printScore((PrintWriter) ((ArrayList) lScoreFiles.get(h)).get(nh),
												parameters.getUpdateScore(), us, N);
									}
								} else if (lWords.size() < 10 && us.equals(uh)) {
									// add this word only in case of success lg
									lWords.add(new String(us));
									ns = ((ArrayList) lWordList.get(s)).size();
									((ArrayList) lWordList.get(s)).add(new String(us));
									((ArrayList) lScoreFiles.get(s))
											.add(new PrintWriter(new BufferedWriter(new FileWriter(
													parameters.getDir() + "/scoreA" + speaker.getID() + us + ".txt"))));
									System.out.println("added file: " + parameters.getDir() + "/scroreA"
											+ speaker.getID() + us + ".txt");
									speaker.printScore((PrintWriter) ((ArrayList) lScoreFiles.get(s)).get(ns),
											parameters.getUpdateScore(), us, N);
									nh = ((ArrayList) lWordList.get(h)).size();
									((ArrayList) lWordList.get(h)).add(new String(uh));
									((ArrayList) lScoreFiles.get(h))
											.add(new PrintWriter(new BufferedWriter(new FileWriter(
													parameters.getDir() + "/scoreA" + hearer.getID() + uh + ".txt"))));
									System.out.println("added file: " + parameters.getDir() + "/scroreA"
											+ hearer.getID() + uh + ".txt");
									hearer.printScore((PrintWriter) ((ArrayList) lScoreFiles.get(h)).get(nh),
											parameters.getUpdateScore(), uh, N);
								}
							}
						}

						// if we want to output each language game
						if (parameters.getPrintGame())
							pStats.print();

						// if we want to keep track of communicative success and
						// so on.
						// in case of lexicon formation (holistic types) also
						// keeps
						// track of individual games (utterances and meanings)
						if (parameters.getLogFile()) {
							if (logFile == null && !openedLogFile && parameters.getVisible()) {
								logFile = canvas.openLogFile();
							}

							if (parameters.getFile() == null)
								if (logFile != null)
									parameters.openFile(logFile);
								else
									parameters.openFile(parameters.getLogFileName());
							if (parameters.getFile() != null) {
								pStats.logFile(parameters.getFile());
								if (n % 100 == 99)
									parameters.flushFile();
							} else {
								parameters.setLogFile(false);
								System.out.println("No logfile opened");
								System.exit(1);
							}
						}
						// output to UI
						if (parameters.getVisible()) {
							canvas.updateGame(speaker, cxt, n, hearer, iter, parameters.getGameType(),
									parameters.getAgentType().getValue(), pStats);
						} else if (n % 1000 == 0 && (parameters.getAgentType() == AgentType.COMPOSITIONAL1
								|| parameters.getAgentType() == AgentType.HOLISTIC)) {
							// Additional output to standard out.
							// "lx" prints lexicon sizes for each agent.
							System.out.print("lx " + n + " ");
							for (int i = 0; i < lAgents.size(); i++)
								if (parameters.getAgentType() == AgentType.COMPOSITIONAL1)
									System.out.print(((HolisticAgent2) lAgents.get(i)).getLexSize() + " ");
								else
									System.out.print(((HolisticAgent) lAgents.get(i)).getLexSize() + " ");
							System.out.println();
							// "on" prints ontology sizes for each agent
							System.out.print("on " + n + " ");
							for (int i = 0; i < lAgents.size(); i++)
								if (parameters.getAgentType() == AgentType.COMPOSITIONAL1)
									System.out.print(((HolisticAgent2) lAgents.get(i)).getOntSize() + " ");
								else
									System.out.print(((HolisticAgent) lAgents.get(i)).getLexSize() + " ");
							System.out.println();
						}

						if (parameters.getAgentType() == AgentType.HOLISTIC
								|| parameters.getAgentType() == AgentType.COMPOSITIONAL1) {
							// for the time being static prototypes are used in
							// compositional studies
							// in all other cases, the prototypes move.
							if (n % (lAgents.size() * 100) == 0 && n > 1)
								for (int i = 0; i < lAgents.size(); i++)
									((Agent) lAgents.get(i)).merge((parameters.getFeatures()).length,
											parameters.getUpdateScore());
						}
						// finished game, next one
						N++;

						// At the end of an iteration, we might want to test the
						// population
						// or we might want to save the lexicon or grammar.
						if (N % parameters.getNInteractions() == 0) {
							Agent oldSpeaker = speaker;
							Agent oldHearer = hearer;
							iter++;
//							if (parameters.getTestPopulation()) {
//								testPopulation(iter);
//								if (parameters.getVisible())
//									canvas.updateGame(oldSpeaker, cxt, n, oldHearer, iter, parameters.getGameType(),
//											parameters.getAgentType(), pStats);
//							}
							if (parameters.getSaveLexicon()) {
								if (parameters.getLexIIFile() == null && parameters.getVisible()) {
									File file = canvas.openLexFile();
									parameters.openLexIIFile(file);
								}
								if (parameters.getLexIIFile() != null)
									if (parameters.getAgentType() == AgentType.STRATEGIC)
										Utils.printGrammar(lAgents, parameters.getLexIIFile(),
												parameters.getUpdateScore(), iter);
									else
										Utils.printGlobalLexicon(lAgents, parameters.getLexIIFile(),
												parameters.getUpdateScore(), iter);
							} else if (parameters.getAgentType() == AgentType.STRATEGIC)
								for (int i = 0; i < lAgents.size(); i++)
									((CompositionalAgent2) lAgents.get(i)).printGrammarStats();

							if (parameters.getPrintLexicon()) {
								if (parameters.getLexFile() == null && parameters.getVisible()) {
									File file = canvas.openLexFile();
									parameters.openLexFile(file);
								}
								if (parameters.getLexFile() != null) {
									if (parameters.getAgentType() == AgentType.STRATEGIC)
										Utils.printGrammar(lAgents, parameters.getLexIIFile(),
												parameters.getUpdateScore(), iter);
									else {
										Utils.printGlobalLexicon(lAgents, parameters.getLexFile(),
												parameters.getUpdateScore(), iter);
										for (int i = 0; i < lAgents.size(); i++)
											((Agent) lAgents.get(i)).print(parameters.getLexFile(),
													parameters.getUpdateScore());
									}
									if (parameters.getVisible())
										canvas.savedLexFile();

								}
							}
						}
					}
					// make sure, the simulation stops when necessary
					if (parameters.getStep()) {
						parameters.setStop(true);
						parameters.setStep(false);
					}
				}
				if (parameters.getSaveLexicon()) {
					parameters.closeLexIIFile();

					if (parameters.getVisible())
						canvas.savedLexFile();
				}
			}

			parameters.setRun(false);
			parameters.setStop(false);
			// other ways to print your lexicon.
			if (parameters.getPrintLexicon() && N > 0) {

				if (parameters.getLexFile() == null && parameters.getVisible()) {
					File file = canvas.openLexFile();
					parameters.openLexFile(file);
				}
				if (parameters.getLexFile() != null) {
					Utils.printGlobalLexicon(lAgents, parameters.getLexFile(), parameters.getUpdateScore(), 9999);
					for (int i = 0; i < lAgents.size(); i++)
						((Agent) lAgents.get(i)).print(parameters.getLexFile(), parameters.getUpdateScore());
					parameters.closeLexFile();
					if (parameters.getVisible())
						canvas.savedLexFile();
				}

				parameters.setPrintLexicon(false);
			}
			if (!parameters.getVisible()) {
				parameters.setQuit(true);
				System.out.println();
			}
		}
		parameters.closeFile();
		System.exit(0);
	}
}
