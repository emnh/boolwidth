package boolwidth.heuristics;

import boolwidth.cutbool.CutBoolComparator;
import boolwidth.cutbool.CutBoolComparatorApprox;
import boolwidth.cutbool.CutBoolComparatorCCMIS;
import graph.PosSet;
import graph.PosSubSet;
import graph.Vertex;
import interfaces.IDecomposition;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;

import boolwidth.BooleanDecomposition;
import boolwidth.CutBool;

// TODO: make DNode generic on TreeSet or VertexSubset (for performance). use Set interface.

public class LocalSearchR<V, E> {

    // log values
    public String graphName;


	// dynamic search state
    private boolean has_valid_best_decomposition = false;
	private LSDecomposition.D<V, E> decomposition;
	private long graph_boolwidth_upper_bound = CutBool.BOUND_UNINITIALIZED;
	TreeMap<PosSubSet<Vertex<V>>, VertexSplit<V>> foundsplits = new TreeMap<PosSubSet<Vertex<V>>, VertexSplit<V>>();

    // static search state
    private CutBoolComparator<V, E> cmp;
    private Greedy<V, E> greedy;
    private GreedyDegree<V, E> greedyDegree;
    private static Random rnd;

	// === Algorithm configuration parameters
    public String comparatorImplementation = "UNN";
    private void initCutBoolComparator() {
        System.out.println("Using " + comparatorImplementation + " for counting neighborhoods.");
        switch (comparatorImplementation) {
            case "UNN":
                this.cmp = new CutBoolComparator<>(this.decomposition);
                break;
            case "CCMIS":
                this.cmp = new CutBoolComparatorCCMIS<>(this.decomposition);
                break;
            case "APX":
                this.cmp = new CutBoolComparatorApprox<>(this.decomposition);
                break;
        }
    }

	// do local search if true, re-randomize cuts completely if not
	private final boolean localSwaps = true;

	// set one side full, other empty, and move node by node greedy until the cut is balanced
    private final boolean useInitDegreeHeuristic = false;
	private final boolean useInitGreedy = true;
	private final boolean useInitGreedyExitEarly = true;

	// accept any new cut that is below the graph upper bound,
	// instead of just cuts smaller than current
	private final boolean allowEverythingBelowGraphUpperBound = false;

    // search time in milliseconds
    public static final int SEARCH_TIME = 10 * 1000;
    public static final int SEARCH_PRINT_INTERVAL = 1000;

    public static final int INNER_SEARCHSTEPS = 1;

	// === End config

	private PosSet<Vertex<V>> rootset;

	// stats
	public int failsToImproveCut = 0;
	public int triesToImproveCut = 0;

	public class Result {
		public LSDecomposition.D<V, E> decomposition;
		public boolean success = false;
	}

	public boolean beatsGraphUpperBound(VertexSplit<V> bag) {
		long bw = this.cmp.maxLeftRightCutBool(bag, getGraphBoolwidthUpperBound());
		return bw != CutBool.BOUND_EXCEEDED;
	}

	public long getGraphBoolwidthUpperBound() {
		return this.graph_boolwidth_upper_bound;
	}

	public boolean hasValidBestDecomposition() {
		return this.has_valid_best_decomposition;
	}

	protected void initCut(VertexSplit<V> split, int depth) {
		// first split is 1/3, the rest 1/2
		int left_size_initial;
		if (split.size() == this.decomposition.numGraphVertices()) {
			// left_size_initial = Util.divRoundUp(split.element().size(), 3);
			left_size_initial = split.element().size() / 2;
		} else {
			left_size_initial = split.element().size() / 2;
		}

		if (this.useInitDegreeHeuristic) {
            split.copyChildren(this.greedyDegree.initGreedy(split, depth == 0));
        } else if (this.useInitGreedy) {
			split.copyChildren(this.greedy.initGreedy(split, depth == 0));
		} else {
            // split randomly at first
			this.decomposition.splitRandom(split, left_size_initial);
		}

		split.updateCached(this.rootset, this.foundsplits);

		split.initialized = true;
	}

	protected void initCutRandom(VertexSplit<V> split) {
		int left_size_initial = split.size() / 2;

		this.decomposition.splitRandom(split, left_size_initial);

		split.getCached(this.rootset, this.foundsplits);

		split.setCached(this.rootset, this.foundsplits);

		split.initialized = true;
	}

