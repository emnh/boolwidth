package boolwidth.greedysearch;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import graph.Vertex;
import interfaces.IGraph;

/**
 * Created by emh on 11/2/2014.
 */

public class TwoStepsForthOneBackDecompose extends BaseDecompose {

    public TwoStepsForthOneBackDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        Split split = new Split(0, this, getGraph().vertices());
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();

        SimpleNode last = ibt.getRoot();
        while (!split.done()) {
            System.out.println("move left");
            split = split.decomposeAdvance((newLefts, toMove) -> this.getCutBool(newLefts, true));
            System.out.println("move right");
            split = split.decomposeAdvanceRight((newLefts, toMove) -> this.getCutBool(newLefts, true));
            System.out.println("move left");
            split = split.decomposeAdvance((newLefts, toMove) -> this.getCutBool(newLefts, true));

            ibt = ibt.addChild(last, split.getLastMoved().id());
            last = ibt.getReference();
        }
        return ibt;
    }
}
