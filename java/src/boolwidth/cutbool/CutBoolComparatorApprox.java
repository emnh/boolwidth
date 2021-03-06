package boolwidth.cutbool;

import boolwidth.heuristics.LSDecomposition;
import boolwidth.heuristics.VertexSplit;
import boolwidth.opencl.JOCLOpenCLCutBoolComputer;
import graph.BiGraph;
import interfaces.IDecomposition;

import java.util.Comparator;

import boolwidth.CutBool;
import sadiasrc.decomposition.CCMISApprox;

public class CutBoolComparatorApprox<V, E> extends CutBoolComparator<V, E> implements Comparator<VertexSplit<V>>  {

    private final LSDecomposition<V, E> decomposition;

    private int upper_bound;

    private static int sampleCount = 1000;

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

            final long CAN_AFFORD_EXACT = 100000;

            //long cbApproximation = CBBacktrackEstimateBinary.estimateNeighborhoods(cut, sampleCount);
            long cbApproximation = JOCLOpenCLCutBoolComputer.estimateNeighbourHoods(cut, sampleCount);
            //long cbApproximation2 = CCMISApprox.BoolDimBranch(CutBoolComparatorCCMIS.convertSadiaBiGraph(cut), sampleCount);

            /*long cbExactDebug = CutBool.countNeighborhoods(cut, CAN_AFFORD_EXACT);
            if (cbExactDebug > 10000) {
                System.out.printf("approx/approx2/exact: %d%% / %d%% : %d\n",
                        cbApproximation * 100 / cbExactDebug,
                        cbApproximation2 * 100 / cbExactDebug,
                        cbExactDebug);
            }*/

            /*if (cbApproximation < CAN_AFFORD_EXACT) {
                long cbExact = CutBool.countNeighborhoods(cut, CAN_AFFORD_EXACT);

                if (cbExact == CutBool.BOUND_EXCEEDED) {
                    node.setCutBoolLowerBound(CAN_AFFORD_EXACT);
                } else {
                    node.setCutBool(cbExact);
                    return cbExact;
                }
            }*/
            node.setCutBool(cbApproximation);
            return cbApproximation;
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

    public CutBoolComparatorApprox(LSDecomposition<V, E> decomposition) {
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
            return O1_LESS_THAN_O2;
        }

        assert cb1 != VertexSplit.CUTBOOL_INITVAL;
        assert cb2 != VertexSplit.CUTBOOL_INITVAL;
        assert cb2 != CutBool.BOUND_EXCEEDED;
        assert cb2 != CutBool.BOUND_EXCEEDED;

        if (cb1 == cb2) {
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


    public void setUpperBound(int upper_bound) {
        this.upper_bound = upper_bound;
    }
}
