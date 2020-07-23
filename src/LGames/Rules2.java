package LGames;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Util.DoubleArray;
import Util.IntArray;
import Util.Utils;

public class Rules2{

	private char topNode = 'x';
	private int totalCover = 0;
	public int[] covers = new int[1];
	private IntArray meanings = new IntArray();
	protected IntArray use;
	protected DoubleArray score;
	protected String[] forms;
	public String expression;
	protected final double eta = 0.1;
	protected double maxScore = 0.0;
	private int interpretation = -1;
	protected IntArray[] M;
	protected IntArray tmpMeanings = new IntArray();
	protected final String alphabet = new String("abcdefghijklmnopqrstuvwxyz");
//	protected int alphabetSize = alphabet.length();
	protected int alphabetSize = 26;
	private IntArray composition = new IntArray();
	private Grammar<Rules2> grammar;
	private List ontology;
	private List instances;
	private Meaning mShared = null;
	private Meaning mUnshared = null;
	private Meaning dUnshared = null;
	private String fShared = null;
	private IntArray iShared = new IntArray();
	private IntArray iUnshared = new IntArray();
	private IntArray idUnshared = new IntArray();
	private int i0 = 0;
	private int i1 = 1;
	private int maxCover = 15;
	private boolean changed = true;
	private int weights = 0;
	protected int totalUse = 0;
	private int timestamp = -1;
	protected IntArray[] tmpGroup = new IntArray[2];
	protected int[] tmpCovers = new int[2];
	protected IntArray group = new IntArray();
	private IntArray crossSection = new IntArray();
	private IntArray complement = new IntArray();
	private final int maxWordLength = 10;
	private double zipfNorm = 0.0;
	protected int frequency = 0;
	public double iScore = 0.001;
	protected boolean parsed = false;
	public double currentScore = 0.0;
	public int number;
	private static Map<Integer, String> coverRepresentations = fillCoverRepresentations();

	private static Map<Integer, String> fillCoverRepresentations() {
		Map<Integer, String> representations = new HashMap<Integer, String>();
		representations.put(63, "rgbSXY");
		representations.put(1, "r");
		representations.put(2, "g");
		representations.put(4, "b");
		representations.put(8, "S");
		representations.put(12, "XY");
		representations.put(18, "gX");
		representations.put(36, "bY");
		return representations;
	}

	/**
	 * Empty constructor
	 */
	public Rules2() {
		expression = new String("nil");

	}

	/**
	 * Copy constructor
	 */
	public Rules2(final Rules2 r) {
		alphabetSize = r.alphabetSize;
		expression = r.expression;
		meanings = r.meanings;
		group = r.group;
		M = r.M;
		tmpGroup[0] = new IntArray();
		tmpGroup[1] = new IntArray();
		use = r.use;
		score = r.score;
		totalCover = r.totalCover;
		covers = r.covers;
		grammar = r.grammar;
		ontology = r.ontology;
		maxCover = r.maxCover;
		number = r.number;
	}

	/**
	 * Constructor for inventing a holistic rule/expression
	 *
	 * @param DCS
	 *            the meaning of the new rule.
	 * @param cov
	 *            the cover of the new rule (recall cover is a
	 *            bit-representation for the conceptual space and acts as the
	 *            terminal label of this rule)
	 * @param g
	 *            a pointer to the agent's grammar (this helps for searching)
	 * @param o
	 *            a pointer to the agent's ontology (this helps for searching)
	 * @param mc
	 *            the maximum cover, ie. the conceptual space of the sentence
	 * @param ab
	 *            the alphabet size
	 */
	public Rules2(final IntArray DCS, int cov, Grammar g, List o, int mc, int ab) {

		alphabetSize = ab;
		expression = createForm();
		meanings = new IntArray(DCS);
		meanings.removeDoubles();
		group = new IntArray(DCS.size());
		M = new IntArray[2];
		M[0] = new IntArray(meanings);
		M[1] = new IntArray();
		tmpGroup[0] = new IntArray();
		tmpGroup[1] = new IntArray();
		use = new IntArray(meanings.size());
		score = new DoubleArray(meanings.size(), 0.01);
		totalCover = cov;
		covers = new int[1];
		covers[0] = cov;
		grammar = g;
		ontology = o;
		maxCover = mc;
	}

	/**
	 * Constructor for adopting/incorporating an expression in a terminal
	 * (possibly holistic) rule
	 *
	 * @param cov
	 *            the cover of the new rule (recall cover is a
	 *            bit-representation for the conceptual space and acts as the
	 *            terminal label of this rule)
	 * @param f
	 *            the expression
	 * @param DCS
	 *            the meaning of the new rule.
	 * @param g
	 *            a pointer to the agent's grammar (this helps for searching)
	 * @param o
	 *            a pointer to the agent's ontology (this helps for searching)
	 * @param mc
	 *            the maximum cover, ie. the conceptual space of the sentence
	 * @param ab
	 *            the alphabet size
	 */
	public Rules2(final int cov, final String f, final IntArray DCS, Grammar g, List o, int mc, int ab) {
		alphabetSize = ab;
		expression = new String(f);
		meanings = new IntArray(DCS);
		meanings.removeDoubles();
		group = new IntArray(DCS.size());
		M = new IntArray[2];
		M[0] = new IntArray(meanings);
		M[1] = new IntArray();
		tmpGroup[0] = new IntArray();
		tmpGroup[1] = new IntArray();
		use = new IntArray(meanings.size());
		score = new DoubleArray(meanings.size(), 0.01);
		totalCover = cov;
		covers = new int[1];
		covers[0] = cov;
		grammar = g;
		ontology = o;
		maxCover = mc;
	}

