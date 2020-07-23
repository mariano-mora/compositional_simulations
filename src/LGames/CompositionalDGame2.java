package LGames;

import java.lang.*;
import java.util.*;
import Util.*;

/**
 * The class CompositionalDGame2 implements the discrimination games in the
 * compositional set-up. The function playGame is used to control the game.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */
public class CompositionalDGame2 extends DGame {

	private IntArray[] CS = new IntArray[1];
	private DoubleArray[] distances = new DoubleArray[1];
	private IntArray[] cover = new IntArray[1];
	private List[] DCS = new List[1];
	private int maxCover = 15;
	private List ontology = new ArrayList();
	private final int k = 5;
	private final double thresholdDS = 0.95;

	/**
	 * Empty constructor
	 */
	public CompositionalDGame2() {

	}

	/**
	 * Constructor that specifies the holistic conceptual space
	 *
	 * @param m
	 *            the `cover' of the conceptual space (integer representation of
	 *            the bit-string indicating which dimensions are used.
	 */
	public CompositionalDGame2(final int m) {
		maxCover = m;
	}

	/**
	 * Constructor that specifies the holistic conceptual space, and sets a
	 * pointer to the agent's ontology
	 *
	 * @param m
	 *            the `cover' of the conceptual space (integer representation of
	 *            the bit-string indicating which dimensions are used.
	 * @param o
	 *            the ontology
	 */
	public CompositionalDGame2(final int m, List o) {
		maxCover = m;
		ontology = o;
	}

	/**
	 * Function to test the algorithm. Used to set an artificial category set.
	 */
	public void test() {

		CS[0] = new IntArray();
		cover[0] = new IntArray();
		DCS[0] = new ArrayList();

		CS[0].add(1);
		CS[0].add(2);
		CS[0].add(3);
		CS[0].add(4);
		CS[0].add(5);
		cover[0].add(15);
		cover[0].add(3);
		cover[0].add(12);
		cover[0].add(9);
		cover[0].add(6);

		IntArray c = new IntArray();

		// constructSets(c.get(),c.get(),0,0,0);

		// System.out.println(DCS[0]);
	}

	/**
	 * The categorize function categorises all objects in the context. In
	 * contrast to some descriptions elsewhere, it is not directly a 1-nearest
	 * neighbourhood search, but something very similar. The categorisation
	 * maximises the response of a categorical feature to a perceptual feature.
	 * This response is based on radial basis functions. The limited
	 * implementation made here make this function similar to the 1-nearest
	 * neighbourhood search.
	 *
	 * @param cxt
	 *            the context
	 * @param timestamp
	 *            the current `time' in language games
	 */
	private void categorize(Context cxt, int timestamp) {

		ANMeaning rbf;
		int dim = -1;
		CS = new IntArray[cxt.distinctive.length];
		// for each object
		for (int i = 0; i < cxt.distinctive.length; i++) {
			CS[i] = new IntArray(cxt.featureVector[0].length);
			double[] maxY = new double[cxt.featureVector[0].length];
			// search the ontology
			for (int j = 1; j < ontology.size(); j++) {
				rbf = (ANMeaning) ontology.get(j);
				// get the dimension of this meaning
				dim = rbf.getDim();
				// evaluate the response of this meaning to the object's feature
				// in this dimension
				if (rbf.evalY(timestamp, cxt.featureVector[i][dim]) > maxY[dim]) {
					// if it exceeds the previously maximum, use this category
					// for this dimension
					// it is the one nearest to the feature (ie.
					// cxt.featureVector[i][dim])
					maxY[dim] = rbf.evalY(timestamp, cxt.featureVector[i][dim]);
					CS[i].set(dim, j);
				}
			}
		}
	}

	/**
	 * playGame controls a discrimination game. The function first calls a
	 * categorisation function where it categorises the featureVector of the
	 * context in relation to the ontology. It then tries to discriminate the
	 * topic from the rest of the context. If this fails, a new category is
	 * added to the ontology, otherwise the category is shifted towards the
	 * topic and possibly merged with another category if it comes too close.
	 *
	 * @param cxt
	 *            the Context
	 * @param topic
	 *            the index of the topic pointing to the <i>topic</i>-th element
	 *            of the cxt list.
	 * @param timestamp
	 *            the current time (language game)
	 * @param adapt
	 *            sets learning on (true) or off (false)
	 *
	 * @return topic if DG succeeds, -1 otherwise.
	 */
	public int playGame(Context cxt, final int topic, final int timestamp, final boolean adapt) {

		int retval = -1;
		boolean SUCCESS = true;
		ANMeaning newRBF;

		// first we categorise all objects
		categorize(cxt, timestamp);

		// then we check if any of the dimensions in the topic's category set
		// CS[topic] failed to categorise
		cxt.setCategory(CS[topic].get());
		for (int i = 0; i < cxt.categoryLength(); i++)
			if (cxt.getCategory(i) == 0){
				SUCCESS = false;
				break;
			}

		// we check for distinctiveness
		for (int i = 0; i < cxt.distinctive.length; i++)
			if (i != topic && CS[topic].equals(CS[i])){
				SUCCESS = false;
				break;
			}

		// we need to adapt the ontology
		if (!SUCCESS && adapt) {
			if (ontology.size() < maxMeanings) {// if possible
				for (int i = 0; i < cxt.categoryLength(); i++) {
					// construct a new category (radial basis function)
					newRBF = new ANMeaning(i, cxt.featureVector[topic][i]);
					if (newRBF.notExists(ontology)) {// if it does not exist, we
														// add it to the
														// ontology
						ontology.add(newRBF);
						cxt.setCategory(i, ontology.size() - 1);
					}
				}
			}
		} else
			retval = topic;
		return retval;
	}

	/**
	 * Currently not used
	 */
	public IntArray decayWeights() {
		IntArray retval = new IntArray();
		for (int i = 1; i < ontology.size(); i++)
			if (!((ANMeaning) ontology.get(i)).decayWeights())
				retval.add(i);
		return retval;
	}

	/**
	 * returns the distinctive category set.
	 */
	public IntArray getDCS(int topic) {
		return CS[topic];
	}

	/**
	 * returns the Meaning representation of the topic
	 */
	public Meaning getTopic(int topic) {
		double[] x = new double[CS[topic].size()];
		for (int i = 0; i < CS[topic].size(); i++)
			x[i] = ((ANMeaning) ontology.get(CS[topic].get(i))).avgCenter();
		return new Meaning(x, topic);
	}

}
