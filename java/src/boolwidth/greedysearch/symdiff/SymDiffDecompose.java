package boolwidth.greedysearch.symdiff;

import boolwidth.greedysearch.base.*;
import graph.Vertex;
import interfaces.IGraph;

/**
 * Created by emh on 11/3/2014.
 */
public class SymDiffDecompose extends StackDecompose {
//public class SymDiffDecompose extends StackDecomposeTryAllFirstVertexChoice {
//public class SymDiffDecompose extends CaterpillarToFullTryAllFirstVertexChoiceDecompose {

    public SymDiffDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        return new SplitSymDiff(depth, decomposition, lefts, rights);
    }
}
