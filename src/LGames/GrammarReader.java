package LGames;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import LGames.ContextStrategic.Destination;
import LGames.ContextStrategic.ObjectColor;
import LGames.ContextStrategic.Shape;
import Util.Utils;

public class GrammarReader {

	private String fileName;
	private String coverKeyFile;
	private List<Grammar<CategoricalRule>> grammars = new ArrayList<Grammar<CategoricalRule>>();
	private Grammar<CategoricalRule> current = new Grammar<CategoricalRule>();
	private StrategyAgent currentAgent;
	private int agentCount = 0;
	private Population population;

	public GrammarReader(String parentDir, Population population) {
		this.fileName = new String(parentDir + "/grammars.txt");
		this.coverKeyFile = new String(parentDir + "/coverKey.txt");
		this.population = population;
		
	}

	public void parseSyntaxKeyMap() throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(this.coverKeyFile))) {
			stream.forEach(l -> parseSyntaxEntry(l));
		}
	}

	private void parseSyntaxEntry(String l) {
		l = l.replaceAll("\\s+", "");
		String[] splits = l.split(":");
		Integer key = Integer.valueOf(splits[0]);
		Character value = splits[1].charAt(0);
		Utils.syntaxMap.put(key, value);
	}

	public void parseFile() throws IOException {
		try (Stream<String> stream = Files.lines(Paths.get(this.fileName))) {
			stream.forEach(l -> parseLine(l));
		}
	}

	private CategoricalRule parseRule(String line) {
		CategoricalRule rule = new CategoricalRule();
		line = line.replaceAll("\\s+", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\(", "")
				.replaceAll("\\)", "");
		String[] splits = line.split("/");
		String number = splits[0].substring(splits[0].indexOf('_') + 1, splits[0].indexOf(':'));
		rule.number = Integer.parseInt(number);
		String globalCover = splits[0].substring(splits[0].indexOf(':') + 1, splits[0].indexOf('\u27fc'));
		Integer coverInt = Utils.getCoverFromSyntax(globalCover.charAt(0));
		rule.cover = coverInt != null ? coverInt : 0;
		String expression = splits[0].substring(splits[0].indexOf('\u27fc') + 1);
		if (expression.contains("\u00b7")) {
			String[] splitCovers = expression.split("\u00b7");
			Integer[] coveredDimensions = new Integer[splitCovers.length];
			for (int i = 0; i < splitCovers.length; i++) {
				Character cover = splitCovers[i].charAt(0);
				Integer intCover = Utils.getCoverFromSyntax(cover);
				if (intCover != null)
					coveredDimensions[i] = Utils.getCoverFromSyntax(cover);
			}
			rule.coveredDimensions = coveredDimensions;
			rule.coveredRules = new HashMap<Integer, List<CategoricalRule>>();
			rule.expression = null;
		} else {
			rule.expression = expression.replaceAll("\"", "");
		}
		rule.meaningsSet = parseMeanings(splits[1]);
		rule.iScore = Double.valueOf(splits[2]);
		rule.totalUse = Integer.valueOf(splits[3]);
		rule.creation = Integer.valueOf(splits[4]);
		rule.split = Boolean.parseBoolean(splits[5]);

		return rule;
	}

	private Set<CategoryMeaning> parseMeanings(String meanings) {
		Set<CategoryMeaning> means = new HashSet<CategoryMeaning>();
		String[] splits = meanings.split(",");
		boolean found = false;
		for (String split : splits) {
			found = false;
			for (Destination dest : Destination.values()) {
				if (!dest.getName().equals(split)) {
					continue;
				}
				CategoryMeaning meaning = currentAgent.architecture.findInOntologyUnique(dest.getCategory(), dest);
				if (meaning == null) {
					meaning = new CategoryMeaning(dest.getCategory(), dest);
					currentAgent.architecture.ontology.addMeaning(meaning);
				}
				means.add(meaning);
				found = true;
				break;
			}
			if (found) {
				continue;
			}

			for (Shape shape : Shape.values()) {
				if (!shape.getName().equals(split)) {
					continue;
				}
				CategoryMeaning meaning = currentAgent.architecture.findInOntologyUnique(shape.getCategory(), shape);
				if (meaning == null) {
					meaning = new CategoryMeaning(shape.getCategory(), shape);
					currentAgent.architecture.ontology.addMeaning(meaning);
				}				
				means.add(meaning);
				found = true;
				break;
			}
			if (found) {
				continue;
			}
			for (ObjectColor color : ObjectColor.values()) {
				if (!color.getName().equals(split)) {
					continue;
				}
				CategoryMeaning meaning = currentAgent.architecture.findInOntologyUnique(color.getCategory(), color);
				if (meaning == null) {
					meaning = new CategoryMeaning(color.getCategory(), color);
					currentAgent.architecture.ontology.addMeaning(meaning);
				}		
				means.add(meaning);
				found = true;
				break;

			}
			if (found) {
				continue;
			}

		}
		return means;
	}
	
	private void parseAgentCostMemory(String line){
		String[] splits = line.split(":");
		currentAgent.memorySize = Integer.parseInt(splits[1]);
		double lastCost = Double.parseDouble(splits[2]);
		for(int i = 0; i<currentAgent.memorySize; i++){
			currentAgent.costMemory.add(lastCost);
		}		
		currentAgent.architecture.nInteractions = Integer.parseInt(splits[3]);
	}

	public void parseLine(String line) {
		if (!line.startsWith("AGENT") && !line.startsWith("R_")) {
			return;
		}
		if (line.startsWith("AGENT")) {
			if(currentAgent != null){
				currentAgent.architecture.ruleCreator.ruleNumber = currentAgent.architecture.grammar.size();
			}
			currentAgent = (StrategyAgent) population.get(agentCount++);
			parseAgentCostMemory(line);
		} else {
			CategoricalRule r = parseRule(line);
			currentAgent.architecture.grammar.rules.add(r);
		}
	}

	public String getFileName() {
		return this.fileName;
	}
}
