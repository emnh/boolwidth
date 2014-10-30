package boolwidth;

import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Map;
import java.util.Stack;

// TODO: optimize by not computing cutbool of complements
// TODO: optimize by integrating final answer loop

// TODO: more cool statistics

// TODO: top-down recursive. prioritize computation "threads".

// boolean-width of a graph is the minimum of boolean-widths over all boolean-width decompositions

// enumerate all vertex 2-partitions and compute cut-bool
// or assume 1/3+2/3 and enumerate just those vertex 2-partitions and compute cut-bool

// speed-up: given boolean-width of one decomposition, we can stop if the first cut has greater width
// iow, boolean-width of any decomposition becomes upper-bound on boolean-width of graph

public class ExactBoolWidth<TVertex extends Vertex<V>, V, E> {

	private static final boolean debug = false;

	private Decomposition.D<V, E> decomposition = null;

	Statistics statistics;

	public final class Computer {

		public int result;
		public final static int FAILED_TO_MEET_BOUND = 0;

		private final IGraph<TVertex, V, E> graph;

		// computation variables
		int vertexct;
		int fullset;
		int numpartitions;
		int[] adjMatrix;
		int[][] binom;
		int[][] subset_order;

		// M[i] is max(boolean width of the minimal partial decomposition
		// of the nodes denoted by i, cutbool of the cut denoted by i)
		int[] boolwidth_bounds;
		static final int BOOLWIDTH_BOUNDS_UNINITIALIZED = Integer.MAX_VALUE;
		static final int BOOLWIDTH_BOUNDS_EXCEEDED = Integer.MAX_VALUE - 1;
		int[] leftchild;
		static final int LEFTCHILD_UNINITIALIZED = Integer.MAX_VALUE;
		static final int LEFTCHILD_ENUMERATED_UNINITIALIZED = Integer.MAX_VALUE - 1;
		static final int LEFTCHILD_BOUND_EXCEEDED = Integer.MAX_VALUE - 2;