	/**
	 * Constructor for making a compositional rule
	 *
	 * @param cHead
	 *            the cover of the first constituent
	 * @param cTail
	 *            the cover of the second constituent
	 * @param mHead
	 *            the possible meanings of the first constituent (this speeds up
	 *            searching)
	 * @param mTail
	 *            the possible meanings of the second constituent (this speeds
	 *            up searching)
	 * @param g
	 *            a pointer to the agent's grammar (this helps for searching)
	 * @param o
	 *            a pointer to the agent;s ontology (this helps for searching)
	 * @param mc
	 *            the maximum cover, ie. the conceptual space of the sentence
	 * @param ab
	 *            the alphabet size
	 */

	public Rules2(final int cHead, final int cTail, final IntArray mHead, final IntArray mTail, Grammar g, List o,
			int mc, int ab) {
		alphabetSize = ab;
		meanings = new IntArray(mHead);
		meanings.addAll(mTail);
		group = new IntArray(meanings.size());
		M = new IntArray[2];
		M[0] = new IntArray(mHead);
		M[1] = new IntArray(mTail);
		for (int i = 0; i < meanings.size(); i++)
			if (i < mHead.size())
				group.set(i, 0);
			else
				group.set(i, 1);
		tmpGroup[0] = new IntArray();
		tmpGroup[1] = new IntArray();
		use = new IntArray(meanings.size());
		score = new DoubleArray(meanings.size(), 0.01);
		totalCover = mc;
		covers = new int[2];
		covers[0] = cHead;
		covers[1] = cTail;
		grammar = g;
		ontology = o;
		maxCover = mc;
	}

	/**
	 * Initialise the rule for a new language game.
	 */
	public void initGame() {
		tmpGroup[0] = new IntArray();
		tmpGroup[1] = new IntArray();
		tmpCovers = new int[2];
		tmpMeanings = new IntArray();
		maxScore = 0.0;
		currentScore = 0.0;
		parsed = false;
		composition.clear();
		if (covers.length == 2)
			expression = null;
	}

	/**
	 * Implements Zipf's law for the word lengths. The frequency of a word with
	 * length L is proportional to 1/L.
	 *
	 * @return wordLength
	 */
	private int zipf() {
		double zipfNorm = 0;
		for (int i = 1; i < maxWordLength - 1; i++)
			zipfNorm += 1.0 / (double) i;
		double x = Math.random();
		double p = 0.0;
		for (int i = 1; i < maxWordLength - 2; i++) {
			p += 1. / ((double) i * zipfNorm);
			if (x <= p)
				return i + 2;
		}
		return maxWordLength;
	}

	/**
	 * This function creates a new form with a length decided following Zipf's
	 * law and the letters are randomly selected from the alphabet between index
	 * 0 and alphabetSize
	 *
	 * @return the invented expression
	 */
	public String createForm() {
		int l = zipf();
		char[] form0 = new char[l];

		for (int i = 0; i < l; i++) {
			form0[i] = alphabet.charAt((int) (alphabetSize * Math.random()));
		}
		return new String(form0);
	}

	/**
	 * This function adds category features to the IntArray meanings, removes
	 * any doubles and sets initial values to scores and usage, and indicates to
	 * which constituent (group) the categories belong.
	 *
	 * @param m
	 *            the IntArray of category features
	 * @param n
	 *            the constituent to which these belong
	 */
	public void addMeaning(IntArray m, int n) {
		// Only create meaning sets if rule has two constituents

		meanings.addAll(m);
		meanings.removeDoubles();
		int diff = meanings.size() - score.size();
		if (diff > 0) {
			score.addAll(new DoubleArray(diff, 0.01));
			use.addAll(new IntArray(diff, 0));
			group.addAll(new IntArray(diff, n));
		}

	}

	/**
	 * Function to see if the rule is compositional, and if so, if its
	 * constituents coincide with cHead and cTail. This is used by the
	 * compositional agent in the chunking process (see addSplit())
	 *
	 * @param cHead
	 *            the cover of the head (1st constituent)
	 * @param cTail
	 *            the cover of the tail (2nd constituent)
	 *
	 * @return true in case this rule is compositional and starts with cHead and
	 *         ends with cTail
	 */
	public boolean matches(final int cHead, final int cTail) {
		if (covers.length == 2 && covers[0] == cHead && covers[1] == cTail)
			return true;
		return false;
	}

