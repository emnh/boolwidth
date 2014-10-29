package boolwidth;

import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**
 * This class decomposes a graph. Splitting the graph according to the method
 * used.
 */
public class RandomSplitter {

	/** Running time: O(n log n) */
	public static <V, E> void evenSplit(Decomposition.D<V, E> decomp,
			Stack<DNode.D<V>> s) {

		while (!s.isEmpty()) {
			DNode.D<V> root = s.pop();

			if (root.element().size() == 1) {
				continue;
			}

			int total = root.element().size();

			ArrayList<Vertex<V>> nodelist = new ArrayList<Vertex<V>>(root
					.element());
			Collections.shuffle(nodelist);

			for (int i = 0; i < total; i++) {
				Vertex<V> node = nodelist.get(i);
				if (i < total / 2) {
					decomp.addLeft(root, node);
				} else {
					decomp.addRight(root, node);
				}
			}

			s.add(decomp.left(root));
			s.add(decomp.right(root));

		}
	}

	/**
	 * Returns a decomposition of the given graph as a binary tree. First cut
	 * puts every third node to the left and the rest to the right as children
	 * of the root. The nodes of each child of the root is split 1/2 + 1/2.
	 * Running time: O(n log n)
	 */
	public static Decomposition.D<Integer, String> randomSplit(
			IGraph<Vertex<Integer>, Integer, String> g, int minbw) {
		// creating a Decomposition
		Decomposition.D<Integer, String> decomp = new Decomposition.D<Integer, String>(
				g);

		// partitioning into 1/3 + 2/3
		DNode.D<Integer> dn = decomp.root();
		int total = dn.element().size();

		ArrayList<Vertex<Integer>> nodelist = new ArrayList<Vertex<Integer>>(dn
				.element());
		Collections.shuffle(nodelist);
		for (int i = 0; i < total; i++) {
			Vertex<Integer> node = nodelist.get(i);
			if (i <= total / 3) {
				decomp.addLeft(dn, node);
			} else {
				decomp.addRight(dn, node);
			}
		}

		// calling method to split into smaller parts
		Stack<DNode.D<Integer>> s = new Stack<DNode.D<Integer>>();
		s.add(decomp.right(dn));
		s.add(decomp.left(dn));
		// evenSplit(decomp, s);
		long bw = CutBool.booleanWidth(decomp, minbw);
		if (bw == CutBool.BOUND_EXCEEDED) {
			return decomp;// TODO:optimize by returning null?
		}
		System.out.println("random done bw = " + bw);
		Splitter.searchSplit(decomp, s, bw);
		return decomp;
	}

	/**
	 * Returns a decomposition of the given graph as a binary tree. First cut
	 * puts every third node to the left and the rest to the right as children
	 * of the root. The nodes of each child of the root is split 1/2 + 1/2.
	 * Running time: O(n log n)
	 */
	public static <V, E> Decomposition.D<V, E> randomSplitGeneral(
			IGraph<Vertex<V>, V, E> g, long minbw) {
		// creating a Decomposition
		Decomposition.D<V, E> decomp = new Decomposition.D<V, E>(g);

		// partitioning into 1/3 + 2/3
		DNode.D<V> dn = decomp.root();
		int total = dn.element().size();

		ArrayList<Vertex<V>> nodelist = new ArrayList<Vertex<V>>(dn.element());
		Collections.shuffle(nodelist);
		for (int i = 0; i < total; i++) {
			Vertex<V> node = nodelist.get(i);
			if (i <= total / 3) {
				decomp.addLeft(dn, node);
			} else {
				decomp.addRight(dn, node);
			}
		}

		// calling method to split into smaller parts
		Stack<DNode.D<V>> s = new Stack<DNode.D<V>>();
		s.add(decomp.right(dn));
		s.add(decomp.left(dn));
		// evenSplit(decomp, s);
		long bw = CutBool.booleanWidth(decomp, minbw);
		if (bw == CutBool.BOUND_EXCEEDED) {
			return decomp;// TODO:optimize by returning null?
		}
		System.out.println("random done bw = " + bw);

		// TODO: make searchSplit general and remove the specific method
		throw new UnsupportedOperationException("not implemented");
		// Splitter.searchSplit(decomp, s, bw);

		// return decomp;
	}
}