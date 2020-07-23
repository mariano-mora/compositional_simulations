package Util;

import java.util.List;

/**
 * This class implements an array of integers in a similar way ArrayLists are
 * implemented. They serve to decrease memory space (as opposed to ArrayLists)
 * and allow functions directly operate on integers.<br>
 * The working will further not be discussed here.
 *
 * <p>
 * Copyright (c) 2004 Paul Vogt
 *
 *
 * @author Paul Vogt
 * @version $4.0.3$
 */

public class IntArray {

	private int[] array = new int[0];
	private boolean changed = true;
	private int maxValue = 0;

	public IntArray() {
	}

	public IntArray(final List<Integer> a) {
		array = new int[a.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = a.get(i);
	}

	public IntArray(final IntArray a) {
		array = new int[a.array.length];
		for (int i = 0; i < array.length; i++)
			array[i] = a.array[i];
	}

	public IntArray(final int[] a) {
		array = new int[a.length];
		for (int i = 0; i < array.length; i++)
			array[i] = a[i];
	}

	public IntArray(final int l) {
		array = new int[l];
	}

	public IntArray(final int l, final int n) {
		array = new int[l];
		for (int i = 0; i < l; i++)
			array[i] = n;
	}

	public int get(final int i) {
		// if (i<array.length)
		return array[i];
		// else{
		// System.out.println("Index "+i+" is too large for IntArray,
		// length="+array.length);
		// System.exit(0);
		// }
		// return -1;
	}

	public int[] get() {
		return array;
	}

	public void add(final int el) {
		int[] a = new int[array.length + 1];
		System.arraycopy(array, 0, a, 0, array.length);
		a[array.length] = el;
		array = a;
		changed = true;
	}

	public void add(final int i, final int el) {
		int[] a = new int[array.length + 1];
		if (i > 0)
			System.arraycopy(array, 0, a, 0, i);
		System.arraycopy(array, i, a, i + 1, array.length - i);
		a[i] = el;
		array = a;
		changed = true;
	}

	public void addAll(final int[] el) {
		int[] a = new int[array.length + el.length];
		System.arraycopy(array, 0, a, 0, array.length);
		System.arraycopy(el, 0, a, array.length, el.length);
		array = a;
		changed = true;
	}

	public void addAll(final IntArray e) {
		int[] el = e.get();
		addAll(el);
		changed = true;
	}

	public void addAll(final int i, final int[] el) {
		int[] a = new int[array.length + el.length];
		if (i > 0)
			System.arraycopy(array, 0, a, 0, i);
		System.arraycopy(el, 0, a, i, el.length);
		System.arraycopy(array, i, a, i + el.length, array.length - i);
		array = a;
		changed = true;
	}

	public void addAll(final int i, final IntArray e) {
		int[] el = e.get();
		addAll(el);
		changed = true;
	}

	public void set(final int i, final int el) {
		// if (i<array.length)
		array[i] = el;
		changed = true;
		// else{
		// System.out.println("Index out of bounds IntArray, Index="+i+",
		// length="+array.length);
		// System.exit(1);
		// }
	}

	public void increment(final int i) {
		array[i]++;
		changed = true;
	}

	public void decrement(final int i) {
		array[i]--;
		changed = true;
	}

	public int size() {
		return array.length;
	}

	public void clear() {
		array = new int[0];
		changed = true;
	}

	public int[] remove(final int i) {
		int[] a = new int[array.length - 1];
		if (i > 0)
			System.arraycopy(array, 0, a, 0, i);
		System.arraycopy(array, i + 1, a, i, array.length - i - 1);
		array = a;
		changed = true;
		return array;
	}

	public int[] remove(final int from, final int to) {
		int[] a = new int[array.length - to + from - 1];
		if (from > 0)
			System.arraycopy(array, 0, a, 0, from);
		System.arraycopy(array, to + 1, a, from, array.length - to - 1);
		array = a;
		changed = true;
		return array;
	}

	public void delete(final int el) {
		int i = 0;
		while (i < array.length)
			if (array[i] == el)
				array = remove(i);
			else
				i++;
	}

	public int[] copy() {
		int[] a = new int[array.length];
		for (int i = 0; i < array.length; i++)
			a[i] = array[i];
		return a;
	}

	public int[] copy(final int from, final int to) {
		int[] a = new int[to - from + 1];
		System.arraycopy(array, from, a, 0, a.length);
		return a;
	}

	public void removeDoubles() {
		int i = 0;
		int j;
		while (i < array.length) {
			j = i + 1;
			while (j < array.length) {
				if (array[i] == array[j])
					array = remove(j);
				else
					j++;
			}
			i++;
		}
		changed = true;
	}

	public int indexOf(final int el) {
		for (int i = 0; i < array.length; i++)
			if (array[i] == el)
				return i;
		return -1;
	}

	public int indexOf(final IntArray a) {
		for (int i = 0; i < array.length; i++)
			if (a.contains(array[i]))
				return i;
		return -1;
	}

	public int lastIndexOf(final int el) {
		int ret = -1;
		for (int i = 0; i < array.length; i++)
			if (array[i] == el)
				ret = i;
		return ret;
	}

	public int indexOf(final List l) {
		for (int i = 0; i < l.size(); i++)
			if (equals((IntArray) l.get(i)))
				return i;
		return -1;
	}

	public boolean contains(final int el) {
		for (int i = 0; i < array.length; i++)
			if (array[i] == el)
				return true;
		return false;
	}

	public boolean containsElementOf(final int[] a) {
		for (int i = 0; i < array.length; i++)
			for (int j = 0; j < a.length; j++)
				if (array[i] == a[j])
					return true;
		return false;
	}

	public boolean containsElementOf(final IntArray a) {
		return containsElementOf(a.array);
	}

	public boolean containsElementOfEqualSize(final int[] a) {
		if (array.length != a.length)
			return false;
		for (int i = 0; i < array.length; i++)
			for (int j = 0; j < a.length; j++)
				if (array[i] == a[j])
					return true;
		return false;
	}

	public boolean containsElementOfEqualSize(final IntArray a) {
		return containsElementOfEqualSize(a.array);
	}

	public boolean isEmpty() {
		if (array.length == 0)
			return true;
		return false;
	}

	public boolean equals(int[] a) {
		if (array.length != a.length)
			return false;
		for (int i = 0; i < a.length; i++)
			if (array[i] != a[i])
				return false;
		return true;
	}

	public boolean equals(final IntArray a) {
		// System.out.println("equals: "+this+" and "+a);
		return equals(a.get());
	}

	public boolean containsAll(int[] a) {// must have equal length
		// if (array.length!=a.length) return false;
		for (int i = 0; i < a.length; i++)
			if (!contains(a[i]))
				return false;
		return true;
	}

	public boolean containsAll(final IntArray a) {
		return containsAll(a.array);
	}

	public boolean elementOf(final List l) {
		for (int i = 0; i < l.size(); i++)
			if (equals((IntArray) l.get(i)))
				return true;
		return false;
	}

	public String toString() {
		String ret = new String("[");
		for (int i = 0; i < array.length; i++)
			if (i < array.length - 1)
				ret = ret.concat(array[i] + ",");
			else
				ret = ret.concat(String.valueOf(array[i]));
		ret = ret.concat("]");
		return ret;
	}

	public static String stringValue(int[] a) {
		String ret = new String("[");
		for (int i = 0; i < a.length; i++)
			if (i < a.length - 1)
				ret = ret.concat(a[i] + ",");
			else
				ret = ret.concat(String.valueOf(a[i]));
		ret = ret.concat("]");
		return ret;
	}

	public void sort() {
		int tmp;
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = i + 1; j < array.length; j++) {
				if (array[j] < array[i]) {
					tmp = array[i];
					array[i] = array[j];
					array[j] = tmp;
				}
			}
		}
	}

	public void sortd() {// sort descending
		int tmp;
		for (int i = 0; i < array.length - 1; i++) {
			for (int j = i + 1; j < array.length; j++) {
				if (array[j] > array[i]) {
					tmp = array[i];
					array[i] = array[j];
					array[j] = tmp;
				}
			}
		}
	}

	public IntArray crossSection(final IntArray a) {
		IntArray retval = new IntArray();
		for (int i = 0; i < array.length; i++)
			if (a.contains(array[i]))
				retval.add(array[i]);
		return retval;
	}

	public IntArray complement(final IntArray a) {
		IntArray retval = new IntArray();
		for (int i = 0; i < array.length; i++)
			if (!a.contains(array[i]))
				retval.add(array[i]);
		return retval;
	}

	public int getPositiveValue() {
		for (int i = 0; i < array.length; i++)
			if (array[i] >= 0)
				return array[i];
		return -1;
	}

	public int maxValue() {
		if (changed) {
			maxValue = 0;
			for (int i = 0; i < array.length; i++)
				if (array[i] > maxValue)
					maxValue = array[i];
		}
		return maxValue;
	}
}