	/**
	 * Function to add the meanings of the head and tail the proposed chunk in
	 * case the function matches returned true.
	 *
	 * @param head
	 *            the string of the first constituent
	 * @param tail
	 *            the string of the second constituent
	 * @param mHead
	 *            the meanings of the first constituent
	 * @param mTail
	 *            the meanings of the second constituent
	 */
	public void adapt(final String head, final String tail, final IntArray mHead, final IntArray mTail) {
		addMeaning(mHead, 0);
		addMeaning(mTail, 1);
		composition.clear();
		composition.add(grammar.size());
		grammar.addRule(new Rules2(covers[0], head, mHead, grammar, ontology, maxCover, alphabetSize));
		composition.add(grammar.size());
		grammar.addRule(new Rules2(covers[1], tail, mTail, grammar, ontology, maxCover, alphabetSize));
	}

	/**
	 * Function that calls itself with an additional argument (lg=-1)
	 */
	public boolean match(final IntArray DCS, int totCover) {
		return match(DCS, totCover, -1);
	}

	/**
	 * Function to check whether the semantics of the rule matches the meaning
	 * of the distinctive categories.
	 *
	 * @param DCS
	 *            the distinctive categories
	 * @param totCover
	 *            the linguistic category that is currently searched
	 * @param lg
	 *            language game number used as a time stamp
	 *
	 * @return true when this rule matches, false when not
	 */
	public boolean match(final IntArray DCS, int totCover, int lg) {
		interpretation = -1;
		if (lg != timestamp) {
			parsed = false;
			timestamp = lg;
		}

		// The rule's linguistic category must coincide with totCover
		// and the entire DCS must match
		if (totalCover != totCover || !meanings.containsAll(DCS))
			return false;
		// possible match
		// add DCS to tmpMeanings (current meaning of the rule)
		tmpMeanings.addAll(DCS);
		maxScore = 1.0;
		int inter = -1;
		for (int i = 0; i < tmpGroup.length; i++)
			tmpGroup[i].clear();
		for (int i = 0; i < DCS.size(); i++) {
			// assign groups (linguistic categories) to individual categorical
			// features
			inter = meanings.indexOf(DCS.get(i));
			tmpGroup[group.get(inter)].add(DCS.get(i));
		}

		if (covers.length > 1) {// this is a compositional rule. We need to find
			// which rules match
			String expr = new String();
			int n = 0;
			Rules2 r = null;
			double mScore;
			composition = new IntArray(2, -1);
			while (n < 2) {// assuming only 2 constituents
				mScore = 0.0;
				for (int i = 0; i < grammar.size(); i++) {
					r = (Rules2) grammar.get(i);
					if (r != this) {
						// some recursion
						if (r.match(tmpGroup[n], covers[n])) {
							if (r.getScore() >= mScore) {
								// we want to select the best
								composition.set(n, i);
								mScore = r.getScore();
							}
						}
					}
				}
				if (composition.get(n) < 0)
					return false;
				maxScore = maxScore * mScore;
				// If either of these things happen, there might be a bug...not
				// sure yet,
				if (grammar == null)
					Utils.error("grammar does not exist?");
				if (expr == null) {
					expr = new String();
					System.out.println("expression was null: e=" + expr);
				}
				if (composition == null)
					Utils.error("composition does not exist?");
				// construct the expression
				String s = ((Rules2) grammar.get(composition.get(n))).getExpression();
				if (s == null)
					return false;
				expr = new String(expr.concat(s));
				n++;
			}
			expression = new String(expr);
		}

		parsed = true;
		currentScore = iScore * meaningScore() * maxScore;
		return true;
	}

	/**
	 * This function checks if the topic's meaning (DCS) matches a part of the
	 * semantics of this rule
	 *
	 * @param DCS
	 *            the meaning of the topic
	 * @param totCover
	 *            the linguistic category/conceptual space under consideration
	 *
	 * @return true if the rule partially matches, otherwise it returns false
	 */