		/**
		 * Compute exact Boolean-width of a graph. doesn't use vertex subset
		 * class.
		 * 
		 * @param boolw_upper_bound
		 *            is an upper bound on boolean width. if > 0 it is used to
		 *            optimize.
		 */
		public Computer(IGraph<TVertex, V, E> graph, int boolw_upper_bound) {

			this.graph = graph;
			this.vertexct = graph.numVertices();
			this.fullset = (1 << graph.numVertices()) - 1;
			this.numpartitions = 1 << this.vertexct;
			this.adjMatrix = graph.intBitsAdjacencyMatrix();

			CutBool cutbool_computer;
			if (ExactBoolWidth.this.statistics == null) {
				cutbool_computer = new CutBool(false);
			} else {
				cutbool_computer = new CutBool(
						ExactBoolWidth.this.statistics.cutbool_statistics);
			}

			// > 32 not supported. intractable anyway
			// <= 30 because we don't want to deal with signed integers
			assert this.vertexct <= 30 : this.vertexct;

			ExactBoolWidth.this.statistics.graph_vertexct = this.vertexct;

			if (this.vertexct == 0) {
				this.result = 1;
				return; // XXX: what's boolean width of empty graph?
			}

			// create an array of all subsets ordered by subset size
			// could use Martin's bit-fiddling tricks on the fly instead
			// hold current index when iterating simultaneously over all subsets
			int[] subset_index = new int[this.vertexct + 1];
			Arrays.fill(subset_index, 0);

			// TODO: could be done in one fell swoop
			this.binom = new int[this.vertexct + 1][];
			for (int i = 1; i <= this.vertexct; i++) {
				this.binom[i] = CutBool.binom(i);
			}

			this.subset_order = new int[this.vertexct + 1][];
			for (int i = 0; i < this.subset_order.length; i++) {
				// System.out.printf("%d %d\n", i, subset_order_sizes[i]);
				this.subset_order[i] = new int[this.binom[this.vertexct][i]];
				// Arrays.fill(subset_order[i], Integer.MAX_VALUE);
			}
			for (int i = 0; i < this.numpartitions; i++) {
				int size = Integer.bitCount(i);
				this.subset_order[size][subset_index[size]] = i;
				subset_index[size]++;
			}

			this.boolwidth_bounds = new int[this.numpartitions];
			this.leftchild = new int[this.numpartitions];

			// initialize M[X] to 2^(min(|X|, n-|X|))
			Arrays.fill(this.boolwidth_bounds, BOOLWIDTH_BOUNDS_UNINITIALIZED);
			Arrays.fill(this.leftchild, LEFTCHILD_UNINITIALIZED);

			// splitsize <= 2/3*vertexct
			for (int splitsize = 1; 3 * splitsize <= 2 * this.vertexct; splitsize++) {

				// loop over subsets of X of size splitsize
				for (int subset : this.subset_order[splitsize]) {
					// if (splitsize < 3) {
					// System.out.printf("%32s\n",
					// Integer.toBinaryString(subset));
					// }

					// for debug: to tell difference between not enumerated and
					// not set
					this.leftchild[subset] = LEFTCHILD_ENUMERATED_UNINITIALIZED;

					int subset_complement = this.fullset ^ subset;

					// TODO: don't recompute if M[i_complement] has been
					// computed
					// might be larger than "cutbool" then, but will be the
					// bound we need
					// TODO: continue if bw < minbw found so far
					// prerequisite: compute bw of all full cuts
					int cutbool = cutbool_computer.countNeighborhoods(
							this.adjMatrix, subset, subset_complement,
							boolw_upper_bound);

					if (ExactBoolWidth.this.statistics != null) {
						ExactBoolWidth.this.statistics.cutbools.count(cutbool);
					}

					if (cutbool == CutBool.BOUND_EXCEEDED) {
						// just so we know it's initialized
						this.boolwidth_bounds[subset] = BOOLWIDTH_BOUNDS_EXCEEDED;
						this.leftchild[subset] = LEFTCHILD_BOUND_EXCEEDED;
						continue;
					}

					// get indexes of vertices in i
					int[] indexes = new int[splitsize];
					int curidx = 0;
					int is = subset;
					for (int k = 0; is > 0 && k < this.vertexct; k++) {
						if ((is & 1) == 1) {
							indexes[curidx] = k;
							curidx++;
						}
						is >>= 1;
					}
					assert curidx == splitsize : String.format("i: %s\n",
							Integer.toBinaryString(subset))
							+ String.format("curidx/splitsize: %d/%d\n",
									curidx, splitsize);

					boolean bounds_not_updated = partialBoolwidth(splitsize,
							subset, cutbool, indexes);

					if (bounds_not_updated) {
						// System.out.println("no break at " + rounds);
						// assert splitsize == 1 : splitsize;
						// if (debug) {
						// System.out.printf("s: %d, M[i]: %d, bw: %d\n",
						// splitsize, M[i], bw);
						// }
						this.boolwidth_bounds[subset] = cutbool;
						this.leftchild[subset] = 0;
					} else {
						this.boolwidth_bounds[subset] = Math.max(cutbool,
								this.boolwidth_bounds[subset]);
					}
					// update upper bound
					// TODO: update complement
					if (this.boolwidth_bounds[subset] != BOOLWIDTH_BOUNDS_UNINITIALIZED
							&& this.boolwidth_bounds[subset] != BOOLWIDTH_BOUNDS_EXCEEDED
							&& this.boolwidth_bounds[subset_complement] != BOOLWIDTH_BOUNDS_UNINITIALIZED
							&& this.boolwidth_bounds[subset_complement] != BOOLWIDTH_BOUNDS_EXCEEDED) {
						int newbound = Math.max(this.boolwidth_bounds[subset],
								this.boolwidth_bounds[subset_complement]);
						if (newbound < boolw_upper_bound) {
							// System.out
							// .printf(
							// "updated upper bound: %d->%d=max(%d,%d), split=%s/%s\n",
							// boolw_upper_bound,
							// newbound,
							// boolwidth_bounds[subset],
							// boolwidth_bounds[subset_complement],
							// Integer.toBinaryString(subset),
							// Integer
							// .toBinaryString(subset_complement));
							boolw_upper_bound = newbound;
						}
					}
					// assert isInitialized(boolwidth_bounds[subset]);
				}
			}

			// find boolwidth, by minimizing over max of both sides of the cut
			int minbw = 1 << this.vertexct;

			// final answer
			// TODO: compute final answer in loop above
			for (int i = 1; i < this.numpartitions; i++) {
				int setsize = Integer.bitCount(i);
				if (!(3 * setsize >= this.vertexct && 2 * setsize <= this.vertexct)) {
					continue;
				}
				int i_complement = (1 << this.vertexct) - 1 ^ i;
				// if (M[i] != M[i_complement]) {
				// System.out.printf("Mi: %d, Mc: %d\n", M[i], M[i_complement]);
				// }
				int bw = Math.max(this.boolwidth_bounds[i],
						this.boolwidth_bounds[i_complement]);

				if (ExactBoolWidth.this.statistics != null
						&& isBoundInitialized(bw)) {
					ExactBoolWidth.this.statistics.boolwidths.count(bw);
				}

				if (debug) {
					System.out.printf("M[%32s] = %d\n", Integer
							.toBinaryString(i), this.boolwidth_bounds[i]);
					System.out.printf("M[%32s] = %d\n", Integer
							.toBinaryString(i_complement),
							this.boolwidth_bounds[i]);
				}
				if (bw < minbw) {
					minbw = bw;
					this.leftchild[this.fullset] = i;
				}
			}

			// temp stats
			if (debug) {
				for (int i = 1; i <= this.numpartitions / 2; i++) {
					int i_complement = (1 << this.vertexct) - 1 ^ i;
					int bw = Math.max(this.boolwidth_bounds[i],
							this.boolwidth_bounds[i_complement]);
					if (bw == minbw) {
						buildDecomposition(graph, this.leftchild, i)
						.printCutSizes();
					}
				}
			}

			if (minbw <= boolw_upper_bound) {
				ExactBoolWidth.this.decomposition = buildDecomposition(graph,
						this.leftchild, this.leftchild[this.fullset]);
				long dcbw = CutBool
				.booleanWidth(ExactBoolWidth.this.decomposition);
				assert dcbw == minbw : dcbw + " " + minbw;
				this.result = minbw;
			} else {
				ExactBoolWidth.this.decomposition = null;
				// System.out.printf(
				// "warning: didn't meet bound: min=%d > bound=%d\n",
				// minbw, boolw_upper_bound);
			}
		}

