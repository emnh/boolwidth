package boolwidth.greedysearch.symdiff;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.base.StackDecompose;
import boolwidth.greedysearch.growNeighbourHood.SplitGrowNeighbourhood;
import graph.Vertex;
import interfaces.IGraph;

/**
 * Created by emh on 11/3/2014.
 */
public class SymDiffDecompose extends StackDecompose {

    public SymDiffDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        return new SplitSymDiff(depth, decomposition, rights);
    }
}