	public boolean partialMatch(final IntArray DCS, int totCover) {
		interpretation = -1;

		// setting the cross section between DCS and the rules' categorical
		// features
		// not entirely sure what it does...
		crossSection = DCS.crossSection(meanings);

		// initialising the composition
		composition = new IntArray(2, -1);

		// if the conceptual space of this rule is not equal to the conceptual
		// space of
		// the topic, return false
		// or if the rule is a terminal node, then the meaning cannot match
		// partially
		if (totalCover != totCover || crossSection.isEmpty() || covers.length == 1)
			return false;
		maxScore = 0.0;
		double mScore = 0.0;
		tmpMeanings.clear();
		int c = 0;
		for (int i = 0; i < tmpGroup.length; i++)
			tmpGroup[i].clear();

		// first we consider the first constituent.
		crossSection = DCS.crossSection(M[0]);
		if (!crossSection.isEmpty()) {// there is a possible match.
			// just check whether the matching part covers all dimensions of the
			// conceptual space of this constit.
			c = 0;
			for (int i = 0; i < crossSection.size(); i++)
				c += ((ANMeaning) ontology.get(crossSection.get(i))).getCover();
			if (c != covers[0])
				return false;// conceptual spaces differ
			for (int i = 0; i < grammar.size(); i++) {// search which rules
														// match here.
				if (((Rules2) grammar.get(i)).match(crossSection, covers[0])) {
					if (((Rules2) grammar.get(i)).getScore() > mScore) {
						interpretation = i;
						mScore = ((Rules2) grammar.get(i)).getScore();
					}
				}
			}
		}
		if (interpretation >= 0) {// set and verify the complement of the
									// partial match
			complement = DCS.complement(crossSection);
			c = 0;
			for (int i = 0; i < complement.size(); i++)
				c += ((ANMeaning) ontology.get(complement.get(i))).getCover();
			if (c != covers[1])
				return false;
			if (complement.isEmpty())
				return false;
			// the following is for future reference
			tmpGroup[0] = crossSection;
			tmpGroup[1] = complement;
			// the match (alignment) is at i0=0, the complement at i1=1
			i0 = 0;
			i1 = 1;
			composition.set(i0, interpretation);
		} else {// now we do the same for the second constituent
			crossSection = DCS.crossSection(M[1]);
			if (crossSection.isEmpty())
				return false;
			c = 0;
			for (int i = 0; i < crossSection.size(); i++)
				c += ((ANMeaning) ontology.get(crossSection.get(i))).getCover();
			if (c != covers[1])
				return false;
			for (int i = 0; i < grammar.size(); i++) {
				if (((Rules2) grammar.get(i)).match(crossSection, covers[1])) {
					if (((Rules2) grammar.get(i)).getScore() > mScore) {
						interpretation = i;
						mScore = ((Rules2) grammar.get(i)).getScore();
					}
				}
			}
			if (interpretation < 0)
				return false;
			else {
				complement = DCS.complement(crossSection);
				c = 0;
				for (int i = 0; i < complement.size(); i++)
					c += ((ANMeaning) ontology.get(complement.get(i))).getCover();
				if (c != covers[0])
					return false;
				if (complement.isEmpty())
					return false;
				tmpGroup[0] = complement;
				tmpGroup[1] = crossSection;
				// the match (alignment) is at i0=1, the complement at i1.0.1$
				i0 = 1;
				i1 = 0;
				composition.set(i0, interpretation);
			}
		}
		tmpMeanings = new IntArray(DCS);
		currentScore = iScore * meaningScore() * mScore;
		return true;
	}

	/**
	 * If we have a partial match, this function invents the non-covering part
	 * and produces and expression
	 *
	 * @return the expression, or if something went wrong nonetheless it returns
	 *         null
	 */
	public String produceExpression() {
		Rules2 r = null;
		double mScore = 0.0;
		maxScore = 0.0;
		composition = new IntArray(2, -1);
		// the matching part was constituent with index .0.1$
		for (int i = 0; i < grammar.size(); i++) {
			r = (Rules2) grammar.get(i);
			if (r != this) {
				if (r.match(tmpGroup[i0], covers[i0])) {
					if (r.getScore() >= mScore) {
						mScore = r.getScore();
						composition.set(i0, i);
					}
				}
			}
		}
		if (composition.get(i0) == -1)
			return null;
		// the new part belongs to constituent i1
		addMeaning(tmpGroup[i1], i1);
		grammar.addRule(new Rules2(tmpGroup[i1], covers[i1], grammar, ontology, maxCover, alphabetSize));
		composition.set(i1, grammar.size() - 1);
		maxScore = mScore * ((Rules2) grammar.get(grammar.size() - 1)).getScore();
		expression = ((Rules2) grammar.get(composition.get(0))).getExpression();
		expression = expression.concat(((Rules2) grammar.get(composition.get(1))).getExpression());
		currentScore = iScore * meaningScore() * maxScore;
		return expression;
	}

	/**
	 * The function that implements the parsing/decoding part for terminal
	 * nodes.
	 *
	 * @param f
	 *            the string to be parsed
	 * @param m
	 *            the meaning of this constituent
	 * @param cov
	 *            the linguistic category of this constituent
	 * @param n
	 *            integer indicating the place of the constituent (0-first or
	 *            1-second)
	 * 
	 * @return true if the parse is there.
	 */
	private boolean parse(final String f, final IntArray m, final int cov, final int n) {
		tmpMeanings.clear();
		if (totalCover != cov || expression == null)
			return false;
		if (n == 0) {// first constituent
			if (!f.startsWith(expression))
				return false;
		} else if (!f.equals(expression))
			return false;
		// now that the expression is part of the utterance, check the
		// semantics:
		return match(m, cov);
	}

