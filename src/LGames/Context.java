package LGames;

import java.util.*;
import java.awt.Color;
import Util.Utils;

/**
 * This class implements the construction and maintenance of the context Each
 * element of the context is a geometrical figure that is specified by certain
 * features. Possible figures include rectangles, squares, triangles and
 * circles. The context keeps also track of the categorisation of its
 * featureVector.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class Context {

	private double r = 0.0;
	protected int h = 0;
	protected int b = 0;
	private double g = 0.0;
	private double area = 0.0;
	private double maxArea = 0.0;
	protected final double maxWidth = 350.0;
	protected final double maxHeight = 250.0;
	protected final double maxHeight0 = 50.0;
	protected final double minHeight0 = 10.0;
	public int[][] description;
	protected String labels = "RGBSXY";
	protected final int nShapes = 10;
	protected final int nColours = 12;
	public double[][] featureVector;
	private int[] category;
	public boolean[] distinctive;
	public boolean[] foa;
	public int[] types;
	public int[] height;
	public int[] width;
	public int[] pointX;
	public int[] pointY;
	public float[] red;
	public float[] green;
	public float[] blue;
	private boolean fixedColors;
	private Meaning meaning;
	protected Random random;
	protected int cxtSize;
	protected boolean[] features;
	private int[][] ts;
	private int tsLength;
	protected ContextObject[] objects;

	/**
	 * Empty constructor
	 */
	public Context() {
	}

	/**
	 * This constructor creates a context using predefined meanings. Used in a
	 * test function.
	 */
	public Context(final int nMeanings, final int cxtSize) {

		category = new int[cxtSize];
		foa = new boolean[cxtSize];
		distinctive = new boolean[cxtSize];

		featureVector = new double[cxtSize][1];
		description = new int[cxtSize][1];

		for (int i = 0; i < cxtSize; i++) {
			boolean create = false;
			while (!create) {
				int m = (int) Math.round((double) nMeanings * Math.random() + 0.500001);
				if (m < 1)
					m = 1;
				else if (m > nMeanings)
					m = nMeanings;
				boolean exists = false;
				for (int j = 0; j < i && !exists; j++)
					if (m == category[j])
						exists = true;

				if (!exists) {
					category[i] = m;
					foa[i] = true;
					distinctive[i] = true;
					create = true;
				}
			}
		}

	}

	/**
	 * This contructor is a copy constructor that adds noise to the original
	 * context.
	 *
	 * @param C
	 *            The original context
	 * @param noise
	 *            The perceptual noise
	 */
	private double g(double x, double s) {
		if (x >= 0)
			return 2. - Math.exp(-(x * x) / (4. * s * s));
		return Math.exp(-(x * x) / (4. * s * s));
	}

	public Context(final Context C, final double noise) {
		height = C.height;
		width = C.width;
		types = C.types;
		red = C.red;
		blue = C.blue;
		green = C.green;
		distinctive = new boolean[C.distinctive.length];
		foa = new boolean[C.distinctive.length];
		category = new int[C.category.length];
		featureVector = new double[C.distinctive.length][C.featureVector[0].length];
		description = new int[C.description.length][C.featureVector[0].length];
		labels = C.getLabels();
		double sigma = noise;
		double x;
		for (int i = 0; i < C.distinctive.length; i++) {
			if (noise > 0.0) {
				for (int n = 0; n < C.featureVector[i].length; n++) {
					// The noise is a gaussian around 0, which is multiplied by
					// the feature
					x = Math.random();
					featureVector[i][n] = C.featureVector[i][n] * g(x - .5, sigma);
					if (featureVector[i][n] > 1.0)
						featureVector[i][n] = 1.0;
					else if (featureVector[i][n] < 0.0)
						featureVector[i][n] = 0.0;
				}
			} else
				for (int n = 0; n < C.featureVector[i].length; n++)
					featureVector[i][n] = C.featureVector[i][n];
			foa[i] = C.foa[i];
			distinctive[i] = C.distinctive[i];
			category[i] = C.category[i];
			description[i] = C.description[i];
		}
	}

	/**
	 * This function selects a predefined colour indicated by a randomly
	 * generated integer
	 *
	 * @param c
	 *            the randomly generated integer determining the colour
	 * @return the colour represented by the colour space used in Java (RGB)
	 */
	protected Color getColor(int c) {

		switch (c) {
		case 0:
			return Color.black;
		case 1:
			return Color.blue;
		case 2:
			return Color.cyan;
		case 3:
			return Color.green;
		case 4:
			return Color.magenta;
		case 5:
			return Color.orange;
		case 6:
			return Color.pink;
		case 7:
			return Color.red;
		case 8:
			return Color.yellow;
		case 9:
			return Color.gray;
		case 10:
			return Color.darkGray;
		case 11:
			return Color.lightGray;
		default:
			return Color.white;
		}
	}

	/**
	 * This function constructs the training set, which is a subset of all
	 * objects. It is used to implement a bottleneck in the compositionality
	 * study.
	 *
	 * @param size
	 *            the size of the subset.
	 *
	 * @return a 2-dimensional array. The first dimension relates to the object
	 *         number, the second has 2 places, the first specifies the shape,
	 *         the second the colour.
	 */
	public int[][] setTrainingSet(int size) {
		int[][] ts = new int[size][2];
		boolean set;
		int s, c;
		for (int i = 0; i < size; i++) {
			set = false;
			while (!set) {
				set = true;
				s = (int) Math.round((double) nShapes * Math.random() - 0.50001);
				if (s < 0)
					s = 0;
				c = (int) Math.round((double) nColours * Math.random() - 0.50001);
				if (c < 0)
					c = 0;
				for (int j = 0; j < i; j++)
					if (ts[j][0] == s && ts[j][1] == c)
						set = false;
				if (set) {
					ts[i][0] = s;
					ts[i][1] = c;
				}
			}
		}
		return ts;
	}

	protected boolean check_boundaries(final int x, final int y, final int b, final int h, final int n) {
		if ((pointX[n] >= x && pointX[n] <= (x + b)) || // x<=pX<=x+b
				((pointX[n] + width[n]) >= x && (pointX[n] + width[n]) <= (x + b)) || // x<=pX+pb<=x+b
				(pointX[n] <= x && (pointX[n] + width[n]) >= x) || // pX<=x<=pX+pb
				(pointX[n] <= (x + b) && (pointX[n] + width[n]) >= (x + b))) {// pX<=x+b<=pX+pb
			if (pointY[n] >= y && pointY[n] <= (y + h))
				return false;// y<=pY<=y+h
			if ((pointY[n] + height[n]) >= y && (pointY[n] + height[n]) <= (y + h))
				return false;// y<=pY+ph<=y+h
			if (pointY[n] <= y && (pointY[n] + height[n]) >= y)
				return false;// py<=y<=py+ph
			if (pointY[n] <= (y + h) && (pointY[n] + height[n]) >= (y + h))
				return false;// py<=y+h<=py+ph
		}
		return true;
	}

	/**
	 * This constructor constructs a new context from scratch.
	 *
	 * @param cxtSize
	 *            The size of the context
	 * @param features
	 *            This is a boolean array telling which features are selected of
	 *            the RGBSXY
	 * @param fixedColors
	 *            This boolean tells if the colours should be generated randomly
	 *            (true) or selected from a list (false)
	 */
	public Context(final int cxtSize, final boolean[] features, final boolean fixedColors, Random random) {
		this.random = random;
		this.fixedColors = fixedColors;
		this.cxtSize = cxtSize;
		this.features = features;
		height = new int[cxtSize];
		width = new int[cxtSize];
		types = new int[cxtSize];
		pointX = new int[cxtSize];
		pointY = new int[cxtSize];
		distinctive = new boolean[cxtSize];
		foa = new boolean[cxtSize];
		category = new int[cxtSize];
		red = new float[cxtSize];
		blue = new float[cxtSize];
		green = new float[cxtSize];
		labels = new String();

	}

	/**
	 * This constructor constructs a context based on the training set given.
	 *
	 * @param cxtSize
	 *            number of objects in the context
	 * @param features
	 *            an array of features that are used
	 * @param fixedColors
	 *            whether colors are selected from a fixed set (true) or not
	 *            (false)
	 * @param ts
	 *            the training set.
	 * @param tslength
	 *            the size of the first dimension (ie. the size of the training
	 *            set)
	 */
	public Context(final int cxtSize, final boolean[] features, final boolean fixedColors, final int[][] ts,
			final int tslength) {

		
		this.cxtSize = cxtSize;
		this.features = features;
		this.ts = ts;
		this.tsLength = tslength;
		height = new int[cxtSize];
		width = new int[cxtSize];
		types = new int[cxtSize];
		pointX = new int[cxtSize];
		pointY = new int[cxtSize];
		distinctive = new boolean[cxtSize];
		foa = new boolean[cxtSize];
		category = new int[cxtSize];
		red = new float[cxtSize];
		blue = new float[cxtSize];
		green = new float[cxtSize];
		labels = new String();
		

	}

	/**
	 * Implement this in subclasses, don't do things in constructor!
	 */
	public void buildContext() {
		int colorID = 0;
		Color color;
		int featureLength = 0;

		if (features[0] == true) {
			labels = labels.concat("R");
			featureLength++;
		}
		if (features[1]) {
			labels = labels.concat("G");
			featureLength++;
		}
		if (features[2]) {
			labels = labels.concat("B");
			featureLength++;
		}
		if (features[3]) {
			labels = labels.concat("S");
			featureLength++;
		}
		if (features[4]) {
			labels = labels.concat("X");
			featureLength++;
		}
		if (features[5]) {
			labels = labels.concat("Y");
			featureLength++;
		}
		featureVector = new double[cxtSize][featureLength];
		description = new int[cxtSize][featureLength];

		maxArea = maxHeight * maxWidth;
		int range = (int) (maxHeight0 - minHeight0) + 1;
		for (int i = 0; i < cxtSize; i++) {
			int type = random.nextInt(nShapes);
			h = random.nextInt(range) + (int) minHeight0;
			b = random.nextInt(range) + (int) minHeight0;

			switch (type) {
			case 0: {// constructie van een cirkel
				r = 0.5 * (maxHeight0 + minHeight0) * random.nextDouble() + 0.5 * minHeight0;// straal
				h = (int) (2.0 * r);// hoogte
				b = (int) (2.0 * r);// breedte
				area = Math.PI * r * r;// oppervlak
				break;
			}
			case 1: {// constructie van een driehoek
				area = 0.5 * (double) h * (double) b;
				break;
			}
			case 2: {// constructie van een rechthoek
				area = (double) h * (double) b;
				break;
			}
			case 3: {// constructie van een vierkant
				b = h;
				area = (double) h * (double) b;
				break;
			}
			case 4:// 5 hoek
				area = 0.75 * (double) h * (double) b;
				break;
			case 5:// 6 hoek
				area = 2.0 * (double) h * (double) b / 4.0;
				break;
			case 6:// irregular 6 hoek 1
				area = 5.0 * (double) h * (double) b / 6.0;
				break;
			case 7:// irregular 6 hoek 2
				area = 8.0 * (double) h * (double) b / 9.0;
				break;
			case 8:// irregular 5 hoek
				area = 17.0 * (double) h * (double) b / 18.0;
				break;
			case 9:// cross
				area = 5.0 * (double) h * (double) b / 9.0;
				break;
			default:
				System.out.println("error constructing shapes, type=" + type);
				System.exit(1);
				break;
			}

			boolean fits = false;
			int pX = 0;
			int pY = 0;
			while (!fits) {
				pX = (int) ((maxWidth - b) * random.nextDouble());
				pY = (int) ((maxHeight - h) * random.nextDouble());
				fits = true;
				for (int j = 0; j < i && fits; j++)
					fits = check_boundaries(pX, pY, b, h, j);
			}

			int j = 0;
			if (!fixedColors) {
				boolean distinct = false;
				while (!distinct) {
					distinct = true;
					if (features[0]) {// Red
						featureVector[i][j] = Math.random();
						red[i] = (float) featureVector[i][j];
						description[i][j] = Utils.index(red[i]);
						j++;
					} else
						red[i] = (float) Math.random();
					if (features[1]) {// Green
						featureVector[i][j] = Math.random();
						green[i] = (float) featureVector[i][j];
						description[i][j] = Utils.index(green[i]);
						j++;
					} else
						green[i] = (float) Math.random();
					if (features[2]) {// Blue
						featureVector[i][j] = Math.random();
						blue[i] = (float) featureVector[i][j];
						description[i][j] = Utils.index(blue[i]);
						j++;
					} else
						blue[i] = (float) Math.random();
					for (int n = 0; n < i && featureLength == 3; n++) {
						boolean flag = true;
						for (int m = 0; m < 3; m++)
							if (description[n][m] == description[i][m] && features[m])
								flag = false;
						if (!flag) {
							distinct = false;
							// System.out.println("not distinct");
							j = 0;
						}
					}
				}
			} else if (features[0] && features[1] && features[2]) {
				boolean flag = false;
				while (!flag) {
					flag = true;
					colorID = random.nextInt(nColours);
					for (int n = 0; n < i && featureLength == 3; n++)
						if (description[n][0] == colorID)
							flag = false;
				}
				color = getColor(colorID);
				featureVector[i][0] = (double) color.getRed() / 255.0;
				red[i] = (float) featureVector[i][0];
				featureVector[i][1] = (double) color.getGreen() / 255.0;
				green[i] = (float) featureVector[i][1];
				if (colorID < 10) {
					description[i][0] = 0;
					description[i][1] = colorID;
				} else {
					description[i][0] = 1;
					description[i][1] = colorID - 10;
				}
				featureVector[i][2] = (double) color.getBlue() / 255.0;
				blue[i] = (float) featureVector[i][2];
				description[i][2] = 0;
				j = 3;
			}
			if (features[3]) {// Filling ratio
				featureVector[i][j] = 2.0 * (area / (double) (h * b) - 0.5);
				description[i][j] = type;
				j++;
			}
			if (features[4]) {// X-axis
				featureVector[i][j] = ((double) pX + 0.5 * (double) b) / maxWidth;
				description[i][j] = Utils.index((float) featureVector[i][j]);
				j++;
			}
			if (features[5]) {// Y-axis
				featureVector[i][j] = ((double) pY + 0.5 * (double) h) / maxHeight;
				description[i][j] = Utils.index((float) featureVector[i][j]);
				j++;
			}

			types[i] = type;
			height[i] = h;
			width[i] = b;
			pointX[i] = (int) pX;
			pointY[i] = (int) pY;
			distinctive[i] = false;
			foa[i] = true;
			category[i] = -1;
		}
	}

	public void buildContextTrainingSet() {
		int colorID = 0;
		Color color;
		int featureLength = 0;
		
		if (features[0] == true) {
			labels = labels.concat("R");
			featureLength++;
		}
		if (features[1]) {
			labels = labels.concat("G");
			featureLength++;
		}
		if (features[2]) {
			labels = labels.concat("B");
			featureLength++;
		}
		if (features[3]) {
			labels = labels.concat("S");
			featureLength++;
		}
		if (features[4]) {
			labels = labels.concat("X");
			featureLength++;
		}
		if (features[5]) {
			labels = labels.concat("Y");
			featureLength++;
		}

		featureVector = new double[cxtSize][featureLength];
		description = new int[cxtSize][featureLength];

		maxArea = maxHeight * maxWidth;

		for (int i = 0; i < cxtSize; i++) {

			int type = (int) Math.round((double) tsLength * Math.random() - 0.50001);
			if (type < 0)
				type = 0;

			h = (int) (maxHeight0 * Math.random() + minHeight0);
			b = (int) (maxHeight0 * Math.random() + minHeight0);

			switch (ts[type][0]) {
			case 0: {// constructie van een cirkel
				r = 0.5 * (maxHeight0 + minHeight0) * Math.random() + 0.5 * minHeight0;// straal
				h = (int) (2.0 * r);// hoogte
				b = (int) (2.0 * r);// breedte
				area = Math.PI * r * r;// oppervlak
				break;
			}
			case 1: {// constructie van een driehoek
				area = 0.5 * (double) h * (double) b;
				break;
			}
			case 2: {// constructie van een rechthoek
				area = (double) h * (double) b;
				break;
			}
			case 3: {// constructie van een vierkant
				b = h;
				area = (double) h * (double) b;
				break;
			}
			case 4:// 5 hoek
				area = 0.75 * (double) h * (double) b;
				break;
			case 5:// 6 hoek
				area = 2.0 * (double) h * (double) b / 4.0;
				break;
			case 6:// irregular 6 hoek 1
				area = 5.0 * (double) h * (double) b / 6.0;
				break;
			case 7:// irregular 6 hoek 2
				area = 8.0 * (double) h * (double) b / 9.0;
				break;
			case 8:// irregular 5 hoek
				area = 17.0 * (double) h * (double) b / 18.0;
				break;
			case 9:// cross
				area = 5.0 * (double) h * (double) b / 9.0;
				break;
			default:
				System.out.println("error constructing shapes, type=" + type);
				System.exit(1);
				break;
			}

			boolean fits = false;
			int pX = 0;
			int pY = 0;
			while (!fits) {
				pX = (int) ((maxWidth - b) * Math.random());
				pY = (int) ((maxHeight - h) * Math.random());
				fits = true;
				for (int j = 0; j < i && fits; j++)
					fits = check_boundaries(pX, pY, b, h, j);
			}

			int j = 0;
			if (!fixedColors) {
				boolean distinct = false;
				while (!distinct) {
					distinct = true;
					if (features[0]) {// Red
						featureVector[i][j] = Math.random();
						red[i] = (float) featureVector[i][j];
						description[i][j] = Utils.index(red[i]);
						j++;
					} else
						red[i] = (float) Math.random();
					if (features[1]) {// Green
						featureVector[i][j] = Math.random();
						green[i] = (float) featureVector[i][j];
						description[i][j] = Utils.index(green[i]);
						j++;
					} else
						green[i] = (float) Math.random();
					if (features[2]) {// Blue
						featureVector[i][j] = Math.random();
						blue[i] = (float) featureVector[i][j];
						description[i][j] = Utils.index(blue[i]);
						j++;
					} else
						blue[i] = (float) Math.random();
					for (int n = 0; n < i && featureLength == 3; n++) {
						boolean flag = true;
						for (int m = 0; m < 3; m++)
							if (description[n][m] == description[i][m] && features[m])
								flag = false;
						if (!flag) {
							distinct = false;
							// System.out.println("not distinct");
							j = 0;
						}
					}
				}
			} else if (features[0] && features[1] && features[2]) {
				boolean flag = false;
				colorID = ts[type][1];
				color = getColor(colorID);
				featureVector[i][0] = (double) color.getRed() / 255.0;
				red[i] = (float) featureVector[i][0];
				featureVector[i][1] = (double) color.getGreen() / 255.0;
				green[i] = (float) featureVector[i][1];
				if (colorID < 10) {
					description[i][0] = 0;
					description[i][1] = colorID;
				} else {
					description[i][0] = 1;
					description[i][1] = colorID - 10;
				}
				featureVector[i][2] = (double) color.getBlue() / 255.0;
				blue[i] = (float) featureVector[i][2];
				description[i][2] = 0;
				j = 3;
			}
			if (features[3]) {// Filling ratio
				featureVector[i][j] = 2.0 * (area / (double) (h * b) - 0.5);
				description[i][j] = ts[type][0];
				j++;
			}
			if (features[4]) {// X-axis
				featureVector[i][j] = ((double) pX + 0.5 * (double) b) / maxWidth;
				description[i][j] = Utils.index((float) featureVector[i][j]);
				j++;
			}
			if (features[5]) {// Y-axis
				featureVector[i][j] = ((double) pY + 0.5 * (double) h) / maxHeight;
				description[i][j] = Utils.index((float) featureVector[i][j]);
				j++;
			}

			types[i] = type;
			height[i] = h;
			width[i] = b;
			pointX[i] = (int) pX;
			pointY[i] = (int) pY;
			distinctive[i] = false;
			foa[i] = true;
			category[i] = -1;
		}

	}

	/**
	 * Returns the string representation of the labels that indicate the
	 * selected features
	 */
	public String getLabels() {
		return labels;
	}

	/**
	 * Sets the focus of attention
	 */
	public void setFOA(int t, int length) {
		int out;
		for (int i = length; i < foa.length; i++) {
			boolean flag = false;
			while (!flag) {
				out = (int) Math.round(foa.length * Math.random() - 0.50001);
				if (out < 0)
					out = 0;
				if (out != t && foa[out]) {
					foa[out] = false;
					flag = true;
				}
			}
		}
		// System.out.println("set foa");
	}

	/**
	 * Returns the feature vector of segment T
	 */
	public double[] getFV(int T) {
		return featureVector[T];
	}

	/**
	 * Sets the meaning of the topic T to meaning M
	 */
	public void setMeaning(Meaning M, int T) {

		meaning = M;
		distinctive[T] = true;
		// meanings.set(T,M);
	}

	/**
	 * Initialises a discrimination game by setting for all objects distinctive
	 * to false;
	 */
	public void initDGame() {
		for (int i = 0; i < distinctive.length; i++) {
			distinctive[i] = false;
			category[i] = -1;
		}
		meaning = null;
	}

	/**
	 * @return the meaning of the topic
	 */
	public Meaning getMeaning() {
		return meaning;
	}

	/**
	 * @return a string describing the object. Helps to analyse what objects are
	 *         used
	 */
	public String getDString(int t) {
		String retval = new String(String.valueOf(description[t][0]));
		for (int i = 1; i < description[t].length; i++)
			retval = retval.concat(String.valueOf(description[t][i]));
		return retval;
	}

	/**
	 * Writes the context to the standard output
	 */
	public void print(final List o) {
		System.out.println("Context:");
		for (int i = 0; i < foa.length; i++) {
			if (foa[i]) {
				System.out.print("fv[" + i + "]=(");
				for (int j = 0; j < featureVector[i].length; j++) {
					String Y = new String(String.valueOf(featureVector[i][j]));
					int y = Math.min(Y.length(), 4);
					System.out.print(Y.substring(0, y));
					if (j < featureVector[i].length - 1)
						System.out.print(",");
				}
				if (category[i] > 0)
					System.out.println(") -> M" + (Meaning) o.get(category[i]));
				else
					System.out.println("M=null");
			}
		}
	}

	/**
	 * Writes the feature vectors of the context to the standard output
	 */
	public void print(int n) {
		System.out.print(n + ": ");
		for (int i = 0; i < featureVector[n].length; i++)
			System.out.print(featureVector[n][i] + " ");
		System.out.println();
	}

	/**
	 * @param i
	 *            the index of an object in the context
	 * @return the index of the category of object i
	 */
	public int getCategory(int i) {
		return category[i];
	}

	/**
	 * This function sets the category of an object
	 *
	 * @param i
	 *            the index of the object
	 * @param n
	 *            the category (Meaning/ANMeaning) of the object
	 */
	public void setCategory(final int i, final int n) {
		category[i] = n;
	}

	/**
	 * This function sets the categories of the context
	 *
	 * @param n
	 *            an array with the categories (Meaning/ANMeaning) of the
	 *            objects
	 */

	public void setCategory(final int[] n) {
		category = n;
	}

	/**
	 * Basically returns the context size
	 */

	public int categoryLength() {
		return category.length;
	}

	public int getCxtSize() {
		return cxtSize;
	}

	public void setCxtSize(int cxtSize) {
		this.cxtSize = cxtSize;
	}

	public ContextObject[] getObjects() {
		return objects;
	}

	public void setObjects(ContextObject[] objects) {
		this.objects = objects;
	}
}
