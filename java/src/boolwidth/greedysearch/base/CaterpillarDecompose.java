package boolwidth.greedysearch.base;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import boolwidth.greedysearch.symdiff.SplitSymDiff;
import graph.Vertex;
import interfaces.IGraph;

/**
 * Created by emh on 11/9/2014.
 */
public class CaterpillarDecompose extends BaseDecompose {

    public CaterpillarDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        SplitSymDiff split = new SplitSymDiff(0, this, getGraph().vertices());
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();

        SimpleNode last = ibt.getRoot();
        while (!split.done()) {
            split = split.decomposeAdvance();
            ibt = ibt.addChild(last, split.getLastMoved().id());
            last = ibt.getReference();
        }
        return ibt;
    }
}
