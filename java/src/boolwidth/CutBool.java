package boolwidth;

import boolwidth.cutbool.CutBoolComparatorCCMIS;
import graph.*;
import graph.subsets.PosSet;
import graph.subsets.PosSubSet;
import sadiasrc.decomposition.CCMIS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeSet;

// TODO: print Hasse-like diagram to understand better and perhaps optimize
// TODO: approximate using entropy, sample bits

/**
 * Contains methods for finding the boolean-width of a graph.
 */
public class CutBool {

	public Statistics stats;

	public final static int BOUND_EXCEEDED = -1;

	public final static int BOUND_UNINITIALIZED = -1;

	public static final class Statistics {
		/* /I = "Integer"; */

		ArrayList</* I; */Integer> a;
		public int unions_taken = 0;
		public int neighborhoods_found = 0;
		public int calls = 0;

		@Override
		public String toString() {
			String s = "\nCutBool statistics:\n";
			s += String.format("calls: %d\n", this.calls);
			s += String.format("unions taken: %d\n", this.unions_taken);
			s += String.format("neighborhoods found: %d\n",
					this.neighborhoods_found);
			if (this.neighborhoods_found > 0) {
				s += String.format("waste factor: %d\n", this.unions_taken
						/ this.neighborhoods_found);
			}
			// s += "\n";
			return s;
		}
	}

	/**
	 * Returns the best general upper bound on 2^bw, that is, 2^(n / 3 rounded
	 * up).
	 * 
	 * @param n
	 *            number of vertices
	 * @return
	 */
	public static long bestGeneralUpperBound(int n) {
		if (n <= 4) {
			return 1;
		}
		int bw = (n - 1) / 3 + 1;
		if (bw >= 63) {
			System.out
			.println("Warning: returning MAX_VALUE for bestGeneralUpperBound");
			return Long.MAX_VALUE;
		}
		return 1 << bw;
	}

	/**
	 * Returns the best general upper bound on 2^bw, if the cut is not at the
	 * top, that is, 2^(n / 2).
	 * 
	 * @param nodeCount
	 *            number of vertices
	 * @return
	 */
	public static long bestGeneralUpperBound(int nodeCount, boolean toplevelcut) {
        // XXX: this should not be committed
        return Long.MAX_VALUE;

        /*
		if (toplevelcut) {
			return bestGeneralUpperBound(nodeCount);
		} else {
			int bw = (nodeCount - 1) / 2 + 1;
			if (bw >= 63) {
				System.out.println("Warning: returning Long.MAX_VALUE for bestGeneralUpperBound instead of (nodeCount - 1) / 2 + 1.");
				return Long.MAX_VALUE;
			}
			return 1 << bw;
		}*/
	}

	/**
	 * Generate all binomial coefficients of n XXX: optimize symmetry
	 */
	public static int[] binom(int n) {
		int[] b = new int[n + 1];

		b[0] = 1;
		for (int i = 1; i <= n; ++i) {
			b[i] = 1;
			for (int j = i - 1; j > 0; --j) {
				b[j] += b[j - 1];
			}
		}
		return b;
	}

	/** @return 2^Boolean-width of given decomposition. */
	public static <TVertex extends DNode<TVertex, V>, V, E> long booleanWidth(
			Decomposition<TVertex, V, E> decomp) {
        return booleanWidth(decomp, BOUND_UNINITIALIZED);
	}

	/** @return 2^Boolean-width of given decomposition. */
	public static <TVertex extends DNode<TVertex, V>, V, E> long booleanWidth(
			Decomposition<TVertex, V, E> decomposition, long upper_bound) {
		// int n = decomp.graph.numVertices();
		long hoods = 0;
		int bw = 1;
		boolean nice = decomposition.hasRight(decomposition.root())
		&& decomposition.hasLeft(decomposition.root());
		for (TVertex dn : decomposition.vertices()) {
			if (dn == decomposition.root()) {
				continue;
			}
			if (nice && dn == decomposition.right(decomposition.root())) {
				continue;
			}
			// System.out.println("so far:"+bw);
			// XXX: the optimization below doesn't work on partial
			// decompositions
			// with one node
			// if (dn.element().size() > bw && (n - dn.element().size()) > bw) {
			//int thisHoods = countNeighborhoods(decomposition.getCut(dn), upper_bound);
            long thisHoods = CCMIS.BoolDimBranch(CutBoolComparatorCCMIS.convertSadiaBiGraph(decomposition.getCut(dn)));

			// dn.setAttr("hoods", thisHoods);

			// dn.setCutBool(thisHoods);

			if (upper_bound != BOUND_UNINITIALIZED
					&& thisHoods == BOUND_EXCEEDED) {
				return BOUND_EXCEEDED;
			}

			if (thisHoods > hoods) {
				int lh = 1 + (int) (Math.log(thisHoods - 1) / Math.log(2.0));
				bw = Math.max(bw, lh);
				hoods = thisHoods;
			}
			// }
		}
		// decomp.setAttr("hoods", hoods);
		BooleanDecomposition.setBoolWidth(decomposition, hoods);
		return hoods;
	}

