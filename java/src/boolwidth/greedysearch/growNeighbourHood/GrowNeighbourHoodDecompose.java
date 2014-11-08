package boolwidth.greedysearch.growNeighbourHood;

import boolwidth.greedysearch.BaseDecompose;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import boolwidth.greedysearch.symdiff.SplitSymDiff;
import graph.Vertex;
import interfaces.IGraph;

/**
 * Created by emh on 11/3/2014.
 */
public class GrowNeighbourHoodDecompose extends BaseDecompose {

    public GrowNeighbourHoodDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        SplitGrowNeighbourhood split = new SplitGrowNeighbourhood(0, this, getGraph().vertices());
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();

        SimpleNode last = ibt.getRoot();
        while (!split.done()) {
            split = split.decomposeAdvance((newLefts, toMove) -> this.getCutBool(newLefts, true));

            ibt = ibt.addChild(last, split.getLastMoved().id());
            last = ibt.getReference();
        }
        return ibt;
    }
}
