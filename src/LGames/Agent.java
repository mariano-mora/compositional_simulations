package LGames;

import java.util.*;
import java.io.PrintWriter;

/**
 * The Agent class implements the agent's structure and behaviours. It is
 * constructed, initialised and controlled from the thsim main class.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public abstract class Agent {

	public enum Role{
		SPEAKER, HEARER;		
	}
	
	protected int id = 0;
	protected Context cxt = new Context();
	protected int nrDGSuccess = 0;
	protected int DG = 0;
	protected int topic = 0;
	protected Symbols utterance;
	protected Meaning TOPIC;
	protected int topicID = 0;
	protected int age = 0;
	protected int dimension = 6;
	protected Role role;

	public Agent() {
	}

	/**
	 * Constucts an agent with identity n
	 *
	 * @param n
	 *            the identity of the agent
	 * @param a
	 *            the agent's `birthdate' (i.e. language game nr.)
	 * @param e
	 *            the standard learning rate eta
	 * @param s
	 *            the learning rate eta for the speakers in case we wish to
	 *            distinguish
	 */
	public Agent(int n, final int a, final double e, final double s) {
	}

	public Agent(int n, final int a, final double e, final double s, final int m, final int M) {
	}

	/**
	 * Used to initialise some settings for testing
	 */

	public void initialise() {
	}

	/**
	 * Sets the context to C and adds noise
	 *
	 * @param C
	 *            the context
	 * @param noise
	 *            the perceptual noise
	 */
	public void setContext(final Context C, final double noise) {
		cxt = new Context(C, noise);
	}

	/**
	 * Returns the integer value of the topic
	 *
	 * @return topic
	 */
	public int getTopic() {
		return topic;
	}

	/**
	 * Sets the topic to the value t
	 *
	 * @param t
	 *            the topic number to be set
	 */
	public void setTopic(final int t) {
		topic = t;
	}

	/**
	 * Returns the feature vector of the topic
	 */
	public double[] getFV() {
		if (topic >= 0)
			return cxt.getFV(topic);
		return cxt.getFV(0);
	}

	/**
	 * Returns the pointer to the topic
	 *
	 * @return TOPIC the whole meaning structure of the topic
	 */
	public Meaning getTOPIC() {
		return TOPIC;
	}

	/**
	 * Returns the agent's id
	 *
	 * @return id the identity
	 */
	public int getID() {
		return id;
	}

	/**
	 * Returns the dimensionality of the feature/conceptual space
	 *
	 * @return dimension
	 */

	public int getDim() {
		return dimension;
	}

	/**
	 * returns the age of the agent
	 *
	 * @return age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * Returns the utterance produced by the speaker or interpreted by the
	 * hearer.
	 * 
	 * @return null if no utterance is produced or interpreted
	 * @return the form of the utterance otherwise
	 */

	public String getUtterance() {
		if (utterance != null)
			return utterance.getForm();
		return null;
	}

	/**
	 * Returns a string representation of the features that are selected. These
	 * may include RGBSXY
	 *
	 * @see Context#getLabels
	 */
	public String getLabels() {
		return cxt.getLabels();
	}

	/**
	 * This function implements the removal of categories or symbols in case of
	 * forgetting.
	 *
	 * @param type
	 *            type of the game that is being played
	 * @param rc
	 *            indicates whether it concerns a category (o) or symbol
	 *            (otherwise)
	 * @param n
	 *            the index of the element to be removed
	 */
	protected abstract void forget(char type, char rc, int n);

	/**
	 * This function implements the search for elements that can be forgotten
	 * based on their effectiveness
	 *
	 * @param type
	 *            type of game that is being played
	 */
	protected abstract void forget(char type);

	/**
	 * This function produces an utterance for the speaker
	 *
	 * @param probability
	 *            The word-creation probability
	 * @param type
	 *            The type of game being played ('s' - selfish game, 'g' -
	 *            guessing game, 'o' - observational game)
	 *
	 * @return Symbols representation of the utterance
	 */
	public abstract Symbols speak(double probability, char type);

	/**
	 * This function processes the interpretation of the hearer for the
	 * observational game.
	 *
	 * @param u
	 *            The Symbols representation of the speaker's utterance
	 * @return Symbols representation of the hearer's utterance
	 *         (interpretatation)
	 */
	public abstract Symbols hear(Symbols u, char type);

	/**
	 * This function processes the interpretation of the hearer for the guessing
	 * and selfish game.
	 *
	 * @param u
	 *            The Symbols representation of the speaker's utterance
	 * @param type
	 *            The type of game being played (either 's' - selfish game or
	 *            'g' - guessing game)
	 * @return Symbols representation of the hearer's utterance
	 *         (interpretatation)
	 */

	public Symbols guess(Symbols u, char utype) {
		return null;
	}

	/**
	 * Function to check whether a category relates to an object in the context
	 *
	 * @param id
	 *            ID of the category to be checked.
	 * @return true if category belongs to the context, false otherwise
	 */
	protected boolean context_member(int id) {

		for (int i = 0; i < cxt.categoryLength(); i++)
			if (id == cxt.getCategory(i))
				return true;
		return false;
	}

	/**
	 * Adaptation function for the speaker
	 *
	 * @param u
	 *            utterance of the hearer
	 * @param T
	 *            the topic
	 * @param type
	 *            the type of game
	 */
	public abstract void adaptLexiconSpeaker(Symbols u, int T, char type, char utype);

	/**
	 * Adaptation function for the hearer
	 *
	 * @param u
	 *            utterance of the speaker
	 * @param T
	 *            the topic
	 * @param type
	 *            the type of game
	 */

	public abstract void adaptLexiconHearer(Symbols u, int T, char type, char utype);

	/**
	 * This function lets the agent play a discrimination game
	 *
	 * @param type
	 *            the type of game
	 * @param uType
	 *            the type of update score- or usage-based
	 * @param adapt
	 *            boolean to indicate whether or not to adapt the ontology
	 */
	public abstract void playDGame(char type, char uType, boolean adapt);

	/**
	 * This function lets the hierarchical agent play a discrimination game
	 *
	 * @param type
	 *            the type of game
	 * @param uType
	 *            the type of update score- or usage-based
	 * @param layer
	 *            the layer at which the game is played
	 * @param adapt
	 *            boolean to indicate whether or not to adapt the ontology
	 */
	public void playDGame(char type, char uType, int layer, boolean adapt) {
	}

	/**
	 * This function lets the hierarchical agent play a discrimination game
	 *
	 * @param type
	 *            the type of game
	 * @param uType
	 *            the type of update score- or usage-based
	 * @param layer
	 *            the layer at which the game is played
	 * @param n
	 *            language game number
	 * @param adapt
	 *            boolean to indicate whether or not to adapt the ontology
	 */
	public void playDGame(char type, char uType, int layer, int n, boolean adapt) {
	}

	/**
	 * Initialise the discrimination game. Sets in the context all categories to
	 * not distinctive. Required in hierarchical layered games.
	 */
	public void initDGame() {
	}

	/**
	 * @return maxLayer the maximum layer currently represented by the
	 *         hierarchical agent
	 */
	public int getMaxLayer() {
		return -1;
	}

	/**
	 * This function implements the merging of meanings.
	 *
	 */

	public void merge(int dim, char type) {
	}

	/**
	 * @return DS the level of discriminative success (0 failure, N for N
	 *         successful games)
	 */
	public double getDS() {
		return 0.0;
	}

	/**
	 * Prints "AgentN", where N is the agent's id
	 */
	public void print() {

		System.out.println("Agent " + id);
		// cxt.print();

	}

	/**
	 * Constructs a string that can be written to the logfile
	 */
	public abstract String getGame();

	/**
	 * This function prints the ontology and lexicon of the agent to a file
	 *
	 * @param outfile
	 *            The file to which the data is written
	 * @param type
	 *            the type of update (score- or usage-based)
	 */
	public void print(PrintWriter outfile, char type) {
	}

	/**
	 * prints the lexicon as a table
	 *
	 * @param outfile
	 *            The file to which the data is written
	 * @param type
	 *            the type of update (score- or usage-based)
	 * @param lg
	 *            the language game number
	 */
	public void printLexiconLatexTabel(PrintWriter ofile, char type, int lg) {
	}

	/**
	 * prints the lexicon as in columns
	 *
	 * @param outfile
	 *            The file to which the data is written
	 * @param type
	 *            the type of update (score- or usage-based)
	 * @param lg
	 *            the language game number
	 */
	public void printLexiconColumns(PrintWriter ofile, char type, int lg) {

	}

	/**
	 * prints the scores of the lexical entry specified by u
	 *
	 * @param outfile
	 *            The file to which the data is written
	 * @param type
	 *            the type of update (score- or usage-based)
	 * @param u
	 *            the word of the lexical entry
	 * @param lg
	 *            the language game number
	 */

	public void printScore(PrintWriter outfile, char type, final String u, int lg) {
	}

	/**
	 * This function is used to produce a bag of words that are used by the
	 * entire population
	 */
	public void getWords(List bag) {

	}

	/**
	 * This function returns a String array with all the words, used for the UI
	 * showLexicon
	 */
	public String[] getWords() {
		return null;
	}

	/**
	 * This function is used to find the meaning that best fits a given word.
	 */
	public String getMeaning(String word, char type) {
		return null;
	}

	/**
	 * This function returns a matrix representation for the meanings of word w
	 * and is used for the UI showLexicon
	 *
	 * @param w
	 *            the word
	 * @param t
	 *            type of score-update
	 */

	public double[][] getMeanings(final String w, char t) {
		return null;
	}
	
	public void setRole(Role r){
		this.role = r;
	}
	
	public Role getRole(){
		return this.role;
	}

}
