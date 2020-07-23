package LGames;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Ontology<T extends Meaning> implements Iterable<T> {
	protected List<T> meanings = new ArrayList<T>();

	public Ontology() {
	}

	public void addMeaning(T meaning) {
		this.meanings.add(meaning);
	}

	public boolean isEmpty() {
		return this.meanings.isEmpty();
	}

	public T getMeaning(int index) {
		return this.meanings.get(index);
	}

	public List<T> getMeanings(){
		return this.meanings;
	}
	
	public int size(){
		return this.meanings.size();
	}
	
	@Override
	public Iterator<T> iterator() {
		return this.meanings.iterator();
	}

	public int indexOf(T element){
		return this.meanings.indexOf(element);
	}
}