	/**
	 * The function that implements the parsing/decoding part for holistic or
	 * non-terminal nodes.
	 *
	 * @param f
	 *            the string to be parsed
	 * @param DCS
	 *            the meaning of this string
	 * @param totCover
	 *            the linguistic category of this string
	 * 
	 * @return true if the parse is there.
	 */
	public boolean parse(final String f, final IntArray DCS, int totCover) {

		if (totalCover != totCover)
			return false;// not the right linguist. category
		composition.clear();
		if (expression != null && !expression.equals(f))
			return false;// the expressions don't match
		if (!meanings.containsAll(DCS))
			return false;// the semantics don't match

		// set the meaning to DCS
		tmpMeanings.addAll(DCS);
		if (expression != null && expression.equals(f) && covers.length == 1) {
			// now the parse is successful, it is holistic
			currentScore = iScore * meaningScore();
			parsed = true;
			return true;
		}

		// parse the different constituents
		tmpGroup[0].clear();
		tmpGroup[1].clear();
		for (int i = 0; i < DCS.size(); i++)// group the semantics under the
											// constituents
			tmpGroup[group.get(meanings.indexOf(DCS.get(i)))].add(DCS.get(i));

		int n = 0;
		double mScore;
		Rules2 r = null;
		String f0 = new String(f);
		composition = new IntArray(2, -1);
		while (n < 2) {// assuming max 2 constituents
			mScore = 0.0;
			for (int i = 0; i < grammar.size(); i++) {
				r = (Rules2) grammar.get(i);
				// try to parse this bit with the rule for constituent n
				if (r != this && r.parse(f0, tmpGroup[n], covers[n], n)) {
					// select the best scoring one for this constituent
					if (r.getScore() > mScore || (r.getScore() == mScore && Math.random() >= 0.0 && mScore > 0.0)) {
						composition.set(n, i);
						mScore = r.getScore();
					}
				}
			}
			if (n == 0) {// this was the first constituent
				if (composition.get(0) >= 0) {// possible parse
					// determine the remainder of the sentence
					f0 = Utils.complement(f, ((Rules2) grammar.get(composition.get(0))).getExpression());
					if (!f0.equals("") && !f0.equals(f)) {// we may proceed
						n++;
						maxScore = mScore;
					} else {// we parsed the part as a whole, this rule has f as
							// expression, stop
						composition.clear();
						n = 2;
					}
				} else {// stop, there is no possible parse
					n = 2;
					composition.clear();
				}
			} else {// n=1, we checked both constituents
				if (composition.get(1) >= 0)// successful parse
					maxScore = maxScore * mScore;
				else
					composition.clear();
				n = 2;
			}
		}

		if (composition.isEmpty())
			return false;
		tmpMeanings = new IntArray(DCS);
		currentScore = iScore * meaningScore() * maxScore;
		parsed = true;
		return true;
	}

	/**
	 * This function tries to parse a part of the speaker's utterance.
	 * 
	 * @param f
	 *            the speaker's utterance
	 * @param DCS
	 *            the meaning to be considered
	 * @param totCover
	 *            the linguistic category
	 *
	 * @return true if a the string can be partially parsed
	 */

	public boolean partialParse(final String f, final IntArray DCS, int totCover) {
		composition.clear();
		if (totalCover != totCover)
			return false;
		if (expression != null)
			return false;// It's not a rule containing two constituents
		if (partialMatch(DCS, totCover)) {
			// semantic match at place i0, which can be either 0 (first part) or
			// 1 (second part)
			maxScore = 0.0;
			composition = new IntArray(2, -1);
			Rules2 r = null;
			// search the rule that fits with the semantic match at i0, and
			// select the highest scoring one
			for (int i = 0; i < grammar.size(); i++) {
				r = (Rules2) grammar.get(i);
				if (r != this && r.parse(f, tmpGroup[i0], covers[i0], i0)) {
					if (r.getScore() > maxScore || (r.getScore() == maxScore && Math.random() > 0.5)) {
						composition.set(i0, i);
						maxScore = r.getScore();
					}
				}
			}
			if (composition.get(i0) < 0)
				return false;
		} else
			return false;
		tmpMeanings = new IntArray(DCS);
		currentScore = iScore * meaningScore() * maxScore;
		return true;
	}

	/**
	 * This function exploits a partial parse to adopt a part of the utterance.
	 *
	 * @param f
	 *            the original expression, part of which is adopted here
	 * @param lg
	 *            language game number
	 *
	 *            return true if exploitation succeeds (should normally be the
	 *            case)
	 */

	public boolean exploit(final String f, int lg) {
		if (composition.get(i0) >= 0) {// partial parse, exploit that and adopt
										// remainder at constituent i1
			String f0 = Utils.complement(f, ((Rules2) grammar.get(composition.get(i0))).getExpression());
			if (f0.equals("")) {// should not happen
				composition.clear();
				return false;
			}
			composition.set(i1, grammar.size());
			// construct and add the new rule
			grammar.addRule(new Rules2(covers[i1], f0, tmpGroup[i1], grammar, ontology, maxCover, alphabetSize));
			// add the meanings
			addMeaning(tmpGroup[i1], i1);
			// and generalise
			generalise(f0, tmpGroup[i1], covers[i1], i0);
		} else
			return false;
		return true;
	}

	/**
	 * This function generalises the grammar by searching unexplored rules that
	 * can be generalised based on a new rule.
	 *
	 * @param f
	 *            the expression of the new rule
	 * @param m
	 *            the meaning of the new rule
	 * @param c
	 *            the cover/conceptual space/linguistic category of the new rule
	 * @param n
	 *            the position/constituent of the new rule
	 */

