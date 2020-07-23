package LGames;

import java.util.Set;

public class CompositionCombinations {
	public Set<CategoricalRule> rules;
	public double contribution = 0.0;
	public double score = 0.0;
	
	public CompositionCombinations(Set<CategoricalRule> rules, double cont, double sc){
		this.rules = rules;
		this.contribution = cont;
		this.score = sc;
	}
	
	public double getScore(){
		return this.score;
	}
	
	public double getContribution(){
		return this.contribution;
	}
	
	public String toString(){
		return this.rules.toString();
	}
}

