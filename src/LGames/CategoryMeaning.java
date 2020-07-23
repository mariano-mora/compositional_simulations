package LGames;

import java.util.Objects;

import LGames.Cognition.Category;

public class CategoryMeaning extends Meaning {

	private IsCategorical value;

	public CategoryMeaning(Category cat, IsCategorical value) {
		this.category = cat;
		this.value = value;
	}

	public Category getCategory() {
		return this.category;
	}

	public IsCategorical getValue() {
		return this.value;
	}

	public String toString() {
		return new String("" + this.value);
	}

	@Override
	public boolean equals(Object o) {
		return (this.category == ((CategoryMeaning) o).getCategory())
				&& (this.value == ((CategoryMeaning) o).getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, value);
	}

}