	public boolean keepCut(VertexSplit<V> split, VertexSplit<V> newsplit, int depth) {
		boolean useNewCut = false;
		Comparator<VertexSplit<V>> valuecmp = this.cmp;
		if (this.localSwaps) {
			//useNewCut = valuecmp.compare(split, newsplit) <= 0;
			if (!this.allowEverythingBelowGraphUpperBound) {
				// first, be very greedy
				useNewCut = valuecmp.compare(split, newsplit) <= 0;
			} else {
				//useNewCut = valuecmp.compare(split, newsplit) <= 0;
				// then relax constraints to get out of local minimum
				//useNewCut = true;

				// if the old cut was under the limit
				long maxLeftRightCutBool =
					this.cmp.maxLeftRightCutBool(split, getGraphBoolwidthUpperBound());
				//				System.out.printf("depth: %d, old cut: %d, graph ubound: %d\n",
				//						depth,
				//						maxLeftRightCutBool, getGraphBoolwidthUpperBound());

				// if old split was below the graph upper bound, check if new split also is
				// and allow it then even if it is not less than the current split.
				// this is to avoid getting stuck deeper down by optimizing too much higher up
				if (maxLeftRightCutBool != CutBool.BOUND_EXCEEDED) {
					long newMaxLeftRightCutBool =
						this.cmp.maxLeftRightCutBool(newsplit, getGraphBoolwidthUpperBound());
					if (newMaxLeftRightCutBool != CutBool.BOUND_EXCEEDED) {
						useNewCut = true;
						System.out.printf("below graph bound, depth: %d\n", depth);
					}
				} else {
					useNewCut = valuecmp.compare(split, newsplit) <= 0;
					if (useNewCut == false) {
						long newMaxLeftRightCutBool =
							this.cmp.maxLeftRightCutBool(newsplit, getGraphBoolwidthUpperBound());
						if (newMaxLeftRightCutBool != CutBool.BOUND_EXCEEDED) {
							//System.out.printf("below old cut, depth: %d\n", depth);
							useNewCut = true;
						}
					}
				}
			}
		} else {
			long newMaxLeftRightCutBool =
				this.cmp.maxLeftRightCutBool(newsplit, getGraphBoolwidthUpperBound());
			useNewCut = (newMaxLeftRightCutBool != CutBool.BOUND_EXCEEDED);
		}
		return useNewCut;
	}

	public Result localSearch(IGraph<Vertex<V>, V, E> g, IDecomposition<?, ?, ?> oldBestDecomposition) {

		// initialize
		this.decomposition = new LSDecomposition.D<>(g);

        initCutBoolComparator();
        if (useInitDegreeHeuristic) {
            this.greedyDegree = new GreedyDegree<>(this.decomposition, cmp, useInitGreedyExitEarly);
        }
        else if (useInitGreedy) {
            this.greedy = new Greedy<>(this.decomposition, cmp, useInitGreedyExitEarly);
        }

		rnd = new Random();

        this.rootset = new PosSet<Vertex<V>>(this.decomposition.root().element());
		if (oldBestDecomposition != null) {
			long oldbw = BooleanDecomposition.getBoolWidth(oldBestDecomposition);
			setGraphBoolwidthUpperBound(oldbw);
		} else {
			setGraphBoolwidthUpperBound(CutBool.bestGeneralUpperBound(
					this.decomposition.numGraphVertices(), false));
		}

		Result result = new Result();
		long start = System.currentTimeMillis();
        long oldPrintTime = start - SEARCH_PRINT_INTERVAL;

		for (int i = 0; System.currentTimeMillis() - start < SEARCH_TIME; i++) {
			// greedy = (i % 20 != 0);
			tryToImproveSubTree(this.decomposition.root(), INNER_SEARCHSTEPS, 1, 0);
			assert this.decomposition.root().size() == this.decomposition.numGraphVertices();

			if (updateGraphBoolwidthUpperBound(this.decomposition.root()
					.getSubTreeUpperBound())) {
				System.out.println("iteration:" + i);
				this.has_valid_best_decomposition = true;
			}
			if (System.currentTimeMillis() - oldPrintTime > SEARCH_PRINT_INTERVAL) {
                oldPrintTime = System.currentTimeMillis();
				System.out.printf("{ \"search time\": %d, \"cut fails\": %d, \"cut tries\": %d, \"iteration\": %d, \"foundsplits\": %d, \"UB\": %d, \"log2UB\": %.2f }\n",
                        oldPrintTime - start,
                        this.failsToImproveCut, this.triesToImproveCut,
                        i,
						this.foundsplits.size(),
                        getGraphBoolwidthUpperBound(),
                        Math.log(getGraphBoolwidthUpperBound()) / Math.log(2));
			}
		}
		result.success = true;

		// present
		this.decomposition.root().buildDecomposition(this);
		this.decomposition.fixParents();
		result.decomposition = this.decomposition;
		return result;
	}


	public void setGraphBoolwidthUpperBound(long graph_bw_upper_bound) {
		this.graph_boolwidth_upper_bound = graph_bw_upper_bound;
	}

