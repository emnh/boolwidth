package boolwidth.greedysearch.best;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.growNeighbourHood.GrowNeighbourHoodDecompose;
import boolwidth.greedysearch.spanning.GreedyMergeDecompose;
import boolwidth.greedysearch.spanning.SpanningTreeDecompose;
import boolwidth.greedysearch.symdiff.SymDiffDecompose;
import graph.Vertex;
import interfaces.IGraph;

/**
 * Created by emh on 11/17/2014.
 */
public class BestDecompose extends BaseDecompose {

    public BestDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        BaseDecompose[] decomposes = new BaseDecompose[] {
            new SpanningTreeDecompose(getGraph()),
            new GrowNeighbourHoodDecompose(getGraph()),
            new SymDiffDecompose(getGraph()),
            new GreedyMergeDecompose(getGraph())
        };
        long UB = Long.MAX_VALUE;
        ImmutableBinaryTree minIBT = null;
        for (BaseDecompose gd : decomposes) {
            System.out.printf("decomposing for: %s\n", gd.getClass().getName());
            ImmutableBinaryTree ibt = gd.decompose();
            ibt.creatorName = gd.getClass().getName();
            long bw = getBooleanWidth(ibt, UB);
            if (bw != UPPER_BOUND_EXCEEDED && bw < UB) {
                UB = bw;
                minIBT = ibt;
            }
        }
        return minIBT;
    }
}
