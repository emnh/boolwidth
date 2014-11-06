package boolwidth.greedysearch;

import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by emh on 11/2/2014.
 */

public class MemoryDecompose extends BaseDecompose {

    public MemoryDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        SplitMemory split = new SplitMemory(0, this, getGraph().vertices());
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
