package LGames;

import Util.*;
import java.util.*;
import java.io.PrintWriter;

/**
 * The CompositionalAgent class is a descendant of the Agent class. It
 * implements the agent's structure and behaviours for the compositionality
 * experiments. It is constructed, initialised and controlled from the thsim
 * main class.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class CompositionalAgent2 extends Agent {

	protected List<ANMeaning> ontology = new ArrayList<ANMeaning>();
	protected List lexicon = new ArrayList();
	protected final int maxMeanings = 2000;
	protected final int maxNSymbols = 600;
	protected int[][] lUse = new int[1][1];
	protected double[][] lScore = new double[1][1];
	protected int iSymbols = 0;
	public int iMeaning = 0;
	protected int nMeanings = 1;
	protected int nSymbols = 1;
	protected int nrDGSuccess = 0;
	protected int DG = 0;
	protected int utteranceID = 0;
	protected double eta = 0.9;
	protected double etaS = 0.95;
	protected double etaN = 0.9;
	private final double thresholdPType = 0.05;
	protected final double thresholdForgetting = 0.002;
	protected final double thresholdForgettingS = 0.02;
	protected int gamesPlayed = 0;
	protected boolean success = false;
	private int nHolisticRules = 0;
	private CompositionalDGame2 discriminationGame = new CompositionalDGame2();
	private Grammar<Rules2> grammar = new Grammar<Rules2>();
	private List<Instance> instances = new ArrayList<Instance>();
	private IntArray DCS = new IntArray();
	private int interpretation = -1;
	private IntArray composition = new IntArray();
	private IntArray expression = new IntArray();
	private IntArray oDCS = new IntArray();
	private IntArray expressions = new IntArray();
	private IntArray interpretations = new IntArray();
	private int rule = -1;
	private IntArray literals = new IntArray();
	private final String terminals = "SABCDEFGHIJKLMNOPQRTUVWXYZ";
	private char[] terminalCover = new char[64];
	private int maxCover = 15;
	private int nextTerm = 1;
	protected String mString = new String("[]");
	private Rules2 dummyRule = new Rules2();
	private int lg = 0;
	private int noLearning = 0;
	private int[] frequencies = new int[maxCover + 1];
	private int alphabetSize = 16;
	private List setOfDCS = new ArrayList();
	private double maxScore = 0.0;
	private String newRule = new String();
	private String[] parts = new String[0];
	private boolean parsed = false;

	/**
	 * Empty constructor
	 */
	public CompositionalAgent2() {
	}

	/**
	 * Copy constructor
	 *
	 * @param a
	 *            Agent to be copied
	 */
	public CompositionalAgent2(final CompositionalAgent2 a) {

		ontology = a.ontology;
		grammar = a.grammar;
		instances = a.instances;
		maxCover = a.maxCover;
		frequencies = a.frequencies;

	}

	/**
	 * Constructor that initialises the agent with a predefined semantics. This
	 * function was only used for testing.
	 */
	public CompositionalAgent2(final List ont) {
		ontology = ont;
	}

	/**
	 * Constructor to initialise the compositional agent (CA).
	 *
	 * @param n
	 *            identity of CA
	 * @param a
	 *            `birthyear' (i.e. lg) in which agent is created
	 * @param e
	 *            learning rate eta
	 * @param s
	 *            learning rate eta (for speakers)
	 * @param fv
	 *            array indicating which quality dimensions are used
	 * @param as
	 *            alphabet size
	 */
	public CompositionalAgent2(int n, final int a, final double e, final double s, final boolean[] fv, final int as) {

		id = n;
		age = a;
		etaN = e;
		etaS = s;
		ontology.add(new ANMeaning());
		iMeaning++;// creating dummy meaning
		maxCover = 0;
		// max-cover is a bit-representation for the features that are used.
		// suppose of the possible features RGBSXY only RGBS are used. The
		// bit-vector will be 111100, represented by the integer 15
		//
		// conceptual spaces of lower dimension have a lower cover.
		// E.g. the colour space RGB is represented by 7 (i.e. 111000).
		for (int i = 0; i < fv.length; i++)
			if (fv[i])
				maxCover += (int) Math.pow(2, i);
		frequencies = new int[maxCover + 1];

		discriminationGame = new CompositionalDGame2(maxCover, ontology);

		grammar.clear();
		// easy representation for converting a cover
		for (int i = 0; i < maxCover; i++)
			terminalCover[i] = '*';
		terminalCover[maxCover] = 'S';
		alphabetSize = as;
	}

	/**
	 * Constructor that converts a holistic agent into a compositional agent
	 * (CA).
	 *
	 * @param a
	 *            the holistic agent
	 * @param fv
	 *            array indicating which quality dimensions are used
	 * @param nl
	 *            number of games before the CA starts inducing grammar
	 * @param as
	 *            alphabet size
	 */
	public CompositionalAgent2(HolisticAgent a, final boolean[] fv, final int nl, final int as) {

		id = a.id;
		age = a.age;
		etaN = a.etaN;
		etaS = a.etaS;
		ontology.add(new ANMeaning());
		iMeaning++;
		maxCover = 0;
		for (int i = 0; i < fv.length; i++)
			if (fv[i])
				maxCover += (int) Math.pow(2, i);
		frequencies = new int[maxCover + 1];

		discriminationGame = new CompositionalDGame2(maxCover, ontology);

		grammar.clear();
		alphabetSize = as;

		for (int i = 0; i < maxCover; i++)
			terminalCover[i] = '*';
		terminalCover[maxCover] = 'S';

		noLearning = nl;

	}

	/**
	 * Constructor to initialise the compositional agent (CA).
	 *
	 * @param n
	 *            identity of CA
	 * @param a
	 *            `birthyear' (i.e. lg) in which agent is created
	 * @param e
	 *            learning rate eta
	 * @param s
	 *            learning rate eta (for speakers)
	 * @param fv
	 *            array indicating which quality dimensions are used
	 * @param nl
	 *            number of games before the CA starts inducing grammar
	 * @param as
	 *            alphabet size
	 */
	public CompositionalAgent2(int n, final int a, final double e, final double s, final boolean[] fv, final int nl,
			final int as) {

		id = n;
		age = a;
		etaN = e;
		etaS = s;
		ontology.add(new ANMeaning());
		iMeaning++;
		maxCover = 0;
		for (int i = 0; i < fv.length; i++)
			if (fv[i])
				maxCover += (int) Math.pow(2, i);
		frequencies = new int[maxCover + 1];
		alphabetSize = as;

		discriminationGame = new CompositionalDGame2(maxCover, ontology);

		grammar.clear();

		for (int i = 0; i < maxCover; i++)
			terminalCover[i] = '*';
		terminalCover[maxCover] = 'S';

		noLearning = nl;

	}

	/**
	 * This function is not implemented
	 */
	protected void forget(char type, char rc, int n) {
	}

	/**
	 * This function is not implemented
	 */
	protected void forget(char type) {
	}

	private Rules2 createRule() {
		Rules2 rule = new Rules2(DCS, maxCover, grammar, ontology, maxCover, alphabetSize);
		return rule;
	}

	private Rules2 createHolisticRule() {
		Rules2 rule = createRule();
		grammar.addRule(rule);
		int r = grammar.size() - 1;
		assert (rule.getNumber() == r);
		rule.updateFrequency(frequencies, maxCover);
		composition.add(rule.getNumber());
		nHolisticRules++;
		newRule = new String("new ");
		return rule;
	}

	private Rules2 getBestRule() {

		Rules2 retRule = null;
		double maxScore = 0.0;

		for (Rules2 rule : grammar) {
			rule.initGame();
			if (rule.match(DCS, maxCover, lg)) {
				if (rule.getScore(frequencies, maxCover) > maxScore) {
					maxScore = rule.getScore(frequencies, maxCover);
					retRule = rule;
				}
				rule.updateFrequency(frequencies, maxCover);
			}
		}
		return retRule;
	}

	private Rules2 getBestPartialMatch() {
		Rules2 retRule = null;
		double maxScore = 0.0;
		double score = 0.0;
		for (Rules2 r : grammar) {
			if (r.partialMatch(DCS, maxCover)) {
				score = r.getScore(frequencies, maxCover);
				if (score > maxScore) {
					maxScore = score;
					retRule = r;
				}
			}
		}
		return retRule;
	}

	private Symbols utteranceFromRule(Rules2 r) {
		Symbols utterance = null;
		String s = r.getExpression();
		if (s != null) {
			utterance = new Symbols(r.getNumber(), s);
			composition.add(r.getNumber());
			composition.addAll(r.getComposition());
		}
		return utterance;
	}

	/**
	 * This function produces an utterance.
	 *
	 * @param score
	 *            the word-creation probability pWc
	 * @param type
	 *            the score-update type (s - score-based, u - usage-based)
	 *
	 * @return utterance the produced utterance
	 */
	protected Symbols produce_utterance(double pWC, char type) {

		double maxScore = 0.0;
		int expr = -1;
		Symbols utt = null;
		Rules2 r = null;

		composition = new IntArray();
		// the encoding bit, looking for (compositions of) rules that match the
		// distinctive category set (DCS) that stands for the topic
		for (int i = 0; i < grammar.size(); i++) {
			r = (Rules2) grammar.get(i);
			r.initGame();
			if (r.match(DCS, maxCover, lg)) {
				if (r.getScore(frequencies, maxCover) > maxScore) {
					// selecting the strongest match
					maxScore = r.getScore(frequencies, maxCover);
					expr = i;
				}
				r.updateFrequency(frequencies, maxCover);
			}
		}

		if (expr >= 0) {// production/encoding successful
			String s = ((Rules2) grammar.get(expr)).getExpression();
			if (s != null) {// setting some values for future reference
				utt = new Symbols(expr, s);
				rule = expr;
				composition.add(expr);
				composition.addAll(((Rules2) grammar.get(expr)).getComposition());
			}
		} else if (Math.random() <= pWC) {// encoding failed, new invention
											// starts here
			maxScore = 0.0;
			// first we look if we can encode a part of the utterance...
			for (int i = 0; i < grammar.size(); i++) {
				r = (Rules2) grammar.get(i);
				if (maxCover != 63) {
					System.out.println(maxCover);
				}
				if (r.partialMatch(DCS, maxCover)) {
					if (r.getScore(frequencies, maxCover) > maxScore) {
						maxScore = r.getScore(frequencies, maxCover);
						expr = i;
					}
				}
			}
			// if we can:
			if (expr >= 0) {// create new compositional rule, by exploiting an
							// existing one
				String s = ((Rules2) grammar.get(expr)).produceExpression();
				if (s != null) {
					utt = new Symbols(expr, s);
					rule = expr;
					((Rules2) grammar.get(expr)).updateFrequency(frequencies, maxCover);
					composition.add(expr);
					composition.addAll(((Rules2) grammar.get(expr)).getComposition());
				}
				newRule = new String("new part ");
			} // otherwise:
			else {// create a new holistic rule
				grammar.addRule(new Rules2(DCS, maxCover, grammar, ontology, maxCover, alphabetSize));
				rule = grammar.size() - 1;
				utt = new Symbols(rule, ((Rules2) grammar.get(rule)).getExpression());
				((Rules2) grammar.get(rule)).updateFrequency(frequencies, maxCover);
				composition.add(rule);
				nHolisticRules++;
				newRule = new String("new ");
			}
		}
		if (rule >= 0) {// helps to construct the UI
			if (!composition.isEmpty())
				composition.remove(0);
			((Rules2) grammar.get(rule)).setComposition(new IntArray(composition));
			parts = ((Rules2) grammar.get(rule)).getParts();
			composition.add(rule);
		}
		return utt;

	}

	/**
	 * Function to call itself with an extra argument (change=true).
	 *
	 * @param probability
	 *            word-creation probability pWC
	 * @param type
	 *            game type
	 */
	public Symbols speak(double probability, char type) {
		return speak(probability, type, true);
	}

	/**
	 * Function to call produce utterance and the generalise & merge
	 *
	 * @param probability
	 *            word-creation probability pWC
	 * @param type
	 *            game type
	 * @param change
	 *            boolean to indicate whether the agent is allowed to change the
	 *            grammar (false when testing the population).
	 */
	public Symbols speak(double probability, char type, boolean change) {
		utterance = null;
		utteranceID = 0;
		expression = new IntArray();
		composition = new IntArray();
		interpretation = -1;
		newRule = new String();
		rule = -1;
		lg++;
		if (lg % 10 == 0 && change)
			generaliseAndMerge();

		if (TOPIC != null)// DG was a success
			utterance = produce_utterance(probability, type);
		return utterance;
	}

	/**
	 * Function to actually chunk an expression and forming new compositional
	 * rules
	 *
	 * @param e
	 *            expression to be chunked
	 * @param s
	 *            IntArray containing information how the chunk is to be made,
	 *            e.g. word-order & place
	 *
	 * @return retval index of the new compositional rule
	 */
	private int addSplit(final String e, final IntArray s) {
		// s.get(1) contains the place where the split is to be made
		String head = e.substring(0, s.get(1));
		if (head.equals(""))
			Utils.error("head is empty string, e=" + e + " s=" + s);
		String tail = e.substring(s.get(1));
		if (tail.equals(""))
			Utils.error("tail is empty string, e=" + e + " s=" + s);
		IntArray crossSection = new IntArray();
		// the tail of s contains the meanings that are aligned
		for (int i = 2; i < s.size(); i++)
			crossSection.add(s.get(i));
		int coverCross = 0;
		// calculating the cover (linguistic category!) of this bit
		for (int i = 0; i < crossSection.size(); i++)
			coverCross += ((ANMeaning) ontology.get(crossSection.get(i))).getCover();
		IntArray mHead;
		IntArray mTail;
		int coverHead, coverTail;
		if (s.get(0) == 0) {// alignment is found at the start of the expression
			mHead = crossSection;
			mTail = DCS.complement(crossSection);
			coverHead = coverCross;
			coverTail = maxCover - coverHead;
		} else {// alignment at the end of the expression, different word-order
			mTail = crossSection;
			mHead = DCS.complement(crossSection);
			coverTail = coverCross;
			coverHead = maxCover - coverTail;
		}

		for (Rules2 r : grammar) {
			if (r.matches(coverHead, coverTail)) {
				r.adapt(head, tail, mHead, mTail);
				composition.clear();
				composition.add(r.getNumber());
				composition.addAll(r.getComposition());
				return r.getNumber();
			}
		}
		// for (int i = 0; i < grammar.size(); i++) {

		// if there already exists a rule which divides the holistic
		// conceptual space
		// into the newly proposed spaces, but which do not already
		// contained the new
		// words and/or meanings.
		// then the words and meanings are added to grammar, but no new
		// compositional rule
		// has to be made.
		// return the matching compositional rule
		// if (((Rules2) grammar.get(i)).matches(coverHead, coverTail)) {
		// ((Rules2) grammar.get(i)).adapt(head, tail, mHead, mTail);
		// composition.clear();
		// composition.add(i);
		// composition.addAll(((Rules2) grammar.get(i)).getComposition());
		// return i;
		// }
		// }
		// the chunk has to be made, so the new compositional rule is assigned
		// the index
		// of the current grammar size
		int retval = grammar.size();
		// add the compositional rule
		grammar.addRule(new Rules2(coverHead, coverTail, mHead, mTail, grammar, ontology, maxCover, alphabetSize));
		// add the terminal head rule
		grammar.addRule(new Rules2(coverHead, head, mHead, grammar, ontology, maxCover, alphabetSize));
		// add the terminal tail rule
		grammar.addRule(new Rules2(coverTail, tail, mTail, grammar, ontology, maxCover, alphabetSize));
		// assing the composition
		composition.clear();
		composition.add(retval);
		composition.add(retval + 1);
		composition.add(retval + 2);
		int[] comp = { retval + 1, retval + 2 };
		// setting up the composition internally
		((Rules2) grammar.get(retval)).setComposition(new IntArray(comp));
		// and try to generalise the new terminal rules
		((Rules2) grammar.get(retval)).generalise(head, mHead, coverHead, 1);
		((Rules2) grammar.get(retval)).generalise(tail, mTail, coverTail, 0);
		return retval;
	}

	/**
	 * The function parse implements the decoding of a utterance
	 *
	 * @param u
	 *            the Symbol to be decoded.
	 */
	private void parse(final Symbols u) {
		Rules2 r = null;
		parsed = false;
		// search a parse
		IntArray c;
		for (int i = 0; i < grammar.size(); i++) {
			r = (Rules2) grammar.get(i);
			// if one exists:
			if (r.parse(u.getForm(), DCS, maxCover)) {
				// select the one which highest score.
				if (r.getScore(frequencies, maxCover) > maxScore) {
					maxScore = r.getScore(frequencies, maxCover);
					rule = i;
					c = new IntArray();
					c.add(rule);
					c.addAll(((Rules2) grammar.get(rule)).getComposition());
					composition = new IntArray(c);
					parsed = true;
				}
				r.updateFrequency(frequencies, maxCover);
				// for monitoring reasons, and for a probabilistic
				// implementation
			}
		}
	}

	/**
	 * The function that implements the induction mechanisms
	 *
	 * @param u
	 *            the Symbol (expression) to be induced.
	 */
	private void induce(final Symbols u) {
		Rules2 r = null;
		success = false;
		maxScore = 0.0;
		rule = -1;
		// first see if we can decode the expression partially.
		for (int i = 0; i < grammar.size(); i++) {
			r = (Rules2) grammar.get(i);
			if (r.partialParse(u.getForm(), DCS, maxCover)) {
				// if so, select the best way depending on the score
				if (r.getScore(frequencies, maxCover) > maxScore) {
					maxScore = r.getScore(frequencies, maxCover);
					rule = i;
				}
			}
		}

		if (rule >= 0) {
			// there is a partial parse, now construct a new terminal rule to
			// fill the
			// empty slot
			if (!((Rules2) grammar.get(rule)).exploit(u.getForm(), lg))
				rule = -1;
			if (rule >= 0) {
				composition.clear();
				composition.add(rule);
				composition.addAll(((Rules2) grammar.get(rule)).getComposition());
				newRule = new String("new part ");
			}
		} else {
			// we will try to find a way to chunk the expression

			IntArray s = new IntArray();// represents the way a split (or chunk)
										// can be made
			List splits = new ArrayList();// contains the list of s
			IntArray counterExact = new IntArray();// counts the frequencies for
													// exact splits
			IntArray counterApprox = new IntArray();// counts the freq.for
													// 'approximate' splits

			// first search all instances if it has a way of chunking the
			// utterance
			for (int i = 0; i < instances.size(); i++) {
				s = ((Instance) instances.get(i)).split(u.getForm(), DCS);
				if (s != null)// if we can chunk this instance, add the proposed
								// chunk
					splits.add(s);
			}
			IntArray s1 = new IntArray();
			// now we search all possible chunks for those with maximum
			// frequency
			// this is done by comparing them. (recall that the instances are
			// expression-meaning pairs stored in a list)
			for (int i = 0; i < splits.size() - 1; i++) {
				counterExact.add(1);
				counterApprox.add(1);
				s = (IntArray) splits.get(i);
				for (int j = i + 1; j < splits.size(); j++) {
					if (s.equals((IntArray) splits.get(j)))
						// these two are exactly the same, so increase the exact
						// frequency
						counterExact.increment(i);
					else {
						s1 = (IntArray) splits.get(j);
						if (s.get(0) == s1.get(0) && s.get(1) == s1.get(1))
							// only word order (s[0]) and position (s[1]) are
							// the same
							// so they are 'approximately' the same, e.g. when
							// utterance="ab"
							// rule for s had expression "ac" and for s1 this
							// was "ad"
							counterApprox.increment(i);
					}
				}
			}
			int maxExact = 0;
			int maxApprox = 0;
			int exact = -1;
			int approx = -1;
			for (int i = 0; i < counterExact.size(); i++) {
				// select the one with highest frequencies, where the
				// approximate
				// chunks are weighted half of the exact chunks
				if ((counterExact.get(i) + 0.0 * counterApprox.get(i)) > maxExact
						|| (counterExact.get(i) == maxExact && Math.random() < 0.5)) {// weight
																						// was
																						// 0.5
					exact = i;
					maxExact = (int) Math.floor((double) counterExact.get(i) + 0.5 * (double) counterApprox.get(i));
				}

			}
			if (maxExact > 1) {// add the chunk
				rule = addSplit(u.getForm(), (IntArray) splits.get(exact));
				newRule = new String("newC ");// for UI
			} else {// add holistic rule
				for (int i = 0; i < grammar.size() && rule < 0; i++) {
					r = (Rules2) grammar.get(i);
					// check if the word does not yet exists, if it does, the
					// meanings
					// are added here:
					if (r.wordMatch(u.getForm(), DCS))
						rule = i;
					newRule = new String("adapt ");
				}
				if (rule < 0) {// add the new rule
					rule = grammar.size();
					grammar.addRule(new Rules2(maxCover, u.getForm(), DCS, grammar, ontology, maxCover, alphabetSize));
					nHolisticRules++;
					newRule = new String("newH ");
				}
				composition.clear();
				composition.add(rule);
			}
		}

		if (rule >= 0) {// update the frequencies for monitoring & probabilistic
						// parsing
			// and assign parts for UI
			if (!composition.isEmpty())
				composition.remove(0);
			((Rules2) grammar.get(rule)).setComposition(new IntArray(composition));
			((Rules2) grammar.get(rule)).updateFrequency(frequencies, maxCover);
			parts = ((Rules2) grammar.get(rule)).getParts();
			composition.add(rule);
		}
	}

	/**
	 * This function calls itself with an extra argument (induce=true)
	 *
	 * @param u
	 *            the symbol to be guessed
	 * @param type
	 *            the score-update type (not used)
	 */
	public Symbols guess(Symbols u, char type) {
		return guess(u, type, true);
	}

	/**
	 * The function for guessing the reference of an utterance
	 *
	 * @param u
	 *            the symbol to be guessed
	 * @param type
	 *            the score-update type (not used)
	 * @param induce
	 *            boolean to indicate whether learning is on or off (=false
	 *            during testing of the population)
	 */
	public Symbols guess(Symbols u, char type, boolean induce) {
		if (u.cover != 15) {
			System.out.println("different cover++++");
			System.out.println(u.cover);
		}
		utterance = null;
		interpretation = -1;
		rule = -1;
		Rules2 r = null;
		int match = -1;
		int partMatch = -1;
		int partMatchForm = -1;
		maxScore = -1.;
		double maxPartScore = 0.0;
		double maxPartFormScore = 0.0;
		int partInterpretation = -1;
		int partRule = -1;
		int partInterpretationForm = -1;
		int partFormRule = -1;
		int oldRule = -1;
		parts = new String[0];
		newRule = new String();
		lg++;
		composition = new IntArray();

		// call generalise and merge
		// I do it first to prevent structures to be destroyed here
		// I do it once in 10 games to speed up the simulation
		if (lg % 10 == 0 && induce)
			generaliseAndMerge();

		// if there is no utterance, nothing need to be done
		if (u == null)
			return null;

		// first initialise some internal structures of the rules.
		for (Rules2 r_ : grammar) {
			r_.initGame();
		}

		// search for each distinctive category found in the context
		// if we can parse the utterance.
		for (int t = 0; t < setOfDCS.size(); t++) {
			DCS = (IntArray) setOfDCS.get(t);
			setTopic(t);
			if (lg >= noLearning && !DCS.isEmpty()) {// (at least if learning is
														// allowed)
				parse(u);
				if (oldRule != rule) {// successfully parsed and the (new) rule
										// has
					// a higher score than the old one, so:
					interpretation = t;
					oldRule = rule;
				}
			}
		}
		if (interpretation >= 0) {// successful parse, setting some things for
									// future references
			topicID = interpretation;
			DCS = (IntArray) setOfDCS.get(topicID);
			setTopic(topicID);
			if (!composition.isEmpty())
				composition.remove(0);
			else
				Utils.error("composition disappeared early");
			((Rules2) grammar.get(rule)).setComposition(new IntArray(composition));
			parts = ((Rules2) grammar.get(rule)).getParts();
			utterance = new Symbols(rule, u.getForm());
			composition.add(0, rule);
		} else {
			DCS = null;
			setTopic(-1);
			utterance = null;// just to be sure
		}
		return utterance;
	}

	/**
	 * Function that calls itself with an extra argument (induce=true)
	 *
	 * @param u
	 *            the symbol to be guessed
	 * @param type
	 *            the score-update type (not used)
	 */

	public Symbols hear(Symbols u, char type) {
		return hear(u, type, true);
	}

	/**
	 * Function for the hearer part of the observational game.
	 *
	 * @param u
	 *            the symbol to be guessed
	 * @param type
	 *            the score-update type (not used)
	 * @param induce
	 *            boolean to indicate whether learning is on or off (=false
	 *            during testing of the population)
	 */

	public Symbols hear(Symbols u, char type, boolean induce) {// obs. game
		utterance = null;
		interpretation = -1;
		rule = -1;
		Rules2 r = null;
		int match = -1;
		int partMatch = -1;
		int partMatchForm = -1;
		maxScore = -1.;
		double maxPartScore = 0.0;
		double maxPartFormScore = 0.0;
		int partInterpretation = -1;
		int partRule = -1;
		int partInterpretationForm = -1;
		int partFormRule = -1;
		newRule = new String();
		parts = new String[0];
		lg++;
		composition = new IntArray();
		if (lg % 10 == 0 && induce)
			generaliseAndMerge();
		if (u == null || DCS.isEmpty())
			return null;

		for (int i = 0; i < grammar.size(); i++)
			((Rules2) grammar.get(i)).initGame();

		if (lg >= noLearning) {// pre-linguistic period is over
			parse(u);
		}

		if (rule >= 0) {// parse was successful
			if (!composition.isEmpty())
				composition.remove(0);
			((Rules2) grammar.get(rule)).setComposition(new IntArray(composition));
			parts = ((Rules2) grammar.get(rule)).getParts();
			utterance = new Symbols(rule, u.getForm());
			composition.add(0, rule);
		} // otherwise induce new knowledge
		else if (induce)
			induce(u);

		return utterance;
	}

	/**
	 * Function that implements the generalise and merge step of the induction
	 * Ugly!
	 */
	private void generaliseAndMerge() {
		int i = 0, j = 0;
		Rules2 r;
		boolean changed = false;
		while (i < grammar.size() - 1) {
			r = (Rules2) grammar.get(i);
			j = i + 1;
			while (j < grammar.size()) {
				// if r.generaliseAndMerge return true, the rule should be
				// destroyed
				if (!r.generaliseAndMerge(j))
					j++;
				else {
					if (((Rules2) grammar.get(j)).getHolistic())
						nHolisticRules--;
					grammar.recordMerged(r, j);
					grammar.remove(j);
					changed = true;
				}
			}
			i++;
		}
		if (changed)
			grammar.resetNumbers();
	}

	/**
	 * Function to adapt the scores for the speaker
	 *
	 * @param u
	 *            utterance of the hearer
	 * @param T
	 *            the topic
	 * @param type
	 *            the type of game
	 * @param utype
	 *            the score-update type
	 */
	public void adaptLexiconSpeaker(final Symbols u, int T, char type, char utype) {
		success = false;

		if (u != null) {// LG success
			success = true;

			for (int i = 0; i < grammar.size(); i++) {
				// excitation
				if (composition.contains(i))
					((Rules2) grammar.get(i)).updateScore(true);
				// lateral inhibition
				else
					((Rules2) grammar.get(i)).updateScore(false);
			}
		}
	}

	/**
	 * Function to adapt the scores for the hearer
	 *
	 * @param u
	 *            utterance of the speaker
	 * @param T
	 *            the topic
	 * @param type
	 *            the type of game
	 * @param utype
	 *            the score-update type
	 */
	public void adaptLexiconHearer(final Symbols u, int T, char type, char utype) {
		success = false;

		if (utterance != null) {
			if (type == 'o' || topic == T) {// LG success
				success = true;

				for (int i = 0; i < grammar.size(); i++) {
					// excitation
					if (composition.contains(i))
						((Rules2) grammar.get(i)).updateScore(true, DCS);
					// lateral inhibition
					else
						((Rules2) grammar.get(i)).updateScore(false, DCS);
				}
			} else if (type == 'g' && topic != T) {
				// in case of a mismatch in topic (GG), inhibit weights
				for (int i = 0; i < composition.size(); i++)
					((Rules2) grammar.get(composition.get(i))).updateScore(false, DCS);
			}
		}

		if (!success && type == 'g') {
			// in case of failure and when play guessing game
			int oldTopic = topic;
			// 'speaker informs' the hearer about the topic (corrective
			// feedback)
			DCS = (IntArray) setOfDCS.get(T);
			setTopic(T);
			// we still need to induce the speaker's expression

			if (!DCS.isEmpty())
				induce(u);

			// here we will update the instance-base before the proper reference
			// of the
			// DCS is destroyed.
			if (u != null && !DCS.isEmpty())
				instances.add(new Instance(u.getForm(), DCS));

			// set the previously selected topic back (mismatch in reference)
			// this is to tell the statistics machine the game failed.
			if (oldTopic >= 0) {
				DCS = (IntArray) setOfDCS.get(oldTopic);
				setTopic(oldTopic);
			} else
				topic = oldTopic;
		} else if (u != null && !DCS.isEmpty())
			instances.add(new Instance(u.getForm(), DCS));

		if (utterance != null && (utterance.getForm()).equals(""))
			Utils.error("something went wrong");
	}

	/**
	 * Return the encoded, decoded or induced rule
	 */
	public Rules2 getRule() {
		if (rule >= 0)
			return (Rules2) grammar.get(rule);
		return null;
	}

	/**
	 * Returns a string "new " in case a new rule was added to the grammar,
	 * otherwise the string is empty. Used in UI
	 */

	public String getNewRule() {
		return newRule;
	}

	/**
	 * Returns the utterance in its parts. Used in UI.
	 */
	public String[] getParts() {
		return parts;
	}

	/**
	 * Returns the entire grammar. Used in the UI (showLexicon)
	 */
	public List getRules() {
		return new ArrayList(grammar.getRules());
	}

	/**
	 * This function is not implemented...
	 */
	public void playDGame(char type, char uType, boolean b) {
	}

	/**
	 * This function lets the agent play a discrimination game
	 *
	 * @param type
	 *            not used
	 * @param uType
	 *            type of method with prototypes are shifted (default:
	 *            centre-of-mass)
	 * @param n
	 *            language game number
	 * @param adapt
	 *            whether or not the learning is on or off.
	 */
	public void playDGame(char type, char uType, int n, boolean adapt) {
		TOPIC = null;
		DCS = new IntArray();
		int dcs;
		lg = n;
		if (cxt == null)
			System.out.println("context null");
		if (ontology == null)
			System.out.println("ontol null");
		topicID = discriminationGame.playGame(cxt, topic, lg, adapt);

		if (topicID >= 0) {
			DCS = discriminationGame.getDCS(topic);
			TOPIC = discriminationGame.getTopic(topic);
			nrDGSuccess++;
		}
		setOfDCS.add(DCS);
		DG++;
	}

	/**
	 * Returns the Meaning representation of the topic.
	 */
	public Meaning getTOPIC() {
		return TOPIC;
	}

	/**
	 * Function to clear the initial set of distinctive categories
	 */
	public void initDCS() {
		setOfDCS.clear();
	}

	/**
	 * Function to set the distinctive category. Used in a test function with
	 * predefined semantics
	 */
	public void setDCS(final IntArray dcs) {
		DCS = dcs;
		topicID = 1;
	}

	/**
	 * Function used to set the topic, the distinctive category and the Meaning
	 * representation of the topic.
	 */
	public void setTopic(int t) {
		if (t >= 0) {
			topic = t;
			if (DCS != null) {
				double[] x = new double[DCS.size()];
				for (int i = 0; i < DCS.size(); i++)
					x[i] = ((ANMeaning) ontology.get(DCS.get(i))).avgCenter();
				TOPIC = new Meaning(x, t);
			} else
				TOPIC = null;
		} else {
			topic = -1;
			TOPIC = null;
		}
	}

	/**
	 * Function to set the rule index. Used to reset the rule when necessary.
	 */
	public void setRule(int r) {
		rule = r;
	}

	/**
	 * Function used to calculate the similarity measure. It compares the
	 * grammar of this agent with that of the other (which is typically an
	 * adult).
	 * <p>
	 * Basically, it calculates the fraction of this agent's grammar that
	 * coincides with the grammar of the adult
	 *
	 * @param adult
	 *            The other agent
	 */
	public double compareAgents(final CompositionalAgent2 adult) {
		int sum = 0;
		for (int i = 0; i < grammar.size() - 1; i++)
			if (((Rules2) grammar.get(i)).similar(adult.grammar))
				sum++;
		return (double) sum / (double) grammar.size();
	}

	/**
	 * Constructs a string that can be written to the logfile
	 */
	public String getGame() {
		if (rule >= 0)
			return ((Rules2) grammar.get(rule)).toString();
		return dummyRule.toString();
	}

	/**
	 * Returns the size of the grammar
	 */
	public int getGrammarSize() {
		return grammar.size();
	}

	/**
	 * Returns the number of holistic rules in the grammar.
	 */
	public int getHolistic() {
		return nHolisticRules;
	}

	/**
	 * Returns a 1 if the found rule is compositional.
	 */
	public double getCompositionSuccess() {
		if (rule >= 0 && ((Rules2) grammar.get(rule)).getCompositional())
			return 1.0;
		return 0.0;
	}

	/**
	 * Returns the decoded or encoded utterance.
	 */
	public String getUtterance() {
		if (utterance != null)
			return utterance.getForm();
		return null;
	}

	/**
	 * Returns the discrimination success in this game
	 */
	public double getDS() {
		double retval;
		if (DG > 0)
			retval = (double) nrDGSuccess / (double) DG;
		else
			retval = 0.0;
		nrDGSuccess = 0;
		DG = 0;
		return retval;
	}

	/**
	 * Returns the ontology
	 */
	public List getOntology() {
		return ontology;
	}

	/**
	 * printGrammar() is used to print the agent's grammar into a file
	 */

	public void printGrammar(PrintWriter ofile, char type) {

//		Collections.sort(grammar.getRules());
		ofile.println("A" + id + ": nSymbols=" + lexicon.size() + " nMeanings=" + ontology.size() + " nRules2="
				+ grammar.size());
		ofile.println();
		ofile.println("grammar:");
		int maxUse = 0;
		int dominant = -1;
		int nComp = 0;
		int nHol = 0;
		int counter = 0;
		for (Rules2 r : grammar) {
			if (r.getFrequency() > 0)
				ofile.println(r);
			if (r.getFrequency() > maxUse && r.isSentence()) {
				dominant = counter;
				maxUse = r.getFrequency();
			}
			if (r.getCompositional())
				nComp++;
			else if (r.getHolistic())
				nHol++;
			counter++;
		}

		ofile.println();
		ofile.println("ontology:");
		for (int j = 0; j < ontology.size(); j++)
			ofile.println(j + ": " + (ANMeaning) ontology.get(j));

		ofile.println();
		ofile.println();
		System.out.print("GR a" + id + " " + grammar.size() + " " + nComp + " " + nHol + " ");
		if (dominant >= 0)
			System.out.println(((Rules2) grammar.get(dominant)).printSH());
		else
			System.out.println("nil");
	}

	/**
	 * Prints some statistics about the grammar to the standard output. The
	 * prefix of "GR" allows the user to search the output for this information
	 * (e.g. by using `grep')
	 */
	public void printGrammarStats() {
		int maxUse = 0;
		int dominant = -1;
		int nComp = 0;
		int nHol = 0;
		Rules2 r;
		for (int i = 0; i < grammar.size(); i++) {
			r = (Rules2) grammar.get(i);
			if (r.getFrequency() > maxUse && r.isSentence()) {
				dominant = i;
				maxUse = r.getFrequency();
			}
			if (r.getCompositional())
				nComp++;
			else if (r.getHolistic())
				nHol++;
		}
		System.out.print("GR a" + id + " " + grammar.size() + " " + nComp + " " + nHol + " ");
		if (dominant >= 0)
			System.out.println(((Rules2) grammar.get(dominant)).printSH());
		else
			System.out.println("nil");
	}

	/**
	 * Prints the grammar to the standard output.
	 */
	public void printGrammar() {
//		Collections.sort(grammar.getRules());
		System.out.println("A" + id + " nMeanings=" + ontology.size() + " nRules2=" + grammar.size());
		System.out.println();
		System.out.println("grammar:");
		for (Rules2 r : this.grammar)
			System.out.println(r);
		System.out.println();
		System.out.println("ontology:");
		for (int j = 0; j < ontology.size(); j++)
			System.out.println((ANMeaning) ontology.get(j));

		System.out.println();
		System.out.println();
	}

	/**
	 * This function is not implemented
	 */
	public void print(PrintWriter ofile, char type) {
	}

	public Grammar getGrammar() {
		return grammar;
	}

}