		public ArrayList<Decomposition<DNode.D<V>, V, E>> getDecompositionsOfWidth(
				int boolwidth) {
			ArrayList<Decomposition<DNode.D<V>, V, E>> decomps = new ArrayList<Decomposition<DNode.D<V>, V, E>>();

			for (int i = 1; i <= this.numpartitions / 2; i++) {
				int i_complement = (1 << this.vertexct) - 1 ^ i;
				int bw = Math.max(this.boolwidth_bounds[i],
						this.boolwidth_bounds[i_complement]);
				if (bw == boolwidth) {
					decomps.add(buildDecomposition(this.graph, this.leftchild,
							i));
				}
			}

			return decomps;
		}

		/**
		 * Compute boolwidth for a set representing one side of a cut
		 * 
		 * @param splitsize
		 *            size of set == Integer.bitCount(set)
		 * @param set
		 * @param cutbool
		 *            goal boolwidth. if we find a partial better than this we
		 *            can stop. normally this will be cutbool of subset
		 * @param indexes
		 * @return
		 */
		private boolean partialBoolwidth(int splitsize, int set, int cutbool,
				int[] indexes) {

			if (ExactBoolWidth.this.statistics != null) {
				ExactBoolWidth.this.statistics.pbw_calls++;
			}

			boolean boolwidth_bounds_not_updated = true;
			int curidx;

			assert splitsize == Integer.bitCount(set);

			if (splitsize <= 1) {
				return boolwidth_bounds_not_updated;
			}

			int thisrounds = 0;
			boolean finish_early = false;

			// enumerate all subsets of size splitsize with first bit 0
			for (int compact_subset = 1; compact_subset <= 1 << splitsize - 1; compact_subset++) {

				// enumerate subsets by size. half are complements
				// for (int subsplitsize = 1; subsplitsize <= splitsize / 2;
				// subsplitsize++) {
				// // enumerate subsets of size subsplitsize of the full set of
				// size splitsize
				// int num_subsets = binom[splitsize][subsplitsize];
				// for (int i = 0; i < num_subsets; i++) {
				// int compact_subset = subset_order[subsplitsize][i];

				if (ExactBoolWidth.this.statistics != null) {
					ExactBoolWidth.this.statistics.pbw_iterations++;
					thisrounds++;
				}

				// compute subset of set. example, set = 1001:
				// compact_subset iterates subsets 0, 1, 10, 11 while
				// subset iterates subsets like 0, 1, 1000,1001
				int subset = 0;
				for (curidx = 0; curidx < splitsize; curidx++) {
					int bit = compact_subset >> curidx & 1;
				subset |= bit << indexes[curidx];
				}
				int subset_complement = set ^ subset;

				// if (Integer.bitCount(subset) != subsplitsize) {
				// for (int j : subset_order[subsplitsize]) {
				// System.out.println(Integer.toBinaryString(j));
				// }
				// System.out.printf("\n%32d\n%32d\n%32s\n%32s\n%32s\n%32s\n\n",
				// num_subsets,
				// subsplitsize,
				// Integer.toBinaryString(set),
				// Integer.toBinaryString(compact_subset),
				// Integer.toBinaryString(subset),
				// Integer.toBinaryString(subset_complement));
				// assert false;
				// }

				if (debug) {
					assert !(subset == 0 || subset_complement == 0) : "illegal split";
				}

				if (!isBoundInitialized(this.boolwidth_bounds[subset])) {
					// assert false;
				}
				if (!isBoundInitialized(this.boolwidth_bounds[subset_complement])) {
					// assert false;
				}

				int max_bw_j_jc = Math.max(this.boolwidth_bounds[subset],
						this.boolwidth_bounds[subset_complement]);
				if (max_bw_j_jc <= this.boolwidth_bounds[set]) {
					this.leftchild[set] = subset;
					this.boolwidth_bounds[set] = max_bw_j_jc;
					boolwidth_bounds_not_updated = false;
					if (this.boolwidth_bounds[set] <= cutbool) {
						// System.out.println("break at " + rounds);
						finish_early = true;
						break;
					}
				}
				if (finish_early) {
					break;
				}
			}
			// }
			if (boolwidth_bounds_not_updated) {
				assert splitsize == 1;
			}
			if (ExactBoolWidth.this.statistics != null) {
				if (finish_early) {
					// statistics.hrounds.count(-1, thisrounds);

				} else {
					ExactBoolWidth.this.statistics.hrounds.count(thisrounds);
					// statistics.hrounds.count(-2, thisrounds);
				}
			}
			return boolwidth_bounds_not_updated;
		}
	}

