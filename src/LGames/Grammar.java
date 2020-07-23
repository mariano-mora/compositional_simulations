package LGames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Grammar<T extends Rules2> implements Iterable<T> {

	
	public List<T> rules = new ArrayList<T>();
	private int ruleNumber = 0;
	private HashMap<T, ArrayList<T>> rulesRemoved = new HashMap<T, ArrayList<T>>();
	public List<RuleChange> changedRules = new ArrayList<RuleChange>();
	
	public enum CreationType{
		SPLIT, MERGED, GENERALISED, SUB, SUPRA, SUPRA_SUPRA;
	}
	
	public class RuleChange {
		private int new_rule_id;
		private Integer r1_id;
		private Integer r2_id;
		private CreationType type;
		
		public RuleChange(int newRuleId, CreationType creType){
			this.new_rule_id = newRuleId;
			this.type = creType;
		}
		
		public RuleChange(int newRuleId, int r1Id, int r2Id, CreationType creType){
			this.new_rule_id = newRuleId;
			this.r1_id =  r1Id;
			this.r2_id = r2Id;
			this.type = creType;
		}
		
		public RuleChange(int newRuleId, int r1Id, CreationType creType){
			this.new_rule_id = newRuleId;
			this.r1_id =  r1Id;
			this.r2_id = null;
			this.type = creType;
		}

		@Override
		public String toString() {
			return new String("New rule: " + new_rule_id + " /  " + r1_id + " /  " + r2_id + " / " + type);
		}
		

	}
	
	public Grammar(){}
		
	
	
	public Grammar(List<T> rules) {
		this.rules = rules;
	}

	public Grammar(Grammar<T> grammar) {
		this.rules = grammar.rules;
	}


	public void addRule(T rule){
		rule.setNumber(ruleNumber++);
		this.rules.add(rule);
		assert(rules.size()==ruleNumber);
	}

	public void removeRule(T rule){
		this.rules.remove(rule);
	}

	public void recordMerged(T merged, int indexToRemove) {
		ArrayList<T> removed = rulesRemoved.get(merged);
		if ( removed != null){
			removed.add(rules.get(indexToRemove));
		}
		else{
			removed = new ArrayList<T>();
			removed.add(rules.get(indexToRemove));
			rulesRemoved.put(merged, removed);
		}
		
	}
	
	public void recordGeneralised(int generalised){
		this.changedRules.add(new RuleChange(generalised, CreationType.GENERALISED));
	}

	public void recordSplit(int newRuleID, int r1ID, int r2ID){
		this.changedRules.add(new RuleChange(newRuleID, r1ID, r2ID, CreationType.SPLIT));
	}
	
	public void recordSub(int newRuleID, int r1ID){
		this.changedRules.add(new RuleChange(newRuleID, r1ID, CreationType.SUB));
	}
	
	public void recordSupra(int newRuleID, int r1ID, int r2ID){
		this.changedRules.add(new RuleChange(newRuleID, r1ID, r2ID, CreationType.SUPRA));
	}
	

	public void recordSupraSupra(int number, int number2) {
		this.changedRules.add(new RuleChange(number, number2, CreationType.SUPRA_SUPRA));
	}
	
	public void resetNumbers() {
		T r = null;
		for (int i = 0; i < rules.size(); i++) {
			r = rules.get(i);
			if (r.getNumber() != i) {
				r.setNumber(i);
			}
		}
		ruleNumber = rules.size();
	}
	
	public List<Rules2> getNonFullCover(){
		List<Rules2> nonFullCover = new ArrayList<Rules2>();
		for (Rules2 rule : rules){
			if (rule.getCovers().length > 1 || rule.getCovers()[0] != 63){
				nonFullCover.add(rule);
			}
		}
		return nonFullCover;
	}
	
	public List<T> getRules() {
		return rules;
	}

	public void setRules(List<T> rules) {
		this.rules = rules;
	}

	@Override
	public Iterator<T> iterator() {
		return rules.iterator();
	}

	public T get(int index){
		return rules.get(index);
	}

	public int size(){
		return this.rules.size();
	}
	
	public void clear() {
		rules.clear();
	}

	public void remove(int index) {
		rules.remove(index);
	}

	public int getRuleNumber() {
		return ruleNumber;
	}

	public void setRuleNumber(int ruleNumber) {
		this.ruleNumber = ruleNumber;
	}

	public Stream<T> stream(){
		return rules.stream();
	}

	public List<RuleChange> getChangedRules() {
		return changedRules;
	}



}
