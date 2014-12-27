package boolwidth.cutbool;

import boolwidth.CutBool;
import boolwidth.heuristics.LSDecomposition;
import boolwidth.heuristics.VertexSplit;
import graph.BiGraph;
import graph.Edge;
import graph.Vertex;
import interfaces.IDecomposition;
import sadiasrc.decomposition.CCMIS;
import sadiasrc.decomposition.CCMISDynamicForest;

import java.util.Comparator;

public class CutBoolComparatorCCMIS<V, E>  extends CutBoolComparator<V, E> implements Comparator<VertexSplit<V>> {

	private final LSDecomposition<V, E> decomposition;

	private long upper_bound;

    public static <V, E> sadiasrc.graph.BiGraph convertSadiaBiGraph(BiGraph<V, E> bigraph) {
        //long before = System.currentTimeMillis();

        sadiasrc.graph.BiGraph sadiaBiGraph = new sadiasrc.graph.BiGraph(bigraph.numLeftVertices(), bigraph.numRightVertices());

        for (Edge<Vertex<V>, V, E> e : bigraph.edges()) {
            int id1 = e.endVertices().get(0).id();
            int id2 =  e.endVertices().get(1).id();
            sadiaBiGraph.insertEdge(id1, id2);
        }

        //long after = System.currentTimeMillis();
        //System.out.printf("convertSadiaBiGraph time: %d\n", after - before);

        return sadiaBiGraph;
    }

	public static <V, E> long getCutBool(IDecomposition<?, V, E> decomposition,
			VertexSplit<V> node) {
		return getCutBool(decomposition, node, CutBool.BOUND_UNINITIALIZED);
	}

	public static <V, E> long getCutBool(IDecomposition<?, V, E> decomposition,
			VertexSplit<V> node, long upper_bound) {
		if (node.hasCutBool()) {
			return node.getCutBool();
		} else {
			BiGraph<V, E> cut = decomposition.getCut(node);

            long cb;

            /*
            int CUTOFF = 16;
            if (cut.numLeftVertices() < CUTOFF || cut.numRightVertices() < CUTOFF) {
                cb = CutBool.countNeighborhoods(cut, upper_bound);
                //System.out.printf("cut: %d/%d, cb: %d\n", cut.numLeftVertices(), cut.numRightVertices(), cb);
            } else {
                cb = CCMIS.BoolDimBranch(convertSadiaBiGraph(cut));
            }*/

            long before, after;
            //cb = CCMIS.BoolDimBranch(convertSadiaBiGraph(cut));
			cb = CCMISDynamicForest.BoolDimBranch(convertSadiaBiGraph(cut));
            /*
            long UPPER_BOUND = 1000000; // should be a runtime (ms) perhaps
            if (upper_bound < UPPER_BOUND) {
                cb = CutBool.countNeighborhoods(cut, upper_bound);
            } else {
                before = System.currentTimeMillis();
                cb = CutBool.countNeighborhoods(cut, UPPER_BOUND);
                after = System.currentTimeMillis();

                long unnDuration = after - before;

                if (cb == CutBool.BOUND_EXCEEDED) {
                    before = System.currentTimeMillis();
                    cb = CCMIS.BoolDimBranch(convertSadiaBiGraph(cut));
                    after = System.currentTimeMillis();
                    long ccmisDuration = after - before;
                    //System.out.printf("UNN time (v=%d/%d,CB=%d): %d\n",
                    //        cut.numLeftVertices(), cut.numRightVertices(), cb, after - before);
                    //System.out.printf("UNN time - CCMIS time (v=%d/%d,CB=%d): %d - %d\n",
                    //        cut.numLeftVertices(), cut.numRightVertices(), cb, unnDuration, ccmisDuration);
                }
            }*/

            //cb = CCMIS.BoolDimBranch(convertSadiaBiGraph(cut));







            //int cb = (int) MISBackTrack.countNeighborhoods(cut);
            if (cb == CutBool.BOUND_EXCEEDED) {
				node.setCutBoolLowerBound(cb);
			} else {
				node.setCutBool(cb);
			}
			return cb;
		}
	}

	public static <V, E> long maxLeftRightCutBool(
			IDecomposition<?, V, E> decomposition, VertexSplit<V> node) {
		return maxLeftRightCutBool(decomposition, node,
				CutBool.BOUND_UNINITIALIZED);
	}