	/** @return 2^Boolean-width of given decomposition.
	 *  Sets the "hoods" attribute for each cut.
	 *  TODO: make a getter/setter class for hoods attribute
	 * */
	public static <TVertex extends DNode<TVertex, V>, V, E> int computeAllHoodCounts(
			Decomposition<TVertex, V, E> decomp) {
		int hoods = 0;
		for (TVertex dn : decomp.vertices()) {
			int thisHoods = countNeighborhoods(decomp.getCut(dn));
			dn.setAttr("hoods", thisHoods);
			if (thisHoods > hoods) {
				hoods = thisHoods;
			}
		}
		decomp.setAttr("hoods", hoods);
		return hoods;
	}

    public static <TVertex extends Vertex<V>, V, E> int countNeighborhoodsG(AdjacencyListGraph<TVertex, V, E> g) {
        return countNeighborhoods(g, BOUND_UNINITIALIZED);
    }

    /** @return 2^Boolean-width of given cut. */
    public static <TVertex extends Vertex<V>, V, E> int countNeighborhoodsG(AdjacencyListGraph<TVertex, V, E> g,
                                                long upper_bound) {

        // set of right,left nodes
        final PosSet<TVertex> all = new PosSet<>(g.vertices());
        //final PosSet<TVertex> lefts = new PosSet<>(g.vertices());

        TreeSet<PosSubSet<TVertex>> initialhoods;

        // set of neighborhoods of right nodes
        final TreeSet<PosSubSet<TVertex>> nodeHoods = new TreeSet<>();

        // initialize all neighborhood sets of 1 left node
        for (TVertex node : g.vertices()) {
            PosSubSet<TVertex> neighbors = new PosSubSet<>(all, g.incidentVertices(node));
            if (neighbors.size() > 0) {
                nodeHoods.add(neighbors);
            }
        }

        TreeSet<PosSubSet<TVertex>> hoods = new TreeSet<>();

        // adds empty set, subset of all
        hoods.add(new PosSubSet<>(all));
        initialhoods = nodeHoods;

        for (PosSubSet<TVertex> neighbors : initialhoods) {
            TreeSet<PosSubSet<TVertex>> newhoods = new TreeSet<>();
            for (PosSubSet<TVertex> hood : hoods) {
                PosSubSet<TVertex> newhood = hood.union(neighbors);
                if (!(hoods.contains(newhood) || newhoods.contains(newhood))) {
                    newhoods.add(newhood);
                }
            }
            hoods.addAll(newhoods);

            if (upper_bound != BOUND_UNINITIALIZED
                    && hoods.size() > upper_bound) {
                return BOUND_EXCEEDED;
            }
            // System.out.println("Number of neighbourhoods found so far: "+hoods.size());
        }
        return hoods.size();
    }


	public static <V, E> int countNeighborhoods(BiGraph<V, E> g) {
		return countNeighborhoods(g, BOUND_UNINITIALIZED);
	}

