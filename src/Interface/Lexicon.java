package Interface;

//import java.awt.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import Util.Parameters.AgentType;
import LGames.Agent;
import LGames.CompositionalAgent2;
import LGames.Rules2;

/**
 * Lexicon controls the display of lexicons and grammars
 *
 * <p>
 * Copyright (c) 2004, Paul Vogt
 * 
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class Lexicon extends JOptionPane {

	private JScrollPane lexicon = new JScrollPane();
	private int nColumns = 2;
	private int nRows = 0;
	private int x0 = 10;
	private int y0 = 20;
	private int nFeatures = 6;
	private Agent a0 = null;
	private String[] w = null;
	private int[] id = null;
	private double[][][][] m = null;// new ArrayList();
	private final JButton btn0 = new JButton("Done");
	private final JButton btn1 = new JButton("Help");
	private final Box box = new Box(BoxLayout.Y_AXIS);
	private boolean done = false;
	private int X0 = 12;
	private int Y0 = 12;
	private Dimension viewsDim = new Dimension(500, 600);
	private AgentType aType = AgentType.STRATEGIC;
	private String labels = new String();
	private List[] compRules = null;
	private List[] sharedRules = null;
	private List[] unsharedRules = null;
	private int nRules = 0;
	private int maxCompRules = 0;
	private int maxUnshared = 0;
	private final double threshold = 0.01;

	public Lexicon(final List lAgents, final AgentType aT, final char uType) {

		nColumns = lAgents.size();
		id = new int[nColumns];

		aType = aT;
		if (aType.getValue() < 2)
			initialiseHolisticAgent(lAgents, uType);
		else
			initialiseCompositionalAgent(lAgents, uType);

		btn0.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// actions(e);
				done = true;
			}
		});

		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (aType.getValue() < 2)
					JOptionPane.showMessageDialog(lexicon,
							"This window shows the lexicon where the matrices correspondto the\n meanings. The rows of these matrices correspond to the quality \n dimension of the meaning and the columns to the included values of\n the dimension. Thickness and colour of the squares indicate the \n weight of that meaning. In general larger and brighter is higher.\n\n You can only continue to run THSim if the lexicon is closed.",
							"Help dialog lexicon", JOptionPane.PLAIN_MESSAGE);
				else
					JOptionPane.showMessageDialog(lexicon,
							"This window shows the grammar of the popualtion.\n It first shows the compositional rules, then shared terminal slots\n and, finally, unshared terminal slots. The matrices correspond to the\n meanings. The rows of these matrices correspond to the quality \n dimension of the meaning and the columns to the included values of\n the dimension. Thickness and colour of the squares indicate the \n weight of that meaning. In general larger and brighter is higher.\n\n You can only continue to run THSim if the lexicon is closed.",
							"Help dialog lexicon", JOptionPane.PLAIN_MESSAGE);

			}
		});

		Views lex = new Views();
		// lex.setForeground(Color.white);
		lexicon = new JScrollPane(lex);
		lexicon.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		lexicon.setPreferredSize(viewsDim);
		lexicon.setMaximumSize(viewsDim);
		lexicon.setMinimumSize(new Dimension(600, 400));
		lex.setMinimumSize(new Dimension(600, 400));
		box.add(lexicon);
		setMessage(box);
		setMessageType(PLAIN_MESSAGE);
		setOptionType(YES_OPTION);
		Object[] options = { btn0, btn1 };
		setOptions(options);
		setInitialValue(btn0);
	}

	public void initialiseHolisticAgent(final List lAgents, final char uType) {
		// we take the first agent as a model, the rest will be filled in
		// accordingly.
		// any differences (e.g. words possessed by the other agents, but not the
		// first one)
		// will be discarded.

		a0 = (Agent) lAgents.get(0);
		id[0] = a0.getID();
		labels = a0.getLabels();
		nFeatures = labels.length();

		w = a0.getWords();
		System.out.println(w);
		m = new double[nColumns][w.length][0][0];
		for (int i = 0; i < w.length; i++)
			m[0][i] = a0.getMeanings(w[i], uType);
		Agent a;
		for (int j = 1; j < lAgents.size(); j++) {
			a = (Agent) lAgents.get(j);
			id[j] = a.getID();
			for (int i = 0; i < w.length; i++)
				m[j][i] = a.getMeanings(w[i], uType);
		}
		viewsDim = new Dimension(100 + X0 + nColumns * (10 * X0 + 2), 2 * y0 + w.length * (nFeatures * Y0 + 2));
	}

	public List getCompRules(List r) {
		List retval = new ArrayList();
		int n = 0;
		while (n < r.size()) {
			if (((Rules2) r.get(n)).getCompositional()) {
				retval.add((Rules2) r.get(n));
				r.remove(n);
			} else
				n++;
		}
		return retval;
	}

	public void initialiseCompositionalAgent(final List lAgents, final char uType) {
		int n = lAgents.size() - 1;
		a0 = (Agent) lAgents.get(n);
		id[n] = a0.getID();
		labels = a0.getLabels();
		nFeatures = labels.length();

		compRules = new ArrayList[nColumns];
		sharedRules = new ArrayList[nColumns];
		unsharedRules = new ArrayList[nColumns];

		List r = ((CompositionalAgent2) a0).getRules();
		compRules[n] = getCompRules(r);

		boolean[] shared = new boolean[r.size()];
		for (int i = 0; i < shared.length; i++)
			shared[i] = false;
		CompositionalAgent2 a;
		List r1 = new ArrayList();
		int sh = -1;
		for (int i = 0; i < n; i++) {
			sharedRules[i] = new ArrayList();
			a = (CompositionalAgent2) lAgents.get(i);
			id[i] = a.getID();
			r1 = a.getRules();
			compRules[i] = getCompRules(r1);
			sh = -1;
			for (int j = 0; j < r.size(); j++) {
				for (int k = 0; k < r1.size() && sh < 0; k++)
					if (((Rules2) r1.get(k)).equals((Rules2) r.get(j)))
						sh = k;
				if (sh >= 0) {
					sharedRules[i].add(new Rules2((Rules2) r1.get(sh)));
					r1.remove(sh);
					shared[j] = true;
				} else
					sharedRules[i].add(null);
				sh = -1;
			}
			unsharedRules[i] = new ArrayList(r1);
		}
		sharedRules[n] = new ArrayList();
		int j = 0;
		int p = 0;
		for (int i = 0; i < shared.length; i++) {
			if (shared[i]) {
				sharedRules[n].add(r.get(j));
				r.remove(j);
				p++;
			} else {
				for (int k = 0; k < n; k++)
					sharedRules[k].remove(p);
				j++;
			}
		}
		unsharedRules[n] = new ArrayList(r);
		int k = 0;
		boolean print = false;
		while (k < sharedRules[n].size()) {
			print = false;
			for (int i = 0; i <= n && !print; i++)
				if (((Rules2) sharedRules[i].get(k)).getWeight() >= threshold)
					print = true;
			if (!print)
				for (int i = 0; i <= n; i++)
					sharedRules[i].remove(k);
			else
				k++;
		}

		for (int i = 0; i <= n; i++) {
			k = 0;
			while (k < compRules[i].size())
				if (((Rules2) compRules[i].get(k)).getWeight() < threshold)
					compRules[i].remove(k);
				else
					k++;
			if (compRules[i].size() > maxCompRules)
				maxCompRules = compRules[i].size();
			k = 0;
			while (k < unsharedRules[i].size())
				if (((Rules2) unsharedRules[i].get(k)).getWeight() < threshold)
					unsharedRules[i].remove(k);
				else
					k++;

			if (unsharedRules[n].size() > maxUnshared)
				maxUnshared = unsharedRules[n].size();
		}
		nRules = maxCompRules + sharedRules[n].size() + maxUnshared;
		viewsDim = new Dimension(100 + nColumns * (10 * X0 + 2), 100 + nRules * ((nFeatures + 1) * Y0 + 2));
	}

	public boolean isDone() {
		return done;
	}

	public void paintComponent(final Graphics tGraphics) {
		// lex.repaint();
	}

	class Views extends JComponent {

		public Views() {
		}

		private Color getColor(float y) {
			float x = (float) 1. - y;
			/*
			 * switch (s){ case 1: return Color.black; case 2: return
			 * Color.blue; case 3: return Color.red; case 4: return
			 * Color.magenta; case 5: return Color.orange; case 6: return
			 * Color.pink; case 7: return Color.green; case 8: return
			 * Color.cyan; case 9: return Color.yellow; case 10: return
			 * Color.white; } return Color.lightGray;
			 */
			return new Color(x, x, x);
		}

		private int getSize(double x) {
			if (x > 0.9)
				return 10;
			if (x > 0.8)
				return 9;
			if (x > 0.7)
				return 8;
			if (x > 0.6)
				return 7;
			if (x > 0.5)
				return 6;
			if (x > 0.4)
				return 5;
			if (x > 0.3)
				return 4;
			if (x > 0.2)
				return 3;
			if (x > 0.1)
				return 2;
			if (x > 0.0)
				return 1;
			return 0;
		}

		private int getDX(int n) {
			if (n <= 2)
				return 5;
			if (n <= 4)
				return 4;
			if (n <= 6)
				return 3;
			if (n <= 8)
				return 2;
			return 1;
		}

		public void paintComponent(final Graphics tGraphics) {

			if (aType.getValue() < 2)
				paintLexicon(tGraphics);
			else
				paintGrammar(tGraphics);
		}

		private void paintLexicon(final Graphics tGraphics) {
			tGraphics.setColor(Color.white);
			tGraphics.fillRect(0, 0, 200 + X0 + nColumns * (10 * X0 + 2), 2 * y0 + w.length * (nFeatures * Y0 + 2));
			tGraphics.setColor(Color.black);

			int y = y0;
			int y1 = y0 - 1;
			int x1 = x0 + 108;
			int x;
			double[][] M = null;
			Color c = null;
			int s = 0;
			int minus = 0;
			boolean update = false;
			int dX = 0;
			for (int i = 0; i < id.length; i++)
				tGraphics.drawString("A" + id[i], x1 + i * (10 * X0 + 2), y1);

			for (int i = 0; i < w.length; i++) {
				y = y0 + (i - minus) * (nFeatures * Y0 + 2);
				// y+=nFeatures*11;
				tGraphics.setColor(Color.black);
				tGraphics.drawString(w[i], x0, y + 11);

				for (int k = 0; k < labels.length(); k++)
					tGraphics.drawString(labels.substring(k, k + 1), x0 + 50, y + (k + 1) * Y0);

				update = false;
				for (int j = 0; j < nColumns; j++) {
					x = x0 + 60 + j * (10 * X0 + 2);
					M = m[j][i];
					for (int k = 0; k < 10; k++)
						for (int l = 0; l < nFeatures; l++) {
							// tGraphics.setColor(Color.black);
							tGraphics.drawRect(x + k * X0, y + l * Y0, X0, Y0);
							s = getSize(M[k][l]);
							if (s > 0) {
								dX = getDX(s);
								// c=getColor((float)M[k][l]);
								// tGraphics.setColor(c);
								tGraphics.fillRect(x + k * X0 + dX, y + l * Y0 + dX, s, s);
								// k=10;l=nFeatures;
								update = true;
							}
							// tGraphics.setColor(Color.white);
						}
				}
				if (!update) {
					minus++;
					tGraphics.setColor(Color.white);
					tGraphics.drawString(w[i], x0, y + 11);
				}
			}
		}

		public void paintGrammar(final Graphics tGraphics) {
			tGraphics.setColor(Color.white);
			tGraphics.fillRect(0, 0, 100 + nColumns * (10 * X0 + 2), 100 + nRules * ((nFeatures + 1) * Y0 + 2));
			tGraphics.setColor(Color.black);
			int y = y0;
			int y1 = y0 - 1;
			int x1 = x0 + 90;
			int x;
			double[][] M = null;
			double z;
			Color c = null;
			int s = 0;
			int minus = 0;
			boolean update = false;
			Rules2 r = null;
			String f;
			int dX = 0;
			for (int i = 0; i < id.length; i++)
				tGraphics.drawString("A" + id[i], x1 + i * (10 * X0 + 12), y1);

			String ruleLabel = new String();
			for (int i = 0; i < maxCompRules; i++) {
				y = y0 + (i + 1) * Y0;
				tGraphics.drawString("S->", x0, y);
				for (int j = 0; j < nColumns; j++) {
					if (i < compRules[j].size()) {
						ruleLabel = ((Rules2) compRules[j].get(i)).getString(labels);
						tGraphics.drawString(ruleLabel, x0 + 60 + j * (10 * X0 + 12), y);
						z = ((Rules2) compRules[j].get(i)).getWeight();
						s = getSize(z);
						// c=getColor((float)z);
						// tGraphics.setColor(c);
						dX = getDX(s);
						tGraphics.fillRect(x0 + 60 + j * (10 * X0) + 9 * X0 + dX, y - Y0 + 1 + dX, s, s);
						tGraphics.setColor(Color.black);
					}
				}
			}

			y1 = y + 2 * Y0;
			int dim = 1;
			y = y1;
			int n = nColumns - 1;
			for (int i = 0; i < sharedRules[n].size(); i++) {

				ruleLabel = ((Rules2) sharedRules[n].get(i)).getString(labels);
				tGraphics.drawString(ruleLabel, x0, y);
				dim = ruleLabel.length();
				for (int k = 0; k < dim; k++)
					tGraphics.drawString(ruleLabel.substring(k, k + 1), x0 + 50, y + (k + 1) * Y0);

				for (int j = 0; j < nColumns; j++) {
					r = (Rules2) sharedRules[j].get(i);
					if (r != null) {
						M = r.getMeaning(labels, ruleLabel, dim);
						f = r.getExpression();
						x = x0 + 60 + j * (10 * X0 + 12);
						tGraphics.drawString(f, x, y);
						z = r.getWeight();
						s = getSize(z);
						// c=getColor((float)z);
						// tGraphics.setColor(c);
						dX = getDX(s);
						tGraphics.fillRect(x + 9 * X0 + dX, y - Y0 + 1 + dX, s, s);
						// tGraphics.setColor(Color.black);
						for (int k = 0; k < 10; k++) {
							for (int l = 0; l < dim; l++) {
								tGraphics.drawRect(x + k * X0, y + l * Y0 + 2, X0, Y0);
								s = getSize(M[k][l]);
								if (s > 0) {
									// c=getColor((float)M[k][l]);
									// tGraphics.setColor(c);
									dX = getDX(s);
									tGraphics.fillRect(x + k * X0 + dX, y + l * Y0 + 2 + dX, s, s);
									// tGraphics.setColor(Color.black);
								}
							}
						}
					}
				}
				y += (dim + 1) * Y0 + 2;
			}
			y1 = y;// +(dim+1)*Y0;
			for (int j = 0; j < nColumns; j++) {
				y = y1;
				for (int i = 0; i < unsharedRules[j].size(); i++) {
					r = (Rules2) unsharedRules[j].get(i);
					ruleLabel = r.getString(labels);
					dim = ruleLabel.length();
					for (int k = 0; k < dim; k++)
						tGraphics.drawString(ruleLabel.substring(k, k + 1), x0 + 50 + j * (10 * X0 + 12),
								y + (k + 1) * Y0);
					M = r.getMeaning(labels, ruleLabel, dim);
					f = r.getExpression();
					x = x0 + 60 + j * (10 * X0 + 12);
					tGraphics.drawString(f, x, y);
					z = r.getWeight();
					s = getSize(z);
					// c=getColor((float)z);
					// tGraphics.setColor(c);
					dX = getDX(s);
					tGraphics.fillRect(x + 9 * X0 + dX, y - Y0 + 1 + dX, s, s);
					// tGraphics.setColor(Color.black);
					for (int k = 0; k < 10; k++) {
						for (int l = 0; l < dim; l++) {
							tGraphics.drawRect(x + k * X0, y + l * Y0 + 2, X0, Y0);
							s = getSize(M[k][l]);
							if (s > 0) {
								// c=getColor((float)M[k][l]);
								// tGraphics.setColor(c);
								dX = getDX(s);
								tGraphics.fillRect(x + k * X0 + dX, y + l * Y0 + 2 + dX, s, s);
								// tGraphics.setColor(Color.black);
							}
						}
					}
					y += (dim + 1) * Y0 + 2;
				}
			}
		}

		public Dimension getPreferredSize() {

			return viewsDim;// new Dimension(500,600);//viewsDim;
		}
	};

}