	/**
	 * statistics note that these are initialized with class and accumulate over
	 * multiple runs of methods
	 * */
	public final class Statistics {
		// histogram: cutbool of all subsets
		public Histogram<Integer> cutbools = new Histogram<Integer>();;
		// histogram: for all subsets X: max(boolwidth(X),boolwidth(X
		// complement))
		public Histogram<Integer> boolwidths = new Histogram<Integer>();
		public CutBool.Statistics cutbool_statistics = new CutBool.Statistics();
		public long pbw_iterations = 0;
		public long pbw_calls = 0;
		public int graph_vertexct = 0;

		public Histogram<Integer> hrounds = new Histogram<Integer>() {
			@Override
			public String toString() {
				StringBuilder sb = new StringBuilder();
				Formatter f = new Formatter(sb);
				int sum = 0;
				// int keysum = 0;
				for (Map.Entry<Integer, Integer> e : this.buckets.entrySet()) {
					int prod = e.getKey() * e.getValue();
					sum += prod;
					f.format("%d: %d. *%d *cumulative: %d\n", e.getKey(), e
							.getValue(), prod, sum);
				}
				return sb.toString();
			}
		};

		public void printStats() {

			assert this.graph_vertexct > 0;

			System.out.println(this.cutbool_statistics);

			System.out.println("Cutbools histogram:");
			System.out.println(this.cutbools);

			System.out.printf("partialboolwidth: ");
			System.out.printf(
					"calls: %d, iterations: %d, avg iter pr call: %.2f\n",
					this.pbw_calls, this.pbw_iterations,
					(double) this.pbw_iterations / this.pbw_calls);
			if (this.hrounds.size() > 0) {
				System.out.printf("iterations histogram for no-smaller:%s\n",
						this.hrounds);
			}
			System.out.println();

			System.out.println("First cuts with boolean width:");
			int sum = 0;
			for (Map.Entry<Integer, Integer> e : this.boolwidths.getBuckets()
					.entrySet()) {
				sum += e.getValue();
				System.out.printf("count(bw(%d))=%d, cumulative: %.2f%%\n", e
						.getKey(), e.getValue(), (float) sum * 100
						/ (1 << this.graph_vertexct - 1));
			}
		}
	}

	protected static <TVertex extends Vertex<V>, V, E> Decomposition.D<V, E> buildDecomposition(
			IGraph<TVertex, V, E> graph, int[] leftchild, int topleft) {
		int fullset = (1 << graph.numVertices()) - 1;
		return buildDecomposition(graph, leftchild, fullset, topleft);
	}

