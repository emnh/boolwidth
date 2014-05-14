package boolwidth.heuristics;

import boolwidth.heuristics.cutbool.CBBacktrackEstimate;
import graph.BiGraph;
import interfaces.IDecomposition;

import java.util.Comparator;

import boolwidth.CutBool;

public class CutBoolComparatorApprox<V, E> extends CutBoolComparator<V, E> implements Comparator<VertexSplit<V>>  {

    private final LSDecomposition<V, E> decomposition;

    private int upper_bound;

    private static int sampleCount = 10;

    public static <V, E> int getCutBool(IDecomposition<?, V, E> decomposition,
                                        VertexSplit<V> node) {
        return getCutBool(decomposition, node, CutBool.BOUND_UNINITIALIZED);
    }

    public static <V, E> int getCutBool(IDecomposition<?, V, E> decomposition,
                                        VertexSplit<V> node, int upper_bound) {
        if (node.hasCutBool()) {
            return node.getCutBool();
        } else {
            BiGraph<V, E> cut = decomposition.getCut(node);

            // TODO: cannot get BOUND_EXCEEDED from estimator, simplify code?
            // TODO: can return long, should refactor whole chain to use long, or make a type for it
            int cb = (int) CBBacktrackEstimate.estimateNeighborhoods(cut, sampleCount);

            // we can afford exact
            if (cb < 100000) {
                //System.out.printf("switching to exact, cb: %d\n", cb);
                cb = CutBool.countNeighborhoods(cut, upper_bound);
            }

            if (cb == CutBool.BOUND_EXCEEDED) {
                node.setCutBoolLowerBound(cb);
            } else {
                node.setCutBool(cb);
            }
            return cb;
        }
    }

    public static <V, E> int maxLeftRightCutBool(
            IDecomposition<?, V, E> decomposition, VertexSplit<V> node) {
        return maxLeftRightCutBool(decomposition, node,
                CutBool.BOUND_UNINITIALIZED);
    }

    public static <V, E> int maxLeftRightCutBool(
            IDecomposition<?, V, E> decomposition, VertexSplit<V> node,
            int upper_bound) {
        int retval = 0;

        if (node.getLeft().size() + node.getRight().size() == decomposition
                .numGraphVertices()) {
            retval = getCutBool(decomposition, node.getLeft(), upper_bound);
        } else {
            int leftcutbool = getCutBool(decomposition, node.getLeft(),
                    upper_bound);
            int rightcutbool = getCutBool(decomposition, node.getRight(),
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

    // private PartialDecompositionHeuristic<V, E> pdheuristic;

    public CutBoolComparatorApprox(LSDecomposition<V, E> decomposition,
                             PartialDecompositionHeuristic<V, E> pdheuristic) {
        super(decomposition, pdheuristic);
        this.decomposition = decomposition;
        // this.pdheuristic = pdheuristic;
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
        int cb1 = maxLeftRightCutBool(this.decomposition, o1);
        int cb2 = maxLeftRightCutBool(this.decomposition, o2, cb1);
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

    public int getCutBool(VertexSplit<V> node) {
        return getCutBool(this.decomposition, node);
    }

    public int getUpperBound() {
        return this.upper_bound;
    }

    public int maxLeftRightCutBool(VertexSplit<V> node) {
        return maxLeftRightCutBool(this.decomposition, node);
    }

    public int maxLeftRightCutBool(VertexSplit<V> node, int upper_bound) {
        return maxLeftRightCutBool(this.decomposition, node, upper_bound);
    }


    public void setUpperBound(int upper_bound) {
        this.upper_bound = upper_bound;
    }
}