	public void generalise(final String f, final IntArray m, final int c, final int n) {
		int gs = grammar.size();
		IntArray mean;
		for (int i = 0; i < gs; i++) {
			// try every rule
			mean = ((Rules2) grammar.get(i)).tryGeneralisation(f, m, c);
			if (mean != null)
				addMeaning(mean, n);
		}
	}

	/**
	 * This function tries to generalises a rule based on a new rule.
	 *
	 * @param f
	 *            the expression of the new rule
	 * @param m
	 *            the meaning of the new rule
	 * @param c
	 *            the cover/conceptual space/linguistic category of the new rule
	 */
	private IntArray tryGeneralisation(final String f, final IntArray m, final int c) {
		// these are the conditions under which generalisation can happen
		if (totalCover == maxCover && expression != null && !expression.equals(f)
				&& (expression.startsWith(f) || expression.endsWith(f)) && meanings.containsAll(m)
				&& covers.length == 1) {
			IntArray mean = meanings.complement(m);
			int c0 = 0;
			for (int i = 0; i < mean.size(); i++)
				c0 += ((ANMeaning) ontology.get(mean.get(i))).getCover();
			if (c0 == maxCover - c) {
				String f0 = Utils.complement(expression, f);
				grammar.addRule(new Rules2(maxCover - c, f0, mean, grammar, ontology, maxCover, alphabetSize));
				return mean;
			} // else do nothing at first
		}
		return null;
	}

	/**
	 * This function calculates the meaning weight by taking the average of the
	 * categorical feature weights
	 */
	private double meaningScore() {
		double mScore = 0.0;
		int n, denom = 0;
		for (int i = 0; i < tmpMeanings.size(); i++) {
			n = meanings.indexOf(tmpMeanings.get(i));
			if (n >= 0) {
				mScore += score.get(n);
				denom++;
			}
		}
		if (denom > 0)
			return mScore / (double) denom;
		return 0.0;
	}

	/**
	 * This function returns the rule weight
	 */
	public double getWeight() {
		return iScore;
	}

	/**
	 * This function returns the score of the rule. This is calculated as the
	 * rule weight times the meaning score
	 */
	public double getScore() {
		return iScore * meaningScore();
	}

	/**
	 * This function calculates the score of the composition.
	 *
	 * @param f
	 *            not used at the moment
	 * @param c
	 *            not used at the moment
	 */

	public double getScore(final int[] f, int c) {
		double prod, p0;
		if (currentScore > 0.0)
			return currentScore;
		p0 = iScore * meaningScore();
		prod = p0;
		for (int i = 0; i < composition.size(); i++)
			if (composition.get(i) >= 0) {
				prod = p0 * ((Rules2) grammar.get(composition.get(i))).getScore(f, covers[i]);
				p0 = prod;
			}
		return prod;
	}

	/**
	 * This function updates the frequency with which it has been able to encode
	 * or decode an expression, irrespective of whether it was finally selected
	 * or not. This could be part of a probabilistic parser. And currently only
	 * used to inform us.
	 */
	public void updateFrequency(int[] f, int c) {
		frequency++;
		f[c]++;
		for (int i = 0; i < composition.size(); i++)
			if (composition.get(i) >= 0)
				((Rules2) grammar.get(composition.get(i))).updateFrequency(f, covers[i]);
	}

	/**
	 * This function returns the words of the rule's composition as a string
	 * array
	 */

	public String[] getParts() {
		String[] retval = new String[0];
		if (covers.length == 1 && expression != null) {
			retval = new String[1];
			retval[0] = expression;
		} else {
			if (composition.size() == 0)
				Utils.error("composition disappeared");
			retval = new String[composition.size()];
			for (int i = 0; i < composition.size(); i++) {
				if (composition.get(i) >= 0)
					retval[i] = ((Rules2) grammar.get(composition.get(i))).getExpression();
				else
					retval[i] = new String();
			}
		}
		return retval;
	}

	/**
	 * This function returns the expression of the rule.
	 */
	public String getExpression() {
		String retval = null;
		if (covers.length == 1 && expression != null)
			return expression;
		else {
			retval = new String();
			for (int i = 0; i < composition.size(); i++) {
				if (composition.get(i) >= 0)
					retval = retval.concat(((Rules2) grammar.get(composition.get(i))).getExpression());
			}
			if (retval.equals(""))
				return null;
		}

		return retval;
	}

	/**
	 * Returns the composition of the rule
	 *
	 * @return the composition as an IntArray. If there is no composition, it
	 *         returns an emty array
	 */
	public IntArray getComposition() {
		if (!composition.contains(-1))
			return composition;
		return new IntArray();
	}

	/**
	 * Sets the composition to the given composition
	 *
	 * @param c
	 *            the composition
	 */
	public void setComposition(final IntArray c) {
		composition = c;
	}