	/** @return 2^Boolean-width of given cut. */
	public static <V, E> int countNeighborhoods(BiGraph<V, E> g,
			long upper_bound) {

		// set of right,left nodes
		//final PosSet<Vertex<V>> rights = new PosSet<>(g.rightVertices());
		//final PosSet<Vertex<V>> lefts = new PosSet<>(g.leftVertices());
        final PosSet<Vertex<V>> all = new PosSet<>(g.vertices());

		TreeSet<PosSubSet<Vertex<V>>> initialhoods;

		// set of neighborhoods of left nodes
        final TreeSet<PosSubSet<Vertex<V>>> leftnodes = new TreeSet<>();

		// set of neighborhoods of right nodes
		final TreeSet<PosSubSet<Vertex<V>>> rightnodes = new TreeSet<>();

		// initialize all neighborhood sets of 1 left node
		for (Vertex<V> node : g.leftVertices()) {
            // must use full vertex set as ground set, because IDs are not translated
			PosSubSet<Vertex<V>> neighbors = new PosSubSet<>(all, g.incidentVertices(node));
			if (neighbors.size() > 0) {
				leftnodes.add(neighbors);
			}
		}
		for (Vertex<V> node : g.rightVertices()) {
            // must use full vertex set as ground set, because IDs are not translated
			PosSubSet<Vertex<V>> neighbors = new PosSubSet<>(all, g.incidentVertices(node));
			if (neighbors.size() > 0) {
				rightnodes.add(neighbors);
			}
		}

		TreeSet<PosSubSet<Vertex<V>>> hoods = new TreeSet<>();
		// choose the smallest neighborhood set
		// if (rights.size() - right_twin_count > lefts.size() - left_twin_count) {
		if (rightnodes.size() > leftnodes.size()) {
			// adds empty set, subset of all
			hoods.add(new PosSubSet<>(all));
			initialhoods = leftnodes;
		} else {
			// adds empty set, subset of all
			hoods.add(new PosSubSet<>(all));
			initialhoods = rightnodes;
		}

		for (PosSubSet<Vertex<V>> neighbors : initialhoods) {
			TreeSet<PosSubSet<Vertex<V>>> newhoods = new TreeSet<>();
			for (PosSubSet<Vertex<V>> hood : hoods) {
				PosSubSet<Vertex<V>> newhood = hood.union(neighbors);
				if (!(hoods.contains(newhood) || newhoods.contains(newhood))) {
					newhoods.add(newhood);
				}
			}
			hoods.addAll(newhoods);

			if (upper_bound != BOUND_UNINITIALIZED
					&& hoods.size() > upper_bound) {
				return BOUND_EXCEEDED;
			}
			// System.out.println("Number of neighbourhoods found so far: "+hoods.size());
		}
		return hoods.size();
	}

    public static <TVertex extends Vertex<V>, V, E> int countNeighborhoods(AdjacencyListGraph<TVertex, V, E> g) {
        return countNeighborhoods(g, BOUND_UNINITIALIZED);
    }

    /** @return 2^Boolean-width of given cut. */
    public static <TVertex extends Vertex<V>, V, E> int countNeighborhoods(AdjacencyListGraph<TVertex, V, E> g,
                                                long upper_bound) {
        // set of right,left nodes
        final PosSet<TVertex> nodes = new PosSet<TVertex>(g.vertices());

        TreeSet<PosSubSet<TVertex>> initialhoods;

        // set of neighborhoods of left nodes
        final TreeSet<PosSubSet<TVertex>> lrhoods = new TreeSet<PosSubSet<TVertex>>();

        // initialize all neighborhood sets of 1 left node
        for (TVertex node : g.vertices()) {
            PosSubSet<TVertex> neighbors = new PosSubSet<TVertex>(nodes, g.incidentVertices(node));
            if (neighbors.size() > 0) {
                lrhoods.add(neighbors);
            }
        }

        TreeSet<PosSubSet<TVertex>> hoods = new TreeSet<PosSubSet<TVertex>>();

        lrhoods.add(new PosSubSet<TVertex>(nodes));
        initialhoods = lrhoods;

        for (PosSubSet<TVertex> neighbors : initialhoods) {
            TreeSet<PosSubSet<TVertex>> newhoods = new TreeSet<PosSubSet<TVertex>>();
            for (PosSubSet<TVertex> hood : lrhoods) {
                PosSubSet<TVertex> newhood = hood.union(neighbors);
                if (!(hoods.contains(newhood) || newhoods.contains(newhood))) {
                    newhoods.add(newhood);
                }
            }
            hoods.addAll(newhoods);

            if (upper_bound != BOUND_UNINITIALIZED
                    && hoods.size() > upper_bound) {
                return BOUND_EXCEEDED;
            }
            // System.out.println("Number of neighbourhoods found so far: "+hoods.size());
        }
        return hoods.size();
    }


	public static <V, E> int countNeighborhoodsLazy(final BiGraph<V, E> g) {
		int i = 0;
		for (@SuppressWarnings("unused") PosSubSet<Vertex<V>> neighborhood : neighborhoodIter(g)) {
			i++;
		}
		return i;
	}

