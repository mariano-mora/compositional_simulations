package LGames;

import LGames.InteractionGame.ProductionMode;

public class CategoricalSymbols extends Symbols {

	public CategoricalRule rule;
	public ProductionMode mode;
	
	public CategoricalSymbols(CategoricalRule rule, String f){
		super(rule.getNumber(), f);
		this.rule = rule;
	}
	
	public CategoricalSymbols(CategoricalRule rule, String f, ProductionMode mode){
		this(rule, f);
		this.mode = mode;
	}
	
	public boolean isInvention(){
		return (this.mode == ProductionMode.HOLISTIC_CREATION || this.mode == ProductionMode.INVENTION);
	}
}
