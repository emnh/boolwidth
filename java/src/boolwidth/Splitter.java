package boolwidth;

import graph.IntegerGraph;
import graph.PosSet;
import graph.PosSubSet;
import graph.Vertex;

import java.util.Arrays;
import java.util.Stack;

/**
 * This class decomposes a graph. Splitting the graph according to the method
 * used.
 */
public class Splitter {

	@SuppressWarnings("unchecked")
	private static PosSet<Vertex<Integer>>[] castArray(PosSet<?>[] ar) {
		return (PosSet<Vertex<Integer>>[]) ar;
	}

	/** method used to recursively split non-root nodes into two halves */
	private static <V, E> void evenSplit(Decomposition.D<V, E> decomp,
			Stack<DNode.D<V>> s) {
		while (!s.isEmpty()) {
			DNode.D<V> root = s.pop();

			if (root.element().size() == 1) {
				continue;
			}
			boolean left = true;
			for (Vertex<V> v : root.element()) {
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

	/**
	 * Returns a decomposition of the given graph as a binary tree. First cut
	 * puts every third node to the left and the rest to the right as children
	 * of the root. The nodes of each child of the root is split 1/2 + 1/2.
	 * Running time: O(n log n)
	 */
	public static Decomposition.D<Integer, String> evenSplit(IntegerGraph g) {
		// creating a Decomposition
		Decomposition.D<Integer, String> decomp = new Decomposition.D<Integer, String>(
				g);

		// partitioning into 1/3 + 2/3
		DNode.D<Integer> dn = decomp.root();
		for (Vertex<Integer> v : dn.element()) {
			if (v.id() % 3 > 0) {
				decomp.addRight(dn, v);
			} else {
				decomp.addLeft(dn, v);
			}
		}

		// calling method to split into smaller parts
		Stack<DNode.D<Integer>> s = new Stack<DNode.D<Integer>>();
		s.add(decomp.right(dn));
		s.add(decomp.left(dn));
		evenSplit(decomp, s);

		return decomp;
	}

	/** Search for a three-way split by flipping pairs or rotating triples. */
	public static Decomposition.D<Integer, String> optSearchSplit(IntegerGraph g) {
		PosSet<Vertex<Integer>> s1 = new PosSet<Vertex<Integer>>();
		PosSet<Vertex<Integer>> s2 = new PosSet<Vertex<Integer>>();
		PosSet<Vertex<Integer>> s3 = new PosSet<Vertex<Integer>>();

		while (true) {
			// randomly split vertices in three sets

			for (Vertex<Integer> v : g.vertices()) {
				int i = (int) (Math.random() * 3.0);
				switch (i) {
				case 1:
					s1.add(v);
					break;
				case 2:
					s2.add(v);
					break;
				default:
					s3.add(v);
					break;
				}
			}
			// make sure no set is empty
			if (s1.size() * s2.size() * s3.size() != 0) {
				break;
			}
		}
		// System.out.println("Split into: "+s1.size()+", "+s2.size()+", "+s3.size());

		PosSubSet<Vertex<Integer>> ts1 = new PosSubSet<Vertex<Integer>>(g);
		ts1.addAll(s1);
		PosSubSet<Vertex<Integer>> ts2 = new PosSubSet<Vertex<Integer>>(g);
		ts2.addAll(s2);
		PosSubSet<Vertex<Integer>> ts3 = new PosSubSet<Vertex<Integer>>(g);
		ts3.addAll(s3);
		PosSubSet<Vertex<Integer>> temp = new PosSubSet<Vertex<Integer>>(g);
		temp.addAll(ts2);
		temp.addAll(ts3);

		Decomposition.D<Integer, String> decomp = new Decomposition.D<Integer, String>(
				g);
		decomp.addLeft(decomp.root(), ts1);
		decomp.addRight(decomp.root(), temp);
		decomp.addLeft(decomp.root().getRight(), ts2);
		decomp.addRight(decomp.root().getRight(), ts3);

		int bound = Integer.MAX_VALUE;
		boolean improve = true;
		while (improve) {
			int count = CutBool.booleanWidth(decomp, bound);
			if (count < bound) {
				bound = count;
			}

		}
		Stack<DNode.D<Integer>> s = new Stack<DNode.D<Integer>>();

		// RandomSplitter.evenSplit(decomp, s);
		int maxN = CutBool.booleanWidth(decomp);
		// System.out.println("maxN = "+maxN);
		searchSplit(decomp, s, maxN);
		return decomp;
	}

	public static void searchSplit(Decomposition.D<Integer, String> decomp,
			Stack<boolwidth.DNode.D<Integer>> s, int maxN) {
		while (!s.isEmpty()) {
			DNode.D<Integer> root = s.pop();
			System.out.println("Deep Searching bag " + root.id());

			if (root.element().size() <= 1) {
				continue;
			}

			for (Vertex<Integer> v : root.element()) {
				double d = Math.random() * 2.0;
				if (d < 1.0) {
					decomp.addLeft(root, v);
				} else {
					decomp.addRight(root, v);
				}
			}
			// if all ended on same side
			if (!decomp.hasLeft(root)) {
				Vertex<Integer> v = decomp.right(root).element().first();
				decomp.removeFromLeaf(decomp.right(root), v);
				decomp.addLeft(root, v);
			}
			if (!decomp.hasRight(root)) {
				Vertex<Integer> v = decomp.left(root).element().first();
				decomp.removeFromLeaf(decomp.left(root), v);
				decomp.addRight(root, v);
			}
			DNode.D<Integer> r = decomp.right(root);
			DNode.D<Integer> l = decomp.left(root);

			// System.out.println("Split into: "+l.element().size()+", "+r.element().size());

			int hoodCount = CutBool.countNeighborhoods(decomp.getCut(r));
			hoodCount = Math.max(hoodCount, CutBool.countNeighborhoods(decomp
					.getCut(l)));

			// System.out.println("before: "+hoodCount);
			boolean improve = true;
			while (hoodCount > maxN && improve) {
				improve = false;
				Vertex<Integer> v = r.element().first();
				while (v != null) {
					Vertex<Integer> u = l.element().first();
					while (u != null) {
						// swap
						decomp.removeFromLeaf(l, v);
						decomp.removeFromLeaf(r, u);
						decomp.addLeft(root, u);
						decomp.addRight(root, v);

						int newCount = CutBool.countNeighborhoods(decomp
								.getCut(r), hoodCount);

						// swap back
						if (newCount == CutBool.BOUND_EXCEEDED) {
							decomp.removeFromLeaf(l, u);
							decomp.removeFromLeaf(r, v);
							decomp.addLeft(root, v);
							decomp.addRight(root, u);
						} else {
							if (newCount < hoodCount) {
								hoodCount = newCount;
								improve = true;
							}
						}
						u = l.element().higher(u);
					}
					v = r.element().higher(v);
				}
			}
			// System.out.println("after: "+hoodCount);
			s.add(r);
			s.add(l);
		}
	}

	/**
	 * Search for a three-way split by flipping pairs or rotating triples.
	 * 
	 * @param minbw
	 */
	public static Decomposition.D<Integer, String> searchSplit(IntegerGraph g,
			long minbw) {
		PosSet<Vertex<Integer>> s1 = new PosSet<Vertex<Integer>>();
		PosSet<Vertex<Integer>> s2 = new PosSet<Vertex<Integer>>();
		PosSet<Vertex<Integer>> s3 = new PosSet<Vertex<Integer>>();
		// randomly split vertices in three sets
		for (Vertex<Integer> v : g.vertices()) {
			int i = (int) (Math.random() * 3.0);
			switch (i) {
			case 1:
				s1.add(v);
				break;
			case 2:
				s2.add(v);
				break;
			default:
				s3.add(v);
				break;
			}
		}
		// System.out.println("Split into: "+s1.size()+", "+s2.size()+", "+s3.size());

		boolean improve = true;
		int i = 0;
		while (improve && i < 10) {
			System.out.println(i++);
			improve = swap2(s1, s2, g, s3);
			improve = improve | swap2(s1, s3, g, s2);
			improve = improve | swap2(s2, s3, g, s1);
		}
		improve = true;
		i = 0;
		while (improve && i < 10) {
			i++;
			improve = swap1(s1, s2, g, s3);
			improve = improve | swap1(s1, s3, g, s2);
			improve = improve | swap1(s2, s3, g, s1);
		}

		Decomposition.D<Integer, String> decomp = new Decomposition.D<Integer, String>(
				g);
		PosSubSet<Vertex<Integer>> ts1 = new PosSubSet<Vertex<Integer>>(g);
		ts1.addAll(s1);
		PosSubSet<Vertex<Integer>> ts2 = new PosSubSet<Vertex<Integer>>(g);
		ts2.addAll(s2);
		PosSubSet<Vertex<Integer>> ts3 = new PosSubSet<Vertex<Integer>>(g);
		ts3.addAll(s3);

		Stack<DNode.D<Integer>> s = new Stack<DNode.D<Integer>>();
		if (ts1.size() > 0) {
			s.add(decomp.addLeft(decomp.root(), ts1));
		}
		PosSubSet<Vertex<Integer>> temp = new PosSubSet<Vertex<Integer>>(g);
		temp.addAll(ts2);
		temp.addAll(ts3);
		boolwidth.DNode.D<Integer> r = decomp.addRight(decomp.root(), temp);

		int maxN = 1;
		if (decomp.hasRight(decomp.root())
				&& decomp.root().getRight().element().size() > 1) {
			s.add(decomp.addLeft(r, ts2));
			s.add(decomp.addRight(r, ts3));
			int tcl = CutBool.countNeighborhoods(decomp.getCut(r.getLeft()),
					minbw);
			if (tcl == CutBool.BOUND_EXCEEDED) {
				return decomp;
			}
			int tcr = CutBool.countNeighborhoods(decomp.getCut(r.getLeft()),
					minbw);
			if (tcr == CutBool.BOUND_EXCEEDED) {
				return decomp;
			}
			maxN = Math.max(tcl, tcr);
		}
		// RandomSplitter.evenSplit(decomp, s);
		int tc = CutBool.countNeighborhoods(decomp.getCut(decomp.root()
				.getLeft()), minbw);
		if (tc == CutBool.BOUND_EXCEEDED) {
			return decomp;
		}
		maxN = Math.max(maxN, tc);
		System.out.println("maxN = " + maxN);
		searchSplit(decomp, s, maxN);
		return decomp;
	}

	private static double[] sum1(PosSet<Vertex<Integer>> s1,
			PosSet<Vertex<Integer>> s2, PosSet<Vertex<Integer>> s3,
			IntegerGraph g) {
		int n = s1.size() + s2.size() + s3.size();
		double[] t = new double[3];
		// http://stackoverflow.com/questions/529085/java-how-to-generic-array-creation
		PosSet<?>[] tmp = { s1, s2, s3 };
		PosSet<Vertex<Integer>>[] S = castArray(tmp);
		for (int a = 0; a < 3; a++) {
			for (Vertex<Integer> v : S[a]) {
				int degree = 0;
				for (Vertex<Integer> u : S[(a + 1) % 3]) {
					if (g.areAdjacent(u, v)) {
						degree++;
					}
				}
				for (Vertex<Integer> u : S[(a + 2) % 3]) {
					if (g.areAdjacent(u, v)) {
						degree++;
					}
				}
				if (degree > 0) {
					t[a] += Math.log(1.0 / (0.6 + 0.4 * degree
							/ (n - S[a].size())));
				}
			}
		}
		Arrays.sort(t);
		return t;
	}

	private static double[] sum2(PosSet<Vertex<Integer>> s1,
			PosSet<Vertex<Integer>> s2, PosSet<Vertex<Integer>> s3,
			IntegerGraph g) {
		double[] t = new double[3];
		PosSet<?>[] tmp = { s1, s2, s3 };
		PosSet<Vertex<Integer>>[] S = castArray(tmp);
		// int[] numN = new int[3];
		for (int a = 0; a < 3; a++) {
			// for(Vertex<Integer> v : S[a])
			// {
			// int degree=0;
			// for(Vertex<Integer> u : S[(a+1)%3]){
			// if(g.areAdjacent(u, v))
			// degree++;
			// }
			// for(Vertex<Integer> u : S[(a+2)%3]){
			// if(g.areAdjacent(u, v))
			// degree++;
			// }
			// if(degree>0)
			// numN[a]++;
			// }
			for (Vertex<Integer> v : S[a]) {
				int degree = 0;
				for (Vertex<Integer> u : S[(a + 1) % 3]) {
					if (g.areAdjacent(u, v)) {
						degree++;
					}
				}
				for (Vertex<Integer> u : S[(a + 2) % 3]) {
					if (g.areAdjacent(u, v)) {
						degree++;
					}
				}
				if (degree > 0) {
					t[a] += degree + 10;
				}
			}
		}
		Arrays.sort(t);
		return t;
	}

	private static boolean swap1(PosSet<Vertex<Integer>> s1,
			PosSet<Vertex<Integer>> s2, IntegerGraph g,
			PosSet<Vertex<Integer>> s3) {
		double[] t = sum1(s1, s2, s3, g);
		double tval = 2.0 * t[0] + t[1] + t[2];
		boolean improve = false;
		int att = 0;

		for (int a = 0; a < s1.size(); a++) {
			for (int b = 0; b < s2.size(); b++) {
				Vertex<Integer> u = s1.getVertex(a);
				Vertex<Integer> v = s2.getVertex(b);
				// swap
				s1.set(a, v);
				s2.set(b, u);
				double[] tt = sum1(s1, s2, s3, g);
				// int[] tt = CutBool.countNeighborhoods(g)
				double ttval = 2.0 * tt[0] + tt[1] + tt[2];
				// swap back
				if (tval < ttval) {
					s1.set(a, u);
					s2.set(b, v);
				} else {
					// System.out.println(ttval);
					if (ttval < tval) {
						tval = ttval;
						improve = true;
						if (tval - ttval < 0.01) {
							if (att > 100) {
								improve = false;
							} else {
								att++;
							}
						} else if (att > 0) {
							att--;
						}

					}
				}
			}
		}
		return improve;
	}

	private static boolean swap2(PosSet<Vertex<Integer>> s1,
			PosSet<Vertex<Integer>> s2, IntegerGraph g,
			PosSet<Vertex<Integer>> s3) {
		double[] t = sum2(s1, s2, s3, g);
		double tval = t[0] + t[1] + t[2];
		boolean improve = false;

		for (int a = 0; a < s1.size(); a++) {
			for (int b = 0; b < s2.size(); b++) {
				Vertex<Integer> u = s1.getVertex(a);
				Vertex<Integer> v = s2.getVertex(b);
				// swap
				s1.set(a, v);
				s2.set(b, u);
				double[] tt = sum2(s1, s2, s3, g);
				double ttval = tt[0] + tt[1] + tt[2];
				// swap back
				if (tval < ttval) {
					s1.set(a, u);
					s2.set(b, v);
				} else {
					// System.out.println(ttval);
					if (ttval < tval) {
						tval = ttval;
						improve = true;
					}
				}
			}
		}
		return improve;
	}
}