package boolwidth.heuristics;

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

	// static search state
	private CutBoolComparator<V, E> cmp;

	private static Random rnd;

	// dynamic search state
	private boolean has_valid_best_decomposition = false;
	private LSDecomposition.D<V, E> decomposition;
	private long graph_boolwidth_upper_bound = CutBool.BOUND_UNINITIALIZED;
	TreeMap<PosSubSet<Vertex<V>>, VertexSplit<V>> foundsplits = new TreeMap<PosSubSet<Vertex<V>>, VertexSplit<V>>();

	// === Algorithm configuration parameters

	// do local search if true, re-randomize cuts completely if not
	private final boolean localSwaps = true;

	// set one side full, other empty, and move node by node greedy until the cut is balanced
	private final boolean useInitGreedy = true;
	private final boolean useInitGreedyExitEarly = false;

	// use lists of active cuts to investigate instead of just one
	private final boolean useActives = false;
	// use size of list for inner search steps
	private final boolean useActivesInner = false;

	// accept any new cut that is below the graph upper bound,
	// instead of just cuts smaller than current
	private final boolean allowEverythingBelowGraphUpperBound = false;

	// === End config

	private PosSet<Vertex<V>> rootset;

	public static final int SEARCHSTEPS = 5000;

	// search time in seconds
	public static final int SEARCHTIME = 10;

	public static final int INNER_SEARCHSTEPS = 1;

	// stats
	public int failsToImproveCut = 0;
	public int triesToImproveCut = 0;

	public class Result {
		public LSDecomposition.D<V, E> decomposition;
		public boolean success = false;
	}

	public boolean addActiveIfGood(VertexSplit<V> parent, VertexSplit<V> active, int depth) {
		boolean ret = false;
		if (this.useActives &&
				SwapConstraints.isValid(active) && beatsGraphUpperBound(active)) {
			ret = parent.addActiveIfGood(active);
			if (ret && parent.activeCuts.size() % 100 == 0) {
				System.out.printf("depth: %d, actives: %d\n", depth, parent.activeCuts.size());
			}
		}
		return ret;
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

		// split randomly at first
		if (this.useInitGreedy) {
			split.copyChildren(initGreedy(split, depth == 0));
		} else {
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

	// TODO: use graph upper bound
	public VertexSplit<V> initGreedy(VertexSplit<V> bag, boolean toplevel) {
		ArrayList<VertexSplit<V>> newSplit = new ArrayList<VertexSplit<V>>(1);
		newSplit.add(null);

		long[] minNewBoolwidth = { Long.MAX_VALUE };

		boolean allover = this.useActives;

		if (allover || toplevel) {
			bag.setLeft(this.decomposition.createVertex(bag.vertices(), this.decomposition.getNextID()));
			bag.setRight(
					this.decomposition.createVertex(
							new PosSubSet<Vertex<V>>(bag.vertices().getSet()), this.decomposition.getNextID()));
		} else {
			this.decomposition.splitRandom(bag, (bag.size() + 1) / 2);
			//			System.out.printf("helo!?: %d+%d=%d, %s",
			//					bag.getLeft().size(), bag.getRight().size(),
			//					bag.size(), bag);
			assert bag.checkNode() : String.format("BAG=\"%s\"\n", bag.toString());
		}
		//System.out.println(bag);

		VertexSplit<V> swapSplit = bag;

		assert bag.getLeft().size() > 0;

		for (int i = 0; i < bag.getLeft().size(); i++) {
			//			System.out.printf("%d: %s\n",
			//					this.cmp.maxLeftRightCutBool(swapSplit, minNewBoolwidth), swapSplit);
			if (initGreedyCheck(bag, swapSplit, newSplit, minNewBoolwidth)) {
				// TODO: don't need to exit early if we use fast approximation
				if (this.useInitGreedyExitEarly) {
					return newSplit.get(0);
				}
			}
            System.out.printf("Greedy init: %d/%d\n", i, bag.getLeft().size());
			swapSplit = swapGreedyLeft(swapSplit);
		}
		initGreedyCheck(bag, swapSplit, newSplit, minNewBoolwidth);

		assert newSplit.get(0) != null;

		//		System.out.printf("RET: %d: %s\n", this.cmp.maxLeftRightCutBool(newSplit), newSplit);
		return newSplit.get(0);
	}

	/**
	 * Check if we want to use this cut
	 * @param bag
	 * @param swapSplit
	 * @return
	 */
	public boolean initGreedyCheck(VertexSplit<V> bag, VertexSplit<V> swapSplit,
			ArrayList<VertexSplit<V>> newSplit, long[] minNewBoolwidth) {
		if (SwapConstraints.isValid(swapSplit)) {
			addActiveIfGood(bag, swapSplit.clone(), -1);
			long boolwidth = this.cmp.maxLeftRightCutBool(swapSplit, minNewBoolwidth[0]);
			//int boolwidth = this.cmp.maxLeftRightCutBool(swapSplit);
			if (boolwidth != CutBool.BOUND_EXCEEDED) {
				if (boolwidth < minNewBoolwidth[0]) {
					newSplit.set(0, swapSplit);
					minNewBoolwidth[0] = boolwidth;
					return true;
				}
			}
		}
		return false;
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

	public Result localSearch(IGraph<Vertex<V>, V, E> g,
			IDecomposition<?, ?, ?> oldBestDecomposition) {

		// initialize
		this.decomposition = new LSDecomposition.D<V, E>(g);
		//this.cmp = new CutBoolComparator<V, E>(this.decomposition, new RandomHeuristic<V, E>());
        //this.cmp = new CutBoolComparatorApprox<V, E>(this.decomposition, new RandomHeuristic<V, E>());
        this.cmp = new CutBoolComparatorCCMIS<V, E>(this.decomposition, new RandomHeuristic<V, E>());
		rnd = new Random();

        this.rootset = new PosSet<Vertex<V>>(this.decomposition.root().element());
		if (oldBestDecomposition != null) {
			int oldbw = BooleanDecomposition.getBoolWidth(oldBestDecomposition);
			setGraphBoolwidthUpperBound(oldbw);
		} else {
			setGraphBoolwidthUpperBound(CutBool.bestGeneralUpperBound(
					this.decomposition.numGraphVertices(), false));
		}
		//setGraphBoolwidthUpperBound(100);
		Result result = new Result();

		//initGreedy(this.decomposition.root());

		//System.exit(1);

		// compute
		//for (int i = 0; i < SEARCHSTEPS; i++) {

		long start = System.currentTimeMillis();

		for (int i = 0; System.currentTimeMillis() - start < SEARCHTIME * 1000; i++) {
			// greedy = (i % 20 != 0);
			tryToImproveSubTree(this.decomposition.root(), INNER_SEARCHSTEPS, 1, 0);
			assert this.decomposition.root().size() == this.decomposition
			.numGraphVertices();
			// System.out.printf("i: %d, root cut: %d, steps: %d/%d\n", i, cmp
			// .maxLeftRightCutBool(decomposition.root()), decomposition
			// .root().getLeft().steps,
			// decomposition.root().getRight().steps);
			// System.out.printf("foundsplits: %d\n", foundsplits.size());

			//this.allowEverythingBelowGraphUpperBound = i > SEARCHSTEPS / 2;

			if (updateGraphBoolwidthUpperBound(this.decomposition.root()
					.getSubTreeUpperBound())) {
				// decomposition.root().buildDecomposition(this);
				// decomposition.fixParents();
				// assert CutBool.booleanWidth(decomposition,
				// decomposition.root()
				// .getSubTreeUpperBound()) == decomposition.root()
				// .getSubTreeUpperBound();
				System.out.println("iteration:" + i);
				this.has_valid_best_decomposition = true;
			}
			if (i % 10 == 0) {
				System.out.printf("iteration: %d, foundsplits: %d, UB: %d, log2UB: %.2f\n", i,
						this.foundsplits.size(),
                        getGraphBoolwidthUpperBound(),
                        Math.log(getGraphBoolwidthUpperBound()) / Math.log(2));
				System.out.printf("cut fails/tries: %d/%d\n", this.failsToImproveCut, this.triesToImproveCut);
			}
			// ArrayList<VertexSplit<V>> goodstuff = new
			// ArrayList<VertexSplit<V>>();
			// for (Iterator<Map.Entry<PosSubSet<Vertex<V>>, VertexSplit<V>>> it
			// =
			// foundsplits
			// .entrySet().iterator(); it.hasNext();) {
			// Map.Entry<PosSubSet<Vertex<V>>, VertexSplit<V>> e = it.next();
			// if (!e.getValue().hasCutBool()
			// || e.getValue().getCutBool() > getGraphBoolwidthUpperBound()) {
			// it.remove();
			// } else {
			// goodstuff.add(e.getValue());
			// }
			// }
			// for (VertexSplit<V> goodone : goodstuff) {
			// localSearch(goodone, 1, 1);
			// }
			/*
			 * System.out.printf("BW: %d\n", decomposition.root()
			 * .getSubTreeUpperBound());
			 */
		}
		result.success = true;

		// for (VertexSplit<V> v : foundsplits.values()) {
		// if (v.size() < decomposition.numGraphVertices() / 3) {
		// continue;
		// }
		// int ml = -1;
		// try {
		// ml = cmp.maxLeftRightCutBool(v);
		// } catch (Exception e) {
		//
		// }
		// System.out.printf("%d, %d: %s\n", cmp.getCutBool(v), ml, v);
		// }

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
	 * Try all 1-node swaps and pick best one
	 * @param bag
	 * @return
	 */
	public VertexSplit<V> swapGreedyLeft(VertexSplit<V> bag) {
		VertexSplit<V> newsplit = null;
		long minNewBoolwidth = Long.MAX_VALUE;

		assert bag.getLeft().size() > 0;
		for (Vertex<V> v : bag.getLeft().vertices()) {
			ArrayList<Vertex<V>> toswap = new ArrayList<Vertex<V>>(1);
			toswap.add(v);
			VertexSplit<V> swapSplit = this.decomposition.swapNodes(bag, toswap, null);

			// experimental
			addActiveIfGood(bag, swapSplit.clone(), -1);

			long boolwidth = this.cmp.maxLeftRightCutBool(swapSplit, minNewBoolwidth);
			if (boolwidth != CutBool.BOUND_EXCEEDED) {
				if (boolwidth < minNewBoolwidth) {
					newsplit = swapSplit;
					minNewBoolwidth = boolwidth;
				} else if (newsplit == null) {
					assert false;
				}
			}
		}
		assert newsplit != null;
		assert newsplit.checkNode();
		return newsplit;
	}

	/**
	 * Try all 1-node swaps and pick best one
	 * @param bag
	 * @return
	 */
	public VertexSplit<V> swapGreedyRight(VertexSplit<V> bag) {
		VertexSplit<V> newsplit = null;
		long minNewBoolwidth = Long.MAX_VALUE;

		assert bag.getRight().size() > 0;
		for (Vertex<V> v : bag.getRight().vertices()) {
			ArrayList<Vertex<V>> toswap = new ArrayList<Vertex<V>>(1);
			toswap.add(v);
			VertexSplit<V> swapSplit = this.decomposition.swapNodes(bag, null, toswap);
			long boolwidth = this.cmp.maxLeftRightCutBool(swapSplit, minNewBoolwidth);
			if (boolwidth != CutBool.BOUND_EXCEEDED) {
				if (boolwidth < minNewBoolwidth) {
					newsplit = swapSplit;
					minNewBoolwidth = boolwidth;
				} else if (newsplit == null) {
					assert false;
				}
			}
		}
		return newsplit;
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

		// System.out.printf("initial: cutbool=%d, left=%s, right=%s\n", cmp
		// .getCutBool(split), split.getLeft(), split.getRight());

		int fromleft = rnd.nextInt(SwapConstraints.maxFromLeft(split) + 1);
		int fromright = rnd.nextInt(SwapConstraints.maxFromRight(split) + 1);
		//		int maxfrom = Math.min(split.getLeft().size() - fromleft,
		//				split.getRight().size() - fromright);
		//		int addboth = rnd.nextInt(maxfrom + 1);
		//		fromleft += addboth;
		//		fromright += addboth;

		// fromleft = rnd.nextInt(Math.min(SwapConstraints.maxFromLeft(split),
		// 1) + 1);
		// fromright = rnd.nextInt(Math.min(SwapConstraints.maxFromRight(split),
		// 1) + 1);
		if (fromleft == 0 && fromright == 0) {
			fromleft = 1;
			fromright = 1;
		}
		//		fromleft = 1;
		//		fromright = 1;
		// System.out.printf("from: %d/%d\n", fromleft, fromright);

		// VertexSplit<V> newsplit = swapNodes(split);
		ArrayList<VertexSplit<V>> newsplits = new ArrayList<VertexSplit<V>>();
		newsplits.add(this.decomposition.swapRandomNodes(split, fromleft,
				fromright));

		//this.cmp.compare(newsplit2, newsplit) <=

		// completely re-randomize
		if (!this.localSwaps) {
			int newleftsize = rnd.nextInt(split.size() / 2 - split.size() / 3
					+ 1)
					+ split.size() / 3;
			newleftsize = split.size() / 2;
			newleftsize = Math.max(newleftsize, SwapConstraints
					.minSplitSize(split));
			newsplits.add(this.decomposition.splitRandomNew(split, newleftsize));
		}

		// System.out.println(newbg.leftVertices());
		// CutBool.countNeighborhoods(newbg, cutbool);

		for (int i = 0; i < newsplits.size(); i++) {
			VertexSplit<V> newsplit = newsplits.get(i);

			newsplit.getCached(this.rootset, this.foundsplits);

			if (keepCut(split, newsplit, depth)) {
				// System.out.printf("lb: %d, old->new cb=%d->%d sz=%d/%d -> %d/%d\n",
				// lower_bound, cmp.maxLeftRightCutBool(split), cmp
				// .maxLeftRightCutBool(newsplit), split.getLeft()
				// .size(), split.getRight().size(), newsplit
				// .getLeft().size(), newsplit.getRight().size());
				// TODO: put back assertion
				// assert cmp.maxLeftRightCutBool(newsplit) <= cmp
				// .maxLeftRightCutBool(split);
				// System.out.printf("%s -> %s\n", split, newsplit);

				split.setCached(this.rootset, this.foundsplits);
				newsplit.setCached(this.rootset, this.foundsplits);
				//			if (this.cmp.maxLeftRightCutBool(newsplit) < getGraphBoolwidthUpperBound()) {
				//				newsplit.setCached(this.rootset, this.foundsplits);
				//			}
				//split.setCached(this.rootset, this.foundsplits);

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

		//for (int i = 0; i < Math.max(searchsteps, split.activeCuts.size()); i++) {
		int beforesize;
		if (this.useActivesInner) {
			beforesize = split.activeCuts.size();
		} else {
			beforesize = searchsteps;
		}
		for (int i = 0; i < beforesize; i++) {
			split.steps++;

			// System.out.printf("outer loop: %d\n", i);
			// System.out.printf("searchsteps: %d\n", searchsteps);
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
					if (!this.useActives || newsplit.activeCuts.size() <= 1) {
						split.setLeft(newsplit.getLeft());
						split.setRight(newsplit.getRight());
					}

					// add to actives
					if (addActiveIfGood(split, newsplit, depth)) {
						// try to improve even more
						//							if (newsplit.getLeft().size() > SwapConstraints.minSplitSize(newsplit)) {
						//								VertexSplit<V> newsplit2 = swapGreedyLeft(newsplit);
						//								if (this.cmp.maxLeftRightCutBool(newsplit2,
						//										getGraphBoolwidthUpperBound()) == CutBool.BOUND_EXCEEDED &&
						//										!split.activeCuts.contains(newsplit2)) {
						//									split.activeCuts.add(newsplit2);
						//								}
						//							}
						//							if (newsplit.getRight().size() > SwapConstraints.minSplitSize(newsplit)) {
						//								VertexSplit<V> newsplit2 = swapGreedyRight(newsplit);
						//								if (this.cmp.maxLeftRightCutBool(newsplit2,
						//										getGraphBoolwidthUpperBound()) == CutBool.BOUND_EXCEEDED &&
						//										!split.activeCuts.contains(newsplit2)) {
						//									split.activeCuts.add(newsplit2);
						//								}
						//							}
					}
				} else {
					//assert false;
				}
			}

			// switch between actives below the bound
			if (this.useActives &&
					this.cmp.maxLeftRightCutBool(split, getGraphBoolwidthUpperBound())
					!= CutBool.BOUND_EXCEEDED) {
				VertexSplit<V> newsplit = null;
				while (newsplit == null && split.activeCuts.size() > 1) {
					int oldActive = split.activeCut;
					split.activeCut = (split.activeCut + 1) % split.activeCuts.size();
					newsplit = split.activeCuts.get(split.activeCut);
					if (this.cmp.maxLeftRightCutBool(newsplit, getGraphBoolwidthUpperBound())
							== CutBool.BOUND_EXCEEDED
					) {
						split.activeCuts.remove(split.activeCut);
						split.activeCut = oldActive;
						newsplit = null;
						System.out.println("removed obsolete cut");
					} else {
						//						System.out.printf("switching to %d < bound(%d): %s\n",
						//								this.cmp.maxLeftRightCutBool(newsplit),
						//								getGraphBoolwidthUpperBound(),
						//								newsplit);
					}
				}
				if (newsplit != null) {
					split.setLeft(newsplit.getLeft());
					split.setRight(newsplit.getRight());
					split.updateCached(this.rootset, this.foundsplits);
				}
			}

			// this cut may be closer to our goal, but won't improve the
			// graph_boolwidth_upper_bound so there's no use splitting it
			// further
			if (CutBoolComparator
					.maxLeftRightCutBool(this.decomposition, split) >= getGraphBoolwidthUpperBound()) {
				// split.updateSubTreeUpperBound(CutBool.bestGeneralUpperBound(split.size(),
				// false));
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
			// it's the max of all computed cuts so far on this particular
			// decomposition
			long lower_bound_for_other_side = next_lower_bound;
			lower_bound_for_other_side = Math.max(lower_bound_for_other_side,
					first.getSubTreeUpperBound());
			tryToImproveSubTree(second, searchsteps, lower_bound_for_other_side, depth + 1);
			if (split.updateSubTreeUpperBound()) {
				// System.out.println("jolly good!");
			}

			// if we can't improve further we're done with this cut.
			// if we're not the bottleneck, postpone splitting it until a later
			// time.
			if (split.isOptimalSubTree()
					|| split.getSubTreeUpperBound() < decomposition_bw_lower_bound) {
				// System.out.println("OPTIMUS PRIME break dance :) !");
				break;
			}
		}
		// if (split.updateSubTreeUpperBound()) {
		// //System.out.println("jolly good!");
		// }
	}

	public boolean updateGraphBoolwidthUpperBound(long graph_boolw_upper_bound) {
		boolean updated = false;
		if (graph_boolw_upper_bound < this.graph_boolwidth_upper_bound) {
			System.out.printf("new bw: %d -> %d\n",
					this.graph_boolwidth_upper_bound, graph_boolw_upper_bound);
			this.graph_boolwidth_upper_bound = graph_boolw_upper_bound;
			updated = true;
		}
		return updated;
	}
}