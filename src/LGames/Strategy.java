package LGames;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Strategy {

	public enum StrategyType {ALTRUISTIC(0), MUTUALISTIC(1);
	private int value;
	private static Map<Integer, StrategyType> map = new HashMap<Integer, StrategyType>();
	
	private StrategyType(int value) {
        this.value = value;
    }
	
	static {
        for (StrategyType strategyType : StrategyType.values()) {
            map.put(strategyType.value, strategyType);
        }
    }
	
	public static StrategyType valueOf(int strategyType) {
        return (StrategyType) map.get(strategyType);
    }

	};
	
	
	
	public static boolean shouldInteract(StrategyType strat, List<Double> memory){
		if (strat == StrategyType.ALTRUISTIC) {
			return true;
		}
		else{
			return computeProbability(memory);
		}
	}
	
	private static boolean computeProbability(List<Double> memory){
		
		return false;
		
	}
}