	public static <V, E> long maxLeftRightCutBool(
			IDecomposition<?, V, E> decomposition, VertexSplit<V> node,
			long upper_bound) {
		long retval = 0;

		if (node.getLeft().size() + node.getRight().size() == decomposition
				.numGraphVertices()) {
			retval = getCutBool(decomposition, node.getLeft(), upper_bound);
		} else {
			long leftcutbool = getCutBool(decomposition, node.getLeft(),
					upper_bound);
			long rightcutbool = getCutBool(decomposition, node.getRight(),
					upper_bound);
			if (leftcutbool == CutBool.BOUND_EXCEEDED
					|| rightcutbool == CutBool.BOUND_EXCEEDED) {
				retval = CutBool.BOUND_EXCEEDED;
			} else {
				retval = Math.max(leftcutbool, rightcutbool);
			}
		}
		// this can happen if cutbool was cached, bound is not used then
		// TODO: change getCutbool instead?
		if (upper_bound != CutBool.BOUND_UNINITIALIZED && retval != CutBool.BOUND_EXCEEDED &&
				retval > upper_bound) {
			retval = CutBool.BOUND_EXCEEDED;
		}
		return retval;
	}

	public CutBoolComparatorCCMIS(LSDecomposition<V, E> decomposition) {
        super(decomposition);
        this.decomposition = decomposition;
	}

	/**
	 * Returns 1 if o1 is better than o2 0 if they are equal and -1 if o2 is
	 * better than o1.
	 * 
	 * o1 is better than o2 if cutvalue(o1) < cutvalue(o2)
	 * 
	 * The upper_bound is not used by this method because we want to make local
	 * progress even when we're beyond the bound
	 */
	@Override
	public int compare(VertexSplit<V> o1, VertexSplit<V> o2) {
		final int O1_LESS_THAN_O2 = 1;
		final int O2_LESS_THAN_O1 = -1;
		final int EQUAL = 0;

		// TODO: use lower bounds
		long cb1 = maxLeftRightCutBool(this.decomposition, o1);
		long cb2 = maxLeftRightCutBool(this.decomposition, o2, cb1);
		if (cb2 == CutBool.BOUND_EXCEEDED) {
			// System.out.println("bound hit");
			return O1_LESS_THAN_O2;
		}

		assert cb1 != VertexSplit.CUTBOOL_INITVAL;
		assert cb2 != VertexSplit.CUTBOOL_INITVAL;
		assert cb2 != CutBool.BOUND_EXCEEDED;
		assert cb2 != CutBool.BOUND_EXCEEDED;

		if (cb1 == cb2) {
			// TODO: factor in time spent
			// if (o1.subcuts_upper_bound == VertexSplit.SUBCUTS_INITVAL) {
			// o1.subcuts_upper_bound = pdheuristic.runHeuristic(
			// decomposition, o1, CutBool.BOUND_UNINITIALIZED);
			// }
			// int ubound1 = o1.subcuts_upper_bound;
			// if (o2.subcuts_upper_bound == VertexSplit.SUBCUTS_INITVAL) {
			// o2.subcuts_upper_bound = pdheuristic.runHeuristic(
			// decomposition, o2, ubound1);
			// }
			// int ubound2 = o2.subcuts_upper_bound;
			// if (ubound1 == ubound2) {
			// return 0;
			// } else if (ubound1 < ubound2) {
			// return 1;
			// } else {
			// return -1;
			// }
			return EQUAL;
		} else if (cb1 < cb2) {
			return O1_LESS_THAN_O2;
		} else {
			return O2_LESS_THAN_O1;
		}
	}

	public long getCutBool(VertexSplit<V> node) {
		return getCutBool(this.decomposition, node);
	}

	public long getUpperBound() {
		return this.upper_bound;
	}

	public long maxLeftRightCutBool(VertexSplit<V> node) {
		return maxLeftRightCutBool(this.decomposition, node);
	}

	public long maxLeftRightCutBool(VertexSplit<V> node, long upper_bound) {
		return maxLeftRightCutBool(this.decomposition, node, upper_bound);
	}


	public void setUpperBound(long upper_bound) {
		this.upper_bound = upper_bound;
	}
}
