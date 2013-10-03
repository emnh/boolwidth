package boolwidth.heuristics;

import graph.Vertex;
import interfaces.IGraph;

import java.util.Stack;

import boolwidth.DNode;
import boolwidth.Decomposition;

/**
 * This class decomposes a graph. Splitting the graph according to the method
 * used.
 */
public class DegreeHeuristic<V, E> {

	/**
	 * Returns a decomposition of the given graph as a binary tree. First cut
	 * puts every third node to the left and the rest to the right as children
	 * of the root. The nodes of each child of the root is split 1/2 + 1/2.
	 * Running time: O(n log n)
	 */
	public Decomposition.D<V, E> evenSplit(IGraph.D<V, E> g) {
		// creating a Decomposition
		Decomposition.D<V, E> decomp = new Decomposition.D<V, E>(g);

		// partitioning into 1/3 + 2/3
		DNode.D<V> dn = decomp.root();
		for (Vertex<V> v : dn.element()) {
			if (v.id() % 3 > 0) {
				decomp.addRight(dn, v);
			} else {
				decomp.addLeft(dn, v);
			}
		}

		// create bigraph
		// select node by

		// calling method to split into smaller parts
		Stack<DNode.D<V>> s = new Stack<DNode.D<V>>();
		s.add(decomp.right(dn));
		s.add(decomp.left(dn));
		evenSplit(decomp, s);

		return decomp;
	}

	/** Running time: O(n log n) */
	private static <IV, IE> void evenSplit(Decomposition.D<IV, IE> decomp,
			Stack<DNode.D<IV>> s) {
		while (!s.isEmpty()) {
			DNode.D<IV> root = s.pop();

			if (root.element().size() == 1) {
				continue;
			}
			boolean left = true;
			for (Vertex<IV> v : root.element()) {
				if (left) {
					decomp.addLeft(root, v);
				} else {
					decomp.addRight(root, v);
				}
				left = !left;
			}
			s.add(decomp.left(root));
			s.add(decomp.right(root));

		}
	}
}