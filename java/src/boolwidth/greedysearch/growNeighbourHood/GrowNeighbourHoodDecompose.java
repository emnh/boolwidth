package boolwidth.greedysearch.growNeighbourHood;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.base.StackDecompose;
import graph.Vertex;
import interfaces.IGraph;

/**
 * Created by emh on 11/3/2014.
 */

class SplitStackItem {
    public Split parent;
    public Split child;
    public boolean isLeft;

    public SplitStackItem(Split parent, Split child, boolean isLeft) {
        this.child = child;
        this.parent = parent;
        this.isLeft = isLeft;
    }
}

public class GrowNeighbourHoodDecompose extends StackDecompose {

    public GrowNeighbourHoodDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public Split createSplit(int depth, BaseDecompose decomposition, Iterable<Vertex<Integer>> rights) {
        return new SplitGrowNeighbourhood(depth, decomposition, rights);
    }
}
