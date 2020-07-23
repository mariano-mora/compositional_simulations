package Util;

import java.io.PrintWriter;

import LGames.Agent;
import LGames.CompositionalAgent2;

/**
 * Implementation of the statistics.
 * 
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class CompositionalStats extends Stats {

	private int[] csTrace = new int[50];
	private double[] dsTrace = new double[50];
	private double[] compTrace = new double[50];
	protected double[] cs = new double[350];
	protected double[] ds = new double[350];
	private double dsSum = 0.0;
	private int csSum = 0;
	protected double[] comp = new double[350];
	private double compSum = 0.0;
	private final int length = 350;
	private int trace = 0;
	private int scale = 1;
	protected int game = 1;
	private final int maxTrace = 50;
	private int lg = 1;
	protected int mid = 175;
	protected int end = 350;
	private int n = 1;
	private int game0 = 1;
	private double cs0 = 0.0;
	private double ds0 = 0.0;
	private double comp0 = 0.0;
	private boolean init = false;
	private int sGramSize = 0;
	private int sHolRules = 0;
	private int hGramSize = 0;
	private int hHolRules = 0;

	private Agent speaker = null;
	private Agent hearer = null;

	private boolean DS = false;
	private boolean CS = true;
	private boolean COMP = true;

	private double compositionality = 0.0;

	/**
	 * constructor
	 */
	public CompositionalStats() {
		ds[0] = 0.0;
		cs[0] = 0.0;
		comp[0] = 0.0;
	}

	/**
	 * This function rescales the arrays for the UI
	 */
	protected int rescale() {

		for (int i = 0; i < 175; i++) {
			cs[i] = cs[2 * i];
			ds[i] = ds[2 * i];
			comp[i] = comp[2 * i];
		}
		return 175;
	}

	/**
	 * This function reinitialises the statistics at the start of each iteration
	 * in the iterated learning model and when a new game is started.
	 *
	 * @param c
	 *            ='n' if a new game is started.
	 */
	public void reinitialize(char c) {
		dsSum = 0.0;
		csSum = 0;
		compSum = 0.0;
		trace = 0;
		game0 = 1;
		if (c == 'n') {
			scale = 1;
			game = 1;
			lg = 1;
			mid = 175;
			end = 350;
			n = 1;
		}
		for (int i = 0; i < dsTrace.length; i++) {
			dsTrace[i] = 0.0;
			csTrace[i] = 0;
			compTrace[i] = 0.0;
		}
		update = false;
	}

	/**
	 * This function calls itself with an additional argument (test=false)
	 *
	 * @param S
	 *            the speaker Agent
	 * @param H
	 *            the hearer Agent
	 * @param t
	 *            the agent's type
	 */
	public void update(final Agent S, final Agent H, int t) {
		update(S, H, t, false);
	}

	/**
	 * Updates the statistics at the end of each language game. The agents are
	 * used to check whether the game was successful or not.
	 *
	 * @param S
	 *            the speaker Agent
	 * @param H
	 *            the hearer Agent
	 * @param t
	 *            the agent's type
	 * @param test
	 *            whether the update is done during testing (true) or not.
	 */
	public void update(final Agent S, final Agent H, int t, boolean test) {

		DS = false;
		CS = false;
		COMP = false;
		speaker = (CompositionalAgent2) S;
		hearer = (CompositionalAgent2) H;

		dsSum -= 0.5 * dsTrace[trace];

		dsTrace[trace] = 0;
		dsTrace[trace] += S.getDS();
		dsTrace[trace] += H.getDS();
		if (dsTrace[trace] > 0.0)
			DS = true;
		dsSum += 0.5 * dsTrace[trace];

		// System.out.println(dsTrace[trace]+" "+dsSum);

		int traceLength = Math.min(game0, maxTrace);
		ds[game] = dsSum / (double) traceLength;

		ds0 = dsSum / (double) traceLength;

		csSum -= csTrace[trace];

		if (!test && speaker.getUtterance() != null && hearer.getUtterance() != null
				&& speaker.getTopic() == hearer.getTopic() && (speaker.getUtterance()).equals(hearer.getUtterance())) {
			csTrace[trace] = 1;
			CS = true;
			// System.out.println("true");
		} else
			csTrace[trace] = 0;
		csSum += csTrace[trace];
		cs[game] = (double) csSum / (double) traceLength;
		cs0 = (double) csSum / (double) traceLength;

		compSum -= 0.5 * compTrace[trace];
		compTrace[trace] = 0.0;
		compTrace[trace] += ((CompositionalAgent2) speaker).getCompositionSuccess();
		compTrace[trace] += ((CompositionalAgent2) hearer).getCompositionSuccess();

		if (compTrace[trace] > 0.0)
			COMP = true;

		compSum += 0.5 * compTrace[trace];
		comp[game] = compSum / (double) traceLength;
		comp0 = compSum / (double) traceLength;

		sGramSize = ((CompositionalAgent2) speaker).getGrammarSize();
		sHolRules = ((CompositionalAgent2) speaker).getHolistic();

		hGramSize = ((CompositionalAgent2) hearer).getGrammarSize();
		hHolRules = ((CompositionalAgent2) hearer).getHolistic();

		if (trace < maxTrace - 1)
			trace++;
		else
			trace = 0;

		if (n == scale) {
			if (game < length - 1)
				game++;
			else {
				game = rescale();
				scale = 2 * scale;
				mid = lg + 1;
				end = 2 * mid;
			}
			n = 1;
		} else
			n++;
		lg++;
		game0++;

		update = false;
	}

	/**
	 * This function writes the statistics of each game to the logfile.
	 *
	 * @param outfile
	 *            the logfile.
	 */
	public void logFile(PrintWriter outfile) {

		if (!init) {
			outfile.print("#lg <DS> DS <CS> CS <COMP> COMP sGramm sHol hGramm hHol");
			outfile.println();
			outfile.print("0 0 false 0 false 0 false");
			outfile.print(" 0 0 0 0");
			outfile.println();
			init = true;
		}

		String X = new String(String.valueOf(ds0));
		String Y = new String(String.valueOf(cs0));
		String Z = new String(String.valueOf(comp0));
		int x = Math.min(X.length(), 4);
		int y = Math.min(Y.length(), 4);
		int z = Math.min(Z.length(), 4);

		outfile.print(lg - 1 + " " + X.substring(0, x) + " " + DS + " " + Y.substring(0, y) + " " + CS + " "
				+ Z.substring(0, z) + " " + COMP);

		outfile.print(" " + sGramSize + " " + sHolRules + " " + hGramSize + " " + hHolRules);
		double a = 0.0;
		double b = 0.0;

		outfile.println();
		if (lg % 10 == 0)
			outfile.flush();
	}

	/**
	 * Function to write the game information to the standard output
	 */

	public void print() {
		System.out
				.println("game " + (lg - 1) + " " + speaker.getGame() + " " + hearer.getGame() + " " + CS + " " + COMP);
	}

	public double[] getComp() {
		return comp;
	}

	public double[] getCS() {
		return cs;
	}

	public double[] getDS() {
		return ds;
	}

	public int getGame() {
		return game;
	}

	public int getMid() {
		return mid;
	}

	public int getEnd() {
		return end;
	}

	public double getICoh() {
		return iCoherence;
	}

	public double getPCoh() {
		return pCoherence;
	}

	public double getCompositionality() {
		return compositionality;
	}

	public void updateCoherence(final double x, final double y) {
	}

	public void updateCoherence(final double x, final double y, final double z) {
		pCoherence = x;
		iCoherence = y;
		compositionality = z;
		update = true;
	}

	public boolean getUpdate() {
		return update;
	}



}