	/**
	 * returns a matrix representation of the meaning. this representation is
	 * used for constructing the grammar in the UI
	 */
	public double[][] getMeaning(final String l, final String l1, int dim) {
		double[][] retval = new double[10][dim];
		int m, d, n, d0;
		double x, s;
		for (int i = 0; i < meanings.size(); i++) {
			m = meanings.get(i);
			x = ((ANMeaning) ontology.get(m)).avgCenter();
			d = ((ANMeaning) ontology.get(m)).getDim();
			d0 = l1.indexOf(l.charAt(d));
			n = Utils.index(x);
			s = score.get(i);
			retval[n][d0] = s;
		}
		return retval;
	}

	/**
	 * This function checks whether the utterance matches the rule's expression.
	 * If so, then it adds the meanings of the dcs to the rule's meaning.
	 *
	 * @param u
	 *            the utterance
	 * @param dcs
	 *            the distinctive category
	 * @return true if the word matches, false otherwise
	 */

	public boolean wordMatch(final String u, final IntArray dcs) {
		if (u.equals(expression) && covers.length == 1) {
			addMeaning(dcs, 0);
			return true;
		}

		return false;
	}

	/**
	 * This function implements generalise and merge. Part of the generalisation
	 * is done with the function generalise(final String f,final IntArray
	 * m,final int c,final int n) that is defined and called elsewhere. Here the
	 * function compares this rule with another rule and sees if there are
	 * commonalities or ways to generalise the two rules.
	 *
	 * @param rule
	 *            index to the rule to be compared.
	 *
	 * @return true if the rule can be merged.
	 */
	public boolean generaliseAndMerge(int rule) {
		Rules2 r = (Rules2) grammar.get(rule);
		if (totalCover != r.totalCover || covers.length != r.covers.length)
			return false;
		if (covers.length == 1 && !expression.equals(r.expression))
			return false;
		for (int i = 0; i < covers.length; i++)
			if (covers[i] != r.covers[i])
				return false;
		// now we can merge:
		int ind;
		frequency += r.frequency;
		for (int i = 0; i < r.meanings.size(); i++) {
			ind = meanings.indexOf(r.meanings.get(i));
			if (ind >= 0) {
				score.set(ind, Math.max(score.get(ind), r.score.get(i)));
				use.set(ind, use.get(ind) + r.use.get(i));
			} else {
				meanings.add(r.meanings.get(i));
				score.add(r.score.get(i));
				use.add(r.use.get(i));
				group.add(r.group.get(i));
			}
		}
		return true;
	}

	/**
	 * general string form
	 */
	public String toString() {
		if (covers.length > 1)
			expression = null;
		if (expression != null)
			return new String("R\\_" + String.valueOf(number) + " & " + this.label("RGBSXY", totalCover)
					+ " $\\rightarrow$ & " + expression + "/" + meaningsRepresentation() + " & " + frequency + " & "
					+ iScore + " \\\\");
		return new String("R\\_" + String.valueOf(number) + " & " + this.label("RGBSXY", totalCover)
				+ " $\\rightarrow$ & [" + this.getString("RGBSXY") + "]/" + meaningsRepresentation() + " & " + frequency
				+ " & " + iScore + " \\\\");
	}

	/**
	 * a string form of the rule
	 */
	public String niceString() {
		if (covers.length > 1)
			expression = null;
		if (expression != null)
			return new String("S->" + expression + "/" + frequency + "/" + Utils.doubleString(iScore, 5));
		return new String("S->" + IntArray.stringValue(covers) + "/" + frequency + "/" + Utils.doubleString(iScore, 5));
	}

	/**
	 * Returns a string representation of the rule
	 */
	public String printSH() {
		if (covers.length > 1)
			expression = null;
		if (expression != null)
			return new String(String.valueOf(totalCover) + "->" + expression + "/" + frequency);
		return new String(String.valueOf(totalCover) + "->" + IntArray.stringValue(covers) + "/" + frequency);
	}

	/**
	 * Returns the labels of the quality dimensions of the conceptual space
	 * indicated by cov
	 *
	 * @param l
	 *            the string with labels of all features used in the simulation
	 *            (e.g. "RGBSXY")
	 * @param cov
	 *            the cover of which we want the label.
	 */
	private String label(final String l, int cov) {
		String r = new String();
		for (int i = 0; i < l.length(); i++) {
			if (((int) Math.pow(2, i) & cov) == (int) Math.pow(2, i))
				r = r.concat(l.substring(i, i + 1));
		}
		return r;
	}

	/**
	 * Returns a string representation for the linguistic categories that make
	 * up this rule.
	 *
	 * @param l
	 *            the string with labels of all features used in the simulation
	 *
	 * @return string of the conceptual space for a terminal node, or a string
	 *         of the combination of of conceptual spaces of the compositional
	 *         rule (e.g. "RGB ; S" if the rule is a composition of colour and
	 *         shape)
	 */
	public String getString(final String l) {
		String retval = new String();
		for (int i = 0; i < covers.length; i++) {
			retval = retval.concat(label(l, covers[i]));
			if (i < covers.length - 1)
				retval = retval.concat(" ; ");
		}
		return retval;
	}