	/** @return 2^Boolean-width of given cut. */
	public static <V, E> Iterable<PosSubSet<Vertex<V>>> neighborhoodIter(final BiGraph<V, E> g) {

		class NeighborhoodIterator implements Iterator<PosSubSet<Vertex<V>>> {

			// set of right nodes
			PosSet<Vertex<V>> rights;

			// set of left nodes
			PosSet<Vertex<V>> lefts;

			TreeSet<PosSubSet<Vertex<V>>> initialhoods = new TreeSet<PosSubSet<Vertex<V>>>();
			// set of neighborhoods of left nodes
			TreeSet<PosSubSet<Vertex<V>>> leftnodes = new TreeSet<PosSubSet<Vertex<V>>>();
			// set of neighborhoods of right nodes
			TreeSet<PosSubSet<Vertex<V>>> rightnodes = new TreeSet<PosSubSet<Vertex<V>>>();

			// set of all unions of neighborhoods
			TreeSet<PosSubSet<Vertex<V>>> hoods = new TreeSet<PosSubSet<Vertex<V>>>();

			// iterator state
			PosSubSet<Vertex<V>> next = null;
			PosSubSet<Vertex<V>> currentInitialHood;
			Iterator<PosSubSet<Vertex<V>>> initialhoodsIterator;
			Iterator<PosSubSet<Vertex<V>>> hoodsIterator;
			TreeSet<PosSubSet<Vertex<V>>> newhoods = new TreeSet<PosSubSet<Vertex<V>>>();
			int count = 1; // always empty set

			public NeighborhoodIterator() {
				this.rights = new PosSet<Vertex<V>>(g.rightVertices());
				this.lefts = new PosSet<Vertex<V>>(g.leftVertices());

				// initialize all neighborhood sets of 1 left node
				int left_twin_count = 0;
				for (Vertex<V> node : g.leftVertices()) {
					PosSubSet<Vertex<V>> neighbors = new PosSubSet<Vertex<V>>(this.rights, g
							.incidentVertices(node));
					if (this.leftnodes.contains(neighbors)) {
						left_twin_count++;
					}
					this.leftnodes.add(neighbors);
				}
				int right_twin_count = 0;
				for (Vertex<V> node : g.rightVertices()) {
					PosSubSet<Vertex<V>> neighbors = new PosSubSet<Vertex<V>>(this.lefts, g
							.incidentVertices(node));
					if (this.rightnodes.contains(neighbors)) {
						right_twin_count++;
					}
					this.rightnodes.add(neighbors);
				}

				if (this.rights.size() - right_twin_count > this.lefts.size() - left_twin_count) {
					// adds empty set, subset of rights
					this.hoods.add(new PosSubSet<Vertex<V>>(this.rights));
					this.initialhoods = this.leftnodes;
				} else {
					// adds empty set, subset of lefts
					this.hoods.add(new PosSubSet<Vertex<V>>(this.lefts));
					this.initialhoods = this.rightnodes;
				}

				this.initialhoodsIterator = this.initialhoods.iterator();
				this.currentInitialHood = this.initialhoodsIterator.next();
				this.hoodsIterator = this.hoods.iterator();
				this.next = this.hoods.first();
			}

			@Override
			public void finalize() {
				System.out.printf("count: %d\n", this.count);
			}

			@Override
			public boolean hasNext() {
				if (this.next != null) {
					return true;
				} else {
					this.next = internalNext();
					return this.next != null;
				}
			}

			private PosSubSet<Vertex<V>> internalNext() {
				//initialhoodsIterator.next();
				//this.hoodsIterator = this.hoods.iterator();

				boolean done = false;
				PosSubSet<Vertex<V>> next = null;
				if (this.currentInitialHood != null) {
					while (!done) {
						if (this.hoodsIterator.hasNext()) {
							PosSubSet<Vertex<V>> hood = this.hoodsIterator.next();
							PosSubSet<Vertex<V>> newhood = hood.union(this.currentInitialHood);
							if (!(this.hoods.contains(newhood) || this.newhoods.contains(newhood))) {
								this.newhoods.add(newhood);
								next = newhood;
								this.count++;
								done = true;
							}
						} else {
							this.hoods.addAll(this.newhoods);
							this.newhoods = new TreeSet<PosSubSet<Vertex<V>>>();
							this.hoodsIterator = this.hoods.iterator();
							if (this.initialhoodsIterator.hasNext()) {
								this.currentInitialHood = this.initialhoodsIterator.next();
							} else {
								this.currentInitialHood = null;
								done = true;
							}
						}
					}
				}
				return next;
			}

			@Override
			public PosSubSet<Vertex<V>> next() throws NoSuchElementException {
				if (this.next != null) {
					PosSubSet<Vertex<V>> next = this.next;
					this.next = null;
					return next;
				} else {
					PosSubSet<Vertex<V>> next = internalNext();
					if (next == null) {
						throw new NoSuchElementException();
					}
					return next;
				}
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		}

		return new Iterable<PosSubSet<Vertex<V>>> () {
			@Override
			public Iterator<PosSubSet<Vertex<V>>> iterator() {
				return new NeighborhoodIterator();
			}
		};
	}


	/**
	 * Transpose a bit matrix
	 */
	public static int[] transpose(int[] m, int m_colct) {
		// abc ad
		// def to be
		// cf
		int m_rowct = m.length;
		int[] t = new int[m_colct];
		Arrays.fill(t, 0);
		for (int t_colidx = 0; t_colidx < m_rowct; t_colidx++) {
			for (int t_rowidx = 0; t_rowidx < m_colct; t_rowidx++) {
				// t[t_rowidx][t_colidx] = m[t_colidx][t_rowidx]
				t[t_rowidx] |= (m[t_colidx] >> t_rowidx & 1) << t_colidx;
			}
		}
		return t;
	}

	public CutBool(boolean stats_enabled) {
		if (stats_enabled) {
			this.stats = new Statistics();
		}
	}

	public CutBool(Statistics stats) {
		this.stats = stats;
	}

	/**
	 * TODO: optimization: check if using http://trove4j.sourceforge.net/ for
	 * TreeMap is faster
	 * 
	 * @param adjMatrix
	 * @param lefts
	 * @param rights
	 * @param upper_bound
	 *            if non-zero, aborts and returns 0 when more neighborhoods are
	 *            found
	 * @return Boolean-width of given cut.
	 */
	public int countNeighborhoods(int[] adjMatrix, int lefts, int rights,
			int upper_bound) {

		// int[] bigraph = new int[adjMatrix.length];

		if (this.stats != null) {
			this.stats.calls++;
		}

		TreeSet<Integer> leftneighbors_s = new TreeSet<Integer>();
		TreeSet<Integer> rightneighbors_s = new TreeSet<Integer>();

		boolean hit_bound = false;

		for (int rowidx = 0; rowidx < adjMatrix.length; rowidx++) {
			// if this is left node
			if ((rights >> rowidx & 1) == 0) {
				// retain edges from nodes in left to nodes in right
				// bigraph[rowidx] = adjMatrix[rowidx] & rights;
				int neighbors = adjMatrix[rowidx] & rights;
				if (neighbors != 0) {
					leftneighbors_s.add(neighbors);
				}

				// bound check
				hit_bound = upper_bound != BOUND_UNINITIALIZED
				&& leftneighbors_s.size() > upper_bound;
				if (hit_bound) {
					break;
				}
			} else {
				// retain edges from nodes in right to nodes in left
				// bigraph[rowidx] = adjMatrix[rowidx] & lefts;
				int neighbors = adjMatrix[rowidx] & lefts;
				if (neighbors != 0) {
					rightneighbors_s.add(neighbors);
				}

				// bound check
				hit_bound = upper_bound != BOUND_UNINITIALIZED
				&& rightneighbors_s.size() > upper_bound;
				if (hit_bound) {
					break;
				}
			}
		}

		// choose viewpoint: left or right. both give same result, so we
		// pick the one with fewest neighborhoods for speed
		TreeSet<Integer> initialhoods = rightneighbors_s.size() > leftneighbors_s
		.size() ? leftneighbors_s : rightneighbors_s;

		TreeSet<Integer> hoods = new TreeSet<Integer>();
		hoods.add(0); // add empty set

		// redundant list for iteration while modifying hoods
		ArrayList<Integer> hoods_previous = new ArrayList<Integer>();
		hoods_previous.add(0); // add empty set

		for (int neighbors : initialhoods) {
			if (hit_bound) {
				break;
			}
			int prevsize = hoods_previous.size();
			for (int i = 0; i < prevsize; i++) {
				int hood = hoods_previous.get(i);
				int newhood = hood | neighbors;
				if (this.stats != null) {
					this.stats.unions_taken++;
				}
				if (!hoods.contains(newhood)) {
					hoods_previous.add(newhood);
					hoods.add(newhood);
					// bound check
					if (upper_bound != BOUND_UNINITIALIZED
							&& hoods.size() > upper_bound) {
						hit_bound = true;
						break;
					}
				}
			}
			// System.out.println("Number of neighborhoods found so far: "+hoods.size());
		}

		// stats. disregard empty set (so waste factor is computed reasonably).
		if (this.stats != null) {
			this.stats.neighborhoods_found += hoods.size() - 1;
		}

		int ret = hit_bound ? BOUND_EXCEEDED : hoods.size();
		return ret;
	}

}
