package boolwidth;

import java.util.Formatter;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * histogram: counts number of each element of type T
 * 
 * @author emh
 * 
 * @param <T>
 */
public class Histogram<T extends Comparable<T>> {

	protected TreeMap<T, Integer> buckets;

	public Histogram() {
		this.buckets = new TreeMap<T, Integer>();
	}

	public Histogram(int max) {
		this.buckets = new TreeMap<T, Integer>();
	}

	public int count(T element) {
		int val;
		if (this.buckets.containsKey(element)) {
			val = this.buckets.get(element);
		} else {
			val = 0;
		}
		val++;
		this.buckets.put(element, val);
		return val;
	}

	public int count(T element, int increment) {
		int val;
		if (this.buckets.containsKey(element)) {
			val = this.buckets.get(element);
		} else {
			val = 0;
		}
		val += increment;
		this.buckets.put(element, val);
		return val;
	}

	public SortedMap<T, Integer> getBuckets() {
		return this.buckets;
	}

	public int getCount(int element) {
		return this.buckets.get(element);
	}

	public int size() {
		return this.buckets.size();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Formatter f = new Formatter(sb);
		for (Map.Entry<T, Integer> e : this.buckets.entrySet()) {
			f.format("%s: %d\n", e.getKey().toString(), e.getValue());
		}
		return sb.toString();
	}
}
