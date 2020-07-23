package Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;

/** Utilities for the talking heads simulation.
 * This class contains all kinds of functions that appeared useful in various places. 
 * 
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 * @author Paul Vogt
 * @version $4.0.3$

*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import LGames.Agent;
import LGames.CategoryMeaning;
import LGames.Cognition.Category;
import LGames.CompositionalAgent2;
import LGames.ContextStrategic.Destination;
import LGames.ContextStrategic.ObjectColor;
import LGames.ContextStrategic.Shape;
import LGames.GrammarException;
import LGames.Ontology;
import LGames.Population;
import LGames.Strategy.StrategyType;
import LGames.StrategyAgent;
import LGames.Umpire;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class Utils {

	public static CharSequence tab_delimiter = "\t";
	public static CharSequence comma_delimiter = ",";
	public static String umpire_filename = "umpire.cvs";
	public static String agents_filename = "agents.csv";
	public static String syntaxCats = "ABEFGHIJKLMNOPQRTUVWXYZ";
	private static int syntaxIndex = 0;
	private static DecimalFormat df2 = new DecimalFormat("0.######E0");
	private static DecimalFormat df3 = new DecimalFormat("#.######");
	public static Map<Integer, List<Category>> catMap = new HashMap<Integer, List<Category>>();
	public static Map<Integer, Character> syntaxMap = new HashMap<Integer, Character>();
	private static String SYNTAX_MAP_FILE_NAME = "coverKey.txt";
	private static Ontology<CategoryMeaning> ontology;
	private static int maxReducedColors = 2;
	private static int maxReducedShapes = 2;

	static{
		syntaxMap.put(7,'S');
	}
	/**
	 * Calculates the Eucledian distance between two arrays.
	 *
	 * @param x
	 *            double array 1
	 * @param y
	 *            double array 2
	 * @return distance between x and y, or -1 if the dimensions of x and y
	 *         differ.
	 */
	public static double distance(double[] x, double[] y) {
		double sum = 0;
		if (x.length == y.length)
			for (int i = 0; i < x.length; i++)
				sum += (x[i] - y[i]) * (x[i] - y[i]);
		else
			sum = -1.0;

		return Math.sqrt(sum);
	}

	/**
	 * This function calculates distances between 2 arrays where the arrays can
	 * have wild-cards (values <0).
	 *
	 * @param x
	 *            double array 1
	 * @param y
	 *            double array 2
	 * @return Eucledian distance between x and y for all non-negative
	 *         dimensions
	 */
	public static double distance1(double[] x, double[] y) {
		double sum = 0;
		if (x.length == y.length) {
			for (int i = 0; i < x.length; i++)
				if (x[i] >= 0.0 && y[i] >= 0.0)
					sum += (x[i] - y[i]) * (x[i] - y[i]);
		} else
			sum = -1.0;

		return sum;
	}

	/**
	 * This function calculates the average values of 2 vectors(double arrays)
	 *
	 * @param x
	 *            vector 1
	 * @param y
	 *            vector 2
	 * @return a vector where each element is the average of the two input
	 *         vectors
	 */
	public static double[] averageVector(double[] x, double[] y) {
		double[] z = new double[x.length];
		if (x.length == y.length) {
			for (int i = 0; i < x.length; i++) {
				if (x[i] >= 0.0 && y[i] >= 0.0)
					z[i] = 0.5 * (x[i] + y[i]);
				else
					z[i] = -1.0;
			}
		}
		return z;
	}

	/**
	 * Function writes a double array to a string where each double only gets 5
	 * places, cf. doubleString(x,5)
	 *
	 * @param x
	 *            the double array
	 * @return the string
	 */
	public static String doubleArrayString(double[] x) {
		String retval = new String("[");
		for (int i = 0; i < x.length; i++) {
			retval = retval.concat(doubleString(x[i], 5));
			if (i < x.length - 1)
				retval = retval.concat(",");
		}
		retval = retval.concat("]");
		return retval;
	}

	/**
	 * Function forms a string of a double value with a given string length.
	 * E.g. doubleString(0.33333333,5) returns "0.333"
	 *
	 * @param x
	 *            the double value
	 * @param l
	 *            the string length
	 * @return string value of the double x with length l
	 */
	public static String doubleString(final double x, final int l) {
		String X = new String(String.valueOf(x));
		if (x > 0.0 && x < 1.0 / Math.pow(10.0, l - 2)) {
			X = new String("0.");
			for (int i = 3; i <= l; i++)
				X = X.concat("0");
		} else if (x < 0.0 && x > 1.0 / Math.pow(10.0, l - 2)) {
			X = new String("-0.");
			for (int i = 3; i <= l; i++)
				X = X.concat("0");
		}
		int le = Math.min(X.length(), l);
		return X.substring(0, le);
	}

	/**
	 * Function to print the 'global' lexicon
	 */
	public static void printGlobalLexicon(List lAgents, PrintWriter lexFile, char type, int iter) {
		List bagOfWords = new ArrayList();
		String word;
		String meaning;
		lexFile.println("Global lexicon, iteration " + iter + ":");
		lexFile.println();
		for (int i = 0; i < lAgents.size(); i++)
			((Agent) lAgents.get(i)).getWords(bagOfWords);
		for (int i = 0; i < bagOfWords.size(); i++) {
			word = (String) bagOfWords.get(i);
			for (int j = 0; j < lAgents.size(); j++) {
				meaning = ((Agent) lAgents.get(j)).getMeaning(word, type);
				lexFile.println("A" + ((Agent) lAgents.get(j)).getID() + " " + word + " " + meaning);
			}
		}
		lexFile.println();
	}

	/**
	 * Function to print the grammars of the entire population.
	 */
	public static void printGrammar(List lAgents, PrintWriter lexFile, char type, int iter) {
		lexFile.println("Grammar, iteration " + iter + ":");
		lexFile.println();
		for (int i = 0; i < lAgents.size(); i++)
			((CompositionalAgent2) lAgents.get(i)).printGrammar(lexFile, type);
		lexFile.println();
		lexFile.flush();
	}

	/**
	 * Function used at some point to output to a latex file
	 */
	public static void beginLatexDocument(PrintWriter file, final String title) {

		file.println("\\documentclass{article}");
		file.println("\\usepackage{mystyle}");
		file.println("\\title{" + title + "}");
		file.println("\\begin{document}");
		file.println("\\maketitle");
		file.println("\\tiny");
	}

	/**
	 * Function used at some point to output to a latex file
	 */
	public static void endLatexDocument(PrintWriter file) {

		file.println("\\end{document}");
	}

	/**
	 * Function used at some point to output to a latex file
	 */

	public static void printLexiconLatexTabel(List lAgents, PrintWriter lexFile, char type, int lg) {

		for (int i = 0; i < lAgents.size(); i++)
			((Agent) lAgents.get(i)).printLexiconLatexTabel(lexFile, type, lg);

	}

	/**
	 * Function to write the lexicon in some format to the lexFile
	 */
	public static void printLexiconColumns(List lAgents, PrintWriter lexFile, char type, int lg) {
		lexFile.print(lg);
		for (int i = 0; i < lAgents.size(); i++)
			((Agent) lAgents.get(i)).printLexiconColumns(lexFile, type, lg);
		lexFile.println();
	}

	/**
	 * Function to remove a string from another string
	 *
	 * @param s1
	 *            string 1
	 * @param s2
	 *            string 2
	 * @return the remainder of string s1 after removal of string s2
	 */
	public static String removeSubstring(final String s1, final String s2) {
		int n = s1.indexOf(s2);
		int l = s2.length();
		String r = new String();
		if (n >= 0) {
			r = r.concat(s1.substring(0, n));
			if (l != s1.length())
				r = r.concat(s1.substring(n + l, s1.length()));
		} else
			r = s1;
		return r;
	}

	/**
	 * This function is used to get the largest substring under certain
	 * conditions. These conditions are: if the substring starts or ends in one
	 * string, it must also start or end in the other, and it must either start
	 * or end at the strings
	 */
	public static String substring(final String s1, final String s2) {
		String sub = new String();
		boolean eq = true;
		int n = 0, i = 0, j = 0;
		if (s1.indexOf(s2) >= 0 || s2.indexOf(s1) >= 0)
			return sub;
		while (i < s1.length()) {
			while (j < s2.length()) {
				if (i < s1.length() && s1.charAt(i) == s2.charAt(j)) {
					eq = true;
					n = 0;
					while (eq) {
						if (i + n < s1.length() && j + n < s2.length() && s1.charAt(i + n) == s2.charAt(j + n))
							n++;
						else {
							if (((i + n == s1.length() && j + n == s2.length()) || // may
																					// be
																					// at
																					// end
																					// of
																					// string
									(i + n < s1.length() && j + n < s2.length()
											&& s1.charAt(i + n) != s2.charAt(j + n)))
									&& n > 1 && // substring ended & l>1
									!((i == 0 && j > 0) || (i > 0 && j == 0)) && // not
																					// allow
																					// sub
																					// at
																					// beginning
									// of one string while not at the other
									(s1.substring(i, i + n)).length() > sub.length())// return
																						// largest
																						// substr
								sub = s1.substring(i, i + n);
							eq = false;
						}
					}
				}
				j++;
			}
			i++;
			j = 1;
		}
		return sub;
	}

	/**
	 * Function returns the complement of a string
	 *
	 * @param s1
	 *            string 1
	 * @param s2
	 *            string 2
	 * @return the substring of s1 that is unequal to s2, with the restriction
	 *         that s1 must start or end with s2. Otherwise it returns null.
	 */
	public static String complement(final String s1, final String s2) {
		String sub = null;
		if (s1.startsWith(s2))
			sub = s1.substring(s2.length());
		else if (s1.endsWith(s2))
			sub = s1.substring(0, s1.lastIndexOf(s2));
		return sub;
	}

	/**
	 * Returns the largest substring of two strings if the substring is either
	 * at the start or at the end of both strings and if the substring is not
	 * equal to either of the two strings.
	 *
	 */
	public static String largestSubString(final String s1, final String s2) {

		int maxL = 0;
		if (s1.equals(s2))
			return new String();// both strings should not be equal
		if (s1.startsWith(s2) || s1.endsWith(s2) || s2.startsWith(s1) || s2.endsWith(s1))
			return new String();
		String sub = new String();
		for (int i = 1; i < s2.length(); i++)
			if (s1.startsWith(s2.substring(0, i)))
				maxL = i;
		for (int i = 1; i < s2.length(); i++) {
			sub = s2.substring(i);
			if (s1.endsWith(sub) && sub.length() > maxL)
				return new String(sub);
		}
		return s2.substring(0, maxL);
	}

	/**
	 * @param l
	 *            a list of IntArrays
	 * @param a
	 *            an IntArray
	 * @return the index of a in l (-1 if a is not in l)
	 */
	public static int indexOf(final List l, final IntArray a) {
		for (int i = 0; i < l.size(); i++)
			if (((IntArray) l.get(i)).containsAll(a))
				return i;
		return -1;
	}

	public static int indexOfPart(final List l, final IntArray a) {
		for (int i = 0; i < l.size(); i++)
			if (((IntArray) l.get(i)).containsElementOfEqualSize(a))
				return i;
		return -1;
	}

	public static int indexOfContainingElement(final List l, final IntArray a) {
		for (int i = 0; i < l.size(); i++)
			if (((IntArray) l.get(i)).containsElementOf(a))
				return i;
		return -1;
	}

	/**
	 * Error message. Outputs the error message to the standard output and exits
	 * the program.
	 */
	public static void error(final String mess) {
		System.out.println("Error message: " + mess);
		System.exit(1);
	}

	/**
	 * Function to calculate the average variance in the columns of a matrix.
	 * This is used to calculate the variance between a set of meanings.
	 *
	 * @param x
	 *            the matrix
	 * @param N
	 *            the number of columns
	 * @param dim
	 *            the number of rows
	 * @return the avarage variance of the columns
	 */
	public static double variance(final double[][] x, final int N, final int dim) {
		// to calculate the average population variance of the columns of x
		double sum, var = 0.0, avg;
		int n;
		for (int i = 0; i < dim; i++) {
			sum = 0.0;
			n = 0;
			for (int j = 0; j < N; j++)
				if (x[j][i] >= 0.0) {
					sum += x[j][i];
					n++;
				}
			avg = sum / (double) n;
			sum = 0.0;
			for (int j = 0; j < N; j++)
				if (x[j][i] >= 0) {
					sum += (x[j][i] - avg) * (x[j][i] - avg);
				}
			if (n > 0)
				var += sum / (double) n;
		}
		return var / (double) dim;
	}

	/**
	 * @return a string of an array of strings
	 */
	public static String printStringArray(final String[] s) {
		String retval = new String("[");
		for (int i = 0; i < s.length; i++) {
			retval = retval.concat(s[i]);
			if (i < s.length - 1)
				retval = retval.concat(",");
		}
		retval = retval.concat("]");
		return retval;
	}

	/**
	 * @param x
	 *            a double value (between 0 and 1)
	 * @return an integer between 0 and 9: 0 if x<0.1, 1 if 0.1<=x<0.2, ... ,9
	 *         if 0.9<=x
	 */
	public static int index(double x) {
		if (x < 0.1)
			return 0;
		else if (x < 0.2)
			return 1;
		else if (x < 0.3)
			return 2;
		else if (x < 0.4)
			return 3;
		else if (x < 0.5)
			return 4;
		else if (x < 0.6)
			return 5;
		else if (x < 0.7)
			return 6;
		else if (x < 0.8)
			return 7;
		else if (x < 0.9)
			return 8;
		return 9;
	}

	/**
	 * Function to create a group of agents of one type
	 * 
	 */
	public static List<Agent> createAgents(Parameters params) {
		List<Agent> lAgents = new ArrayList<Agent>();
		int lastID = 1;
		for (int i = 0; i < params.getNAgents(); i++) {
			lAgents.add(new StrategyAgent(lastID, 0, params.getEtaN(), params.getEtaS(), params.getFeatures(),
					params.getNNoLearning(), params.getAlphabetSize(), params.getStrategyType(),
					params.getMemorySize()));
			lastID++;
		}
		return lAgents;
	}

	public static String buildDirectoryName(String parentDir, StrategyType strategyType, double actionRate, double coordRate, int testNumber) {
		StringBuilder dirName = new StringBuilder(parentDir);
		dirName.append(strategyType == StrategyType.ALTRUISTIC ? "altruistic/" : "mutualistic/");
		dirName.append(Utils.formatDirName(actionRate));
		dirName.append(File.separator);
		dirName.append(Utils.formatDirName(coordRate));
		dirName.append(File.separator);
		dirName.append(String.format("%02d", testNumber));
		return dirName.toString();
	}
	
	public static String buildPopulationDirectoryName(String parentDir, double actionRate, double coordRate, int testNumber){
		StringBuilder dirName = new StringBuilder(parentDir);
		if (!parentDir.endsWith("/")){
			dirName.append(File.separator);
		}
		dirName.append(Utils.formatDirName(actionRate));
		dirName.append(File.separator);
		dirName.append(Utils.formatDirName(coordRate));
		dirName.append(File.separator);
		dirName.append(String.format("%02d", testNumber));
		return dirName.toString();
	}

	public static String buildPopulationDirectoryNameAction(String parentDir, double actionRate){
		StringBuilder dirName = new StringBuilder(parentDir);
		if (!parentDir.endsWith("/")){
			dirName.append(File.separator);
		}
		dirName.append(Utils.formatDirName(actionRate));
		dirName.append(File.separator);
		return dirName.toString();
	}
	
	public static void storeUmpire(Umpire umpire, String storeDir, int interaction) {
		try {
			umpire.storeAsCSV(storeDir, interaction);
		} catch (DataCheckException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void storeAgents(Population population, String storeDir, int interaction) {
		try {
			population.storeAsCSV(storeDir, interaction);
		} catch (DataCheckException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void storeSyntaxMap(String storeDir){
		
		Path path = Paths.get(storeDir + "/" + SYNTAX_MAP_FILE_NAME);
		try (BufferedWriter writer = Files.newBufferedWriter(path))
		{
			for(Entry<Integer, Character> entry : syntaxMap.entrySet()){
				String cat = Integer.toString(entry.getKey())+ ":" + entry.getValue();
				writer.write(cat);
				writer.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static int calculateCover(Set<Category> distinctiveCategories) {
		int cover = 0;
		for (Category cat : distinctiveCategories) {
			cover += (int) Math.pow(2, cat.getValue());
		}
		return cover;
	}

	
	public static int calculateCover(Collection<CategoryMeaning> meanings) {
		int cover = 0;
		for (CategoryMeaning meaning : meanings) {
			cover += (int) Math.pow(2, meaning.getCategory().getValue());
		}
		if(!catMap.containsKey(cover)){
			List<Category> cats = new ArrayList<Category>();
			for (CategoryMeaning meaning : meanings){
				cats.add(meaning.getCategory());
			}
			catMap.put(cover, cats);
		}
		if(!syntaxMap.containsKey(cover)){
			syntaxMap.put(cover, syntaxCats.charAt(syntaxIndex++));
		}
		return cover;
	}
	
	public static Integer getCoverFromSyntax(Character value){
		Integer ret = null;
		for(Entry<Integer, Character> entry : syntaxMap.entrySet()){
			if(entry.getValue().equals(value)){
				return entry.getKey();
			}
		}
		return ret;
	}
	
	public static List<Category> calculateCategory(Integer c) throws GrammarException {
		if(!catMap.containsKey(c)){
			throw new GrammarException("Cover is not there!");
		}
		return catMap.get(c);
	}

	public static List<CategoryMeaning> getDifference(Collection<CategoryMeaning> list1,
			Collection<CategoryMeaning> list2) {
		List<CategoryMeaning> diff = new ArrayList<CategoryMeaning>();
		for (CategoryMeaning mean : list1) {
			if (!list2.contains(mean)) {
				diff.add(mean);
			}
		}
		for (CategoryMeaning mean : list2) {
			if (!list1.contains(mean)) {
				diff.add(mean);
			}
		}
		return diff;
	}

	public static long countCommon(Collection<CategoryMeaning> list1, Collection<CategoryMeaning> list2) {
		return list1.stream().filter(list2::contains).count();
	}

	public static List<CategoryMeaning> getCommon(Collection<CategoryMeaning> list1,
			Collection<CategoryMeaning> list2) {
		return list1.stream().filter(list2::contains).collect(Collectors.toList());
	}

	public static List<CategoryMeaning> getComplement(Collection<CategoryMeaning> list1,
			Collection<CategoryMeaning> list2) {
		List<CategoryMeaning> complement = new ArrayList<CategoryMeaning>();
		for (CategoryMeaning meaning : list2) {
			if (!list1.contains(meaning)) {
				complement.add(meaning);
			}
		}
		return complement;
	}

	public static int factorial(int n) {
		if (n == 0)
			return 1;	
		else
			return (n * factorial(n - 1));
	}

	public static int computeNumberOfPossiblePairs(int populationSize) {
		long possiblePairs = CombinatoricsUtils.binomialCoefficient(populationSize, 2);
		return (int)possiblePairs;
	}

	public static String formatDirName(double value){
		return String.format("%-4s", value ).replace(' ', '0');
	}
	
	public static String format(double value){
		return df2.format(value);
	}
	
	public static String formatStat(double value){
		return df3.format(value);
	}

	private static Ontology<CategoryMeaning> createOntology(){
		Ontology<CategoryMeaning> onto = new Ontology<CategoryMeaning>();
		for(int i=0; i< maxReducedColors; i++){
			CategoryMeaning shape = new CategoryMeaning(Shape.values()[i].getCategory(), Shape.values()[i]);
			CategoryMeaning color = new CategoryMeaning(ObjectColor.values()[i].getCategory(), ObjectColor.values()[i]);
			onto.addMeaning(shape);
			onto.addMeaning(color);
		}
		for(Destination dest : Destination.values()){
			onto.addMeaning(new CategoryMeaning(dest.getCategory(), dest));
		}
		return onto;
	}
	
	public static Ontology<CategoryMeaning> getSingletonOntology(){
		if(ontology == null){
			ontology = Utils.createOntology();
		}
		return ontology;
	}

	public static Object cloneObject(Object obj){
        try{
            Object clone = obj.getClass().newInstance();
            for (Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(clone, field.get(obj));
            }
            return clone;
        }catch(Exception e){
            return null;
        }
    }
}
