package LGames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import LGames.Cognition.Category;
import LGames.ContextStrategic.Destination;

public class Action {
	public Map<Category, IsCategorical> values = new HashMap<Category, IsCategorical>();
	private List<CategoryMeaning> meanings;
	public Double probScore = 0.0;

	public Action(List<CategoryMeaning> meanings) {
		for (CategoryMeaning meaning : meanings) {
			values.put(meaning.getCategory(), meaning.getValue());
		}
		this.meanings = meanings;
	}

	public Action(ContextObject ob, Destination dest) {
		values.put(dest.getCategory(), dest);
		values.putAll(ob.values);
		this.meanings = new ArrayList<CategoryMeaning>();
		for (IsCategorical value : values.values()) {
			this.meanings.add(new CategoryMeaning(value.getCategory(), value));
		}
	}

	@Override
	public boolean equals(Object action) {
		Action act = (Action) action;
		for (Category cat : this.values.keySet()) {
			if (!(this.values.get(cat) == act.values.get(cat))) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(meanings);
	}

	
	public String toString(){
		return new String("[" + this.meanings + "]");
	}
	public List<CategoryMeaning> getMeanings() {
		return this.meanings;
	}

	public Double getProbScore() {
		return probScore;
	}
}