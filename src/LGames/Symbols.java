package LGames;

/**
 * The Symbols class stores and manages lexical elements. It contains a list of
 * meanings and functions to guide the hearer's interpretation and adaptation.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class Symbols {

	private int ID;
	private String form;
	private String cons = "bcdfgh";// jklmnprstvwz";
	private String vowels = "aei";// ouy";
	private final double eta = 0.9;
	private final double epsilonBase = 0.98;
	private int use = 1;
	private char terminal;
	public int cover = 15;

	/**
	 * Empty constructor
	 */
	public Symbols() {
	}

	/**
	 * Constructs a symbol, specifying the form and ID
	 *
	 * @param id
	 *            the ID
	 * @param f
	 *            the specified form.
	 */
	public Symbols(int id, String f) {
		ID = id;
		form = new String(f);
	}
	
	public Symbols(CategoricalRule rule, String f){
		
	}

	/**
	 * Constructs a symbol, specifying only the id. This constructor also
	 * creates a new form and is called from the speaker when it fails to
	 * produce an utterance.
	 *
	 * @param id
	 *            the ID of the new Symbol
	 */

	public Symbols(int id) {

		ID = id;
		int l;

		if (Math.random() > 0.5)
			l = 6;
		else
			l = 4;

		char[] form0 = new char[l];

		for (int i = 0; i < (int) (0.5 * (double) l); i++) {
			form0[2 * i] = cons.charAt((int) (cons.length() * Math.random()));
			form0[2 * i + 1] = vowels.charAt((int) (vowels.length() * Math.random()));
		}
		form = new String(form0);
	}

	/**
	 * @return the form of the symbol
	 */
	public String getForm() {
		return form;
	}

	/**
	 * @return the identity of the symbol
	 */
	public int getID() {
		return ID;
	}

	/**
	 * @return the string representation of the symbol
	 */
	public String toString() {
		return new String(ID + ": " + terminal + ": " + form + "/" + cover);
	}

	/**
	 * @return true if the symbols' forms are equal
	 */
	public boolean equals(final Object s) {
		if (form.equals(((Symbols) s).form))
			return true;
		return false;
	}

}
