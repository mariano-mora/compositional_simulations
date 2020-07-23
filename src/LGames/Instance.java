package LGames;

import Util.*;
import java.util.*;

/**
 * This class implements the instance base used by the CompositionalAgent2 for
 * inducing compositional structures. It stores an expression with its observed
 * meaning.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class Instance {
	private String expression = new String();
	private IntArray M = new IntArray();
	private List<CategoryMeaning> meanings;
	public int count = 1;

	/**
	 * Empty constructor
	 */
	public Instance() {
	}

	/**
	 * An instance constructor
	 *
	 * @param e
	 *            the expression
	 * @param m
	 *            an IntArray pointing to the meanings
	 */
	public Instance(final String e, final IntArray m) {
		expression = e;
		M = m;
	}

	public Instance(final String e, final List<CategoryMeaning> meanings) {
		this.expression = e;
		this.meanings = meanings;
	}

	/**
	 * A function to repair the index of the meanings if another meaning m was
	 * removed.
	 *
	 * @param m
	 *            the meaning that was removed.
	 * @return true if m is one of the meanings in this instance (in which case
	 *         the instance will be removed). It returns false if all is ok.
	 */
	public boolean updateMeanings(final int m) {
		for (int i = 0; i < M.size(); i++) {
			if (m < M.get(i))
				M.decrement(i);
			else if (m == M.get(i))
				return true;
		}
		return false;
	}

	/**
	 * A function to repair the index of the meanings if an array of meanings m
	 * were removed.
	 *
	 * @param m
	 *            the array of meanings that was removed.
	 * @return true if one of the meanings in m equals one of the meanings in
	 *         this instance (in which case the instance will be removed). It
	 *         returns false if all is ok.
	 */
	public boolean updateMeanings(final IntArray m) {
		for (int n = 0; n < m.size(); n++) {
			for (int i = 0; i < M.size(); i++) {
				if (m.get(n) < M.get(i))
					M.decrement(i);
				else if (m.get(n) == M.get(i))
					return true;
			}
		}
		return false;
	}

	/**
	 * This function investigates how the heard expression e in relation to
	 * meaning m can be chunked (hence the name split :-()).
	 *
	 * @param e
	 *            the expression
	 * @param m
	 *            the meaning
	 *
	 * @return an IntArray a that contains the following information: a[0] = 0
	 *         alignment first constituent, a[0] = 1 alignment second
	 *         constituent, a[1] = the position in the string where the chunk
	 *         can be applied (ie. where the alignment ends or starts),
	 *         a[2]..a[a.length] the aligning meanings.
	 */
	public IntArray split(final String e, final IntArray m) {
		String sub = Utils.largestSubString(expression, e);
		if (sub.equals("") || sub.equals(e))
			return null;
		IntArray retval = M.crossSection(m);
		if (retval.isEmpty() || M.containsAll(m))
			return null;
		if (e.startsWith(sub)) {
			retval.add(0, sub.length());
			retval.add(0, 0);
		} else {// it ends with
			retval.add(0, e.length() - sub.length());
			retval.add(0, 1);
		}
		return retval;
	}

	public void increaseCount() {
		this.count += 1;
	}

	public boolean isSame(String expression, List<CategoryMeaning> meanings){
		return this.expression == expression && this.meanings.containsAll(meanings);
	}
	
	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public List<CategoryMeaning> getMeanings() {
		return meanings;
	}
}
