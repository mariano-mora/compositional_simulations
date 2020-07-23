package LGames;

import java.util.*;
import Util.*;

/**
 * This class implements the representations of meanings. Meanings are
 * represented as Adaptive Radial Basis Networks, though not yet completed. They
 * actually function as prototypes
 *
 * The class controls the meaning and implements certain operations that can be
 * performed on this representation.
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class ANMeaning {

	protected int dim = -1;
	protected DoubleArray rbf = new DoubleArray();
	protected DoubleArray w = new DoubleArray();
	protected final double initialWeight = 1.0;
	protected final double std = 0.05;
	protected double z = 0.0;
	protected double y = -1.;
	private int timestamp = -1;
	private double oldX = -1.0;
	private double sigma2 = 0.0;
	private double avgCenter = -1.;
	private double sumNodes = 0.1;
	private final double alpha = 1.;
	private final double beta = 0.99;
	private final double thresholdWeights = 0.05;
	private IntArray targets = new IntArray();
	private int cover = -1;

	/**
	 * Empty constructor
	 */
	public ANMeaning() {
	}

	/**
	 * Constructs a radial basis function (ANMeaning) for dimension d and with a
	 * center point x. Basically, it is an categorical feature at dimension d
	 * with value x
	 *
	 * @param d
	 *            dimension
	 * @param x
	 *            the feature value
	 */
	public ANMeaning(final int d, final double x) {
		dim = d;
		cover = (int) Math.pow(2, dim);
		rbf.clear();
		rbf.add(x);
		avgCenter = x;
		sumNodes = x;
		w.clear();
		w.add(initialWeight);
		sigma2 = std * std;
	}

	/**
	 * Function to evaluate the activation value for the given feature
	 *
	 * @param ts
	 *            a timestamp
	 * @param x
	 *            the feature value
	 *
	 * @return the activation value
	 */
	public double evalY(int ts, double x) {
		if (timestamp != ts || oldX != x) {
			timestamp = ts;
			oldX = x;
			y = 0.0;
			for (int i = 0; i < rbf.size(); i++) {
				z = Math.exp(-0.5 * (x - rbf.get(i)) * (x - rbf.get(i)) / sigma2);
				y += z * w.get(i);
			}
		}
		return y;
	}

	/**
	 * Checks if the current meaning is an element of the ontology.
	 *
	 * @param o
	 *            the ontology
	 *
	 * @return false if the meaning is in the ontology, true if not.
	 */
	public boolean notExists(final List<ANMeaning> o) {
		for (ANMeaning meaning : o){
			if (meaning.rbf.containsElementOf(rbf) && meaning.dim == dim)
				return false;
		}
		return true;
//		for (int i = 0; i < o.size(); i++)
//			if (((o.get(i)).rbf).containsElementOf(rbf) && (o.get(i)).dim == dim)
//				return false;
//		return true;
	}

	/**
	 * This function is not used currently.
	 */
	public void addLocalUnit(int d, double x) {
		if (d != dim)
			Utils.error("adding local unit of wrong dimension");
		if (!rbf.contains(x)) {
			rbf.add(x);
			w.add(initialWeight);
			sumNodes += x;
			avgCenter = sumNodes / (double) rbf.size();
		}
	}

	/**
	 * This function is not used currently.
	 */
	public void updateWeights(final int d, final double x) {
		if (d != dim)
			Utils.error("updating weights of wrong dimension, d=" + d + " dim=" + dim);
		double newW;
		for (int i = 0; i < w.size(); i++) {
			newW = Math.min(1.0, w.get(i) + alpha * Math.exp(-0.5 * (x - rbf.get(i)) * (x - rbf.get(i)) / sigma2));
			w.set(i, newW);
		}
	}

	/**
	 * This function is not used currently.
	 */
	public boolean decayWeights() {
		int i = 0;
		double newW;
		while (i < w.size()) {
			newW = w.get(i) * beta;
			if (newW > thresholdWeights) {
				w.set(i, newW);
				i++;
			} else {
				w.remove(i);
				rbf.remove(i);
			}
		}
		if (w.isEmpty())
			return false;
		return true;
	}

	/**
	 * @return the center (categorical feature) of the meaning
	 */
	public double avgCenter() {
		return avgCenter;
	}

	/**
	 * This function is not used currently.
	 */
	public IntArray getTargets() {
		return targets;
	}

	/**
	 * This function is not used currently.
	 */
	public void addTarget(int a) {
		targets.add(a);
	}

	/**
	 * @return the dimension of the meaning
	 */

	public int getDim() {
		return dim;
	}

	/**
	 * @return the cover of the meaning
	 */
	public int getCover() {
		return cover;
	}

	/**
	 * @return the string representation of the meaning.
	 */
	public String toString() {
		return new String(dim + ":" + rbf.toString(3));// +" "+w.toString(5));
	}
}
