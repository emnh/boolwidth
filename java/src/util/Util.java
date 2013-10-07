package util;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeSet;

public class Util {

	public static Random rnd;

	/**
	 * Lexicographic ordering of arrays
	 * 
	 * @author emh
	 * 
	 * @param <T>
	 */
	public static class LexArray<T extends Comparable<T>> implements
	Comparator<T[]> {

		@Override
		public int compare(T[] o1, T[] o2) {
			int cmp = 0;
			for (int i = 0; i < Math.min(o1.length, o2.length); i++) {
				cmp = o1[i].compareTo(o2[i]);
				if (cmp != 0) {
					break;
				}
			}
			if (cmp == 0) {
				if (o1.length < o2.length) {
					cmp = -1;
				} else if (o2.length < o1.length) {
					cmp = 1;
				}
			}
			return cmp;
		}
	}

	/**
	 * Lexicographic ordering of treeset
	 * 
	 * @author emh
	 * 
	 * @param <T>
	 */
	public static class LexCollection<T extends Comparable<T>> implements
	Comparator<Collection<T>> {

		// @SuppressWarnings("unchecked")
		@Override
		public int compare(Collection<T> o1, Collection<T> o2) {
			int cmp = 0;
			ArrayList<T> o1a = new ArrayList<T>(o1);
			ArrayList<T> o2a = new ArrayList<T>(o2);
			for (int i = 0; i < Math.min(o1.size(), o2.size()); i++) {
				cmp = o1a.get(i).compareTo(o2a.get(i));
				if (cmp != 0) {
					break;
				}
			}
			if (cmp == 0) {
				if (o1.size() < o2.size()) {
					cmp = -1;
				} else if (o2.size() < o1.size()) {
					cmp = 1;
				}
			}
			return cmp;
		}
	}

	public static class Pair<T extends Comparable<T>> implements
	Comparable<Pair<T>> {
		private T left;
		private T right;

		public Pair(T left, T right) {
			this.setLeft(left);
			this.setRight(right);
		}

		@Override
		public int compareTo(Pair<T> o) {
			int cmp = this.left.compareTo(o.left);
			if (cmp == 0) {
				cmp = this.right.compareTo(o.right);
			}
			return cmp;
		}

		public int compareToUnordered(Pair<T> o2) {
			Pair<T> o1 = this;
			TreeSet<T> o1vertices = new TreeSet<T>();
			o1vertices.add(o1.getLeft());
			o1vertices.add(o1.getRight());
			TreeSet<T> o2vertices = new TreeSet<T>();
			o2vertices.add(o2.getLeft());
			o2vertices.add(o2.getRight());
			new Util.LexCollection<T>().compare(o1vertices, o2vertices);
			// TODO Auto-generated method stub
			return 0;
		}

		public T getLeft() {
			return this.left;
		}

		public T getRight() {
			return this.right;
		}

		public void setLeft(T left) {
			this.left = left;
		}

		public void setRight(T right) {
			this.right = right;
		}

		@Override
		public String toString() {
			return String.format("(%s, %s)", this.left, this.right);
		}
	}

	public static <T> ArrayList<T> asList(Iterable<T> it) {
		ArrayList<T> list = new ArrayList<T>();
		for (T t : it) {
			list.add(t);
		}
		return list;
	}

	/**
	 * choose k elements from collection they will be the last k elements in the
	 * ArrayList returned while the first size - k elements are the ones not
	 * chosen
	 * 
	 * @param <T>
	 * @return
	 */
	public static <T> ArrayList<T> choose(Collection<T> c, int k) {
		if (rnd == null) {
			rnd = new Random();
		}
		ArrayList<T> list = new ArrayList<T>(c);
		assert list.size() == c.size();
		int size = list.size();
		if (k > size) {
			k = size;
		}
		for (int i = size; i > 1 && i > size - k; i--) {
			Collections.swap(list, i - 1, rnd.nextInt(i));
		}
		return list;
	}

	/**
	 * n / m rounded up
	 * 
	 * @param n
	 * @param m
	 */
	public static int divRoundUp(int n, int m) {
		// return n / m + ((n % m > 0) ? 1 : 0);
		return (n - 1) / m + 1;
	}

	public static void fileToString(String filename) {
		FileReader fw;
		try {
			fw = new FileReader(filename);
			fw.read();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static String formatIntArray(int dec, int... K) {
		String[] strings = new String[K.length];
		int i = 0;
		for (int k : K) {
			strings[i++] = String.format("%0" + dec + "d", k);
		}
		return join(",", strings);
	}

	public static String join(String sep, String... args) {
		StringBuilder ret = new StringBuilder();
		boolean first = true;
		for (String arg : args) {
			if (!first) {
				ret.append(sep);
			}
			first = false;
			ret.append(arg);
		}
		return ret.toString();
	}

	public static void printStackTrace() {
		new Exception("Stack trace:").printStackTrace(System.out);
	}

	public static int product(int... a) {
		int prod = 1;
		for (int v : a) {
			prod *= v;
		}
		return prod;
	}

	public static <T> ArrayList<T> shuffle(Collection<T> c) {
		return choose(c, c.size());
	}

	public static void stringToFile(String filename, String towrite) {
		FileWriter fw;
		try {
			fw = new FileWriter(filename);
			fw.write(towrite);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
