package boolwidth.greedysearch.growNeighbourHood;

import boolwidth.greedysearch.base.*;
import graph.Vertex;
import interfaces.IGraph;

/**
 * Created by emh on 11/3/2014.
 */

//public class GrowNeighbourHoodDecompose extends StackDecomposeTryAllFirstVertexChoice {
public class GrowNeighbourHoodDecompose extends StackDecompose {
//public class GrowNeighbourHoodDecompose extends CaterpillarDecompose {
//public class GrowNeighbourHoodDecompose extends CaterpillarToFullDecompose {
//public class GrowNeighbourHoodDecompose extends CaterpillarToFullTryAllFirstVertexChoiceDecompose {

    public GrowNeighbourHoodDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> lefts, Iterable<Vertex<Integer>> rights) {
        return new SplitGrowNeighbourhood(depth, decomposition, lefts, rights);
    }
}
