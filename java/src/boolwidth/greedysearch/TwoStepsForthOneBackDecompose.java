package boolwidth.greedysearch;

import boolwidth.greedysearch.base.BaseDecompose;
import boolwidth.greedysearch.base.Split;
import boolwidth.greedysearch.ds.ImmutableBinaryTree;
import boolwidth.greedysearch.ds.SimpleNode;
import graph.Vertex;
import interfaces.IGraph;

import java.util.ArrayList;

/**
 * Created by emh on 11/2/2014.
 */

public class TwoStepsForthOneBackDecompose extends BaseDecompose {

    public TwoStepsForthOneBackDecompose(IGraph<Vertex<Integer>, Integer, String> graph) {
        super(graph);
    }

    @Override
    public ImmutableBinaryTree decompose() {
        ArrayList<Vertex<Integer>> lefts = new ArrayList<>();
        ArrayList<Vertex<Integer>> rights = new ArrayList<>();
        int i = 0;
        for (Vertex<Integer> v : getGraph().vertices()) {
            if (i % 2 == 0) {
                lefts.add(v);
            } else {
                rights.add(v);
            }
        }
        Split split = new Split(0, this, lefts, rights);
        ImmutableBinaryTree ibt = new ImmutableBinaryTree();
        ibt = ibt.addRoot();

        SimpleNode last = ibt.getRoot();
        while (!split.done()) {
            System.out.println("move left");
            split = split.decomposeAdvance();
            System.out.println("move right");
            split = split.decomposeAdvanceRight();

            ibt = ibt.addChild(last, split.getLastMoved().id());
            last = ibt.getReference();
        }
        return ibt;
    }
}
