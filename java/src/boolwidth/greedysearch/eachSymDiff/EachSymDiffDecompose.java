package boolwidth.greedysearch.eachSymDiff;

import boolwidth.greedysearch.base.*;
import boolwidth.greedysearch.symdiff.SplitSymDiff;
import graph.Vertex;
import interfaces.IGraph;

/**
 * Created by emh on 11/3/2014.
 */
//public class EachSymDiffDecompose extends StackDecomposeTryAllFirstVertexChoice {
public class EachSymDiffDecompose extends StackDecompose {
    public EachSymDiffDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        return new SplitEachSymDiff(depth, decomposition, rights);
    }
}