	@SuppressWarnings("unchecked")
	protected static <TVertex extends Vertex<V>, V, E> Decomposition.D<V, E> buildDecomposition(
			IGraph<TVertex, V, E> graph, int[] leftchild, int root, int topleft) {
		int parent;
		// TODO: fix cast

		Decomposition.D<V, E> dc = new Decomposition.D<V, E>(
				(IGraph<Vertex<V>, V, E>) graph);
		DNode.D<V> parent_node = dc.root();
		Stack<DNode.D<V>> dnodes = new Stack<DNode.D<V>>();
		parent_node.setSubSet(root);
		dnodes.push(parent_node);

		while (!dnodes.empty()) {

			parent_node = dnodes.pop();

			// int subsets
			parent = parent_node.getSubSet();
			int left;
			if (parent == root) {
				left = topleft;
			} else {
				left = leftchild[parent];
			}

			assert left != ExactBoolWidth.Computer.LEFTCHILD_UNINITIALIZED : String
			.format("uninitialized: parent: %s\n", Integer
					.toBinaryString(parent));
			assert left != ExactBoolWidth.Computer.LEFTCHILD_ENUMERATED_UNINITIALIZED : String
			.format("not set: parent: %s\n", Integer
					.toBinaryString(parent));
			int right = parent ^ left;

			if (debug) {
				StringBuilder sb = new StringBuilder();
				Formatter f = new Formatter(sb);
				f.format("\np=%s\nl=%s\nr=%s\n\n", Integer
						.toBinaryString(parent), Integer.toBinaryString(left),
						Integer.toBinaryString(right));
				assert Integer.bitCount(left) < Integer.bitCount(parent) : sb
				.toString();
				assert Integer.bitCount(right) < Integer.bitCount(parent) : sb
				.toString();
			}

			// TODO: change decomposition to use VertexSet and make this code
			// nicer
			PosSet<TVertex> leftset = intToSet(graph, left);
			PosSet<TVertex> rightset = intToSet(graph, right);
			assert leftset.size() == Integer.bitCount(left) : String.format(
					"%s %s\n", leftset, Integer.toBinaryString(left));
			assert rightset.size() == Integer.bitCount(right) : String.format(
					"%s %s\n", rightset, Integer.toBinaryString(right));

			// add children
			for (Vertex<V> v : leftset) {
				dc.addLeft(parent_node, v);
			}
			for (Vertex<V> v : rightset) {
				dc.addRight(parent_node, v);
			}

			// queue dnodes for splitting
			// if size > 1 we want to split again
			if (leftset.size() > 1) {
				DNode.D<V> dnode = parent_node.getLeft();
				assert dnode != null;
				dnodes.add(dnode);
				dnode.setSubSet(left);
			}
			if (rightset.size() > 1) {
				DNode.D<V> dnode = parent_node.getRight();
				assert dnode != null;
				dnodes.add(dnode);
				dnode.setSubSet(right);
			}
		}
		dc.fixIds();
		CutBool.computeAllHoodCounts(dc);
		return dc;
	}

	// can be optimized for 32-bit if it becomes a hotspot
	public static <TVertex extends Vertex<V>, V, E> PosSet<TVertex> intToSet(
			IGraph<TVertex, V, E> g, int intset) {
		PosSet<TVertex> vertices = new PosSet<TVertex>(g.vertices());
		PosSubSet<TVertex> subset = new PosSubSet<TVertex>(vertices);
		subset.setBits(0, intset);
		return subset.getSubSet();
	}

	public static boolean isBoundInitialized(long val) {
		return val != ExactBoolWidth.Computer.BOOLWIDTH_BOUNDS_UNINITIALIZED;
	}

	public ExactBoolWidth(boolean stats) {
		if (stats) {
			this.statistics = new Statistics();
		}
	}

	public long exactBooleanWidthNew(IGraph<TVertex, V, E> graph,
			long upper_bound) {
		return new Computer(graph, (int) upper_bound).result;
	}

	public Decomposition.D<V, E> getDecomposition() {
		return this.decomposition;
	}

	// public static<TVertex extends Vertex<V>, V, E> void sdfds(IGraph<TVertex,
	// V, E> graph) {
	// IGraph<? extends Vertex<V>, V, E> graph2 = graph;
	// graph2.add(new Vertex<V>());
	// //IGraph<TVertex, V, E> graph2 = graph;
	// System.out.println(graph2);
	// }

	public Statistics getStatistics() {
		return this.statistics;
	}

	public void setDecomposition(Decomposition.D<V, E> decomposition) {
		this.decomposition = decomposition;
	}

	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
}