	private String meaningsRepresentation() {
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < meanings.size(); i++) {
			builder.append(ontology.get(i));
			builder.append(" ; ");
		}
		return builder.toString();

	}

	/**
	 * Implementation of an alternative equals
	 *
	 * @param r1
	 *            the rule to compare
	 *
	 * @return true if the rules are - apart from their semantics! - equal
	 */
	public boolean equals(final Rules2 r1) {
		if (covers.length != r1.covers.length)
			return false;
		if (covers.length > 1) {
			for (int i = 0; i < covers.length; i++)
				if (covers[i] != r1.covers[i])
					return false;
		} else if (expression == null || !expression.equals(r1.expression) || totalCover != r1.totalCover)
			return false;
		return true;
	}

	/**
	 * Updates all weights of this rule and their meanings.
	 *
	 * @param s
	 *            boolean indicating the success of the game
	 */
	public void updateScore(boolean s) {
		double sc;
		int n;
		if (parsed) {
			if (s) {// success
				totalUse++;
				// increasing rule weight
				iScore = eta * iScore + 1. - eta;
				for (int i = 0; i < meanings.size(); i++) {
					sc = score.get(i);
					if (tmpMeanings.contains(meanings.get(i)))
						// increasing categorical feature weight
						sc = eta * sc + 1. - eta;
					else
						sc = eta * sc;// lateral inhibition of competing
										// categories
					score.set(i, sc);
				}
			} else {// failure: decreasing the weights
				iScore = eta * iScore;
				for (int i = 0; i < tmpMeanings.size(); i++) {
					n = meanings.indexOf(tmpMeanings.get(i));
					if (n >= 0) {
						sc = score.get(n);
						sc = eta * sc;
						score.set(n, sc);
					}
				}
			}
		}
	}

	/**
	 * Function that calls the function updateScore(boolean s) function, after
	 * the tmpMeanings array is initialised with the proper meanings specified
	 * in d
	 *
	 * @param s
	 *            boolean indicating the success of the game
	 * @param d
	 *            the IntArray containing the distinctive categories
	 */
	public void updateScore(boolean s, final IntArray d) {
		tmpMeanings = new IntArray(d);
		updateScore(s);
	}

	/**
	 * Returns the frequency of the rule
	 */
	public int getFrequency() {
		return frequency;
	}

	/**
	 * @return true if the rule has more than 1 constituent, false otherwise
	 */
	public boolean getCompositional() {
		return (covers.length > 1);
	}

	/**
	 * @return 1 if the rule has more than 1 constituent, 0 otherwise
	 */
	public int compositionality() {
		if (covers.length > 1)
			return 1;
		return 0;
	}

	/**
	 * @return true if the rule is holistic
	 */
	public boolean getHolistic() {
		if (covers.length == 1 && covers[0] == maxCover)
			return true;
		return false;
	}

	/**
	 * @return true if the rule is a rewrite rule for a sentence (ie. it covers
	 *         the entire conceptual space)
	 */
	public boolean isSentence() {
		if (totalCover == maxCover)
			return true;
		return false;
	}

	/**
	 * @param e
	 *            an expression
	 * @return true if the string e is the same as the rule's expression
	 */
	private boolean similarExpression(final String e) {
		if (expression != null && e.equals(expression))
			return true;
		return false;
	}

	/**
	 * @param c
	 *            an array of covers
	 * @return true if the covers c are the same as the rule's covers
	 */
	private boolean similarCovers(final int[] c) {
		if (covers.length != c.length)
			return false;
		for (int i = 0; i < c.length; i++)
			if (covers[i] != c[i])
				return false;
		return true;
	}

	/**
	 * This function searches the grammar g if it has a rule that is similar to
	 * this one. A rule is considered similar if:<br>
	 * 1. the expressions are equal, or<br>
	 * 2. the covers/linguistic categories are equal<br>
	 * For reasons of convenience, the semantics are not compared.
	 *
	 * @param g
	 *            grammar of another agent
	 * @return true if the rule has a similar counterpart in g
	 */
	public boolean similar(final Grammar g) {
		if (expression != null) {
			for (int i = 0; i < g.size(); i++)
				if (((Rules2) g.get(i)).similarExpression(expression))
					return true;
		} else {
			for (int i = 0; i < g.size(); i++)
				if (((Rules2) g.get(i)).similarCovers(covers))
					return true;
		}
		return false;
	}

	/**
	 * @return the number of times the rule was used in the language games
	 */
	public int getTotalUse() {
		return totalUse;
	}

	public int getNumber() {
		return number;
	}
//
//	@Override
//	public int compareTo(Rules2 o) {
//		if (this.getCompositional() == o.getCompositional()) {
//			return Comparator.comparing(Rules2::getFrequency).thenComparingInt(Rules2::getNumber).reversed()
//					.compare(this, o);
//		}
//		return this.getCompositional() ? -1 : 1;
//	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int[] getCovers() {
		return covers;
	}

	public int getTotalCover() {
		return totalCover;
	}

}