	/**
	 * Makes a new split from split and if the new cut was better, it returns
	 * the new cut, else it returns the old cut.
	 * 
	 * @param split
	 * @param valuecmp
	 * @param lower_bound
	 * @return
	 */
	public ArrayList<VertexSplit<V>> tryToImproveCut(VertexSplit<V> split,
			Comparator<VertexSplit<V>> valuecmp, long lower_bound, int depth) {

		int fromleft = rnd.nextInt(SwapConstraints.maxFromLeft(split) + 1);
		int fromright = rnd.nextInt(SwapConstraints.maxFromRight(split) + 1);

		if (fromleft == 0 && fromright == 0) {
			fromleft = 1;
			fromright = 1;
		}

		ArrayList<VertexSplit<V>> newsplits = new ArrayList<VertexSplit<V>>();
		newsplits.add(this.decomposition.swapRandomNodes(split, fromleft,
				fromright));

		// completely re-randomize
		if (!this.localSwaps) {
			int newleftsize = split.size() / 2;
			newleftsize = Math.max(newleftsize, SwapConstraints
					.minSplitSize(split));
			newsplits.add(this.decomposition.splitRandomNew(split, newleftsize));
		}

		for (int i = 0; i < newsplits.size(); i++) {
			VertexSplit<V> newsplit = newsplits.get(i);

			newsplit.getCached(this.rootset, this.foundsplits);

			if (keepCut(split, newsplit, depth)) {
				split.setCached(this.rootset, this.foundsplits);
				newsplit.setCached(this.rootset, this.foundsplits);

				split = newsplit;
			} else {
				newsplits.remove(i);
				i--;
			}
		}

		return newsplits;
	}

	/**
	 * 
	 * @param split
	 * @param searchsteps
	 *            how much work to do
	 * @param decomposition_bw_lower_bound
	 * @return subtree upper bound
	 */

	protected void tryToImproveSubTree(VertexSplit<V> split, int searchsteps,
			long decomposition_bw_lower_bound, int depth) {

		if (split.size() == 1) {
			this.cmp.getCutBool(split); // will be cached
			split.updateSubTreeUpperBound();
			return;
		}

		// initializes the memoized cutbool
		this.cmp.getCutBool(split);
		if (split.isOptimalSubTree()
				|| split.getSubTreeUpperBound() < decomposition_bw_lower_bound) {
			// System.out.println("OPTIMUS PRIME :) !");
			return;
		}

		// if necessary
		if (!split.initialized) {
			initCut(split, depth);
			split.activeCuts.add(split.clone());
		}

		long next_lower_bound = Math.max(decomposition_bw_lower_bound, this.cmp
				.getCutBool(split));

		for (int i = 0; i < searchsteps; i++) {
			split.steps++;

			this.cmp.setUpperBound(decomposition_bw_lower_bound);

			ArrayList<VertexSplit<V>> newsplits = tryToImproveCut(split, this.cmp,
					getGraphBoolwidthUpperBound(), depth);
			this.triesToImproveCut++;

			if (newsplits.isEmpty()) {
				this.failsToImproveCut++;
			}

			// update current cut if the new one is different
			for (int j = 0; j < newsplits.size(); j++) {
				VertexSplit<V> newsplit = newsplits.get(j);
				if (split.getLeft() != newsplit.getLeft()
						&& split.getLeft() != newsplit.getRight()) {

					// for downward search above bound to work
					split.setLeft(newsplit.getLeft());
					split.setRight(newsplit.getRight());

				} else {
					//assert false;
				}
			}

			// this cut may be closer to our goal, but won't improve the
			// graph_boolwidth_upper_bound so there's no use splitting it
			// further
			if (CutBoolComparator.maxLeftRightCutBool(this.decomposition, split) >= getGraphBoolwidthUpperBound()) {
				continue;
			}

			// choose who goes first. probably doesn't matter? select by size?
			VertexSplit<V> first;
			VertexSplit<V> second;
			if (rnd.nextBoolean()) {
				first = split.getRight();
				second = split.getLeft();
			} else {
				first = split.getLeft();
				second = split.getRight();
			}

			tryToImproveSubTree(first, searchsteps, next_lower_bound, depth + 1);
			// it's the max of all computed cuts so far on this particular decomposition
			long lower_bound_for_other_side = next_lower_bound;
			lower_bound_for_other_side = Math.max(lower_bound_for_other_side,
					first.getSubTreeUpperBound());
			tryToImproveSubTree(second, searchsteps, lower_bound_for_other_side, depth + 1);
			split.updateSubTreeUpperBound();

			// if we can't improve further we're done with this cut.
			// if we're not the bottleneck, postpone splitting it until a later time.
			if (split.isOptimalSubTree() || split.getSubTreeUpperBound() < decomposition_bw_lower_bound) {
				break;
			}
		}
	}

	public boolean updateGraphBoolwidthUpperBound(long graph_boolw_upper_bound) {
		boolean updated = false;
		if (graph_boolw_upper_bound < this.graph_boolwidth_upper_bound) {
            long time = System.currentTimeMillis();
			System.out.printf("{ \"time\": %d, \"graph\": \"%s\", \"oldCB\": %d, \"oldBW\": %.2f, \"newCB\": %d, \"newBW\": %.2f }\n",
                    time,
                    this.graphName,
					this.graph_boolwidth_upper_bound,
                    Math.log(this.graph_boolwidth_upper_bound) / Math.log(2),
                    graph_boolw_upper_bound,
                    Math.log(graph_boolw_upper_bound) / Math.log(2));
			this.graph_boolwidth_upper_bound = graph_boolw_upper_bound;
			updated = true;
		}
		return updated;
	}
}