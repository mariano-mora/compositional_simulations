package LGames;

import java.util.*;

import LGames.Cognition.Category;

import java.io.PrintWriter;
import Util.*;

/**
 * This class implements the representations of meanings. The meanings are
 * represented as prototypes, an n-dimensional vector. The class controls the
 * meaning and implements certain operations that can be performed on this
 * representation.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class Meaning {

	protected double[] prototype = new double[1];
	protected int ID = -1;
	protected final double epsilon = 0.01;
	protected int use = 0;
	protected final double alpha = 0.001;
	protected double temperature = 1.0;
	protected int cover = 0;
	protected final double epsilonRegion = 0.05;
	protected int layer = 0;
	protected Category category;

	/**
	 * Empty constructor
	 */
	public Meaning() {
	}

	/**
	 * Constructs a meaning with the given feature vector as the new prototype
	 * and id.
	 *
	 * @param fv
	 *            the feature vector
	 * @param n
	 *            the id
	 */
	public Meaning(double[] fv, int n) {
		cover = 0;
		ID = n;
		prototype = new double[fv.length];
		for (int i = 0; i < fv.length; i++) {
			prototype[i] = fv[i];
			if (fv[i] >= 0.0)
				cover += Math.pow(2, i);
		}
	}

	/**
	 * Constructs a meaning with the given feature vector as the new prototype,
	 * an id and its hierarchical layer
	 *
	 * @param fv
	 *            the feature vector
	 * @param n
	 *            the id
	 * @param l
	 *            the layer
	 */
	public Meaning(double[] fv, int n, int l) {
		cover = l;
		ID = n;
		layer = l;
		prototype = new double[fv.length];
		for (int i = 0; i < fv.length; i++) {
			prototype[i] = fv[i];
			if (fv[i] >= 0.0)
				cover += Math.pow(2, i);
		}
	}

	/**
	 * Constructs a meaning with the given feature vector as the new prototype,
	 * an id, its cover and its hierarchical layer.<br>
	 * The cover also indicates on which dimensions the prototypes are added.
	 * Currently not used (i think)
	 *
	 * @param fv
	 *            the feature vector
	 * @param n
	 *            the id
	 * @param c
	 *            the cover
	 * @param l
	 *            the layer
	 */
	public Meaning(double[] fv, int n, int c, int l) {
		ID = n;
		cover = c;
		layer = l;
		prototype = new double[fv.length];
		for (int i = 0; i < fv.length; i++) {
			if (((int) Math.pow(2, i) & cover) == (int) Math.pow(2, i))
				prototype[i] = fv[i];
			else
				prototype[i] = -1.0;
		}
	}

	/**
	 * Constructs a dummy meaning with feature values of -100. This is placed at
	 * the front of the ontology list, for which the score matrix holds other
	 * information. If this is not done, the meaning may be used as a real one.
	 */
	public Meaning(int id) {
		ID = id;
		cover = 0;
		prototype[0] = -100.0;
	}

	/**
	 * Constructs a meaning based on the categorical features specified in the
	 * dcs and given in the ontology o
	 */
	public Meaning(final int[] dcs, final List o) {
		prototype = new double[(((Meaning) o.get(1)).getPrototype()).length];
		cover = 0;
		int n;
		double[] ptype;
		String idString = new String();
		for (int i = 0; i < dcs.length; i++) {
			n = dcs[i];
			ptype = ((Meaning) o.get(n)).getPrototype();
			// idString=idString.concat(Integer.toString(((Meaning)o.get(n)).getID()));
			for (int j = 0; j < ptype.length; j++)
				if (ptype[j] >= 0) {
					prototype[j] = ptype[j];
					cover += (int) Math.pow(2, j);
				}
		}
		ID = ((Meaning) o.get(dcs[0])).getID();
	}

	/**
	 * Constructs a meaning used for testing.
	 */
	public Meaning(int id, int c) {
		ID = id;
		cover = c;
	}

	/**
	 * @return the meaning's id
	 */
	public int getID() {
		return ID;
	}

	public void setID(int id) {
		ID = id;
	}

	/**
	 * @return the meaning's prototype
	 */
	public double[] getPrototype() {

		return prototype;
	}

	/**
	 * increases the usage counter by 1
	 */
	public void increaseUse() {
		use++;
	}

	/**
	 * @return usage counter
	 */
	public int getUse() {
		return use;
	}

	/**
	 * @return the cover of the meaning
	 */
	public int getCover() {
		return cover;
	}

	/**
	 * @return the hierarchical layer of the meaning
	 */
	public int getLayer() {
		return layer;
	}

	/**
	 * Checks wether this meaning is a member of the ontology
	 *
	 * @param o
	 *            the ontology
	 *
	 * @return index of this meaning in the ontology (-1 if this meaning is not
	 *         in the ontology)
	 */
	public int memberOf(final List o) {
		for (int i = 0; i < o.size(); i++)
			if (equals((Meaning) o.get(i)))
				return i;
		return -1;
	}

	/**
	 * The function equals checks whether two meanings are equal. They are equal
	 * if the prototypes are.
	 *
	 * @param m
	 *            the second meaning
	 * @return true if the two meanings are equal, false otherwise
	 */
	public boolean equals(Meaning m) {
		for (int i = 0; i < prototype.length; i++)
			if (prototype[i] != m.prototype[i])
				return false;
		return true;
	}

	/**
	 * returns a string representation of the meaning that is used for writing
	 * to the log file in the Stats class.
	 */
	public String toString() {
		return new String(ID + " " + layer + " " + Utils.doubleArrayString(prototype));
	}

	/**
	 * returns a string representation of the meaning that is used for writing
	 * to the log file in the Stats class.
	 */
	public String mstring() {
		String retval = new String(ID + " (");
		String X;
		int l;
		for (int i = 0; i < prototype.length; i++) {
			X = new String(String.valueOf(prototype[i]));
			l = Math.min(X.length(), 4);
			retval = retval.concat(X.substring(0, l));
			if (i + 1 < prototype.length)
				retval = retval.concat(",");
		}
		retval = retval.concat(")");
		return retval;
	}

	/**
	 * Returns a string representation of the meaning.
	 */
	public String string() {
		String retval = new String("");
		String X;
		int l;
		for (int i = 0; i < prototype.length; i++) {
			X = new String(String.valueOf(prototype[i]));
			l = Math.min(X.length(), 4);
			retval = retval.concat(X.substring(0, l));
			if (i + 1 < prototype.length)
				retval = retval.concat(" ");
		}
		return retval;
	}

	/**
	 * This function shifts each dimension of the prototype in the direction of
	 * the corresponding dimension of the feature vector.
	 *
	 * @param fv
	 *            the feature vector.
	 * @param updateType
	 *            specifies the way the prototypes are shifted. There is one
	 *            that uses a center-of-mass method, one uses simulated
	 *            annealing and another a direct walk
	 */
	public void shift_prototype(double[] fv, char updateType) {
		if (updateType == 'c')// center-of-mass
			for (int i = 0; i < fv.length; i++)
				prototype[i] = ((double) use * prototype[i] + fv[i]) / (double) (use + 1);
		else if (updateType == 's')// simulated annealing
			for (int i = 0; i < fv.length; i++)
				prototype[i] += Math.exp(-Math.abs(fv[i] - prototype[i]) / temperature) * (fv[i] - prototype[i]);
		else if (updateType == 'w')// walk
			for (int i = 0; i < fv.length; i++)// walk
				prototype[i] += epsilon * (fv[i] - prototype[i]);
		use++;
		temperature = 0.9 * temperature;
	}

	/**
	 * if after shifting prototypes, two prototypes come within a certain
	 * Eucledian distance of each other, the two are merged. This is tricky as
	 * both the Meaning and Symbols are connected at two places. And all
	 * information, including the associated symbols have to be merged without
	 * making doubles.
	 *
	 * @param M
	 *            the meaning to be merged.
	 *
	 */
	public void merge(Meaning M) {

		double[] target = M.getPrototype();

		for (int i = 0; i < prototype.length; i++)
			prototype[i] += 0.5 * (target[i] - prototype[i]);
		use += M.getUse();
	}

	/**
	 * compares two prototypes (or possibly a feature vector) with each other.
	 *
	 * @param c
	 *            the prototype (feature vector) to be compared.
	 * @return true if both are equal, false otherwise.
	 */
	public boolean comparePType(double[] c) {
		boolean equal = true;
		if (c.length != prototype.length)
			return false;
		for (int i = 0; i < prototype.length && equal; i++)
			if (prototype[i] != c[i])
				equal = false;
		return equal;
	}

	/**
	 * Writes the meaning to the standard output
	 */
	public void print() {

		System.out.print("M" + ID + ": ");
		for (int i = 0; i < prototype.length; i++)
			System.out.print(prototype[i] + " ");
		System.out.println();
	}

	/**
	 * Writes the meaning to a file
	 */
	public void print(PrintWriter outfile) {

		outfile.print(ID + " (");
		for (int i = 0; i < prototype.length; i++) {
			Double x = new Double(prototype[i]);
			int l = Math.min(4, (x.toString()).length());
			outfile.print((x.toString()).substring(0, l));
			if (i + 1 < prototype.length)
				outfile.print(",");
		}
		outfile.print(")");
	}

	/**
	 * Checks whether the meaning is in the context.
	 *
	 * @param cxt
	 *            the context
	 * @return true if it is in the context, false otherwise
	 */
	public boolean inContext(Context cxt) {
		for (int i = 0; i < cxt.categoryLength(); i++)
			if (ID == cxt.getCategory(i))
				return true;
		return false;
	}

	/**
	 * Calculates the cover of the similarity between the vector x and the
	 * prototype
	 *
	 * @param x
	 *            the vector
	 * @return cover of the similarity
	 */
	public int similarDimensions(final double[] x) {
		int retval = 0;

		for (int i = 0; i < prototype.length; i++)
			if (prototype[i] >= 0.0 && x[i] > 0 && Math.abs(prototype[i] - x[i]) < epsilonRegion)
				retval += Math.pow(2, i);
		return retval;
	}

	/**
	 * This function returns the k-nearest neighbours of this meaning in the
	 * ontology o. This is currently not used
	 *
	 * @param o
	 *            the ontology
	 * @param k
	 *            the number of nearest neighbours
	 *
	 * @return an array integers with the indices to the nearest neighbours
	 */
	public int[] getkNN(final List o, int k) {
		double[] d = new double[k];
		int[] retval = new int[k];
		double dist = 0.0;
		for (int i = 0; i < k; i++) {
			d[i] = 1000.0;
			retval[i] = -1;
		}

		for (int i = 0; i < o.size(); i++) {
			dist = Utils.distance(prototype, ((Meaning) o.get(i)).getPrototype());
			for (int j = 0; j < k; j++) {
				if (dist <= d[j]) {
					int n = k - 1;
					while (n > j) {
						d[n] = d[n - 1];
						retval[n] = retval[n - 1];
						n--;
					}
					d[n] = dist;
					retval[n] = i;
					j = k;
				}
			}
		}
		return retval;
	}

	/**
	 * This function searches the index of this meaning in the ontology o
	 *
	 * @param o
	 *            the ontology
	 * @return the index of this meaning (-1 is it is not in o)
	 */
	public int elementOf(final List o) {
		for (int i = 0; i < o.size(); i++)
			if (equals((Meaning) o.get(i)))
				return i;
		return -1;
	}
}
